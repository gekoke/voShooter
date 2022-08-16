package ee.taltech.voshooter.networking.server.gamestate;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.headless.HeadlessFileHandle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import ee.taltech.voshooter.map.GameMap;
import ee.taltech.voshooter.networking.messages.clientreceived.ChatGamePlayerChange;
import ee.taltech.voshooter.networking.messages.clientreceived.GameEnd;
import ee.taltech.voshooter.networking.messages.serverreceived.PlayerAction;
import ee.taltech.voshooter.networking.messages.serverreceived.PlayerInput;
import ee.taltech.voshooter.networking.server.VoConnection;
import ee.taltech.voshooter.networking.server.VoServer;
import ee.taltech.voshooter.networking.server.gamestate.collision.CollisionHandler;
import ee.taltech.voshooter.networking.server.gamestate.collision.utils.HijackedTmxLoader;
import ee.taltech.voshooter.networking.server.gamestate.collision.utils.LevelGenerator;
import ee.taltech.voshooter.networking.server.gamestate.entitymanager.EntityManagerHub;
import ee.taltech.voshooter.networking.server.gamestate.gamemodes.GameMode;
import ee.taltech.voshooter.networking.server.gamestate.gamemodes.GameModeManagerFactory;
import ee.taltech.voshooter.networking.server.gamestate.player.Bot;
import ee.taltech.voshooter.networking.server.gamestate.player.Player;
import ee.taltech.voshooter.networking.server.gamestate.statistics.StatisticsTracker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Game extends Thread {

    private boolean running = false;
    private VoServer parent;

    public static final double TICK_RATE_IN_HZ = 60.0;
    private static final double TICK_RATE = 1000000000.0 / TICK_RATE_IN_HZ;

    private final Map<VoConnection, Set<PlayerAction>> connectionInputs = new ConcurrentHashMap<>();
    private final Set<Player> bots = ConcurrentHashMap.newKeySet();

    static {
        World.setVelocityThreshold(0.1f);
    }

    private final GameMap.MapType mapType;
    private TiledMap currentMap;
    public final int gameLength;
    private final int gameMode;
    private final World world = new World(new Vector2(0, 0), false);

    private final StatisticsTracker statisticsTracker;
    private final EntityManagerHub entityManagerHub;
    private final CollisionHandler collisionHandler = new CollisionHandler(world, this);
    private final InputHandler inputHandler = new InputHandler();
    private final GameMode gameModeManager;

    /**
     * Construct the game.
     * @param gameMode The game mode for the game.
     * @param mapType The game map used in this game.
     * @param gameLength Length of the round.
     */
    public Game(VoServer parent, int gameMode, GameMap.MapType mapType, int gameLength) {
        this.parent = parent;
        this.mapType = mapType;
        this.gameMode = gameMode;
        if (gameLength >= 15) this.gameLength = gameLength;
        else this.gameLength = Integer.MAX_VALUE;
        setCurrentMap();
        statisticsTracker = GameModeManagerFactory.makeStatisticsTracker(this, gameMode);
        entityManagerHub = new EntityManagerHub(world, this, statisticsTracker);
        gameModeManager = GameModeManagerFactory.makeGameModeManager(this, statisticsTracker, gameMode);
        LevelGenerator.generateLevel(world, currentMap);
        world.setContactListener(collisionHandler);
    }

    /**
     * Add a connection to this game.
     * @param connection The connection to add.
     */
    public void addConnection(VoConnection connection) {
        // Track the connection.
        if (!connectionInputs.containsKey(connection)) connectionInputs.put(connection, ConcurrentHashMap.newKeySet());

        // Create a player object with a physics body which will be tracked in the world.
        entityManagerHub.createPlayer(connection);
        for (VoConnection c : connectionInputs.keySet()) {
            if (!c.equals(connection)) {
                c.sendTCP(new ChatGamePlayerChange(String.format("Player %s has joined the game.",
                        connection.player.getName())));
            }
        }
    }

    public void addBot() {
       entityManagerHub.createBot();
    }

    /**
     * Remove a connection from this game.
     * @param connection the connection to remove.
     */
    public void removeConnection(VoConnection connection) {
        if (connectionInputs.containsKey(connection)) {
            connectionInputs.remove(connection);
            entityManagerHub.removePlayer(connection.user.id);
            for (VoConnection c : connectionInputs.keySet()) {
                if (!c.equals(connection)) {
                    c.sendTCP(new ChatGamePlayerChange(String.format("Player %s has left the game.",
                    connection.player.getName())));
                }
            }
        }
    }

    /** Main game logic. */
    private void tick() {
        connectionInputs.forEach(this::handleInputs);     // Handle inputs.
        entityManagerHub.update();                        // Update logic.
        gameModeManager.update();                         // Update game mode logic.

        world.step((float) (1 / TICK_RATE_IN_HZ), 8, 4);  // Update physics simulation.

        entityManagerHub.sendUpdates();                   // Send updates to players.
        clearPlayerInputs();                              // Clear inputs.

        checkKeepAliveTimerPerConnection();
    }

    private void checkKeepAliveTimerPerConnection() {
        for (VoConnection connection : getConnections()) {
            if (connection.player.getKeepAliveTimer() > 15) {
                parent.handleDisconnects(connection);
            }
        }
    }

    /**
     * React to the players' inputs.
     * @param c The connection which performed the inputs.
     * @param actions The actions they wish to take.
     */
    private void handleInputs(VoConnection c, Set<PlayerAction> actions) {
        try {
            actions.forEach(a -> {
                inputHandler.handleInput(c, a);
            });
        } catch (ConcurrentModificationException ignored) {
        }
    }

    /**
     * Add inputs to be dealt with next tick.
     * @param c The connection the input came from.
     * @param input The actions the player requests.
     */
    public void addPlayerInput(VoConnection c, PlayerInput input) {
        if (connectionInputs.containsKey(c)) {
            try {
                connectionInputs.get(c).addAll(input.inputs);
            } catch (ConcurrentModificationException ignored) {
            }
        }
    }

    /** Reset "last input" for every connection after every tick. */
    private void clearPlayerInputs() {
        connectionInputs.replaceAll((k, v) -> new HashSet<>());
    }

    /**
     * Start the game simulation.
     */
    @Override
    public void run() {
        running = true;
        double delta = 0;

        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / TICK_RATE;
            lastTime = now;

            while (delta >= 1) {
                tick();
                delta--;
            }
        }
    }

    /** Close the game simulation. */
    public void shutDown() {
        running = false;
        List<Player> players = getPlayers();
        int playersAmount = (int) players.stream().filter(player -> !(player instanceof Bot)).count();
        int botAmount = (int) players.stream().filter(player -> player instanceof Bot).count();
        List<String> leaderBoard = statisticsTracker.generateEndLeaderBoard();
        GameEnd gameEnd = new GameEnd(gameMode, gameLength, playersAmount, botAmount, mapType, leaderBoard);
        for (VoConnection c : getConnections()) {
            c.sendTCP(gameEnd);
        }
        writeToFile(gameEnd);
    }

    public void writeToFile() {
        List<Player> players = getPlayers();
        int playersAmount = (int) players.stream().filter(player -> !(player instanceof Bot)).count();
        int botAmount = (int) players.stream().filter(player -> player instanceof Bot).count();
        List<String> leaderBoard = statisticsTracker.generateEndLeaderBoard();
        writeToFile(new GameEnd(gameMode, Math.round(gameModeManager.getTimePassed() * 10f) / 10, playersAmount, botAmount, mapType, leaderBoard));
    }

    private void writeToFile(GameEnd msg) {
        String dir = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "VoShooter" + File.separator + "ServerGames";
        final File file = new File(dir, (LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy__HH-mm-ss")) + ".txt"));
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
            FileWriter fWriter = new FileWriter(file);
            fWriter.write(String.format("Time played: %s%nGamemode: %s%nMap: %s%nPlayer count: %s%nBot count: %s%n%n",
                    Math.round(gameModeManager.getTimePassed() * 10f) / 10f, msg.gameMode, msg.mapType,
                    msg.playerCount, msg.botAmount));
            for (String line : msg.leaderBoard) {
                fWriter.write(line);
                fWriter.write("\n");
            }
            fWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return A list of player objects in this game.
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(entityManagerHub.getAllPlayers());
    }

    public Set<VoConnection> getConnections() {
        return connectionInputs.keySet();
    }

    public GameMap.MapType getMapType() {
        return mapType;
    }

    public EntityManagerHub getEntityManagerHub() {
        return entityManagerHub;
    }

    public StatisticsTracker getStatisticsTracker() {
        return statisticsTracker;
    }

    private void setCurrentMap() {
        currentMap = new HijackedTmxLoader(fileName -> new HeadlessFileHandle(fileName, Files.FileType.Classpath))
                .load(GameMap.getTileSet(mapType));
    }

    public TiledMap getCurrentMap() {
        return currentMap;
    }

    public World getWorld() {
        return world;
    }

    public GameMode getGameModeManager() {
        return gameModeManager;
    }

    public static float timeElapsed() {
        return (float) (1 / Game.TICK_RATE_IN_HZ);
    }
}

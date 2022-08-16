package ee.taltech.voshooter;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.esotericsoftware.kryonet.Client;
import ee.taltech.voshooter.gamestate.GameState;
import ee.taltech.voshooter.networking.VoClient;
import ee.taltech.voshooter.screens.ChangeControlsScreen;
import ee.taltech.voshooter.screens.CreateGameScreen;
import ee.taltech.voshooter.screens.EndScreen;
import ee.taltech.voshooter.screens.JoinGameScreen;
import ee.taltech.voshooter.screens.LoadingScreen;
import ee.taltech.voshooter.screens.LobbyScreen;
import ee.taltech.voshooter.screens.LobbySettingsScreen;
import ee.taltech.voshooter.screens.MainScreen;
import ee.taltech.voshooter.screens.MenuScreen;
import ee.taltech.voshooter.screens.PreferencesScreen;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class VoShooter extends Game {

    private LoadingScreen loadingScreen;
    private PreferencesScreen preferencesScreen;
    private MenuScreen menuScreen;
    public MainScreen mainScreen;
    private AppPreferences preferences;
    private ChangeControlsScreen changeControlsScreen;
    public CreateGameScreen createGameScreen;
    public JoinGameScreen joinGameScreen;
    private LobbyScreen lobbyScreen;
    public LobbySettingsScreen lobbySettingsScreen;
    public EndScreen endScreen;
    private BadConnectionReason disconnectReason;
    private String lobbyCode;
    private boolean cameFromGame;
    public VoClient client;
    public GameState gameState;
    public Screen screen;

    public String HOST_ADDRESS = "193.40.255.29";
    public int PORT_NUMBER = 5001;

    public enum Screen {
        LOADING,
        MENU,
        PREFERENCES,
        MAIN,
        CREATE_GAME,
        JOIN_GAME,
        LOBBY,
        CHANGE_CONTROLS,
        LOBBY_SETTINGS,
        END_SCREEN
    }

    public enum BadConnectionReason {
        NO_SUCH_LOBBY,
        NOT_A_UNIQUE_NAME,
        LOBBY_FULL,
        LOBBY_JOINED
    }

    public VoShooter(String[] args) {
       if (args.length > 0) HOST_ADDRESS = args[0];
       if (args.length > 1) PORT_NUMBER = Integer.parseInt(args[1]);
    }

    /**
     * Initialize the orchestrator object.
     */
    @Override
    public void create() {
        preferences = new AppPreferences();
        gameState = new GameState();
        changeScreen(VoShooter.Screen.LOADING);
    }

    /**
     * Change the current screen.
     * @param screen An enumerable of type Screen.
     */
    public void changeScreen(Screen screen) {
        if (screen != Screen.MAIN) {
            this.screen = null;
            gameState.ongoingGame = false;
        }
        switch (screen) {
            case MENU:
                if (menuScreen == null) menuScreen = new MenuScreen(this);
                setScreen(menuScreen);
                break;
            case PREFERENCES:
                if (preferencesScreen == null) preferencesScreen = new PreferencesScreen(this);
                setScreen(preferencesScreen);
                break;
            case MAIN:
                if (mainScreen == null) mainScreen = new MainScreen(this);
                if (this.screen != screen) {
                    setScreen(mainScreen);
                    this.screen = screen;
                }
                break;
            case LOADING:
                if (loadingScreen == null) loadingScreen = new LoadingScreen(this);
                setScreen(loadingScreen);
                break;
            case CREATE_GAME:
                if (createGameScreen == null) createGameScreen = new CreateGameScreen(this);
                setScreen(createGameScreen);
                break;
            case JOIN_GAME:
                if (joinGameScreen == null) joinGameScreen = new JoinGameScreen(this);
                setScreen(joinGameScreen);
                break;
            case LOBBY:
                if (lobbySettingsScreen == null) lobbySettingsScreen = new LobbySettingsScreen(this);
                if (lobbyScreen == null) lobbyScreen = new LobbyScreen(this);
                setScreen(lobbyScreen);
                break;
            case CHANGE_CONTROLS:
                if (changeControlsScreen == null) changeControlsScreen = new ChangeControlsScreen(this);
                setScreen(changeControlsScreen);
                break;
            case LOBBY_SETTINGS:
                if (lobbySettingsScreen == null) lobbySettingsScreen = new LobbySettingsScreen(this);
                setScreen(lobbySettingsScreen);
                break;
            case END_SCREEN:
                if (endScreen == null) endScreen = new EndScreen(this);
                setScreen(endScreen);
                break;
            default:
                // Noop.
        }
    }

    /**
    * @return The object defining the user's app preferences.
    */
    public AppPreferences getPreferences() {
        return preferences;
    }

    /**
     * @return The client where you can send outbound requests.
     */
    public Client getClient() {
        return client.client;
    }

    /**
     * Get the code correct boolean.
     * @return boolean
     */
    public BadConnectionReason getDisconnectReason() {
        return disconnectReason;
    }

    /**
     * Set a boolean if the code was incorrect.
     * @param disconnectReason boolean
     */
    public void setDisconnectReason(BadConnectionReason disconnectReason) {
        this.disconnectReason = disconnectReason;
    }

    /**
     * @return code of the attempted lobby.
     */
    public String getLobbyCode() {
        return lobbyCode;
    }

    /**
     * @param lobbyCode Code of the lobby.
     */
    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    /**
     * @return true if previous screen was main screen.
     */
    public boolean isCameFromGame() {
        return cameFromGame;
    }

    /**
     * @param cameFromGame set true if came from main screen.
     */
    public void setCameFromGame(boolean cameFromGame) {
        this.cameFromGame = cameFromGame;
    }

    /**
     * Used to instantiate a new VoClient object for communication
     * with the server.
     */
    public void createNetworkClient() throws IOException {
        client = new VoClient(this, HOST_ADDRESS, PORT_NUMBER);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ignored) {
        }
    }

    public boolean doesNotContainChangeListener(DelayedRemovalArray<EventListener> listeners) {
        for (EventListener listener : listeners) {
            if (listener instanceof ChangeListener) {
                return false;
            }
        }
        return true;
    }
}

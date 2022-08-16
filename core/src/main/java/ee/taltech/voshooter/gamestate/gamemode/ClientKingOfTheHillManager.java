package ee.taltech.voshooter.gamestate.gamemode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ee.taltech.voshooter.entity.player.ClientPlayer;
import ee.taltech.voshooter.screens.MainScreen;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ee.taltech.voshooter.screens.MainScreen.STATS_ROW_PAD;

public class ClientKingOfTheHillManager extends ClientGameModeManager {

    public Map<Long, Double> players = new HashMap<>();

    public ClientKingOfTheHillManager(MainScreen screen, SpriteBatch batch) {
        super(screen, batch);
    }

    @Override
    public void update() {
        if (mainScreen.statsTabOpen) drawStatistics();
    }

    public void setTimePassed(double timePassed) {
        this.timePassed = timePassed;
    }

    @Override
    public void drawStatistics() {
        hudBatch.begin();
        int tableTop = Gdx.graphics.getHeight() - 30;
        int tableLeft = Gdx.graphics.getWidth() / 2 - 3 * STATS_ROW_PAD;
        int gameLength = mainScreen.parent.gameState.currentLobby.getGameLength();
        if (gameLength >= 15) font.draw(hudBatch, String.format("Hill held until win: %s",
                mainScreen.parent.gameState.currentLobby.getGameLength()), tableLeft, tableTop);
        else font.draw(hudBatch, "Endless game", tableLeft, tableTop);
        tableTop -= 20;
        font.draw(hudBatch, "Player names", tableLeft, tableTop);
        font.draw(hudBatch, "Kills", tableLeft + (STATS_ROW_PAD * 2), tableTop);
        font.draw(hudBatch, "Deaths", tableLeft + (STATS_ROW_PAD * 3), tableTop);
        font.draw(hudBatch, "KDR", tableLeft + (STATS_ROW_PAD * 4), tableTop);
        font.draw(hudBatch, "Seconds held", tableLeft + (STATS_ROW_PAD * 5), tableTop);
        tableTop -= 20;
        for (ClientPlayer player : mainScreen.parent.gameState.getPlayers().values().stream()
                .sorted(Comparator.comparing(player -> players.getOrDefault(((ClientPlayer) player).getId(), 0.0))
                        .reversed()).collect(Collectors.toList())) {
            if (!players.containsKey(player.getId())) break;
            if (player.isBot()) font.setColor(Color.CYAN);
            else font.setColor(Color.WHITE);
            font.draw(hudBatch, player.getName(), tableLeft, tableTop);
            font.setColor(Color.WHITE);
            font.draw(hudBatch, String.valueOf(player.getKills()), tableLeft + (STATS_ROW_PAD * 2), tableTop);
            font.draw(hudBatch, String.valueOf(player.getDeaths()), tableLeft + (STATS_ROW_PAD * 3), tableTop);
            if (player.getDeaths() > 0) {
                font.draw(hudBatch,
                        String.valueOf((double) Math.round((player.getKills()
                                / (double) player.getDeaths()) * 100) / 100),
                        tableLeft + (STATS_ROW_PAD * 4), tableTop);
            } else {
                font.draw(hudBatch, String.valueOf(player.getKills()), tableLeft + (STATS_ROW_PAD * 4), tableTop);
            }
            font.draw(hudBatch, String.valueOf(Math.round(players.getOrDefault(player.getId(), 0.0) * 10.0) / 10.0),
                    tableLeft + (STATS_ROW_PAD * 5), tableTop);
            tableTop -= 20;
        }
        font.getData().setScale(1f);
        font.draw(hudBatch, mainScreen.parent.gameState.currentLobby.getLobbyCode(),
                Gdx.graphics.getWidth() / (float) 2 - 80, 50);
        font.getData().setScale(0.6f);
        hudBatch.end();
    }
}

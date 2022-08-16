package ee.taltech.voshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ee.taltech.voshooter.VoShooter;
import ee.taltech.voshooter.networking.messages.serverreceived.CreateLobby;
import ee.taltech.voshooter.networking.messages.serverreceived.SetUsername;

import java.io.IOException;

import static ee.taltech.voshooter.VoShooter.Screen.MENU;

public class CreateGameScreen implements Screen {

    private VoShooter parent;
    private Stage stage;
    public VoShooter.Screen shouldChangeScreen;
    private Table popUpTable;

    /**
     * Construct the menu screen.
     * @param parent A reference to the orchestrator object.
     */
    public CreateGameScreen(VoShooter parent) {
        this.parent = parent;

        // Create stage which will contain this screen's objects
        stage = new Stage(new ScreenViewport());
    }

    /**
     * Define the assets to be shown in the render loop.
     */
    @Override
    public void show() {
        // Have it handle player's input.
        Gdx.input.setInputProcessor(stage);
        stage.clear();

        // A Skin object defines the theme for menu objects.
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        // Add a table which will contain game creation settings.
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Table for no connection pop-up.
        popUpTable = new Table();
        stage.addActor(popUpTable);
        popUpTable.setVisible(false);
        popUpTable.setPosition((float) Gdx.graphics.getWidth() / 2, (float) Gdx.graphics.getHeight() / 2);
        Label noConnection = new Label("Connection failed, try again.", skin);
        TextButton closePopUp = new TextButton("Okay", skin);
        popUpTable.add(noConnection);
        popUpTable.row().pad(10, 0, 0, 0);
        popUpTable.add(closePopUp);

        // Create the menu objects for our stage.
        Label createLobbyTitle = new Label("Create lobby", skin);
        Label chooseNameLabel = new Label("Choose name:", skin);
        Label playerNameHintLabel = new Label("Too short", skin);
        playerNameHintLabel.setColor(Color.RED);
        TextField playerNameField = new TextField("", skin);
        playerNameField.setMaxLength(12);
        stage.setKeyboardFocus(playerNameField);
        TextButton back = new TextButton("Back", skin);
        TextButton createGame = new TextButton("Create", skin);

        // Add the objects to the table.
        table.add(createLobbyTitle).left();
        table.row().pad(60, 0, 0, 0);
        table.add(chooseNameLabel).left();
        table.add(playerNameField).right().width(210);
        table.add(playerNameHintLabel).padLeft(20);
        table.row().pad(100, 0, 0, 0);
        table.add(back).left();
        table.add(createGame);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    if (popUpTable.isVisible()) {
                        closePopUp.toggle();
                    } else if (stage.getKeyboardFocus() == playerNameField) {
                        createGame.toggle();
                    }
                }
                if (keycode == Input.Keys.ESCAPE && !popUpTable.isVisible()) {
                    back.toggle();
                }
                return true;
            }
        });

        if (parent.doesNotContainChangeListener(back.getListeners())) {
            back.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    parent.changeScreen(MENU);
                }
            });
        }

        if (parent.doesNotContainChangeListener(closePopUp.getListeners())) {
            closePopUp.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    popUpTable.setVisible(false);
                    table.setVisible(true);
                }
            });
        }

        if (parent.doesNotContainChangeListener(playerNameField.getListeners())) {
            playerNameField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (playerNameField.getText().length() < 4) {
                        playerNameHintLabel.setText("Too short");
                        playerNameHintLabel.setColor(Color.RED);
                    } else if (playerNameField.getText().equals("")
                            || playerNameField.getText().replace(" ", "").equals("")) {
                        playerNameHintLabel.setText("Empty    ");
                        playerNameHintLabel.setColor(Color.RED);
                    } else {
                        playerNameHintLabel.setText("Good name");
                        playerNameHintLabel.setColor(Color.GREEN);
                    }
                }
            });
        }

        if (parent.doesNotContainChangeListener(createGame.getListeners())) {
            createGame.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (!playerNameField.getText().equals("")
                            && !playerNameField.getText().replace(" ", "").equals("")
                            && playerNameField.getText().length() >= 4) {
                        try {
                            parent.createNetworkClient();
                            parent.gameState.clientUser.setName(playerNameField.getText());
                            parent.gameState.clientUser.setHost(true);
                            parent.getClient().sendTCP(new SetUsername(playerNameField.getText()));
                            parent.getClient().sendTCP(new CreateLobby());
                        } catch (IOException e) {
                            popUpTable.setVisible(true);
                            table.setVisible(false);
                        }
                    }
                }
            });
        }
    }

    /**
     * Render the elements defined in the show() method.
     */
    @Override
    public void render(float delta) {
        // Refresh the graphics renderer every cycle.
        if (shouldChangeScreen != null) {
            parent.changeScreen(shouldChangeScreen);
            shouldChangeScreen = null;
        }
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // And draw over it again.
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));  // Cap menu FPS to 30.
        stage.draw();
    }

    /**
     * Make sure the window doesn't break.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    /**
     * Dispose of the stage.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }
}

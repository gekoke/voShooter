package ee.taltech.voshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ee.taltech.voshooter.AppPreferences;
import ee.taltech.voshooter.VoShooter;
import ee.taltech.voshooter.soundeffects.MusicPlayer;

import static ee.taltech.voshooter.VoShooter.Screen.CREATE_GAME;
import static ee.taltech.voshooter.VoShooter.Screen.JOIN_GAME;

public class MenuScreen implements Screen {

    private VoShooter parent;
    private Stage stage;

    /**
     * Construct the menu screen.
     * @param parent A reference to the orchestrator object.
     */
    public MenuScreen(VoShooter parent) {
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

        // Add a table which will contain menu items to the stage.
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // A Skin object defines the theme for menu objects.
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        // Create the menu objects for our stage.
        TextButton joinGame = new TextButton("Join Game", skin);
        TextButton createGame = new TextButton("Create Game", skin);
        TextButton preferences = new TextButton("Settings", skin);
        TextButton exit = new TextButton("Exit", skin);
        TextField address = new TextField(parent.HOST_ADDRESS, skin);
        address.setAlignment(Align.center);
        TextField port = new TextField(String.valueOf(parent.PORT_NUMBER), skin);
        port.setAlignment(Align.center);
        address.setTextFieldListener((textField, c) -> parent.HOST_ADDRESS = textField.getText());
        port.setTextFieldListener((textField, c) -> parent.PORT_NUMBER = Integer.parseInt(textField.getText()));
        port.setTextFieldFilter((textField, c) -> Character.isDigit(c));

        // Add the buttons to the table.
        table.add(joinGame).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(createGame).fillX().uniformX();
        table.row().pad(20, 0, 10, 0);
        table.add(preferences).fillX().uniformX();
        table.row().pad(40, 0, 40, 0);
        table.add(exit).fillX().uniformX();
        table.row().pad(60, 0, 10, 0).minWidth(400);
        table.add(address);
        table.row().pad(10, 0, 10, 0).minWidth(400);
        table.add(port);

        // Music.
        MusicPlayer.setMusic("soundfx/bensound-evolution.mp3");
        MusicPlayer.setVolume(AppPreferences.getMusicVolume());

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.NUM_1) joinGame.toggle();
                else if (keycode == Input.Keys.NUM_2) createGame.toggle();
                else if (keycode == Input.Keys.NUM_3) preferences.toggle();
                return true;
            }
        });

        if (parent.doesNotContainChangeListener(joinGame.getListeners())) {
            joinGame.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    parent.changeScreen(JOIN_GAME);
                }
            });
        }

        if (parent.doesNotContainChangeListener(createGame.getListeners())) {
            createGame.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    parent.changeScreen(CREATE_GAME);
                }
            });
        }

        if (parent.doesNotContainChangeListener(preferences.getListeners())) {
            preferences.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    parent.changeScreen(VoShooter.Screen.PREFERENCES);
                }
            });
        }

        if (parent.doesNotContainChangeListener(exit.getListeners())) {
            exit.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    Gdx.app.exit();
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
     * Free memory of assets when done.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }
}

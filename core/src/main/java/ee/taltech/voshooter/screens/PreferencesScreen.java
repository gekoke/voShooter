package ee.taltech.voshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ee.taltech.voshooter.AppPreferences;
import ee.taltech.voshooter.VoShooter;
import ee.taltech.voshooter.soundeffects.MusicPlayer;

public class PreferencesScreen implements Screen {

    private VoShooter parent;
    private Stage stage;
    private Label volumeMusicIndicator;
    private Label volumeSoundIndicator;
    private Slider volumeMusicSlider;
    private Slider volumeSoundSlider;
    private CheckBox particleToggleCheckbox;
    private CheckBox minimapCheckbox;
    private TextButton goToChangeControls;
    private TextButton returnToPreviousScreen;

    /**
     * Construct the preferences screen. Pass in a reference to the orchestrator.
     * @param parent A reference to the orchestrator to enable communication.
     */
    public PreferencesScreen(VoShooter parent) {
        this.parent = parent;

        // Create stage which will contain this screen's objects
        stage = new Stage(new ScreenViewport());
    }

    /**
     * Initialize UI elements to be drawn.
     */
    @Override
    public void show() {
        // Have it handle player's input.
        Gdx.input.setInputProcessor(stage);
        // Add a table which will contain settings items to the stage.
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // A Skin object defines the theme for settings objects.
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        // Create the settings objects for our stage.
        volumeMusicSlider = new Slider(0.0f, 1.0f, 0.05f, false, skin);
        volumeSoundSlider = new Slider(0.0f, 1.0f, 0.05f, false, skin);
        if (parent.isCameFromGame()) {
            returnToPreviousScreen = new TextButton("Back to game", skin);
        } else {
            returnToPreviousScreen = new TextButton("Main menu", skin);
        }
        goToChangeControls = new TextButton("Change controls", skin);

        final Label titleLabel = new Label("Settings", skin);
        final Label volumeMusicLabel = new Label("Music volume", skin);
        final Label volumeSoundLabel = new Label("Sound volume", skin);
        final Label particleToggleLabel = new Label("Toggle particles", skin);

        volumeMusicIndicator = new Label(String.valueOf(Math.round(AppPreferences.getMusicVolume() * 100)), skin);
        volumeSoundIndicator = new Label(String.valueOf(Math.round(AppPreferences.getSoundVolume() * 100)), skin);
        particleToggleCheckbox = new CheckBox("", skin);
        particleToggleCheckbox.setChecked(AppPreferences.getParticlesOn());
        final Label minimapToggleLabel = new Label("Minimap", skin);
        minimapCheckbox = new CheckBox("", skin);
        minimapCheckbox.setChecked(AppPreferences.getMinimapOn());

        // Add the sliders and labels to the table.
        table.add(titleLabel).fillX().uniformX().pad(0, 0, 20, 0).bottom().right();

        table.row().pad(0, 0, 5, 30);
        table.add(volumeMusicLabel).fillX().uniformX();
        table.add(volumeMusicSlider).fillX().uniformX();
        table.add(volumeMusicIndicator).fillX().uniformX();

        table.row().pad(0, 0, 5, 30);
        table.add(volumeSoundLabel).fillX().uniformX();
        table.add(volumeSoundSlider).fillX().uniformX();
        table.add(volumeSoundIndicator).fillX().uniformX();

        table.row().pad(0, 0, 5, 30);
        table.add(particleToggleLabel).fillX().uniformX();
        table.add(particleToggleCheckbox);
        table.row().pad(0, 0, 100, 30);
        table.add(minimapToggleLabel).fillX().uniformX();
        table.add(minimapCheckbox);

        table.row().pad(0, 0, 0, 30);
        table.add(returnToPreviousScreen).fillX().uniformX().bottom().right();
        table.add(goToChangeControls).fillX().uniformX().bottom().right();

        table.pack();

        // Slider and button functionality.
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    returnToPreviousScreen.toggle();
                } else if (keycode == Input.Keys.ENTER) {
                    goToChangeControls.toggle();
                }
                return true;
            }
        });

        volumeMusicSlider.setValue(AppPreferences.getMusicVolume());
        if (parent.doesNotContainChangeListener(volumeMusicSlider.getListeners())) {
            volumeMusicSlider.addListener(event -> {
                AppPreferences.setMusicVolume(volumeMusicSlider.getValue());
                volumeMusicIndicator.setText(String.valueOf(Math.round(AppPreferences.getMusicVolume() * 100)));
                return false;
            });
        }

        volumeSoundSlider.setValue(AppPreferences.getSoundVolume());
        if (parent.doesNotContainChangeListener(volumeSoundSlider.getListeners())) {
            volumeSoundSlider.addListener(event -> {
                AppPreferences.setSoundVolume(volumeSoundSlider.getValue());
                volumeSoundIndicator.setText(String.valueOf(Math.round(AppPreferences.getSoundVolume() * 100)));
                return false;
            });
        }

        if (parent.doesNotContainChangeListener(particleToggleCheckbox.getListeners())) {
            particleToggleCheckbox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    AppPreferences.setParticlesOn(particleToggleCheckbox.isChecked());
                    particleToggleCheckbox.setChecked(particleToggleCheckbox.isChecked());
                }
            });
        }

        if (parent.doesNotContainChangeListener(minimapCheckbox.getListeners())) {
            minimapCheckbox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    AppPreferences.setMinimapOn(minimapCheckbox.isChecked());
                    minimapCheckbox.setChecked(minimapCheckbox.isChecked());
                }
            });
        }

        if (parent.doesNotContainChangeListener(returnToPreviousScreen.getListeners())) {
            returnToPreviousScreen.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    stage.clear();
                    VoShooter.Screen screenToGoTo = VoShooter.Screen.MENU;
                    if (parent.isCameFromGame()) {
                        screenToGoTo = VoShooter.Screen.MAIN;
                    }
                    parent.changeScreen(screenToGoTo);
                }
            });
        }

        if (parent.doesNotContainChangeListener(goToChangeControls.getListeners())) {
            goToChangeControls.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    stage.clear();
                    parent.changeScreen(VoShooter.Screen.CHANGE_CONTROLS);
                }
            });
        }
    }

    /**
     * Render the elements specified in the show() method every frame.
     */
    @Override
    public void render(float delta) {
        // Refresh the graphics renderer every cycle.
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // And draw over it again.
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));  // Cap menu FPS to 30.
        MusicPlayer.setVolume(AppPreferences.getMusicVolume());
        stage.draw();
    }

    /**
     * Update to check if the window needs to be resized.
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

    @Override
    public void dispose() {
    }

}

package ee.taltech.voshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ee.taltech.voshooter.AppPreferences;
import ee.taltech.voshooter.VoShooter;
import ee.taltech.voshooter.settingsinputs.SettingsInput;
import ee.taltech.voshooter.settingsinputs.SettingsInputsHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChangeControlsScreen implements Screen {

    private VoShooter parent;
    private Stage stage;
    private InputMultiplexer inputMultiplexer;
    private Map<Label, TextButton> controls;
    private Label keyUp;
    private Label keyDown;
    private Label keyLeft;
    private Label keyRight;
    private Label buttonLeft;
    private Label pistol;
    private Label shotgun;
    private Label rocketLauncher;
    private Label flameThrower;
    private Label machineGun;
    private Label grenadeLauncher;
    private Label railGun;
    private TextButton returnToPreferencesScreen;
    private Map.Entry<Label, TextButton> changeControlEntry;

    /**
     * Construct the preferences screen. Pass in a reference to the orchestrator.
     * @param parent A reference to the orchestrator to enable communication.
     */
    public ChangeControlsScreen(VoShooter parent) {
        this.parent = parent;

        // Create stage which will contain this screen's objects
        inputMultiplexer = new InputMultiplexer();
        stage = new Stage(new ScreenViewport());
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(new SettingsInputsHandler());
    }

    /**
     * Initialize UI elements to be drawn.
     */
    @Override
    public void show() {
        // Have it handle player's input.
        Gdx.input.setInputProcessor(inputMultiplexer);
        // Add a table which will contain settings items to the stage.
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // A Skin object defines the theme for settings objects.
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        // Create the settings objects for our stage.
        returnToPreferencesScreen = new TextButton("Back", skin);
        final Label titleLabel = new Label("Change controls", skin);

        // Control labels.
        controls = setButtonsCorrect();

        // Add the sliders and labels to the table.
        table.add(titleLabel).fillX().uniformX().bottom().right();
        table.row().pad(60, 0, 20, 30);
        table.add(new Label("Move and shoot", skin));
        table.row().pad(0, 0, 5, 30);

        for (Map.Entry<Label, TextButton> entry : controls.entrySet()) {
            if (entry.getKey().equals(pistol)) {
                table.row().pad(60, 0, 20, 30);
                table.add(new Label("Weapon switches", skin));
            }
            table.row().pad(0, 0, 5, 30);
            table.add(entry.getKey());
            table.add(entry.getValue());
            if (parent.doesNotContainChangeListener(entry.getValue().getListeners())) {
                entry.getValue().addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (entry.getValue().getColor().equals(Color.RED)) {
                            entry.getValue().setColor(Color.WHITE);
                            changeControlEntry = null;
                        } else {
                            setButtonsWhite();
                            entry.getValue().setColor(Color.RED);
                            changeControlEntry = entry;
                        }
                    }
                });
            }
        }
        table.row().pad(20, 0, 100, 30);
        table.add(returnToPreferencesScreen).fillX().uniformX().bottom().right();

        table.pack();

        if (parent.doesNotContainChangeListener(returnToPreferencesScreen.getListeners())) {
            returnToPreferencesScreen.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    stage.clear();
                    changeControlEntry = null;
                    parent.changeScreen(VoShooter.Screen.PREFERENCES);
                }
            });
        }
    }

    /**
     * Set the buttons to be buttons if buttons or keys if keys.
     * @return hashmap with correct buttons.
     */
    private HashMap<Label, TextButton> setButtonsCorrect() {
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        return new LinkedHashMap<Label, TextButton>() {{
            keyUp = new Label("Move up", skin);
            if (AppPreferences.getUpKeyIsKey()) {
                put(keyUp, new TextButton(Input.Keys.toString(AppPreferences.getUpKey()), skin));
            } else {
                put(keyUp, new TextButton(AppPreferences.repr(AppPreferences.getUpKey()), skin));
            }
            keyLeft = new Label("Move left", skin);
            if (AppPreferences.getLeftKeyIsKey()) {
                put(keyLeft, new TextButton(Input.Keys.toString(AppPreferences.getLeftKey()), skin));
            } else {
                put(keyLeft, new TextButton(AppPreferences.repr(AppPreferences.getLeftKey()), skin));
            }
            keyDown = new Label("Move down", skin);
            if (AppPreferences.getDownKeyIsKey()) {
                put(keyDown, new TextButton(Input.Keys.toString(AppPreferences.getDownKey()), skin));
            } else {
                put(keyDown, new TextButton(AppPreferences.repr(AppPreferences.getDownKey()), skin));
            }
            keyRight = new Label("Move right", skin);
            if (AppPreferences.getRightKeyIsKey()) {
                put(keyRight, new TextButton(Input.Keys.toString(AppPreferences.getRightKey()), skin));
            } else {
                put(keyRight, new TextButton(AppPreferences.repr(AppPreferences.getRightKey()), skin));
            }
            buttonLeft = new Label("Shoot", skin);
            if (AppPreferences.getButtonLeftIsKey()) {
                put(buttonLeft, new TextButton(Input.Keys.toString(AppPreferences.getMouseLeft()), skin));
            } else {
                put(buttonLeft, new TextButton(AppPreferences.repr(AppPreferences.getMouseLeft()), skin));
            }
            pistol = new Label("Pistol", skin);
            if (AppPreferences.getNumberOneIsKey()) {
                put(pistol, new TextButton(Input.Keys.toString(AppPreferences.getNumberOne()), skin));
            } else {
                put(pistol, new TextButton(AppPreferences.repr(AppPreferences.getNumberOne()), skin));
            }
            shotgun = new Label("Shotgun", skin);
            if (AppPreferences.getNumberTwoIsKey()) {
                put(shotgun, new TextButton(Input.Keys.toString(AppPreferences.getNumberTwo()), skin));
            } else {
                put(shotgun, new TextButton(AppPreferences.repr(AppPreferences.getNumberTwo()), skin));
            }
            rocketLauncher = new Label("Rocket Launcher", skin);
            if (AppPreferences.getNumberThreeIsKey()) {
                put(rocketLauncher, new TextButton(Input.Keys.toString(AppPreferences.getNumberThree()), skin));
            } else {
                put(rocketLauncher, new TextButton(AppPreferences.repr(AppPreferences.getNumberThree()), skin));
            }
            flameThrower = new Label("Flamethrower", skin);
            if (AppPreferences.getNumberFourIsKey()) {
                put(flameThrower, new TextButton(Input.Keys.toString(AppPreferences.getNumberFour()), skin));
            } else {
                put(flameThrower, new TextButton(AppPreferences.repr(AppPreferences.getNumberFour()), skin));
            }
            machineGun = new Label("Machine Gun", skin);
            if (AppPreferences.getNumberFiveIsKey()) {
                put(machineGun, new TextButton(Input.Keys.toString(AppPreferences.getNumberFive()), skin));
            } else {
                put(machineGun, new TextButton(AppPreferences.repr(AppPreferences.getNumberFive()), skin));
            }
            grenadeLauncher = new Label("Grenade Launcher", skin);
            if (AppPreferences.getNumberSixIsKey()) {
                put(grenadeLauncher, new TextButton(Input.Keys.toString(AppPreferences.getNumberSix()), skin));
            } else {
                put(grenadeLauncher, new TextButton(AppPreferences.repr(AppPreferences.getNumberSix()), skin));
            }
            railGun = new Label("Railgun", skin);
            if (AppPreferences.getNumberSevenIsKey()) {
                put(railGun, new TextButton(Input.Keys.toString(AppPreferences.getNumberSeven()), skin));
            } else {
                put(railGun, new TextButton(AppPreferences.repr(AppPreferences.getNumberSeven()), skin));
            }
        }};
    }

    /**
     * Set all the buttons back to white.
     */
    public void setButtonsWhite() {
        for (Map.Entry<Label, TextButton> entrySet : controls.entrySet()) {
            entrySet.getValue().setColor(Color.WHITE);
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
        Integer inputKey = SettingsInput.getInputKey();
        if (inputKey != null && inputKey == Input.Keys.ESCAPE) {
            returnToPreferencesScreen.toggle();
            SettingsInput.removeInputKey();
        }
        if (changeControlEntry != null) {
            Integer inputButton = SettingsInput.getInputButton();
            if (inputKey != null) {
                changeControlKey(inputKey);
            } else if (inputButton != null) {
                changeControlButton(inputButton);
            }
        }

        // And draw over it again.
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));  // Cap menu FPS to 30.
        stage.draw();
    }

    /**
     * Change control when a key clicked.
     * @param inputKey that was clicked
     */
    private void changeControlKey(Integer inputKey) {
        Label key = changeControlEntry.getKey();
        if (key.equals(keyUp)) {
            AppPreferences.setUpKey(inputKey);
            AppPreferences.setUpKeyIsKey(true);
        } else if (key.equals(keyDown)) {
            AppPreferences.setDownKey(inputKey);
            AppPreferences.setDownKeyIsKey(true);
        } else if (key.equals(keyLeft)) {
            AppPreferences.setLeftKey(inputKey);
            AppPreferences.setLeftKeyIsKey(true);
        } else if (key.equals(keyRight)) {
            AppPreferences.setRightKey(inputKey);
            AppPreferences.setRightKeyIsKey(true);
        } else if (key.equals(buttonLeft)) {
            AppPreferences.setMouseLeft(inputKey);
            AppPreferences.setButtonLeftIsKey(true);
        } else if (key.equals(pistol)) {
            AppPreferences.setNumberOne(inputKey);
            AppPreferences.setNumberOneIsKey(true);
        } else if (key.equals(shotgun)) {
            AppPreferences.setNumberTwo(inputKey);
            AppPreferences.setNumberTwoIsKey(true);
        } else if (key.equals(rocketLauncher)) {
            AppPreferences.setNumberThree(inputKey);
            AppPreferences.setNumberThreeIsKey(true);
        } else if (key.equals(flameThrower)) {
            AppPreferences.setNumberFour(inputKey);
            AppPreferences.setNumberFourIsKey(true);
        } else if (key.equals(machineGun)) {
            AppPreferences.setNumberFive(inputKey);
            AppPreferences.setNumberFiveIsKey(true);
        } else if (key.equals(grenadeLauncher)) {
            AppPreferences.setNumberSix(inputKey);
            AppPreferences.setNumberSixIsKey(true);
        } else if (key.equals(railGun)) {
            AppPreferences.setNumberSeven(inputKey);
            AppPreferences.setNumberSevenIsKey(true);
        }
        changeControlEntry.getValue().setText(Input.Keys.toString(inputKey));
        setButtonsWhite();
        changeControlEntry = null;
    }

    /**
     * Change input when button clicked.
     * @param inputButton that was clicked
     */
    private void changeControlButton(Integer inputButton) {
        Label key = changeControlEntry.getKey();
        if (key.equals(keyUp)) {
            AppPreferences.setUpKey(inputButton);
            AppPreferences.setUpKeyIsKey(false);
        } else if (key.equals(keyDown)) {
            AppPreferences.setDownKey(inputButton);
            AppPreferences.setDownKeyIsKey(false);
        } else if (key.equals(keyLeft)) {
            AppPreferences.setLeftKey(inputButton);
            AppPreferences.setLeftKeyIsKey(false);
        } else if (key.equals(keyRight)) {
            AppPreferences.setRightKey(inputButton);
            AppPreferences.setRightKeyIsKey(false);
        } else if (key.equals(buttonLeft)) {
            AppPreferences.setMouseLeft(inputButton);
            AppPreferences.setButtonLeftIsKey(false);
        } else if (key.equals(pistol)) {
            AppPreferences.setNumberOne(inputButton);
            AppPreferences.setNumberOneIsKey(false);
        } else if (key.equals(shotgun)) {
            AppPreferences.setNumberTwo(inputButton);
            AppPreferences.setNumberTwoIsKey(false);
        } else if (key.equals(rocketLauncher)) {
            AppPreferences.setNumberThree(inputButton);
            AppPreferences.setNumberThreeIsKey(false);
        } else if (key.equals(flameThrower)) {
            AppPreferences.setNumberFour(inputButton);
            AppPreferences.setNumberFourIsKey(false);
        } else if (key.equals(machineGun)) {
            AppPreferences.setNumberFive(inputButton);
            AppPreferences.setNumberFiveIsKey(false);
        } else if (key.equals(grenadeLauncher)) {
            AppPreferences.setNumberSix(inputButton);
            AppPreferences.setNumberSixIsKey(false);
        } else if (key.equals(railGun)) {
            AppPreferences.setNumberSeven(inputButton);
            AppPreferences.setNumberSevenIsKey(false);
        }
        changeControlEntry.getValue().setText(AppPreferences.repr(inputButton));
        setButtonsWhite();
        changeControlEntry = null;
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

package ee.taltech.voshooter.controller;

import com.badlogic.gdx.Gdx;
import ee.taltech.voshooter.AppPreferences;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    /**
     * Get inputs of object and handle them.
     * @return ArrayList of keys and buttons currently pressed
     */
    public static List<ActionType> getInputs() {
        List<ActionType> actionsPerformed = new ArrayList<>();

        if ((Gdx.input.isKeyPressed(AppPreferences.getLeftKey()) && AppPreferences.getLeftKeyIsKey())
                || (Gdx.input.isButtonPressed(AppPreferences.getLeftKey()) && !AppPreferences.getLeftKeyIsKey())) {
            actionsPerformed.add(ActionType.MOVE_LEFT);
        }
        if ((Gdx.input.isKeyPressed(AppPreferences.getRightKey()) && AppPreferences.getRightKeyIsKey())
                || (Gdx.input.isButtonPressed(AppPreferences.getRightKey()) && !AppPreferences.getRightKeyIsKey())) {
            actionsPerformed.add(ActionType.MOVE_RIGHT);
        }
        if ((Gdx.input.isKeyPressed(AppPreferences.getUpKey()) && AppPreferences.getUpKeyIsKey())
                || (Gdx.input.isButtonPressed(AppPreferences.getUpKey()) && !AppPreferences.getUpKeyIsKey())) {
            actionsPerformed.add(ActionType.MOVE_UP);
        }
        if ((Gdx.input.isKeyPressed(AppPreferences.getDownKey()) && AppPreferences.getDownKeyIsKey())
                || (Gdx.input.isButtonPressed(AppPreferences.getDownKey()) && !AppPreferences.getDownKeyIsKey())) {
            actionsPerformed.add(ActionType.MOVE_DOWN);
        }
        if ((Gdx.input.isKeyPressed(AppPreferences.getNumberOne()) && AppPreferences.getNumberOneIsKey())
                || (Gdx.input.isButtonPressed(AppPreferences.getNumberOne()) && !AppPreferences.getNumberOneIsKey())) {
            actionsPerformed.add(ActionType.WEAPON_PISTOL);
        }
        if ((Gdx.input.isKeyPressed(AppPreferences.getNumberTwo()) && AppPreferences.getNumberTwoIsKey())
                || (Gdx.input.isButtonPressed(AppPreferences.getNumberTwo()) && !AppPreferences.getNumberTwoIsKey())) {
            actionsPerformed.add(ActionType.WEAPON_SHOTGUN);
        }
        if ((Gdx.input.isKeyPressed(AppPreferences.getNumberThree()) && AppPreferences.getNumberThreeIsKey())
                || (Gdx.input.isButtonPressed(AppPreferences.getNumberThree()) && !AppPreferences.getNumberThreeIsKey())) {
            actionsPerformed.add(ActionType.WEAPON_RPG);
        }
        if ((Gdx.input.isKeyPressed(AppPreferences.getNumberFour()) && AppPreferences.getNumberFourIsKey())
                || (Gdx.input.isButtonPressed(AppPreferences.getNumberFour()) && !AppPreferences.getNumberFourIsKey())) {
            actionsPerformed.add(ActionType.WEAPON_FLAMETHROWER);
        }
        if ((Gdx.input.isKeyPressed(AppPreferences.getNumberFive()) && AppPreferences.getNumberFiveIsKey())
                || (Gdx.input.isButtonPressed(AppPreferences.getNumberFive()) && !AppPreferences.getNumberFiveIsKey())) {
            actionsPerformed.add(ActionType.WEAPON_MACHINE_GUN);
        }
        if ((Gdx.input.isKeyPressed(AppPreferences.getNumberSix()) && AppPreferences.getNumberSixIsKey())
                || (Gdx.input.isButtonPressed(AppPreferences.getNumberSix()) && !AppPreferences.getNumberSixIsKey())) {
            actionsPerformed.add(ActionType.WEAPON_GRENADE_LAUNCHER);
        }
        if ((Gdx.input.isKeyPressed(AppPreferences.getNumberSeven()) && AppPreferences.getNumberSevenIsKey())
                || (Gdx.input.isButtonPressed(AppPreferences.getNumberSeven()) && !AppPreferences.getNumberSevenIsKey())) {
            actionsPerformed.add(ActionType.WEAPON_RAILGUN);
        }
        if ((Gdx.input.isKeyPressed(AppPreferences.getLShiftKey()) && AppPreferences.getLShiftKeyIsKey())
                || (Gdx.input.isButtonPressed(AppPreferences.getLShiftKey()) && !AppPreferences.getLShiftKeyIsKey())) {
            actionsPerformed.add(ActionType.DASH);
        }
        // Mouse buttons
        if ((Gdx.input.isKeyPressed(AppPreferences.getMouseLeft()) && AppPreferences.getButtonLeftIsKey())
                || (Gdx.input.isButtonPressed(AppPreferences.getMouseLeft()) && !AppPreferences.getButtonLeftIsKey())) {
            actionsPerformed.add(ActionType.MOUSE_LEFT);
        }

        return actionsPerformed;
    }
}

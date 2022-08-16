package ee.taltech.voshooter.networking.server.gamestate.player.botstrategy;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import ee.taltech.voshooter.networking.messages.serverreceived.MouseCoords;
import ee.taltech.voshooter.networking.server.gamestate.Game;
import ee.taltech.voshooter.networking.server.gamestate.collision.utils.RayCaster;
import ee.taltech.voshooter.networking.server.gamestate.collision.utils.RayCollision;
import ee.taltech.voshooter.networking.server.gamestate.entitymanager.PlayerManager;
import ee.taltech.voshooter.networking.server.gamestate.player.Bot;
import ee.taltech.voshooter.networking.server.gamestate.player.Player;
import ee.taltech.voshooter.networking.server.gamestate.player.botstrategy.moving.MovingStrategy;
import ee.taltech.voshooter.networking.server.gamestate.player.botstrategy.shooting.ShootingStrategy;
import ee.taltech.voshooter.networking.server.gamestate.player.botstrategy.weaponswitching.WeaponSwitchingStrategy;

import java.util.HashSet;
import java.util.Set;

public class DefaultBotStrategy implements BotStrategy {

    private static final RayCaster rayCaster = new RayCaster();
    private final float turningSpeed = 720;

    protected Bot bot;
    protected PlayerManager playerManager;
    protected final ShootingStrategy shootingStrategy;
    protected final MovingStrategy movingStrategy;
    protected final WeaponSwitchingStrategy weaponSwitchingStrategy;

    protected Body targetedBody;
    private Player closestEnemy;

    public DefaultBotStrategy(ShootingStrategy shootingStrategy, MovingStrategy movingStrategy, WeaponSwitchingStrategy weaponSwitchingStrategy) {
        this.shootingStrategy = shootingStrategy;
        this.movingStrategy = movingStrategy;
        this.weaponSwitchingStrategy = weaponSwitchingStrategy;
    }

    @Override
    public BotAction getAction() {
        closestEnemy = determineClosestEnemy();
        targetedBody = getHitScannedTarget();
        BotAction action = new BotAction();

        action.setAim(determineAimDirection(closestEnemy));
        action.setShooting(shootingStrategy.toShoot(targetIsHitScanned()));
        action.setMovementDirections(movingStrategy.getMovementDirections(closestEnemy, targetIsHitScanned()));
        action.setWeaponToSwitchTo(weaponSwitchingStrategy.getWeaponToSwitchTo(closestEnemy));

        return action;
    }

    protected MouseCoords determineAimDirection(Player closestEnemy) {
        if (closestEnemy == null) return new MouseCoords(bot.getViewDirection().x, bot.getViewDirection().y);
        else return lookTowardsTargetMouseCoords(closestEnemy);
    }

    private MouseCoords lookTowardsTargetMouseCoords(Player target) {
        Vector2 directionToTarget = target.getPos().cpy().sub(bot.getPos().cpy());
        Vector2 currentViewDirection = bot.getViewDirection().cpy();
        float angleDiff = directionToTarget.angleDeg() - currentViewDirection.angleDeg();
        float turnBy = turningSpeed * Game.timeElapsed();
        float actualTurn = (Math.abs(angleDiff) < Math.abs(turnBy)) ? angleDiff : turnBy;

        Vector2 newViewDirection = currentViewDirection.cpy().rotateDeg(actualTurn);
        return new MouseCoords(newViewDirection.x, newViewDirection.y);
    }

    private Player determineClosestEnemy() {
        Player closest = null;
        float closestDist = Float.MAX_VALUE;

        for (Player p : getPlayers()) {
            if (p != bot && p.isAlive() && getDistanceTo(p) < closestDist) {
                closest = p;
                closestDist = getDistanceTo(p);
            }
        }

        return closest;
    }


    protected Body getHitScannedTarget() {
        RayCollision collision = rayCaster.getFirstCollision(
                bot.getGame(),
                bot.getPos(),
                bot.getViewDirection(),
                50f,
                new HashSet<>()
        );

        Body b = null;
        if (collision != null) b = collision.getCollidedBody();

        return b;
    }

    public Body getTargetedBody() {
        return targetedBody;
    }

    public Player getClosestEnemy() {
        return closestEnemy;
    }

    boolean targetIsHitScanned() {
        return (targetedBody != null && targetedBody.getUserData() instanceof Player);
    }

    private float getDistanceTo(Player p) {
        return Vector2.dst(bot.getPos().x, bot.getPos().y, p.getPos().x, p.getPos().y);
    }

    private Set<Player> getPlayers() {
        return playerManager.getPlayers();
    }

    public void setBot(Bot bot) {
        this.bot = bot;
        this.playerManager = bot.getPlayerManager();
        shootingStrategy.setBot(bot);
        movingStrategy.setBot(bot);
        weaponSwitchingStrategy.setBot(bot);
    }
}

package ee.taltech.voshooter.weapon.projectileweapon;

import com.badlogic.gdx.math.Vector2;
import ee.taltech.voshooter.networking.server.gamestate.player.Player;
import ee.taltech.voshooter.weapon.projectile.Fireball;
import ee.taltech.voshooter.weapon.projectile.Projectile;

import java.util.Random;

public class Flamethrower extends ProjectileWeapon {

    private static final int STARTING_AMMO = 50;
    private static final int FLAME_COUNT = 5;
    private static final float COOL_DOWN = 0.1f;
    private static final float CONE_ANGLE = 60f;
    private final Random rand = new Random();

    /**
     * @param wielder The player who wields this weapon.
     */
    public Flamethrower(Player wielder) {
        super(wielder, COOL_DOWN, STARTING_AMMO, Type.FLAMETHROWER);
    }

    @Override
    protected void onFire() {
        for (int i = 0; i < FLAME_COUNT; i++) {
            final float start = -(CONE_ANGLE / 2);
            final float end = (CONE_ANGLE / 2);
            final float inc = (end - start) / FLAME_COUNT;

            Vector2 offset = wielder.getViewDirection().cpy().nor().rotateDeg(start + i * inc);

            Projectile p = new Fireball(
                    wielder,
                    wielder.getPos().cpy(),
                    offset.cpy(), type
            );

            wielder.getGame().getEntityManagerHub().add(p);
        }
    }

    @Override
    public int getMaxAmmo() {
        return STARTING_AMMO;
    }
}

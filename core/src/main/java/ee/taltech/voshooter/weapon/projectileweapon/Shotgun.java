package ee.taltech.voshooter.weapon.projectileweapon;

import com.badlogic.gdx.math.Vector2;
import ee.taltech.voshooter.networking.server.gamestate.player.Player;
import ee.taltech.voshooter.weapon.projectile.PistolBullet;
import ee.taltech.voshooter.weapon.projectile.Projectile;
import ee.taltech.voshooter.weapon.projectile.ShotgunPellet;

public class Shotgun extends ProjectileWeapon {

    private static final int STARTING_AMMO = 10;
    private static final int PELLET_COUNT = 9;
    private static final float COOL_DOWN = 1f;
    private static final float CONE_SIZE_DEG = 25f;

    /**
     * @param wielder The player who wields this weapon.
     */
    public Shotgun(Player wielder) {
        super(wielder, COOL_DOWN, STARTING_AMMO, Type.SHOTGUN);
    }

    @Override
    protected void onFire() {
        for (int i = 0; i < PELLET_COUNT; i++) {
            final float start = -(CONE_SIZE_DEG) / 2;
            final float end = CONE_SIZE_DEG / 2;
            final float inc = (end - start) / PELLET_COUNT;

            Vector2 offset = wielder.getViewDirection().cpy().nor().rotateDeg(start + i * inc);

            Projectile p = new ShotgunPellet(
                    wielder,
                    wielder.getPos().cpy().add(offset.cpy().setLength(PistolBullet.RADIUS)),
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

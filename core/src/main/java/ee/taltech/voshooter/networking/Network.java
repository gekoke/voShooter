package ee.taltech.voshooter.networking;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import ee.taltech.voshooter.controller.ActionType;
import ee.taltech.voshooter.geometry.Pos;
import ee.taltech.voshooter.map.GameMap;
import ee.taltech.voshooter.networking.messages.User;
import ee.taltech.voshooter.networking.messages.clientreceived.ChatGamePlayerChange;
import ee.taltech.voshooter.networking.messages.clientreceived.ChatReceiveMessage;
import ee.taltech.voshooter.networking.messages.clientreceived.GameEnd;
import ee.taltech.voshooter.networking.messages.clientreceived.GameStarted;
import ee.taltech.voshooter.networking.messages.clientreceived.LobbyFull;
import ee.taltech.voshooter.networking.messages.clientreceived.LobbyJoined;
import ee.taltech.voshooter.networking.messages.clientreceived.LobbyPlayerNameExists;
import ee.taltech.voshooter.networking.messages.clientreceived.LobbyUserUpdate;
import ee.taltech.voshooter.networking.messages.clientreceived.NoSuchLobby;
import ee.taltech.voshooter.networking.messages.clientreceived.PlayerAmmoUpdate;
import ee.taltech.voshooter.networking.messages.clientreceived.PlayerDashed;
import ee.taltech.voshooter.networking.messages.clientreceived.PlayerDead;
import ee.taltech.voshooter.networking.messages.clientreceived.PlayerDeath;
import ee.taltech.voshooter.networking.messages.clientreceived.PlayerHealthUpdate;
import ee.taltech.voshooter.networking.messages.clientreceived.PlayerKothChange;
import ee.taltech.voshooter.networking.messages.clientreceived.PlayerKothScores;
import ee.taltech.voshooter.networking.messages.clientreceived.PlayerPositionUpdate;
import ee.taltech.voshooter.networking.messages.clientreceived.PlayerStatistics;
import ee.taltech.voshooter.networking.messages.clientreceived.PlayerSwappedWeapon;
import ee.taltech.voshooter.networking.messages.clientreceived.PlayerTookDamage;
import ee.taltech.voshooter.networking.messages.clientreceived.PlayerViewUpdate;
import ee.taltech.voshooter.networking.messages.clientreceived.ProjectileCreated;
import ee.taltech.voshooter.networking.messages.clientreceived.ProjectileDestroyed;
import ee.taltech.voshooter.networking.messages.clientreceived.ProjectilePositionUpdate;
import ee.taltech.voshooter.networking.messages.clientreceived.ProjectilePositions;
import ee.taltech.voshooter.networking.messages.clientreceived.RailgunFired;
import ee.taltech.voshooter.networking.messages.serverreceived.ChangeWeapon;
import ee.taltech.voshooter.networking.messages.serverreceived.ChatSendMessage;
import ee.taltech.voshooter.networking.messages.serverreceived.CreateLobby;
import ee.taltech.voshooter.networking.messages.serverreceived.JoinLobby;
import ee.taltech.voshooter.networking.messages.serverreceived.KeepAlive;
import ee.taltech.voshooter.networking.messages.serverreceived.LeaveLobby;
import ee.taltech.voshooter.networking.messages.serverreceived.LobbySettingsChanged;
import ee.taltech.voshooter.networking.messages.serverreceived.MouseCoords;
import ee.taltech.voshooter.networking.messages.serverreceived.MovePlayer;
import ee.taltech.voshooter.networking.messages.serverreceived.PlayerAction;
import ee.taltech.voshooter.networking.messages.serverreceived.PlayerDash;
import ee.taltech.voshooter.networking.messages.serverreceived.PlayerInput;
import ee.taltech.voshooter.networking.messages.serverreceived.SetUsername;
import ee.taltech.voshooter.networking.messages.serverreceived.Shoot;
import ee.taltech.voshooter.networking.messages.serverreceived.StartGame;
import ee.taltech.voshooter.networking.server.gamestate.player.Bot;
import ee.taltech.voshooter.networking.server.gamestate.player.Player;
import ee.taltech.voshooter.weapon.Weapon;
import ee.taltech.voshooter.weapon.projectile.Projectile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Network {

    // These IDs are used to register objects in ObjectSpaces.
    public static final short REMOTE = 1;
    public static final short SERVER_ENTRY = 2;

    /** Hide public constructor. */
    private Network() {
    }

    /**
     * Register all classes that will be passed over the network.
     * @param endPoint The server.
     */
    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        ObjectSpace.registerClasses(kryo);

        // Register all classes transported over the connection.
        kryo.register(String[].class);
        kryo.register(List.class);
        kryo.register(Set.class);
        kryo.register(HashSet.class);
        kryo.register(String.class);
        kryo.register(ArrayList.class);
        kryo.register(boolean.class);
        kryo.register(Vector2.class);
        kryo.register(Weapon.Type.class);
        kryo.register(HashMap.class);
        kryo.register(Long.class);

        kryo.register(Player.class);
        kryo.register(Bot.class);
        kryo.register(Pos.class);
        kryo.register(GameMap.MapType.class);
        kryo.register(PlayerInput.class);
        kryo.register(PlayerAction.class);
        kryo.register(MouseCoords.class);
        kryo.register(ChatGamePlayerChange.class);
        kryo.register(PlayerKothChange.class);
        kryo.register(PlayerKothScores.class);
        kryo.register(PlayerPositionUpdate.class);
        kryo.register(PlayerViewUpdate.class);
        kryo.register(PlayerHealthUpdate.class);
        kryo.register(PlayerAmmoUpdate.class);
        kryo.register(PlayerDeath.class);
        kryo.register(PlayerDead.class);
        kryo.register(PlayerStatistics.class);
        kryo.register(ChangeWeapon.class);
        kryo.register(ActionType.class);
        kryo.register(Weapon.Type.class);
        kryo.register(PlayerSwappedWeapon.class);
        kryo.register(GameEnd.class);
        kryo.register(PlayerTookDamage.class);
        kryo.register(PlayerDashed.class);
        kryo.register(RailgunFired.class);

        kryo.register(Shoot.class);
        kryo.register(MovePlayer.class);
        kryo.register(PlayerDash.class);

        kryo.register(User.class);
        kryo.register(SetUsername.class);
        kryo.register(CreateLobby.class);
        kryo.register(LobbyJoined.class);
        kryo.register(JoinLobby.class);
        kryo.register(LeaveLobby.class);
        kryo.register(LobbyFull.class);
        kryo.register(LobbyPlayerNameExists.class);
        kryo.register(NoSuchLobby.class);
        kryo.register(LobbyUserUpdate.class);
        kryo.register(StartGame.class);
        kryo.register(GameStarted.class);
        kryo.register(Game.class);
        kryo.register(ProjectileCreated.class);
        kryo.register(ProjectilePositionUpdate.class);
        kryo.register(ProjectileDestroyed.class);
        kryo.register(Projectile.Type.class);
        kryo.register(ProjectilePositions.class);
        kryo.register(ProjectileCreated.class);
        kryo.register(ProjectileDestroyed.class);
        kryo.register(LobbySettingsChanged.class);
        kryo.register(ChatSendMessage.class);
        kryo.register(ChatReceiveMessage.class);
        kryo.register(KeepAlive.class);
    }
}

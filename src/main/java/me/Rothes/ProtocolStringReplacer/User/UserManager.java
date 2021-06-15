package me.Rothes.ProtocolStringReplacer.User;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.UUID;

public class UserManager {

    private HashMap<UUID, User> users = new HashMap<>();

    public User getUser(@NotNull UUID uuid) {
        return users.getOrDefault(uuid, loadUser(uuid));
    }

    @Nonnull
    public User getUser(@NotNull Player player) {
        return users.getOrDefault(player.getUniqueId(), loadUser(player));
    }

    public User loadUser(@NotNull UUID uuid) {
        return users.putIfAbsent(uuid, new User(uuid));
    }

    public User loadUser(@NotNull Player player) {
        return users.putIfAbsent(player.getUniqueId(), new User(player));
    }

    public void unloadUser(@NotNull UUID uuid) {
        users.remove(uuid);
    }

    public void unloadUser(@NotNull Player player) {
        unloadUser(player.getUniqueId());
    }

}

package io.github.rothes.protocolstringreplacer.bukkit.packetlisteners;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.client.CloseWindow;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.client.itemstack.SetCreativeSlot;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.client.itemstack.WindowClick;
import io.github.rothes.protocolstringreplacer.bukkit.ProtocolStringReplacer;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.BossBar;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.Chat;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.EntityMetadata;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.OpenWindow;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.sign.MapChunkUpper18;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.sign.TileEntityDataUpper18;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.title.SetSubtitleText;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.title.SetTitleText;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.title.Title;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.scoreboard.ScoreBoardObjective;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.scoreboard.UpdateScore;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.sign.MapChunk;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.sign.TileEntityData;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.sign.UpdateSign;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.itemstack.SetSlot;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.itemstack.WindowItems;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.itemstack.WindowItemsUpper12;
import org.bukkit.NamespacedKey;

public class PacketListenerManager {

    private ProtocolManager protocolManager;
    private NamespacedKey userCacheKey;

    public NamespacedKey getUserCacheKey() {
        return userCacheKey;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public void initialize() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        if (ProtocolStringReplacer.getInstance().getServerMajorVersion() >= 13) {
            userCacheKey = new NamespacedKey(ProtocolStringReplacer.getInstance(), "user_cache_key");
        }
        addListeners();
    }

    public void addListeners() {
        if (ProtocolStringReplacer.getInstance().getServerMajorVersion() >= 17) {
            protocolManager.addPacketListener(new SetTitleText().packetAdapter);
            protocolManager.addPacketListener(new SetSubtitleText().packetAdapter);
        } else {
            protocolManager.addPacketListener(new Title().packetAdapter);
        }

        if (ProtocolStringReplacer.getInstance().getServerMajorVersion() >= 11) {
            protocolManager.addPacketListener(new WindowItemsUpper12().packetAdapter);
        } else {
            protocolManager.addPacketListener(new WindowItems().packetAdapter);
        }

        if (ProtocolStringReplacer.getInstance().getServerMajorVersion() >= 18) {
            protocolManager.addPacketListener(new MapChunkUpper18().packetAdapter);
            protocolManager.addPacketListener(new TileEntityDataUpper18().packetAdapter);
        } else if (ProtocolStringReplacer.getInstance().getServerMajorVersion() >= 10) {
            protocolManager.addPacketListener(new MapChunk().packetAdapter);
            protocolManager.addPacketListener(new TileEntityData().packetAdapter);
        } else {
            protocolManager.addPacketListener(new UpdateSign().packetAdapter);
        }

        if (ProtocolStringReplacer.getInstance().getServerMajorVersion() >= 8) {
            protocolManager.addPacketListener(new BossBar().packetAdapter);
        }

        protocolManager.addPacketListener(new Chat().packetAdapter);
        protocolManager.addPacketListener(new SetSlot().packetAdapter);
        protocolManager.addPacketListener(new OpenWindow().packetAdapter);
        protocolManager.addPacketListener(new EntityMetadata().packetAdapter);
        protocolManager.addPacketListener(new UpdateScore().packetAdapter);
        protocolManager.addPacketListener(new ScoreBoardObjective().packetAdapter);


        protocolManager.addPacketListener(new WindowClick().packetAdapter);
        protocolManager.addPacketListener(new SetCreativeSlot().packetAdapter);
        protocolManager.addPacketListener(new CloseWindow().packetAdapter);
    }

    public void removeListeners() {
        protocolManager.removePacketListeners(ProtocolStringReplacer.getInstance());
    }

}

package io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.client;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.client.itemstack.AbstractClientItemPacketListener;

public class CloseWindow extends AbstractClientItemPacketListener {

    public CloseWindow() {
        super(PacketType.Play.Client.CLOSE_WINDOW);
    }

    protected void process(PacketEvent packetEvent) {
        getEventUser(packetEvent).setCurrentWindowTitle(null);
    }


}

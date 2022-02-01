package me.rothes.protocolstringreplacer.packetlisteners.client;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import me.rothes.protocolstringreplacer.packetlisteners.client.itemstack.AbstractClientItemPacketListener;

public class CloseWindow extends AbstractClientItemPacketListener {

    public CloseWindow() {
        super(PacketType.Play.Client.CLOSE_WINDOW);
    }

    protected void process(PacketEvent packetEvent) {
        getEventUser(packetEvent).setCurrentWindowTitle(null);
    }


}

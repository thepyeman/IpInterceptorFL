package io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import io.github.rothes.protocolstringreplacer.bukkit.replacer.ListenType;
import io.github.rothes.protocolstringreplacer.bukkit.api.user.PsrUser;

public final class OpenWindow extends AbstractServerPacketListener {

    public OpenWindow() {
        super(PacketType.Play.Server.OPEN_WINDOW, ListenType.WINDOW_TITLE);
    }

    protected void process(PacketEvent packetEvent) {
        PacketContainer packet = packetEvent.getPacket();
        StructureModifier<WrappedChatComponent> wrappedChatComponentStructureModifier = packet.getChatComponents();
        WrappedChatComponent wrappedChatComponent = wrappedChatComponentStructureModifier.read(0);
        String json = wrappedChatComponent.getJson();
        PsrUser user = getEventUser(packetEvent);

        WrappedChatComponent replaced = getReplacedJsonWrappedComponent(packetEvent, user, listenType, json, filter, true);
        if (replaced != null) {
            wrappedChatComponentStructureModifier.write(0,
                    replaced);
        }
    }

}

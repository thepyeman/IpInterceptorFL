package io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.scoreboard;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.AbstractServerPacketListener;
import io.github.rothes.protocolstringreplacer.bukkit.replacer.ListenType;
import io.github.rothes.protocolstringreplacer.bukkit.api.user.PsrUser;

public class UpdateScore extends AbstractScoreBoardListener {

    public UpdateScore() {
        super(PacketType.Play.Server.SCOREBOARD_SCORE, ListenType.SCOREBOARD);
    }

    protected void process(PacketEvent packetEvent) {
        PsrUser user = getEventUser(packetEvent);
        PacketContainer packet = packetEvent.getPacket();
        StructureModifier<String> strings = packet.getStrings();

        String replaced = AbstractServerPacketListener.getReplacedText(packetEvent, user, listenType, strings.read(0), entityNameFilter);
        if (replaced != null) {
            strings.write(0, replaced);
        }
        // TODO: Edit the score (Integer) maybe? But §klazy to do.
        /*if (packet.getScoreboardActions().read(0) != EnumWrappers.ScoreboardAction.REMOVE) {
            int score = packet.getIntegers().read(0);
        }*/
    }

}

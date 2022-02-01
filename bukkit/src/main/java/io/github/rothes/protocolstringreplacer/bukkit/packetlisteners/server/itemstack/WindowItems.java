package io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server.itemstack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BukkitConverters;
import io.github.rothes.protocolstringreplacer.bukkit.ProtocolStringReplacer;
import io.github.rothes.protocolstringreplacer.bukkit.replacer.ReplacerConfig;
import io.github.rothes.protocolstringreplacer.bukkit.replacer.ReplacerManager;
import io.github.rothes.protocolstringreplacer.bukkit.api.user.PsrUser;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WindowItems extends AbstractServerItemPacketListener {

    public WindowItems() {
        super(PacketType.Play.Server.WINDOW_ITEMS);
    }

    protected void process(PacketEvent packetEvent) {
        PsrUser user = getEventUser(packetEvent);
        Object[] read = (Object[]) packetEvent.getPacket().getModifier().read(1);
        ReplacerManager replacerManager = ProtocolStringReplacer.getInstance().getReplacerManager();
        List<ReplacerConfig> replacers = replacerManager.getAcceptedReplacers(user, itemFilter);
        for (Object item : read) {
            ItemStack itemStack = BukkitConverters.getItemStackConverter().getSpecific(item);
            boolean blocked = replaceItemStack(packetEvent, user, listenType, itemStack, replacers);
            if (blocked) {
                return;
            }
        }
    }

}

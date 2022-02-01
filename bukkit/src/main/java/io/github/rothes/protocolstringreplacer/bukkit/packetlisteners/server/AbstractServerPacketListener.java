package io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.server;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import io.github.rothes.protocolstringreplacer.bukkit.packetlisteners.AbstractPacketListener;
import io.github.rothes.protocolstringreplacer.bukkit.replacer.ListenType;
import io.github.rothes.protocolstringreplacer.bukkit.replacer.ReplacerConfig;
import io.github.rothes.protocolstringreplacer.bukkit.replacer.ReplacerManager;
import io.github.rothes.protocolstringreplacer.bukkit.replacer.containers.ChatJsonContainer;
import io.github.rothes.protocolstringreplacer.bukkit.replacer.containers.ItemMetaContainer;
import io.github.rothes.protocolstringreplacer.bukkit.replacer.containers.Replaceable;
import io.github.rothes.protocolstringreplacer.bukkit.replacer.containers.SimpleTextContainer;
import io.github.rothes.protocolstringreplacer.bukkit.ProtocolStringReplacer;
import io.github.rothes.protocolstringreplacer.bukkit.api.capture.CaptureInfoImpl;
import io.github.rothes.protocolstringreplacer.bukkit.api.user.PsrUser;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public abstract class AbstractServerPacketListener extends AbstractPacketListener {

    protected final BiPredicate<ReplacerConfig, PsrUser> filter;
    protected final ListenType listenType;

    protected AbstractServerPacketListener(PacketType packetType, ListenType listenType) {
        super(packetType);
        this.listenType = listenType;
        filter = (replacerConfig, user) -> containType(replacerConfig) && checkPermission(user, replacerConfig);
        packetAdapter = new PacketAdapter(ProtocolStringReplacer.getInstance(), ProtocolStringReplacer.getInstance().getConfigManager().listenerPriority, packetType) {
            public void onPacketSending(PacketEvent packetEvent) {
                boolean readOnly = packetEvent.isReadOnly();
                if (!canWrite(packetEvent)) {
                    return;
                }
                process(packetEvent);
                if (readOnly) {
                    packetEvent.setReadOnly(readOnly);
                }
            }
        };
    }

    protected final boolean containType(ReplacerConfig replacerConfig) {
        return replacerConfig.getListenTypeList().contains(listenType);
    }

    protected final boolean checkPermission(PsrUser user, ReplacerConfig replacerConfig) {
        String permission = replacerConfig.getConfiguration().getString("Options.Filter.PsrUser.Permission");
        if (permission != null) {
            return user.hasPermission(permission);
        }
        return true;
    }

    protected static ChatJsonContainer deployContainer(@Nonnull PacketEvent packetEvent, @Nonnull PsrUser user, @Nonnull ListenType listenType,
                                                       @Nonnull String json, BiPredicate<ReplacerConfig, PsrUser> filter, boolean saveTitle) {
        ReplacerManager replacerManager = ProtocolStringReplacer.getInstance().getReplacerManager();
        List<ReplacerConfig> replacers = replacerManager.getAcceptedReplacers(user, filter);

        return deployContainer(packetEvent, user, listenType, json, replacers, saveTitle);
    }

    protected static ChatJsonContainer deployContainer(@Nonnull PacketEvent packetEvent, @Nonnull PsrUser user, @Nonnull ListenType listenType,
                                                       @Nonnull String json, List<ReplacerConfig> replacers, boolean saveTitle) {
        boolean blocked = false;
        ReplacerManager replacerManager = ProtocolStringReplacer.getInstance().getReplacerManager();

        ChatJsonContainer container = new ChatJsonContainer(json, true);
        CaptureInfoImpl info = null;
        if (user.isCapturing(listenType)) {
            info = new CaptureInfoImpl();
            info.setTime(System.currentTimeMillis());
            info.setUser(user);
            info.setListenType(listenType);
        }

        container.createJsons(container);
        if (saveTitle) {
            List<Replaceable> jsons = container.getJsons();
            user.setCurrentWindowTitle(jsons.get(jsons.size() - 1).getText());
        }
        if (user.isCapturing(listenType)) {
            info.setJsons(container.getJsons());
        }
        if (replacerManager.isJsonBlocked(container, replacers)) {
            packetEvent.setCancelled(true);
            blocked = true;
        } else {
            replacerManager.replaceContainerJsons(container, replacers);
        }
        container.createDefaultChildren();
        container.createTexts(container);
        if (user.isCapturing(listenType)) {
            info.setTexts(container.getTexts());
            user.addCaptureInfo(listenType, info);
        }
        if (blocked || replacerManager.isTextBlocked(container, replacers)) {
            packetEvent.setCancelled(true);
            blocked = true;
        } else {
            replacerManager.replaceContainerTexts(container, replacers);
            replacerManager.setPapi(user, container.getTexts());
        }

        return blocked ? null : container;
    }

    @Nullable
    protected static String getReplacedJson(@Nonnull PacketEvent packetEvent, @Nonnull PsrUser user, @Nonnull ListenType listenType,
                                            @Nonnull String json, BiPredicate<ReplacerConfig, PsrUser> filter) {
        ChatJsonContainer container = deployContainer(packetEvent, user, listenType, json, filter, false);

        if (container != null) {
            return container.getResult();
        } else {
            return null;
        }
    }

    @Nullable
    protected static String getReplacedJson(@Nonnull PacketEvent packetEvent, @Nonnull PsrUser user, @Nonnull ListenType listenType,
                                            @Nonnull String json, List<ReplacerConfig> replacers) {
        ChatJsonContainer container = deployContainer(packetEvent, user, listenType, json, replacers, false);

        if (container != null) {
            return container.getResult();
        } else {
            return null;
        }
    }

    @Nullable
    protected static WrappedChatComponent getReplacedJsonWrappedComponent(@Nonnull PacketEvent packetEvent, @Nonnull PsrUser user, @Nonnull ListenType listenType,
                                                                          @Nonnull String json, BiPredicate<ReplacerConfig, PsrUser> filter, boolean saveTitle) {
        ChatJsonContainer container = deployContainer(packetEvent, user, listenType, json, filter, saveTitle);

        if (container != null) {
            return WrappedChatComponent.fromJson(container.getResult());
        } else {
            return null;
        }
    }

    @Nullable
    protected static WrappedChatComponent getReplacedJsonWrappedComponent(@Nonnull PacketEvent packetEvent, @Nonnull PsrUser user, @Nonnull ListenType listenType,
                                                                          @Nonnull String json, BiPredicate<ReplacerConfig, PsrUser> filter) {
        return getReplacedJsonWrappedComponent(packetEvent, user, listenType, json, filter, false);
    }

    @Nullable
    protected static String getReplacedText(@Nonnull PacketEvent packetEvent, @Nonnull PsrUser user, @Nonnull ListenType listenType,
                                            @Nonnull String text, BiPredicate<ReplacerConfig, PsrUser> filter) {
        ReplacerManager replacerManager = ProtocolStringReplacer.getInstance().getReplacerManager();
        List<ReplacerConfig> replacers = replacerManager.getAcceptedReplacers(user, filter);
        SimpleTextContainer container = new SimpleTextContainer(text);
        CaptureInfoImpl info = null;
        if (user.isCapturing(listenType)) {
            info = new CaptureInfoImpl();
            info.setTime(System.currentTimeMillis());
            info.setUser(user);
            info.setListenType(listenType);
            info.setJsons(new ArrayList<>());
        }

        container.createTexts(container);
        if (user.isCapturing(listenType)) {
            info.setTexts(container.getTexts());
            user.addCaptureInfo(listenType, info);
        }
        if (replacerManager.isTextBlocked(container, replacers)) {
            packetEvent.setCancelled(true);
            return null;
        }
        replacerManager.replaceContainerTexts(container, replacers);
        replacerManager.setPapi(user, container.getTexts());
        return container.getResult();
    }

    protected static boolean replaceItemStack(@Nonnull PacketEvent packetEvent, @Nonnull PsrUser user, @Nonnull ListenType listenType,
                                              @Nonnull ItemStack itemStack, BiPredicate<ReplacerConfig, PsrUser> filter) {
        ReplacerManager replacerManager = ProtocolStringReplacer.getInstance().getReplacerManager();
        List<ReplacerConfig> replacers = replacerManager.getAcceptedReplacers(user, filter);
        return replaceItemStack(packetEvent, user, listenType, itemStack, replacers);
    }

    protected static boolean replaceItemStack(@Nonnull PacketEvent packetEvent, @Nonnull PsrUser user, @Nonnull ListenType listenType,
                                              @Nonnull ItemStack itemStack, List<ReplacerConfig> replacers) {
        if (itemStack.hasItemMeta()) {
            ItemStack original = itemStack.clone();

            ReplacerManager replacerManager = ProtocolStringReplacer.getInstance().getReplacerManager();
            ItemMetaContainer container = new ItemMetaContainer(itemStack.getItemMeta());
            CaptureInfoImpl info = null;
            if (user.isCapturing(listenType)) {
                info = new CaptureInfoImpl();
                info.setTime(System.currentTimeMillis());
                info.setUser(user);
                info.setListenType(listenType);
            }

            container.createDefaultChildren();
            boolean fromCache = container.isFromCache();
            if (fromCache && container.getMetaCache().isBlocked()) {
                packetEvent.setCancelled(true);
                return true;
            }

            container.createJsons(container);
            if (!fromCache && replacerManager.isJsonBlocked(container, replacers)) {
                packetEvent.setCancelled(true);
                container.getMetaCache().setBlocked(true);
                return true;
            }
            if (!fromCache) {
                if (user.isCapturing(listenType)) {
                    info.setJsons(container.getJsons());
                }
                replacerManager.replaceContainerJsons(container, replacers);
            }
            container.createTexts(container);
            if (!fromCache && replacerManager.isTextBlocked(container, replacers)) {
                packetEvent.setCancelled(true);
                container.getMetaCache().setBlocked(true);
                return true;
            }
            if (!fromCache) {
                if (user.isCapturing(listenType)) {
                    info.setTexts(container.getTexts());
                    user.addCaptureInfo(listenType, info);
                }
                replacerManager.replaceContainerTexts(container, replacers);
                itemStack.setItemMeta(container.getResult());
            }

            if (!fromCache && !original.isSimilar(itemStack)) {
                user.saveUserMetaCache(original, itemStack);
            }

            List<Integer> papiIndexes;
            if (fromCache) {
                papiIndexes = container.getMetaCache().getPlaceholderIndexes();
            } else {
                papiIndexes = replacerManager.getPapiIndexes(container.getTexts());
                container.getMetaCache().setPlaceholderIndexes(papiIndexes);
            }
            replacerManager.setPapi(user, container.getTexts(), papiIndexes);
            itemStack.setItemMeta(container.getResult());
        }
        return false;
    }

}

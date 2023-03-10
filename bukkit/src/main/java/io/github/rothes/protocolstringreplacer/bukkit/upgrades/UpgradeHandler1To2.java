package io.github.rothes.protocolstringreplacer.bukkit.upgrades;

import io.github.rothes.protocolstringreplacer.bukkit.ProtocolStringReplacer;
import io.github.rothes.protocolstringreplacer.bukkit.api.configuration.CommentYamlConfiguration;
import io.github.rothes.protocolstringreplacer.bukkit.api.configuration.DotYamlConfiguration;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class UpgradeHandler1To2 extends AbstractUpgradeHandler{

    private enum PacketType {
        CHAT("CHAT", "chat"),
        TITLE("TITLE", "title"),
        BOSS_BAR("BOSS_BAR", "boss-bar"),
        OPEN_WINDOW("OPEN_WINDOW", "window-title"),
        SET_SLOT("SET_SLOT", "itemstack"),
        WINDOW_ITEMS("WINDOW_ITEMS", "itemstack"),
        ENTITY_METADATA("ENTITY_METADATA", "entity"),
        TILE_ENTITY_DATA("TILE_ENTITY_DATA", "sign"),
        MAP_CHUNK("MAP_CHUNK", "sign");

        private String packetType;
        private String listenType;
        PacketType(String packetType, String listenType) {
            this.packetType = packetType;
            this.listenType = listenType;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void upgrade() {
        upgradeAllReplacerConfigs(new File(ProtocolStringReplacer.getInstance().getDataFolder() + "/Replacers"));

        CommentYamlConfiguration config = ProtocolStringReplacer.getInstance().getConfig();
        ListOrderedMap keyValues = new ListOrderedMap();
        ConfigurationSection configurationSection = config.getConfigurationSection("");
        for (String key: configurationSection.getKeys(true)) {
            if (key.equals("Configs-Version")) {
                continue;
            }
            keyValues.put(key, config.get(key));
        }
        config = new CommentYamlConfiguration();
        config.set("12340????????????????????????", "0| # ??????????????????Configs-Version???!");
        config.set("Configs-Version", 2);
        config.set("12341????????????????????????", "0| #");
        Set<Map.Entry<String, Object>> entrySet = (Set<Map.Entry<String, Object>>) keyValues.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            config.set(entry.getKey(), entry.getValue());
        }
        try {
            config.save(ProtocolStringReplacer.getInstance().getConfigFile());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void upgradeReplacerConfig(@Nonnull File file, @Nonnull DotYamlConfiguration config) {
        Validate.notNull(file, "configuration File cannot be null");
        Validate.notNull(config, "configuration cannot be null");

        LinkedList<String> listenTypes = new LinkedList<>();
        List<String> packetTypes = config.getStringList("Options???Filter???Packet-Types");
        if (packetTypes.isEmpty()) {
            for (PacketType packetType : PacketType.values()) {
                if (!listenTypes.contains(packetType.listenType)) {
                    listenTypes.add(packetType.listenType);
                }
            }
        } else {
            for (String type : packetTypes) {
                boolean typeFound = false;
                for (PacketType packetType : PacketType.values()) {
                    if (packetType.packetType.equals(type)) {
                        typeFound = true;
                        if (!listenTypes.contains(packetType.listenType)) {
                            listenTypes.add(packetType.listenType);
                        }
                        break;
                    }
                }
                if (!typeFound) {
                    ProtocolStringReplacer.warn("??c????????????????????????????????????: " + type + ", ?????????.");
                }
            }
        }

        config.set("Options???Filter???12340????????????????????????", "0|     # window-title ????????????????????????");
        config.set("Options???Filter???12341????????????????????????", "1|     # itemstack ????????????(?????????|Lore|?????????|?????????)??????");
        config.set("Options???Filter???12342????????????????????????", "2|     # boss-bar ??????Boss???????????????");
        config.set("Options???Filter???12343????????????????????????", "3|     # entity ?????????????????????");
        config.set("Options???Filter???12344????????????????????????", "4|     # title ????????????(title|subtitle)??????");
        config.set("Options???Filter???12345????????????????????????", "5|     # sign ?????????????????????");
        config.set("Options???Filter???12346????????????????????????", "6|     # chat ????????????(chat|actionbar)????????????");
        config.set("Options???Filter???12347????????????????????????", "7|     # ??????????????????????????????. ???????????????. ?????????: ");
        config.set("Options???Filter???Listen-Types", listenTypes);

        Pattern commentPattern = CommentYamlConfiguration.getCommentKeyPattern();
        List<String> commentKeys = new ArrayList<>();
        ConfigurationSection configurationSection = config.getConfigurationSection("");
        for (String key: configurationSection.getKeys(true)) {
            if (commentPattern.matcher(key).find()) {
                commentKeys.add(key);
            } else {
                if (key.equals("Config-Version") || key.equals("Options???Filter???Packet-Types")) {
                    for (String commentKey : commentKeys) {
                        config.set(commentKey, null);
                    }
                    config.set(key, null);
                }
                commentKeys.clear();
            }
        }
        try {
            config.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}

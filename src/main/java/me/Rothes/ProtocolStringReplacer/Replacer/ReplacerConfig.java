package me.Rothes.ProtocolStringReplacer.Replacer;

import com.comphenix.protocol.PacketType;
import me.Rothes.ProtocolStringReplacer.API.Configuration.CommentYamlConfiguration;
import me.Rothes.ProtocolStringReplacer.API.Configuration.DotYamlConfiguration;
import me.Rothes.ProtocolStringReplacer.ProtocolStringReplacer;
import org.apache.commons.collections.map.ListOrderedMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ReplacerConfig {

    public enum MatchType {
        CONTAIN("contain"),
        EQUAL("equal"),
        REGEX("regex");

        private String name;

        MatchType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private File file;
    private DotYamlConfiguration configuration;
    private boolean enable;
    private int priority;
    private List<PacketType> packetTypeList = new ArrayList<>();
    private MatchType matchType;
    private ListOrderedMap replaces = new ListOrderedMap();
    private String author;
    private String version;

    public ReplacerConfig(@Nonnull File file, @Nonnull DotYamlConfiguration configuration) {
        long startTime = System.currentTimeMillis();
        this.file = file;
        this.configuration = configuration;
        enable = configuration.getBoolean("Enable", false);
        priority = configuration.getInt("Priority", 5);
        author = configuration.getString("Author");
        version = configuration.getString("Version");
        List<String> types = configuration.getStringList("Filter鰠Packet-Types");
        boolean typeFound;
        if (types.isEmpty()) {
            ReplacerType[] replacerTypes = ReplacerType.values();
            for (ReplacerType replacerType : replacerTypes) {
                packetTypeList.add(replacerType.getPacketType());
            }
        } else {
            for (String type : types) {
                typeFound = false;
                for (ReplacerType replacerType : ReplacerType.values()) {
                    if (replacerType.getName().equals(type)) {
                        typeFound = true;
                        packetTypeList.add(replacerType.getPacketType());
                        break;
                    }
                }
                if (!typeFound) {
                    Bukkit.getConsoleSender().sendMessage("§7[§cProtocol§6StringReplacer§7] §c未知或不支持的数据包类型: " + type);
                }
            }
        }
        if (Integer.parseInt(Bukkit.getServer().getBukkitVersion().split("\\.")[1].split("-")[0]) >= 17) {
            if (packetTypeList.remove(PacketType.Play.Server.TITLE)) {
                packetTypeList.add(PacketType.Play.Server.SET_TITLE_TEXT);
                packetTypeList.add(PacketType.Play.Server.SET_SUBTITLE_TEXT);
            }
        }

        String matchType = configuration.getString("Match-Type", "contain");
        typeFound = false;
        for (MatchType availableMatchType : MatchType.values()) {
            if (availableMatchType.name.equalsIgnoreCase(matchType)) {
                this.matchType = availableMatchType;
                typeFound = true;
                break;
            }
        }
        if (!typeFound) {
            this.matchType = MatchType.EQUAL;
            Bukkit.getConsoleSender().sendMessage("§7[§cProtocol§6StringReplacer§7] §c未知的文本匹配方式: " + matchType + ". 使用默认值\"contain\"");
        }
        ConfigurationSection section = configuration.getConfigurationSection("Replaces");
        if (section != null) {
            Pattern commentKeyPattern = CommentYamlConfiguration.getCommentKeyPattern();
            if (this.matchType == MatchType.REGEX) {
                for (String replace : section.getKeys(true)) {
                    if (commentKeyPattern.matcher(replace).find()) {
                        return;
                    }
                    replaces.put(Pattern.compile(replace, Pattern.DOTALL), configuration.getString("Replaces鰠" + replace));
                }
            } else {
                for (String replace : section.getKeys(true)) {
                    if (commentKeyPattern.matcher(replace).find()) {
                        return;
                    }
                    replaces.put(replace, configuration.getString("Replaces鰠" + replace));
                }
            }
        }
        if (ProtocolStringReplacer.getInstance().getConfig().getBoolean("Options.Features.Console.Print-Replacer-Config-When-Loaded", false)) {
            Bukkit.getConsoleSender().sendMessage("§7[§cProtocol§6StringReplacer§7] §a载入替换配置: " + getRelativePath() + ". §8耗时 " + (System.currentTimeMillis() - startTime) + "ms");
        }
    }

    public File getFile() {
        return file;
    }

    public boolean isEnable() {
        return enable;
    }

    public DotYamlConfiguration getConfiguration() {
        return configuration;
    }

    public int getPriority() {
        return priority;
    }

    public List<PacketType> getPacketTypeList() {
        return packetTypeList;
    }

    public ListOrderedMap getReplaces() {
        return replaces;
    }

    public String getAuthor() {
        return author;
    }

    public String getVersion() {
        return version;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public String getRelativePath() {
        return file.getAbsolutePath().replace(ProtocolStringReplacer.getInstance().getDataFolder().getAbsolutePath() + "\\", "");
    }

    @Override
    public String toString() {
        return "ReplacerConfig{" +
                "file=" + file +
                ", configuration=" + configuration +
                ", enable=" + enable +
                ", priority=" + priority +
                ", packetTypeList=" + packetTypeList +
                ", matchType=" + matchType +
                ", replaces=" + replaces +
                ", author='" + author + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

}
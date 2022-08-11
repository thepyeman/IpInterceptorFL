package me.rothes.protocolstringreplacer.api.replacer;

import me.rothes.protocolstringreplacer.api.configuration.CommentYamlConfiguration;
import me.rothes.protocolstringreplacer.replacer.ListenType;
import me.rothes.protocolstringreplacer.replacer.MatchMode;
import me.rothes.protocolstringreplacer.replacer.ReplacesMode;
import org.apache.commons.collections.map.ListOrderedMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.neosearch.stringsearcher.StringSearcher;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.List;

public interface ReplacerConfig {

    /**
     * Determines whether PSR should saveConfig on disable or not.
     *
     * @return Edited.
     */
    boolean isEdited();

    /**
     * @return If this replacer config is enabled.
     */
    boolean isEnabled();

    /**
     * @return The priority of this replacer config.
     */
    int getPriority();

    @NotNull List<ListenType> getListenTypeList();
    @NotNull ListOrderedMap getReplaces(@Nonnull ReplacesMode replacesMode);
    @NotNull List<Object> getBlocks(@Nonnull ReplacesMode replacesMode);
    @Nullable String getAuthor();
    @Nullable String getVersion();
    @NotNull MatchMode getMatchMode();
    @NotNull StringSearcher<String> getReplacesStringSearcher(ReplacesMode replacesMode);
    @NotNull StringSearcher<String> getBlocksStringSearcher(ReplacesMode replacesMode);

    /**
     * @return The relative path of this replacer config. Used for some commands.
     */
    @NotNull String getRelativePath();

    /**
     * @return The file of the FileConfiguration. Can be null if not exist.
     */
    @Nullable File getFile();
    @Nullable CommentYamlConfiguration getConfiguration();
    void saveConfig();

}

package me.Rothes.ProtocolStringReplacer.API.Configuration;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentYamlConfiguration extends YamlConfiguration {

    protected static Pattern commentKeyPattern = Pattern.compile("([0-9]+)这是注释([0-9]+)");
    protected static Pattern commentPattern = Pattern.compile("^( *)([0-9]+)这是注释([0-9]+): (')");

    protected Pattern startedSpacePattern = Pattern.compile("^( *)");
    protected Pattern endedSpacePattern = Pattern.compile("( *)$");

    public static Pattern getCommentKeyPattern() {
        return commentKeyPattern;
    }

    @Override
    public void loadFromString(@Nonnull String contents) throws InvalidConfigurationException {
        Validate.notNull(contents, "Contents cannot be null");

        String[] lines = contents.split("\n");
        StringBuilder stringBuilder = new StringBuilder();
        short commentIndex = 0;
        short passedLines = 1;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            stringBuilder.append(line).append("\n");
            if (line.contains("#")) {
                String startedSpace = getStartedSpace(line);
                int cursor = startedSpace.length();
                // Initialize with an impossible value.
                char quoteChar = '\n';
                boolean isInQuote = false;
                boolean commentFound = false;
                boolean isPath = false;
                char[] chars = line.toCharArray();
                for (; cursor < line.length(); cursor++) {
                    char charAtCursor = chars[cursor];
                    if (isPath && charAtCursor != ' ') {
                        isPath = false;
                    }
                    if (isInQuote) {
                        if (charAtCursor == quoteChar) {
                            if (chars[cursor + 1] == charAtCursor) {
                                cursor++;
                            } else {
                                isInQuote = false;
                            }
                        }
                    } else {
                        if (charAtCursor == '\'' || charAtCursor == '\"') {
                            quoteChar = charAtCursor;
                            isInQuote = true;
                        } else if (charAtCursor == ':') {
                            isPath = true;
                        } else if (charAtCursor == '#') {
                            commentFound = true;
                            break;
                        }
                    }
                }
                if (commentFound) {
                    if (isPath) {
                        startedSpace = getStartedSpace(lines[i + 1]);
                    }
                    // The comment behind the settings will be removed when saved, so we only need to do this.
                    stringBuilder.append(startedSpace).append(commentIndex++).append("这是注释").append(passedLines).append(": '").append(getEndedSpace(line.substring(0, cursor))).append(line.substring(cursor).replace("'", "''")).append("'").append("\n");
                }
            }
        }
        super.loadFromString(stringBuilder.toString());
    }

    @Override
    @Nonnull
    public String saveToString() {
        String contents = super.saveToString();
        String[] lines = contents.split("\n");
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : lines) {
            Matcher matcher = commentPattern.matcher(line);
            if (matcher.find()) {
                stringBuilder.append(StringUtils.replace(line.substring(matcher.group(0).length()), "''", "'"));
                if (("'").equals(matcher.group(4))) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                stringBuilder.append("\n");
            } else {
                stringBuilder.append(line).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    @Nonnull
    public static CommentYamlConfiguration loadConfiguration(@Nonnull File file) {
        Validate.notNull(file, "File cannot be null");
        CommentYamlConfiguration config = new CommentYamlConfiguration();

        try {
            config.load(file);
        } catch (FileNotFoundException ignored) {
        } catch (IOException | InvalidConfigurationException var4) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, var4);
        }

        return config;
    }

    protected String getStartedSpace(@Nonnull String string) {
        Validate.notNull(string, "String cannot be null");

        Matcher matcher = startedSpacePattern.matcher(string);
        //noinspection ResultOfMethodCallIgnored
        matcher.find();
        return matcher.group(1);
    }

    protected String getEndedSpace(@Nonnull String string) {
        Validate.notNull(string, "String cannot be null");

        Matcher matcher = endedSpacePattern.matcher(string);
        //noinspection ResultOfMethodCallIgnored
        matcher.find();
        return matcher.group(1);
    }

}

package BlockKit;

import java.util.ArrayList;

public class BlocksAndElements {
    private static ArrayList<String> blocks = new ArrayList<>();
    private BlocksAndElements() {
    }

    public static String blocks() {
        StringBuilder blocksString = new StringBuilder();
        for (String block: blocks) {
            blocksString.append(block).append(",");
        }
        blocksString.deleteCharAt(blocksString.lastIndexOf(","));
        return "{\"blocks\": ["+ blocksString + "]}";
    }

    public static void setBlock(String block) {
        BlocksAndElements.blocks.add(block);
    }

    public static void clearBlockList() {
        BlocksAndElements.blocks.clear();
    }

    public static void headerBlock(String headerText) {
        setBlock("{\"type\": \"header\",\"text\": {\"type\": \"plain_text\",\"text\": \"" + headerText + "\",\"emoji\": true}}");
    }

    public static void dividerBlock() {
        setBlock("{\"type\": \"divider\"}");
    }

    public static void sectionBlock(String text) {
        setBlock("{\"type\": \"section\",\"text\": }");
    }

    public static void sectionBlock(String mrkdwnText, String fields) {
        setBlock("{\"type\": \"section\",\"text\": " + mrkdwnText + "," + fields + "}");
    }

    public static String fieldsEl(String ...fields) {
        StringBuilder fieldsList = new StringBuilder();
        for (String field: fields) {
            fieldsList.append(field).append(",");
        }
        fieldsList.deleteCharAt(fieldsList.lastIndexOf(","));
        return "\"fields\": [" + fieldsList + "]";
    }

    public static String markdownTextEl(String text) {
        return "{\"type\": \"mrkdwn\",\"text\":\"" + text + "\"}";
    }
}

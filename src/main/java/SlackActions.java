import com.google.gson.Gson;
import com.slack.api.bolt.context.Context;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.methods.response.conversations.ConversationsListResponse;
import com.slack.api.model.Conversation;
import com.slack.api.model.Message;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static BlockKit.BlocksAndElements.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class SlackActions {
    static Optional<List<Message>> conversationHistory = Optional.empty();

    /**
     * Find conversation ID using the conversations.list method
     */
    public static String findChannel(String name, Context ctx) {
        try {
            ConversationsListResponse result = ctx.client().conversationsList(r -> r
                    .token(System.getenv("SLACK_BOT_TOKEN"))
            );
            for (Conversation channel : result.getChannels()) {
                if (channel.getName().equals(name)) {
                    return channel.getId();
                }
            }
        } catch (IOException | SlackApiException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Fetch conversation history using ID from last example
     */
    public static Optional<List<Message>> fetchChannelHistory(String id, Context ctx) {
        try {
            ConversationsHistoryResponse result = ctx.client().conversationsHistory(r -> r
                    .token(System.getenv("SLACK_BOT_TOKEN"))
                    .channel(id)
            );
            return Optional.ofNullable(result.getMessages());
            // Print results

        } catch (IOException | SlackApiException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Post a message to a channel your app is in using ID and message text
     */
    public static ChatPostMessageResponse publishMessage(String id, String text, Context ctx) {
        try {
            return ctx.client().chatPostMessage(r -> r
                    .token(System.getenv("SLACK_BOT_TOKEN"))
                    .channel(id)
                    .text(text)
            );
        } catch (IOException | SlackApiException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    //TODO Post a message to a channel your app is in using ID and Slack Blocks

    //public static ChatPostMessageResponse publishViewMessage(String id, List<LayoutBlock> blocks, Context ctx) throws SlackApiException, IOException {
    //    return ctx.client().chatPostMessage(req -> req
    //            .token(System.getenv("SLACK_BOT_TOKEN"))
    //            .channel(id)
    //            .blocks(blocks)
    //            .text("!")
    //    );
    //}

    /**
     * Publish a message with the channel ID and message TS
     */
    public static ChatPostMessageResponse replyMessage(String id, String ts, Context ctx, String text) {
        try {
            return ctx.client().chatPostMessage(r -> r
                    // The token you used to initialize your app
                    .token(System.getenv("SLACK_BOT_TOKEN"))
                    .channel(id)
                    .threadTs(ts)
                    .text(text)
            );
        } catch (IOException | SlackApiException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    //Следует поменять сигнатуру метода, в аргументах должна быть строка jql
    public static ArrayList getIssuesFromProject(String project) {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("Accept-Encoding", "UTF-8");

        HashMap request = Unirest.get("https://burning-heart.atlassian.net/rest/api/3/search")
                .basicAuth(System.getenv("JIRA_USERNAME"), System.getenv("JIRA_TOKEN"))
                .headers(headers)
                .queryString("jql", "project = " + project + " AND status = \"On HOLD\" AND assignee in (60c86be318e9f60071701bf2, 5ebbf802a39bb50bb1410a28, 5ec54570b12d2b0c2f81e869) AND sprint in (openSprints())")
                .asObject(i -> new Gson().fromJson(i.getContentReader(), HashMap.class))
                .getBody();
        JSONArray issues = (JSONArray) new JSONObject(request).get("issues");
        ArrayList<JSONObject> issueShortList = new ArrayList<>();
        for (Object issue : issues) {
            JSONObject innerObj = (JSONObject) issue;
            JSONObject shortIssue = new JSONObject();
            HashMap issueReq = Unirest.get("https://burning-heart.atlassian.net/rest/api/3/issue/" + innerObj.get("key").toString() + "/comment")
                    .basicAuth(System.getenv("JIRA_USERNAME"), System.getenv("JIRA_TOKEN"))
                    .headers(headers)
                    .asObject(i -> new Gson().fromJson(i.getContentReader(), HashMap.class))
                    .getBody();
           shortIssue.put("self", innerObj.get("self").toString());
            shortIssue.put("key", innerObj.get("key"));
            shortIssue.put("id", innerObj.get("id"));

            innerObj = (JSONObject) innerObj.get("fields");
            shortIssue.put("issue_name", innerObj.get("summary").toString().replace("\"", ""));
            JSONArray comments = (JSONArray) new JSONObject(issueReq).get("comments");
            innerObj = (JSONObject) comments.get(comments.length() - 1);
            JSONObject comment = (JSONObject) innerObj.get("author");
            shortIssue.put("comment_author", comment.get("displayName"));
            comment = (JSONObject) innerObj.get("body");
            JSONArray comment_content = (JSONArray) comment.get("content");
            comment = (JSONObject) comment_content.get(0);
            comment_content = (JSONArray) comment.get("content");
            comment = (JSONObject) comment_content.get(0);
            shortIssue.put("comment", comment.get("text"));
            issueShortList.add(shortIssue);
        }
        return issueShortList;
    }

    public static void sendProjectIssuesWithWebHook(String project) throws UnsupportedEncodingException {
        ArrayList issues = SlackActions.getIssuesFromProject(project);
        int itemCounter = 1;
        //Вызов метода *Block() из BlockAndElements добавляет блок в поле класса blocks
        //Вызывать методы блоков строго в том порядке, в котором они должны быть отображены
        headerBlock("ON HOLD");
        dividerBlock();

        // Кодировка для кириллицы Cp1251, не потерять эту мысль!
        String text = new String("*Последний комментарий*".getBytes("Cp1251"), UTF_8);
        for (Object issue : issues) {
            JSONObject innerObj = (JSONObject) issue;
            String self = innerObj.get("self").toString();
            String key = innerObj.get("key").toString();
            String issue_name = innerObj.get("issue_name").toString();
            String comment_author = innerObj.get("comment_author").toString();
            String comment = innerObj.get("comment").toString();

            sectionBlock(
                    //Блок включает в себя элементы, совместимость элементов с блоками можно проверить в песочнице:
                    //https://app.slack.com/block-kit-builder/TDN2PEPQB#%7B%22blocks%22:%5B%5D%7D
                markdownTextEl("<" + self + "|" + itemCounter + ".>    <" + self + "|*" + key + "*>     <" + self + "|" + issue_name + ">"),
                fieldsEl(
                    markdownTextEl(text + "\\n\\n*" + comment_author + "*\\n\\n>" + comment)
                )
            );
            dividerBlock();
            itemCounter++;
        }

        byte[] bodyBytes = blocks().getBytes();
        System.out.print(new String(bodyBytes, UTF_8));
        //Очищаем список blocks после того как перевели будущее тело запроса в байты
        clearBlockList();
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Accept", "*/*");
            headers.put("Accept-Encoding", "gzip, deflate");
            headers.put("Accept-Charset", "UTF-8");
            headers.put("charset", "UTF-8");
            headers.put("Cache-Control", "no-cache");
            headers.put("cache-control", "no-cache");
            headers.put("Host", "hooks.slack.com");
            headers.put("Authorization", "Bearer " + System.getenv("SLACK_BOT_TOKEN"));

            String req = Unirest.post(System.getenv("WEBHOOK_B2G"))
                    .headers(headers)
                    .body(new String(bodyBytes, UTF_8))
                    .asString()
                    .getBody();
            System.out.println(req);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

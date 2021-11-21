import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.response.Response;

/**
 * Обработчики команд от участников канала Slack
 */
public class CommandHandlers {
    static Response helloCommandHandler(SlashCommandRequest request, SlashCommandContext ctx) {

        return ctx.ack();
    }
}

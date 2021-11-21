import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.views.ViewsPublishResponse;
import com.slack.api.model.event.AppHomeOpenedEvent;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;
import com.slack.api.model.view.View;

import java.io.IOException;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.view.Views.view;

public class EventHandlers {
    public static Response AppHomeOpenedEventHandler(EventsApiPayload<AppHomeOpenedEvent> payload, EventContext ctx) throws SlackApiException, IOException {

        /*
          Отображение информации на домашней странице бота
         */
        View appHomeView = view(view -> view
                .type("home")
                .blocks(asBlocks(
                        section(section -> section.text(markdownText(mt -> mt.text("*Welcome to SlackJiraBot* :tada:")))),
                        divider(),
                        section(section -> section.text(markdownText(mt -> mt.text("You can send me key of your issue (for ex: HVGCD-8345) to get issue description"))))

                ))
        );
        ViewsPublishResponse res = ctx.client().viewsPublish(r -> r
                .userId(payload.getEvent().getUser())
                .view(appHomeView)
        );

        return ctx.ack();
    }

    protected static Response AppMentionEventHandler(EventsApiPayload<AppMentionEvent> payload, EventContext ctx) {
        return ctx.ack();
    }

    protected static Response MessageEventHandler(EventsApiPayload<MessageEvent> payload, EventContext ctx) {
        return ctx.ack();
    }
}
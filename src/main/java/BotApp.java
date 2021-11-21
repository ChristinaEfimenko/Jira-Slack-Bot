import com.slack.api.bolt.App;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.model.event.AppHomeOpenedEvent;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;

public class BotApp {
    public static void main(String[] args) throws Exception {
        // App expects an env variable: SLACK_BOT_TOKEN

        App slack = new App();
        /*
          Здесь будут описаны все команды бота и события, на которые он реагирует
         */
        /*
        slack.command("/yourCommand", CommandHandlers::yourCommandHandler);
         */
        slack.event(AppHomeOpenedEvent.class, EventHandlers::AppHomeOpenedEventHandler);

        //В plusDays указать через сколько дней запустить метод первый раз с момента поднятия сервера,
        //withHour() и withMinute() время запуска метода
        long delayInSeconds =  ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(2).withHour(9)
                .withMinute(30).toInstant().getEpochSecond() -
                ZonedDateTime.now().toInstant().getEpochSecond();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                SlackActions.sendProjectIssuesWithWebHook("B2GED");
            }
        };
        timer.scheduleAtFixedRate(task, delayInSeconds*1000, 1000*60*60*24*7);
        //SocketModeApp expects an env variable: SLACK_APP_TOKEN
        new SocketModeApp(slack).start();
    }

}

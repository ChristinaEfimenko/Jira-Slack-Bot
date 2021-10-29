package command

import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted
import data.Cache
import data.JiraConfig

interface BotCommand {
    suspend fun handle(event: SlackMessagePosted, session: SlackSession)
}

interface BotCommandFactory<out BotCommand> {
    fun create(cache: Cache, jiraConfig: JiraConfig) : BotCommand
}

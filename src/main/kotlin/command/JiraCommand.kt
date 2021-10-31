package command

import com.atlassian.jira.rest.client.api.JiraRestClientFactory
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import com.google.gson.Gson
import com.ullink.slack.simpleslackapi.SlackAttachment
import com.ullink.slack.simpleslackapi.SlackPreparedMessage
import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted
import data.Cache
import data.JiraConfig
import data.JiraIssue
import data.ResponseConfig
import java.net.URI
import org.slf4j.LoggerFactory

class JiraCommand {

    class TaskCommand(val cache: Cache, val jiraConfig: JiraConfig) : BotCommand {
        private val LOGGER = LoggerFactory.getLogger(TaskCommand::class.java)

        override suspend fun handle(event: SlackMessagePosted, session: SlackSession) {
            val userName = event.messageContent.split(regex = Regex("\\s+"))[1]
            val response = queryTask(userName)
            val preparedMessage = SlackPreparedMessage.builder()
                .message(response.message)
                .attachments(response.attachments)
                .build()
            session.sendMessage(event.channel, preparedMessage)
        }

        private suspend fun queryTask(userName: String): ResponseConfig {
            LOGGER.info("query task for user $userName")
            val factory: JiraRestClientFactory = AsynchronousJiraRestClientFactory()
            val jiraServerUri = URI(jiraConfig.uri)
            val restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, jiraConfig.email, jiraConfig.password)

            val result = restClient.searchClient.searchJql("project = TRM AND resolution = Unresolved AND assignee = $userName").claim()

            val attachments = result.issues.asSequence()
                .map {
                    LOGGER.info("request data for ${it.key}")
                    if (cache.get(it.key) != null) {
                        val issue = Gson().fromJson(cache.get(it.key), JiraIssue::class.java)
                        LOGGER.info("get from cache: " + issue.key)
                        issue
                    } else {
                        val issue = JiraIssue(restClient.issueClient.getIssue(it.key).claim())
                        cache.set(it.key, Gson().toJson(issue))
                        LOGGER.info("requested from api: " + issue.key)
                        issue
                    }
                }
                .map {
                    LOGGER.info("create attachment ${it.key}")
                    val attachment = SlackAttachment()
                    attachment.title = it.summary
                    attachment.titleLink = "${jiraConfig.uri}/browse/${it.key}"
                    attachment.color = "#7CD197"
                    attachment
                }.toList()
            return ResponseConfig("Task of $userName", attachments)
        }
    }

        class Factory : BotCommandFactory<TaskCommand> {
            override fun create(cache: Cache, jiraConfig: JiraConfig): TaskCommand = TaskCommand(cache, jiraConfig)
        }
}

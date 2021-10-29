package bot

import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener
import command.BotCommand
import command.BotCommandFactory
import data.BotConfig
import data.Cache
import data.CommandConfig
import data.JiraConfig
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BotProcess(var config: BotConfig = BotConfig(), var jiraConfig: JiraConfig = JiraConfig()) {
    private val cache = Cache()
    private val commandRegistry = hashMapOf<String, BotCommandFactory<BotCommand>>()
    private var botId: String? = null

    private val messagePostedListener = SlackMessagePostedListener { event, session ->
        handleIncomingEvent(event, session)
    }

    fun start() {
        SlackSessionFactory.createWebSocketSlackSession(config.authToken).apply {
            connect()
            addMessagePostedListener(messagePostedListener)
            botId = findUserByUserName(config.name).id
            println("${config.name} is started with id $botId")
        }
    }

    private fun handleIncomingEvent(event: SlackMessagePosted, session: SlackSession) {
        println(event.toString())

        if (event.isNotMe()) {
            CoroutineScope(EmptyCoroutineContext).launch {
                val key = event.getCommandKey()
                val command = commandRegistry[key]

                command?.create(cache = cache, jiraConfig = jiraConfig)?.apply {
                    handle(event, session)
                }
            }
        }
    }

    fun config(block: BotConfig.() -> Unit) {
        config = BotConfig().apply(block)
    }

    fun jiraConfig(block: JiraConfig.() -> Unit) {
        jiraConfig = JiraConfig().apply(block)
    }

    fun command(block: CommandConfig.() -> Unit) {
        val config = CommandConfig().apply(block)
        register(config.command, config.factory)
    }

    fun register(key: String, factory: BotCommandFactory<BotCommand>?) {
        factory?.let {
            if (!commandRegistry.containsKey(key)) {
                commandRegistry.put(key, it)
            }
        }
    }

    private fun SlackMessagePosted.getCommandKey(): String {
        return messageContent.split(regex = Regex("\\s+"))[0]
    }

    private fun SlackMessagePosted.isNotMe(): Boolean = botId != user.id
}

fun botProcess(block: BotProcess.() -> Unit): BotProcess {
    return BotProcess().apply(block)
}

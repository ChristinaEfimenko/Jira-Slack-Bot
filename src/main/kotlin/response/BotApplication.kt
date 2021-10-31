package response

import PropertiesProvider.configOf
import bot.botProcess
import command.JiraCommand
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.routing.*
import io.ktor.gson.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.*
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    val port = System.getenv("PORT").toIntOrNull() ?: 8080

    embeddedServer(Netty, port) {
        install(CallLogging) {
            level = Level.INFO
            filter { call -> call.request.path().startsWith("/") }
        }

        install(ContentNegotiation) {
            gson {
            }
        }

        routing {
            route()
            get("/json/gson") {
                call.respond(mapOf("hello" to "world"))
            }
        }
        runBot()
    }.start(wait = true)
}

fun runBot() {
    Thread.UncaughtExceptionHandler { t, e -> LoggerFactory.getLogger("Main").error(e) }
    botProcess {
        config {
            name = configOf("botName")
            authToken = configOf("authToken")
        }

        jiraConfig {
            email = configOf("jiraEmail")
            password = configOf("jiraPassword")
            uri = configOf("jiraUri")
            jql = configOf("jiraProject")
        }

        command {
            command = "task"
            factory = JiraCommand.Factory()
        }
    }.start()
}

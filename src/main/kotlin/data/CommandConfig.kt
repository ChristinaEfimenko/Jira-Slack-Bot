package data

import command.BotCommand
import command.BotCommandFactory

data class CommandConfig(
    var command: String = "",
    var factory: BotCommandFactory<BotCommand>? = null
)

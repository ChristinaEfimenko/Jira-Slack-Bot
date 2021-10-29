package data

import com.ullink.slack.simpleslackapi.SlackAttachment

data class ResponseConfig(val message: String, val attachments: List<SlackAttachment>)

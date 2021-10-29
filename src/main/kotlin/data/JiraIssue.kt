package data

import com.atlassian.jira.rest.client.api.domain.Issue
import java.io.Serializable

data class JiraIssue(
    var key: String = "",
    var summary: String = "",
    var assignee: String? = null,
    var project: String? = null,
    var description: String? = null
) : Serializable {

    constructor(issue: Issue) : this(
        issue.key,
        issue.summary,
        issue.assignee?.displayName,
        issue.project?.name,
        issue.description
    )
}

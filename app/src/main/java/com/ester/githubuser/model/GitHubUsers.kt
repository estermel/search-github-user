package com.ester.githubuser.model

import com.google.gson.annotations.SerializedName


class GitHubUsers {
    @SerializedName("total_count")
    var totalCount = 0
    @SerializedName("incomplete_results")
    var isIncompleteResults = false
    @SerializedName("items")
    var userList: List<User>? = null

    inner class User {
        @SerializedName("login")
        var login: String? = null
        @SerializedName("id")
        var id = 0.0
        @SerializedName("node_id")
        var nodeId: String? = null
        @SerializedName("avatar_url")
        var avatarUrl: String? = null
        @SerializedName("gravatar_id")
        var gravatarId: String? = null
        @SerializedName("url")
        var url: String? = null
        @SerializedName("html_url")
        var htmlUrl: String? = null
        @SerializedName("followers_url")
        var followersUrl: String? = null
        @SerializedName("following_url")
        var followingUrl: String? = null
        @SerializedName("gists_url")
        var gistsUrl: String? = null
        @SerializedName("starred_url")
        var starredUrl: String? = null
        @SerializedName("subscriptions_url")
        var subscriptionsUrl: String? = null
        @SerializedName("organizations_url")
        var organizationsUrl: String? = null
        @SerializedName("repos_url")
        var reposUrl: String? = null
        @SerializedName("events_url")
        var eventsUrl: String? = null
        @SerializedName("received_events_url")
        var receivedEventsUrl: String? = null
        @SerializedName("type")
        var type: String? = null
        @SerializedName("site_admin")
        var siteAdmin: String? = null
        @SerializedName("score")
        var score = 0f

    }

}

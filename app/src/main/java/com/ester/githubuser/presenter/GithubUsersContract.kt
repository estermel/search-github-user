package com.ester.githubuser.presenter

import com.ester.githubuser.model.GitHubUsers

interface GitHubUsersContract {
    interface View {
        fun onDataLoaded(users: GitHubUsers?)
        fun onFailed(message: String)
    }

    interface Presenter {
        fun onDestroy()
        fun getGitHubUsers(query: String?, page: Int, perPage: Int)
    }
}

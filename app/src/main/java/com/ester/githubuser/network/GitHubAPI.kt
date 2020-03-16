package com.ester.githubuser.network

import com.ester.githubuser.model.GitHubUsers
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubAPI {
    @GET("/search/users")
    fun getUsers(
        @Query("q") query: String?, @Query("page") page: Int, @Query("per_page") perPage: Int
    ): Observable<GitHubUsers>
}

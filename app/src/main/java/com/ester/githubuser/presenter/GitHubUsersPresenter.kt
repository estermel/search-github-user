package com.ester.githubuser.presenter

import android.annotation.SuppressLint
import com.ester.githubuser.model.GitHubUsers
import com.ester.githubuser.network.ClientAPI
import com.ester.githubuser.network.GitHubAPI
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class GitHubUsersPresenter(private var view: GitHubUsersContract.View) :
    GitHubUsersContract.Presenter {
    private lateinit var disposables: CompositeDisposable

    fun onFinished(data: GitHubUsers?) {
        view.onDataLoaded(data)
    }

    fun onFailed(message: String?) {
        message?.let {
            view.onFailed(message)
        }
    }

    fun onHttpExceptionCaught(code: Int) {
        when (code) {
            401 -> view.onFailed("Error 401 Unauthorized")
            403 -> view.onFailed("Error 403 Forbidden")
            404 -> view.onFailed("Error 404 Service Not Found")
            405 -> view.onFailed("Error 405 Method not allowed")
            412 -> view.onFailed("Error 412 Invalid Input")
            429 -> view.onFailed("Error 429 Too Many Request")
            500 -> view.onFailed("Error 500 Internal Server Error")
            503 -> view.onFailed("Error 503 Service Unavailable")
            400 -> view.onFailed("Error 400 Bad Request")
            else -> view.onFailed("Error 400 Bad Request")
        }
    }

    override fun onDestroy() {
        disposables.clear()
    }

    @SuppressLint("CheckResult")
    override fun getGitHubUsers(query: String?, page: Int, perPage: Int) {
        disposables = CompositeDisposable()

        val service = ClientAPI.instance.create(GitHubAPI::class.java)


        val observable = service.getUsers(query, page, perPage)

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        observable.subscribe(object : Observer<GitHubUsers> {
            override fun onComplete() {
                onDestroy()
            }

            override fun onSubscribe(d: Disposable) {
                disposables.add(d)
            }

            override fun onNext(data: GitHubUsers) {
                onFinished(data)
            }

            override fun onError(e: Throwable) {
                if (e is HttpException) onHttpExceptionCaught(e.code())
                else onFailed(e.localizedMessage)
            }
        })
    }
}

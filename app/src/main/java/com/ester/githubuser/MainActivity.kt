package com.ester.githubuser

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ester.githubuser.model.GitHubUsers
import com.ester.githubuser.presenter.GitHubUsersContract
import com.ester.githubuser.presenter.GitHubUsersPresenter
import com.ester.githubuser.view.GitHubUsersAdapter
import com.ester.githubuser.view.UserNotFoundView


class MainActivity : AppCompatActivity(), GitHubUsersContract.View {
    private lateinit var presenter: GitHubUsersPresenter
    private lateinit var viewUserNotFound: UserNotFoundView
    private var username: String? = null
    private var rvUser: RecyclerView? = null
    private var isLoading = false
    private var isLastPage = false
    private var page = 1
    private var userList: MutableList<GitHubUsers.User>? = mutableListOf()
    private var adapter: GitHubUsersAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = GitHubUsersPresenter(this)
        initView()
        initAdapter()
    }

    private fun initView() {
        val svGitHubUser: SearchView = findViewById(R.id.sv_github_user)
        rvUser = findViewById(R.id.rv_github_users)
        viewUserNotFound = findViewById(R.id.view_user_not_found)

        svGitHubUser.isIconified = true
        svGitHubUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                username = s.trim { it <= ' ' }
                presenter.getGitHubUsers(username, page, PAGE_SIZE)
                hideKeyboard()
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })
    }

    private fun initAdapter() {
        adapter = GitHubUsersAdapter(userList)
        rvUser?.adapter = adapter
        setRvGitHubUsers(rvUser)
    }

    override fun onDataLoaded(users: GitHubUsers?) {
        if (users?.userList == null && userList!!.size == 0) {
            setErrorView("User Not Found")
        } else {
            setUserView(users)
        }
    }

    private fun setUserView(users: GitHubUsers?) {
        runOnUiThread {
            isLoading = false
            viewUserNotFound.visibility = View.GONE
            rvUser?.visibility = View.VISIBLE
            if (users?.userList != null) {
                userList?.addAll(users.userList!!)

                adapter!!.notifyDataSetChanged()

                users.userList?.let {
                    if (it.size < PAGE_SIZE) {
                        isLastPage = true
                    }
                }
            }
        }
    }

    private fun setErrorView(message: String) {
        runOnUiThread {
            rvUser?.visibility = View.GONE
            viewUserNotFound.visibility = View.VISIBLE
            viewUserNotFound.setErrorMessage(message)
            hideKeyboard()
            isLastPage = true
        }
    }

    private fun hideKeyboard() {
        val imm =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    override fun onFailed(message: String) {
        setErrorView(message)
    }

    private fun loadMore() {
        isLoading = true
        page += 1
        presenter.getGitHubUsers(username, page, PAGE_SIZE)
    }

    private fun setRvGitHubUsers(rvUsers: RecyclerView?) {
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvUsers?.let {
            rvUsers.layoutManager = layoutManager

            rvUsers.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    if (!isLoading && !isLastPage) {
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE
                        ) {
                            loadMore()
                        }
                    }
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    companion object {
        private const val PAGE_SIZE = 30
    }
}

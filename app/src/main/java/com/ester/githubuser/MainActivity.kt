package com.ester.githubuser

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ester.githubuser.model.GitHubUsers
import com.ester.githubuser.presenter.GitHubUsersContract
import com.ester.githubuser.presenter.GitHubUsersPresenter
import com.ester.githubuser.view.GitHubUsersAdapter
import com.ester.githubuser.view.UserNotFoundView
import timber.log.Timber
import java.io.IOException
import java.util.*


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
                validateQuery(s.trim { it <= ' ' })
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        validateQuery(s.trim { it <= ' ' })
                    }
                }, 2000)
                return false
            }
        })
        svGitHubUser.setOnCloseListener {
            adapter?.clear()
            false
        }
    }

    private fun initAdapter() {
        adapter = GitHubUsersAdapter(userList)
        rvUser?.adapter = adapter
        setRvGitHubUsers(rvUser)
    }

    /**
     * execute getGitHubUsers when query is valid
     * and device is connected to internet
     *
     * @param query -> query given
     */
    private fun validateQuery(query: String) {
        if (query.isNotEmpty()) {
            try {
                if (isConnected) {
                    username = query
                    presenter.getGitHubUsers(username, page, PAGE_SIZE)
                    hideKeyboard()
                } else {
                    setErrorView("Internet Not Available")
                }
            } catch (ie: InterruptedException) {
                Timber.e(MainActivity::class.java.simpleName, ie.localizedMessage)
                Thread.currentThread().interrupt()
            } catch (ioe: IOException) {
                Timber.e(MainActivity::class.java.simpleName, ioe.localizedMessage)
            }
        } else {
            adapter?.clear()
            Toast.makeText(this, "No username inserted", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Check internet connection by pinging to google.com
     *
     * @return true if device is connected to internet, false if not connected
     * @throws InterruptedException when something interrupted
     * @throws IOException          when command corrupted or not found
     */
    private val isConnected: Boolean
        get() {
            val command = "ping -c 1 google.com"
            return Runtime.getRuntime().exec(command).waitFor() == 0
        }

    override fun onDataLoaded(users: GitHubUsers?) {
        if (users?.userList == null && userList!!.size == 0) {
            setErrorView("User Not Found")
        } else {
            setUserView(users)
        }
    }

    private fun setUserView(users: GitHubUsers?) {
        isLoading = false
        viewUserNotFound.visibility = View.GONE
        rvUser?.visibility = View.VISIBLE
        if (users?.userList != null) {
            userList?.addAll(users.userList!!)
            adapter?.notifyDataSetChanged()
            users.userList?.let {
                if (it.size < PAGE_SIZE) {
                    isLastPage = true
                }
            }
        }
    }

    private fun setErrorView(message: String) {
        rvUser?.visibility = View.GONE
        viewUserNotFound.visibility = View.VISIBLE
        viewUserNotFound.setErrorMessage(message)
        hideKeyboard()
        isLastPage = true
    }

    private fun hideKeyboard() {
        val imm =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    override fun onFailed(message: String) {
        Timber.e(MainActivity::class.java.simpleName, message)
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

package com.ester.githubuser.view

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.ester.githubuser.R
import com.ester.githubuser.model.GitHubUsers


class GitHubUsersAdapter(private val users: MutableList<GitHubUsers.User>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    fun clear() {
        val size = users!!.size
        users.clear()
        notifyItemRangeRemoved(0, size)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_github_user, viewGroup, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        populateUserRows(viewHolder as UserViewHolder, position)
    }

    class UserViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val ivUserAvatar: ImageView = itemView.findViewById(R.id.iv_user_avatar)
        val tvUserLogin: TextView = itemView.findViewById(R.id.tv_user_login)
    }

    private fun populateUserRows(
        userViewHolder: UserViewHolder,
        position: Int
    ) {
        Glide.with(userViewHolder.ivUserAvatar.context)
            .load(users!![position].avatarUrl)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .apply(
                RequestOptions().placeholder(R.drawable.ic_sentiment_satisfied_black_24dp).error(
                    R.drawable.ic_sentiment_dissatisfied_black_24dp
                )
            )
            .into(userViewHolder.ivUserAvatar)
        userViewHolder.tvUserLogin.text = users[position].login
    }

    override fun getItemCount(): Int = users?.size ?: 0
}


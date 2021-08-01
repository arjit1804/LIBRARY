package com.example.library.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.library.R
import com.example.library.model.Comments

class CommentsAdapterPublic(val context: Context, private val list:ArrayList<Comments>): RecyclerView.Adapter<CommentsAdapterPublic.CommentsViewHolderPublic>() {

    class CommentsViewHolderPublic(itemView: View): RecyclerView.ViewHolder(itemView) {
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val postTitle: TextView = itemView.findViewById(R.id.postTitle)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentsViewHolderPublic {
        return CommentsViewHolderPublic(
            LayoutInflater.from(parent.context).inflate(R.layout.comments_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CommentsViewHolderPublic, position: Int) {
        val commentList = list[position]
        holder.userName.text = commentList.displayName
        holder.postTitle.text =commentList.postTitle
        Glide.with(holder.userImage.context).load(commentList.imgUrl).circleCrop().into(holder.userImage)
    }

    override fun getItemCount(): Int {
       return list.size
    }
}
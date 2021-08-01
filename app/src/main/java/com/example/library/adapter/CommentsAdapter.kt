package com.example.library.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.library.R
import com.example.library.model.Comments
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CommentsAdapter(options: FirestoreRecyclerOptions<Comments>) :
    FirestoreRecyclerAdapter<Comments, CommentsAdapter.CommentsViewHolder>(options) {

    class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val postTitle:TextView = itemView.findViewById(R.id.postTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.comments_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int, model: Comments) {
        holder.userName.text = model.displayName
        holder.postTitle.text =model.postTitle
        Glide.with(holder.userImage.context).load(model.imgUrl).circleCrop().into(holder.userImage)
    }
}
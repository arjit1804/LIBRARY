package com.example.library.dao

import com.example.library.model.Comments
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CommentsDao {
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

   // val usersCollection = db.collection("users")

    fun addComment(text: String?, bookId: String?){
        val currentUser = auth.currentUser!!.uid
        val userImage = auth.currentUser!!.photoUrl.toString()
        val userName = auth.currentUser!!.displayName
        val timestamp = System.currentTimeMillis()

        val postComment = Comments(userImage,text.toString(), userName.toString(),currentUser,
            bookId.toString(),timestamp
        )

        //making subCollections for the comments of the users
       // usersCollection.document(currentUser).collection("comments").document().set(postComment)
        db.collection("comments").document().set(postComment)
    }
}
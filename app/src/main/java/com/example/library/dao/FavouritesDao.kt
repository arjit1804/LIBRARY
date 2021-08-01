package com.example.library.dao

import android.content.Context
import android.widget.Toast
import com.example.library.model.FavBook
import com.example.library.model.Users
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FavouritesDao {
    private val db = FirebaseFirestore.getInstance()
    private val favouritesCollection =db.collection("favourites")
    private val users = db.collection("users")
    private val auth = Firebase.auth

    fun addToFav(context: Context, bookImage:String, bookTitle:String, bookPrice:String, bookAuthor:String, bookId:String){
        GlobalScope.launch {
            val user = auth.currentUser!!.uid
            val userDetails = getUserById(user).await().toObject(Users::class.java)

            val bookFavDetails = FavBook(bookImage, bookTitle, bookAuthor, bookPrice, bookId, user)
            favouritesCollection.document().set(bookFavDetails).addOnSuccessListener {
                Toast.makeText(context, "saved successfully", Toast.LENGTH_SHORT).show()

                val isFav = userDetails!!.favourites.contains(bookId)

                if (!isFav){
                    userDetails.favourites.add(bookId)
                }
                users.document(user).set(userDetails)

            }
        }
    }

    private fun getUserById(user:String): Task<DocumentSnapshot> {
        return users.document(user).get()
    }

    fun removeFromFav(context: Context, bookId:String){
        GlobalScope.launch {
            val user = auth.currentUser!!.uid
            val userDetails = getUserById(user).await().toObject(Users::class.java)

            favouritesCollection.whereEqualTo("bookId",bookId).get()
                .addOnSuccessListener {
                    for (documents in it){
                        favouritesCollection.document(documents.id).delete()
                    }
                    Toast.makeText(context, "removed successfully", Toast.LENGTH_SHORT).show()

                    val isFav = userDetails!!.favourites.contains(bookId)

                    if (isFav){
                        userDetails.favourites.remove(bookId)
                    }
                    users.document(user).set(userDetails)
                }

        }
    }
}
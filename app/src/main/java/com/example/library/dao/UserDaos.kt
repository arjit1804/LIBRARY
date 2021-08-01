package com.example.library.dao

import com.example.library.model.Users
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDaos {

    private val db = FirebaseFirestore.getInstance()
    private val userCollections = db.collection("users")

    fun addUser(users: Users?){
        users?.let {
            GlobalScope.launch (Dispatchers.IO){
                userCollections.document(users.id).set(it)
            }
        }
    }
}
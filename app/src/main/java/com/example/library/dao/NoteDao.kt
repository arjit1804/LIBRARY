package com.example.library.dao

import com.example.library.model.Notes
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NoteDao {

    private val db = FirebaseFirestore.getInstance()
    private val noteCollection = db.collection("notes")
    private val auth = Firebase.auth

    fun addNote(note : String, bookId:String){

        GlobalScope.launch (Dispatchers.IO) {
            val currentUser = auth.currentUser!!.uid
            val createNotes = Notes(note,bookId,currentUser)

            noteCollection.document(bookId).set(createNotes)
        }

    }

}
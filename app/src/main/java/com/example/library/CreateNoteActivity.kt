package com.example.library

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.library.dao.NoteDao
import kotlinx.android.synthetic.main.activity_create_note.*

class CreateNoteActivity : AppCompatActivity() {

    private val noteDao = NoteDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        edtNote.setText(intent.getStringExtra("note"))

        btnSaveNote.setOnClickListener {
            if (intent != null){
                val bookId = intent.getStringExtra("bookId")
                val note = edtNote.text.toString()
                noteDao.addNote(note, bookId.toString())

                finish()
            }
        }
    }
}
package com.example.library

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_genre.*
import kotlinx.android.synthetic.main.activity_sign_in.*

class GenreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_genre)

        searchByName.setOnClickListener{
            val intent = Intent(this,SearchActivity::class.java)
            startActivity(intent)
        }
        onGenreClick()
    }

    private fun onGenreClick(){
        btnHealth.setOnClickListener {
            val intent = Intent(this,BooksActivity::class.java)
            intent.putExtra("genre",btnHealth.text.toString())
            startActivity(intent)
        }
        btnHistory.setOnClickListener {
            val intent = Intent(this,BooksActivity::class.java)
            intent.putExtra("genre",btnHistory.text.toString())
            startActivity(intent)
        }
        btnHorror.setOnClickListener {
            val intent = Intent(this,BooksActivity::class.java)
            intent.putExtra("genre",btnHorror.text.toString())
            startActivity(intent)
        }
        btnLove.setOnClickListener {
            val intent = Intent(this,BooksActivity::class.java)
            intent.putExtra("genre",btnLove.text.toString())
            startActivity(intent)
        }
        btnMarvel.setOnClickListener {
            val intent = Intent(this,BooksActivity::class.java)
            intent.putExtra("genre",btnMarvel.text.toString())
            startActivity(intent)
        }
        btnMotivation.setOnClickListener {
            val intent = Intent(this,BooksActivity::class.java)
            intent.putExtra("genre",btnMotivation.text.toString())
            startActivity(intent)
        }
        btnScience.setOnClickListener {
            val intent = Intent(this,BooksActivity::class.java)
            intent.putExtra("genre",btnScience.text.toString())
            startActivity(intent)
        }
        btnSports.setOnClickListener {
            val intent = Intent(this,BooksActivity::class.java)
            intent.putExtra("genre",btnSports.text.toString())
            startActivity(intent)
        }
        btnTechnology.setOnClickListener {
            val intent = Intent(this,BooksActivity::class.java)
            intent.putExtra("genre",btnTechnology.text.toString())
            startActivity(intent)
        }
        btnWildLife.setOnClickListener {
            val intent = Intent(this,BooksActivity::class.java)
            intent.putExtra("genre",btnWildLife.text.toString())
            startActivity(intent)
        }
    }
}
package com.example.library

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.library.adapter.FavouriteAdapter
import com.example.library.model.Favourite
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.comments_row.*

class FavouritesActivity : AppCompatActivity() {

    lateinit var recyclerFav: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var favouriteAdapter: FavouriteAdapter

    var favList = arrayListOf<Favourite>()
    var id:String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        recyclerFav = findViewById(R.id.recyclerFav)
        layoutManager = GridLayoutManager(this@FavouritesActivity,2)

        readData()
    }

    private fun readData(){
        val db = FirebaseFirestore.getInstance()
        val auth = Firebase.auth
        val userId = auth.currentUser!!.uid
        if (intent!=null){
            id = intent.getStringExtra("id")
        }
        db.collection("favourites").whereEqualTo("uid",userId)
            .get()
            .addOnCompleteListener{
                for (document in it.result!!){
                    Log.d("document","${document.data}")
                    val favDetails = Favourite(
                        id.toString(),
                        document.data.getValue("bookTitle") as String,
                        document.data.getValue("bookImage") as String,
                        document.data.getValue("bookAuthor") as String,
                        document.data.getValue("bookPrice") as String
                    )

                    favList.add(favDetails)
                    favouriteAdapter = FavouriteAdapter(this,favList)
                    recyclerFav.adapter = favouriteAdapter
                    recyclerFav.layoutManager = layoutManager
                }
            }
    }

    override fun onBackPressed() {
        val intent = Intent(this,SearchActivity::class.java)
        startActivity(intent)
        finish()
    }
}
package com.example.library

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.library.dao.FavouritesDao
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_description.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DescriptionActivity : AppCompatActivity() {
    var id:String?=""
    var link:String?=""
    var authorName:String?=""
    var listPrice: String?=""
    var title:String?=""

    private val auth = Firebase.auth
    val favouritesDao = FavouritesDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "About Book"

        if (auth.currentUser == null){
            llNote.visibility = View.GONE
            fav_Rem_Buttons.visibility = View.GONE
        }
        else{
            llNote.visibility = View.VISIBLE
            fav_Rem_Buttons.visibility = View.VISIBLE
        }


        if (intent != null){
            id = intent.getStringExtra("id")

            btnCommentsSection.setOnClickListener {
                val intent = Intent(this,CommentsActivity::class.java)
                intent.putExtra("bookId",id)
                startActivity(intent)

            }

            btnMakeNote.setOnClickListener {
                val intent = Intent(this,CreateNoteActivity::class.java)
                intent.putExtra("bookId",id)
                intent.putExtra("note",txtUserNote.text)
                startActivity(intent)
            }

            btnAddToFav.setOnClickListener {
                favBook(id)
            }

            btnRemoveFromFav.setOnClickListener {
                favouritesDao.removeFromFav(this, id.toString())
                btnRemoveFromFav.visibility = View.GONE
                btnAddToFav.visibility = View.VISIBLE
            }

            bookDescription(id)
        }
    }

    private fun bookDescription(id: String?) {
        GlobalScope.launch (Dispatchers.IO) {
            val queue = Volley.newRequestQueue(this@DescriptionActivity)
            val url = "https://www.googleapis.com/books/v1/volumes?q=${id}"

            val jsonRequest = object : JsonObjectRequest(Request.Method.GET,url,null,
                Response.Listener {
                    //Responses are handled here

                    try {

                        val itemsArray = it.getJSONArray("items")

                        for (i in 0 until itemsArray.length()){

                            val bookObject = itemsArray.getJSONObject(i)

                            val volumeInfo = bookObject.optJSONObject("volumeInfo")

                            //for title
                            txtBookTitleDesc.text = volumeInfo.getString("title")
                            title=volumeInfo.getString("title")

                            //for image loading
                            val imageLinks = volumeInfo.optJSONObject("imageLinks")
                            var imageUrl:String?=""

                            if (imageLinks != null && imageLinks.has("thumbnail")){
                                imageUrl = imageLinks.getString("thumbnail")
                                link = "https:${imageUrl?.substring(5,imageUrl.length)}"
                                Picasso.get().load(link).error(R.drawable.ic_launcher_background).into(imgBookDesc)

                            }else{
                                link="https://cdn1.iconfinder.com/data/icons/get-me-home/512/square_blank_check_empty-128.png"
                                Picasso.get().load(link).error(R.drawable.ic_launcher_background).into(imgBookDesc)
                            }

                            //for displaying author name
                            val author = volumeInfo.optJSONArray("authors")

                            if (author != null){
                                if (author.length() >= 1){
                                    authorName ="By- " + author.getString(0)
                                    txtAuthorDesc.text = authorName
                                }
                            }else{
                                authorName="No Author"
                                txtAuthorDesc.text = authorName
                            }

                            //for displaying the book price
                            val saleInfo = bookObject.optJSONObject("saleInfo")
                            var amount: Double?

                            if (saleInfo != null && saleInfo.has("listPrice")){
                                amount = saleInfo.getJSONObject("listPrice").getDouble("amount")
                                listPrice = "$amount USD"
                                txtPriceDesc.text = listPrice
                            }else{
                                listPrice = "NOT_FOR_SALE"
                                txtPriceDesc.text = listPrice
                            }

                            //for description of the book
                            var description:String?
                            if (volumeInfo != null && volumeInfo.has("description")){
                                description = volumeInfo.getString("description")
                                txtBookDescription.text = description
                            }else{
                                description = "NO_DESCRIPTION_FOR_THIS_BOOK"
                                txtBookDescription.text = description
                            }

                        }


                    }catch (e:Exception){

                        Toast.makeText(this@DescriptionActivity, e.message, Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {
                    //Errors are handled here

                    Toast.makeText(this@DescriptionActivity, it.message, Toast.LENGTH_SHORT).show()
                })
            {

            }

            queue.add(jsonRequest)

        }
    }

    private fun favBook(bookId: String?) {
        GlobalScope.launch(Dispatchers.IO) {
            val queue = Volley.newRequestQueue(this@DescriptionActivity)
            val url = "https://www.googleapis.com/books/v1/volumes?q=${bookId}"

            var bookImage: String
            var bookTitle: String
            var bookPrice: String
            var bookAuthor: String= ""

            val jsonRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener {
                    //Responses are handled here

                    try {

                        val itemsArray = it.getJSONArray("items")

                        for (i in 0 until itemsArray.length()) {

                            val bookObject = itemsArray.getJSONObject(i)

                            val volumeInfo = bookObject.optJSONObject("volumeInfo")

                            //for title
                            bookTitle = volumeInfo.getString("title")

                            //for image loading
                            val imageLinks = volumeInfo.optJSONObject("imageLinks")
                            var imageUrl: String? = ""

                            if (imageLinks != null && imageLinks.has("thumbnail")) {
                                imageUrl = imageLinks.getString("thumbnail")
                                bookImage = "https:${imageUrl?.substring(5, imageUrl.length)}"

                            } else {
                                bookImage =
                                    "https://cdn1.iconfinder.com/data/icons/get-me-home/512/square_blank_check_empty-128.png"
                            }

                            //for displaying author name
                            val author = volumeInfo.optJSONArray("authors")

                            if (author != null) {
                                if (author.length() >= 1) {
                                    bookAuthor = "By- " + author.getString(0)
                                }
                            } else {
                                bookAuthor = "No Author"
                            }

                            //for displaying the book price
                            val saleInfo = bookObject.optJSONObject("saleInfo")
                            var amount: Double?

                            if (saleInfo != null && saleInfo.has("listPrice")) {
                                amount = saleInfo.getJSONObject("listPrice").getDouble("amount")
                                bookPrice = "$amount USD"
                            } else {
                                bookPrice = "NOT_FOR_SALE"
                            }
                            favouritesDao.addToFav(
                                this@DescriptionActivity,
                                bookImage,
                                bookTitle,
                                bookPrice,
                                bookAuthor,
                                bookId.toString()
                            )
                            btnAddToFav.visibility = View.GONE
                            btnRemoveFromFav.visibility = View.VISIBLE
                        }

                    } catch (e: Exception) {

                        Toast.makeText(this@DescriptionActivity, e.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                }, Response.ErrorListener {
                    //Errors are handled here
                    Toast.makeText(this@DescriptionActivity, it.message, Toast.LENGTH_SHORT).show()
                }) {
            }

            queue.add(jsonRequest)

        }
    }

    private fun retrieveNote() {
            val id = intent.getStringExtra("id")
            val auth = Firebase.auth
            val user = auth.currentUser!!.uid

            GlobalScope.launch(Dispatchers.IO) {
                FirebaseFirestore.getInstance().collection("notes")
                    .whereEqualTo("uid", user)
                    .whereEqualTo("bookId", id).get()
                    .addOnCompleteListener {

                        for (documents in it.result!!) {
                            val noteText = documents.data.getValue("note")
                            txtUserNote.text = noteText.toString()
                        }
                    }
            }
        }

    private fun isFav() {
        val auth = Firebase.auth
        val currentUser = auth.currentUser!!.uid

        FirebaseFirestore.getInstance().collection("users")
            .document(currentUser).get()
            .addOnSuccessListener {
                if (it.exists()){
                    val id = it.get("favourites") as ArrayList<*>
                    val bookId = intent.getStringExtra("id")

                    val isFav = id.contains(bookId)

                    if (isFav){
                        btnAddToFav.visibility = View.GONE
                        btnRemoveFromFav.visibility = View.VISIBLE
                    }else{
                        btnAddToFav.visibility = View.VISIBLE
                        btnRemoveFromFav.visibility = View.GONE
                    }
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            isFav()
            retrieveNote()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val auth = Firebase.auth
        if (auth.currentUser != null ){
            menuInflater.inflate(R.menu.favourite,menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.btnFavourites){
            val intent = Intent(this,FavouritesActivity::class.java)
            intent.putExtra("id",id)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

}

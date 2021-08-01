package com.example.library

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.library.adapter.BooksAdapter
import com.example.library.model.Book
import kotlinx.android.synthetic.main.activity_books.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BooksActivity : AppCompatActivity() {

    lateinit var bookAdapter:BooksAdapter

    val bookList = arrayListOf<Book>()
    var genre: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books)

        if (intent != null ){
            genre = intent.getStringExtra("genre")
            bookRecycler(genre)
        }
    }

    private fun bookRecycler(genre: String?) {
        GlobalScope.launch (Dispatchers.IO){
            val queue = Volley.newRequestQueue(this@BooksActivity)
            val url = "https://www.googleapis.com/books/v1/volumes?q=${"$genre"}"

            val jsonRequest = object : JsonObjectRequest(Request.Method.GET,url,null,
                Response.Listener {
                    //Responses are handled here

                    try {

                        val itemsArray = it.getJSONArray("items")

                        for (i in 0 until itemsArray.length()){

                            val bookObject = itemsArray.getJSONObject(i)

                            val volumeInfo = bookObject.getJSONObject("volumeInfo")

                            //for image loading
                            val imageLinks = volumeInfo.optJSONObject("imageLinks")
                            var imageUrl:String?=""
                            var link:String?=""
                            if (imageLinks != null && imageLinks.has("thumbnail")){
                                imageUrl = imageLinks.getString("thumbnail")
                                link = "https:${imageUrl?.substring(5,imageUrl.length)}"
                            }else{
                                link="https://cdn1.iconfinder.com/data/icons/get-me-home/512/square_blank_check_empty-128.png"
                            }

                            //for displaying author name
                            val author = volumeInfo.optJSONArray("authors")
                            var authorName:String?=""
                            if (author != null){
                                if (author.length() >= 1){
                                    authorName ="By- " + author.getString(0)
                                }
                            }else{
                                authorName="No Author"
                            }

                            //for displaying the book price
                            val saleInfo = bookObject.optJSONObject("saleInfo")
                            var amount: Double?
                            var listPrice: String?
                            if (saleInfo != null && saleInfo.has("listPrice")){
                                amount = saleInfo.getJSONObject("listPrice").getDouble("amount")
                                listPrice = "$amount USD"
                            }else{
                                listPrice = "NOT_FOR_SALE"
                            }

                            //for book id
                            val id = bookObject.getString("id")

                            val bookDetails = Book(
                                link,
                                volumeInfo.getString("title"),
                                authorName.toString(),
                                listPrice,
                                id
                            )
                            bookList.add(bookDetails)
                            bookAdapter = BooksAdapter(this@BooksActivity,bookList)
                            genreBookRecyclerview.adapter = bookAdapter
                            genreBookRecyclerview.layoutManager = LinearLayoutManager(this@BooksActivity)
                        }

                    }catch (e:Exception){

                        Toast.makeText(this@BooksActivity, e.message, Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {
                    //Errors are handled here
                    Toast.makeText(this@BooksActivity, it.message, Toast.LENGTH_SHORT).show()
                })
            {

            }

            queue.add(jsonRequest)
        }

    }
}
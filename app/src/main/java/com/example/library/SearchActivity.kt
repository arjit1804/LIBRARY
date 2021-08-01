package com.example.library

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.library.adapter.BooksAdapter
import com.example.library.model.Book
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var bookAdapter: BooksAdapter
    private lateinit var edtSearchBook: EditText
    private lateinit var btnSearch: Button

    val bookList = arrayListOf<Book>()
    val auth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)



        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this)

        edtSearchBook = findViewById(R.id.edtSearchBook)
        btnSearch = findViewById(R.id.btnSearch)

        onActivityStart("Marvel")

        btnSearch.setOnClickListener {
            onClickBtnSearch(edtSearchBook.text)

        }

        if (auth.currentUser != null){

            btmNavigationView.visibility = View.VISIBLE
            btmNavigationView.setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.btnFavBottom -> {
                        val intent = Intent(this,FavouritesActivity::class.java)
                        startActivity(intent)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.btnSettings -> {
                        settingsClick()
                        return@setOnNavigationItemSelectedListener true
                    }
                }
                false
            }
        }
        else{
            btmNavigationView.visibility = View.GONE
        }



    }

    private fun settingsClick() {
        val dialog = AlertDialog.Builder(this@SearchActivity)
        dialog.setTitle("Delete")
        dialog.setMessage("Log out?")
        dialog.setPositiveButton("Yes"){ _, _ ->

            val auth = Firebase.auth
            auth.signOut()
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)

        }
        dialog.setNegativeButton("No"){ _, _ ->
            //
        }
        dialog.create()
        dialog.show()
    }

    private fun onClickBtnSearch(text: Editable) {

        if (text.isEmpty()){
            Toast.makeText(this, "please enter the search bar", Toast.LENGTH_SHORT).show()
        }
        else{
            val queue = Volley.newRequestQueue(this)
            val url = "https://www.googleapis.com/books/v1/volumes?q=${text}"

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
                            bookAdapter = BooksAdapter(this,bookList)
                            recyclerView.adapter = bookAdapter
                            recyclerView.layoutManager = layoutManager
                            bookAdapter.notifyDataSetChanged()
                        }

                    }catch (e:Exception){

                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {
                    //Errors are handled here

                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                })
            {
            }
            queue.add(jsonRequest)
        }
        bookList.clear()
    }

    private fun onActivityStart(text: String) {

        if (text.isEmpty()){
            Toast.makeText(this, "please enter the search bar", Toast.LENGTH_SHORT).show()
        }
        else{
            val queue = Volley.newRequestQueue(this)
            val url = "https://www.googleapis.com/books/v1/volumes?q=${text}"

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
                            bookAdapter = BooksAdapter(this,bookList)
                            recyclerView.adapter = bookAdapter
                            recyclerView.layoutManager = layoutManager
                            bookAdapter.notifyDataSetChanged()
                        }

                    }catch (e:Exception){

                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {
                    //Errors are handled here

                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                })
            {
            }
            queue.add(jsonRequest)
        }
        bookList.clear()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        val auth = Firebase.auth
        if (auth.currentUser != null){
            finishAffinity()
        }
        else {
            startActivity(Intent(this,SignInActivity::class.java))
            finish()
        }

    }
}
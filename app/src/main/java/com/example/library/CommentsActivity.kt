package com.example.library

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.library.adapter.CommentsAdapter
import com.example.library.adapter.CommentsAdapterPublic
import com.example.library.adapter.FavouriteAdapter
import com.example.library.dao.CommentsDao
import com.example.library.model.Comments
import com.example.library.model.Favourite
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : AppCompatActivity() {

    lateinit var commentPublicAdapter: CommentsAdapterPublic
    var commentPublicList = arrayListOf<Comments>()

    private lateinit var adapter:CommentsAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var bookId:String? =""
    private val commentsDao=CommentsDao()

    private var auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Comments"

        if (auth.currentUser == null){
            btnPostComment.visibility = View.GONE
            edtCommentText.visibility = View.GONE

            setUpPublicRecyclerView()

        }else{
            btnPostComment.visibility = View.VISIBLE
            edtCommentText.visibility = View.VISIBLE
        }

        btnPostComment.setOnClickListener {
            val comment = edtCommentText.text.toString()
            if(comment.isNotEmpty() && intent != null){

                bookId = intent.getStringExtra("bookId")
                commentsDao.addComment(comment,bookId)
                edtCommentText.text.clear()
                setupRecyclerView()
                adapter.startListening()
            }
        }
        setupRecyclerView()
    }

    private fun setUpPublicRecyclerView(){
        val db = FirebaseFirestore.getInstance()
        val bookId = intent.getStringExtra("bookId")

        db.collection("comments").whereEqualTo("bookId",bookId)
            .get()
            .addOnCompleteListener{
                for (document in it.result!!){
                    Log.d("document","${document.data}")
                    val commentDetails = Comments(
                        document.data.getValue("imgUrl") as String,
                        document.data.getValue("postTitle") as String,
                        document.data.getValue("displayName") as String
                        //document.data.getValue("timeStamp").toString()
                    )

                    commentPublicList.add(commentDetails)
                    commentPublicAdapter = CommentsAdapterPublic(this,commentPublicList)
                    recyclerComment.adapter = commentPublicAdapter
                    recyclerComment.layoutManager = LinearLayoutManager(this)
                }
            }
    }

    private fun setupRecyclerView() {
                layoutManager = LinearLayoutManager(this@CommentsActivity)
                val db = FirebaseFirestore.getInstance().collection("comments")

                val bookId = intent.getStringExtra("bookId")
                val query = db.orderBy("timeStamp",Query.Direction.DESCENDING).whereEqualTo("bookId",bookId)

                val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Comments>().setQuery(query,Comments::class.java).build()
                adapter = CommentsAdapter(recyclerViewOptions)

                recyclerComment.adapter = adapter
                recyclerComment.layoutManager = layoutManager
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}
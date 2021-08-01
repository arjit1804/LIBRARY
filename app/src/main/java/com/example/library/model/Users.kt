package com.example.library.model

data class Users(
    val id:String="",
    val displayName:String = "",
    val imageUrl:String = "",
    val favourites:ArrayList<String> = ArrayList()
)
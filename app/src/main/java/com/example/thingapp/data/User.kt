package com.example.thingapp.data

data class User(
    val firstName: String,
    val lastName : String,
    val email : String,
    // User can add the profile image after the sign up operation, it will change whenever the user upload a new image
    var imagePath : String = ""
){
    constructor() : this("","","","")
}

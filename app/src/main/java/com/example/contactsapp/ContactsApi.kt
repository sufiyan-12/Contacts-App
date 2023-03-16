package com.example.contactsapp

import retrofit2.Call
import retrofit2.http.GET


interface ContactsApi {
    @get:GET("contacts")
    val contacts: Call<List<Contact?>?>?
}

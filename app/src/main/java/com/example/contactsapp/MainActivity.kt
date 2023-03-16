package com.example.contactsapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.people.v1.PeopleServiceScopes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class MainActivity : AppCompatActivity(), myContactsItemClicked{

    /* start of var */

    val APPLICATION_NAME = "My First Project"
    val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    val TOKENS_DIRECTORY_PATH = "https://oauth2.googleapis.com/token"

    val SCOPES = Arrays.asList(PeopleServiceScopes.CONTACTS_READONLY)
    val CREDENTIALS_FILE_PATH = "/credentials.json"

    /* end of var*/



    // Variable Required
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mGoogleApiClient: GoogleApiClient

    // constant Variables Required
    private val PERMISSION_CODE = 1001
    private val REQUEST_CODE = 9001
    private val TAG = "MainActivity"

    // Views used
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView

    // recyclerView Adapter
    private lateinit var adapter:ContactsAdapter

    // Contacts list to store contacts
    private val contactsList = ArrayList<MyContacts>()
    private val tempList = ArrayList<MyContacts>()

    // permissions array
    val permissionArray = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // view initialization
        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)

        // Permission Check
        if (!permissionGranted(permissionArray)) {
            Toast.makeText(this@MainActivity, "Please give permissions", Toast.LENGTH_SHORT).show()
            requestPermission(permissionArray)
        }

        // google sign when contacts permission granted
        if (permissionGranted(permissionArray)) {
            googleSignIn()

            // adapter initialization
            adapter = ContactsAdapter(this)
            recyclerView.adapter = adapter

            getContacts() // function to fetch user contacts

            searchView.clearFocus() // to ignore focus from searchView


            /* setOnQueryTextListener Ends Here */
            // Query listener on searchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                // fetch query when submit
                override fun onQueryTextSubmit(newText: String?): Boolean {
                    tempList.clear()
                    val searchText = newText!!.lowercase(Locale.getDefault())
                    if (searchText.isNotEmpty()) {
                        contactsList.forEach {
                            if (it.name.lowercase(Locale.getDefault()).contains(searchText)) {
                                tempList.add(it)
                            }
                        }
                        adapter.updateContacts(tempList)
                    } else {
                        tempList.clear()
                        adapter.updateContacts(contactsList)
                    }
                    return false
                }


                // fetch query when write text
                override fun onQueryTextChange(newText: String?): Boolean {
                    tempList.clear()
                    val searchText = newText!!.lowercase(Locale.getDefault())
                    if (searchText.isNotEmpty()) {
                        contactsList.forEach {
                            if (it.name.lowercase(Locale.getDefault()).contains(searchText)) {
                                tempList.add(it)
                            }
                        }
                        adapter.updateContacts(tempList)
                    } else {
                        tempList.clear()
                        adapter.updateContacts(contactsList)
                    }
                    return false
                }
            })
            /* setOnQueryTextListener Ends Here */
        }
    }

    // function to fetch contacts
    private fun getContacts() {
        val uri: Uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        Log.i(TAG, "Content Providers Data\n"+ cursor!!.count.toString())
        if(cursor.count > 0){
            while (cursor.moveToNext()){
                val contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                contactsList.add(MyContacts(contactName, contactNumber))
            }
        }
        // update items in recyclerView
        if(contactsList.isNotEmpty()) adapter.updateContacts(contactsList)
    }

    // utility function to request permission
    private fun requestPermission(permissionArray: Array<String>) {
        ActivityCompat.requestPermissions(this@MainActivity, permissionArray, PERMISSION_CODE)
    }

    // utility function to check permission
    private fun permissionGranted(permissionArray: Array<String>) : Boolean{
        for(permission in permissionArray){
            if(ContextCompat.checkSelfPermission(this@MainActivity, permission) != PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }

    // function used to sign in
    private fun googleSignIn() {
        // Create a new GoogleSignInOptions object with the required scopes
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, REQUEST_CODE)
    }

    // function to signed out google account
    private fun signOut() {
        mGoogleSignInClient.signOut()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // function for recyclerView item's click handling
    override fun onItemClick(item: MyContacts) {
        Toast.makeText(applicationContext, item.name+" is clicked!!", Toast.LENGTH_SHORT).show()
    }

}
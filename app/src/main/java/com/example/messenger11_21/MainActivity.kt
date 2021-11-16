package com.example.messenger11_21

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var contactList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var addBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().getReference()

        userList = ArrayList()
        contactList = ArrayList()

        adapter = UserAdapter(this, contactList)

        userRecyclerView = findViewById(R.id.userRecyclerView)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter
        val uid = mAuth.currentUser?.uid!!

        // Search for users of the app from Firebase
        db.child("user").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if (mAuth.currentUser?.uid != currentUser?.uid){
                        userList.add(currentUser!!)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        db.child("user").child(uid).child("contacts").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                contactList.clear()
                for (postSnapshot in snapshot.children) {
                    val contact = postSnapshot.getValue(User::class.java)
                    println(contact)
                    Toast.makeText(this@MainActivity, "Contact: $contact", Toast.LENGTH_SHORT).show()
                    contactList.add(contact!!)
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        addBtn = findViewById(R.id.addBtn)
        addBtn.setOnClickListener{
            val editContact = findViewById<EditText>(R.id.contact)
            val newContact = editContact.text.toString()
            db = FirebaseDatabase.getInstance().getReference()
            // Add a new contact if they exist in userList
            var verifiedContact : User
            for (user in userList) {
                if (user.name == newContact) {
                    verifiedContact = user
                    db.child("user").child(uid).child("contacts").child(newContact).setValue(verifiedContact)
                    editContact.text.clear()
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logout){
            mAuth.signOut()
            val intent = Intent(this, Login::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return true
    }
}
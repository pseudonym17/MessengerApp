package com.example.messenger11_21

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var loginBtn: Button
    private lateinit var signupBtn: Button
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        editEmail = findViewById(R.id.email)
        editPassword = findViewById(R.id.password)
        loginBtn = findViewById(R.id.loginbtn)
        signupBtn = findViewById(R.id.signupbtn)

        signupBtn.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener {
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()
            if (email == "") {
                Toast.makeText(this, "Email is Missing", Toast.LENGTH_SHORT).show()
            } else if (password == "") {
                Toast.makeText(this, "Password is Missing", Toast.LENGTH_SHORT).show()
            } else {
                login(email, password)
            }
        }

    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@Login, Contacts::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Incorrect Email or Password", Toast.LENGTH_SHORT).show()
                }

            }
    }
}
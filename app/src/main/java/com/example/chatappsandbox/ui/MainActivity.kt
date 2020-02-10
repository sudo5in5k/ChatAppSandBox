package com.example.chatappsandbox.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatappsandbox.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthAnonymousUpgradeException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val db = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        log_out.setOnClickListener {
            AuthUI.getInstance().signOut(this).addOnCompleteListener {
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        val uid = intent.getStringExtra("uid")
        val username = intent.getStringExtra("user_name")
        val email = intent.getStringExtra("email")
        // register
        if (uid != null) {
            db.getReference("users/${uid}").setValue(UserInfo(username, email))
        }

        ref.setOnClickListener {
            db.getReference("users/${uid}").addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    return
                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        Log.d("debug", "key: ${it.key}, value: ${it.value}")
                    }
                }
            })
        }
    }

    data class UserInfo(val username: String?, val email: String?)
}

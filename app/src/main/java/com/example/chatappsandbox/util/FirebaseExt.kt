package com.example.chatappsandbox.util

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine

suspend fun DatabaseReference.getValueWithCoroutine() {
    withContext(Dispatchers.IO) {
        suspendCoroutine<DataSnapshot> {
            addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        return
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        p0.children.forEach {
                            Log.d("debug", "key: ${it.key}, value: ${it.value}")
                        }
                    }
                }
            )
        }
    }
}
package com.example.chatappsandbox.util

import android.util.Log
import com.example.chatappsandbox.entity.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
fun DatabaseReference.fetchMessageArchiveWithFlow(): Flow<List<Message>> = callbackFlow {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val adapter = moshi.adapter(Message::class.java)
    val eventListener = addValueEventListener(
        object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                close(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val arrayList: ArrayList<Message> = arrayListOf()
                p0.children.forEach {
                    val result = runCatching { adapter.fromJsonValue(it.value) }
                    if (result.isSuccess) {
                        val json = result.getOrNull()
                        if (json != null) {
                            arrayList.add(json)
                        }
                    } else {
                        val error = result.exceptionOrNull() ?: "Undefined Error"
                        Log.d("debug", "$error")
                    }
                }
                sendBlocking(arrayList)
            }
        }
    )
    awaitClose {
        Log.d("debug", "remove event listener")
        removeEventListener(eventListener)
    }
}
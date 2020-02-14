package com.example.chatappsandbox.util

import com.example.chatappsandbox.entity.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Suppress("UNCHECKED_CAST")
suspend fun DatabaseReference.fetchMessageArchive(): List<Message> {
    return suspendCoroutine { continuation ->
        addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    return
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val arrayList: ArrayList<Message> = arrayListOf()
                    try {
                        p0.children.forEach {
                            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                            val adapter = moshi.adapter(Message::class.java)
                            val json = adapter.fromJsonValue(it.value)
                            if (json != null) {
                                arrayList.add(json)
                            }
                        }
                        continuation.resume(arrayList)
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
            }
        )
    }
}
package com.example.chatappsandbox.util

import com.example.chatappsandbox.entity.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Suppress("UNCHECKED_CAST")
suspend fun DatabaseReference.fetchMessageArchive(uid: String): ArrayList<Message> {
    return suspendCoroutine { continuation ->
        addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    return
                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        p0.children.forEach {
                            if (it.key == uid) {
                                continuation.resume(
                                    it.value as? ArrayList<Message> ?: arrayListOf()
                                )
                            }
                        }
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
            }
        )
    }
}
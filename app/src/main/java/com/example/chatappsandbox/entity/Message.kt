package com.example.chatappsandbox.entity

import com.google.firebase.database.IgnoreExtraProperties
import com.squareup.moshi.JsonClass
import java.io.Serializable

@IgnoreExtraProperties
@JsonClass(generateAdapter = true)
data class Message(val from: String, val text: String, val time: Long): Serializable
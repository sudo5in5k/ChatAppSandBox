package com.example.chatappsandbox.entity

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserInfo(val username: String?, val email: String?) {

    @Exclude
    fun toMap(): Map<String, String?> {
        return mapOf(
            "username" to username,
            "email" to email
        )
    }
}
package com.example.chatappsandbox.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("parseTime")
fun TextView.parseTime(time: Long) = apply {
    val parser = SimpleDateFormat("yyyyMMddHHmm", Locale.JAPAN)
    val date = parser.parse(time.toString()) ?: return@apply
    val formatter = SimpleDateFormat("MM/dd HH:mm", Locale.JAPAN)
    text = formatter.format(date)
}
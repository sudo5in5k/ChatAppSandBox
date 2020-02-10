package com.example.chatappsandbox.ui.login

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class LoginWorker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        val data = inputData.getString("FACEBOOK")
        if (data?.isNotEmpty() == true) {
            Log.d("debug", data)
            return Result.success()
        }
        //TODO hogehogehoge
        return Result.failure()
    }
}
package com.bekircaglar.bluchat


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

const val YOUR_ONESIGNAL_APP_ID = BuildConfig.ONESIGNAL_APP_ID
const val YOUR_REST_API_KEY = BuildConfig.REST_API_KEY

suspend fun sendNotificationToChannel(ids: List<String?>, message: String) {
    withContext(Dispatchers.IO) {
        val client = OkHttpClient()

        val json = JSONObject().apply {
            put("app_id", YOUR_ONESIGNAL_APP_ID)
            put("headings", JSONObject().apply {
                put("en", "Test Cihazı")
            })
            put("contents", JSONObject().apply {
                put("en", message)
            })
            put("include_aliases", JSONObject().put("external_id", JSONArray().apply {
                ids.forEach { userId ->
                    put(userId)
                }
            }))
            put("target_channel", "push")
        }

        val body: RequestBody =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("https://onesignal.com/api/v1/notifications")
            .post(body)
            .addHeader("Authorization", "Basic $YOUR_REST_API_KEY")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                println("Request failed with code: ${response.code}")
                println("Response body: ${response.body?.string()}")
                throw IOException("Unexpected code $response")
            }
        }
    }
}

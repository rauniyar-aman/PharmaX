package com.example.pharmax.repo

import android.os.Handler
import android.os.Looper
import com.example.pharmax.BuildConfig
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

data class KhaltiInitiateResult(
    val paymentUrl: String,
    val pidx: String
)

class KhaltiRepo {

    private val client = OkHttpClient()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun initiatePayment(
        amountNpr: Double,
        purchaseOrderId: String,
        purchaseOrderName: String,
        customerName: String,
        customerPhone: String,
        callback: (Boolean, KhaltiInitiateResult?, String) -> Unit
    ) {
        val body = JSONObject().apply {
            put("return_url", "https://pharmax.app/payment/callback/")
            put("website_url", "https://pharmax.app/")
            put("amount", Math.round(amountNpr * 100))
            put("purchase_order_id", purchaseOrderId)
            put("purchase_order_name", purchaseOrderName)
            put("customer_info", JSONObject().apply {
                put("name", customerName.ifBlank { "PharmaX Customer" })
                put("phone", customerPhone.ifBlank { "9800000000" })
            })
        }

        val request = Request.Builder()
            .url("${BuildConfig.KHALTI_BASE_URL}epayment/initiate/")
            .addHeader("Authorization", "Key ${BuildConfig.KHALTI_SECRET_KEY}")
            .addHeader("Content-Type", "application/json")
            .post(body.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mainHandler.post { callback(false, null, e.message ?: "Network error") }
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                val raw = response.body?.string().orEmpty()
                mainHandler.post {
                    if (response.isSuccessful) {
                        try {
                            val json = JSONObject(raw)
                            val paymentUrl = json.getString("payment_url")
                            val pidx = json.getString("pidx")
                            callback(true, KhaltiInitiateResult(paymentUrl, pidx), "Success")
                        } catch (e: Exception) {
                            callback(false, null, "Unexpected response from Khalti")
                        }
                    } else {
                        callback(false, null, "Khalti error: $raw")
                    }
                }
            }
        })
    }
}

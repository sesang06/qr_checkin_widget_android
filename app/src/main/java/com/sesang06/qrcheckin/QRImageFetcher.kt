package com.sesang06.qrcheckin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.jsoup.Jsoup


class QRImageFetcher(private val context: Context) {


    private val url = "https://nid.naver.com/login/privacyQR"

    fun fetch(success: (Bitmap) -> Unit, failure: () -> Unit ) {
        val cookie = android.webkit.CookieManager.getInstance().getCookie("https://nid.naver.com/login/privacyQR")

        val queue = Volley.newRequestQueue(context)
        val stringRequest = object: StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                val bitmap = parse(response)
                if (bitmap != null) {
                    success(bitmap)
                } else {
                    failure()
                }
            },
            Response.ErrorListener {
                failure()
            }

        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["cookie"] = cookie
                return params
            }
        }
        queue.add(stringRequest)
    }

    fun parse(text: String): Bitmap? {
        val doc = Jsoup.parse(text)
        val image = doc.getElementById("qrImage") ?: return null
        val src = image.attr("src");
        val prefix = "data:image/jpeg;base64, "
        val pureBase64 = src.replace(prefix, "")
        val decodedBytes: ByteArray =
            Base64.decode(pureBase64, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        return decodedByte
    }

}
package com.sesang06.qrcheckin

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.jsoup.Jsoup
import java.net.CookieHandler


class MainActivity : AppCompatActivity() {


    private lateinit var webview: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        webview = findViewById(R.id.web_view)
        webview.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                CookieManager.getInstance().flush()
            }
        }

        val setting = webview.settings
        setting.javaScriptEnabled = true
        setting.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부
        setting.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        setting.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        setting.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        setting.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        setting.setSupportZoom(false); // 화면 줌 허용 여부
        setting.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        setting.setDomStorageEnabled(true); // 로컬저장소 허용 여부
        webview.loadUrl("https://nid.naver.com/login/privacyQR")


    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
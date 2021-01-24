package com.apps.get

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.apps.get.databinding.ActivityBrowserBinding

class Browser : AppCompatActivity() {
    private lateinit var binding: ActivityBrowserBinding
    var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        receive url
        var `in` = intent
        url = `in`.getStringExtra("url")
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(applicationContext, "URL not found", Toast.LENGTH_SHORT).show()
            finish()
        }
        else{
//            open in webview
            binding.webView.webViewClient = MyWebViewClient(this)
            binding.webView.clearCache(true)
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.isHorizontalScrollBarEnabled = false
            binding.webView.loadUrl("$url")
        }
    }
    class MyWebViewClient internal constructor(private val activity: Activity) : WebViewClient() {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val url: String = request?.url.toString();
            view?.loadUrl(url)
            return true
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return true
        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            Toast.makeText(activity, "Got Error! $error", Toast.LENGTH_SHORT).show()
        }
    }
}
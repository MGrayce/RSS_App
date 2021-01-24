package com.apps.get

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.apps.get.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var link : String
//    using View Binding for view syncing
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        attaching  a test url for rss
        binding.link.setText("https://www.omnycontent.com/d/playlist/aaea4e69-af51-495e-afc9-a9760146922b/44bbf446-4627-4f83-a7fd-ab070007db11/72b96aa8-88bd-480a-87af-ab070007db36/podcast.rss")
        binding.getFeed.setOnClickListener {
            link = binding.link.text.toString()
//            check if string is empty
            if (TextUtils.isEmpty(link)){ }
            else{
//                validate
                try {
                    if (!link.startsWith("http://") && !link.startsWith("https://"))
                        link = "http://$link"
                        startActivity(Intent(this@MainActivity, RSSFeedActivity::class.java).putExtra("rssLink", link))
                } catch (e: Exception) {
                }
            }
        }
//        check for internet status
        val conMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo
        if (netInfo == null){
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.alert_message)
                    .setTitle(R.string.app_name)
                    .setCancelable(false)
                    .setPositiveButton("Close"
                    ) { _, _ -> finish() }
            val alert = builder.create()
            alert.show()
        }
        else{ }
    }
}
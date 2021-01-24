package com.apps.get

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.apps.get.databinding.ActivityHeadlineBinding
import java.util.*


class Headline : AppCompatActivity() ,TextToSpeech.OnInitListener{
//    variables
    private lateinit var binding: ActivityHeadlineBinding
    private var tts: TextToSpeech? = null
    private var url: String? = null
    private var title: String? = null
    private var desc: String? = null
    private var date: String? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeadlineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            super@Headline.onBackPressed()
        }
        var `in` = intent
        url = `in`.getStringExtra("url")
        title = `in`.getStringExtra("title")
        desc = `in`.getStringExtra("desc")
        date = `in`.getStringExtra("date")
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(applicationContext, "Invalid Link", Toast.LENGTH_SHORT).show()
            binding.voice!!.isEnabled = false
            binding.desc.visibility = View.GONE
        }
        else{
//            init TTS
            tts = TextToSpeech(this, this)
            binding.voice!!.isEnabled = false
            binding.desc.visibility = View.VISIBLE
            binding.desc.text = desc
            binding.title.text = title
            binding.voice.setOnClickListener {
                binding.voice.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_record_voice))
                if(TextUtils.isEmpty(url)){
                    readDesc("$date.\nReading.\n $title \n\n $desc")
                }
                else{
                    readDesc("$date.\nReading.\n $title. \n\n $desc \n\n Tap on the icon in the top right corner to find out more") } }
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val a: MutableSet<String> = HashSet()
            a.add("male") //using custom male voice
            val v = Voice("en-us-x-sfg#male_2-local", Locale("en", "US"), 900, 500, true, a)
            tts!!.voice = v
            tts!!.setSpeechRate(0.85f)

            val result: Int = tts!!.setVoice(v)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onDone(utteranceId: String) {
                    binding.voice.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_play_arrow))
                    binding.voice!!.isEnabled = true
                }

                override fun onError(utteranceId: String) {}
                override fun onStart(utteranceId: String) {}
            })
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            } else {
                binding.voice!!.isEnabled = true
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun readDesc(reading: String) {
        tts!!.speak(reading, TextToSpeech.QUEUE_FLUSH, null, "")
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item!!.itemId) {
            R.id.linkUrl -> startActivity(Intent(this@Headline, Browser::class.java).putExtra("url", url))
        }
        return true
    }
    public override fun onDestroy() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}
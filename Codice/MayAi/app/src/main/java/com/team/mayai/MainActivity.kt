package com.team.mayai

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.StrictMode
import android.provider.Settings
import android.util.Base64
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import khttp.responses.Response
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var user_sid: Long? = null
    private var state: Boolean = false
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "language"

    private var deviceID: String? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref: SharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val editor = sharedPref.edit()
        editor.putString(PREF_NAME, "it")
        editor.apply()

        //  BAD practice, io operations shoud be in separate thread
        val policy =
            StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        //val reset = Thread()

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions,0)
        }

        /*Configuring Media Recorder*/
        output = Environment.getExternalStorageDirectory().absolutePath + "/recording.wav"
        mediaRecorder = MediaRecorder()
        deviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(AudioFormat.ENCODING_PCM_16BIT);
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder?.setAudioChannels(1);
        mediaRecorder?.setAudioEncodingBitRate(128000);
        mediaRecorder?.setAudioSamplingRate(16000);
        mediaRecorder?.setOutputFile(output)

        scriviQui.visibility = View.INVISIBLE

        Thread{
            System.out.println("Here I am")
            Thread.sleep(20000)
            May.setImageResource(R.drawable.face_welcome)
        }
        //val lin = sharedPref.getString(PREF_NAME, "it")
        //SendJSONaudio(lin.toString())

        /*Start recording until pressing button*/
        TestRecord.setOnTouchListener(OnTouchListener { _, event -> // TODO Auto-generated method stub
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        ActivityCompat.requestPermissions(this, permissions,0)
                    } else {
                        TestRecord.setBackgroundResource(R.drawable.mic_r)
                        May.setImageResource(R.drawable.face_listening)
                        startRecording()
                    }
                    return@OnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    stopRecording()
                    TestRecord.setBackgroundResource(R.drawable.mic_in)
                    May.setImageResource(R.drawable.face_muted)
                    val lin = sharedPref.getString(PREF_NAME, "it")
                    SendJSONaudio(lin.toString())
                    //script()
                }
            }
            false
        })

        keyboard.setOnClickListener {
            showHide(scriviQui)
        }

        language.setOnClickListener {
            if(sharedPref.getString(PREF_NAME,"it") == "it"){
                editor.putString(PREF_NAME,"en")
                language.setImageResource(R.drawable.uk)
                chat.setText(R.string.wellcome_en)
            }else{
                editor.putString(PREF_NAME,"it")
                language.setImageResource(R.drawable.it)
                chat.setText(R.string.wellcome_it)
            }
            editor.apply()
        }

//        img.webViewClient = object : WebViewClient() {
//            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                view?.loadUrl(url)
//                return true
//            }
//        }
        //img.loadUrl("")


    }

    private fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            //Toast.makeText(this, "Recording started!", Toast.LenTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording(){
        try {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            state = false
            Toast.makeText(this, "Registrato", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            //Toast.makeText(this, "C'è stato un errore", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            //Toast.makeText(this, "C'è stato un errore", Toast.LENGTH_LONG).show()
        }
    }

    private fun showHide(view:View) {
        view.visibility = if (view.visibility == View.VISIBLE){
            View.INVISIBLE
        } else{
            View.VISIBLE
        }
    }

    private fun APIResponse(response: String){
        if (response == "ko"){
            May.setImageResource(R.drawable.face_risposta_ko)
        }
        if (response == "ok"){
            May.setImageResource(R.drawable.face_risposta_ok)
        }
    }

    private fun PlayResponse(){
        var mediaPlayer: MediaPlayer? = MediaPlayer.create(this, Uri.parse(Environment.getExternalStorageDirectory().toString()+"/response.wav"))
        mediaPlayer?.start()
    }

    private fun SendJSONaudio(lingua: String){
        if (user_sid == null) user_sid = System.currentTimeMillis() //  anche se passati x secondi dall'ultima registrazione
        val richiesta = Base64.encodeToString(File(output).readBytes(), Base64.NO_WRAP)
        val iid = deviceID
        val sid = user_sid
        val text = null
        val lc = lingua
        val outputAudio = false
        val authToken = "auth.TODO"

        try {
            val response : Response = khttp.post(
                url = "https://liver.mynetgear.com:1443/api/v1/detectIntent",
                headers = mapOf("Content-Type" to "application/json", "'Authorization" to authToken),

                json = mapOf(
                    "iid" to iid,
                    "sid" to sid,
                    "text" to text,
                    "inputAudio" to richiesta,
                    "lc" to lc,
                    "outputAudio" to outputAudio
                )
            )
            val jsonObj  : JSONObject = response.jsonObject
            val fulfillmentText: String = jsonObj.getString("fulfillmentText")
            val imageProdUri: String    = jsonObj.getString("imageProdUri")
            val imageLocUri: String     = jsonObj.getString("imageLocUri")
            val outputAudio: String     = jsonObj.getString("outputAudio")

            chat.setText(fulfillmentText)

        } catch (e: Exception) {
            e.printStackTrace()
            //Toast.makeText(this, "C'è stato un errore", Toast.LENGTH_LONG).show()
        }
    }


}
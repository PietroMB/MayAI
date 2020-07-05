package com.team.mayai


import android.R.attr.name
import android.os.Environment
import android.util.Base64
import org.json.JSONException
import org.json.JSONObject
import java.io.File


class Handler {
    fun convertToBase64(attachment: File): String {
        return Base64 . encodeToString (attachment.readBytes(), Base64.NO_WRAP)
    }

    fun getJSONdata(jsonData: String): ArrayList<String>{
        val response = ArrayList<String>()
        try {
            val obj = JSONObject(jsonData)

            //val employee: JSONObject = obj.getJSONObject("employee")
            response.add(obj.getString("fulfillmentText"))
            response.add(obj.getString("imageProdUri"))
            response.add(obj.getString("imageLocUri"))
            val outAudio = obj.getString("outputAudio")

            /* Salvataggio risposta audio in MP3*/
            val file = File(Environment.getExternalStorageDirectory().toString(), "response.mp3")
            if (file.exists()) file.delete()
            try {
                file.writeBytes(Base64.decode(outAudio,Base64.NO_WRAP))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return response
    }

}
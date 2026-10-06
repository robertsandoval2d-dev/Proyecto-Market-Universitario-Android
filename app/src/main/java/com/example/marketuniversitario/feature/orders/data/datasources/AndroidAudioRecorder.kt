package com.example.marketuniversitario.feature.orders.data.datasources

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import com.example.marketuniversitario.feature.orders.domain.repositories.AudioRecorder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import javax.inject.Inject

class AndroidAudioRecorder @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioRecorder {

    private var mediaRecorder: MediaRecorder? = null
    private var currentOutputFile: File? = null

    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }

    override fun startRecording(): File? {
        try {
            val audioDir = File(context.cacheDir, "audios")
            if (!audioDir.exists()) {
                audioDir.mkdirs()
            }
            val file = File(audioDir, "voice_${System.currentTimeMillis()}.m4a")
            currentOutputFile = file

            val recorder = createRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            mediaRecorder = recorder
            return file
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    override fun stopRecording(): File? {
        val fileToReturn = currentOutputFile
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mediaRecorder?.release()
            fileToReturn?.delete()
            currentOutputFile = null
            mediaRecorder = null
            return null
        }
        mediaRecorder = null
        currentOutputFile = null
        return fileToReturn
    }
}

package com.example.marketuniversitario.feature.orders.data.datasources

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import com.example.marketuniversitario.feature.orders.domain.repositories.AudioPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidAudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null

    override fun play(url: String, onCompletion: () -> Unit) {
        // Asegurarse de detener cualquier reproducción anterior antes de iniciar una nueva
        stop()

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, url.toUri())
                setOnPreparedListener { mp ->
                    mp.start()
                }
                setOnCompletionListener {
                    stop()
                    onCompletion()
                }
                prepareAsync() // Asíncrono para no bloquear el hilo principal
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stop()
            onCompletion()
        }
    }

    override fun stop() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
        }
    }
}

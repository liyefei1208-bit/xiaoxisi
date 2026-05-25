package com.xiaoxisi.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TtsEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var isInitialized: Boolean = false
    private var pendingText: String? = null

    init {
        initialize()
    }

    private fun initialize() {
        tts = TextToSpeech(context) { status ->
            isInitialized = (status == TextToSpeech.SUCCESS)
            if (isInitialized) {
                tts?.language = Locale.CHINESE
                tts?.setSpeechRate(0.9f)
                tts?.setPitch(1.0f)
                pendingText?.let { speak(it) }
                pendingText = null
            }
        }
    }

    fun speak(text: String) {
        if (!isInitialized) {
            pendingText = text
            return
        }

        tts?.let {
            if (it.isSpeaking) it.stop()

            val utteranceId = System.currentTimeMillis().toString()
            it.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}

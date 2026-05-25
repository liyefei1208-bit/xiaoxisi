package com.xiaoxisi.voice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRecorder @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    var isRecording: Boolean = false
        private set

    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun startRecording(): Result<Unit> {
        if (!hasPermission()) {
            return Result.failure(SecurityException("录音权限未授予"))
        }

        return try {
            outputFile = File(context.cacheDir, "xiaoxisi_recording_${System.currentTimeMillis()}.pcm")

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile?.absolutePath)
                prepare()
                start()
            }

            isRecording = true
            Result.success(Unit)
        } catch (e: Exception) {
            mediaRecorder?.release()
            mediaRecorder = null
            Result.failure(e)
        }
    }

    fun stopRecording(): Result<ByteArray> {
        val recorder = mediaRecorder ?: return Result.failure(IllegalStateException("未在录音"))

        return try {
            recorder.stop()
            recorder.release()
            mediaRecorder = null
            isRecording = false

            val file = outputFile ?: return Result.failure(IllegalStateException("录音文件不存在"))
            val audioData = file.readBytes()

            file.delete()

            Result.success(audioData)
        } catch (e: Exception) {
            recorder.release()
            mediaRecorder = null
            isRecording = false
            outputFile?.delete()
            Result.failure(e)
        }
    }

    fun cancelRecording() {
        mediaRecorder?.apply {
            try { stop() } catch (_: Exception) {}
            release()
        }
        mediaRecorder = null
        isRecording = false
        outputFile?.delete()
    }
}

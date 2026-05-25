package com.xiaoxisi.automation.action

import android.content.Context
import android.content.Intent as AndroidIntent
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import android.net.wifi.WifiManager
import com.xiaoxisi.automation.GuideAccessibilityService
import com.xiaoxisi.domain.model.Intent as UserIntent
import com.xiaoxisi.domain.model.SettingType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class SystemSettingsAction @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val audioManager: AudioManager
        get() = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    suspend fun execute(intent: UserIntent.SystemSetting): ActionExecutor.ActionResult {
        return withContext(Dispatchers.Main) {
            when (intent.setting) {
                SettingType.WIFI -> openWifiSettings()
                SettingType.BRIGHTNESS -> adjustBrightness(intent.action)
                SettingType.VOLUME -> adjustVolume(intent.action)
                SettingType.FONT_SIZE -> adjustFontSize(intent.action)
            }
        }
    }

    private fun openWifiSettings(): ActionExecutor.ActionResult {
        val intent = AndroidIntent(Settings.ACTION_WIFI_SETTINGS)
        intent.addFlags(AndroidIntent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)

        GuideAccessibilityService.instance?.showOverlay(
            540f, 200f, "点这里开关WiFi"
        )

        return ActionExecutor.ActionResult(
            success = true,
            message = "已经把WiFi设置打开了，点一下要连的WiFi名字就好",
            needsOverlay = true,
            overlayX = 540f,
            overlayY = 200f
        )
    }

    private fun adjustBrightness(action: String): ActionExecutor.ActionResult {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                val intent = AndroidIntent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.addFlags(AndroidIntent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return ActionExecutor.ActionResult(
                    success = true,
                    message = "需要你点一下允许修改系统设置，然后我就能帮你调亮度了"
                )
            }
        }

        val current = try {
            Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS
            )
        } catch (e: Exception) {
            128
        }

        val newBrightness = when {
            action.contains("亮") || action.contains("大") -> (current + 50).coerceAtMost(255)
            action.contains("暗") || action.contains("小") -> (current - 50).coerceAtLeast(30)
            else -> current
        }

        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            newBrightness
        )

        val desc = if (newBrightness > current) "亮" else "暗"
        return ActionExecutor.ActionResult(
            success = true,
            message = "屏幕已经调${desc}了，你看看合适吗？"
        )
    }

    private fun adjustVolume(action: String): ActionExecutor.ActionResult {
        val streamType = AudioManager.STREAM_MUSIC
        val currentVolume = audioManager.getStreamVolume(streamType)
        val maxVolume = audioManager.getStreamMaxVolume(streamType)

        val newVolume = when {
            action.contains("大") || action.contains("响") || action.contains("高") ->
                (currentVolume + 2).coerceAtMost(maxVolume)
            action.contains("小") || action.contains("轻") || action.contains("低") ->
                (currentVolume - 2).coerceAtLeast(0)
            else -> currentVolume
        }

        audioManager.setStreamVolume(streamType, newVolume, 0)

        val desc = if (newVolume > currentVolume) "响" else "轻"
        return ActionExecutor.ActionResult(
            success = true,
            message = "声音已经调${desc}了"
        )
    }

    private fun adjustFontSize(action: String): ActionExecutor.ActionResult {
        val intent = AndroidIntent(Settings.ACTION_DISPLAY_SETTINGS)
        intent.addFlags(AndroidIntent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)

        GuideAccessibilityService.instance?.showOverlay(
            540f, 600f, "找字体大小"
        )

        return ActionExecutor.ActionResult(
            success = true,
            message = "已打开显示设置，往下翻找到「字体大小」点进去就能调了",
            needsOverlay = true,
            overlayX = 540f,
            overlayY = 600f
        )
    }
}

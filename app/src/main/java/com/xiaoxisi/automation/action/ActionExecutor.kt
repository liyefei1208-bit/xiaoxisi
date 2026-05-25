package com.xiaoxisi.automation.action

import android.util.Log
import com.xiaoxisi.domain.model.Intent
import com.xiaoxisi.domain.model.PhoneAction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionExecutor @Inject constructor(
    private val systemSettingsAction: SystemSettingsAction,
    private val phoneAction: PhoneActionExecutor,
    private val appAction: AppAction
) {
    companion object {
        private const val TAG = "Xiaoxisi-Executor"
    }

    suspend fun execute(intent: Intent): ActionResult {
        Log.d(TAG, "execute intent: ${intent::class.simpleName}")
        return when (intent) {
            is Intent.SystemSetting -> systemSettingsAction.execute(intent)
            is Intent.Phone -> phoneAction.execute(intent)
            is Intent.App -> {
                Log.d(TAG, "executing App action: action=${intent.action}, appName=${intent.appName}")
                appAction.execute(intent)
            }
            is Intent.Chat -> ActionResult(
                success = true,
                message = null
            )
            is Intent.Unknown -> ActionResult(
                success = false,
                message = "不太确定你想做什么，能换个说法试试吗？"
            )
        }
    }

    data class ActionResult(
        val success: Boolean,
        val message: String?,
        val needsOverlay: Boolean = false,
        val overlayX: Float = 0f,
        val overlayY: Float = 0f
    )
}

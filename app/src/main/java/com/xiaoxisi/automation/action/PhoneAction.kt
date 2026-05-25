package com.xiaoxisi.automation.action

import android.Manifest
import android.content.Context
import android.content.Intent as AndroidIntent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import com.xiaoxisi.domain.model.Intent as UserIntent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class PhoneActionExecutor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun execute(intent: UserIntent.Phone): ActionExecutor.ActionResult {
        return withContext(Dispatchers.Main) {
            when (intent.action) {
                com.xiaoxisi.domain.model.PhoneAction.DIAL -> dial(intent.contactName)
                com.xiaoxisi.domain.model.PhoneAction.HANGUP -> hangup()
                com.xiaoxisi.domain.model.PhoneAction.ANSWER -> answer()
            }
        }
    }

    private fun dial(contactName: String?): ActionExecutor.ActionResult {
        if (!hasCallPermission()) {
            return ActionExecutor.ActionResult(
                success = false,
                message = "需要电话权限才能帮你打电话，请在设置中授权"
            )
        }

        if (contactName.isNullOrBlank()) {
            val intent = AndroidIntent(AndroidIntent.ACTION_DIAL)
            intent.addFlags(AndroidIntent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            return ActionExecutor.ActionResult(
                success = true,
                message = "已打开拨号盘，你想打给谁？"
            )
        }

        val intent = AndroidIntent(AndroidIntent.ACTION_CALL).apply {
            data = Uri.parse("tel:$contactName")
            addFlags(AndroidIntent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)

        return ActionExecutor.ActionResult(
            success = true,
            message = "正在帮你打电话给$contactName"
        )
    }

    private fun hangup(): ActionExecutor.ActionResult {
        return ActionExecutor.ActionResult(
            success = true,
            message = "好的，电话已挂断。你可以按手机上的红色挂断按钮"
        )
    }

    private fun answer(): ActionExecutor.ActionResult {
        return ActionExecutor.ActionResult(
            success = true,
            message = "来电时可以按绿色接听按钮接电话"
        )
    }

    private fun hasCallPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

package com.xiaoxisi.automation.action

import android.content.Context
import android.content.Intent as AndroidIntent
import android.content.pm.PackageManager
import android.net.Uri
import com.xiaoxisi.domain.model.Intent as UserIntent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class AppAction @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun execute(intent: UserIntent.App): ActionExecutor.ActionResult {
        return withContext(Dispatchers.Main) {
            when (intent.action) {
                com.xiaoxisi.domain.model.AppAction.OPEN -> openApp(intent.appName)
                com.xiaoxisi.domain.model.AppAction.SEARCH -> searchApp(intent.appName)
                com.xiaoxisi.domain.model.AppAction.INSTALL -> installApp(intent.appName)
            }
        }
    }

    private fun openApp(appName: String?): ActionExecutor.ActionResult {
        if (appName.isNullOrBlank()) {
            return ActionExecutor.ActionResult(
                success = false,
                message = "你想打开哪个应用？"
            )
        }

        val packageName = findPackageName(appName)
        if (packageName != null) {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(AndroidIntent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                return ActionExecutor.ActionResult(
                    success = true,
                    message = "已经帮你打开了$appName"
                )
            }
        }

        return ActionExecutor.ActionResult(
            success = true,
            message = "没有找到${appName}，我帮你搜索一下"
        )
    }

    private fun searchApp(appName: String?): ActionExecutor.ActionResult {
        if (appName.isNullOrBlank()) {
            return ActionExecutor.ActionResult(
                success = false,
                message = "你想找哪个应用？"
            )
        }

        val intent = AndroidIntent(AndroidIntent.ACTION_VIEW).apply {
            data = Uri.parse("market://search?q=$appName")
            addFlags(AndroidIntent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(intent)
            ActionExecutor.ActionResult(
                success = true,
                message = "已在应用商店搜索$appName，点一下安装就好"
            )
        } catch (e: Exception) {
            val webIntent = AndroidIntent(AndroidIntent.ACTION_VIEW).apply {
                data = Uri.parse("https://sj.qq.com/app/search?keyword=$appName")
                addFlags(AndroidIntent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
            ActionExecutor.ActionResult(
                success = true,
                message = "已打开应用商店网页，搜索$appName"
            )
        }
    }

    private fun installApp(appName: String?): ActionExecutor.ActionResult {
        return searchApp(appName)
    }

    private fun findPackageName(appName: String): String? {
        val packageMap = mapOf(
            "微信" to "com.tencent.mm",
            "wechat" to "com.tencent.mm",
            "支付宝" to "com.eg.android.AlipayGphone",
            "抖音" to "com.ss.android.ugc.aweme",
            "淘宝" to "com.taobao.taobao",
            "美团" to "com.sankuai.meituan",
            "高德" to "com.autonavi.minimap",
            "地图" to "com.autonavi.minimap",
            "相机" to "com.android.camera",
            "照片" to "com.android.gallery3d",
            "相册" to "com.android.gallery3d",
            "设置" to "com.android.settings",
            "电话" to "com.android.dialer",
            "短信" to "com.android.mms"
        )

        packageMap[appName]?.let {
            try {
                context.packageManager.getPackageInfo(it, 0)
                return it
            } catch (_: PackageManager.NameNotFoundException) {}
        }

        try {
            val packages = context.packageManager.getInstalledApplications(0)
            for (pkg in packages) {
                val label = context.packageManager.getApplicationLabel(pkg).toString()
                if (label.contains(appName, ignoreCase = true)) {
                    return pkg.packageName
                }
            }
        } catch (_: Exception) {}

        return null
    }
}

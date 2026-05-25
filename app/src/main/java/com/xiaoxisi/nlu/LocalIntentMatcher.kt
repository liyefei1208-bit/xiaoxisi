package com.xiaoxisi.nlu

import android.util.Log
import com.xiaoxisi.domain.model.Intent
import com.xiaoxisi.domain.model.SettingType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalIntentMatcher @Inject constructor() {

    companion object {
        private const val TAG = "Xiaoxisi-LocalMatch"
    }

    data class MatchResult(
        val intent: Intent,
        val replyText: String,
        val entities: Map<String, String> = emptyMap()
    )

    fun match(userText: String): MatchResult {
        val text = userText.trim()
        Log.d(TAG, "match input: '$text'")

        when {
            matchesWifi(text) -> {
                Log.d(TAG, "matched: WIFI")
                return if (text.contains("开") || text.contains("连") || text.contains("打")) {
                    MatchResult(
                        intent = Intent.SystemSetting(SettingType.WIFI, "open"),
                        replyText = "好嘞，WiFi设置已经帮你打开哉，点你要连的WiFi名字就好。",
                        entities = mapOf("setting" to "wifi")
                    )
                } else {
                    MatchResult(
                        intent = Intent.SystemSetting(SettingType.WIFI, "open"),
                        replyText = "WiFi设置打开了，你看看要连哪个。",
                        entities = mapOf("setting" to "wifi")
                    )
                }
            }

            matchesBrightness(text) -> {
                Log.d(TAG, "matched: BRIGHTNESS")
                return if (text.contains("暗")) {
                    MatchResult(
                        intent = Intent.SystemSetting(SettingType.BRIGHTNESS, "down"),
                        replyText = "屏幕已经调暗了，你看看合适吗？",
                        entities = mapOf("setting" to "brightness")
                    )
                } else {
                    MatchResult(
                        intent = Intent.SystemSetting(SettingType.BRIGHTNESS, "up"),
                        replyText = "屏幕已经调亮了，你看看合适吗？",
                        entities = mapOf("setting" to "brightness")
                    )
                }
            }

            matchesVolume(text) -> {
                Log.d(TAG, "matched: VOLUME")
                val up = text.contains("大") || text.contains("响") || text.contains("高")
                return MatchResult(
                    intent = Intent.SystemSetting(SettingType.VOLUME, if (up) "up" else "down"),
                    replyText = if (up) "声音已经调响了" else "声音已经调轻了",
                    entities = mapOf("setting" to "volume")
                )
            }

            matchesFontSize(text) -> {
                Log.d(TAG, "matched: FONT_SIZE")
                return MatchResult(
                    intent = Intent.SystemSetting(SettingType.FONT_SIZE, "up"),
                    replyText = "已打开显示设置，往下翻找到「字体大小」就能调了。",
                    entities = mapOf("setting" to "font_size")
                )
            }

            matchesDial(text) -> {
                Log.d(TAG, "matched: DIAL")
                val name = extractName(text) ?: ""
                return if (name.isNotBlank()) {
                    MatchResult(
                        intent = Intent.Phone(com.xiaoxisi.domain.model.PhoneAction.DIAL, name),
                        replyText = "正在帮你打电话给$name",
                        entities = mapOf("contact" to name)
                    )
                } else {
                    MatchResult(
                        intent = Intent.Phone(com.xiaoxisi.domain.model.PhoneAction.DIAL),
                        replyText = "已打开拨号盘，你想打给谁？",
                        entities = emptyMap()
                    )
                }
            }

            matchesHangup(text) -> {
                Log.d(TAG, "matched: HANGUP")
                return MatchResult(
                    intent = Intent.Phone(com.xiaoxisi.domain.model.PhoneAction.HANGUP),
                    replyText = "好的，电话已挂断。你也可以按手机上的红色挂断按钮。",
                    entities = emptyMap()
                )
            }

            matchesOpenApp(text) -> {
                Log.d(TAG, "matched: OPEN_APP")
                val appName = extractAppName(text) ?: ""
                return if (appName.isNotBlank()) {
                    MatchResult(
                        intent = Intent.App(com.xiaoxisi.domain.model.AppAction.OPEN, appName),
                        replyText = "好的，帮你打开$appName",
                        entities = mapOf("app" to appName)
                    )
                } else {
                    MatchResult(
                        intent = Intent.App(com.xiaoxisi.domain.model.AppAction.OPEN),
                        replyText = "你想打开哪个应用？",
                        entities = emptyMap()
                    )
                }
            }

            matchesInstallApp(text) -> {
                Log.d(TAG, "matched: INSTALL_APP")
                val appName = extractAppName(text) ?: ""
                return if (appName.isNotBlank()) {
                    MatchResult(
                        intent = Intent.App(com.xiaoxisi.domain.model.AppAction.INSTALL, appName),
                        replyText = "已在应用商店搜索$appName，点安装就好。",
                        entities = mapOf("app" to appName)
                    )
                } else {
                    MatchResult(
                        intent = Intent.App(com.xiaoxisi.domain.model.AppAction.INSTALL),
                        replyText = "你想安装哪个应用？",
                        entities = emptyMap()
                    )
                }
            }

            isGreeting(text) -> {
                Log.d(TAG, "matched: GREETING")
                return MatchResult(
                    intent = Intent.Chat(),
                    replyText = "侬好呀！有撒事体需要我帮忙伐？",
                    entities = emptyMap()
                )
            }

            else -> {
                Log.d(TAG, "matched: UNKNOWN, falling back")
                return MatchResult(
                    intent = Intent.Unknown(0.3f),
                    replyText = "不太确定你想做什么，能换个说法试试吗？比如试试说「打开微信」或者「屏幕太暗哉」。",
                    entities = emptyMap()
                )
            }
        }
    }

    private fun matchesWifi(text: String): Boolean {
        val keywords = listOf("wifi", "wi-fi", "WiFi", "Wi-Fi", "无线", "上网", "网络", "连网", "连牢")
        return keywords.any { text.contains(it, ignoreCase = true) } ||
            text.contains("连勿上") || text.contains("连不上")
    }

    private fun matchesBrightness(text: String): Boolean {
        val keywords = listOf("亮", "暗", "屏幕", "光", "亮度", "太亮", "太暗", "暗哉")
        return keywords.any { text.contains(it) } &&
            !text.contains("音") && !text.contains("声") && !text.contains("wifi") && !text.contains("WiFi")
    }

    private fun matchesVolume(text: String): Boolean {
        val keywords = listOf("声音", "音量", "响", "轻", "声音")
        return keywords.any { text.contains(it) } ||
            text.contains("听勿见") || text.contains("听不见") || text.contains("忒轻") || text.contains("忒响")
    }

    private fun matchesFontSize(text: String): Boolean {
        val keywords = listOf("字", "字体", "大小")
        return keywords.any { text.contains(it) } &&
            (text.contains("小") || text.contains("大") || text.contains("看勿清"))
    }

    private fun matchesDial(text: String): Boolean {
        val keywords = listOf("打电话", "打畀", "拨", "打给", "拨号")
        return keywords.any { text.contains(it) } || text.contains("电话")
    }

    private fun matchesHangup(text: String): Boolean {
        val keywords = listOf("挂断", "挂脱", "挂", "关脱")
        return keywords.any { text.contains(it) }
    }

    private fun matchesOpenApp(text: String): Boolean {
        val openKeywords = listOf("打开", "开", "点开", "帮我开", "我要用", "用一下", "看看")
        val hasOpen = openKeywords.any { text.contains(it) }
        val notSettings = !text.contains("wifi") && !text.contains("WiFi") && !text.contains("Wi-Fi") &&
            !text.contains("无线") && !text.contains("上网") && !text.contains("网络") &&
            !text.contains("连") && !text.contains("设置")
        return hasOpen && notSettings
    }

    private fun matchesInstallApp(text: String): Boolean {
        val keywords = listOf("装", "安装", "下载")
        return keywords.any { text.contains(it) }
    }

    private fun isGreeting(text: String): Boolean {
        val keywords = listOf("你好", "侬好", "嗨", "hello", "hi", "在吗", "在不在", "小希司")
        return keywords.any { text.contains(it, ignoreCase = true) } &&
            text.length <= 10
    }

    private fun extractName(text: String): String? {
        val patterns = listOf(
            Regex("(?:打(?:电话)?(?:给|畀|拨))(.{2,4})"),
            Regex("(.{2,4})(?:电话)")
        )
        for (p in patterns) {
            val match = p.find(text)
            if (match != null) {
                val name = match.groupValues[1].trim()
                if (name.length in 2..4) return name
            }
        }
        return null
    }

    private fun extractAppName(text: String): String? {
        val knownApps = listOf(
            "微信", "wechat", "WeChat",
            "支付宝", "抖音", "淘宝", "美团", "高德",
            "相机", "照片", "相册", "设置", "电话", "短信", "通讯录",
            "地图", "天气", "音乐", "视频", "浏览器", "日历",
            "计算器", "手电筒", "闹钟", "时钟", "QQ", "微博",
            "小红书", "拼多多", "京东", "滴滴", "饿了么"
        )
        for (app in knownApps) {
            if (text.contains(app, ignoreCase = true)) {
                Log.d(TAG, "extractAppName found known app: '$app'")
                return app
            }
        }

        val patterns = listOf(
            Regex("(?:打开|开|点开|帮我开|我要用|用一下|看看)(.{2,6})"),
        )
        for (p in patterns) {
            val match = p.find(text)
            if (match != null) {
                val name = match.groupValues[1].trim().trimEnd('。', '！', '，', '?', '！', '~', '了', '呗', '吧')
                Log.d(TAG, "extractAppName pattern matched: '$name'")
                if (name.length in 2..6) return name
            }
        }
        Log.d(TAG, "extractAppName: no match")
        return null
    }
}

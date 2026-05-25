package com.xiaoxisi.domain.model

sealed class Intent {
    abstract val type: IntentType
    abstract val confidence: Float

    data class SystemSetting(
        val setting: SettingType,
        val action: String,
        override val confidence: Float = 1.0f
    ) : Intent() {
        override val type = IntentType.SYSTEM_SETTING
    }

    data class Phone(
        val action: PhoneAction,
        val contactName: String? = null,
        override val confidence: Float = 1.0f
    ) : Intent() {
        override val type = IntentType.PHONE
    }

    data class App(
        val action: AppAction,
        val appName: String? = null,
        override val confidence: Float = 1.0f
    ) : Intent() {
        override val type = IntentType.APP
    }

    data class Chat(
        override val confidence: Float = 1.0f
    ) : Intent() {
        override val type = IntentType.CHAT
    }

    data class Unknown(
        override val confidence: Float = 0.0f
    ) : Intent() {
        override val type = IntentType.UNKNOWN
    }
}

enum class IntentType {
    SYSTEM_SETTING, PHONE, APP, CHAT, UNKNOWN
}

enum class SettingType {
    WIFI, BRIGHTNESS, VOLUME, FONT_SIZE
}

enum class PhoneAction {
    DIAL, HANGUP, ANSWER
}

enum class AppAction {
    OPEN, SEARCH, INSTALL
}

data class IntentResult(
    val intent: Intent,
    val replyText: String,
    val entities: Map<String, String> = emptyMap()
)

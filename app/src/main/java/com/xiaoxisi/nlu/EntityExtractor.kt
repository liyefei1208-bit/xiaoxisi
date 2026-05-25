package com.xiaoxisi.nlu

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntityExtractor @Inject constructor() {

    fun extractContactName(text: String): String? {
        val patterns = listOf(
            Regex("(?:打(?:电话)?(?:给|畀|拨))([\\u4e00-\\u9fa5]{2,4})"),
            Regex("(?:打电话畀)([\\u4e00-\\u9fa5]{2,4})")
        )
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                return match.groupValues[1]
            }
        }
        return null
    }

    fun extractAppName(text: String): String? {
        val patterns = listOf(
            Regex("(?:打开|开)([\\u4e00-\\u9fa5a-zA-Z]{2,10})"),
            Regex("(?:装|下载|安装)([\\u4e00-\\u9fa5a-zA-Z]{2,10})")
        )
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                return match.groupValues[1]
            }
        }
        return null
    }
}

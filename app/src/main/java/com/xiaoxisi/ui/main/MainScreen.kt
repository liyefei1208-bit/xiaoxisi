package com.xiaoxisi.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xiaoxisi.ui.theme.Beige
import com.xiaoxisi.ui.theme.BeigeDark
import com.xiaoxisi.ui.theme.Teal
import com.xiaoxisi.ui.theme.TextHint
import com.xiaoxisi.ui.theme.TextPrimary
import com.xiaoxisi.ui.theme.TextSecondary
import com.xiaoxisi.ui.theme.White
import com.xiaoxisi.ui.theme.XiaoxisiType

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var showTextInput by remember { mutableStateOf(false) }
    var textInput by remember { mutableStateOf("") }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        containerColor = Beige
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            HeaderBar(onSettingsClick = onNavigateToSettings)

            if (!uiState.apiConfigured) {
                ApiConfigBanner(llmConfigured = uiState.llmConfigured)
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (uiState.messages.isEmpty()) {
                    item { Spacer(modifier = Modifier.height(60.dp)) }
                    item { WelcomeSection() }
                }

                items(uiState.messages, key = { it.id }) { message ->
                    ChatBubble(message = message)
                }

                if (uiState.isProcessing) {
                    item { TypingIndicator() }
                }

                if (uiState.errorMessage != null) {
                    item {
                        ErrorBanner(
                            message = uiState.errorMessage!!,
                            onDismiss = { viewModel.dismissError() }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            AnimatedVisibility(
                visible = showTextInput,
                enter = fadeIn() + slideInVertically()
            ) {
                TextInputBar(
                    value = textInput,
                    onValueChange = { textInput = it },
                    onSend = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendText(textInput.trim())
                            textInput = ""
                            showTextInput = false
                        }
                    },
                    onClose = { showTextInput = false }
                )
            }

            VoiceInputBar(
                isRecording = uiState.isRecording,
                onRecordStart = { viewModel.startRecording() },
                onRecordStop = { viewModel.stopRecording() },
                onToggleTextInput = { showTextInput = !showTextInput },
                onClearHistory = { viewModel.clearHistory() }
            )
        }
    }
}

@Composable
private fun HeaderBar(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Beige)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(48.dp))
        Text(
            text = "小希司",
            style = XiaoxisiType.displayLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onSettingsClick) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "设置",
                tint = TextSecondary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun WelcomeSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text("希", fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Teal)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("你好，我是小希司", style = XiaoxisiType.headline, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "用绍兴话跟我说，我来帮你用手机",
            style = XiaoxisiType.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.Center) {
            SuggestionChip("打开微信")
            Spacer(modifier = Modifier.width(12.dp))
            SuggestionChip("屏幕太暗")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.Center) {
            SuggestionChip("打电话")
            Spacer(modifier = Modifier.width(12.dp))
            SuggestionChip("声音太轻")
        }
    }
}

@Composable
private fun SuggestionChip(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Text(
            text = text,
            style = XiaoxisiType.labelMedium,
            color = Teal,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Teal),
            contentAlignment = Alignment.Center
        ) {
            Text("希", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = Teal
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("正在理解...", style = XiaoxisiType.bodySmall, color = TextHint)
            }
        }
    }
}

@Composable
private fun ErrorBanner(message: String, onDismiss: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFF3E0)
    ) {
        Text(
            text = message,
            style = XiaoxisiType.bodySmall,
            color = Color(0xFFE65100),
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
private fun ApiConfigBanner(llmConfigured: Boolean) {
    val bgColor = if (llmConfigured) Color(0xFFE3F2FD) else Color(0xFFE8F5E9)
    val title = if (llmConfigured) "DeepSeek 已接入" else "本地模式"
    val msg = if (llmConfigured)
        "文字对话使用 AI 智能回复。语音识别未配置。"
    else
        "API 未配置，使用关键词匹配。"

    Surface(modifier = Modifier.fillMaxWidth(), color = bgColor) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("• $title", style = XiaoxisiType.labelSmall, color = Color(0xFF1565C0))
            Spacer(modifier = Modifier.width(8.dp))
            Text(msg, style = XiaoxisiType.labelSmall, color = Color(0xFF1565C0).copy(alpha = 0.7f))
        }
    }
}

package com.xiaoxisi.ui.main

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.xiaoxisi.ui.theme.BrownBorder
import com.xiaoxisi.ui.theme.BrownMedium
import com.xiaoxisi.ui.theme.Orange
import com.xiaoxisi.ui.theme.OrangeDark
import com.xiaoxisi.ui.theme.OrangeSurface
import com.xiaoxisi.ui.theme.TextPrimary
import com.xiaoxisi.ui.theme.TextSecondary
import com.xiaoxisi.ui.theme.WarmWhite
import com.xiaoxisi.ui.theme.White
import com.xiaoxisi.ui.theme.XiaoxisiType
import com.xiaoxisi.ui.theme.orangeGradient
import com.xiaoxisi.ui.theme.orangeGreetingGradient

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var showTextInput by remember { mutableStateOf(false) }
    var textInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startRecording()
        }
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        containerColor = WarmWhite
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
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                    item { WelcomeSection(onSuggestionClick = { viewModel.sendText(it) }) }
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

            Column(
                modifier = Modifier.imePadding()
            ) {
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

                if (!showTextInput) {
                    VoiceInputBar(
                        isRecording = uiState.isRecording,
                        onRecordStart = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                                == PackageManager.PERMISSION_GRANTED
                            ) {
                                viewModel.startRecording()
                            } else {
                                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        onRecordStop = { viewModel.stopRecording() },
                        onToggleTextInput = { showTextInput = !showTextInput },
                        onClearHistory = { viewModel.clearHistory() }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderBar(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WarmWhite)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(orangeGradient()),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "希",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "小希司",
            style = XiaoxisiType.brandTitle,
            color = TextPrimary,
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
private fun WelcomeSection(onSuggestionClick: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(orangeGradient()),
            contentAlignment = Alignment.Center
        ) {
            Text("希", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(orangeGreetingGradient())
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "你好，我是小希司",
                    style = XiaoxisiType.headline,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "用绍兴话跟我说，我来帮你用手机",
                    style = XiaoxisiType.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.6f))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "随时可以按下方说话键",
                        style = XiaoxisiType.caption,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.Center) {
            SuggestionChip("打开微信", onClick = { onSuggestionClick("打开微信") })
            Spacer(modifier = Modifier.width(12.dp))
            SuggestionChip("屏幕太暗", onClick = { onSuggestionClick("屏幕太暗了") })
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.Center) {
            SuggestionChip("打电话", onClick = { onSuggestionClick("打电话") })
            Spacer(modifier = Modifier.width(12.dp))
            SuggestionChip("声音太轻", onClick = { onSuggestionClick("声音太轻了") })
        }
    }
}

@Composable
private fun SuggestionChip(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BrownBorder)
    ) {
        Text(
            text = text,
            style = XiaoxisiType.labelMedium,
            color = Orange,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "typingAlpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(orangeGradient()),
            contentAlignment = Alignment.Center
        ) {
            Text("希", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, BrownBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = Orange.copy(alpha = alpha)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("正在理解...", style = XiaoxisiType.bodySmall, color = BrownMedium)
            }
        }
    }
}

@Composable
private fun ErrorBanner(message: String, @Suppress("UNUSED_PARAMETER") onDismiss: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = OrangeSurface
    ) {
        Text(
            text = message,
            style = XiaoxisiType.bodySmall,
            color = OrangeDark,
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
private fun ApiConfigBanner(llmConfigured: Boolean) {
    val bgColor = if (llmConfigured) Color(0xFFE8F5E9) else Color(0xFFE3F2FD)
    val title = if (llmConfigured) "AI 已接入" else "本地模式"
    val msg = if (llmConfigured)
        "文字对话使用 AI 智能回复。语音识别未配置。"
    else
        "API 未配置，使用关键词匹配。"

    Surface(modifier = Modifier.fillMaxWidth(), color = bgColor) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$title", style = XiaoxisiType.labelSmall, color = Color(0xFF1565C0))
            Spacer(modifier = Modifier.width(8.dp))
            Text(msg, style = XiaoxisiType.labelSmall, color = Color(0xFF1565C0).copy(alpha = 0.7f))
        }
    }
}

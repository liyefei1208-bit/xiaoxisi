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
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.xiaoxisi.ui.theme.BrownBorder
import com.xiaoxisi.ui.theme.BrownDark
import com.xiaoxisi.ui.theme.BrownMedium
import com.xiaoxisi.ui.theme.Cream
import com.xiaoxisi.ui.theme.GreenAccent
import com.xiaoxisi.ui.theme.GreenLight
import com.xiaoxisi.ui.theme.Orange
import com.xiaoxisi.ui.theme.OrangeDark
import com.xiaoxisi.ui.theme.OrangeSurface
import com.xiaoxisi.ui.theme.TextHint
import com.xiaoxisi.ui.theme.TextPrimary
import com.xiaoxisi.ui.theme.TextSecondary
import com.xiaoxisi.ui.theme.WarmWhite
import com.xiaoxisi.ui.theme.White
import com.xiaoxisi.ui.theme.XiaoxisiType
import com.xiaoxisi.ui.theme.greenGradient
import com.xiaoxisi.ui.theme.orangeGradient
import com.xiaoxisi.ui.theme.orangeRecordingGradient

@Composable
fun ChatScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var textInput by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val view = LocalView.current
    val density = LocalDensity.current
    var keyboardHeightDp by remember { mutableStateOf(0f) }

    DisposableEffect(view) {
        val listener = android.view.ViewTreeObserver.OnGlobalLayoutListener {
            val rect = android.graphics.Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeightDp = with(density) { view.rootView.height.toDp() }
            val visibleBottomDp = with(density) { rect.bottom.toDp() }
            val keypadHeightDp = screenHeightDp - visibleBottomDp
            if (keypadHeightDp > screenHeightDp * 0.15f) {
                keyboardHeightDp = keypadHeightDp.value
            } else {
                keyboardHeightDp = 0f
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
    val focusManager = LocalFocusManager.current

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) viewModel.startRecording()
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmWhite)
            .then(
                if (keyboardHeightDp > 0f) Modifier.clickable(
                    indication = null,
                    interactionSource = interactionSource
                ) {
                    val imm = context.getSystemService(android.view.inputmethod.InputMethodManager::class.java)
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                } else Modifier
            )
    ) {
        ChatHeader()

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (uiState.messages.isEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    ChatWelcome(
                        greeting = "老伯/老姐，我是小希司，侬有啥要问的，尽管跟我说！"
                    )
                }
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

        Box(
            modifier = Modifier
                .padding(bottom = keyboardHeightDp.dp)
        ) {
            Column {
                ChatInputBar(
                    value = textInput,
                    isRecording = uiState.isRecording,
                    onValueChange = { textInput = it },
                    onSend = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendText(textInput.trim())
                            textInput = ""
                        }
                    },
                    onMicClick = {
                        if (uiState.isRecording) {
                            viewModel.stopRecording()
                        } else {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                                == PackageManager.PERMISSION_GRANTED
                            ) {
                                viewModel.startRecording()
                            } else {
                                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ChatHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(orangeGradient()),
            contentAlignment = Alignment.Center
        ) {
            Text("希", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "小希司",
                style = XiaoxisiType.headline,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "在线 · 随时可问",
                style = XiaoxisiType.caption,
                color = GreenAccent,
                fontWeight = FontWeight.Medium
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BrownBorder)
    )
}

@Composable
private fun ChatWelcome(greeting: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(orangeGradient()),
            contentAlignment = Alignment.Center
        ) {
            Text("希", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        androidx.compose.material3.Surface(
            modifier = Modifier.fillMaxWidth(0.82f),
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = 6.dp, bottomEnd = 16.dp
            ),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, BrownBorder),
            shadowElevation = 0.dp
        ) {
            Text(
                text = greeting,
                style = XiaoxisiType.bodyLarge,
                color = BrownDark,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(orangeGradient()),
            contentAlignment = Alignment.Center
        ) {
            Text("希", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        androidx.compose.material3.Surface(
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
    androidx.compose.material3.Surface(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatInputBar(
    value: String,
    isRecording: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onMicClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isRecording) orangeRecordingGradient() else orangeGradient()
                    )
                    .clickable { onMicClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Mic,
                    contentDescription = if (isRecording) "停止录音" else "录音",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text("说话或者打字都可以……", style = XiaoxisiType.bodyMedium, color = TextHint)
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(22.dp)),
                textStyle = XiaoxisiType.bodyLarge.copy(color = BrownDark),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Cream,
                    unfocusedContainerColor = Cream,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(22.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .then(
                        if (value.isNotBlank()) Modifier.background(brush = greenGradient())
                        else Modifier.background(Cream)
                    )
                    .clickable {
                        if (value.isNotBlank()) onSend()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "发送",
                    tint = if (value.isNotBlank()) Color.White else TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        AnimatedVisibility(
            visible = isRecording,
            enter = fadeIn() + slideInVertically()
        ) {
            VoiceWaveBar()
        }
    }
}

@Composable
private fun VoiceWaveBar() {
    val infiniteTransition = rememberInfiniteTransition(label = "waves")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { i ->
            val heightFraction by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = i * 100),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wave$i"
            )
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height((20 * heightFraction).dp)
                    .clip(CircleShape)
                    .background(Orange)
                    .padding(horizontal = 2.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text("正在录音……", style = XiaoxisiType.labelSmall, color = Orange, fontWeight = FontWeight.Medium)
    }
}

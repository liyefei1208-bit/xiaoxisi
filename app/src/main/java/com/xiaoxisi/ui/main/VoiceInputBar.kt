package com.xiaoxisi.ui.main

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xiaoxisi.ui.theme.BrownDark
import com.xiaoxisi.ui.theme.Cream
import com.xiaoxisi.ui.theme.GreenAccent
import com.xiaoxisi.ui.theme.Orange
import com.xiaoxisi.ui.theme.TextHint
import com.xiaoxisi.ui.theme.TextSecondary
import com.xiaoxisi.ui.theme.WarmWhite
import com.xiaoxisi.ui.theme.White
import com.xiaoxisi.ui.theme.XiaoxisiType
import com.xiaoxisi.ui.theme.orangeGradient
import com.xiaoxisi.ui.theme.orangeRecordingGradient

@Composable
fun VoiceInputBar(
    isRecording: Boolean,
    onRecordStart: () -> Unit,
    onRecordStop: () -> Unit,
    onToggleTextInput: () -> Unit,
    onClearHistory: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micPulse"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(WarmWhite)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onClearHistory) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "新会话",
                        tint = TextSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    "新会话",
                    style = XiaoxisiType.labelSmall,
                    color = TextHint,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(2f)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .scale(if (isRecording) pulseScale else 1f)
                        .clip(CircleShape)
                        .background(if (isRecording) orangeRecordingGradient() else orangeGradient())
                        .clickable {
                            if (isRecording) onRecordStop() else onRecordStart()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Mic,
                        contentDescription = if (isRecording) "松开发送" else "按住说话",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    if (isRecording) "松开发送" else "按住说话",
                    style = XiaoxisiType.labelSmall,
                    color = if (isRecording) Orange else TextHint,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onToggleTextInput) {
                    Icon(
                        Icons.Filled.Keyboard,
                        contentDescription = "键盘输入",
                        tint = TextSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    "打字",
                    style = XiaoxisiType.labelSmall,
                    color = TextHint,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WarmWhite)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text("用绍兴方言打字试试...", style = XiaoxisiType.bodyMedium, color = TextHint)
            },
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp)),
            textStyle = XiaoxisiType.bodyLarge.copy(color = BrownDark),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(24.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (value.isNotBlank()) GreenAccent else Cream)
                .clickable { if (value.isNotBlank()) onSend() else onClose() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (value.isNotBlank()) Icons.AutoMirrored.Filled.Send else Icons.Filled.Keyboard,
                contentDescription = if (value.isNotBlank()) "发送" else "关闭",
                tint = if (value.isNotBlank()) Color.White else TextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

package com.xiaoxisi.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xiaoxisi.ui.theme.Beige
import com.xiaoxisi.ui.theme.BeigeDark
import com.xiaoxisi.ui.theme.Teal
import com.xiaoxisi.ui.theme.TextHint
import com.xiaoxisi.ui.theme.TextPrimary
import com.xiaoxisi.ui.theme.TextSecondary
import com.xiaoxisi.ui.theme.White
import com.xiaoxisi.ui.theme.XiaoxisiType

@Composable
fun VoiceInputBar(
    isRecording: Boolean,
    onRecordStart: () -> Unit,
    onRecordStop: () -> Unit,
    onToggleTextInput: () -> Unit,
    onClearHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Beige)
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
                        contentDescription = "添加",
                        tint = TextSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    "添加",
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
                        .clip(CircleShape)
                        .background(if (isRecording) Color(0xFFD32F2F) else Beige)
                        .clickable {
                            if (isRecording) onRecordStop() else onRecordStart()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Mic,
                        contentDescription = if (isRecording) "松开发送" else "按住说话",
                        tint = if (isRecording) Color.White else Teal,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    if (isRecording) "松开发送" else "按住说话",
                    style = XiaoxisiType.labelSmall,
                    color = TextHint,
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
                        contentDescription = "键盘",
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
            .background(Beige)
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
            textStyle = XiaoxisiType.bodyLarge.copy(color = TextPrimary),
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
                .background(if (value.isNotBlank()) Teal else BeigeDark)
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

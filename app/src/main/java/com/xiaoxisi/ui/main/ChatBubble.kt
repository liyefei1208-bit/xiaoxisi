package com.xiaoxisi.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoxisi.domain.model.Message
import com.xiaoxisi.domain.model.MessageRole
import com.xiaoxisi.ui.theme.BrownBorder
import com.xiaoxisi.ui.theme.BrownDark
import com.xiaoxisi.ui.theme.BrownMedium
import com.xiaoxisi.ui.theme.Orange
import com.xiaoxisi.ui.theme.OrangeLight
import com.xiaoxisi.ui.theme.TextPrimary
import com.xiaoxisi.ui.theme.White
import com.xiaoxisi.ui.theme.XiaoxisiType
import com.xiaoxisi.ui.theme.orangeGradient

@Composable
fun ChatBubble(message: Message) {
    when (message.role) {
        MessageRole.USER -> UserBubble(message)
        MessageRole.ASSISTANT -> AIBubble(message)
        else -> {}
    }
}

@Composable
private fun UserBubble(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 6.dp
            ),
            color = Color.Transparent,
            shadowElevation = 0.dp
        ) {
            Box(
                modifier = Modifier
                    .background(orangeGradient(), RoundedCornerShape(16.dp))
                    .then(
                        Modifier.background(
                            Color.Transparent,
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = 16.dp,
                                bottomEnd = 6.dp
                            )
                        )
                    )
            ) {
                Text(
                    text = message.content,
                    style = XiaoxisiType.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun AIBubble(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(orangeGradient()),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "希",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 6.dp,
                bottomEnd = 16.dp
            ),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, BrownBorder),
            shadowElevation = 0.dp
        ) {
            Text(
                text = message.content,
                style = XiaoxisiType.bodyLarge,
                color = BrownDark,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

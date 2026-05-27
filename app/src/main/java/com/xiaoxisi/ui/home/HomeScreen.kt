package com.xiaoxisi.ui.home

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoxisi.ui.theme.BrownBorder
import com.xiaoxisi.ui.theme.BrownDark
import com.xiaoxisi.ui.theme.BrownMedium
import com.xiaoxisi.ui.theme.GreenAccent
import com.xiaoxisi.ui.theme.GreenLight
import com.xiaoxisi.ui.theme.Orange
import com.xiaoxisi.ui.theme.OrangeLight
import com.xiaoxisi.ui.theme.TextHint
import com.xiaoxisi.ui.theme.TextPrimary
import com.xiaoxisi.ui.theme.TextSecondary
import com.xiaoxisi.ui.theme.WarmWhite
import com.xiaoxisi.ui.theme.White
import com.xiaoxisi.ui.theme.XiaoxisiType
import com.xiaoxisi.ui.theme.orangeGradient
import com.xiaoxisi.ui.theme.orangeGreetingGradient
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class QuickAction(
    val icon: ImageVector,
    val label: String,
    val color: Color,
    val bg: Color,
)

private val QuickActions = listOf(
    QuickAction(Icons.Filled.Phone, "打电话", Color(0xFFFF6B2B), Color(0xFFFFF0E5)),
    QuickAction(Icons.AutoMirrored.Filled.Chat, "发消息", Color(0xFF00B898), Color(0xFFE5F9F5)),
    QuickAction(Icons.Filled.Cloud, "看天气", Color(0xFF4A90D9), Color(0xFFEAF2FD)),
    QuickAction(Icons.Filled.Tv, "看视频", Color(0xFFC044A0), Color(0xFFFAE5F6)),
    QuickAction(Icons.Filled.Camera, "拍照片", Color(0xFFFFB347), Color(0xFFFFF6E5)),
    QuickAction(Icons.Filled.Favorite, "健康助手", Color(0xFFE53935), Color(0xFFFDECEC)),
    QuickAction(Icons.Filled.Place, "找地方", Color(0xFF43A047), Color(0xFFE8F5E9)),
    QuickAction(Icons.AutoMirrored.Filled.VolumeUp, "朗读文字", Color(0xFF7C5CBF), Color(0xFFF0EBF9)),
)

private data class RecentChat(
    val text: String,
    val time: String,
    val reply: String,
)

private val SampleRecentChats = listOf(
    RecentChat(
        text = "小希司，帮我给阿囡发条消息说今朝要晚点回去",
        time = "上午 9:12",
        reply = "好的，已经帮侬发给阿囡了！"
    ),
    RecentChat(
        text = "今朝天气咋样？",
        time = "昨天",
        reply = "今朝绍兴晴，气温24度，适合出门。"
    ),
)

@Composable
fun HomeScreen(
    onNavigateToChat: () -> Unit = {},
) {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentTime = System.currentTimeMillis()
        }
    }

    val dateFormat = remember { SimpleDateFormat("M月d日 EEEE", Locale.CHINESE) }
    val dateStr = remember(currentTime) { dateFormat.format(Date(currentTime)) }
    val hour = remember(currentTime) { SimpleDateFormat("H", Locale.CHINESE).format(Date(currentTime)).toInt() }
    val greeting = when {
        hour < 6 -> "夜里好"
        hour < 11 -> "早上好"
        hour < 13 -> "中午好"
        hour < 18 -> "下午好"
        else -> "晚上好"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(WarmWhite),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }
        item {
            HomeHeader(dateStr = dateStr)
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            GreetingCard(greeting = greeting)
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            VoiceButton(isListening = false, onToggle = {})
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            QuickActionsSection(onActionClick = { onNavigateToChat() })
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            RecentChatsSection(onChatOpen = onNavigateToChat, chats = SampleRecentChats)
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun HomeHeader(dateStr: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
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
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "小希司",
                style = XiaoxisiType.headline,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dateStr,
                style = XiaoxisiType.caption,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun GreetingCard(greeting: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(orangeGreetingGradient())
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .padding(end = 0.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.15f)
            ) {}
        }
        Column {
            Text(
                text = "$greeting，老伯！",
                style = XiaoxisiType.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "今天想让小希司",
                style = XiaoxisiType.greeting,
                color = Color.White
            )
            Text(
                text = "帮侬做啥？",
                style = XiaoxisiType.greeting,
                color = Color.White
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
}

@Composable
private fun VoiceButton(
    isListening: Boolean,
    onToggle: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "voicePulse")
    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )
    val pulseAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha1"
    )
    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, delayMillis = 300),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse2"
    )
    val pulseAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, delayMillis = 300),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha2"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(96.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isListening) {
                Surface(
                    modifier = Modifier
                        .size(96.dp)
                        .scale(pulseScale1),
                    shape = CircleShape,
                    color = Orange.copy(alpha = pulseAlpha1)
                ) {}
                Surface(
                    modifier = Modifier
                        .size(96.dp)
                        .scale(pulseScale2),
                    shape = CircleShape,
                    color = Orange.copy(alpha = pulseAlpha2)
                ) {}
            }
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        if (isListening) Brush.linearGradient(listOf(Color(0xFFE55A1A), Orange))
                        else orangeGradient()
                    )
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Mic,
                    contentDescription = "按住说话",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = if (isListening) "正在听侬说……" else "按这里说话",
            style = XiaoxisiType.labelMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "支持绍兴话 · 普通话",
            style = XiaoxisiType.caption,
            color = TextSecondary
        )
    }
}

@Composable
private fun QuickActionsSection(
    onActionClick: (String) -> Unit
) {
    Column {
        Text(
            text = "常用功能",
            style = XiaoxisiType.title,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickActions.chunked(4).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { action ->
                        QuickActionCard(
                            action = action,
                            onClick = { onActionClick(action.label) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    action: QuickAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = action.bg
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = action.color.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        action.icon,
                        contentDescription = action.label,
                        tint = action.color,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = action.label,
                style = XiaoxisiType.labelSmall,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RecentChatsSection(
    onChatOpen: () -> Unit,
    chats: List<RecentChat>
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "最近对话",
                style = XiaoxisiType.title,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onChatOpen() }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "查看全部",
                    style = XiaoxisiType.labelSmall,
                    color = Orange
                )
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = Orange,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            chats.forEach { chat ->
                RecentChatCard(chat = chat, onClick = onChatOpen)
            }
        }
    }
}

@Composable
private fun RecentChatCard(
    chat: RecentChat,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BrownBorder),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = chat.text,
                    style = XiaoxisiType.labelMedium,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = chat.time,
                        style = XiaoxisiType.caption,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "小希司：${chat.reply}",
                style = XiaoxisiType.labelSmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

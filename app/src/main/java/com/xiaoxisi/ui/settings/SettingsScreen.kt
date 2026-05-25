package com.xiaoxisi.ui.settings

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoxisi.ui.theme.Beige
import com.xiaoxisi.ui.theme.BeigeDark
import com.xiaoxisi.ui.theme.Gold
import com.xiaoxisi.ui.theme.Teal
import com.xiaoxisi.ui.theme.TealDark
import com.xiaoxisi.ui.theme.TextPrimary
import com.xiaoxisi.ui.theme.TextSecondary
import com.xiaoxisi.ui.theme.White
import com.xiaoxisi.ui.theme.XiaoxisiType

data class DialectOption(
    val name: String,
    val emoji: String
)

data class VoiceType(
    val name: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    var speed by remember { mutableFloatStateOf(1.0f) }
    var volume by remember { mutableFloatStateOf(0.8f) }
    var selectedDialect by remember { mutableStateOf("绍兴话") }
    var hangzhouPref by remember { mutableStateOf(true) }

    val dialects = listOf(
        DialectOption("杭州话", "🌊"),
        DialectOption("绍兴话", "⛵"),
        DialectOption("普通话", "🏯"),
        DialectOption("其他方言", "🎭")
    )

    val voiceTypes = listOf(
        VoiceType("和蔼阿姨", "温柔亲切的老年女性声音"),
        VoiceType("慈祥爷爷", "温和稳重的老年男性声音"),
        VoiceType("标准女声", "清晰的普通话标准女声")
    )

    Scaffold(
        containerColor = Beige
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Beige)
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = TextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "方言与声音设置",
                    style = XiaoxisiType.headline,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(36.dp))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item { DialectSection(dialects, selectedDialect) { selectedDialect = it } }
                item { SpeedSlider(speed) { speed = it } }
                item { VolumeSlider(volume) { volume = it } }
                item { VoiceTypeSection(voiceTypes, selectedDialect) }
                item { ToggleSection(hangzhouPref) { hangzhouPref = it } }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = XiaoxisiType.title,
        color = TextPrimary,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun DialectSection(
    dialects: List<DialectOption>,
    selected: String,
    onSelect: (String) -> Unit
) {
    SectionTitle("选择方言")
    Spacer(modifier = Modifier.height(12.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            dialects.take(2).forEach { dialect ->
                DialectCard(
                    dialect = dialect,
                    selected = dialect.name == selected,
                    onClick = { onSelect(dialect.name) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            dialects.drop(2).forEach { dialect ->
                DialectCard(
                    dialect = dialect,
                    selected = dialect.name == selected,
                    onClick = { onSelect(dialect.name) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DialectCard(
    dialect: DialectOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Teal else White,
        shadowElevation = if (selected) 4.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dialect.emoji,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = dialect.name,
                style = XiaoxisiType.labelLarge,
                color = if (selected) Color.White else TextPrimary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun SpeedSlider(speed: Float, onValueChange: (Float) -> Unit) {
    SectionTitle("语音语速")
    Spacer(modifier = Modifier.height(8.dp))
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("慢", style = XiaoxisiType.bodyLarge, color = TextSecondary)
                Text(
                    "%.1fx".format(speed.coerceIn(0.5f, 2.0f)),
                    style = XiaoxisiType.headline,
                    color = Teal
                )
                Text("快", style = XiaoxisiType.bodyLarge, color = TextSecondary)
            }
            Slider(
                value = speed,
                onValueChange = onValueChange,
                valueRange = 0.5f..2.0f,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Teal,
                    activeTrackColor = Teal,
                    inactiveTrackColor = BeigeDark
                )
            )
        }
    }
}

@Composable
private fun VolumeSlider(volume: Float, onValueChange: (Float) -> Unit) {
    SectionTitle("语音音量")
    Spacer(modifier = Modifier.height(8.dp))
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("🔈", fontSize = 24.sp)
                Text(
                    "%d%%".format((volume * 100).toInt()),
                    style = XiaoxisiType.headline,
                    color = Teal
                )
                Text("🔊", fontSize = 24.sp)
            }
            Slider(
                value = volume,
                onValueChange = onValueChange,
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Teal,
                    activeTrackColor = Teal,
                    inactiveTrackColor = BeigeDark
                )
            )
        }
    }
}

@Composable
private fun VoiceTypeSection(voiceTypes: List<VoiceType>, currentDialect: String) {
    SectionTitle("声音类型")
    Spacer(modifier = Modifier.height(8.dp))
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        voiceTypes.forEachIndexed { index, voice ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (index == 0) Teal else BeigeDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (index == 0) "✓" else "${index + 1}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (index == 0) Color.White else TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(voice.name, style = XiaoxisiType.labelLarge, color = TextPrimary)
                        Text(voice.description, style = XiaoxisiType.bodySmall, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun ToggleSection(hangzhouPref: Boolean, onToggle: (Boolean) -> Unit) {
    SectionTitle("偏好设置")
    Spacer(modifier = Modifier.height(8.dp))
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "杭州话偏好",
                    style = XiaoxisiType.labelLarge,
                    color = TextPrimary
                )
                Text(
                    "优先使用杭州方言回复",
                    style = XiaoxisiType.bodySmall,
                    color = TextSecondary
                )
            }
            Switch(
                checked = hangzhouPref,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Teal,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = BeigeDark
                )
            )
        }
    }
}

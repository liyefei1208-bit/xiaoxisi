package com.xiaoxisi.ui.settings

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoxisi.ui.theme.BrownBorder
import com.xiaoxisi.ui.theme.BrownDark
import com.xiaoxisi.ui.theme.BrownMedium
import com.xiaoxisi.ui.theme.Cream
import com.xiaoxisi.ui.theme.GreenAccent
import com.xiaoxisi.ui.theme.GreenLight
import com.xiaoxisi.ui.theme.Orange
import com.xiaoxisi.ui.theme.OrangeLight
import com.xiaoxisi.ui.theme.OrangeSurface
import com.xiaoxisi.ui.theme.TextPrimary
import com.xiaoxisi.ui.theme.TextSecondary
import com.xiaoxisi.ui.theme.WarmWhite
import com.xiaoxisi.ui.theme.White
import com.xiaoxisi.ui.theme.XiaoxisiType
import com.xiaoxisi.ui.theme.greenGradient
import com.xiaoxisi.ui.theme.orangeGradient

private data class FamilyContact(
    val name: String,
    val role: String,
    val phone: String,
)

private val SampleContacts = listOf(
    FamilyContact("阿囡", "女儿", "138****5566"),
    FamilyContact("阿囝", "儿子", "139****7788"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var selectedFontIdx by remember { mutableIntStateOf(0) }
    val fontSizes = listOf("大", "特大")

    var selectedDialect by remember { mutableStateOf("绍兴话") }
    val dialects = listOf("绍兴话", "杭州话", "普通话")

    var voiceSpeed by remember { mutableFloatStateOf(0.5f) }
    val speedLabel = when {
        voiceSpeed < 0.33f -> "慢"
        voiceSpeed < 0.66f -> "适中"
        else -> "快"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(WarmWhite),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "设置",
                style = XiaoxisiType.headline,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            SettingsCard {
                SectionTitle("字体大小")
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    fontSizes.forEachIndexed { idx, size ->
                        val isSelected = idx == selectedFontIdx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .then(
                                    if (isSelected) Modifier.background(brush = orangeGradient())
                                    else Modifier.background(Cream)
                                )
                                .clickable { selectedFontIdx = idx },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = size,
                                style = XiaoxisiType.labelLarge,
                                color = if (isSelected) Color.White else BrownMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        item {
            SettingsCard {
                SectionTitle("方言选择")
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dialects.forEach { dialect ->
                        val isSelected = dialect == selectedDialect
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .then(
                                    if (isSelected) Modifier.background(brush = greenGradient())
                                    else Modifier.background(Cream)
                                )
                                .clickable { selectedDialect = dialect },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dialect,
                                style = XiaoxisiType.labelMedium,
                                color = if (isSelected) Color.White else BrownMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        item {
            SettingsCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle("说话速度")
                    Text(
                        text = speedLabel,
                        style = XiaoxisiType.labelMedium,
                        color = Orange,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = voiceSpeed,
                    onValueChange = { voiceSpeed = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = Orange,
                        activeTrackColor = Orange,
                        inactiveTrackColor = Cream
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("慢", style = XiaoxisiType.caption, color = TextSecondary)
                    Text("快", style = XiaoxisiType.caption, color = TextSecondary)
                }
            }
        }

        item {
            SettingsCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle("家人联系人")
                    Text(
                        text = "+ 添加",
                        style = XiaoxisiType.labelMedium,
                        color = Orange,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                SampleContacts.forEach { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(orangeGradient()),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = contact.name.take(1),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                buildString {
                                    append(contact.name)
                                    append("  ")
                                    append(contact.role)
                                },
                                style = XiaoxisiType.labelMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = contact.phone,
                                style = XiaoxisiType.caption,
                                color = TextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Cream)
                                .clickable {},
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Phone,
                                contentDescription = "打电话",
                                tint = Orange,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "小希司",
                    style = XiaoxisiType.title,
                    color = Orange,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "版本 1.0.0 · 绍兴方言定制版",
                    style = XiaoxisiType.caption,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BrownBorder),
        shadowElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = XiaoxisiType.labelLarge,
        color = TextPrimary,
        fontWeight = FontWeight.Bold
    )
}

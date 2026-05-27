package com.xiaoxisi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoxisi.ui.home.HomeScreen
import com.xiaoxisi.ui.main.ChatScreen
import com.xiaoxisi.ui.settings.SettingsScreen
import com.xiaoxisi.ui.theme.BrownBorder
import com.xiaoxisi.ui.theme.Orange
import com.xiaoxisi.ui.theme.TextHint
import com.xiaoxisi.ui.theme.XiaoxisiType
import com.xiaoxisi.ui.theme.White
import com.xiaoxisi.ui.theme.WarmWhite
import com.xiaoxisi.ui.theme.XiaoxisiTheme
import dagger.hilt.android.AndroidEntryPoint

import android.graphics.Rect
import androidx.compose.runtime.DisposableEffect

private data class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val index: Int
)

private val NavItems = listOf(
    NavItem("首页", Icons.Filled.Home, Icons.Outlined.Home, 0),
    NavItem("对话", Icons.Filled.ChatBubbleOutline, Icons.Outlined.ChatBubbleOutline, 1),
    NavItem("设置", Icons.Filled.Settings, Icons.Outlined.Settings, 2),
)

@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XiaoxisiTheme {
                var currentTab by rememberSaveable { mutableIntStateOf(0) }
                val view = LocalView.current
                val density = LocalDensity.current
                var isKeyboardVisible by rememberSaveable { mutableStateOf(false) }

                DisposableEffect(view) {
                    val listener = android.view.ViewTreeObserver.OnGlobalLayoutListener {
                        val rect = Rect()
                        view.getWindowVisibleDisplayFrame(rect)
                        val screenHeight = view.rootView.height
                        val keypadHeight = screenHeight - rect.bottom
                        isKeyboardVisible = keypadHeight > screenHeight * 0.15
                    }
                    view.viewTreeObserver.addOnGlobalLayoutListener(listener)
                    onDispose {
                        view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = WarmWhite
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            AnimatedContent(
                                targetState = currentTab,
                                transitionSpec = {
                                    if (targetState > initialState) {
                                        (fadeIn() + slideInHorizontally { it / 4 }) togetherWith
                                            (fadeOut() + slideOutHorizontally { -it / 4 })
                                    } else {
                                        (fadeIn() + slideInHorizontally { -it / 4 }) togetherWith
                                            (fadeOut() + slideOutHorizontally { it / 4 })
                                    }
                                },
                                label = "tabTransition"
                            ) { tab ->
                                when (tab) {
                                    0 -> HomeScreen(
                                        onNavigateToChat = { currentTab = 1 }
                                    )
                                    1 -> ChatScreen()
                                    2 -> SettingsScreen()
                                }
                            }
                        }
                        if (!isKeyboardVisible) {
                            BottomNavBar(
                                selectedTab = currentTab,
                                onTabSelected = { currentTab = it }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItems.forEach { item ->
                val isSelected = selectedTab == item.index
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(item.index) }
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .then(
                                if (isSelected) Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Orange)
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            tint = if (isSelected) Color.White else TextHint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = item.label,
                        style = XiaoxisiType.labelSmall,
                        color = if (isSelected) Orange else TextHint,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
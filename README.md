# 小希司 (xiaoxisi)

面向老年用户的 AI 语音助手 Android App，支持绍兴方言语音输入 + 普通话语音反馈，帮助父母完成手机操作。

## 核心功能

- **方言语音输入**：按住麦克风说绍兴方言，云端 ASR 转文字
- **文字输入**：键盘输入方言文本（如"侬好"、"做啥西"）
- **AI 意图理解**：DeepSeek LLM 理解用户意图，自动执行操作
- **普通话播报**：系统 TTS 语音 + 大字气泡文字双通道反馈
- **系统操作**：WiFi、亮度、音量、字体调节
- **电话操作**：语音拨号、挂断
- **App 管理**：语音打开/搜索/卸载应用

## 技术栈

| 层级 | 技术 |
|------|------|
| 语言 | Kotlin |
| 最低 SDK | API 29 (Android 10) |
| UI | Jetpack Compose + Material3 |
| 架构 | MVVM + Repository |
| DI | Hilt |
| 数据库 | Room |
| 网络 | Retrofit + OkHttp |
| 协程 | Coroutines + Flow |
| ASR | 科大讯飞语音识别 API |
| LLM | DeepSeek API |
| TTS | Android 内置 TTS |

## 项目结构

```
app/src/main/java/com/xiaoxisi/
├── App.kt                  # Application，初始化 Hilt
├── MainActivity.kt         # 单 Activity，Compose 入口
├── core/
│   ├── di/                 # Hilt 依赖注入模块
│   ├── config/             # API 配置、Prompt 模板
│   └── util/               # 扩展函数、权限工具
├── data/
│   ├── local/              # Room 数据库、DAO、Entity
│   └── remote/             # Retrofit API、DTO
├── domain/
│   ├── model/              # 域模型（Intent、Action 等）
│   └── usecase/            # 业务用例
├── voice/
│   ├── AudioRecorder.kt    # 录音管理
│   ├── AsrEngine.kt        # 讯飞 ASR 引擎
│   └── TtsEngine.kt        # 系统 TTS 引擎
├── nlu/
│   ├── LlmIntentClassifier.kt  # LLM 意图分类
│   ├── LocalIntentMatcher.kt   # 离线关键词匹配
│   ├── EntityExtractor.kt      # 实体提取
│   └── DialogueManager.kt      # 对话管理
├── automation/
│   ├── GuideAccessibilityService.kt  # 无障碍服务
│   └── action/                     # 操作执行器
└── ui/
    ├── main/               # 主界面、聊天气泡、输入栏
    ├── settings/           # 设置页面
    └── theme/              # Material3 主题、大字字体
```

## 快速开始

### 环境要求

- Android Studio Hedgehog (2023.1.1)+
- JDK 17
- Android SDK 35
- Gradle 8.2

### 配置 API 密钥

在 `local.properties` 中添加：

```properties
xiaoxisi.asr.appId=your_iflytek_app_id
xiaoxisi.asr.apiKey=your_iflytek_api_key
xiaoxisi.asr.apiSecret=your_iflytek_api_secret

xiaoxisi.llm.provider=deepseek
xiaoxisi.llm.apiKey=sk-your_deepseek_key
xiaoxisi.llm.baseUrl=https://api.deepseek.com/v1
```

未配置密钥时会自动降级为离线关键词匹配模式（支持 WiFi/亮度/音量/电话/App 等简单指令）。

### 构建

```bash
./gradlew assembleDebug
```

### 安装

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 权限

首次运行需授予以下权限：
- 麦克风（录音）
- 电话（拨号）
- 修改系统设置（亮度/音量）
- 悬浮窗（引导浮层）
- 无障碍服务（屏幕引导）

## 对话触发语示例

| 功能 | 绍兴方言示例 | 普通话含义 |
|------|-------------|-----------|
| WiFi | "WiFi 连勿上哉" | WiFi 连不上了 |
| 亮度 | "屏幕忒暗" | 屏幕太暗 |
| 音量 | "声音忒轻" | 声音太小 |
| 字体 | "字忒小，看勿清" | 字太小看不清 |
| 拨号 | "打电话畀阿明" | 打电话给阿明 |
| 挂断 | "挂脱算哉" | 挂了吧 |
| 打开App | "打开微信" | 打开微信 |
| 安装 | "装个抖音" | 安装抖音 |

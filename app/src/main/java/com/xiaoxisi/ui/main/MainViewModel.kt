package com.xiaoxisi.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xiaoxisi.automation.action.ActionExecutor
import com.xiaoxisi.core.config.ApiConfig
import com.xiaoxisi.data.repository.MessageRepository
import com.xiaoxisi.domain.model.ConversationContext
import com.xiaoxisi.domain.model.Message
import com.xiaoxisi.domain.model.MessageRole
import com.xiaoxisi.domain.usecase.ProcessTextInputUseCase
import com.xiaoxisi.domain.usecase.ProcessVoiceInputUseCase
import com.xiaoxisi.nlu.DialogueManager
import com.xiaoxisi.nlu.LlmIntentClassifier
import com.xiaoxisi.voice.AudioRecorder
import com.xiaoxisi.voice.TtsEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val audioRecorder: AudioRecorder,
    private val processTextInput: ProcessTextInputUseCase,
    private val processVoiceInput: ProcessVoiceInputUseCase,
    private val actionExecutor: ActionExecutor,
    private val ttsEngine: TtsEngine,
    private val messageRepository: MessageRepository,
    private val dialogueManager: DialogueManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState(
        apiConfigured = ApiConfig.isFullyConfigured(),
        llmConfigured = ApiConfig.isLlmConfigured()
    ))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            messageRepository.getAllMessages().collect { messages ->
                _uiState.value = _uiState.value.copy(messages = messages)
            }
        }
    }

    fun startRecording() {
        if (!audioRecorder.hasPermission()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "需要录音权限，请在设置中允许"
            )
            return
        }

        val result = audioRecorder.startRecording()
        if (result.isSuccess) {
            _uiState.value = _uiState.value.copy(isRecording = true, errorMessage = null)
        } else {
            _uiState.value = _uiState.value.copy(
                errorMessage = "无法开始录音: ${result.exceptionOrNull()?.message}"
            )
        }
    }

    fun stopRecording() {
        _uiState.value = _uiState.value.copy(isRecording = false, isProcessing = true)

        val result = audioRecorder.stopRecording()
        result.onSuccess { audioData ->
            viewModelScope.launch {
                val context = dialogueManager.getContext()
                val voiceResult = processVoiceInput(audioData, context)

                voiceResult.onSuccess { data ->
                    handleAssistantResponse(
                        userText = data.recognizedText,
                        replyText = data.intentResult.replyText,
                        intent = data.intentResult.intent,
                        entities = data.intentResult.entities
                    )
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        errorMessage = error.message ?: "听勿清，再讲一遍试试？"
                    )
                }
            }
        }.onFailure {
            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                errorMessage = "录音出了点问题，再试一次"
            )
        }
    }

    fun cancelRecording() {
        audioRecorder.cancelRecording()
        _uiState.value = _uiState.value.copy(isRecording = false)
    }

    fun sendText(text: String) {
        _uiState.value = _uiState.value.copy(isProcessing = true)

        viewModelScope.launch {
            val userMessage = Message(
                role = MessageRole.USER,
                content = text
            )
            messageRepository.insertMessage(userMessage)
            dialogueManager.addMessage(userMessage)

            val context = dialogueManager.getContext()
            val result = processTextInput(text, context)

            handleAssistantResponse(
                userText = text,
                replyText = result.replyText,
                intent = result.intent,
                entities = result.entities
            )
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleAssistantResponse(
        userText: String,
        replyText: String,
        intent: com.xiaoxisi.domain.model.Intent,
        entities: Map<String, String>
    ) {
        val assistantMessage = Message(
            role = MessageRole.ASSISTANT,
            content = replyText,
            intent = intent.type.name,
            entities = entities
        )

        viewModelScope.launch {
            messageRepository.insertMessage(assistantMessage)
            dialogueManager.addMessage(assistantMessage)

            ttsEngine.speak(replyText)

            if (intent !is com.xiaoxisi.domain.model.Intent.Chat &&
                intent !is com.xiaoxisi.domain.model.Intent.Unknown) {
                actionExecutor.execute(intent)
            }

            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                errorMessage = null
            )
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            messageRepository.clearAll()
            dialogueManager.clear()
            _uiState.value = MainUiState()
        }
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class MainUiState(
    val messages: List<Message> = emptyList(),
    val isRecording: Boolean = false,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
    val apiConfigured: Boolean = false,
    val llmConfigured: Boolean = false
)

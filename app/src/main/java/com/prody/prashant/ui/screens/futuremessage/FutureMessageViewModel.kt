package com.prody.prashant.ui.screens.futuremessage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.FutureMessageDao
import com.prody.prashant.data.local.entity.FutureMessageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FutureMessageUiState(
    val deliveredMessages: List<FutureMessageEntity> = emptyList(),
    val pendingMessages: List<FutureMessageEntity> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class FutureMessageViewModel @Inject constructor(
    private val futureMessageDao: FutureMessageDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(FutureMessageUiState())
    val uiState: StateFlow<FutureMessageUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
        checkForDeliveredMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            combine(
                futureMessageDao.getDeliveredMessages(),
                futureMessageDao.getPendingMessages(),
                futureMessageDao.getUnreadCount()
            ) { delivered, pending, unread ->
                FutureMessageUiState(
                    deliveredMessages = delivered,
                    pendingMessages = pending,
                    unreadCount = unread,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun checkForDeliveredMessages() {
        viewModelScope.launch {
            val readyMessages = futureMessageDao.getMessagesReadyForDelivery(System.currentTimeMillis())
            readyMessages.forEach { message ->
                futureMessageDao.markAsDelivered(message.id)
            }
        }
    }

    fun markAsRead(messageId: Long) {
        viewModelScope.launch {
            futureMessageDao.markAsRead(messageId)
        }
    }
}

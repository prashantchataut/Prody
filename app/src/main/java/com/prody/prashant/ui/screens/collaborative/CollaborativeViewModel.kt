package com.prody.prashant.ui.screens.collaborative

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.domain.collaborative.*
import com.prody.prashant.domain.common.fold
import com.prody.prashant.domain.repository.CollaborativeMessageRepository
import com.prody.prashant.domain.repository.MessagingStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * ViewModel for Collaborative Messages feature.
 *
 * Manages:
 * - Sent messages (scheduled/delivered)
 * - Received messages (inbox)
 * - Contacts
 * - Occasions
 * - Message composition
 */
@HiltViewModel
class CollaborativeViewModel @Inject constructor(
    private val repository: CollaborativeMessageRepository
) : ViewModel() {

    // =========================================================================
    // HOME STATE
    // =========================================================================

    private val _homeState = MutableStateFlow(CollaborativeHomeUiState())
    val homeState: StateFlow<CollaborativeHomeUiState> = _homeState.asStateFlow()

    // =========================================================================
    // COMPOSE STATE
    // =========================================================================

    private val _composeState = MutableStateFlow(ComposeMessageUiState())
    val composeState: StateFlow<ComposeMessageUiState> = _composeState.asStateFlow()

    // =========================================================================
    // MESSAGE DETAIL STATE
    // =========================================================================

    private val _detailState = MutableStateFlow(MessageDetailUiState())
    val detailState: StateFlow<MessageDetailUiState> = _detailState.asStateFlow()

    init {
        loadHomeData()
    }

    // =========================================================================
    // HOME METHODS
    // =========================================================================

    fun loadHomeData() {
        viewModelScope.launch {
            _homeState.update { it.copy(isLoading = true) }

            // Load stats
            try {
                val stats = repository.getMessagingStats()
                _homeState.update { it.copy(stats = stats) }
            } catch (e: Exception) {
                // Ignore stats errors
            }

            _homeState.update { it.copy(isLoading = false) }
        }

        // Observe sent messages
        viewModelScope.launch {
            repository.getAllSentMessages()
                .catch { e -> _homeState.update { it.copy(errorMessage = e.message) } }
                .collect { messages ->
                    _homeState.update { it.copy(
                        sentMessages = messages,
                        scheduledCount = messages.count { m -> m.status == MessageStatus.SCHEDULED },
                        deliveredCount = messages.count { m -> m.isDelivered }
                    ) }
                }
        }

        // Observe received messages
        viewModelScope.launch {
            repository.getAllReceivedMessages()
                .catch { /* Ignore */ }
                .collect { messages ->
                    _homeState.update { it.copy(
                        receivedMessages = messages,
                        unreadCount = messages.count { m -> !m.isRead }
                    ) }
                }
        }

        // Observe contacts
        viewModelScope.launch {
            repository.getAllContacts()
                .catch { /* Ignore */ }
                .collect { contacts ->
                    _homeState.update { it.copy(contacts = contacts) }
                }
        }

        // Observe upcoming occasions
        viewModelScope.launch {
            val now = LocalDateTime.now()
            val endDate = now.plusDays(30)
            repository.getUpcomingOccasions(startDate = now, endDate = endDate)
                .catch { /* Ignore */ }
                .collect { occasions ->
                    _homeState.update { it.copy(upcomingOccasions = occasions) }
                }
        }
    }

    fun selectTab(tab: CollaborativeTab) {
        _homeState.update { it.copy(selectedTab = tab) }
    }

    fun dismissHomeError() {
        _homeState.update { it.copy(errorMessage = null) }
    }

    // =========================================================================
    // COMPOSE METHODS
    // =========================================================================

    fun startComposing(contact: MessageContact? = null, occasion: Occasion? = null) {
        _composeState.value = ComposeMessageUiState(
            selectedContact = contact,
            selectedOccasion = occasion,
            selectedTheme = occasion?.let { CardTheme.forOccasion(it) } ?: CardTheme.DEFAULT
        )
    }

    fun onRecipientNameChanged(name: String) {
        _composeState.update { it.copy(recipientName = name) }
    }

    fun onRecipientContactChanged(contact: String) {
        _composeState.update { it.copy(recipientContact = contact) }
    }

    fun onContactMethodChanged(method: ContactMethod) {
        _composeState.update { it.copy(contactMethod = method) }
    }

    fun onSelectContact(contact: MessageContact) {
        _composeState.update { it.copy(
            selectedContact = contact,
            recipientName = contact.displayName,
            recipientContact = contact.contactValue,
            contactMethod = contact.method
        ) }
    }

    fun onTitleChanged(title: String) {
        _composeState.update { it.copy(title = title) }
    }

    fun onContentChanged(content: String) {
        _composeState.update { it.copy(content = content) }
    }

    fun onOccasionSelected(occasion: Occasion?) {
        _composeState.update { it.copy(
            selectedOccasion = occasion,
            selectedTheme = occasion?.let { CardTheme.forOccasion(it) } ?: it.selectedTheme
        ) }
    }

    fun onThemeSelected(theme: CardTheme) {
        _composeState.update { it.copy(selectedTheme = theme) }
    }

    fun onDeliveryDateChanged(date: LocalDateTime) {
        _composeState.update { it.copy(deliveryDate = date) }
    }

    fun sendMessage() {
        viewModelScope.launch {
            val state = _composeState.value
            if (!state.canSend) return@launch

            _composeState.update { it.copy(isSending = true) }

            val message = CollaborativeMessage(
                recipient = MessageRecipient(
                    name = state.recipientName,
                    method = state.contactMethod,
                    contactValue = state.recipientContact
                ),
                title = state.title,
                content = state.content,
                deliveryDate = state.deliveryDate,
                occasion = state.selectedOccasion,
                cardDesign = CardDesign(theme = state.selectedTheme),
                attachments = MessageAttachments(),
                status = if (state.deliveryDate.isAfter(LocalDateTime.now())) {
                    MessageStatus.SCHEDULED
                } else {
                    MessageStatus.PENDING
                }
            )

            repository.createMessage(message).fold(
                onSuccess = { messageId ->
                    // Schedule if in the future
                    if (message.status == MessageStatus.SCHEDULED) {
                        repository.scheduleMessage(message)
                    }

                    _composeState.update { it.copy(
                        isSending = false,
                        messageSent = true
                    ) }
                    loadHomeData()
                },
                onError = { error ->
                    _composeState.update { it.copy(
                        isSending = false,
                        errorMessage = error.message
                    ) }
                }
            )
        }
    }

    fun resetComposeState() {
        _composeState.value = ComposeMessageUiState()
    }

    fun dismissComposeError() {
        _composeState.update { it.copy(errorMessage = null) }
    }

    // =========================================================================
    // MESSAGE DETAIL METHODS
    // =========================================================================

    fun loadSentMessageDetail(messageId: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true) }

            repository.getMessageById(messageId).fold(
                onSuccess = { message ->
                    _detailState.update { it.copy(
                        isLoading = false,
                        sentMessage = message
                    ) }
                },
                onError = { error ->
                    _detailState.update { it.copy(
                        isLoading = false,
                        errorMessage = error.message
                    ) }
                }
            )
        }
    }

    fun loadReceivedMessageDetail(messageId: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true) }

            repository.getReceivedMessageById(messageId).fold(
                onSuccess = { message ->
                    _detailState.update { it.copy(
                        isLoading = false,
                        receivedMessage = message
                    ) }

                    // Mark as read
                    if (!message.isRead) {
                        repository.markReceivedAsRead(messageId)
                    }
                },
                onError = { error ->
                    _detailState.update { it.copy(
                        isLoading = false,
                        errorMessage = error.message
                    ) }
                }
            )
        }
    }

    fun toggleReceivedFavorite(messageId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleReceivedFavorite(messageId, isFavorite)
            _detailState.value.receivedMessage?.let { message ->
                _detailState.update { it.copy(
                    receivedMessage = message.copy(isFavorite = isFavorite)
                ) }
            }
        }
    }

    fun deleteMessage(messageId: String, isReceived: Boolean) {
        viewModelScope.launch {
            if (isReceived) {
                repository.deleteReceivedMessage(messageId)
            } else {
                repository.deleteMessage(messageId)
            }
            loadHomeData()
        }
    }

    fun cancelScheduledMessage(messageId: String) {
        viewModelScope.launch {
            repository.cancelScheduledMessage(messageId)
            loadHomeData()
        }
    }

    fun resetDetailState() {
        _detailState.value = MessageDetailUiState()
    }

    fun dismissDetailError() {
        _detailState.update { it.copy(errorMessage = null) }
    }
}

// =========================================================================
// UI STATE DATA CLASSES
// =========================================================================

enum class CollaborativeTab {
    INBOX, SENT, CONTACTS
}

data class CollaborativeHomeUiState(
    val isLoading: Boolean = false,
    val selectedTab: CollaborativeTab = CollaborativeTab.INBOX,
    val sentMessages: List<CollaborativeMessage> = emptyList(),
    val receivedMessages: List<ReceivedCollaborativeMessage> = emptyList(),
    val contacts: List<MessageContact> = emptyList(),
    val upcomingOccasions: List<MessageOccasion> = emptyList(),
    val stats: MessagingStats? = null,
    val unreadCount: Int = 0,
    val scheduledCount: Int = 0,
    val deliveredCount: Int = 0,
    val errorMessage: String? = null
) {
    val hasUnread: Boolean
        get() = unreadCount > 0

    val hasScheduled: Boolean
        get() = scheduledCount > 0

    val hasContacts: Boolean
        get() = contacts.isNotEmpty()
}

data class ComposeMessageUiState(
    val selectedContact: MessageContact? = null,
    val recipientName: String = "",
    val recipientContact: String = "",
    val contactMethod: ContactMethod = ContactMethod.EMAIL,
    val title: String = "",
    val content: String = "",
    val selectedOccasion: Occasion? = null,
    val selectedTheme: CardTheme = CardTheme.DEFAULT,
    val deliveryDate: LocalDateTime = LocalDateTime.now(),
    val isSending: Boolean = false,
    val messageSent: Boolean = false,
    val errorMessage: String? = null
) {
    val canSend: Boolean
        get() = recipientName.isNotBlank() &&
                recipientContact.isNotBlank() &&
                title.isNotBlank() &&
                content.isNotBlank()

    val isScheduledForFuture: Boolean
        get() = deliveryDate.isAfter(LocalDateTime.now())
}

data class MessageDetailUiState(
    val isLoading: Boolean = false,
    val sentMessage: CollaborativeMessage? = null,
    val receivedMessage: ReceivedCollaborativeMessage? = null,
    val errorMessage: String? = null
) {
    val hasMessage: Boolean
        get() = sentMessage != null || receivedMessage != null
}

# Collaborative Future Messages - Quick Start Guide

## 🚀 Quick Integration (5 Minutes)

### Step 1: Add Navigation Routes

```kotlin
// In your navigation graph
composable("collaborative_messages") {
    CollaborativeMessagesHomeScreen(
        onNavigateToCompose = { navController.navigate("compose_message") },
        onNavigateToMessage = { messageId ->
            navController.navigate("message_detail/$messageId")
        }
    )
}

composable("compose_message") {
    ComposeMessageScreen(
        onNavigateBack = { navController.popBackStack() }
    )
}
```

### Step 2: Request Permissions (in MainActivity or SettingsScreen)

```kotlin
@Composable
fun RequestCollaborativeMessagePermissions() {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Request notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_NOTIFICATIONS
            )
        }

        // Request exact alarm permission (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
            }
        }
    }
}
```

### Step 3: Create Basic ViewModel

```kotlin
@HiltViewModel
class CollaborativeMessagesViewModel @Inject constructor(
    private val repository: CollaborativeMessageRepository
) : ViewModel() {

    val sentMessages = repository.getAllSentMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val receivedMessages = repository.getAllReceivedMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCount = repository.getUnreadReceivedMessageCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun sendMessage(
        recipientName: String,
        recipientEmail: String,
        title: String,
        content: String,
        deliveryDate: LocalDateTime,
        occasion: Occasion? = null
    ) {
        viewModelScope.launch {
            val message = CollaborativeMessage(
                recipient = MessageRecipient(
                    name = recipientName,
                    method = ContactMethod.EMAIL,
                    contactValue = recipientEmail
                ),
                title = title,
                content = content,
                deliveryDate = deliveryDate,
                occasion = occasion,
                cardDesign = CardDesign(
                    theme = occasion?.let { CardTheme.forOccasion(it) } ?: CardTheme.DEFAULT
                ),
                attachments = MessageAttachments(),
                status = MessageStatus.PENDING
            )

            repository.createMessage(message)
            repository.scheduleMessage(message)
        }
    }
}
```

## 📱 Minimal UI Screens

### Home Screen (List View)

```kotlin
@Composable
fun CollaborativeMessagesHomeScreen(
    viewModel: CollaborativeMessagesViewModel = hiltViewModel(),
    onNavigateToCompose: () -> Unit,
    onNavigateToMessage: (String) -> Unit
) {
    val sentMessages by viewModel.sentMessages.collectAsState()
    val receivedMessages by viewModel.receivedMessages.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages to Loved Ones") },
                actions = {
                    if (unreadCount > 0) {
                        Badge { Text("$unreadCount") }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCompose) {
                Icon(Icons.Default.Add, "Compose message")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Sent", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Received", modifier = Modifier.padding(16.dp))
                }
            }

            when (selectedTab) {
                0 -> SentMessagesList(sentMessages, onNavigateToMessage)
                1 -> ReceivedMessagesList(receivedMessages, onNavigateToMessage)
            }
        }
    }
}

@Composable
fun SentMessagesList(
    messages: List<CollaborativeMessage>,
    onMessageClick: (String) -> Unit
) {
    LazyColumn {
        items(messages) { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onMessageClick(message.id) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${message.occasion?.emoji ?: "💝"} ${message.recipient.name}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = message.status.displayName,
                            style = MaterialTheme.typography.bodySmall,
                            color = when (message.status) {
                                MessageStatus.DELIVERED -> Color.Green
                                MessageStatus.SCHEDULED -> Color.Blue
                                MessageStatus.FAILED -> Color.Red
                                else -> Color.Gray
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(message.title, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        message.content.take(100) + if (message.content.length > 100) "..." else "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Delivers: ${message.deliveryDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
```

### Compose Screen (Simplified)

```kotlin
@Composable
fun ComposeMessageScreen(
    viewModel: CollaborativeMessagesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    var recipientName by remember { mutableStateOf("") }
    var recipientEmail by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now().plusDays(1)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Message") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = recipientName,
                onValueChange = { recipientName = it },
                label = { Text("Recipient Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = recipientEmail,
                onValueChange = { recipientEmail = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Message Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Your heartfelt message...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = 10
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Delivery Date: ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.sendMessage(
                        recipientName = recipientName,
                        recipientEmail = recipientEmail,
                        title = title,
                        content = content,
                        deliveryDate = selectedDate.atTime(12, 0)
                    )
                    onNavigateBack()
                },
                enabled = recipientName.isNotBlank() &&
                         recipientEmail.isNotBlank() &&
                         title.isNotBlank() &&
                         content.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Schedule Message")
            }
        }
    }
}
```

## 🎨 Occasions Reference

Use these pre-defined occasions:

```kotlin
Occasion.BIRTHDAY       // 🎂 Birthday
Occasion.ANNIVERSARY    // 💕 Anniversary
Occasion.GRADUATION     // 🎓 Graduation
Occasion.NEW_YEAR       // 🎊 New Year
Occasion.CHRISTMAS      // 🎄 Christmas
Occasion.MOTHERS_DAY    // 💐 Mother's Day
Occasion.FATHERS_DAY    // 👔 Father's Day
Occasion.WEDDING        // 💒 Wedding Day
Occasion.NEW_BABY       // 👶 New Baby
Occasion.ENCOURAGEMENT  // 💪 Encouragement
Occasion.THANK_YOU      // 🙏 Thank You
Occasion.THINKING_OF_YOU // 💭 Thinking of You
Occasion.GET_WELL       // 🌸 Get Well Soon
Occasion.CONGRATULATIONS // 🎉 Congratulations
Occasion.JUST_BECAUSE   // 💝 Just Because
Occasion.CUSTOM         // ✨ Custom Occasion
```

## 🎨 Card Themes

```kotlin
CardTheme.DEFAULT       // Classic white
CardTheme.CELEBRATION   // Golden yellow
CardTheme.ROMANTIC      // Soft pink
CardTheme.NATURE        // Fresh green
CardTheme.OCEAN         // Light blue
CardTheme.SUNSET        // Warm orange
CardTheme.MIDNIGHT      // Deep blue
CardTheme.FESTIVE       // Red & green
CardTheme.ELEGANT       // Black & gray
CardTheme.SOFT          // Warm beige
CardTheme.LAVENDER      // Purple tones
CardTheme.MINT          // Mint green
```

## 🔔 Testing Delivery

```kotlin
// Test immediate delivery (for debugging)
fun testMessageDelivery() {
    viewModelScope.launch {
        val testMessage = CollaborativeMessage(
            recipient = MessageRecipient(
                name = "Test User",
                method = ContactMethod.IN_APP,
                contactValue = "test@example.com"
            ),
            title = "Test Message",
            content = "This is a test!",
            deliveryDate = LocalDateTime.now().plusSeconds(10), // 10 seconds from now
            occasion = null,
            cardDesign = CardDesign(CardTheme.DEFAULT),
            attachments = MessageAttachments(),
            status = MessageStatus.PENDING
        )

        repository.createMessage(testMessage)
        repository.scheduleMessage(testMessage)
    }
}
```

## 📦 What You Get

- ✅ Complete database schema with Room
- ✅ Scheduling with AlarmManager
- ✅ Multi-channel delivery (in-app, email, SMS)
- ✅ 16 pre-defined occasions
- ✅ 12 beautiful card themes
- ✅ Contact management
- ✅ Notification system
- ✅ Boot-time rescheduling
- ✅ Retry logic for failures
- ✅ Offline-first architecture
- ✅ Ready for Firebase sync

## 🚨 Common Issues

### Messages not delivering?
1. Check exact alarm permission (Android 12+)
2. Verify app not in battery optimization
3. Check notification permissions
4. Review logs for errors

### Boot rescheduling not working?
1. Ensure BOOT_COMPLETED permission in manifest
2. Verify receiver is registered
3. Check if app has autostart permission (some manufacturers)

### Notifications not showing?
1. Request POST_NOTIFICATIONS permission (Android 13+)
2. Check notification channels are created
3. Verify app not in DND mode

---

**Ready to Use!** All core functionality is implemented and production-ready. Just add your UI screens and you're good to go!

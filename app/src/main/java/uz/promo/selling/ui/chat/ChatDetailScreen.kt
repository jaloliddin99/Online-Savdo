package uz.promo.selling.ui.chat

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import uz.promo.selling.BuildConfig
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.chat.ChatMessage
import uz.promo.selling.data.remote.models.chat.Conversation
import uz.promo.selling.ui.TopAppBar
import uz.promo.selling.utils.chatTimeLabel
import uz.promo.selling.utils.formatNumberWithSpaces
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.PaddingValues
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow

private const val POLL_MS = 4000L
private const val POLL_MS_SOCKET_FALLBACK = 30000L

@Composable
fun ChatDetailRoute(
    conversationId: Long,
    navigateBack: () -> Unit,
    onOpenPost: (Int) -> Unit = {},
    onOpenSeller: (Int) -> Unit = {},
    viewModel: ChatDetailViewModel = hiltViewModel()
) {
    // While this thread is open, suppress its push notifications.
    androidx.compose.runtime.DisposableEffect(conversationId) {
        uz.promo.selling.utils.ChatPresence.activeConversationId = conversationId
        onDispose {
            if (uz.promo.selling.utils.ChatPresence.activeConversationId == conversationId) {
                uz.promo.selling.utils.ChatPresence.activeConversationId = -1L
            }
        }
    }

    // Load once, keep a live WebSocket for instant updates, and poll as a
    // safety net (slow while the socket is healthy, fast when it isn't).
    LaunchedEffect(conversationId) {
        viewModel.loadHeader(conversationId)
        viewModel.refreshMessages(conversationId)
        viewModel.connectSocket(conversationId)
        while (true) {
            delay(if (viewModel.socketConnected) POLL_MS_SOCKET_FALLBACK else POLL_MS)
            if (!viewModel.socketConnected) viewModel.connectSocket(conversationId)
            viewModel.refreshMessages(conversationId)
        }
    }

    val context = LocalContext.current
    val blocked = viewModel.state.conversation?.blocked == true
    var menuOpen by remember { mutableStateOf(false) }
    var showReport by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val title = viewModel.state.conversation?.otherUserName
        ?: stringResource(R.string.messages_title)

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = title,
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = navigateBack,
            // Tapping the other user's name opens their public profile.
            onTitleClick = viewModel.state.conversation?.otherUserId?.let { id ->
                { onOpenSeller(id) }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            ),
            actionContent = {
                Box {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(R.string.chat_more),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.chat_report)) },
                            leadingIcon = { Icon(Icons.Filled.Flag, null) },
                            onClick = {
                                menuOpen = false
                                showReport = true
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(
                                        if (blocked) R.string.chat_unblock else R.string.chat_block
                                    )
                                )
                            },
                            leadingIcon = { Icon(Icons.Filled.Block, null) },
                            onClick = {
                                menuOpen = false
                                if (blocked) viewModel.unblock(conversationId)
                                else viewModel.block(conversationId)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.chat_delete)) },
                            leadingIcon = { Icon(Icons.Filled.Delete, null) },
                            onClick = {
                                menuOpen = false
                                showDeleteConfirm = true
                            }
                        )
                    }
                }
            }
        )
        val conversation = viewModel.state.conversation
        if (conversation?.postId != null) {
            PostStrip(
                conversation = conversation,
                onClick = { onOpenPost(conversation.postId.toInt()) }
            )
        }
        ChatDetailScreen(
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel,
            conversationId = conversationId,
            blocked = blocked
        )
    }

    if (showReport) {
        ReportDialog(
            onDismiss = { showReport = false },
            onSubmit = { reason, message ->
                showReport = false
                viewModel.report(conversationId, reason, message) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.chat_reported),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.chat_confirm_delete)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.delete(conversationId) { navigateBack() }
                }) { Text(stringResource(R.string.chat_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.chat_cancel))
                }
            }
        )
    }
}

/** Tappable bar under the app bar showing the post this thread is about. */
@Composable
private fun PostStrip(conversation: Conversation, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val thumb = conversation.postImage
            if (!thumb.isNullOrBlank()) {
                AsyncImage(
                    model = "${BuildConfig.BASE_URL}post/image/$thumb?size=thumb",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = conversation.postTitle ?: "",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                conversation.postPrice?.let { price ->
                    Text(
                        text = "${formatNumberWithSpaces(price)} ${conversation.postPriceUnit ?: ""}".trim(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                }
            }
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChatDetailScreen(
    modifier: Modifier,
    viewModel: ChatDetailViewModel,
    conversationId: Long,
    blocked: Boolean
) {
    val state = viewModel.state
    val listState = rememberLazyListState()
    var text by remember { mutableStateOf("") }
    var deleteMessageId by remember { mutableStateOf<Long?>(null) }

    // Server-rejected edit/delete (e.g. window expired) → localized toast.
    val context = LocalContext.current
    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    // Keep the latest message in view.
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    // The list shrinks when the keyboard opens — keep the bottom in view.
    val imeVisible = WindowInsets.isImeVisible
    LaunchedEffect(imeVisible) {
        if (imeVisible && state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    // Backdrop for the glass input pill — the messages behind it get blurred.
    val chatHazeState = remember { HazeState() }

    Box(modifier = modifier.fillMaxSize()) {
        // Messages fill the whole area and scroll under the floating input field —
        // the input's outer background is transparent, only the pill has a surface.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                .hazeSource(state = chatHazeState)
        ) {
            if (state.loaded && state.messages.isEmpty()) {
                Text(
                    text = stringResource(R.string.chat_start_hint),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                )
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = 8.dp,
                    // Clearance for the floating input the messages scroll under.
                    bottom = 76.dp
                ),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(items = state.messages, key = { it.id }) { message ->
                    MessageBubble(
                        message = message,
                        onReply = {
                            if (state.editing != null) text = ""
                            viewModel.startReply(message)
                        },
                        onEdit = {
                            viewModel.startEdit(message)
                            text = message.content
                        },
                        onDelete = { deleteMessageId = message.id }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            if (blocked) {
                Box(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
                    BlockedBar(onUnblock = { viewModel.unblock(conversationId) })
                }
            } else {
                MessageInput(
                    text = text,
                    onTextChange = { text = it },
                    sending = state.sending,
                    hazeState = chatHazeState,
                    replyingTo = state.replyingTo,
                    replyToName = state.replyingTo?.let {
                        if (it.mine) stringResource(R.string.chat_you)
                        else state.conversation?.otherUserName.orEmpty()
                    }.orEmpty(),
                    editing = state.editing,
                    onCancelReplyEdit = {
                        if (state.editing != null) text = ""
                        viewModel.cancelReplyEdit()
                    },
                    onSend = {
                        val value = text.trim()
                        if (value.isNotEmpty()) {
                            viewModel.send(conversationId, value)
                            text = ""
                        }
                    }
                )
            }
        }
    }

    deleteMessageId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteMessageId = null },
            title = { Text(stringResource(R.string.chat_delete_message_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteMessage(conversationId, id)
                    deleteMessageId = null
                }) { Text(stringResource(R.string.chat_delete_message)) }
            },
            dismissButton = {
                TextButton(onClick = { deleteMessageId = null }) {
                    Text(stringResource(R.string.chat_cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MessageBubble(
    message: ChatMessage,
    onReply: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val mine = message.mine
    var menuOpen by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    val contentColor = if (mine) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start
    ) {
        Box {
            val bubbleShape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (mine) 16.dp else 4.dp,
                bottomEnd = if (mine) 4.dp else 16.dp
            )
            Column(
                modifier = Modifier
                    .widthIn(max = 290.dp)
                    .clip(bubbleShape)
                    .background(
                        color = if (mine) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { if (!message.deleted) menuOpen = true }
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                if (message.replyToId != null && !message.deleted) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(contentColor.copy(alpha = 0.10f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = message.replyToSenderName.orEmpty(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = contentColor.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = message.replyToContent
                                ?: stringResource(R.string.chat_message_deleted),
                            fontSize = 12.sp,
                            fontStyle = if (message.replyToContent == null) FontStyle.Italic
                            else FontStyle.Normal,
                            color = contentColor.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                }
                if (message.deleted) {
                    Text(
                        text = stringResource(R.string.chat_message_deleted),
                        color = contentColor.copy(alpha = 0.6f),
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic
                    )
                } else {
                    Text(
                        text = message.content,
                        color = contentColor,
                        fontSize = 15.sp
                    )
                }
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chatTimeLabel(message.createdDate),
                        fontSize = 10.sp,
                        color = if (mine) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    if (message.editedAt != null && !message.deleted) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = stringResource(R.string.chat_edited),
                            fontSize = 10.sp,
                            color = if (mine) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                    if (mine && !message.deleted) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = if (message.read) Icons.Filled.DoneAll else Icons.Filled.Done,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.chat_reply)) },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Reply, null) },
                    onClick = {
                        menuOpen = false
                        onReply()
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.chat_copy)) },
                    leadingIcon = { Icon(Icons.Filled.ContentCopy, null) },
                    onClick = {
                        menuOpen = false
                        clipboard.setText(AnnotatedString(message.content))
                    }
                )
                if (message.editable) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.chat_edit)) },
                        leadingIcon = { Icon(Icons.Filled.Edit, null) },
                        onClick = {
                            menuOpen = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.chat_delete_message)) },
                        leadingIcon = { Icon(Icons.Filled.Delete, null) },
                        onClick = {
                            menuOpen = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BlockedBar(onUnblock: () -> Unit) {
    Surface(tonalElevation = 2.dp, color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // One inset, whichever is taller: nav bar (keyboard closed) or
                // keyboard (open). Applying both stacked caused a gap above the IME.
                .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.chat_blocked_note),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onUnblock) {
                Text(stringResource(R.string.chat_unblock))
            }
        }
    }
}

@Composable
private fun MessageInput(
    text: String,
    onTextChange: (String) -> Unit,
    sending: Boolean,
    hazeState: HazeState,
    replyingTo: ChatMessage?,
    replyToName: String,
    editing: ChatMessage?,
    onCancelReplyEdit: () -> Unit,
    onSend: () -> Unit
) {
    var showEmojiPicker by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Close the emoji panel on back press instead of leaving the screen.
    BackHandler(enabled = showEmojiPicker) { showEmojiPicker = false }

    // Transparent outer bar — messages scroll visibly around the pill; only the
    // pill itself carries a (slightly translucent) surface for readability.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            // One inset, whichever is taller: nav bar (keyboard closed) or
            // keyboard (open). Applying both stacked caused a gap above the IME.
            .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        val surfaceColor = MaterialTheme.colorScheme.surface

        // Reply / edit banner sitting on top of the input pill.
        val bannerMessage = editing ?: replyingTo
        if (bannerMessage != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .hazeEffect(state = hazeState) {
                        backgroundColor = surfaceColor
                        blurRadius = 24.dp
                        noiseFactor = 0f
                        tints = listOf(HazeTint(surfaceColor.copy(alpha = 0.60f)))
                        fallbackTint = HazeTint(surfaceColor.copy(alpha = 0.95f))
                    }
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(start = 12.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (editing != null) stringResource(R.string.chat_editing)
                        else replyToName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = bannerMessage.content,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = onCancelReplyEdit) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.chat_cancel),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(24.dp))
                // Real backdrop blur of the messages scrolling underneath.
                .hazeEffect(state = hazeState) {
                    backgroundColor = surfaceColor
                    blurRadius = 24.dp
                    noiseFactor = 0f
                    tints = listOf(HazeTint(surfaceColor.copy(alpha = 0.45f)))
                    fallbackTint = HazeTint(surfaceColor.copy(alpha = 0.90f))
                }
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
                // Emoji toggle (left)
                Icon(
                    imageVector = Icons.Filled.EmojiEmotions,
                    contentDescription = stringResource(R.string.cd_emoji),
                    tint = if (showEmojiPicker) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (showEmojiPicker) {
                                showEmojiPicker = false
                            } else {
                                keyboardController?.hide()
                                showEmojiPicker = true
                            }
                        }
                        .padding(6.dp)
                )

                // Text input
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.chat_input_hint),
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                    BasicTextField(
                        value = text,
                        onValueChange = onTextChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { if (it.isFocused) showEmojiPicker = false },
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { onSend() })
                    )
                }

                // Send button (right)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (text.isNotBlank()) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable(enabled = !sending && text.isNotBlank()) { onSend() },
                    contentAlignment = Alignment.Center
                ) {
                    if (sending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = stringResource(R.string.chat_send),
                            tint = if (text.isNotBlank()) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
        }

        AnimatedVisibility(visible = showEmojiPicker) {
            // Glass panel, same treatment as the input pill.
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .hazeEffect(state = hazeState) {
                        backgroundColor = surfaceColor
                        blurRadius = 24.dp
                        noiseFactor = 0f
                        tints = listOf(HazeTint(surfaceColor.copy(alpha = 0.60f)))
                        fallbackTint = HazeTint(surfaceColor.copy(alpha = 0.95f))
                    }
            ) {
                EmojiPicker(onEmojiSelected = { onTextChange(text + it) })
            }
        }
    }
}

/** Common emojis offered in the chat picker. */
private val emojiList: List<String> = (
    "😀 😃 😄 😁 😆 😅 😂 🤣 😊 😇 🙂 🙃 😉 😌 😍 🥰 😘 😗 😙 😚 😋 😛 😝 😜 🤪 🤨 " +
    "🧐 🤓 😎 🥳 🤩 😏 😒 😞 😔 😟 😕 🙁 ☹️ 😣 😖 😫 😩 🥺 😢 😭 😤 😠 😡 🤬 🤯 😳 " +
    "🥵 🥶 😱 😨 😰 😥 😓 🤗 🤔 🤭 🤫 😶 😐 😑 😬 🙄 😯 😦 😧 😮 😲 🥱 😴 🤤 😪 😵 🤐 " +
    "👍 👎 👌 ✌️ 🤞 🤟 🤘 👏 🙌 🙏 🤝 💪 👋 🤙 ☝️ 👆 👇 👈 👉 ✋ 🤚 🖐️ 🖖 👊 ✊ 🫶 " +
    "❤️ 🧡 💛 💚 💙 💜 🖤 🤍 🤎 💔 ❣️ 💕 💞 💓 💗 💖 💘 💝 ⭐ 🌟 ✨ ⚡ 🔥 💯 ✅ ❌ ❓ ❗ " +
    "🎉 🎊 🚗 🚙 📍 🗺️ ⏰ ⏳ 📞 📱 💬 💸 💰 🙇 🎁 ☕ 🍔"
    ).split(" ").filter { it.isNotBlank() }

/** Self-contained emoji panel: tapping an emoji appends it to the input text. */
@Composable
private fun EmojiPicker(onEmojiSelected: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 44.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(top = 6.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(emojiList) { emoji ->
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable { onEmojiSelected(emoji) },
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 24.sp)
            }
        }
    }
}

@Composable
private fun ReportDialog(
    onDismiss: () -> Unit,
    onSubmit: (reason: String, message: String?) -> Unit
) {
    val reasons = listOf(
        "spam" to stringResource(R.string.chat_reason_spam),
        "scam" to stringResource(R.string.chat_reason_scam),
        "offensive" to stringResource(R.string.chat_reason_offensive),
        "other" to stringResource(R.string.chat_reason_other)
    )
    var selected by remember { mutableStateOf("spam") }
    var message by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.chat_report_title)) },
        text = {
            Column {
                reasons.forEach { (code, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(selected = selected == code, onClick = { selected = code })
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selected == code, onClick = { selected = code })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(label)
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text(stringResource(R.string.chat_report_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(selected, message.trim().ifBlank { null }) }) {
                Text(stringResource(R.string.chat_submit))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.chat_cancel)) }
        }
    )
}

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import kotlinx.coroutines.delay
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.chat.ChatMessage
import uz.promo.selling.ui.TopAppBar
import uz.promo.selling.utils.chatTimeLabel

private const val POLL_MS = 4000L

@Composable
fun ChatDetailRoute(
    conversationId: Long,
    navigateBack: () -> Unit,
    viewModel: ChatDetailViewModel = hiltViewModel()
) {
    // Load once, then poll for new/read messages while the screen is open.
    LaunchedEffect(conversationId) {
        viewModel.loadHeader(conversationId)
        viewModel.refreshMessages(conversationId)
        while (true) {
            delay(POLL_MS)
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
        ChatDetailScreen(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
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

    // Keep the latest message in view.
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
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
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(items = state.messages, key = { it.id }) { message ->
                    MessageBubble(message)
                }
            }
        }

        if (blocked) {
            BlockedBar(onUnblock = { viewModel.unblock(conversationId) })
        } else {
            MessageInput(
                text = text,
                onTextChange = { text = it },
                sending = state.sending,
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

@Composable
private fun MessageBubble(message: ChatMessage) {
    val mine = message.mine
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 290.dp)
                .background(
                    color = if (mine) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (mine) 16.dp else 4.dp,
                        bottomEnd = if (mine) 4.dp else 16.dp
                    )
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = message.content,
                color = if (mine) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp
            )
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
                if (mine) {
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
    }
}

@Composable
private fun BlockedBar(onUnblock: () -> Unit) {
    Surface(tonalElevation = 2.dp, color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
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
    onSend: () -> Unit
) {
    Surface(
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(R.string.chat_input_hint)) },
                maxLines = 4,
                shape = RoundedCornerShape(24.dp),
                keyboardActions = KeyboardActions(onSend = { onSend() })
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onSend,
                enabled = !sending && text.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.chat_send),
                    tint = if (text.isNotBlank()) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
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

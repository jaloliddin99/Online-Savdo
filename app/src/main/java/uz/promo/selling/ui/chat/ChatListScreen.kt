package uz.promo.selling.ui.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import uz.promo.selling.BuildConfig
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.chat.Conversation
import uz.promo.selling.ui.TopAppBar
import uz.promo.selling.ui.main.saved.SegmentedToggle
import uz.promo.selling.utils.chatListTimeLabel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle

@Composable
fun ChatListRoute(
    onBackClick: () -> Unit,
    onConversationClick: (Long) -> Unit,
    // When Chat is a bottom-bar tab there's no back target — hide the arrow.
    showBackButton: Boolean = true,
    viewModel: ChatListViewModel = hiltViewModel()
) {
    // Reload on every resume so deletions made inside a thread are reflected on return.
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.loadConversations()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var searchActive by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableIntStateOf(0) } // 0 = All, 1 = Unread

    val state = viewModel.state
    val filtered = remember(state.items, filter, query) {
        state.items.filter { c ->
            val matchesFilter = filter == 0 || c.unreadCount > 0
            val matchesQuery = query.isBlank() || listOfNotNull(
                c.otherUserName, c.postTitle, c.lastMessage
            ).any { it.contains(query.trim(), ignoreCase = true) }
            matchesFilter && matchesQuery
        }
    }
    val unreadTotal = remember(state.items) { state.items.count { it.unreadCount > 0 } }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = stringResource(R.string.messages_title),
            navigationIcon = if (showBackButton) Icons.AutoMirrored.Filled.ArrowBack else null,
            onNavigationClick = onBackClick,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            ),
            actionContent = {
                IconButton(onClick = {
                    searchActive = !searchActive
                    if (!searchActive) query = ""
                }) {
                    Icon(
                        imageVector = if (searchActive) Icons.Filled.Close else Icons.Filled.Search,
                        contentDescription = stringResource(R.string.chat_search_hint),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )

        // Reveal-on-tap search field + All/Unread toggle.
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            AnimatedVisibility(visible = searchActive) {
                Column {
                    ChatSearchField(query = query, onQueryChange = { query = it })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            SegmentedToggle(
                options = listOf(
                    stringResource(R.string.all),
                    if (unreadTotal > 0)
                        "${stringResource(R.string.chat_filter_unread)} ($unreadTotal)"
                    else stringResource(R.string.chat_filter_unread)
                ),
                selectedIndex = filter,
                onSelect = { filter = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        ChatListScreen(
            modifier = Modifier.fillMaxSize(),
            items = filtered,
            isLoading = state.isLoading && state.items.isEmpty(),
            onConversationClick = onConversationClick,
            onDelete = viewModel::deleteConversation
        )
    }
}

@Composable
private fun ChatSearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            decorationBox = { inner ->
                Box {
                    if (query.isBlank()) {
                        Text(
                            text = stringResource(R.string.chat_search_hint),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            fontSize = 15.sp
                        )
                    }
                    inner()
                }
            }
        )
    }
}

@Composable
private fun ChatListScreen(
    modifier: Modifier,
    items: List<Conversation>,
    isLoading: Boolean,
    onConversationClick: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    var deleteId by remember { mutableStateOf<Long?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            items.isEmpty() -> {
                Text(
                    text = stringResource(R.string.chat_empty),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    // Content scrolls under the floating glass bottom bar — keep the tail reachable.
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 96.dp)
                ) {
                    itemsIndexed(items = items, key = { _, c -> c.id }) { index, conversation ->
                        ConversationRow(
                            conversation = conversation,
                            onClick = { onConversationClick(conversation.id) },
                            onLongClick = { deleteId = conversation.id }
                        )
                        if (index < items.lastIndex) {
                            HorizontalDivider(
                                // Indent past the avatar, WhatsApp-style.
                                modifier = Modifier.padding(start = 84.dp, end = 16.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }

    deleteId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteId = null },
            title = { Text(stringResource(R.string.chat_confirm_delete)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(id)
                    deleteId = null
                }) { Text(stringResource(R.string.chat_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { deleteId = null }) {
                    Text(stringResource(R.string.chat_cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ConversationRow(
    conversation: Conversation,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        val avatarUrl = conversation.otherUserProfileUrl
        if (!avatarUrl.isNullOrBlank()) {
            AsyncImage(
                model = "${BuildConfig.BASE_URL}user/image/$avatarUrl",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (conversation.otherUserName?.firstOrNull() ?: '?').toString().uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = conversation.otherUserName ?: "",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                conversation.lastMessageAt?.let {
                    Text(
                        text = chatListTimeLabel(it),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            conversation.postTitle?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = conversation.lastMessage ?: "",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (conversation.unreadCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = conversation.unreadCount.toString(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Post thumbnail
        val thumb = conversation.postImage
        if (!thumb.isNullOrBlank()) {
            Spacer(modifier = Modifier.width(12.dp))
            AsyncImage(
                model = "${BuildConfig.BASE_URL}post/image/$thumb?size=thumb",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}

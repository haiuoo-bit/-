package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DarkOnSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LightOnPrimary
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightOutlineVariant
import com.example.ui.theme.LightPrimary
import com.example.ui.theme.LightSecondary
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceContainer
import com.example.ui.theme.LightSurfaceContainerHigh
import com.example.ui.theme.LightSurfaceContainerLow
import com.example.ui.theme.SearchActiveAmber
import com.example.ui.theme.SearchHighlightYellow
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArticleDetailScreen(
  uiState: HomeFeedUiState,
  onBack: () -> Unit,
  onToggleFollow: () -> Unit,
  onToggleLike: () -> Unit,
  onToggleCollect: () -> Unit,
  onOpenShareSheet: () -> Unit,
  onToggleOfflineCache: () -> Unit,
  onToggleArticleSearch: () -> Unit,
  onSearchQueryChange: (String) -> Unit,
  onStepSearchMatch: (delta: Int, totalMatches: Int) -> Unit,
  onOpenAttachmentPreview: () -> Unit,
  onCloseAttachmentPreview: () -> Unit,
  onSubmitComment: (String) -> Unit,
  onReplyClick: (commentId: String, authorName: String) -> Unit,
  onClearReplyTarget: () -> Unit,
  onToggleCommentLike: (commentId: String, replyId: String?) -> Unit,
  onToggleCollapseReplies: (commentId: String) -> Unit,
  onShowToast: (String) -> Unit,
  onOpenGlobalSearch: (String) -> Unit = {},
  onOpenTagFinder: (String) -> Unit = {},
  modifier: Modifier = Modifier,
) {
  val story = uiState.detailStory
  val scroller = rememberScrollState()
  val scope = rememberCoroutineScope()
  @Suppress("DEPRECATION")
  val clipboardManager = LocalClipboardManager.current

  val isHeaderSolid = scroller.value > 220
  var attachmentOffsetY by remember { mutableIntStateOf(0) }
  var commentsOffsetY by remember { mutableIntStateOf(0) }
  var inlineCommentDraft by remember { mutableStateOf("") }
  val inlineInputFocus = remember { FocusRequester() }

  // Pre-compute match offsets across all paragraphs for accurate search count & active highlight
  val query = uiState.articleSearchQuery.trim()
  val allSegments =
    remember(story.id) {
      buildList {
        add(story.lead)
        addAll(story.paragraphs)
        addAll(story.characterSettings)
        addAll(story.personalityItems)
        addAll(story.bondParagraphs)
      }
    }
  val segmentMatchCounts =
    remember(allSegments, query) {
      if (query.isEmpty()) List(allSegments.size) { 0 }
      else allSegments.map { countOccurrences(it, query) }
    }
  val totalMatches = remember(segmentMatchCounts) { segmentMatchCounts.sum() }
  val safeActiveMatch =
    if (totalMatches <= 0) 0 else uiState.activeSearchMatchIndex.coerceIn(0, totalMatches - 1)

  val textCopiedToast = stringResource(R.string.toast_text_copied)

  Scaffold(
    containerColor = LightSurface,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    bottomBar = {
      ArticleBottomActionBar(
        story = story,
        commentsCount = uiState.totalCommentsCount,
        isOfflineCached = uiState.isOfflineCached,
        onQuickSayClick = {
          scope.launch {
            scroller.animateScrollTo(commentsOffsetY)
            runCatching { inlineInputFocus.requestFocus() }
          }
        },
        onLikeClick = onToggleLike,
        onJumpCommentsClick = {
          scope.launch { scroller.animateScrollTo(commentsOffsetY) }
        },
        onCollectClick = onToggleCollect,
        onShareClick = onOpenShareSheet,
        onCopyClick = {
          val text =
            buildString {
              appendLine(story.title)
              appendLine(story.lead)
              story.paragraphs.forEach { appendLine(it) }
              story.characterSettings.forEach { appendLine(it) }
            }
          clipboardManager.setText(AnnotatedString(text))
          onShowToast(textCopiedToast)
        },
        onDownloadClick = onToggleOfflineCache,
      )
    },
    modifier = modifier.fillMaxSize().imePadding(),
  ) { innerPadding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      Column(
        modifier =
          Modifier.fillMaxSize()
            .widthIn(max = 600.dp)
            .verticalScroll(scroller)
            .testTag("article_detail_scroller"),
      ) {
        // Cover Hero Image
        Box(modifier = Modifier.fillMaxWidth().height(312.dp).background(DarkSurface)) {
          Image(
            painter = painterResource(id = story.coverRes),
            contentDescription = story.title,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize(),
          )
          Box(
            modifier =
              Modifier.fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    colors = listOf(Color.Transparent, DarkSurface.copy(alpha = 0.45f), DarkSurface)
                  )
                )
          )
          Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(horizontal = 16.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Text(
              text = story.readingMeta,
              style = MaterialTheme.typography.bodySmall,
              color = DarkOnSurface.copy(alpha = 0.82f),
            )
            Text(
              text = story.title,
              style = MaterialTheme.typography.headlineLarge,
              color = DarkOnSurface,
              modifier = Modifier.testTag("article_detail_title"),
            )
          }
        }

        // Overlapping light paper article card
        Column(
          modifier =
            Modifier.fillMaxWidth()
              .offset(y = (-16).dp)
              .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
              .background(LightSurface)
              .padding(horizontal = 16.dp, vertical = 22.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          // Work properties pills
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Surface(
              shape = CircleShape,
              color = LightSurfaceContainer,
              modifier = Modifier.border(1.dp, SuccessGreen.copy(alpha = 0.35f), CircleShape),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
              ) {
                Icon(
                  imageVector = Icons.Outlined.CheckCircle,
                  contentDescription = null,
                  tint = SuccessGreen,
                  modifier = Modifier.size(14.dp),
                )
                Text(
                  text = stringResource(R.string.chip_free_read),
                  style = MaterialTheme.typography.labelSmall,
                  color = SuccessGreen,
                )
              }
            }

            Surface(
              shape = CircleShape,
              color = LightSurfaceContainer,
              modifier =
                Modifier.border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), CircleShape)
                  .clip(CircleShape)
                  .clickable { scope.launch { scroller.animateScrollTo(attachmentOffsetY) } },
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
              ) {
                Icon(
                  imageVector = Icons.Outlined.Description,
                  contentDescription = null,
                  tint = LightSecondary,
                  modifier = Modifier.size(14.dp),
                )
                Text(
                  text = stringResource(R.string.chip_has_pdf),
                  style = MaterialTheme.typography.labelSmall,
                  color = LightOnSurface,
                )
              }
            }

            Surface(
              shape = CircleShape,
              color = LightSurfaceContainer,
              modifier = Modifier.border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), CircleShape),
            ) {
              Text(
                text = stringResource(R.string.chip_open_derivative),
                style = MaterialTheme.typography.labelSmall,
                color = LightOnSurface,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
              )
            }

            Surface(
              shape = CircleShape,
              color = LightSurfaceContainer,
              modifier = Modifier.border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), CircleShape),
            ) {
              Text(
                text = stringResource(R.string.chip_word_count_format, story.totalSearchableWordCount),
                style = MaterialTheme.typography.labelSmall,
                color = LightOnSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
              )
            }
          }

          var globalMatchCursor = 0
          @Composable
          fun HighlightedParagraph(text: String, style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium) {
            val matchCount = if (query.isEmpty()) 0 else countOccurrences(text, query)
            val startGlobalIdx = globalMatchCursor
            globalMatchCursor += matchCount
            Text(
              text =
                highlightSearchMatches(
                  text = text,
                  query = query,
                  startGlobalMatchIdx = startGlobalIdx,
                  activeGlobalMatchIdx = safeActiveMatch,
                ),
              style = style.copy(lineHeight = 22.sp),
              color = LightOnSurface,
            )
          }

          // Narrative Paragraphs
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            HighlightedParagraph(story.lead)
            story.paragraphs.forEach { HighlightedParagraph(it) }
          }

          // Character Setting Card
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            story.characterSettings.forEach { HighlightedParagraph(it) }
          }

          // Personality Section
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = story.personalityTitle,
              style = MaterialTheme.typography.titleMedium,
              color = LightOnSurface,
              modifier = Modifier.padding(bottom = 2.dp),
            )
            story.personalityItems.forEach { HighlightedParagraph(it) }
          }

          // Relationships / Bonds Section
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = story.bondsTitle,
              style = MaterialTheme.typography.titleMedium,
              color = LightOnSurface,
              modifier = Modifier.padding(bottom = 2.dp),
            )
            story.bondParagraphs.forEach { HighlightedParagraph(it) }
          }

          // Hashtag Pills
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            story.tags.forEach { rawTag ->
              val cleanTag = rawTag.removePrefix("#").trim()
              val formatted = if (rawTag.startsWith("# ")) rawTag else rawTag.replace("#", "# ")
              Surface(
                shape = CircleShape,
                color = LightSurfaceContainer,
                modifier =
                  Modifier.border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), CircleShape)
                    .clip(CircleShape)
                    .clickable { onOpenTagFinder(cleanTag) },
              ) {
                Text(
                  text = formatted,
                  style = MaterialTheme.typography.labelSmall,
                  color = LightSecondary,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                )
              }
            }
          }

          // PDF Attachment Card
          Column(
            modifier =
              Modifier.fillMaxWidth().onGloballyPositioned {
                attachmentOffsetY = (it.positionInParent().y.toInt() - 100).coerceAtLeast(0)
              },
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = stringResource(R.string.section_attachments),
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
                color = LightOnSurface,
              )
              Text(
                text = stringResource(R.string.attachment_count_one),
                style = MaterialTheme.typography.labelSmall,
                color = LightOnSurfaceVariant,
              )
            }

            Row(
              modifier =
                Modifier.fillMaxWidth()
                  .clip(RoundedCornerShape(16.dp))
                  .background(LightSurfaceContainerLow)
                  .border(1.dp, LightOutlineVariant, RoundedCornerShape(16.dp))
                  .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              StitchPdfBadgeIcon()
              Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
              ) {
                Text(
                  text = story.attachmentFileName,
                  style = MaterialTheme.typography.titleSmall,
                  color = LightOnSurface,
                  maxLines = 1,
                )
                Text(
                  text = story.attachmentSizeLabel,
                  style = MaterialTheme.typography.bodySmall,
                  color = LightOnSurfaceVariant,
                )
              }
              Surface(
                shape = CircleShape,
                color = LightSurface,
                modifier =
                  Modifier.border(1.dp, LightOutlineVariant, CircleShape)
                    .clip(CircleShape)
                    .clickable(onClick = onOpenAttachmentPreview)
                    .testTag("attachment_view_btn"),
              ) {
                Text(
                  text = stringResource(R.string.action_view_attachment),
                  style = MaterialTheme.typography.labelSmall,
                  color = LightOnSurface,
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                )
              }
            }
          }

          HorizontalDivider(color = LightOutlineVariant, thickness = 1.dp)

          // Full Synchronized Comments Section
          Column(
            modifier =
              Modifier.fillMaxWidth().onGloballyPositioned {
                commentsOffsetY = (it.positionInParent().y.toInt() - 80).coerceAtLeast(0)
              },
            verticalArrangement = Arrangement.spacedBy(16.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = stringResource(R.string.comment_detail_total_format, uiState.totalCommentsCount),
                style = MaterialTheme.typography.titleMedium,
                color = LightOnSurface,
                modifier = Modifier.testTag("detail_total_comments_label"),
              )
              Text(
                text = stringResource(R.string.comment_sort_hot),
                style = MaterialTheme.typography.labelSmall,
                color = LightOnSurfaceVariant,
              )
            }

            if (uiState.replyTarget != null) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text(
                  text = stringResource(R.string.comment_hint_reply_format, uiState.replyTarget.authorName),
                  style = MaterialTheme.typography.labelSmall,
                  color = LightSecondary,
                )
                Text(
                  text = stringResource(R.string.action_collapse),
                  style = MaterialTheme.typography.labelSmall,
                  color = LightOnSurfaceVariant,
                  modifier = Modifier.clickable(onClick = onClearReplyTarget),
                )
              }
            }

            // Inline comment composer
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(LightSurfaceContainer),
                contentAlignment = Alignment.Center,
              ) {
                Text(
                  text = stringResource(R.string.comment_me_name),
                  style = MaterialTheme.typography.labelSmall,
                  color = LightOnSurfaceVariant,
                )
              }

              Row(
                modifier =
                  Modifier.weight(1f)
                    .height(40.dp)
                    .clip(CircleShape)
                    .background(LightSurfaceContainer)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                BasicTextField(
                  value = inlineCommentDraft,
                  onValueChange = { inlineCommentDraft = it },
                  singleLine = true,
                  textStyle = MaterialTheme.typography.bodySmall.copy(color = LightOnSurface),
                  cursorBrush = SolidColor(LightSecondary),
                  keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                  keyboardActions =
                    KeyboardActions(
                      onSend = {
                        if (inlineCommentDraft.isNotBlank()) {
                          onSubmitComment(inlineCommentDraft)
                          inlineCommentDraft = ""
                        }
                      }
                    ),
                  modifier =
                    Modifier.weight(1f)
                      .focusRequester(inlineInputFocus)
                      .testTag("detail_inline_comment_input"),
                  decorationBox = { innerText ->
                    if (inlineCommentDraft.isEmpty()) {
                      Text(
                        text =
                          if (uiState.replyTarget != null) {
                            stringResource(R.string.comment_hint_reply_format, uiState.replyTarget.authorName)
                          } else {
                            stringResource(R.string.comment_hint_inline)
                          },
                        style = MaterialTheme.typography.bodySmall,
                        color = LightOnSurfaceVariant,
                      )
                    }
                    innerText()
                  },
                )

                Text(
                  text = stringResource(R.string.comment_action_send),
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  color = LightSecondary,
                  modifier =
                    Modifier.clip(RoundedCornerShape(8.dp))
                      .clickable {
                        if (inlineCommentDraft.isNotBlank()) {
                          onSubmitComment(inlineCommentDraft)
                          inlineCommentDraft = ""
                        }
                      }
                      .padding(horizontal = 6.dp, vertical = 4.dp)
                      .testTag("detail_inline_comment_send"),
                )
              }
            }

            // Comment List
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
              uiState.comments.forEach { item ->
                CommentItemRow(
                  comment = item,
                  onReplyClick = { author ->
                    onReplyClick(item.id, author)
                    runCatching { inlineInputFocus.requestFocus() }
                  },
                  onToggleLike = { replyId -> onToggleCommentLike(item.id, replyId) },
                  onToggleCollapseReplies = { onToggleCollapseReplies(item.id) },
                )
              }
            }
          }
        }
      }

      // Sticky adaptive top header
      ArticleStickyHeader(
        story = story,
        isSolid = isHeaderSolid,
        isSearchVisible = uiState.isArticleSearchVisible,
        searchQuery = uiState.articleSearchQuery,
        activeMatchIdx = safeActiveMatch,
        totalMatches = totalMatches,
        onBack = onBack,
        onToggleFollow = onToggleFollow,
        onToggleSearch = onToggleArticleSearch,
        onSearchQueryChange = onSearchQueryChange,
        onPrevMatch = { onStepSearchMatch(-1, totalMatches) },
        onNextMatch = { onStepSearchMatch(1, totalMatches) },
        onGlobalSearchClick = { onOpenGlobalSearch(uiState.articleSearchQuery) },
        modifier = Modifier.align(Alignment.TopCenter),
      )

      OverlayToastPill(
        message = uiState.toastMessage,
        darkPolarity = true,
        modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 64.dp),
      )
    }
  }

  if (uiState.isAttachmentPreviewOpen) {
    AlertDialog(
      onDismissRequest = onCloseAttachmentPreview,
      containerColor = LightSurface,
      titleContentColor = LightOnSurface,
      textContentColor = LightOnSurfaceVariant,
      title = {
        Text(
          text = story.attachmentFileName,
          style = MaterialTheme.typography.titleMedium,
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = story.attachmentSizeLabel,
            style = MaterialTheme.typography.labelSmall,
            color = LightSecondary,
          )
          Text(
            text = "包含 ${story.title} 人物小传、关系图谱及未公开长廊撑伞分镜手稿设定，已支持离线阅读缓存。",
            style = MaterialTheme.typography.bodySmall,
          )
        }
      },
      confirmButton = {
        TextButton(onClick = onCloseAttachmentPreview) {
          Text(stringResource(R.string.modal_known), color = LightSecondary)
        }
      },
    )
  }
}

@Composable
private fun ArticleStickyHeader(
  story: FeedStory,
  isSolid: Boolean,
  isSearchVisible: Boolean,
  searchQuery: String,
  activeMatchIdx: Int,
  totalMatches: Int,
  onBack: () -> Unit,
  onToggleFollow: () -> Unit,
  onToggleSearch: () -> Unit,
  onSearchQueryChange: (String) -> Unit,
  onPrevMatch: () -> Unit,
  onNextMatch: () -> Unit,
  onGlobalSearchClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val contentTint by
    animateColorAsState(
      targetValue = if (isSolid) LightOnSurface else DarkOnSurface,
      animationSpec = tween(180),
      label = "detailHeaderTint",
    )
  val followBg =
    when {
      isSolid && story.isFollowed -> LightSurfaceContainer
      isSolid -> LightPrimary
      story.isFollowed -> DarkOnSurface.copy(alpha = 0.18f)
      else -> LightSecondary
    }
  val followTextColor =
    when {
      isSolid && story.isFollowed -> LightOnSurface
      isSolid -> LightOnPrimary
      else -> DarkOnSurface
    }

  Column(
    modifier =
      modifier
        .fillMaxWidth()
        .then(
          if (isSolid) {
            Modifier.background(LightSurface)
          } else {
            Modifier.background(
              Brush.verticalGradient(
                colors =
                  listOf(
                    DarkSurface.copy(alpha = 0.78f),
                    DarkSurface.copy(alpha = 0.36f),
                    Color.Transparent,
                  )
              )
            )
          }
        )
        .statusBarsPadding()
        .padding(horizontal = 12.dp, vertical = 10.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.weight(1f),
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.size(36.dp).testTag("article_back_btn"),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.cd_back_home),
            tint = contentTint,
            modifier = Modifier.size(21.dp),
          )
        }

        Image(
          painter = painterResource(id = story.coverRes),
          contentDescription = story.authorName,
          contentScale = ContentScale.Crop,
          modifier =
            Modifier.size(32.dp)
              .clip(CircleShape)
              .border(1.dp, LightOutlineVariant.copy(alpha = 0.45f), CircleShape),
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.weight(1f, fill = false),
        ) {
          Text(
            text = story.authorName,
            style = MaterialTheme.typography.titleMedium,
            color = contentTint,
            maxLines = 1,
          )
          if (story.isVerifiedBlueV) {
            BlueVBadge(size = 15.dp)
          }
        }

        Surface(
          shape = CircleShape,
          color = followBg,
          contentColor = followTextColor,
          modifier =
            Modifier.height(28.dp)
              .clip(CircleShape)
              .clickable(onClick = onToggleFollow)
              .testTag("article_header_follow_btn"),
        ) {
          Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(
              text =
                stringResource(
                  if (story.isFollowed) R.string.label_followed else R.string.label_follow_plus
                ),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            )
          }
        }
      }

      IconButton(
        onClick = onToggleSearch,
        modifier = Modifier.size(36.dp).testTag("article_search_toggle_btn"),
      ) {
        Icon(
          imageVector = Icons.Outlined.Search,
          contentDescription = stringResource(R.string.cd_search_article),
          tint = contentTint,
          modifier = Modifier.size(21.dp),
        )
      }
    }

    AnimatedVisibility(visible = isSearchVisible) {
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .clip(CircleShape)
            .background(LightSurface)
            .border(1.dp, LightOutlineVariant, CircleShape)
            .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Icon(
          imageVector = Icons.Outlined.Search,
          contentDescription = null,
          tint = LightOnSurfaceVariant,
          modifier = Modifier.size(16.dp),
        )
        BasicTextField(
          value = searchQuery,
          onValueChange = onSearchQueryChange,
          singleLine = true,
          textStyle = MaterialTheme.typography.bodySmall.copy(color = LightOnSurface),
          cursorBrush = SolidColor(LightSecondary),
          modifier = Modifier.weight(1f).testTag("article_search_input"),
          decorationBox = { inner ->
            if (searchQuery.isEmpty()) {
              Text(
                text = stringResource(R.string.hint_article_search),
                style = MaterialTheme.typography.bodySmall,
                color = LightOnSurfaceVariant,
              )
            }
            inner()
          },
        )
        Text(
          text =
            if (totalMatches > 0) "${activeMatchIdx + 1}/$totalMatches" else "0/0",
          style = MaterialTheme.typography.labelSmall,
          color = LightOnSurfaceVariant,
          modifier = Modifier.padding(horizontal = 4.dp).testTag("article_search_count"),
        )
        Text(
          text = "↑",
          style = MaterialTheme.typography.labelMedium,
          color = LightOnSurfaceVariant,
          modifier =
            Modifier.clip(CircleShape)
              .clickable(enabled = totalMatches > 0, onClick = onPrevMatch)
              .padding(horizontal = 6.dp, vertical = 4.dp),
        )
        Text(
          text = "↓",
          style = MaterialTheme.typography.labelMedium,
          color = LightOnSurfaceVariant,
          modifier =
            Modifier.clip(CircleShape)
              .clickable(enabled = totalMatches > 0, onClick = onNextMatch)
              .padding(horizontal = 6.dp, vertical = 4.dp),
        )
        Surface(
          shape = CircleShape,
          color = LightSurfaceContainer,
          contentColor = LightOnSurface,
          modifier = Modifier.clip(CircleShape).clickable(onClick = onGlobalSearchClick),
        ) {
          Text(
            text = stringResource(R.string.action_global_search),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          )
        }
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier.size(26.dp)
              .clip(CircleShape)
              .background(LightSurfaceContainer)
              .clickable(onClick = onToggleSearch),
        ) {
          Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = stringResource(R.string.cd_close),
            tint = LightOnSurfaceVariant,
            modifier = Modifier.size(14.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun ArticleBottomActionBar(
  story: FeedStory,
  commentsCount: Int,
  isOfflineCached: Boolean,
  onQuickSayClick: () -> Unit,
  onLikeClick: () -> Unit,
  onJumpCommentsClick: () -> Unit,
  onCollectClick: () -> Unit,
  onShareClick: () -> Unit,
  onCopyClick: () -> Unit,
  onDownloadClick: () -> Unit,
) {
  Surface(
    color = LightSurface,
    shadowElevation = 4.dp,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
      HorizontalDivider(color = LightOutlineVariant, thickness = 1.dp)
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Surface(
          shape = CircleShape,
          color = LightSurfaceContainer,
          contentColor = LightOnSurfaceVariant,
          modifier =
            Modifier.width(96.dp)
              .height(34.dp)
              .clip(CircleShape)
              .clickable(onClick = onQuickSayClick)
              .testTag("article_quick_say_btn"),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 10.dp),
          ) {
            Icon(
              imageVector = Icons.Outlined.Edit,
              contentDescription = null,
              modifier = Modifier.size(14.dp),
            )
            Text(
              text = stringResource(R.string.action_quick_say),
              style = MaterialTheme.typography.labelSmall,
              maxLines = 1,
            )
          }
        }

        Row(
          modifier = Modifier.weight(1f),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          BottomMiniAction(
            label = story.likesDisplay,
            tint = if (story.isLiked) ErrorRed else LightOnSurfaceVariant,
            onClick = onLikeClick,
            modifier = Modifier.weight(1f).testTag("detail_like_btn"),
          ) {
            StitchHeartIcon(
              filled = story.isLiked,
              tint = if (story.isLiked) ErrorRed else LightOnSurface,
              modifier = Modifier.size(20.dp),
            )
          }

          BottomMiniAction(
            label = commentsCount.toString(),
            tint = LightOnSurfaceVariant,
            onClick = onJumpCommentsClick,
            modifier = Modifier.weight(1f).testTag("detail_comment_jump_btn"),
          ) {
            StitchCommentBubbleIcon(tint = LightOnSurface, modifier = Modifier.size(20.dp))
          }

          BottomMiniAction(
            label = story.collectsDisplay,
            tint = if (story.isCollected) LightSecondary else LightOnSurfaceVariant,
            onClick = onCollectClick,
            modifier = Modifier.weight(1f).testTag("detail_collect_btn"),
          ) {
            StitchStarIcon(
              filled = story.isCollected,
              tint = if (story.isCollected) LightSecondary else LightOnSurface,
              modifier = Modifier.size(20.dp),
            )
          }

          BottomMiniAction(
            label = story.sharesCount.toString(),
            tint = LightOnSurfaceVariant,
            onClick = onShareClick,
            modifier = Modifier.weight(1f).testTag("detail_share_btn"),
          ) {
            StitchShareIcon(tint = LightOnSurface, modifier = Modifier.size(20.dp))
          }

          BottomMiniAction(
            label = stringResource(R.string.action_copy_text),
            tint = LightOnSurfaceVariant,
            onClick = onCopyClick,
            modifier = Modifier.weight(1f).testTag("detail_copy_btn"),
          ) {
            Icon(
              imageVector = Icons.Outlined.ContentCopy,
              contentDescription = stringResource(R.string.action_copy_text),
              tint = LightOnSurface,
              modifier = Modifier.size(19.dp),
            )
          }

          BottomMiniAction(
            label =
              stringResource(
                if (isOfflineCached) R.string.action_offline_cached else R.string.action_offline_download
              ),
            tint = if (isOfflineCached) SuccessGreen else LightOnSurfaceVariant,
            onClick = onDownloadClick,
            modifier = Modifier.weight(1f).testTag("detail_download_btn"),
          ) {
            Icon(
              imageVector = if (isOfflineCached) Icons.Filled.Check else Icons.Outlined.Download,
              contentDescription = stringResource(R.string.action_offline_download),
              tint = if (isOfflineCached) SuccessGreen else LightOnSurface,
              modifier = Modifier.size(20.dp),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun BottomMiniAction(
  label: String,
  tint: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  iconContent: @Composable () -> Unit,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(3.dp),
    modifier =
      modifier
        .clip(RoundedCornerShape(8.dp))
        .clickable(onClick = onClick)
        .padding(vertical = 3.dp),
  ) {
    iconContent()
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
      color = tint,
      maxLines = 1,
    )
  }
}

private fun countOccurrences(text: String, query: String): Int {
  if (query.isEmpty()) return 0
  var count = 0
  var idx = 0
  while (true) {
    val found = text.indexOf(query, idx, ignoreCase = true)
    if (found < 0) break
    count++
    idx = found + query.length
  }
  return count
}

private fun highlightSearchMatches(
  text: String,
  query: String,
  startGlobalMatchIdx: Int,
  activeGlobalMatchIdx: Int,
): AnnotatedString {
  if (query.isEmpty()) return AnnotatedString(text)
  return buildAnnotatedString {
    append(text)
    var idx = 0
    var localMatch = 0
    while (true) {
      val found = text.indexOf(query, idx, ignoreCase = true)
      if (found < 0) break
      val isActive = (startGlobalMatchIdx + localMatch) == activeGlobalMatchIdx
      addStyle(
        style =
          SpanStyle(
            background = if (isActive) SearchActiveAmber else SearchHighlightYellow,
            color = LightOnSurface,
            fontWeight = FontWeight.SemiBold,
          ),
        start = found,
        end = found + query.length,
      )
      localMatch++
      idx = found + query.length
    }
  }
}

package com.example.ui.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.home.OverlayToastPill
import com.example.ui.home.StitchCommentBubbleIcon
import com.example.ui.home.StitchHeartIcon
import com.example.ui.home.StitchShareIcon
import com.example.ui.home.StitchStarIcon
import com.example.ui.plaza.CreatorVBadge
import com.example.ui.plaza.PlazaSubScreenHeader
import com.example.ui.theme.AccentBlue
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
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommunityPostDetailScreen(
  post: CommunityPost,
  comments: List<CommunityComment>,
  sortByHot: Boolean,
  replyTarget: CommunityReplyTarget?,
  isMusicPlaying: Boolean,
  toastMessage: String?,
  onBack: () -> Unit,
  onToggleFollow: () -> Unit,
  onToggleLike: () -> Unit,
  onToggleBookmark: () -> Unit,
  onOpenShare: () -> Unit,
  onSelectPollOption: (String) -> Unit,
  onConfirmOrResetPoll: () -> Unit,
  onApplyBounty: () -> Unit,
  onToggleMusicPlay: () -> Unit,
  onSeekMusic: (Int) -> Unit,
  onViewDocument: (CommunityDocument) -> Unit,
  onClickQuote: (CommunityQuoteTarget) -> Unit,
  onToggleSort: () -> Unit,
  onReplyClick: (String, String) -> Unit,
  onClearReplyTarget: () -> Unit,
  onToggleCommentLike: (String, String?) -> Unit,
  onSubmitComment: (String) -> Unit,
  onShowToast: (String) -> Unit,
) {
  var commentDraft by remember { mutableStateOf("") }
  val inputFocusRequester = remember { FocusRequester() }
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()

  Scaffold(
    containerColor = LightSurfaceContainerLow,
    topBar = {
      PlazaSubScreenHeader(
        title = stringResource(R.string.comm_detail_title),
        onBack = onBack,
        backTestTag = "comm_detail_back_btn",
        trailingContent = {
          IconButton(
            onClick = onOpenShare,
            modifier = Modifier.size(48.dp).testTag("comm_detail_header_share_btn"),
          ) {
            StitchShareIcon(tint = LightOnSurface, modifier = Modifier.size(20.dp))
          }
        },
      )
    },
    bottomBar = {
      Surface(color = LightSurface, shadowElevation = 4.dp) {
        Column(
          modifier =
            Modifier.fillMaxWidth()
              .imePadding()
              .navigationBarsPadding()
              .padding(horizontal = 16.dp, vertical = 10.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          if (replyTarget != null) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = stringResource(R.string.comment_hint_reply_format, replyTarget.authorName),
                style = MaterialTheme.typography.labelSmall,
                color = AccentBlue,
              )
              Text(
                text = "取消回复",
                style = MaterialTheme.typography.labelSmall,
                color = LightOnSurfaceVariant,
                modifier = Modifier.clickable(onClick = onClearReplyTarget).padding(4.dp),
              )
            }
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Box(
              modifier =
                Modifier.weight(1f)
                  .height(42.dp)
                  .clip(CircleShape)
                  .background(LightSurfaceContainer)
                  .padding(horizontal = 16.dp),
              contentAlignment = Alignment.CenterStart,
            ) {
              BasicTextField(
                value = commentDraft,
                onValueChange = { commentDraft = it },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(color = LightOnSurface),
                cursorBrush = SolidColor(LightSecondary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions =
                  KeyboardActions(
                    onSend = {
                      if (commentDraft.isNotBlank()) {
                        onSubmitComment(commentDraft)
                        commentDraft = ""
                      }
                    }
                  ),
                modifier = Modifier.fillMaxWidth().focusRequester(inputFocusRequester).testTag("comm_detail_comment_input"),
                decorationBox = { inner ->
                  if (commentDraft.isEmpty()) {
                    Text(
                      text =
                        if (replyTarget != null) {
                          stringResource(R.string.comment_hint_reply_format, replyTarget.authorName)
                        } else {
                          stringResource(R.string.comm_detail_comment_hint)
                        },
                      style = MaterialTheme.typography.bodySmall,
                      color = LightOnSurfaceVariant,
                    )
                  }
                  inner()
                },
              )
            }
            Surface(
              shape = CircleShape,
              color = LightPrimary,
              contentColor = LightOnPrimary,
              modifier =
                Modifier.height(42.dp)
                  .clip(CircleShape)
                  .clickable {
                    if (commentDraft.isNotBlank()) {
                      onSubmitComment(commentDraft)
                      commentDraft = ""
                    }
                  }
                  .testTag("comm_detail_send_btn"),
            ) {
              Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(stringResource(R.string.comment_action_send), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
              }
            }
          }
        }
      }
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().widthIn(max = 600.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        item {
          Column(
            modifier = Modifier.fillMaxWidth().background(LightSurface).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                if (post.avatarRes != null) {
                  Image(
                    painter = painterResource(id = post.avatarRes),
                    contentDescription = post.authorName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(44.dp).clip(CircleShape),
                  )
                } else {
                  Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(LightSurfaceContainerHigh),
                    contentAlignment = Alignment.Center,
                  ) {
                    Text(post.avatarInitial, style = MaterialTheme.typography.titleMedium, color = LightOnSurface)
                  }
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(post.authorName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = LightOnSurface)
                    CreatorVBadge(tier = post.badgeTier, size = 15.dp)
                  }
                  Text(
                    text = "${post.timeAndSource} · 已编辑",
                    style = MaterialTheme.typography.bodySmall,
                    color = LightOnSurfaceVariant,
                  )
                }
              }
              Surface(
                shape = CircleShape,
                color = if (post.isFollowed) LightSurfaceContainer else LightPrimary,
                contentColor = if (post.isFollowed) LightOnSurfaceVariant else LightOnPrimary,
                modifier = Modifier.clip(CircleShape).clickable(onClick = onToggleFollow).testTag("detail_follow_btn"),
              ) {
                Text(
                  text = if (post.isFollowed) stringResource(R.string.label_followed) else stringResource(R.string.label_follow_plus),
                  style = MaterialTheme.typography.labelSmall,
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                )
              }
            }

            val tagsToShow = post.detailTags.ifEmpty { listOf(post.inlineTagPill) }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              tagsToShow.forEachIndexed { idx, tag ->
                Surface(shape = RoundedCornerShape(6.dp), color = LightSurfaceContainer) {
                  Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (idx == 0) LightOnSurface else LightOnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                  )
                }
              }
            }

            val paragraphs = post.fullParagraphs.ifEmpty { listOf(post.summaryBody) }
            paragraphs.forEach { para ->
              Text(text = para, style = MaterialTheme.typography.bodyMedium, color = LightOnSurface)
            }

            if (post.images.isNotEmpty()) {
              CommunityImagesBlock(images = post.images)
            }
            post.poll?.let { poll ->
              CommunityPollCard(post.id, poll, onSelectPollOption, onConfirmOrResetPoll)
            }
            post.linkCard?.let { link ->
              CommunityLinkCardView(link, onShowToast)
            }
            post.document?.let { doc ->
              CommunityDocumentCard(
                document = doc,
                buttonLabel = stringResource(R.string.comm_detail_view_attachment),
                onViewClick = { onViewDocument(doc) },
              )
            }
            post.bountyCard?.let { bounty ->
              CommunityBountyCardView(post.id, bounty, onApplyBounty)
            }
            post.musicTrack?.let { track ->
              MistyBlueMusicPlayerCard(track, isMusicPlaying, onToggleMusicPlay, onSeekMusic)
            }
            post.quoteCard?.let { quote ->
              CommunityQuoteCardView(quote = quote, onClick = { onClickQuote(quote.target) })
            }

            HorizontalDivider(color = LightOutlineVariant.copy(alpha = 0.6f))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
              Row(
                modifier = Modifier.weight(1f).clip(CircleShape).clickable(onClick = onToggleLike).padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                StitchHeartIcon(filled = post.isLiked, tint = if (post.isLiked) ErrorRed else LightOnSurfaceVariant, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(6.dp))
                Text(post.likeCount.toString(), style = MaterialTheme.typography.labelSmall, color = if (post.isLiked) ErrorRed else LightOnSurfaceVariant)
              }
              Row(
                modifier =
                  Modifier.weight(1f)
                    .clip(CircleShape)
                    .clickable {
                      coroutineScope.launch { listState.animateScrollToItem(1) }
                      runCatching { inputFocusRequester.requestFocus() }
                    }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                StitchCommentBubbleIcon(tint = LightOnSurfaceVariant, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(6.dp))
                Text(post.commentCount.toString(), style = MaterialTheme.typography.labelSmall, color = LightOnSurfaceVariant)
              }
              Row(
                modifier = Modifier.weight(1f).clip(CircleShape).clickable(onClick = onToggleBookmark).padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                StitchStarIcon(filled = post.isBookmarked, tint = if (post.isBookmarked) LightSecondary else LightOnSurfaceVariant, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(6.dp))
                Text(post.bookmarkCount.toString(), style = MaterialTheme.typography.labelSmall, color = if (post.isBookmarked) LightSecondary else LightOnSurfaceVariant)
              }
              Row(
                modifier = Modifier.weight(1f).clip(CircleShape).clickable(onClick = onOpenShare).padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                StitchShareIcon(tint = SuccessGreen, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(6.dp))
                Text(stringResource(R.string.action_share), style = MaterialTheme.typography.labelSmall, color = LightOnSurfaceVariant)
              }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(post.detailTimestamp, style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
              Text(post.detailReadsLabel, style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
            }
          }
        }

        item {
          Surface(color = LightSurface, modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = "评论 ${post.commentCount}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = LightOnSurface,
              )
              Text(
                text = if (sortByHot) "按热度 ▾" else "按时间 ▾",
                style = MaterialTheme.typography.labelSmall,
                color = LightOnSurfaceVariant,
                modifier = Modifier.clickable(onClick = onToggleSort).padding(4.dp).testTag("detail_sort_toggle_btn"),
              )
            }
          }
        }

        items(comments, key = { it.id }) { comment ->
          Column(modifier = Modifier.fillMaxWidth().background(LightSurface).padding(horizontal = 16.dp, vertical = 10.dp)) {
            CommunityCommentRow(
              comment = comment,
              onReplyClick = { author ->
                onReplyClick(comment.id, author)
                runCatching { inputFocusRequester.requestFocus() }
              },
              onToggleLike = { replyId -> onToggleCommentLike(comment.id, replyId) },
            )
          }
        }
      }

      OverlayToastPill(
        message = toastMessage,
        darkPolarity = false,
        modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
      )
    }
  }
}

package com.example.ui.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoAwesomeMotion
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.home.StitchCommentBubbleIcon
import com.example.ui.home.StitchHeartIcon
import com.example.ui.home.StitchShareIcon
import com.example.ui.home.StitchStarIcon
import com.example.ui.plaza.CreatorVBadge
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

@Composable
fun CommunityScreen(
  uiState: CommunityUiState,
  onQueryChange: (String) -> Unit,
  onSelectTopic: (String) -> Unit,
  onOpenGlobalSearch: (String) -> Unit,
  onOpenCompose: () -> Unit,
  onOpenAccessoryShowcase: () -> Unit,
  onReplaySkeleton: () -> Unit,
  onOpenPostDetail: (String) -> Unit,
  onToggleFollow: (String) -> Unit,
  onToggleLike: (String) -> Unit,
  onToggleBookmark: (String) -> Unit,
  onOpenComments: (String) -> Unit,
  onOpenShare: (String) -> Unit,
  onSelectPollOption: (String, String) -> Unit,
  onConfirmOrResetPoll: (String) -> Unit,
  onApplyBounty: (String) -> Unit,
  onToggleMusicPlay: (String) -> Unit,
  onSeekMusic: (String, Int) -> Unit,
  onOpenDocumentPreview: (CommunityDocument) -> Unit,
  onOpenQuoteTarget: (CommunityQuoteTarget) -> Unit,
  onShowToast: (String) -> Unit,
) {
  val shimmerBrush = rememberSkeletonShimmerBrush()

  Column(
    modifier = Modifier.fillMaxSize().background(LightSurfaceContainerLow),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Surface(
      color = LightSurface,
      modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Row(
            modifier =
              Modifier.weight(1f)
                .height(42.dp)
                .clip(CircleShape)
                .background(LightSurfaceContainer)
                .padding(start = 12.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Icon(
              imageVector = Icons.Filled.Search,
              contentDescription = null,
              tint = LightOnSurfaceVariant,
              modifier = Modifier.size(18.dp),
            )
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
              BasicTextField(
                value = uiState.searchQuery,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(color = LightOnSurface),
                cursorBrush = SolidColor(LightSecondary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onOpenGlobalSearch(uiState.searchQuery) }),
                modifier = Modifier.fillMaxWidth().testTag("community_search_input"),
                decorationBox = { inner ->
                  if (uiState.searchQuery.isEmpty()) {
                    Text(
                      text = stringResource(R.string.comm_search_placeholder),
                      style = MaterialTheme.typography.bodySmall,
                      color = LightOnSurfaceVariant,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                  }
                  inner()
                },
              )
            }
            if (uiState.searchQuery.isNotEmpty()) {
              Box(
                modifier =
                  Modifier.size(26.dp)
                    .clip(CircleShape)
                    .background(LightSurfaceContainerHigh)
                    .clickable { onQueryChange("") }
                    .testTag("community_clear_search_btn"),
                contentAlignment = Alignment.Center,
              ) {
                Icon(
                  imageVector = Icons.Filled.Close,
                  contentDescription = "清空搜索词",
                  tint = LightOnSurfaceVariant,
                  modifier = Modifier.size(14.dp),
                )
              }
            }
            Surface(
              shape = CircleShape,
              color = LightSurface,
              contentColor = LightOnSurface,
              modifier =
                Modifier.border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), CircleShape)
                  .clip(CircleShape)
                  .clickable { onOpenGlobalSearch(uiState.searchQuery) }
                  .testTag("community_global_search_btn"),
            ) {
              Text(
                text = stringResource(R.string.comm_search_global_btn),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              )
            }
          }

          Surface(
            shape = CircleShape,
            color = LightPrimary,
            contentColor = LightOnPrimary,
            modifier =
              Modifier.height(42.dp)
                .clip(CircleShape)
                .clickable(onClick = onOpenCompose)
                .testTag("community_open_compose_btn"),
          ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
              Text(
                text = stringResource(R.string.comm_compose_btn),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
              )
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          CommunitySeedData.topics.forEach { topic ->
            val selected = uiState.selectedTopic == topic
            val label = if (topic == "推荐") topic else "#$topic"
            Surface(
              shape = CircleShape,
              color = if (selected) LightPrimary else LightSurfaceContainer,
              contentColor = if (selected) LightOnPrimary else LightOnSurfaceVariant,
              modifier =
                Modifier.height(34.dp)
                  .clip(CircleShape)
                  .clickable { onSelectTopic(topic) }
                  .testTag("community_topic_$topic"),
            ) {
              Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 14.dp)) {
                Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium))
              }
            }
          }
        }
      }
    }

    HorizontalDivider(color = LightOutlineVariant.copy(alpha = 0.7f))

    val filtered = uiState.filteredPosts
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 600.dp),
      contentPadding = PaddingValues(bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      item {
        Surface(color = LightSurface, modifier = Modifier.fillMaxWidth()) {
          Column {
            Row(
              modifier =
                Modifier.fillMaxWidth()
                  .clickable { onOpenPostDetail(CommunitySeedData.feedPosts.first().id) }
                  .padding(horizontal = 16.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Surface(shape = RoundedCornerShape(4.dp), color = ErrorRed, contentColor = LightOnPrimary) {
                  Text(
                    text = stringResource(R.string.comm_hot_badge),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  )
                }
                Text(
                  text = stringResource(R.string.comm_hot_topic),
                  style = MaterialTheme.typography.bodySmall,
                  color = LightOnSurface,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
              }
              Text(
                text = stringResource(R.string.comm_hot_count),
                style = MaterialTheme.typography.labelSmall,
                color = LightOnSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp),
              )
            }

            HorizontalDivider(color = LightOutlineVariant.copy(alpha = 0.5f))

            Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text =
                  stringResource(
                    if (uiState.isSkeletonLoading) R.string.comm_skeleton_loading else R.string.comm_skeleton_loaded
                  ),
                style = MaterialTheme.typography.bodySmall,
                color = LightOnSurfaceVariant,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = LightSurfaceContainer,
                  contentColor = LightOnSurface,
                  modifier =
                    Modifier.clip(CircleShape)
                      .clickable(onClick = onOpenAccessoryShowcase)
                      .testTag("open_accessory_showcase_btn"),
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Icon(Icons.Outlined.AutoAwesomeMotion, contentDescription = null, modifier = Modifier.size(14.dp))
                    Text("配件效果(6)", style = MaterialTheme.typography.labelSmall)
                  }
                }
                Surface(
                  shape = CircleShape,
                  color = LightSurfaceContainer,
                  contentColor = LightOnSurface,
                  modifier =
                    Modifier.clip(CircleShape)
                      .clickable(onClick = onReplaySkeleton)
                      .testTag("replay_skeleton_btn"),
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Text(stringResource(R.string.comm_skeleton_replay), style = MaterialTheme.typography.labelSmall)
                  }
                }
              }
            }
          }
        }
      }

      if (uiState.isSkeletonLoading) {
        items(3) { idx ->
          CommunitySkeletonFeedCard(
            shimmerBrush = shimmerBrush,
            mediaHeightDp = if (idx == 0) 176 else 142,
          )
        }
      } else if (filtered.isEmpty()) {
        item {
          Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Text(
              text = stringResource(R.string.comm_empty_hint),
              style = MaterialTheme.typography.bodyMedium,
              color = LightOnSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              Surface(
                shape = CircleShape,
                color = LightSurface,
                contentColor = LightOnSurface,
                modifier =
                  Modifier.height(38.dp)
                    .border(1.dp, LightOutlineVariant, CircleShape)
                    .clip(CircleShape)
                    .clickable { onOpenGlobalSearch(uiState.searchQuery) },
              ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                  Text(stringResource(R.string.comm_empty_global_btn), style = MaterialTheme.typography.labelSmall)
                }
              }
              Surface(
                shape = CircleShape,
                color = LightPrimary,
                contentColor = LightOnPrimary,
                modifier = Modifier.height(38.dp).clip(CircleShape).clickable(onClick = onOpenCompose),
              ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                  Text(stringResource(R.string.comm_empty_post_btn), style = MaterialTheme.typography.labelSmall)
                }
              }
            }
          }
        }
      } else {
        itemsIndexed(filtered, key = { _, p -> p.id }) { idx, post ->
          StaggeredRevealBlock(
            revealed = !uiState.isSkeletonLoading,
            staggerIndex = idx.coerceAtMost(4),
            cycleKey = uiState.skeletonCycle,
          ) {
            CommunityPostCard(
              post = post,
              isMusicPlaying = uiState.playingMusicPostId == post.id,
              onPostClick = { onOpenPostDetail(post.id) },
              onToggleFollow = { onToggleFollow(post.id) },
              onToggleLike = { onToggleLike(post.id) },
              onToggleBookmark = { onToggleBookmark(post.id) },
              onOpenComments = { onOpenComments(post.id) },
              onOpenShare = { onOpenShare(post.id) },
              onSelectPollOption = { optId -> onSelectPollOption(post.id, optId) },
              onConfirmOrResetPoll = { onConfirmOrResetPoll(post.id) },
              onApplyBounty = { onApplyBounty(post.id) },
              onToggleMusicPlay = { onToggleMusicPlay(post.id) },
              onSeekMusic = { sec -> onSeekMusic(post.id, sec) },
              onViewDocument = { doc -> onOpenDocumentPreview(doc) },
              onClickQuote = onOpenQuoteTarget,
              onShowToast = onShowToast,
            )
          }
        }
      }
    }
  }
}

@Composable
fun CommunityPostCard(
  post: CommunityPost,
  isMusicPlaying: Boolean,
  onPostClick: () -> Unit,
  onToggleFollow: () -> Unit,
  onToggleLike: () -> Unit,
  onToggleBookmark: () -> Unit,
  onOpenComments: () -> Unit,
  onOpenShare: () -> Unit,
  onSelectPollOption: (String) -> Unit,
  onConfirmOrResetPoll: () -> Unit,
  onApplyBounty: () -> Unit,
  onToggleMusicPlay: () -> Unit,
  onSeekMusic: (Int) -> Unit,
  onViewDocument: (CommunityDocument) -> Unit,
  onClickQuote: (CommunityQuoteTarget) -> Unit,
  onShowToast: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier =
      modifier
        .fillMaxWidth()
        .background(LightSurface)
        .padding(horizontal = 16.dp, vertical = 14.dp)
        .testTag("community_post_${post.id}"),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
        modifier = Modifier.weight(1f).clickable(onClick = onPostClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        if (post.avatarRes != null) {
          Image(
            painter = painterResource(id = post.avatarRes),
            contentDescription = post.authorName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(40.dp).clip(CircleShape),
          )
        } else {
          Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(LightSurfaceContainerHigh),
            contentAlignment = Alignment.Center,
          ) {
            Text(post.avatarInitial, style = MaterialTheme.typography.titleSmall, color = LightOnSurface)
          }
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
              text = post.authorName,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
              color = LightOnSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
            CreatorVBadge(tier = post.badgeTier, size = 15.dp)
            if (post.accessoryLabel != null) {
              Surface(shape = RoundedCornerShape(4.dp), color = LightSurfaceContainer) {
                Text(
                  text = post.accessoryLabel,
                  style = MaterialTheme.typography.labelSmall,
                  color = LightOnSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                )
              }
            }
          }
          Text(
            text = post.timeAndSource,
            style = MaterialTheme.typography.bodySmall,
            color = LightOnSurfaceVariant,
          )
        }
      }

      Surface(
        shape = CircleShape,
        color = if (post.isFollowed) LightPrimary else LightSurfaceContainer,
        contentColor = if (post.isFollowed) LightOnPrimary else LightOnSurface,
        modifier =
          Modifier.heightIn(min = 32.dp)
            .clip(CircleShape)
            .clickable(onClick = onToggleFollow)
            .testTag("follow_post_${post.id}"),
      ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
          Text(
            text = if (post.isFollowed) "已关注" else "关注",
            style = MaterialTheme.typography.labelSmall,
          )
        }
      }
    }

    val readFullText = stringResource(R.string.comm_read_full)
    Text(
      text =
        buildAnnotatedString {
          withStyle(SpanStyle(color = LightSecondary, fontWeight = FontWeight.Medium)) {
            append("${post.inlineTagPill} ")
          }
          append(post.summaryBody)
          if (post.showReadFullLink) {
            append(" ")
            withStyle(SpanStyle(color = LightOnSurface, fontWeight = FontWeight.SemiBold)) {
              append(readFullText)
            }
          }
        },
      style = MaterialTheme.typography.bodyMedium,
      color = LightOnSurface,
      modifier = Modifier.fillMaxWidth().clickable(onClick = onPostClick),
    )

    if (post.images.isNotEmpty()) {
      CommunityImagesBlock(
        images = post.images,
        modifier = Modifier.clickable(onClick = onPostClick),
      )
    }

    post.poll?.let { poll ->
      CommunityPollCard(
        postId = post.id,
        poll = poll,
        onSelectOption = onSelectPollOption,
        onConfirmOrReset = onConfirmOrResetPoll,
      )
    }

    post.linkCard?.let { link ->
      CommunityLinkCardView(linkCard = link, onToast = onShowToast)
    }

    post.document?.let { doc ->
      CommunityDocumentCard(
        document = doc,
        buttonLabel = stringResource(R.string.action_view_attachment),
        onViewClick = { onViewDocument(doc) },
      )
    }

    post.bountyCard?.let { bounty ->
      CommunityBountyCardView(
        postId = post.id,
        bounty = bounty,
        onApplyClick = onApplyBounty,
      )
    }

    post.musicTrack?.let { track ->
      MistyBlueMusicPlayerCard(
        track = track,
        isPlaying = isMusicPlaying,
        onTogglePlay = onToggleMusicPlay,
        onSeek = onSeekMusic,
      )
    }

    post.quoteCard?.let { quote ->
      CommunityQuoteCardView(
        quote = quote,
        onClick = { onClickQuote(quote.target) },
      )
    }

    HorizontalDivider(color = LightOutlineVariant.copy(alpha = 0.55f))

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      PostActionCell(
        countText = post.likeCount.toString(),
        active = post.isLiked,
        activeTint = ErrorRed,
        onClick = onToggleLike,
        modifier = Modifier.weight(1f).testTag("post_like_${post.id}"),
        icon = { tint -> StitchHeartIcon(filled = post.isLiked, tint = tint, modifier = Modifier.size(18.dp)) },
      )
      PostActionCell(
        countText = post.commentCount.toString(),
        active = false,
        activeTint = LightOnSurface,
        onClick = onOpenComments,
        modifier = Modifier.weight(1f).testTag("post_comment_${post.id}"),
        icon = { tint -> StitchCommentBubbleIcon(tint = tint, modifier = Modifier.size(18.dp)) },
      )
      PostActionCell(
        countText = post.bookmarkCount.toString(),
        active = post.isBookmarked,
        activeTint = LightSecondary,
        onClick = onToggleBookmark,
        modifier = Modifier.weight(1f).testTag("post_bookmark_${post.id}"),
        icon = { tint -> StitchStarIcon(filled = post.isBookmarked, tint = tint, modifier = Modifier.size(18.dp)) },
      )
      PostActionCell(
        countText = post.repostCount.toString(),
        active = false,
        activeTint = SuccessGreen,
        onClick = onOpenShare,
        modifier = Modifier.weight(1f).testTag("post_share_${post.id}"),
        icon = { tint -> StitchShareIcon(tint = tint, modifier = Modifier.size(18.dp)) },
      )
    }
  }
}

@Composable
private fun PostActionCell(
  countText: String,
  active: Boolean,
  activeTint: androidx.compose.ui.graphics.Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: @Composable (androidx.compose.ui.graphics.Color) -> Unit,
) {
  val tint = if (active) activeTint else LightOnSurfaceVariant
  Row(
    modifier =
      modifier
        .heightIn(min = 40.dp)
        .clip(CircleShape)
        .clickable(onClick = onClick),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    icon(tint)
    Spacer(Modifier.size(6.dp))
    Text(text = countText, style = MaterialTheme.typography.labelSmall, color = tint)
  }
}

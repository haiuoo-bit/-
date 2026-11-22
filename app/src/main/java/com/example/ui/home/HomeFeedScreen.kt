package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DarkOnSurface
import com.example.ui.theme.DarkOnSurfaceVariant
import com.example.ui.theme.DarkOutlineVariant
import com.example.ui.theme.DarkSecondary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceContainer
import com.example.ui.theme.DarkSurfaceContainerHigh
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LightOnPrimary
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightOutlineVariant
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceContainer
import com.example.ui.theme.LightSurfaceContainerHigh
import com.example.ui.theme.LightSurfaceContainerLow
import kotlinx.coroutines.delay

@Composable
fun HomeFeedScreen(
  uiState: HomeFeedUiState,
  onSelectChannel: (HomeChannel) -> Unit,
  onToggleGridMode: () -> Unit,
  onSearchClick: () -> Unit,
  onToggleAutoScroll: () -> Unit,
  onAutoScrollUserTouchPause: () -> Unit,
  onToggleDescExpanded: () -> Unit,
  onToggleFollow: (String) -> Unit,
  onToggleLike: (String) -> Unit,
  onToggleCollect: (String) -> Unit,
  onOpenCommentDrawer: () -> Unit,
  onOpenShareSheet: () -> Unit,
  onOpenArticleDetail: (String) -> Unit,
  onToggleGridCardLike: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val bgColor by
    animateColorAsState(
      targetValue = if (uiState.isGridMode) LightSurfaceContainerLow else DarkSurface,
      animationSpec = tween(180),
      label = "homeBg",
    )

  val immersiveScrollState = rememberScrollState()
  val gridScrollState = rememberScrollState()

  LaunchedEffect(uiState.activeChannel) {
    immersiveScrollState.scrollTo(0)
    gridScrollState.scrollTo(0)
  }

  val scrollProgress =
    if (uiState.isGridMode) 1f
    else ((immersiveScrollState.value - 40f) / 180f).coerceIn(0f, 1f)

  Box(modifier = modifier.fillMaxSize().background(bgColor)) {
    if (!uiState.isGridMode) {
      ImmersiveStoryFeedView(
        story = uiState.currentImmersiveStory,
        homeCommentsCount = uiState.homeCommentsCount,
        isAutoScrolling = uiState.isAutoScrolling,
        isDescExpanded = uiState.isDescExpanded,
        scrollState = immersiveScrollState,
        onToggleAutoScroll = onToggleAutoScroll,
        onUserDragPause = onAutoScrollUserTouchPause,
        onToggleDescExpanded = onToggleDescExpanded,
        onToggleFollow = { onToggleFollow(uiState.currentImmersiveStory.id) },
        onToggleLike = { onToggleLike(uiState.currentImmersiveStory.id) },
        onToggleCollect = { onToggleCollect(uiState.currentImmersiveStory.id) },
        onOpenComments = onOpenCommentDrawer,
        onOpenShare = onOpenShareSheet,
        onReadArticle = { onOpenArticleDetail(uiState.currentImmersiveStory.id) },
      )
    } else {
      DualColumnMasonryView(
        cards = uiState.currentMasonryCards,
        scrollState = gridScrollState,
        onCardClick = onOpenArticleDetail,
        onCardLikeClick = onToggleGridCardLike,
      )
    }

    HomeTopChannelBar(
      activeChannel = uiState.activeChannel,
      isGridMode = uiState.isGridMode,
      headerSolidAlpha = scrollProgress,
      onSelectChannel = onSelectChannel,
      onToggleGridMode = onToggleGridMode,
      onSearchClick = onSearchClick,
      modifier = Modifier.align(Alignment.TopCenter),
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ImmersiveStoryFeedView(
  story: FeedStory,
  homeCommentsCount: Int,
  isAutoScrolling: Boolean,
  isDescExpanded: Boolean,
  scrollState: androidx.compose.foundation.ScrollState,
  onToggleAutoScroll: () -> Unit,
  onUserDragPause: () -> Unit,
  onToggleDescExpanded: () -> Unit,
  onToggleFollow: () -> Unit,
  onToggleLike: () -> Unit,
  onToggleCollect: () -> Unit,
  onOpenComments: () -> Unit,
  onOpenShare: () -> Unit,
  onReadArticle: () -> Unit,
) {
  val density = LocalDensity.current
  val speedPxPerSec = remember(density) { with(density) { 22.dp.toPx() } }

  // Pause auto-scroll when the user drags manually
  LaunchedEffect(scrollState.isScrollInProgress, isAutoScrolling) {
    if (scrollState.isScrollInProgress && isAutoScrolling) {
      onUserDragPause()
    }
  }

  // Smooth frame-paced auto scroll
  LaunchedEffect(story.id, isAutoScrolling) {
    if (!isAutoScrolling) return@LaunchedEffect
    if (scrollState.value == 0) {
      delay(1100L)
    }
    var lastMs = withFrameMillis { it }
    while (isAutoScrolling) {
      val nowMs = withFrameMillis { it }
      val dt = ((nowMs - lastMs).coerceIn(0L, 48L)) / 1000f
      lastMs = nowMs
      if (scrollState.value >= scrollState.maxValue && scrollState.maxValue > 0) {
        onUserDragPause()
        break
      }
      val delta = speedPxPerSec * dt
      if (delta > 0f) {
        scrollState.dispatchRawDelta(delta)
      }
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier =
        Modifier.fillMaxSize()
          .verticalScroll(scrollState)
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onToggleAutoScroll,
          )
          .testTag("immersive_feed_scroller")
    ) {
      // Hero cover + seamless fade into DarkSurface
      Box(modifier = Modifier.fillMaxWidth().height(420.dp)) {
        Image(
          painter = painterResource(id = story.coverRes),
          contentDescription = story.title,
          contentScale = ContentScale.Crop,
          alignment = Alignment.TopCenter,
          modifier = Modifier.fillMaxSize(),
        )
        Box(
          modifier =
            Modifier.fillMaxWidth()
              .height(210.dp)
              .align(Alignment.BottomCenter)
              .background(
                Brush.verticalGradient(
                  colors = listOf(Color.Transparent, DarkSurface.copy(alpha = 0.68f), DarkSurface)
                )
              )
        )
      }

      // Story body with right rail clearance
      Column(
        modifier =
          Modifier.fillMaxWidth()
            .offset(y = (-22).dp)
            .padding(start = 16.dp, end = 76.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text(
          text = story.title,
          style = MaterialTheme.typography.headlineLarge,
          color = DarkOnSurface,
          modifier = Modifier.testTag("immersive_story_title"),
        )
        Text(
          text = story.lead,
          style = MaterialTheme.typography.bodyMedium,
          color = DarkOnSurface,
        )
        Text(
          text = story.tags.take(4).joinToString("  "),
          style = MaterialTheme.typography.bodySmall,
          color = DarkOnSurfaceVariant,
        )
        Spacer(Modifier.height(2.dp))
        story.paragraphs.forEach { para ->
          Text(
            text = para,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 23.sp),
            color = DarkOnSurface.copy(alpha = 0.86f),
          )
        }
        Spacer(Modifier.height(220.dp))
      }
    }

    // Centered translucent play triangle when paused
    AnimatedVisibility(
      visible = !isAutoScrolling,
      enter = fadeIn() + scaleIn(initialScale = 0.88f),
      exit = fadeOut() + scaleOut(targetScale = 0.88f),
      modifier = Modifier.align(Alignment.Center),
    ) {
      Icon(
        imageVector = Icons.Filled.PlayArrow,
        contentDescription = stringResource(R.string.cd_pause_indicator),
        tint = DarkOnSurface.copy(alpha = 0.34f),
        modifier = Modifier.size(88.dp).testTag("center_pause_indicator"),
      )
    }

    // Bottom gradient + "阅读文章" CTA + Author meta
    Column(
      modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
    ) {
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Transparent, DarkSurface.copy(alpha = 0.92f), DarkSurface)
              )
            )
            .padding(top = 24.dp, bottom = 10.dp),
        contentAlignment = Alignment.Center,
      ) {
        Surface(
          shape = CircleShape,
          color = DarkSurfaceContainer.copy(alpha = 0.94f),
          contentColor = DarkOnSurface,
          shadowElevation = 4.dp,
          modifier =
            Modifier.height(40.dp)
              .border(1.dp, DarkOutlineVariant, CircleShape)
              .clip(CircleShape)
              .clickable(onClick = onReadArticle)
              .testTag("read_article_btn"),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 24.dp),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.MenuBook,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Text(
              text = stringResource(R.string.action_read_article),
              style = MaterialTheme.typography.labelLarge,
            )
          }
        }
      }

      Column(
        modifier =
          Modifier.fillMaxWidth()
            .background(DarkSurface)
            .padding(start = 16.dp, end = 76.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Text(
            text = "@ ${story.authorName}",
            style = MaterialTheme.typography.titleMedium,
            color = DarkOnSurface,
          )
          if (story.isVerifiedBlueV) {
            BlueVBadge(size = 16.dp)
          }
        }

        Row(
          verticalAlignment = Alignment.Bottom,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Text(
            text = story.shortDesc,
            style = MaterialTheme.typography.bodySmall,
            color = DarkOnSurfaceVariant,
            maxLines = if (isDescExpanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
          )
          Text(
            text =
              if (isDescExpanded) {
                stringResource(R.string.action_collapse)
              } else {
                stringResource(R.string.action_expand)
              },
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = DarkOnSurface,
            modifier =
              Modifier.clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onToggleDescExpanded)
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .testTag("expand_desc_btn"),
          )
        }

        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          story.tags.take(4).forEach { tag ->
            Surface(
              shape = CircleShape,
              color = DarkSurfaceContainer,
              contentColor = DarkOnSurfaceVariant,
            ) {
              Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              )
            }
          }
        }
      }
    }

    // Right vertical action rail
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier =
        Modifier.align(Alignment.BottomEnd)
          .padding(end = 12.dp, bottom = 78.dp)
          .testTag("immersive_right_rail"),
    ) {
      // Avatar + Follow badge
      Box(
        modifier = Modifier.padding(bottom = 6.dp),
        contentAlignment = Alignment.BottomCenter,
      ) {
        Image(
          painter = painterResource(id = story.coverRes),
          contentDescription = stringResource(R.string.cd_author_avatar),
          contentScale = ContentScale.Crop,
          modifier =
            Modifier.size(44.dp)
              .clip(CircleShape)
              .border(2.dp, DarkOnSurface, CircleShape),
        )
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier.offset(y = 7.dp)
              .size(20.dp)
              .clip(CircleShape)
              .background(if (story.isFollowed) LightSurface else ErrorRed)
              .clickable(onClick = onToggleFollow)
              .testTag("right_rail_follow_btn"),
        ) {
          Icon(
            imageVector = if (story.isFollowed) Icons.Filled.Check else Icons.Filled.Add,
            contentDescription =
              stringResource(
                if (story.isFollowed) R.string.cd_unfollow_author else R.string.cd_follow_author
              ),
            tint = if (story.isFollowed) LightOnSurface else LightOnPrimary,
            modifier = Modifier.size(13.dp),
          )
        }
      }

      RailActionButton(
        label = story.likesDisplay,
        contentDescription = stringResource(R.string.cd_like_work),
        onClick = onToggleLike,
        modifier = Modifier.testTag("right_rail_like_btn"),
      ) {
        StitchHeartIcon(
          filled = story.isLiked,
          tint = if (story.isLiked) ErrorRed else DarkOnSurface,
          modifier = Modifier.size(28.dp),
        )
      }

      RailActionButton(
        label = homeCommentsCount.toString(),
        contentDescription = stringResource(R.string.cd_open_comments),
        onClick = onOpenComments,
        modifier = Modifier.testTag("right_rail_comment_btn"),
      ) {
        StitchCommentBubbleIcon(
          tint = DarkOnSurface,
          modifier = Modifier.size(28.dp),
        )
      }

      RailActionButton(
        label = story.collectsDisplay,
        contentDescription = stringResource(R.string.cd_collect_work),
        onClick = onToggleCollect,
        modifier = Modifier.testTag("right_rail_collect_btn"),
      ) {
        StitchStarIcon(
          filled = story.isCollected,
          tint = if (story.isCollected) DarkSecondary else DarkOnSurface,
          modifier = Modifier.size(28.dp),
        )
      }

      RailActionButton(
        label = stringResource(R.string.action_share),
        contentDescription = stringResource(R.string.cd_share_work),
        onClick = onOpenShare,
        modifier = Modifier.testTag("right_rail_share_btn"),
      ) {
        StitchShareIcon(
          tint = DarkOnSurface,
          modifier = Modifier.size(28.dp),
        )
      }
    }
  }
}

@Composable
private fun RailActionButton(
  label: String,
  contentDescription: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  iconContent: @Composable () -> Unit,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(3.dp),
    modifier =
      modifier
        .clip(RoundedCornerShape(10.dp))
        .clickable(onClick = onClick)
        .padding(horizontal = 4.dp, vertical = 2.dp),
  ) {
    Box(modifier = Modifier.size(30.dp), contentAlignment = Alignment.Center) {
      iconContent()
    }
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = DarkOnSurface,
    )
  }
}

@Composable
private fun HomeTopChannelBar(
  activeChannel: HomeChannel,
  isGridMode: Boolean,
  headerSolidAlpha: Float,
  onSelectChannel: (HomeChannel) -> Unit,
  onToggleGridMode: () -> Unit,
  onSearchClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val textColor = if (isGridMode) LightOnSurface else DarkOnSurface
  val mutedColor = if (isGridMode) LightOnSurfaceVariant else DarkOnSurface.copy(alpha = 0.72f)
  val solidBgColor = if (isGridMode) LightSurface else DarkSurface
  val dividerColor = if (isGridMode) LightOutlineVariant else DarkOutlineVariant

  Box(modifier = modifier.fillMaxWidth()) {
    // Scrim for dark immersive mode
    if (!isGridMode) {
      Box(
        modifier =
          Modifier.matchParentSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(DarkSurface.copy(alpha = 0.92f), DarkSurface.copy(alpha = 0.65f), Color.Transparent)
              )
            )
      )
    }

    // Solid background layer smoothly fading in
    Column(
      modifier = Modifier.matchParentSize().alpha(headerSolidAlpha).background(solidBgColor),
      verticalArrangement = Arrangement.Bottom,
    ) {
      HorizontalDivider(color = dividerColor.copy(alpha = 0.75f), thickness = 1.dp)
    }

    Row(
      modifier =
        Modifier.fillMaxWidth()
          .statusBarsPadding()
          .padding(horizontal = 16.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Spacer(Modifier.width(68.dp))

      // 4 Channel Tabs with animated bottom bar
      BoxWithConstraints(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.Center,
      ) {
        val tabWidth = 46.dp
        val spacing = 8.dp
        val totalWidth = tabWidth * 4 + spacing * 3
        val pillWidth = 16.dp
        val indicatorOffset by
          animateDpAsState(
            targetValue =
              (tabWidth + spacing) * activeChannel.index + (tabWidth - pillWidth) / 2,
            animationSpec = tween(durationMillis = 220),
            label = "channelIndicatorX",
          )

        Box(modifier = Modifier.width(totalWidth)) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            HomeChannel.entries.forEach { channel ->
              val selected = channel == activeChannel
              Box(
                contentAlignment = Alignment.Center,
                modifier =
                  Modifier.width(tabWidth)
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSelectChannel(channel) }
                    .testTag("home_channel_${channel.name.lowercase()}"),
              ) {
                Text(
                  text = stringResource(channel.labelRes),
                  style =
                    MaterialTheme.typography.titleMedium.copy(
                      fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    ),
                  color = if (selected) textColor else mutedColor,
                  maxLines = 1,
                )
              }
            }
          }

          Box(
            modifier =
              Modifier.align(Alignment.BottomStart)
                .offset(x = indicatorOffset)
                .width(pillWidth)
                .height(2.5.dp)
                .clip(CircleShape)
                .background(textColor)
          )
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
      ) {
        IconButton(
          onClick = onToggleGridMode,
          modifier = Modifier.size(34.dp).testTag("layout_toggle_btn"),
        ) {
          StitchGridModeIcon(
            isGridMode = isGridMode,
            tint = textColor,
            modifier = Modifier.size(20.dp),
          )
        }
        IconButton(
          onClick = onSearchClick,
          modifier = Modifier.size(34.dp).testTag("home_search_btn"),
        ) {
          Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = stringResource(R.string.cd_search_stories),
            tint = textColor,
            modifier = Modifier.size(21.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun DualColumnMasonryView(
  cards: List<MasonryCard>,
  scrollState: androidx.compose.foundation.ScrollState,
  onCardClick: (String) -> Unit,
  onCardLikeClick: (String) -> Unit,
) {
  val leftCards = remember(cards) { cards.filterIndexed { i, _ -> i % 2 == 0 } }
  val rightCards = remember(cards) { cards.filterIndexed { i, _ -> i % 2 == 1 } }

  Row(
    modifier =
      Modifier.fillMaxSize()
        .verticalScroll(scrollState)
        .statusBarsPadding()
        .padding(top = 60.dp, bottom = 18.dp, start = 12.dp, end = 12.dp)
        .testTag("dual_grid_feed"),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.Top,
  ) {
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      leftCards.forEach { card ->
        MasonryStoryCard(
          card = card,
          onClick = { onCardClick(card.storyId) },
          onLikeClick = { onCardLikeClick(card.id) },
        )
      }
    }
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      rightCards.forEach { card ->
        MasonryStoryCard(
          card = card,
          onClick = { onCardClick(card.storyId) },
          onLikeClick = { onCardLikeClick(card.id) },
        )
      }
    }
  }
}

@Composable
private fun MasonryStoryCard(
  card: MasonryCard,
  onClick: () -> Unit,
  onLikeClick: () -> Unit,
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = LightSurface,
    shadowElevation = 1.dp,
    modifier =
      Modifier.fillMaxWidth()
        .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
        .clip(RoundedCornerShape(16.dp))
        .clickable(onClick = onClick)
        .testTag("masonry_card_${card.id}"),
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .aspectRatio(card.aspectRatio)
            .background(LightSurfaceContainer)
      ) {
        Image(
          painter = painterResource(id = card.coverRes),
          contentDescription = card.title,
          contentScale = ContentScale.Crop,
          alignment = Alignment.TopCenter,
          modifier = Modifier.fillMaxSize(),
        )
        Surface(
          shape = CircleShape,
          color = DarkSurface.copy(alpha = 0.72f),
          contentColor = DarkOnSurface,
          modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
        ) {
          Text(
            text = card.tag,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
          )
        }
      }

      Text(
        text = card.title,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
        color = LightOnSurface,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 4.dp),
      )

      Row(
        modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, top = 2.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(5.dp),
          modifier = Modifier.weight(1f),
        ) {
          if (card.avatarRes != null) {
            Image(
              painter = painterResource(id = card.avatarRes),
              contentDescription = card.author,
              contentScale = ContentScale.Crop,
              modifier = Modifier.size(16.dp).clip(CircleShape),
            )
          } else {
            Box(
              modifier = Modifier.size(16.dp).clip(CircleShape).background(LightSurfaceContainerHigh),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = card.authorInitial,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = LightOnSurface,
              )
            }
          }
          Text(
            text = card.author,
            style = MaterialTheme.typography.labelSmall,
            color = LightOnSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(3.dp),
          modifier =
            Modifier.clip(RoundedCornerShape(8.dp))
              .clickable(onClick = onLikeClick)
              .padding(PaddingValues(horizontal = 4.dp, vertical = 2.dp)),
        ) {
          StitchHeartIcon(
            filled = card.isLiked,
            tint = if (card.isLiked) ErrorRed else LightOnSurfaceVariant,
            modifier = Modifier.size(15.dp),
          )
          Text(
            text = card.likesDisplay,
            style = MaterialTheme.typography.labelSmall,
            color = if (card.isLiked) ErrorRed else LightOnSurfaceVariant,
          )
        }
      }
    }
  }
}

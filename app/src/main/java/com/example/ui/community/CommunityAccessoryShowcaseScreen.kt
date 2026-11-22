package com.example.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.home.OverlayToastPill
import com.example.ui.plaza.PlazaSubScreenHeader
import com.example.ui.theme.LightOnPrimary
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightPrimary
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceContainer
import com.example.ui.theme.LightSurfaceContainerLow

@Composable
fun CommunityAccessoryShowcaseScreen(
  showcasePosts: List<CommunityPost>,
  playingMusicPostId: String?,
  toastMessage: String?,
  onBack: () -> Unit,
  onOpenCompose: () -> Unit,
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
  Scaffold(
    containerColor = LightSurfaceContainerLow,
    topBar = {
      PlazaSubScreenHeader(
        title = stringResource(R.string.comm_accessory_showcase_title),
        onBack = onBack,
        backTestTag = "comm_showcase_back_btn",
        trailingContent = {
          Surface(shape = CircleShape, color = LightSurfaceContainer) {
            Text(
              text = stringResource(R.string.comm_accessory_showcase_badge),
              style = MaterialTheme.typography.labelSmall,
              color = LightOnSurfaceVariant,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            )
          }
        },
      )
    },
    bottomBar = {
      Surface(color = LightSurface, shadowElevation = 4.dp) {
        Box(
          modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 10.dp),
          contentAlignment = Alignment.Center,
        ) {
          Surface(
            shape = CircleShape,
            color = LightPrimary,
            contentColor = LightOnPrimary,
            modifier =
              Modifier.fillMaxWidth()
                .widthIn(max = 600.dp)
                .height(44.dp)
                .clip(CircleShape)
                .clickable(onClick = onOpenCompose)
                .testTag("showcase_go_compose_btn"),
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text("去发动态试试", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
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
        modifier = Modifier.fillMaxSize().widthIn(max = 600.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth().background(LightSurface).padding(horizontal = 16.dp, vertical = 10.dp),
          ) {
            Text(
              text = "以下展示在「发动态」中挂载不同配件并发布到社区后，其他用户在信息流里看到的实际卡片样式。",
              style = MaterialTheme.typography.bodySmall,
              color = LightOnSurfaceVariant,
            )
          }
        }

        items(showcasePosts, key = { it.id }) { post ->
          CommunityPostCard(
            post = post,
            isMusicPlaying = playingMusicPostId == post.id,
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

      OverlayToastPill(
        message = toastMessage,
        darkPolarity = false,
        modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
      )
    }
  }
}

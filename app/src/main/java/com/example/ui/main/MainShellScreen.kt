package com.example.ui.main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.auth.AuthStep
import com.example.ui.auth.AuthUiState
import com.example.ui.auth.PrimaryPillButton
import com.example.ui.auth.SecondaryPillButton
import com.example.ui.community.CommunityCommentDrawerSheet
import com.example.ui.community.CommunityDocumentPreviewDialog
import com.example.ui.community.CommunityQuoteTarget
import com.example.ui.community.CommunityScreen
import com.example.ui.community.CommunityShareSheet
import com.example.ui.community.CommunityViewModel
import com.example.ui.home.CommentDrawerSheet
import com.example.ui.home.HomeFeedScreen
import com.example.ui.home.HomeFeedViewModel
import com.example.ui.home.OverlayToastPill
import com.example.ui.home.ShareWorkSheet
import com.example.ui.plaza.BountyPanel
import com.example.ui.plaza.PlazaScreen
import com.example.ui.plaza.PlazaViewModel
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.IslandTheme
import com.example.ui.theme.LightSurfaceContainerLow
import com.example.ui.update.FloatingUpdatePill
import com.example.ui.update.VersionUpdateDialog

@Composable
fun MainShellScreen(
  uiState: AuthUiState,
  homeViewModel: HomeFeedViewModel,
  plazaViewModel: PlazaViewModel,
  communityViewModel: CommunityViewModel,
  onOpenArticleDetail: (String) -> Unit,
  onOpenCommunityPostDetail: (String) -> Unit,
  onOpenCommunityCompose: () -> Unit,
  onOpenCommunityAccessoryShowcase: () -> Unit,
  onOpenCommunityGlobalSearch: (String) -> Unit,
  onOpenGlobalSearch: () -> Unit,
  onOpenRankings: () -> Unit,
  onOpenBounty: (BountyPanel) -> Unit,
  onOpenScripts: () -> Unit,
  onOpenRelayReviews: () -> Unit,
  onOpenTagFinder: (String?) -> Unit,
  onOpenEmulators: () -> Unit,
  onOpenMarket: () -> Unit,
  onBackToStep: (AuthStep) -> Unit,
  onResetAgreementAndPreviewStart: () -> Unit,
) {
  var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }
  var hasUnreadMessages by rememberSaveable { mutableStateOf(true) }
  val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
  val plazaState by plazaViewModel.uiState.collectAsStateWithLifecycle()
  val communityState by communityViewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    homeViewModel.maybeShowInitialUpdatePrompt()
  }

  val isNavDark = selectedTab == MainTab.HOME && !homeState.isGridMode

  Scaffold(
    containerColor = if (isNavDark) DarkSurface else LightSurfaceContainerLow,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    bottomBar = {
      MainBottomNavBar(
        selectedTab = selectedTab,
        hasUnreadMessages = hasUnreadMessages,
        darkTheme = isNavDark,
        onSelectTab = { tab ->
          selectedTab = tab
          if (tab == MainTab.MESSAGES) {
            hasUnreadMessages = false
          }
        },
      )
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      Crossfade(
        targetState = selectedTab,
        animationSpec = tween(durationMillis = 140),
        label = "mainTabCrossfade",
      ) { tab ->
        if (tab == MainTab.HOME) {
          HomeFeedScreen(
            uiState = homeState,
            onSelectChannel = homeViewModel::selectChannel,
            onToggleGridMode = homeViewModel::toggleGridMode,
            onSearchClick = onOpenGlobalSearch,
            onToggleAutoScroll = homeViewModel::toggleAutoScroll,
            onAutoScrollUserTouchPause = { homeViewModel.setAutoScrollPlaying(false) },
            onToggleDescExpanded = homeViewModel::toggleDescExpanded,
            onToggleFollow = homeViewModel::toggleFollowStory,
            onToggleLike = homeViewModel::toggleLikeStory,
            onToggleCollect = homeViewModel::toggleCollectStory,
            onOpenCommentDrawer = homeViewModel::openCommentDrawer,
            onOpenShareSheet = homeViewModel::openShareSheet,
            onOpenArticleDetail = onOpenArticleDetail,
            onToggleGridCardLike = homeViewModel::toggleGridCardLike,
          )
        } else if (tab == MainTab.PLAZA) {
          PlazaScreen(
            uiState = plazaState,
            onOpenGlobalSearch = onOpenGlobalSearch,
            onOpenRankings = onOpenRankings,
            onOpenBounty = onOpenBounty,
            onOpenScripts = onOpenScripts,
            onOpenRelayReviews = onOpenRelayReviews,
            onOpenTagFinder = onOpenTagFinder,
            onOpenEmulators = onOpenEmulators,
            onOpenMarket = onOpenMarket,
            onOpenArticleDetail = onOpenArticleDetail,
            onJumpToHomeTab = { selectedTab = MainTab.HOME },
            onSelectTag = plazaViewModel::selectPlazaTag,
            onSelectKind = plazaViewModel::selectPlazaKind,
            onResetFilters = plazaViewModel::resetPlazaFilters,
            onOpenNoticeModal = { plazaViewModel.setNoticeModalOpen(true) },
            onDismissNoticeModal = { plazaViewModel.setNoticeModalOpen(false) },
            onOpenCategorySheet = { plazaViewModel.setCategorySheetOpen(true) },
            onDismissCategorySheet = { plazaViewModel.setCategorySheetOpen(false) },
            onShowToast = plazaViewModel::showToast,
          )
        } else if (tab == MainTab.COMMUNITY) {
          CommunityScreen(
            uiState = communityState,
            onQueryChange = communityViewModel::updateSearchQuery,
            onSelectTopic = communityViewModel::selectTopic,
            onOpenGlobalSearch = onOpenCommunityGlobalSearch,
            onOpenCompose = onOpenCommunityCompose,
            onOpenAccessoryShowcase = onOpenCommunityAccessoryShowcase,
            onReplaySkeleton = communityViewModel::replaySkeletonLoad,
            onOpenPostDetail = onOpenCommunityPostDetail,
            onToggleFollow = communityViewModel::toggleFollow,
            onToggleLike = communityViewModel::toggleLike,
            onToggleBookmark = communityViewModel::toggleBookmark,
            onOpenComments = communityViewModel::openCommentDrawer,
            onOpenShare = communityViewModel::openShareSheet,
            onSelectPollOption = communityViewModel::selectPollOption,
            onConfirmOrResetPoll = communityViewModel::confirmOrResetPoll,
            onApplyBounty = communityViewModel::applyBounty,
            onToggleMusicPlay = communityViewModel::toggleMusicPlay,
            onSeekMusic = communityViewModel::seekMusic,
            onOpenDocumentPreview = communityViewModel::openDocumentPreview,
            onOpenQuoteTarget = { target ->
              when (target) {
                CommunityQuoteTarget.SCRIPT_LOBBY -> onOpenScripts()
                CommunityQuoteTarget.WECHAT_SKIN -> onOpenEmulators()
              }
            },
            onShowToast = communityViewModel::showToast,
          )
        } else {
          val tabLabel =
            when (tab) {
              MainTab.HOME -> stringResource(R.string.nav_home)
              MainTab.PLAZA -> stringResource(R.string.nav_plaza)
              MainTab.COMMUNITY -> stringResource(R.string.nav_community)
              MainTab.MESSAGES -> stringResource(R.string.nav_messages)
              MainTab.ME -> stringResource(R.string.nav_me)
            }

          Column(
            modifier =
              Modifier.fillMaxWidth()
                .widthIn(max = 600.dp)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            Surface(
              shape = RoundedCornerShape(24.dp),
              color = MaterialTheme.colorScheme.surface,
              shadowElevation = 2.dp,
              modifier = Modifier.fillMaxWidth(),
            ) {
              Column(modifier = Modifier.fillMaxWidth()) {
                Image(
                  painter = painterResource(id = R.drawable.img_floating_island_hero),
                  contentDescription = stringResource(R.string.cd_hero_illustration),
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxWidth().height(168.dp),
                )
                Column(
                  modifier = Modifier.padding(20.dp),
                  verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                  Surface(
                    shape = CircleShape,
                    color = IslandTheme.colors.success.copy(alpha = 0.12f),
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(6.dp),
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                      Box(
                        modifier =
                          Modifier.size(16.dp)
                            .clip(CircleShape)
                            .background(IslandTheme.colors.success.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center,
                      ) {
                        Icon(
                          imageVector = Icons.Filled.CheckCircle,
                          contentDescription = null,
                          tint = IslandTheme.colors.success,
                          modifier = Modifier.size(14.dp),
                        )
                      }
                      Text(
                        text = stringResource(R.string.home_preview_badge),
                        style = MaterialTheme.typography.labelSmall,
                        color = IslandTheme.colors.success,
                      )
                    }
                  }

                  Text(
                    text = stringResource(R.string.home_preview_title, tabLabel),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("main_tab_title"),
                  )
                  Text(
                    text =
                      stringResource(
                        R.string.home_preview_subtitle,
                        uiState.lastAccountDisplay,
                        uiState.lastLoginMethodLabel,
                      ),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                  )
                  Text(
                    text = stringResource(R.string.home_preview_tab_hint, tabLabel),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              }
            }

            if (tab == MainTab.ME) {
              PrimaryPillButton(
                text = stringResource(R.string.action_check_update_v250),
                loadingText = "",
                isLoading = false,
                onClick = homeViewModel::openUpdateModal,
                modifier = Modifier.testTag("check_update_v250_btn"),
              )

              if (!hasUnreadMessages) {
                SecondaryPillButton(
                  text = stringResource(R.string.home_preview_restore_unread),
                  onClick = { hasUnreadMessages = true },
                  modifier = Modifier.testTag("restore_unread_dot_button"),
                )
              }

              SecondaryPillButton(
                text = stringResource(R.string.home_preview_replay_start),
                onClick = { onBackToStep(AuthStep.LOGIN_START) },
                modifier = Modifier.testTag("home_back_login_start"),
              )

              SecondaryPillButton(
                text = stringResource(R.string.home_preview_reset_agreement),
                onClick = onResetAgreementAndPreviewStart,
                modifier = Modifier.testTag("home_reset_agreement"),
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                SecondaryPillButton(
                  text = stringResource(R.string.home_preview_jump_code),
                  onClick = { onBackToStep(AuthStep.PHONE_CODE) },
                  modifier = Modifier.weight(1f),
                )
                SecondaryPillButton(
                  text = stringResource(R.string.home_preview_jump_password),
                  onClick = { onBackToStep(AuthStep.PHONE_PASSWORD) },
                  modifier = Modifier.weight(1f),
                )
              }

              SecondaryPillButton(
                text = stringResource(R.string.home_preview_jump_forgot),
                onClick = { onBackToStep(AuthStep.FORGOT_PASSWORD) },
              )
            }
          }
        }
      }

      FloatingUpdatePill(
        visible = homeState.showFloatingUpdatePill,
        progressPct = homeState.updateProgressPct,
        isReady = homeState.isUpdateReadyToInstall,
        onReopenModal = homeViewModel::openUpdateModal,
        modifier =
          Modifier.align(Alignment.BottomCenter)
            .padding(bottom = if (selectedTab == MainTab.HOME && !homeState.isGridMode) 172.dp else 20.dp),
      )

      OverlayToastPill(
        message = communityState.toastMessage ?: plazaState.toastMessage ?: homeState.toastMessage,
        darkPolarity = isNavDark,
        modifier =
          Modifier.align(Alignment.BottomCenter)
            .padding(bottom = if (selectedTab == MainTab.HOME && !homeState.isGridMode) 118.dp else 72.dp),
      )
    }
  }

  if (homeState.isCommentDrawerOpen) {
    CommentDrawerSheet(
      totalCommentsCount = homeState.homeCommentsCount,
      comments = homeState.comments,
      replyTarget = homeState.replyTarget,
      onDismiss = homeViewModel::closeCommentDrawer,
      onReplyClick = homeViewModel::setReplyTarget,
      onClearReplyTarget = homeViewModel::clearReplyTarget,
      onToggleCommentLike = homeViewModel::toggleCommentLike,
      onToggleCollapseReplies = homeViewModel::toggleSubRepliesCollapsed,
      onSubmitComment = { text -> homeViewModel.submitComment(text, showSyncToast = false) },
    )
  }

  if (homeState.isShareSheetOpen) {
    val story = homeState.currentImmersiveStory
    ShareWorkSheet(
      storyTitle = story.title,
      authorName = story.authorName,
      onDismiss = homeViewModel::closeShareSheet,
      onShowToast = homeViewModel::showToast,
    )
  }

  communityState.commentDrawerPostId?.let { drawerPostId ->
    val post = communityState.posts.find { it.id == drawerPostId } ?: communityState.showcasePosts.find { it.id == drawerPostId }
    CommunityCommentDrawerSheet(
      totalCommentsCount = post?.commentCount ?: 0,
      comments = communityState.commentsFor(drawerPostId),
      replyTarget = communityState.replyTarget,
      onDismiss = communityViewModel::closeCommentDrawer,
      onReplyClick = communityViewModel::setReplyTarget,
      onClearReplyTarget = communityViewModel::clearReplyTarget,
      onToggleCommentLike = { commentId, replyId -> communityViewModel.toggleCommentLike(drawerPostId, commentId, replyId) },
      onSubmitComment = { text -> communityViewModel.submitComment(drawerPostId, text) },
    )
  }

  communityState.shareSheetPostId?.let { sharePostId ->
    val post = communityState.posts.find { it.id == sharePostId } ?: communityState.showcasePosts.find { it.id == sharePostId }
    CommunityShareSheet(
      postSummary = post?.summaryBody ?: "",
      onDismiss = communityViewModel::closeShareSheet,
      onChannelClick = { msg -> communityViewModel.recordShare(sharePostId, msg) },
    )
  }

  communityState.previewDocument?.let { doc ->
    CommunityDocumentPreviewDialog(
      document = doc,
      onDismiss = communityViewModel::closeDocumentPreview,
      onShowToast = communityViewModel::showToast,
    )
  }

  VersionUpdateDialog(
    phase = homeState.updatePhase,
    progressPct = homeState.updateProgressPct,
    isReadyToInstall = homeState.isUpdateReadyToInstall,
    onStartDownload = homeViewModel::startSimulatedDownload,
    onReplayProgressDemo = homeViewModel::replaySimulatedDownloadFromStart,
    onMinimizeToBackground = homeViewModel::minimizeUpdateToBackground,
    onLater = homeViewModel::dismissUpdatePrompt,
    onInstallNow = homeViewModel::triggerInstallSimulation,
  )
}

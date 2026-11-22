package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.auth.AuthEvent
import com.example.ui.auth.AuthFlowCardContent
import com.example.ui.auth.AuthModalsHost
import com.example.ui.auth.AuthStep
import com.example.ui.auth.AuthViewModel
import com.example.ui.auth.SharedAuthScaffold
import com.example.ui.community.CommunityAccessoryShowcaseScreen
import com.example.ui.community.CommunityCommentDrawerSheet
import com.example.ui.community.CommunityComposeScreen
import com.example.ui.community.CommunityDocumentPreviewDialog
import com.example.ui.community.CommunityPostDetailScreen
import com.example.ui.community.CommunityQuoteTarget
import com.example.ui.community.CommunityShareSheet
import com.example.ui.community.CommunityViewModel
import com.example.ui.home.ArticleDetailScreen
import com.example.ui.home.HomeFeedViewModel
import com.example.ui.home.ShareWorkSheet
import com.example.ui.main.MainShellScreen
import com.example.ui.plaza.BountyPanel
import com.example.ui.plaza.EmulatorDetailScreen
import com.example.ui.plaza.MarketItemDetailScreen
import com.example.ui.plaza.PlazaBountyScreen
import com.example.ui.plaza.PlazaEmulatorsScreen
import com.example.ui.plaza.PlazaExpansionViewModel
import com.example.ui.plaza.PlazaRankingsScreen
import com.example.ui.plaza.PlazaRelayReviewsScreen
import com.example.ui.plaza.PlazaScriptsScreen
import com.example.ui.plaza.PlazaViewModel
import com.example.ui.plaza.PopojiaMarketScreen
import com.example.ui.plaza.RelayStationDetailScreen
import com.example.ui.plaza.ScriptActivityDetailScreen
import com.example.ui.plaza.SearchScope
import com.example.ui.search.GlobalSearchScreen
import com.example.ui.search.TagFinderScreen
import com.example.ui.theme.IslandAppTheme

private object AuthRoute {
  const val AUTH_FLOW = "auth_flow"
  const val HOME_FEED_PREVIEW = "home_feed_preview"
  const val ARTICLE_DETAIL = "article_detail"
  const val COMMUNITY_POST_DETAIL = "community_post_detail"
  const val COMMUNITY_COMPOSE = "community_compose"
  const val COMMUNITY_ACCESSORY_SHOWCASE = "community_accessory_showcase"
  const val PLAZA_RANKINGS = "plaza_rankings"
  const val PLAZA_BOUNTY = "plaza_bounty"
  const val PLAZA_SCRIPTS = "plaza_scripts"
  const val PLAZA_RELAY = "plaza_relay"
  const val TAG_FINDER = "tag_finder"
  const val GLOBAL_SEARCH = "global_search"
  const val PLAZA_EMULATORS = "plaza_emulators"
  const val PLAZA_EMULATOR_DETAIL = "plaza_emulator_detail"
  const val PLAZA_MARKET = "plaza_market"
  const val PLAZA_MARKET_DETAIL = "plaza_market_detail"
  const val PLAZA_RELAY_DETAIL = "plaza_relay_detail"
  const val PLAZA_SCRIPT_DETAIL = "plaza_script_detail"
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      IslandAppTheme(darkTheme = false) {
        val navController = rememberNavController()
        val authViewModel: AuthViewModel = viewModel()
        val homeViewModel: HomeFeedViewModel = viewModel()
        val plazaViewModel: PlazaViewModel = viewModel()
        val expansionViewModel: PlazaExpansionViewModel = viewModel()
        val communityViewModel: CommunityViewModel = viewModel()
        val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
        val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
        val plazaState by plazaViewModel.uiState.collectAsStateWithLifecycle()
        val expansionState by expansionViewModel.uiState.collectAsStateWithLifecycle()
        val communityState by communityViewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
          authViewModel.events.collect { event ->
            when (event) {
              AuthEvent.NavigateToHome -> {
                navController.navigate(AuthRoute.HOME_FEED_PREVIEW) {
                  launchSingleTop = true
                }
              }
            }
          }
        }

        val openArticle: (String) -> Unit = { storyId ->
          homeViewModel.openStoryDetail(storyId)
          navController.navigate(AuthRoute.ARTICLE_DETAIL) {
            launchSingleTop = true
          }
        }

        val openGlobalSearch: (SearchScope, String?) -> Unit = { scope, initialQuery ->
          plazaViewModel.prepareGlobalSearch(scope = scope, initialQuery = initialQuery)
          navController.navigate(AuthRoute.GLOBAL_SEARCH) {
            launchSingleTop = true
          }
        }

        val openTagFinder: (String?) -> Unit = { initialTag ->
          plazaViewModel.openTagDetail(initialTag)
          navController.navigate(AuthRoute.TAG_FINDER) {
            launchSingleTop = true
          }
        }

        val openBounty: (BountyPanel) -> Unit = { panel ->
          plazaViewModel.selectBountyPanel(panel)
          navController.navigate(AuthRoute.PLAZA_BOUNTY) {
            launchSingleTop = true
          }
        }

        NavHost(
          navController = navController,
          startDestination = AuthRoute.AUTH_FLOW,
          enterTransition = { fadeIn(tween(180)) },
          exitTransition = { fadeOut(tween(140)) },
          popEnterTransition = { fadeIn(tween(180)) },
          popExitTransition = { fadeOut(tween(140)) },
        ) {
          composable(AuthRoute.AUTH_FLOW) {
            BackHandler(enabled = uiState.currentStep != AuthStep.LOGIN_START) {
              authViewModel.navigateBack()
            }

            SharedAuthScaffold {
              AuthFlowCardContent(
                uiState = uiState,
                onNavigateToStep = authViewModel::navigateToStep,
                onOneClickLogin = authViewModel::onOneClickLoginClicked,
                onPhoneChange = authViewModel::onPhoneChange,
                onClearPhone = authViewModel::clearPhone,
                onCodeChange = authViewModel::onSmsCodeChange,
                onSendCodeClick = authViewModel::onSendCodeClicked,
                onPasswordChange = authViewModel::onPasswordChange,
                onTogglePasswordVisibility = authViewModel::togglePasswordVisibility,
                onNewPasswordChange = authViewModel::onNewPasswordChange,
                onToggleNewPasswordVisibility = authViewModel::toggleNewPasswordVisibility,
                onCodeLoginSubmit = authViewModel::onCodeLoginSubmit,
                onPasswordLoginSubmit = authViewModel::onPasswordLoginSubmit,
                onResetPasswordSubmit = authViewModel::onResetPasswordSubmit,
                onToggleAgreed = authViewModel::setAgreed,
                onOpenPolicy = authViewModel::openPolicyDrawer,
              )
            }
          }

          composable(AuthRoute.HOME_FEED_PREVIEW) {
            MainShellScreen(
              uiState = uiState,
              homeViewModel = homeViewModel,
              plazaViewModel = plazaViewModel,
              communityViewModel = communityViewModel,
              onOpenArticleDetail = openArticle,
              onOpenCommunityPostDetail = { postId ->
                communityViewModel.openPostDetail(postId)
                navController.navigate(AuthRoute.COMMUNITY_POST_DETAIL) { launchSingleTop = true }
              },
              onOpenCommunityCompose = {
                navController.navigate(AuthRoute.COMMUNITY_COMPOSE) { launchSingleTop = true }
              },
              onOpenCommunityAccessoryShowcase = {
                navController.navigate(AuthRoute.COMMUNITY_ACCESSORY_SHOWCASE) { launchSingleTop = true }
              },
              onOpenCommunityGlobalSearch = { query ->
                openGlobalSearch(SearchScope.COMMUNITY, query.ifBlank { null })
              },
              onOpenGlobalSearch = { openGlobalSearch(SearchScope.ALL, null) },
              onOpenRankings = {
                navController.navigate(AuthRoute.PLAZA_RANKINGS) { launchSingleTop = true }
              },
              onOpenBounty = openBounty,
              onOpenScripts = {
                navController.navigate(AuthRoute.PLAZA_SCRIPTS) { launchSingleTop = true }
              },
              onOpenRelayReviews = {
                navController.navigate(AuthRoute.PLAZA_RELAY) { launchSingleTop = true }
              },
              onOpenTagFinder = openTagFinder,
              onOpenEmulators = {
                navController.navigate(AuthRoute.PLAZA_EMULATORS) { launchSingleTop = true }
              },
              onOpenMarket = {
                navController.navigate(AuthRoute.PLAZA_MARKET) { launchSingleTop = true }
              },
              onBackToStep = { step ->
                authViewModel.navigateToStep(step)
                navController.popBackStack(AuthRoute.AUTH_FLOW, inclusive = false)
              },
              onResetAgreementAndPreviewStart = {
                authViewModel.resetDemoAgreement()
                navController.popBackStack(AuthRoute.AUTH_FLOW, inclusive = false)
              },
            )
          }

          composable(AuthRoute.COMMUNITY_POST_DETAIL) {
            BackHandler { navController.popBackStack() }
            val activePost = communityState.activeDetailPost
            CommunityPostDetailScreen(
              post = activePost,
              comments = communityState.commentsFor(activePost.id),
              sortByHot = communityState.commentSortHot,
              replyTarget = communityState.replyTarget,
              isMusicPlaying = communityState.playingMusicPostId == activePost.id,
              toastMessage = communityState.toastMessage,
              onBack = { navController.popBackStack() },
              onToggleFollow = { communityViewModel.toggleFollow(activePost.id) },
              onToggleLike = { communityViewModel.toggleLike(activePost.id) },
              onToggleBookmark = { communityViewModel.toggleBookmark(activePost.id) },
              onOpenShare = { communityViewModel.openShareSheet(activePost.id) },
              onSelectPollOption = { optId -> communityViewModel.selectPollOption(activePost.id, optId) },
              onConfirmOrResetPoll = { communityViewModel.confirmOrResetPoll(activePost.id) },
              onApplyBounty = { communityViewModel.applyBounty(activePost.id) },
              onToggleMusicPlay = { communityViewModel.toggleMusicPlay(activePost.id) },
              onSeekMusic = { sec -> communityViewModel.seekMusic(activePost.id, sec) },
              onViewDocument = communityViewModel::openDocumentPreview,
              onClickQuote = { target ->
                when (target) {
                  CommunityQuoteTarget.SCRIPT_LOBBY -> navController.navigate(AuthRoute.PLAZA_SCRIPTS) { launchSingleTop = true }
                  CommunityQuoteTarget.WECHAT_SKIN -> navController.navigate(AuthRoute.PLAZA_EMULATORS) { launchSingleTop = true }
                }
              },
              onToggleSort = communityViewModel::toggleCommentSort,
              onReplyClick = communityViewModel::setReplyTarget,
              onClearReplyTarget = communityViewModel::clearReplyTarget,
              onToggleCommentLike = { commentId, replyId ->
                communityViewModel.toggleCommentLike(activePost.id, commentId, replyId)
              },
              onSubmitComment = { content -> communityViewModel.submitComment(activePost.id, content) },
              onShowToast = communityViewModel::showToast,
            )

            communityState.shareSheetPostId?.let { sharePostId ->
              CommunityShareSheet(
                postSummary = activePost.summaryBody,
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
          }

          composable(AuthRoute.COMMUNITY_COMPOSE) {
            BackHandler { navController.popBackStack() }
            CommunityComposeScreen(
              toastMessage = communityState.toastMessage,
              onCancel = { navController.popBackStack() },
              onOpenAccessoryShowcase = {
                navController.navigate(AuthRoute.COMMUNITY_ACCESSORY_SHOWCASE) { launchSingleTop = true }
              },
              onShowToast = communityViewModel::showToast,
              onPublish = { body, tags, images, poll, doc, link, bounty, music ->
                communityViewModel.publishNewPost(body, tags, images, poll, doc, link, bounty, music)
                navController.popBackStack()
              },
            )
          }

          composable(AuthRoute.COMMUNITY_ACCESSORY_SHOWCASE) {
            BackHandler { navController.popBackStack() }
            CommunityAccessoryShowcaseScreen(
              showcasePosts = communityState.showcasePosts,
              playingMusicPostId = communityState.playingMusicPostId,
              toastMessage = communityState.toastMessage,
              onBack = { navController.popBackStack() },
              onOpenCompose = {
                navController.navigate(AuthRoute.COMMUNITY_COMPOSE) { launchSingleTop = true }
              },
              onOpenPostDetail = { postId ->
                communityViewModel.openPostDetail(postId)
                navController.navigate(AuthRoute.COMMUNITY_POST_DETAIL) { launchSingleTop = true }
              },
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
                  CommunityQuoteTarget.SCRIPT_LOBBY -> navController.navigate(AuthRoute.PLAZA_SCRIPTS) { launchSingleTop = true }
                  CommunityQuoteTarget.WECHAT_SKIN -> navController.navigate(AuthRoute.PLAZA_EMULATORS) { launchSingleTop = true }
                }
              },
              onShowToast = communityViewModel::showToast,
            )

            communityState.commentDrawerPostId?.let { drawerPostId ->
              val post = communityState.showcasePosts.find { it.id == drawerPostId } ?: communityState.posts.find { it.id == drawerPostId }
              CommunityCommentDrawerSheet(
                totalCommentsCount = post?.commentCount ?: 0,
                comments = communityState.commentsFor(drawerPostId),
                replyTarget = communityState.replyTarget,
                onDismiss = communityViewModel::closeCommentDrawer,
                onReplyClick = communityViewModel::setReplyTarget,
                onClearReplyTarget = communityViewModel::clearReplyTarget,
                onToggleCommentLike = { commentId, replyId ->
                  communityViewModel.toggleCommentLike(drawerPostId, commentId, replyId)
                },
                onSubmitComment = { text -> communityViewModel.submitComment(drawerPostId, text) },
              )
            }

            communityState.shareSheetPostId?.let { sharePostId ->
              val post = communityState.showcasePosts.find { it.id == sharePostId } ?: communityState.posts.find { it.id == sharePostId }
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
          }

          composable(AuthRoute.ARTICLE_DETAIL) {
            BackHandler {
              navController.popBackStack()
            }

            ArticleDetailScreen(
              uiState = homeState,
              onBack = { navController.popBackStack() },
              onToggleFollow = { homeViewModel.toggleFollowStory(homeState.detailStory.id) },
              onToggleLike = { homeViewModel.toggleLikeStory(homeState.detailStory.id) },
              onToggleCollect = { homeViewModel.toggleCollectStory(homeState.detailStory.id) },
              onOpenShareSheet = homeViewModel::openShareSheet,
              onToggleOfflineCache = homeViewModel::toggleOfflineCache,
              onToggleArticleSearch = homeViewModel::toggleArticleSearch,
              onSearchQueryChange = homeViewModel::updateArticleSearchQuery,
              onStepSearchMatch = homeViewModel::stepArticleSearchMatch,
              onOpenAttachmentPreview = homeViewModel::openAttachmentPreview,
              onCloseAttachmentPreview = homeViewModel::closeAttachmentPreview,
              onSubmitComment = { text -> homeViewModel.submitComment(text, showSyncToast = true) },
              onReplyClick = homeViewModel::setReplyTarget,
              onClearReplyTarget = homeViewModel::clearReplyTarget,
              onToggleCommentLike = homeViewModel::toggleCommentLike,
              onToggleCollapseReplies = homeViewModel::toggleSubRepliesCollapsed,
              onShowToast = homeViewModel::showToast,
              onOpenGlobalSearch = { query ->
                openGlobalSearch(SearchScope.WORKS, query.ifBlank { null })
              },
              onOpenTagFinder = { tag -> openTagFinder(tag) },
            )

            if (homeState.isShareSheetOpen) {
              ShareWorkSheet(
                storyTitle = homeState.detailStory.title,
                authorName = homeState.detailStory.authorName,
                onDismiss = homeViewModel::closeShareSheet,
                onShowToast = homeViewModel::showToast,
              )
            }
          }

          composable(AuthRoute.PLAZA_RANKINGS) {
            BackHandler { navController.popBackStack() }
            PlazaRankingsScreen(
              uiState = plazaState,
              onBack = { navController.popBackStack() },
              onSelectBoard = plazaViewModel::selectRankingBoard,
              onToggleVote = plazaViewModel::toggleRankingVote,
              onOpenStoryDetail = openArticle,
            )
          }

          composable(AuthRoute.PLAZA_BOUNTY) {
            BackHandler { navController.popBackStack() }
            PlazaBountyScreen(
              uiState = plazaState,
              onBack = { navController.popBackStack() },
              onSelectPanel = plazaViewModel::selectBountyPanel,
              onSelectTopic = plazaViewModel::selectBountyTopic,
              onOpenCreatorSheet = plazaViewModel::openCreatorSheet,
              onSubmitCustomRequest = plazaViewModel::submitCustomCommissionRequest,
              onOpenBountySheet = plazaViewModel::openBountySheet,
              onSubmitBountyApplication = plazaViewModel::submitBountyApplication,
              onShowToast = plazaViewModel::showToast,
            )
          }

          composable(AuthRoute.PLAZA_SCRIPTS) {
            BackHandler { navController.popBackStack() }
            PlazaScriptsScreen(
              uiState = plazaState,
              onBack = { navController.popBackStack() },
              onSelectFilter = plazaViewModel::selectScriptFilter,
              onOpenCreateLockModal = { plazaViewModel.setCreateScriptLockOpen(true) },
              onDismissCreateLockModal = { plazaViewModel.setCreateScriptLockOpen(false) },
              onApplyEarlyScript = plazaViewModel::submitEarlyScriptApplication,
              onOpenScriptJoinSheet = plazaViewModel::openScriptJoinSheet,
              onConfirmJoinScript = plazaViewModel::confirmJoinScript,
              onOpenScriptDetail = { script ->
                expansionViewModel.loadScriptDetail(script)
                navController.navigate(AuthRoute.PLAZA_SCRIPT_DETAIL) { launchSingleTop = true }
              },
              onShowToast = plazaViewModel::showToast,
            )
          }

          composable(AuthRoute.PLAZA_RELAY) {
            BackHandler { navController.popBackStack() }
            PlazaRelayReviewsScreen(
              uiState = plazaState,
              onBack = { navController.popBackStack() },
              onSelectFilter = plazaViewModel::selectRelayFilter,
              onOpenStationDetail = { station ->
                if (station != null) {
                  expansionViewModel.openRelayDetail(station.id)
                  navController.navigate(AuthRoute.PLAZA_RELAY_DETAIL) { launchSingleTop = true }
                } else {
                  plazaViewModel.openRelayStationDetail(null)
                }
              },
              onSubmitRating = plazaViewModel::submitRelayRating,
            )
          }

          composable(AuthRoute.PLAZA_EMULATORS) {
            BackHandler { navController.popBackStack() }
            PlazaEmulatorsScreen(
              uiState = expansionState,
              onBack = { navController.popBackStack() },
              onSelectCategory = expansionViewModel::selectEmulatorCategory,
              onOpenDetail = { emulatorId ->
                expansionViewModel.openEmulatorDetail(emulatorId)
                navController.navigate(AuthRoute.PLAZA_EMULATOR_DETAIL) { launchSingleTop = true }
              },
              onPurchaseEmulator = expansionViewModel::purchaseEmulator,
              onDownloadEmulator = expansionViewModel::startEmulatorDownload,
              onOpenGuide = { expansionViewModel.setEmulatorGuideOpen(true) },
              onDismissGuide = { expansionViewModel.setEmulatorGuideOpen(false) },
              onShowToast = expansionViewModel::showToast,
            )
          }

          composable(AuthRoute.PLAZA_EMULATOR_DETAIL) {
            BackHandler { navController.popBackStack() }
            EmulatorDetailScreen(
              uiState = expansionState,
              onBack = { navController.popBackStack() },
              onPurchaseEmulator = expansionViewModel::purchaseEmulator,
              onDownloadEmulator = expansionViewModel::startEmulatorDownload,
              onShowToast = expansionViewModel::showToast,
            )
          }

          composable(AuthRoute.PLAZA_MARKET) {
            BackHandler { navController.popBackStack() }
            PopojiaMarketScreen(
              uiState = expansionState,
              onBack = { navController.popBackStack() },
              onSelectCategory = expansionViewModel::selectMarketCategory,
              onOpenDetail = { itemId ->
                expansionViewModel.openMarketDetail(itemId)
                navController.navigate(AuthRoute.PLAZA_MARKET_DETAIL) { launchSingleTop = true }
              },
              onCardBuyOrPreview = expansionViewModel::onMarketCardActionClick,
              onConfirmPayment = expansionViewModel::confirmBuySheetPayment,
              onSwitchToPreview = expansionViewModel::switchBuySuccessToPreview,
              onStartSheetDownload = expansionViewModel::startSheetDocumentDownload,
              onDismissBuySheet = expansionViewModel::dismissBuySheet,
              onDismissPreviewSheet = expansionViewModel::dismissPreviewSheet,
              onOpenSellFlow = expansionViewModel::openSellFlow,
              onVerifyBetaCode = expansionViewModel::verifyBetaCode,
              onDismissBetaModal = expansionViewModel::dismissBetaModal,
              onPublishItem = expansionViewModel::publishMarketItem,
              onDismissSellSheet = expansionViewModel::dismissSellSheet,
              onShowToast = expansionViewModel::showToast,
            )
          }

          composable(AuthRoute.PLAZA_MARKET_DETAIL) {
            BackHandler { navController.popBackStack() }
            MarketItemDetailScreen(
              uiState = expansionState,
              onBack = { navController.popBackStack() },
              onToggleFavorite = expansionViewModel::toggleMarketItemFavorite,
              onPurchaseOrDownload = expansionViewModel::purchaseOrDownloadOnDetail,
              onShowToast = expansionViewModel::showToast,
            )
          }

          composable(AuthRoute.PLAZA_RELAY_DETAIL) {
            BackHandler { navController.popBackStack() }
            val station =
              plazaState.relayStations.find { it.id == expansionState.activeRelayId }
                ?: plazaState.relayStations.first()
            RelayStationDetailScreen(
              station = station,
              extended = expansionState.activeRelayExtended,
              toastMessage = expansionState.toastMessage,
              onBack = { navController.popBackStack() },
              onToggleImpression = expansionViewModel::toggleRelayImpression,
              onFeedbackNoteChange = expansionViewModel::updateRelayFeedbackNote,
              onSubmitRating = { stationId, stars ->
                plazaViewModel.submitRelayRating(stationId, stars)
                expansionViewModel.showToast("感谢测评！已更新 ${station.name} ${stars} 星评分")
              },
              onShowToast = expansionViewModel::showToast,
            )
          }

          composable(AuthRoute.PLAZA_SCRIPT_DETAIL) {
            BackHandler { navController.popBackStack() }
            ScriptActivityDetailScreen(
              detail = expansionState.scriptDetail,
              toastMessage = expansionState.toastMessage,
              onBack = { navController.popBackStack() },
              onSelectFaction = expansionViewModel::selectScriptFaction,
              onPrimaryAction = {
                expansionViewModel.advanceScriptDetailAction { scriptId, chosenFaction ->
                  plazaViewModel.confirmJoinScript(scriptId, chosenFaction)
                }
              },
              onUploadProof = expansionViewModel::uploadScriptProof,
              onShowToast = expansionViewModel::showToast,
            )
          }

          composable(AuthRoute.TAG_FINDER) {
            BackHandler {
              if (plazaState.selectedTagDetail != null) {
                plazaViewModel.openTagDetail(null)
              } else {
                navController.popBackStack()
              }
            }
            TagFinderScreen(
              uiState = plazaState,
              onBack = { navController.popBackStack() },
              onSelectCategory = plazaViewModel::selectTagCategory,
              onSearchQueryChange = plazaViewModel::updateTagSearchQuery,
              onOpenTagDetail = plazaViewModel::openTagDetail,
              onSelectWorkSort = plazaViewModel::selectTagWorkSort,
              onOpenArticleDetail = openArticle,
              onShowToast = plazaViewModel::showToast,
            )
          }

          composable(AuthRoute.GLOBAL_SEARCH) {
            BackHandler { navController.popBackStack() }
            GlobalSearchScreen(
              uiState = plazaState,
              onBack = { navController.popBackStack() },
              onQueryChange = plazaViewModel::updateSearchQuery,
              onSubmitSearch = plazaViewModel::submitSearchTerm,
              onSelectScope = plazaViewModel::selectSearchScope,
              onRemoveRecent = plazaViewModel::removeRecentSearch,
              onClearRecent = plazaViewModel::clearRecentSearches,
              onToggleGroupJoin = plazaViewModel::toggleGroupJoin,
              onToggleCreatorFollow = plazaViewModel::toggleSearchCreatorFollow,
              onOpenArticleDetail = openArticle,
              onOpenTagFinder = { openTagFinder(null) },
              onOpenScripts = {
                navController.navigate(AuthRoute.PLAZA_SCRIPTS) { launchSingleTop = true }
              },
              onOpenBounty = { openBounty(BountyPanel.BOUNTY) },
              onShowToast = plazaViewModel::showToast,
            )
          }
        }

        AuthModalsHost(
          activeModal = uiState.activeModal,
          onDismiss = authViewModel::dismissModal,
          onAgreementConfirm = authViewModel::confirmAgreementDialogAndLogin,
          onOpenPolicy = authViewModel::openPolicyDrawer,
          onAcceptPolicyDrawer = authViewModel::acceptPolicyFromDrawer,
          onLoginSuccessEnterHome = {
            authViewModel.dismissModal()
            navController.navigate(AuthRoute.HOME_FEED_PREVIEW) { launchSingleTop = true }
          },
          onResetSuccessBackToPasswordLogin = {
            authViewModel.dismissModal()
            authViewModel.navigateToStep(AuthStep.PHONE_PASSWORD)
          },
        )
      }
    }
  }
}

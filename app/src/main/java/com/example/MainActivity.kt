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
import com.example.ui.home.ArticleDetailScreen
import com.example.ui.home.HomeFeedViewModel
import com.example.ui.home.ShareWorkSheet
import com.example.ui.main.MainShellScreen
import com.example.ui.plaza.BountyPanel
import com.example.ui.plaza.PlazaBountyScreen
import com.example.ui.plaza.PlazaRankingsScreen
import com.example.ui.plaza.PlazaRelayReviewsScreen
import com.example.ui.plaza.PlazaScriptsScreen
import com.example.ui.plaza.PlazaViewModel
import com.example.ui.plaza.SearchScope
import com.example.ui.search.GlobalSearchScreen
import com.example.ui.search.TagFinderScreen
import com.example.ui.theme.IslandAppTheme

private object AuthRoute {
  const val AUTH_FLOW = "auth_flow"
  const val HOME_FEED_PREVIEW = "home_feed_preview"
  const val ARTICLE_DETAIL = "article_detail"
  const val PLAZA_RANKINGS = "plaza_rankings"
  const val PLAZA_BOUNTY = "plaza_bounty"
  const val PLAZA_SCRIPTS = "plaza_scripts"
  const val PLAZA_RELAY = "plaza_relay"
  const val TAG_FINDER = "tag_finder"
  const val GLOBAL_SEARCH = "global_search"
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
        val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
        val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
        val plazaState by plazaViewModel.uiState.collectAsStateWithLifecycle()

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
              onOpenArticleDetail = openArticle,
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
              onShowToast = plazaViewModel::showToast,
            )
          }

          composable(AuthRoute.PLAZA_RELAY) {
            BackHandler { navController.popBackStack() }
            PlazaRelayReviewsScreen(
              uiState = plazaState,
              onBack = { navController.popBackStack() },
              onSelectFilter = plazaViewModel::selectRelayFilter,
              onOpenStationDetail = plazaViewModel::openRelayStationDetail,
              onSubmitRating = plazaViewModel::submitRelayRating,
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

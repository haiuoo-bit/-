package com.example.ui.plaza

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class TagWorkSort(val label: String) {
  HOT("综合热度"),
  NEW("最新发布"),
  PLAY("最多人玩"),
}

data class PlazaUiState(
  // 08 广场
  val activeTag: String = "全部",
  val activeKind: String = "all",
  val isNoticeModalOpen: Boolean = false,
  val isCategorySheetOpen: Boolean = false,
  val latestWorks: List<PlazaLatestWork> = PlazaSeedData.latestWorks,
  val featuredActivities: List<PlazaActivityItem> = PlazaSeedData.featuredActivities,

  // 15 榜单
  val activeBoard: RankingBoardType = RankingBoardType.TOTAL,
  val remainingVotes: Int = 3,
  val rankingsByBoard: Map<RankingBoardType, List<RankingEntry>> = PlazaSeedData.rankingsByBoard,

  // 16 定制与悬赏
  val activeBountyPanel: BountyPanel = BountyPanel.CUSTOM,
  val activeBountyTopic: String = "全部",
  val creators: List<CreatorShowcaseItem> = PlazaSeedData.creators,
  val bounties: List<BountyRequestItem> = PlazaSeedData.bounties,
  val selectedCreatorForSheet: CreatorShowcaseItem? = null,
  val selectedBountyForSheet: BountyRequestItem? = null,

  // 17 剧本局
  val activeScriptFilter: ScriptFilter = ScriptFilter.ALL,
  val scripts: List<ScriptLobbyItem> = PlazaSeedData.scripts,
  val isCreateScriptLockOpen: Boolean = false,
  val activeScriptJoinSheet: ScriptLobbyItem? = null,

  // 19 中转测评
  val activeRelayFilter: RelayFilter = RelayFilter.ALL,
  val relayStations: List<RelayStationItem> = PlazaSeedData.relayStations,
  val selectedRelayStation: RelayStationItem? = null,

  // 10 标签找设定
  val activeTagCategory: String = "热门",
  val tagSearchQuery: String = "",
  val selectedTagDetail: String? = null,
  val tagWorkSort: TagWorkSort = TagWorkSort.HOT,
  val allTags: List<TagMeta> = PlazaSeedData.allTags,
  val tagWorks: List<TagWorkItem> = PlazaSeedData.tagWorks,

  // 51 全局搜索
  val searchScope: SearchScope = SearchScope.ALL,
  val searchQuery: String = "",
  val recentSearches: List<String> = PlazaSeedData.defaultRecentSearches,
  val searchGroups: List<SearchGroupItem> = PlazaSeedData.searchGroups,
  val searchWorks: List<SearchWorkItem> = PlazaSeedData.searchWorks,
  val searchChats: List<SearchChatItem> = PlazaSeedData.searchChats,
  val searchCreators: List<SearchCreatorItem> = PlazaSeedData.searchCreators,
  val searchPosts: List<SearchCommunityPostItem> = PlazaSeedData.searchCommunityPosts,

  val toastMessage: String? = null,
) {
  val filteredPlazaWorks: List<PlazaLatestWork>
    get() =
      if (activeTag == "全部") latestWorks
      else latestWorks.filter { work -> work.tags.any { it.contains(activeTag.removePrefix("#")) } }

  val filteredPlazaActivities: List<PlazaActivityItem>
    get() =
      featuredActivities.filter { act ->
        val kindOk = activeKind == "all" || act.kind == activeKind
        val tagOk = activeTag == "全部" || act.tag == activeTag
        kindOk && tagOk
      }

  val currentRankings: List<RankingEntry>
    get() = rankingsByBoard[activeBoard].orEmpty()

  val filteredCreators: List<CreatorShowcaseItem>
    get() =
      if (activeBountyTopic == "全部") creators
      else creators.filter { item -> item.topics.any { it.contains(activeBountyTopic) } }

  val filteredBounties: List<BountyRequestItem>
    get() =
      if (activeBountyTopic == "全部") bounties
      else bounties.filter { item -> item.topic == activeBountyTopic }

  val filteredScripts: List<ScriptLobbyItem>
    get() =
      when (activeScriptFilter) {
        ScriptFilter.ALL -> scripts
        ScriptFilter.OPEN -> scripts.filter { it.status == ScriptStatus.OPEN }
        ScriptFilter.ACTIVE -> scripts.filter { it.status == ScriptStatus.ACTIVE }
        ScriptFilter.ENDED -> scripts.filter { it.status == ScriptStatus.ENDED }
      }

  val filteredRelayStations: List<RelayStationItem>
    get() =
      if (activeRelayFilter == RelayFilter.ALL) relayStations
      else relayStations.filter { it.category == activeRelayFilter }

  val filteredTagsGrid: List<TagMeta>
    get() {
      val q = tagSearchQuery.trim()
      if (q.isNotEmpty()) {
        return allTags.filter { it.name.contains(q, ignoreCase = true) || it.category.contains(q, ignoreCase = true) }
      }
      return when (activeTagCategory) {
        "热门" -> allTags.filter { it.hot }
        "全部" -> allTags
        else -> allTags.filter { it.category == activeTagCategory }
      }
    }

  val filteredTagDetailWorks: List<TagWorkItem>
    get() {
      val tag = selectedTagDetail ?: return emptyList()
      val base = tagWorks.filter { it.tags.contains(tag) }.ifEmpty { tagWorks }
      return when (tagWorkSort) {
        TagWorkSort.HOT -> base.sortedByDescending { it.likes }
        TagWorkSort.NEW -> base.sortedByDescending { if (it.isNew) 1 else 0 }
        TagWorkSort.PLAY -> base.sortedByDescending { it.players }
      }
    }
}

class PlazaViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(PlazaUiState())
  val uiState: StateFlow<PlazaUiState> = _uiState.asStateFlow()

  private var toastJob: Job? = null

  fun showToast(message: String) {
    toastJob?.cancel()
    _uiState.update { it.copy(toastMessage = message) }
    toastJob =
      viewModelScope.launch {
        delay(2200)
        _uiState.update { it.copy(toastMessage = null) }
      }
  }

  // --- 08 广场 ---
  fun selectPlazaTag(tag: String) {
    _uiState.update { it.copy(activeTag = tag) }
  }

  fun selectPlazaKind(kind: String) {
    _uiState.update { it.copy(activeKind = kind, isCategorySheetOpen = false) }
  }

  fun resetPlazaFilters() {
    _uiState.update { it.copy(activeTag = "全部", activeKind = "all") }
  }

  fun setNoticeModalOpen(open: Boolean) {
    _uiState.update { it.copy(isNoticeModalOpen = open) }
  }

  fun setCategorySheetOpen(open: Boolean) {
    _uiState.update { it.copy(isCategorySheetOpen = open) }
  }

  // --- 15 榜单 ---
  fun selectRankingBoard(board: RankingBoardType) {
    _uiState.update { it.copy(activeBoard = board) }
  }

  fun toggleRankingVote(entryId: String) {
    val state = _uiState.value
    val board = state.activeBoard
    val entries = state.rankingsByBoard[board].orEmpty()
    val target = entries.find { it.id == entryId } ?: return
    if (!target.voted && state.remainingVotes <= 0) {
      showToast("今日 3 票已投完，明日 0 点刷新配额")
      return
    }
    val nextVoted = !target.voted
    val nextRemaining = if (nextVoted) state.remainingVotes - 1 else (state.remainingVotes + 1).coerceAtMost(3)
    val updatedEntries =
      entries.map { item ->
        if (item.id == entryId) {
          item.copy(
            voted = nextVoted,
            score = if (nextVoted) item.score + 12 else (item.score - 12).coerceAtLeast(0),
          )
        } else {
          item
        }
      }
    val updatedMap = state.rankingsByBoard.toMutableMap().apply { put(board, updatedEntries) }
    _uiState.update { it.copy(rankingsByBoard = updatedMap, remainingVotes = nextRemaining) }
    showToast(if (nextVoted) "已为 ${target.title} 投出 1 票（剩余 $nextRemaining 票）" else "已取消投票")
  }

  // --- 16 定制与悬赏 ---
  fun selectBountyPanel(panel: BountyPanel) {
    _uiState.update { it.copy(activeBountyPanel = panel) }
  }

  fun selectBountyTopic(topic: String) {
    _uiState.update { it.copy(activeBountyTopic = topic) }
  }

  fun openCreatorSheet(creator: CreatorShowcaseItem?) {
    _uiState.update { it.copy(selectedCreatorForSheet = creator) }
  }

  fun submitCustomCommissionRequest(creatorName: String, note: String) {
    _uiState.update { it.copy(selectedCreatorForSheet = null) }
    val summary = if (note.isBlank()) "标准人设长文约稿" else note.trim()
    showToast("已向 @$creatorName 发送约稿意向「$summary」")
  }

  fun openBountySheet(bounty: BountyRequestItem?) {
    _uiState.update { it.copy(selectedBountyForSheet = bounty) }
  }

  fun submitBountyApplication(bountyId: String) {
    val target = _uiState.value.bounties.find { it.id == bountyId } ?: return
    if (target.hasApplied) {
      _uiState.update { it.copy(selectedBountyForSheet = null) }
      showToast("你已投稿过该悬赏，请在消息中查看进度")
      return
    }
    _uiState.update { state ->
      state.copy(
        selectedBountyForSheet = null,
        bounties =
          state.bounties.map { item ->
            if (item.id == bountyId) {
              item.copy(hasApplied = true, applicantsCount = item.applicantsCount + 1)
            } else {
              item
            }
          },
      )
    }
    showToast("投稿接赏已提交，赏金处于平台担保托管中")
  }

  // --- 17 剧本局 ---
  fun selectScriptFilter(filter: ScriptFilter) {
    _uiState.update { it.copy(activeScriptFilter = filter) }
  }

  fun setCreateScriptLockOpen(open: Boolean) {
    _uiState.update { it.copy(isCreateScriptLockOpen = open) }
  }

  fun submitEarlyScriptApplication() {
    _uiState.update { it.copy(isCreateScriptLockOpen = false) }
    showToast("已提交开局权限申请，审核结果将在消息中心通知")
  }

  fun openScriptJoinSheet(script: ScriptLobbyItem?) {
    _uiState.update { it.copy(activeScriptJoinSheet = script) }
  }

  fun confirmJoinScript(scriptId: String, chosenRole: String) {
    _uiState.update { state ->
      state.copy(
        activeScriptJoinSheet = null,
        scripts =
          state.scripts.map { item ->
            if (item.id == scriptId) {
              val alreadyJoined = item.selectedRole != null
              item.copy(
                currentPlayers = if (alreadyJoined) item.currentPlayers else (item.currentPlayers + 1).coerceAtMost(item.maxPlayers),
                selectedRole = chosenRole,
              )
            } else {
              item
            }
          },
      )
    }
    showToast("上车成功！已锁定身份：$chosenRole")
  }

  // --- 19 中转测评 ---
  fun selectRelayFilter(filter: RelayFilter) {
    _uiState.update { it.copy(activeRelayFilter = filter) }
  }

  fun openRelayStationDetail(station: RelayStationItem?) {
    _uiState.update { it.copy(selectedRelayStation = station) }
  }

  fun submitRelayRating(stationId: String, stars: Int) {
    val clamped = stars.coerceIn(1, 5)
    _uiState.update { state ->
      val updated =
        state.relayStations.map { item ->
          if (item.id == stationId) {
            val prevCount = if (item.userRatedStars == null) item.reviewsCount else item.reviewsCount - 1
            val prevTotal = item.score * item.reviewsCount - (item.userRatedStars ?: 0)
            val nextCount = prevCount + 1
            val nextAvg = (((prevTotal + clamped) / nextCount) * 10.0).roundToInt() / 10.0
            item.copy(score = nextAvg.coerceIn(1.0, 5.0), reviewsCount = nextCount, userRatedStars = clamped)
          } else {
            item
          }
        }
      state.copy(
        relayStations = updated,
        selectedRelayStation = updated.find { it.id == stationId },
      )
    }
    showToast("感谢测评！已提交 ${clamped} 星实测打分")
  }

  // --- 10 标签找设定 ---
  fun selectTagCategory(category: String) {
    _uiState.update { it.copy(activeTagCategory = category) }
  }

  fun updateTagSearchQuery(query: String) {
    _uiState.update { it.copy(tagSearchQuery = query) }
  }

  fun openTagDetail(tagName: String?) {
    _uiState.update { it.copy(selectedTagDetail = tagName) }
  }

  fun selectTagWorkSort(sort: TagWorkSort) {
    _uiState.update { it.copy(tagWorkSort = sort) }
  }

  // --- 51 全局搜索 ---
  fun prepareGlobalSearch(scope: SearchScope = SearchScope.ALL, initialQuery: String? = null) {
    _uiState.update { state ->
      state.copy(
        searchScope = scope,
        searchQuery = initialQuery ?: state.searchQuery,
      )
    }
  }

  fun selectSearchScope(scope: SearchScope) {
    _uiState.update { it.copy(searchScope = scope) }
  }

  fun updateSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun submitSearchTerm(term: String) {
    val trimmed = term.trim()
    if (trimmed.isEmpty()) return
    _uiState.update { state ->
      val updatedRecent = (listOf(trimmed) + state.recentSearches.filterNot { it == trimmed }).take(8)
      state.copy(searchQuery = trimmed, recentSearches = updatedRecent)
    }
  }

  fun removeRecentSearch(term: String) {
    _uiState.update { state ->
      state.copy(recentSearches = state.recentSearches.filterNot { it == term })
    }
  }

  fun clearRecentSearches() {
    _uiState.update { it.copy(recentSearches = emptyList()) }
  }

  fun toggleGroupJoin(groupId: String) {
    val group = _uiState.value.searchGroups.find { it.id == groupId } ?: return
    if (group.status != GroupJoinStatus.IDLE) return
    val nextStatus =
      if (group.joinMode == GroupJoinMode.DIRECT) GroupJoinStatus.JOINED else GroupJoinStatus.PENDING
    _uiState.update { state ->
      state.copy(
        searchGroups =
          state.searchGroups.map { item ->
            if (item.id == groupId) {
              item.copy(
                status = nextStatus,
                membersCount = if (nextStatus == GroupJoinStatus.JOINED) item.membersCount + 1 else item.membersCount,
              )
            } else {
              item
            }
          }
      )
    }
    showToast(
      if (nextStatus == GroupJoinStatus.JOINED) {
        "已加入「${group.name}」，可前往消息中心聊天"
      } else {
        "入群申请已发送至群主，请耐心等待审核"
      }
    )
  }

  fun toggleSearchCreatorFollow(creatorId: String) {
    val target = _uiState.value.searchCreators.find { it.id == creatorId } ?: return
    val next = !target.isFollowed
    _uiState.update { state ->
      state.copy(
        searchCreators =
          state.searchCreators.map { item ->
            if (item.id == creatorId) item.copy(isFollowed = next) else item
          }
      )
    }
    showToast(if (next) "已关注 @${target.name}" else "已取消关注")
  }
}

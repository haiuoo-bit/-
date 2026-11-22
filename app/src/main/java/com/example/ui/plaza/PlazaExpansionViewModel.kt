package com.example.ui.plaza

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlazaExpansionUiState(
  // 54 & 55 小手机与模拟器
  val emulatorCategory: EmulatorCategory = EmulatorCategory.ALL,
  val emulators: List<EmulatorItem> = PlazaExpansionSeedData.emulators,
  val selectedEmulatorId: String = "retro-handheld",
  val isEmulatorGuideOpen: Boolean = false,

  // 56 & 70 破破甲集市
  val marketCategory: MarketCategory = MarketCategory.ALL,
  val marketItems: List<MarketItem> = PlazaExpansionSeedData.marketItems,
  val selectedMarketItemId: String = "doc-ooc-anchor",
  val buySheetItem: MarketItem? = null,
  val buyStep: MarketBuyStep = MarketBuyStep.HIDDEN,
  val previewSheetItem: MarketItem? = null,
  val sheetDownloadPct: Int = 0,
  val isSheetDownloading: Boolean = false,
  val isBetaVerified: Boolean = false,
  val isBetaModalOpen: Boolean = false,
  val isSellSheetOpen: Boolean = false,
  val isDetailUnlocking: Boolean = false,

  // 22 中转站详情
  val activeRelayId: String = "rs_island",
  val relayExtendedById: Map<String, RelayStationExtended> = PlazaExpansionSeedData.relayDetailsByStationId,

  // 21 剧本局活动详情
  val scriptDetail: ScriptActivityDetailState = ScriptActivityDetailState(),

  val toastMessage: String? = null,
) {
  val filteredEmulators: List<EmulatorItem>
    get() =
      when (emulatorCategory) {
        EmulatorCategory.ALL -> emulators
        EmulatorCategory.FREE -> emulators.filter { it.isFreeCategory }
        EmulatorCategory.PAID -> emulators.filter { !it.isFreeCategory }
      }

  val selectedEmulator: EmulatorItem
    get() = emulators.find { it.id == selectedEmulatorId } ?: emulators.first()

  val filteredMarketItems: List<MarketItem>
    get() =
      if (marketCategory == MarketCategory.ALL) marketItems
      else marketItems.filter { it.category == marketCategory }

  val selectedMarketItem: MarketItem
    get() = marketItems.find { it.id == selectedMarketItemId } ?: marketItems.first()

  val activeRelayExtended: RelayStationExtended
    get() = relayExtendedById[activeRelayId] ?: relayExtendedById.values.first()
}

class PlazaExpansionViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(PlazaExpansionUiState())
  val uiState: StateFlow<PlazaExpansionUiState> = _uiState.asStateFlow()

  private var toastJob: Job? = null
  private val emulatorDownloadJobs = mutableMapOf<String, Job>()
  private var sheetDownloadJob: Job? = null

  fun showToast(message: String) {
    toastJob?.cancel()
    _uiState.update { it.copy(toastMessage = message) }
    toastJob =
      viewModelScope.launch {
        delay(2200)
        _uiState.update { it.copy(toastMessage = null) }
      }
  }

  // --- 54 & 55 小手机与模拟器 ---
  fun selectEmulatorCategory(category: EmulatorCategory) {
    _uiState.update { it.copy(emulatorCategory = category) }
  }

  fun openEmulatorDetail(emulatorId: String) {
    _uiState.update { it.copy(selectedEmulatorId = emulatorId) }
  }

  fun setEmulatorGuideOpen(open: Boolean) {
    _uiState.update { it.copy(isEmulatorGuideOpen = open) }
  }

  fun purchaseEmulator(emulatorId: String) {
    val item = _uiState.value.emulators.find { it.id == emulatorId } ?: return
    if (item.isUnlocked || item.isUnlocking) return
    _uiState.update { state ->
      state.copy(
        emulators = state.emulators.map { if (it.id == emulatorId) it.copy(isUnlocking = true) else it }
      )
    }
    viewModelScope.launch {
      delay(900)
      _uiState.update { state ->
        state.copy(
          emulators =
            state.emulators.map {
              if (it.id == emulatorId) it.copy(isUnlocking = false, isUnlocked = true) else it
            }
        )
      }
      showToast("${item.title} 购买成功，已解锁下载并同步至「我的岛屿库」")
    }
  }

  fun startEmulatorDownload(emulatorId: String) {
    val item = _uiState.value.emulators.find { it.id == emulatorId } ?: return
    if (item.isDownloadDone) {
      showToast("${item.title} 已就绪，可随时打开安装")
      return
    }
    if (item.isDownloading || emulatorDownloadJobs.containsKey(emulatorId)) return

    _uiState.update { state ->
      state.copy(
        emulators =
          state.emulators.map {
            if (it.id == emulatorId) it.copy(isDownloading = true, downloadProgressPct = 0) else it
          }
      )
    }
    emulatorDownloadJobs[emulatorId] =
      viewModelScope.launch {
        var pct = 0
        while (pct < 100) {
          delay(180)
          pct = (pct + 16).coerceAtMost(100)
          _uiState.update { state ->
            state.copy(
              emulators =
                state.emulators.map {
                  if (it.id == emulatorId) {
                    it.copy(
                      downloadProgressPct = pct,
                      isDownloading = pct < 100,
                      isDownloadDone = pct >= 100,
                    )
                  } else {
                    it
                  }
                }
            )
          }
        }
        emulatorDownloadJobs.remove(emulatorId)
        showToast("${item.title} 下载完成")
      }
  }

  // --- 56 & 70 破破甲集市 ---
  fun selectMarketCategory(category: MarketCategory) {
    _uiState.update { it.copy(marketCategory = category) }
  }

  fun openMarketDetail(itemId: String) {
    _uiState.update { it.copy(selectedMarketItemId = itemId) }
  }

  fun onMarketCardActionClick(item: MarketItem) {
    if (item.isPurchased) {
      _uiState.update {
        it.copy(previewSheetItem = item, sheetDownloadPct = 0, isSheetDownloading = false)
      }
    } else {
      _uiState.update {
        it.copy(
          buySheetItem = item,
          buyStep = MarketBuyStep.CONFIRM,
          sheetDownloadPct = 0,
          isSheetDownloading = false,
        )
      }
    }
  }

  fun dismissBuySheet() {
    sheetDownloadJob?.cancel()
    _uiState.update { it.copy(buySheetItem = null, buyStep = MarketBuyStep.HIDDEN, isSheetDownloading = false) }
  }

  fun confirmBuySheetPayment() {
    val item = _uiState.value.buySheetItem ?: return
    _uiState.update { it.copy(buyStep = MarketBuyStep.LOADING) }
    viewModelScope.launch {
      delay(850)
      val unlocked = item.copy(isPurchased = true)
      _uiState.update { state ->
        state.copy(
          marketItems = state.marketItems.map { if (it.id == item.id) unlocked else it },
          buySheetItem = unlocked,
          buyStep = MarketBuyStep.SUCCESS,
        )
      }
    }
  }

  fun switchBuySuccessToPreview() {
    val item = _uiState.value.buySheetItem ?: return
    sheetDownloadJob?.cancel()
    _uiState.update {
      it.copy(
        buySheetItem = null,
        buyStep = MarketBuyStep.HIDDEN,
        previewSheetItem = item,
        sheetDownloadPct = 0,
        isSheetDownloading = false,
      )
    }
  }

  fun dismissPreviewSheet() {
    sheetDownloadJob?.cancel()
    _uiState.update { it.copy(previewSheetItem = null, isSheetDownloading = false) }
  }

  fun startSheetDocumentDownload() {
    if (_uiState.value.isSheetDownloading) return
    sheetDownloadJob?.cancel()
    _uiState.update { it.copy(isSheetDownloading = true, sheetDownloadPct = 0) }
    sheetDownloadJob =
      viewModelScope.launch {
        var pct = 0
        while (pct < 100) {
          delay(140)
          pct = (pct + 20).coerceAtMost(100)
          _uiState.update { it.copy(sheetDownloadPct = pct, isSheetDownloading = pct < 100) }
        }
        showToast("文档已下载到本地")
      }
  }

  fun openSellFlow() {
    if (_uiState.value.isBetaVerified) {
      _uiState.update { it.copy(isSellSheetOpen = true) }
    } else {
      _uiState.update { it.copy(isBetaModalOpen = true) }
    }
  }

  fun dismissBetaModal() {
    _uiState.update { it.copy(isBetaModalOpen = false) }
  }

  fun verifyBetaCode(code: String) {
    if (code.trim().isEmpty()) {
      showToast("请先输入内测码")
      return
    }
    _uiState.update { it.copy(isBetaVerified = true, isBetaModalOpen = false, isSellSheetOpen = true) }
    showToast("内测码验证通过")
  }

  fun dismissSellSheet() {
    _uiState.update { it.copy(isSellSheetOpen = false) }
  }

  fun publishMarketItem(title: String, category: MarketCategory, priceInput: String, desc: String) {
    val cleanTitle = title.trim()
    val cleanDesc = desc.trim()
    if (cleanTitle.isEmpty() || cleanDesc.isEmpty()) {
      showToast("请填写完整的商品名称与简介")
      return
    }
    val numericPrice = priceInput.toDoubleOrNull()?.coerceAtLeast(1.0) ?: 6.0
    val priceStr = if (numericPrice % 1.0 == 0.0) "￥${numericPrice.toInt()}" else "￥$numericPrice"
    val isPlugin = category == MarketCategory.PLUGINS
    val formatStr = if (isPlugin) "JSON / JS" else "MD / TXT"
    val newItem =
      MarketItem(
        id = "custom_${System.currentTimeMillis()}",
        category = category,
        categoryBadge = category.label,
        formatLabel = formatStr,
        salesLabel = "刚刚上架",
        title = cleanTitle,
        summary = cleanDesc,
        detailIntro = cleanDesc,
        priceText = priceStr,
        seller = "我 · 岛遇创作者",
        fileName = "$cleanTitle.${if (isPlugin) "zip" else "md"}",
        fileMeta = "64 KB · 购买后可下载",
        ctaLabel = if (isPlugin) "购买插件" else "购买文档",
        previewContent = cleanDesc,
        galleryRes = listOf(R.drawable.img_floating_island_hero, R.drawable.img_story_jinghua),
      )
    _uiState.update { state ->
      state.copy(
        marketItems = listOf(newItem) + state.marketItems,
        isSellSheetOpen = false,
      )
    }
    showToast("已成功上架到破破甲集市")
  }

  fun toggleMarketItemFavorite(itemId: String) {
    val item = _uiState.value.marketItems.find { it.id == itemId } ?: return
    val next = !item.isFavorited
    _uiState.update { state ->
      state.copy(
        marketItems = state.marketItems.map { if (it.id == itemId) it.copy(isFavorited = next) else it }
      )
    }
    showToast(if (next) "已加入收藏" else "已取消收藏")
  }

  fun purchaseOrDownloadOnDetail(itemId: String) {
    val item = _uiState.value.marketItems.find { it.id == itemId } ?: return
    if (item.isPurchased) {
      showToast("正在下载 ${item.fileName}")
      return
    }
    if (_uiState.value.isDetailUnlocking) return
    _uiState.update { it.copy(isDetailUnlocking = true) }
    viewModelScope.launch {
      delay(700)
      _uiState.update { state ->
        state.copy(
          isDetailUnlocking = false,
          marketItems = state.marketItems.map { if (it.id == itemId) it.copy(isPurchased = true) else it },
        )
      }
      showToast("购买成功，可立即下载文件")
    }
  }

  // --- 22 中转站详情 ---
  fun openRelayDetail(stationId: String) {
    _uiState.update { it.copy(activeRelayId = stationId) }
  }

  fun toggleRelayImpression(stationId: String, tag: String) {
    _uiState.update { state ->
      val current = state.relayExtendedById[stationId] ?: return@update state
      val nextTags =
        if (current.selectedImpressions.contains(tag)) current.selectedImpressions - tag
        else current.selectedImpressions + tag
      val updatedMap = state.relayExtendedById.toMutableMap().apply {
        put(stationId, current.copy(selectedImpressions = nextTags))
      }
      state.copy(relayExtendedById = updatedMap)
    }
  }

  fun updateRelayFeedbackNote(stationId: String, note: String) {
    _uiState.update { state ->
      val current = state.relayExtendedById[stationId] ?: return@update state
      val updatedMap = state.relayExtendedById.toMutableMap().apply {
        put(stationId, current.copy(feedbackNote = note.take(80)))
      }
      state.copy(relayExtendedById = updatedMap)
    }
  }

  // --- 21 剧本局活动详情 ---
  fun loadScriptDetail(script: ScriptLobbyItem) {
    _uiState.update { state ->
      val existing = state.scriptDetail
      if (existing.scriptId == script.id) {
        state
      } else {
        state.copy(
          scriptDetail = ScriptActivityDetailState(
            scriptId = script.id,
            title = if (script.id == "sc_sleep") "《星际休眠舱：第7号觉醒者》" else "《${script.title}》",
            currentPlayers = script.currentPlayers,
            maxPlayers = script.maxPlayers,
            coverRes = if (script.id == "sc_sleep") R.drawable.img_story_nightboat else R.drawable.img_story_jinghua,
            enrolled = script.selectedRole != null,
            selectedFaction = script.selectedRole ?: "返航派",
          )
        )
      }
    }
  }

  fun selectScriptFaction(factionName: String) {
    val detail = _uiState.value.scriptDetail
    if (detail.enrolled) {
      showToast("已锁定阵营：${detail.selectedFaction}")
      return
    }
    _uiState.update { it.copy(scriptDetail = detail.copy(selectedFaction = factionName)) }
  }

  fun advanceScriptDetailAction(onEnrolledInLobby: (String, String) -> Unit) {
    val detail = _uiState.value.scriptDetail
    when {
      !detail.enrolled -> {
        val updatedFactions =
          detail.factions.map { f ->
            if (f.name == detail.selectedFaction && !f.isFull) f.copy(currentCount = f.currentCount + 1) else f
          }
        _uiState.update {
          it.copy(
            scriptDetail = detail.copy(
              enrolled = true,
              currentPlayers = (detail.currentPlayers + 1).coerceAtMost(detail.maxPlayers),
              factions = updatedFactions,
            )
          )
        }
        onEnrolledInLobby(detail.scriptId, detail.selectedFaction)
        showToast("报名成功！已解锁设定文档下载")
      }
      !detail.proofUploaded -> {
        showToast("可点击「下载设定文档」或「上传通关截图」")
      }
      !detail.rewardClaimed -> {
        _uiState.update { it.copy(scriptDetail = detail.copy(rewardClaimed = true)) }
        showToast("奖励「星海徽章 +￥200」已领取！")
      }
      else -> Unit
    }
  }

  fun uploadScriptProof() {
    val detail = _uiState.value.scriptDetail
    if (!detail.enrolled) {
      showToast("请先报名参与活动后再上传通关截图")
      return
    }
    _uiState.update { it.copy(scriptDetail = detail.copy(proofUploaded = true)) }
    showToast("通关截图上传成功，可点击下方「领取奖励」")
  }
}

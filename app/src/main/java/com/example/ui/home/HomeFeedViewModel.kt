package com.example.ui.home

import androidx.annotation.DrawableRes
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

enum class HomeChannel(val index: Int, val labelRes: Int) {
  RECOMMEND(0, R.string.channel_recommend),
  LATEST(1, R.string.channel_latest),
  FOLLOWING(2, R.string.channel_following),
  HOT(3, R.string.channel_hot),
}

enum class UpdateModalPhase {
  HIDDEN,
  PROMPT,
  DOWNLOADING,
}

data class CommentReply(
  val id: String,
  val author: String,
  val authorInitial: String,
  val isAuthorBadge: Boolean = false,
  val isVerifiedBlueV: Boolean = false,
  @DrawableRes val avatarRes: Int? = null,
  val replyToAuthor: String? = null,
  val content: String,
  val timestamp: String,
  val likeCount: Int,
  val isLiked: Boolean = false,
)

data class CommentItem(
  val id: String,
  val author: String,
  val authorInitial: String,
  val content: String,
  val timestamp: String,
  val likeCount: Int,
  val isLiked: Boolean = false,
  val replies: List<CommentReply> = emptyList(),
  val isRepliesCollapsed: Boolean = false,
)

data class FeedStory(
  val id: String,
  val channel: HomeChannel,
  @DrawableRes val coverRes: Int,
  val title: String,
  val readingMeta: String,
  val lead: String,
  val tags: List<String>,
  val authorName: String,
  val authorInitial: String,
  val isVerifiedBlueV: Boolean,
  val shortDesc: String,
  val paragraphs: List<String>,
  val characterSettings: List<String>,
  val personalityTitle: String,
  val personalityItems: List<String>,
  val bondsTitle: String,
  val bondParagraphs: List<String>,
  val attachmentFileName: String,
  val attachmentSizeLabel: String,
  val likesIdle: String,
  val likesActive: String,
  val baseCommentsCount: Int,
  val collectsIdle: String,
  val collectsActive: String,
  val sharesCount: Int,
  val isLiked: Boolean = false,
  val isCollected: Boolean = false,
  val isFollowed: Boolean = false,
) {
  val likesDisplay: String
    get() = if (isLiked) likesActive else likesIdle

  val collectsDisplay: String
    get() = if (isCollected) collectsActive else collectsIdle

  val totalSearchableWordCount: Int
    get() {
      val allText =
        buildList {
          add(lead)
          addAll(paragraphs)
          addAll(characterSettings)
          addAll(personalityItems)
          addAll(bondParagraphs)
        }.joinToString("")
      return allText.replace(Regex("\\s+"), "").length
    }
}

data class MasonryCard(
  val id: String,
  val storyId: String,
  @DrawableRes val coverRes: Int,
  val aspectRatio: Float,
  val tag: String,
  val meta: String,
  val title: String,
  val author: String,
  val authorInitial: String,
  @DrawableRes val avatarRes: Int? = null,
  val likesIdle: String,
  val likesActive: String,
  val isLiked: Boolean = false,
) {
  val likesDisplay: String
    get() = if (isLiked) likesActive else likesIdle
}

data class ReplyTarget(
  val commentId: String,
  val authorName: String,
)

data class HomeFeedUiState(
  val activeChannel: HomeChannel = HomeChannel.RECOMMEND,
  val isGridMode: Boolean = false,
  val isAutoScrolling: Boolean = true,
  val isDescExpanded: Boolean = false,
  val stories: Map<String, FeedStory> = DefaultStories,
  val channelFeaturedStoryId: Map<HomeChannel, String> = DefaultChannelFeaturedIds,
  val detailStoryId: String = "jinghua",
  val masonryCardsByChannel: Map<HomeChannel, List<MasonryCard>> = DefaultMasonryByChannel,
  val comments: List<CommentItem> = DefaultComments,
  val isCommentDrawerOpen: Boolean = false,
  val isShareSheetOpen: Boolean = false,
  val replyTarget: ReplyTarget? = null,
  val isOfflineCached: Boolean = false,
  val isArticleSearchVisible: Boolean = false,
  val articleSearchQuery: String = "",
  val activeSearchMatchIndex: Int = 0,
  val isAttachmentPreviewOpen: Boolean = false,
  val updatePhase: UpdateModalPhase = UpdateModalPhase.HIDDEN,
  val updateProgressPct: Int = 0,
  val isDownloadStarted: Boolean = false,
  val showFloatingUpdatePill: Boolean = false,
  val hasShownAutoUpdatePrompt: Boolean = false,
  val toastMessage: String? = null,
) {
  val currentImmersiveStory: FeedStory
    get() {
      val id = channelFeaturedStoryId[activeChannel] ?: "jinghua"
      return stories[id] ?: stories.values.first()
    }

  val detailStory: FeedStory
    get() = stories[detailStoryId] ?: currentImmersiveStory

  val currentMasonryCards: List<MasonryCard>
    get() = masonryCardsByChannel[activeChannel].orEmpty()

  val totalCommentsCount: Int
    get() {
      val initialSubCount = 2
      val initialTopCount = 3
      val currentSubCount = comments.sumOf { it.replies.size }
      val delta = (comments.size - initialTopCount) + (currentSubCount - initialSubCount)
      return detailStory.baseCommentsCount + delta
    }

  val homeCommentsCount: Int
    get() {
      val initialSubCount = 2
      val initialTopCount = 3
      val currentSubCount = comments.sumOf { it.replies.size }
      val delta = (comments.size - initialTopCount) + (currentSubCount - initialSubCount)
      return currentImmersiveStory.baseCommentsCount + delta
    }

  val isUpdateReadyToInstall: Boolean
    get() = isDownloadStarted && updateProgressPct >= 100
}

class HomeFeedViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(HomeFeedUiState())
  val uiState: StateFlow<HomeFeedUiState> = _uiState.asStateFlow()

  private var toastJob: Job? = null
  private var downloadJob: Job? = null

  fun selectChannel(channel: HomeChannel) {
    _uiState.update { state ->
      state.copy(
        activeChannel = channel,
        isDescExpanded = false,
        isAutoScrolling = true,
      )
    }
  }

  fun toggleGridMode() {
    val nextGrid = !_uiState.value.isGridMode
    _uiState.update { state ->
      state.copy(
        isGridMode = nextGrid,
        isAutoScrolling = if (nextGrid) false else true,
      )
    }
    showToast(if (nextGrid) "已切换为双列瀑布流" else "已切换为沉浸长文流")
  }

  fun toggleAutoScroll() {
    _uiState.update { it.copy(isAutoScrolling = !it.isAutoScrolling) }
  }

  fun setAutoScrollPlaying(playing: Boolean) {
    _uiState.update { it.copy(isAutoScrolling = playing) }
  }

  fun toggleDescExpanded() {
    _uiState.update { it.copy(isDescExpanded = !it.isDescExpanded) }
  }

  fun toggleFollowStory(storyId: String) {
    val current = _uiState.value.stories[storyId] ?: return
    val nextFollowed = !current.isFollowed
    _uiState.update { state ->
      val updatedMap =
        state.stories.toMutableMap().apply {
          this[storyId] = current.copy(isFollowed = nextFollowed)
        }
      state.copy(stories = updatedMap)
    }
    showToast(if (nextFollowed) "已关注 @${current.authorName}" else "已取消关注")
  }

  fun toggleLikeStory(storyId: String) {
    val current = _uiState.value.stories[storyId] ?: return
    _uiState.update { state ->
      val updatedMap =
        state.stories.toMutableMap().apply {
          this[storyId] = current.copy(isLiked = !current.isLiked)
        }
      state.copy(stories = updatedMap)
    }
  }

  fun toggleCollectStory(storyId: String) {
    val current = _uiState.value.stories[storyId] ?: return
    _uiState.update { state ->
      val updatedMap =
        state.stories.toMutableMap().apply {
          this[storyId] = current.copy(isCollected = !current.isCollected)
        }
      state.copy(stories = updatedMap)
    }
  }

  fun toggleGridCardLike(cardId: String) {
    _uiState.update { state ->
      val channel = state.activeChannel
      val cards = state.masonryCardsByChannel[channel].orEmpty()
      val updatedCards =
        cards.map { card ->
          if (card.id == cardId) card.copy(isLiked = !card.isLiked) else card
        }
      val nextMap =
        state.masonryCardsByChannel.toMutableMap().apply { this[channel] = updatedCards }
      state.copy(masonryCardsByChannel = nextMap)
    }
  }

  fun openStoryDetail(storyId: String) {
    _uiState.update {
      it.copy(
        detailStoryId = if (it.stories.containsKey(storyId)) storyId else "jinghua",
        isAutoScrolling = false,
        isArticleSearchVisible = false,
        articleSearchQuery = "",
        activeSearchMatchIndex = 0,
      )
    }
  }

  fun openCommentDrawer() {
    _uiState.update { it.copy(isCommentDrawerOpen = true, isAutoScrolling = false) }
  }

  fun closeCommentDrawer() {
    _uiState.update { it.copy(isCommentDrawerOpen = false, replyTarget = null) }
  }

  fun openShareSheet() {
    _uiState.update { it.copy(isShareSheetOpen = true, isAutoScrolling = false) }
  }

  fun closeShareSheet() {
    _uiState.update { it.copy(isShareSheetOpen = false) }
  }

  fun setReplyTarget(commentId: String, authorName: String) {
    _uiState.update { it.copy(replyTarget = ReplyTarget(commentId, authorName)) }
  }

  fun clearReplyTarget() {
    _uiState.update { it.copy(replyTarget = null) }
  }

  fun submitComment(rawText: String, showSyncToast: Boolean = false) {
    val trimmed = rawText.trim()
    if (trimmed.isEmpty()) return
    _uiState.update { state ->
      val target = state.replyTarget
      val updatedComments =
        if (target != null) {
          state.comments.map { item ->
            if (item.id == target.commentId) {
              val newReply =
                CommentReply(
                  id = "reply_${System.nanoTime()}",
                  author = "我",
                  authorInitial = "我",
                  replyToAuthor = target.authorName,
                  content = trimmed,
                  timestamp = "刚刚",
                  likeCount = 0,
                )
              item.copy(replies = item.replies + newReply, isRepliesCollapsed = false)
            } else {
              item
            }
          }
        } else {
          val newComment =
            CommentItem(
              id = "comment_${System.nanoTime()}",
              author = "我",
              authorInitial = "我",
              content = trimmed,
              timestamp = "刚刚",
              likeCount = 0,
            )
          listOf(newComment) + state.comments
        }
      state.copy(comments = updatedComments, replyTarget = null)
    }
    if (showSyncToast) {
      showToast("评论已发布并同步至首页")
    }
  }

  fun toggleCommentLike(commentId: String, replyId: String? = null) {
    _uiState.update { state ->
      val updated =
        state.comments.map { item ->
          if (item.id != commentId) {
            item
          } else if (replyId == null) {
            val nextLiked = !item.isLiked
            item.copy(
              isLiked = nextLiked,
              likeCount = (item.likeCount + if (nextLiked) 1 else -1).coerceAtLeast(0),
            )
          } else {
            item.copy(
              replies =
                item.replies.map { r ->
                  if (r.id != replyId) {
                    r
                  } else {
                    val nextLiked = !r.isLiked
                    r.copy(
                      isLiked = nextLiked,
                      likeCount = (r.likeCount + if (nextLiked) 1 else -1).coerceAtLeast(0),
                    )
                  }
                }
            )
          }
        }
      state.copy(comments = updated)
    }
  }

  fun toggleSubRepliesCollapsed(commentId: String) {
    _uiState.update { state ->
      state.copy(
        comments =
          state.comments.map { item ->
            if (item.id == commentId) {
              item.copy(isRepliesCollapsed = !item.isRepliesCollapsed)
            } else {
              item
            }
          }
      )
    }
  }

  fun toggleOfflineCache() {
    val next = !_uiState.value.isOfflineCached
    _uiState.update { it.copy(isOfflineCached = next) }
    showToast(if (next) "已离线缓存至「我的岛屿库」" else "已取消离线缓存")
  }

  fun toggleArticleSearch() {
    val next = !_uiState.value.isArticleSearchVisible
    _uiState.update {
      it.copy(
        isArticleSearchVisible = next,
        articleSearchQuery = if (!next) "" else it.articleSearchQuery,
        activeSearchMatchIndex = 0,
      )
    }
  }

  fun updateArticleSearchQuery(query: String) {
    _uiState.update { it.copy(articleSearchQuery = query, activeSearchMatchIndex = 0) }
  }

  fun stepArticleSearchMatch(delta: Int, totalMatches: Int) {
    if (totalMatches <= 0) return
    _uiState.update { state ->
      val nextIdx = (state.activeSearchMatchIndex + delta + totalMatches) % totalMatches
      state.copy(activeSearchMatchIndex = nextIdx)
    }
  }

  fun openAttachmentPreview() {
    _uiState.update { it.copy(isAttachmentPreviewOpen = true) }
  }

  fun closeAttachmentPreview() {
    _uiState.update { it.copy(isAttachmentPreviewOpen = false) }
  }

  fun maybeShowInitialUpdatePrompt() {
    val state = _uiState.value
    if (!state.hasShownAutoUpdatePrompt) {
      _uiState.update {
        it.copy(
          hasShownAutoUpdatePrompt = true,
          updatePhase = UpdateModalPhase.PROMPT,
        )
      }
    }
  }

  fun openUpdateModal() {
    val state = _uiState.value
    val targetPhase =
      if (state.isDownloadStarted) UpdateModalPhase.DOWNLOADING else UpdateModalPhase.PROMPT
    _uiState.update {
      it.copy(
        updatePhase = targetPhase,
        showFloatingUpdatePill = false,
      )
    }
  }

  fun startSimulatedDownload() {
    downloadJob?.cancel()
    _uiState.update {
      it.copy(
        updatePhase = UpdateModalPhase.DOWNLOADING,
        isDownloadStarted = true,
        updateProgressPct = 1,
        showFloatingUpdatePill = false,
      )
    }
    downloadJob =
      viewModelScope.launch {
        var pct = 1
        while (pct < 100) {
          delay(110L)
          pct = (pct + 4).coerceAtMost(100)
          _uiState.update { it.copy(updateProgressPct = pct) }
          if (pct >= 100) {
            showToast("下载完成，可点击立即安装")
          }
        }
      }
  }

  fun replaySimulatedDownloadFromStart() {
    startSimulatedDownload()
  }

  fun minimizeUpdateToBackground() {
    val state = _uiState.value
    if (state.isDownloadStarted) {
      _uiState.update {
        it.copy(
          updatePhase = UpdateModalPhase.HIDDEN,
          showFloatingUpdatePill = true,
        )
      }
      showToast("已切换至后台继续下载")
    } else {
      dismissUpdatePrompt()
    }
  }

  fun dismissUpdatePrompt() {
    _uiState.update {
      it.copy(
        updatePhase = UpdateModalPhase.HIDDEN,
        showFloatingUpdatePill = false,
      )
    }
    showToast("已暂缓更新，可在「我」页随时检查新版本")
  }

  fun triggerInstallSimulation() {
    _uiState.update {
      it.copy(
        updatePhase = UpdateModalPhase.HIDDEN,
        showFloatingUpdatePill = false,
      )
    }
    showToast("正在拉起系统安装界面...")
  }

  fun showToast(message: String) {
    toastJob?.cancel()
    _uiState.update { it.copy(toastMessage = message) }
    toastJob =
      viewModelScope.launch {
        delay(2000L)
        _uiState.update { it.copy(toastMessage = null) }
      }
  }
}

private val DefaultChannelFeaturedIds =
  mapOf(
    HomeChannel.RECOMMEND to "jinghua",
    HomeChannel.LATEST to "chunxin",
    HomeChannel.FOLLOWING to "island_letter",
    HomeChannel.HOT to "changjie",
  )

private val DefaultStories =
  mapOf(
    "jinghua" to
      FeedStory(
        id = "jinghua",
        channel = HomeChannel.RECOMMEND,
        coverRes = R.drawable.img_story_jinghua,
        title = "《京华深处》",
        readingMeta = "阅读约 3 分钟 · 11-07",
        lead = "高门深院，暗流涌动。一个见惯风云的人，却在她面前第一次乱了分寸。",
        tags = listOf("#现代都市", "#高干", "#年上", "#家族权谋", "#京华深处"),
        authorName = "零度叙事",
        authorInitial = "零",
        isVerifiedBlueV = true,
        shortDesc = "他见惯了风云，却在她面前乱了分寸。一场身份悬殊的相遇，牵出一段深藏多年的家族秘事与雨夜重逢。",
        paragraphs =
          listOf(
            "那晚京城落了入秋以来的第一场细雨，长廊尽头的灯笼被风吹得微微晃动。他站在台阶上，黑色大衣肩头沾着湿意，目光穿过人群，准确地落在了她的身上。",
            "旁人都说沈家掌权人冷静克制、从不涉入无谓纠葛，可只有她知道，那句压低了声音的“过来”，背后藏着多少不动声色的试探与偏爱。",
            "一场身份悬殊的相遇，悄然撕开了京华名利场平静的表象。当尘封多年的旧案重新浮出水面，他选择站在她身前，替她挡下所有的明枪暗箭。",
            "“别怕，”他低头替她拢紧披肩，“只要我在，京华的风雨就落不到你身上。”",
          ),
        characterSettings =
          listOf(
            "角色：沈砚辞",
            "身份：沈氏集团掌权人 · 京圈最年轻的家主",
            "年龄：29岁 · 天蝎座",
            "外貌：眉眼深邃冷峻，鼻梁高挺，常年一身剪裁利落的黑色高定西装或长款羊绒大衣。骨节分明的手指习惯性摩挲腕间一枚旧怀表，眼神平静时透着疏离与压迫感，唯有看向她时才会悄然柔和。",
          ),
        personalityTitle = "性格底色",
        personalityItems =
          listOf(
            "1. 遇事松弛冷静，从不被旁人情绪裹挟，习惯掌控全局。",
            "2. 洞察力极强，看破不说破，却对她的微表情格外敏锐。",
            "3. 极度护短，对他人边界分明，唯独在她这里一再退让。",
            "4. 外表克制疏离，内里占有欲深沉，认定了便不会放手。",
          ),
        bondsTitle = "关系羁绊",
        bondParagraphs =
          listOf(
            "旁人都说沈家掌权人冷静克制、从不涉入无谓纠葛，可只有她知道，那句压低了声音的“过来”，背后藏着多少不动声色的试探与偏爱。",
            "当尘封多年的旧案重新浮出水面，他选择站在她身前，替她挡下所有的明枪暗箭：“别怕，只要我在，京华的风雨就落不到你身上。”",
          ),
        attachmentFileName = "《京华深处》角色设定集.pdf",
        attachmentSizeLabel = "1.8 MB · PDF 文档",
        likesIdle = "3.2万",
        likesActive = "3.3万",
        baseCommentsCount = 586,
        collectsIdle = "1.1万",
        collectsActive = "1.2万",
        sharesCount = 110,
      ),
    "chunxin" to
      FeedStory(
        id = "chunxin",
        channel = HomeChannel.LATEST,
        coverRes = R.drawable.img_update_banner,
        title = "《春信至》",
        readingMeta = "阅读约 2 分钟 · 刚刚更新",
        lead = "除夕夜的浮空岛烟火升起时，去年没能说完的那句话终于有了回音。",
        tags = listOf("#新春特辑", "#重逢", "#治愈向", "#双向奔赴"),
        authorName = "岛遇精选",
        authorInitial = "岛",
        isVerifiedBlueV = true,
        shortDesc = "新春版本精选特别篇：跨越冬雪与潮汐的约定，在烟火落尽前重新递出那封未拆的春信。",
        paragraphs =
          listOf(
            "零点钟声敲响前三分钟，浮空岛南岸的灯笼逐一亮起。他踩着积雪走过木桥，指尖还留着刚写完的祈愿签温度。",
            "“今年不许再不告而别了。”她把围巾绕到他颈间，抬头时恰好有一簇金色烟火在夜空散开，映亮了两人眼底的笑意。",
          ),
        characterSettings =
          listOf(
            "角色：顾屿白",
            "身份：浮空岛气象台守望员",
            "年龄：26岁 · 双鱼座",
            "外貌：浅灰色长风衣外搭一条深蓝羊绒围巾，眼尾微挑，笑起来时左颊有一颗淡褐色小痣。",
          ),
        personalityTitle = "性格底色",
        personalityItems =
          listOf(
            "1. 温柔笃定，习惯把重要的事写进信笺里。",
            "2. 对风雪与天气有着近乎浪漫的执着，却更记得她曾说怕冷。",
          ),
        bondsTitle = "关系羁绊",
        bondParagraphs =
          listOf(
            "跨越整个冬天的失联后，他在除夕零点前赶回浮空岛南岸，把一年未寄出的十二封信一并交到她手里。",
          ),
        attachmentFileName = "《春信至》新春设定卡.pdf",
        attachmentSizeLabel = "1.2 MB · PDF 文档",
        likesIdle = "1.5万",
        likesActive = "1.6万",
        baseCommentsCount = 312,
        collectsIdle = "6480",
        collectsActive = "6481",
        sharesCount = 86,
      ),
    "island_letter" to
      FeedStory(
        id = "island_letter",
        channel = HomeChannel.FOLLOWING,
        coverRes = R.drawable.img_floating_island_hero,
        title = "《岛屿来信》",
        readingMeta = "阅读约 4 分钟 · 1小时前",
        lead = "在潮汐退去之前，替你留住那段未寄出的独白与遗落在灯塔边的旧怀表。",
        tags = listOf("#治愈向", "#海岛", "#慢热", "#书信体"),
        authorName = "林听风",
        authorInitial = "林",
        isVerifiedBlueV = false,
        shortDesc = "你关注的作者刚刚更新了第十二章：潮汐退去后的礁石滩上，终于等来了那班延迟许久的渡轮。",
        paragraphs =
          listOf(
            "海风吹过灯塔二层的木窗，桌上摊开的信纸已经写到了第三页。他习惯在每次退潮后把信折好，放进那只褪了色的铁皮盒里。",
            "直到渡轮鸣笛的声音穿透薄雾，他才合上钢笔，推门走向码头——这一次，收信人就站在栈桥尽头。",
          ),
        characterSettings =
          listOf(
            "角色：陆闻舟",
            "身份：南湾灯塔维修师",
            "年龄：28岁 · 巨蟹座",
            "外貌：晒成浅麦色的肤色与清朗眉目，总穿着洗得发白的牛仔工装外套。",
          ),
        personalityTitle = "性格底色",
        personalityItems =
          listOf(
            "1. 话少心细，把对她的思念写满整整一铁皮盒。",
            "2. 无论雾有多浓，每天黄昏都会准时点亮南湾的灯。",
          ),
        bondsTitle = "关系羁绊",
        bondParagraphs =
          listOf(
            "延迟两年的渡轮靠岸那一刻，铁皮盒里的三十七封未寄信件，终于等来了它的收信人。",
          ),
        attachmentFileName = "《岛屿来信》灯塔手札.pdf",
        attachmentSizeLabel = "2.1 MB · PDF 文档",
        likesIdle = "1.8万",
        likesActive = "1.9万",
        baseCommentsCount = 429,
        collectsIdle = "8920",
        collectsActive = "8921",
        sharesCount = 94,
        isFollowed = true,
      ),
    "changjie" to
      FeedStory(
        id = "changjie",
        channel = HomeChannel.HOT,
        coverRes = R.drawable.img_story_nightboat,
        title = "《长街灯火》",
        readingMeta = "阅读约 3 分钟 · 2小时前",
        lead = "旧案重启那夜，他撑着黑伞站在长廊尽头，替她拦下了所有窥探的目光。",
        tags = listOf("#家族权谋", "#强强联合", "#悬疑", "#热榜Top2"),
        authorName = "江晚意",
        authorInitial = "江",
        isVerifiedBlueV = true,
        shortDesc = "全站热榜飙升作品：长街尽头的一场夜谈，彻底改写了南北两家持续十年的暗中博弈。",
        paragraphs =
          listOf(
            "雨滴顺着黑伞骨滑落，长街两侧的路灯在积水里拉出长长的倒影。她把手里的卷宗递过去，指尖冰凉却神色笃定。",
            "“这局棋你让我执黑，就不怕我掀了整张棋盘？”他低笑一声，接过卷宗，“我站在这里，就是来替你兜底的。”",
          ),
        characterSettings =
          listOf(
            "角色：裴斯年",
            "身份：南城律所首席合伙人 · 幕后棋手",
            "年龄：31岁 · 摩羯座",
            "外貌：金丝细框眼镜下藏着一双洞察世故的眼，指尖常执一把黑檀木折伞。",
          ),
        personalityTitle = "性格底色",
        personalityItems =
          listOf(
            "1. 滴水不漏的辩护者，面对她的试探却甘愿主动交出底牌。",
            "2. 看似置身事外，实则每一步布局都算到了她退后的余地。",
          ),
        bondsTitle = "关系羁绊",
        bondParagraphs =
          listOf(
            "从各自为战的对手到生死相托的盟友，长街尽头的雨水见证了他们唯一一次并肩撑伞。",
          ),
        attachmentFileName = "《长街灯火》卷宗密档.pdf",
        attachmentSizeLabel = "1.5 MB · PDF 文档",
        likesIdle = "2.4万",
        likesActive = "2.5万",
        baseCommentsCount = 718,
        collectsIdle = "1.3万",
        collectsActive = "1.4万",
        sharesCount = 142,
      ),
  )

private val DefaultMasonryByChannel =
  mapOf(
    HomeChannel.RECOMMEND to
      listOf(
        MasonryCard("rec_1", "jinghua", R.drawable.img_story_jinghua, 0.75f, "#现代都市", "2.4万字", "《京华深处》：那晚京城落了入秋以来的第一场细雨", "零度叙事", "零", R.drawable.img_story_jinghua, "3.2万", "3.3万"),
        MasonryCard("rec_2", "island_letter", R.drawable.img_floating_island_hero, 1.33f, "#治愈向", "可二创", "《岛屿来信》：在潮汐退去之前，替你留住未寄出的独白", "林听风", "林", null, "1.8万", "1.9万"),
        MasonryCard("rec_3", "changjie", R.drawable.img_story_nightboat, 1.0f, "#悬疑微恐", "4人剧本局", "《夜航船》：雾起时不要回头，船头灯只照见有缘人", "苏念安", "苏", null, "9420", "9421"),
        MasonryCard("rec_4", "changjie", R.drawable.img_story_nightboat, 0.75f, "#家族权谋", "含附件", "《长街灯火》：旧案重启那夜，他撑着黑伞站在长廊尽头", "江晚意", "江", null, "2.4万", "2.5万"),
        MasonryCard("rec_5", "jinghua", R.drawable.img_floating_island_hero, 1.33f, "#年上", "1.6万字", "《雪落无声》：身份悬殊的七年试探，终于在除夕夜破冰", "鱼汤", "鱼", null, "6830", "6831"),
        MasonryCard("rec_6", "chunxin", R.drawable.img_update_banner, 1.0f, "#新春特辑", "官方精选", "《春信至》：新春版本精选角色与烟火重逢特别篇", "岛遇精选", "岛", R.drawable.img_update_banner, "1.5万", "1.6万"),
      ),
    HomeChannel.LATEST to
      listOf(
        MasonryCard("lat_1", "chunxin", R.drawable.img_update_banner, 0.75f, "刚刚发布", "官方精选", "《春信至》：新春版本精选角色与烟火重逢特别篇", "岛遇精选", "岛", R.drawable.img_update_banner, "328", "329"),
        MasonryCard("lat_2", "jinghua", R.drawable.img_floating_island_hero, 1.33f, "12分钟前", "免费试读", "《雪落无声》：身份悬殊的七年试探，终于在除夕夜破冰", "鱼汤", "鱼", null, "512", "513"),
        MasonryCard("lat_3", "island_letter", R.drawable.img_floating_island_hero, 1.0f, "38分钟前", "8600字", "《白塔之下》：潮汐退去后，遗落在灯塔边的旧怀表", "林听风", "林", null, "846", "847"),
        MasonryCard("lat_4", "changjie", R.drawable.img_story_nightboat, 0.75f, "1小时前", "4人剧本局", "《夜航船》：雾起时不要回头，船头灯只照见有缘人", "苏念安", "苏", null, "1290", "1291"),
        MasonryCard("lat_5", "jinghua", R.drawable.img_story_jinghua, 1.33f, "2小时前", "含附件", "《京华番外·长廊撑伞》：雨夜重逢后的第三封手写信", "零度叙事", "零", R.drawable.img_story_jinghua, "2140", "2141"),
        MasonryCard("lat_6", "changjie", R.drawable.img_story_nightboat, 1.0f, "3小时前", "可二创", "《旧案卷宗》：十年前的雪夜证词与南北两家暗线", "江晚意", "江", null, "1760", "1761"),
      ),
    HomeChannel.FOLLOWING to
      listOf(
        MasonryCard("fol_1", "jinghua", R.drawable.img_story_jinghua, 0.75f, "已关注 · 更新", "2.4万字", "《京华深处》：那晚京城落了入秋以来的第一场细雨", "零度叙事", "零", R.drawable.img_story_jinghua, "3.2万", "3.3万"),
        MasonryCard("fol_2", "changjie", R.drawable.img_story_nightboat, 1.0f, "已关注", "含附件", "《长廊撑伞》：京华番外·雨夜重逢的另一段视角", "零度叙事", "零", R.drawable.img_story_jinghua, "1.1万", "1.2万"),
        MasonryCard("fol_3", "island_letter", R.drawable.img_floating_island_hero, 1.33f, "已关注", "可二创", "《岛屿来信》：在潮汐退去之前，替你留住未寄出的独白", "林听风", "林", null, "1.8万", "1.9万"),
        MasonryCard("fol_4", "chunxin", R.drawable.img_update_banner, 0.75f, "官方关注", "新春特辑", "《春信至》：浮空岛南岸的零点烟火与双向约定", "岛遇精选", "岛", R.drawable.img_update_banner, "1.5万", "1.6万"),
      ),
    HomeChannel.HOT to
      listOf(
        MasonryCard("hot_1", "jinghua", R.drawable.img_story_jinghua, 0.75f, "热榜 #1", "3.2万赞", "《京华深处》：高门深院，暗流涌动下的克制试探", "零度叙事", "零", R.drawable.img_story_jinghua, "3.2万", "3.3万"),
        MasonryCard("hot_2", "changjie", R.drawable.img_story_nightboat, 1.0f, "热榜 #2", "含附件", "《长街灯火》：旧案重启那夜，他撑着黑伞站在长廊尽头", "江晚意", "江", null, "2.4万", "2.5万"),
        MasonryCard("hot_3", "island_letter", R.drawable.img_floating_island_hero, 1.33f, "热榜 #3", "书信体", "《岛屿来信》：替你留住未寄出的独白", "林听风", "林", null, "1.8万", "1.9万"),
        MasonryCard("hot_4", "chunxin", R.drawable.img_update_banner, 0.75f, "热榜 #4", "官方精选", "《春信至》：新春版本精选角色与烟火重逢特别篇", "岛遇精选", "岛", R.drawable.img_update_banner, "1.5万", "1.6万"),
      ),
  )

private val DefaultComments =
  listOf(
    CommentItem(
      id = "c1",
      author = "江晚意",
      authorInitial = "江",
      content = "“只要我在，京华的风雨就落不到你身上”这句真的太戳了！沈砚辞这个年上克制感拿捏得死死的。",
      timestamp = "11-07",
      likeCount = 128,
      replies =
        listOf(
          CommentReply(
            id = "c1_r1",
            author = "零度叙事",
            authorInitial = "零",
            isAuthorBadge = true,
            isVerifiedBlueV = true,
            avatarRes = R.drawable.img_story_jinghua,
            content = "谢谢喜欢！下一章会写到长廊撑伞那段重逢戏～",
            timestamp = "14小时前",
            likeCount = 36,
          ),
          CommentReply(
            id = "c1_r2",
            author = "鱼汤",
            authorInitial = "鱼",
            replyToAuthor = "零度叙事",
            content = "太期待撑伞名场面了！请问沈总什么时候才会主动低头呀？",
            timestamp = "1小时前",
            likeCount = 9,
          ),
        ),
    ),
    CommentItem(
      id = "c2",
      author = "苏念安",
      authorInitial = "苏",
      content = "已经收藏准备今晚去游玩模式里和沈总对戏了，这个人物设定写得好细腻。",
      timestamp = "昨天 21:14",
      likeCount = 64,
    ),
    CommentItem(
      id = "c3",
      author = "林听风",
      authorInitial = "林",
      content = "摩挲旧怀表的小动作好有画面感，蹲一个后续家族旧案反转！",
      timestamp = "2小时前",
      likeCount = 19,
    ),
  )

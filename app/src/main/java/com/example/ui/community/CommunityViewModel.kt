package com.example.ui.community

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

data class CommunityUiState(
  val posts: List<CommunityPost> = CommunitySeedData.feedPosts,
  val showcasePosts: List<CommunityPost> = CommunitySeedData.accessoryShowcasePosts,
  val selectedTopic: String = "推荐",
  val searchQuery: String = "",
  val isSkeletonLoading: Boolean = true,
  val skeletonCycle: Int = 1,
  val activeDetailPostId: String = CommunitySeedData.feedPosts.first().id,
  val commentDrawerPostId: String? = null,
  val commentSortHot: Boolean = true,
  val replyTarget: CommunityReplyTarget? = null,
  val commentsByPostId: Map<String, List<CommunityComment>> =
    mapOf(CommunitySeedData.feedPosts.first().id to CommunitySeedData.defaultComments),
  val shareSheetPostId: String? = null,
  val playingMusicPostId: String? = null,
  val previewDocument: CommunityDocument? = null,
  val toastMessage: String? = null,
) {
  val filteredPosts: List<CommunityPost>
    get() {
      val q = searchQuery.trim().lowercase()
      return posts.filter { post ->
        val topicOk = selectedTopic == "推荐" || post.topic == selectedTopic || post.inlineTagPill.contains(selectedTopic)
        val queryOk =
          q.isEmpty() ||
            post.authorName.lowercase().contains(q) ||
            post.summaryBody.lowercase().contains(q) ||
            post.inlineTagPill.lowercase().contains(q) ||
            post.searchKeywords.lowercase().contains(q)
        topicOk && queryOk
      }
    }

  val activeDetailPost: CommunityPost
    get() =
      posts.find { it.id == activeDetailPostId }
        ?: showcasePosts.find { it.id == activeDetailPostId }
        ?: posts.first()

  fun commentsFor(postId: String): List<CommunityComment> {
    val list = commentsByPostId[postId] ?: CommunitySeedData.defaultComments
    return if (commentSortHot) list else list.reversed()
  }
}

class CommunityViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(CommunityUiState())
  val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

  private var toastJob: Job? = null
  private var skeletonJob: Job? = null
  private var musicJob: Job? = null

  init {
    replaySkeletonLoad()
  }

  fun replaySkeletonLoad() {
    skeletonJob?.cancel()
    _uiState.update { it.copy(isSkeletonLoading = true, skeletonCycle = it.skeletonCycle + 1) }
    skeletonJob =
      viewModelScope.launch {
        delay(1550L)
        _uiState.update { it.copy(isSkeletonLoading = false) }
      }
  }

  fun selectTopic(topic: String) {
    _uiState.update { it.copy(selectedTopic = topic) }
  }

  fun updateSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun openPostDetail(postId: String) {
    _uiState.update { it.copy(activeDetailPostId = postId, replyTarget = null) }
  }

  fun toggleFollow(postId: String) {
    var nextFollowed = false
    updatePostById(postId) { post ->
      nextFollowed = !post.isFollowed
      post.copy(isFollowed = nextFollowed)
    }
    showToast(if (nextFollowed) "已关注该作者" else "已取消关注")
  }

  fun toggleLike(postId: String) {
    updatePostById(postId) { post ->
      val next = !post.isLiked
      post.copy(
        isLiked = next,
        likeCount = if (next) post.likeCount + 1 else (post.likeCount - 1).coerceAtLeast(0),
      )
    }
  }

  fun toggleBookmark(postId: String) {
    var saved = false
    updatePostById(postId) { post ->
      saved = !post.isBookmarked
      post.copy(
        isBookmarked = saved,
        bookmarkCount = if (saved) post.bookmarkCount + 1 else (post.bookmarkCount - 1).coerceAtLeast(0),
      )
    }
    showToast(if (saved) "已收藏帖子" else "已取消收藏")
  }

  fun openShareSheet(postId: String) {
    _uiState.update { it.copy(shareSheetPostId = postId) }
  }

  fun closeShareSheet() {
    _uiState.update { it.copy(shareSheetPostId = null) }
  }

  fun recordShare(postId: String, toastText: String) {
    updatePostById(postId) { it.copy(repostCount = it.repostCount + 1) }
    _uiState.update { it.copy(shareSheetPostId = null) }
    showToast(toastText)
  }

  fun selectPollOption(postId: String, optionId: String) {
    updatePostById(postId) { post ->
      val poll = post.poll ?: return@updatePostById post
      if (poll.hasVoted) return@updatePostById post
      val current = poll.pendingSelectedIds
      val next =
        if (poll.maxSelectCount <= 1) {
          setOf(optionId)
        } else if (optionId in current) {
          current - optionId
        } else if (current.size < poll.maxSelectCount) {
          current + optionId
        } else {
          showToast("最多选择 ${poll.maxSelectCount} 项")
          current
        }
      post.copy(poll = poll.copy(pendingSelectedIds = next))
    }
  }

  fun confirmOrResetPoll(postId: String) {
    var message: String? = null
    updatePostById(postId) { post ->
      val poll = post.poll ?: return@updatePostById post
      if (poll.hasVoted) {
        val decremented =
          poll.options.map { opt ->
            if (opt.id in poll.confirmedSelectedIds) opt.copy(votes = (opt.votes - 1).coerceAtLeast(0))
            else opt
          }
        post.copy(
          poll =
            poll.copy(
              options = decremented,
              pendingSelectedIds = emptySet(),
              confirmedSelectedIds = emptySet(),
              hasVoted = false,
            )
        )
      } else {
        if (poll.pendingSelectedIds.isEmpty()) {
          message = "请先选择一个投票选项"
          return@updatePostById post
        }
        val incremented =
          poll.options.map { opt ->
            if (opt.id in poll.pendingSelectedIds) opt.copy(votes = opt.votes + 1) else opt
          }
        message = "投票成功"
        post.copy(
          poll =
            poll.copy(
              options = incremented,
              confirmedSelectedIds = poll.pendingSelectedIds,
              hasVoted = true,
            )
        )
      }
    }
    message?.let(::showToast)
  }

  fun applyBounty(postId: String) {
    var applied = false
    updatePostById(postId) { post ->
      val bounty = post.bountyCard ?: return@updatePostById post
      if (bounty.hasApplied) return@updatePostById post
      applied = true
      post.copy(bountyCard = bounty.copy(hasApplied = true, applicantCount = bounty.applicantCount + 1))
    }
    showToast(if (applied) "已提交接单应征意向" else "已提交过应征意向")
  }

  fun toggleMusicPlay(postId: String) {
    val currentPlaying = _uiState.value.playingMusicPostId
    if (currentPlaying == postId) {
      musicJob?.cancel()
      _uiState.update { it.copy(playingMusicPostId = null) }
    } else {
      musicJob?.cancel()
      _uiState.update { it.copy(playingMusicPostId = postId) }
      musicJob =
        viewModelScope.launch {
          while (true) {
            delay(1000L)
            updatePostById(postId) { post ->
              val track = post.musicTrack ?: return@updatePostById post
              val nextSec = if (track.elapsedSec >= track.durationSec) 0 else track.elapsedSec + 1
              post.copy(musicTrack = track.copy(elapsedSec = nextSec))
            }
          }
        }
    }
  }

  fun seekMusic(postId: String, targetSec: Int) {
    updatePostById(postId) { post ->
      val track = post.musicTrack ?: return@updatePostById post
      post.copy(musicTrack = track.copy(elapsedSec = targetSec.coerceIn(0, track.durationSec)))
    }
  }

  fun openDocumentPreview(doc: CommunityDocument) {
    _uiState.update { it.copy(previewDocument = doc) }
  }

  fun closeDocumentPreview() {
    _uiState.update { it.copy(previewDocument = null) }
  }

  fun openCommentDrawer(postId: String) {
    _uiState.update { it.copy(commentDrawerPostId = postId, replyTarget = null) }
  }

  fun closeCommentDrawer() {
    _uiState.update { it.copy(commentDrawerPostId = null, replyTarget = null) }
  }

  fun toggleCommentSort() {
    val nextHot = !_uiState.value.commentSortHot
    _uiState.update { it.copy(commentSortHot = nextHot) }
    showToast(if (nextHot) "已切换为按热度排序" else "已切换为按时间排序")
  }

  fun setReplyTarget(commentId: String, authorName: String) {
    _uiState.update { it.copy(replyTarget = CommunityReplyTarget(commentId, authorName)) }
  }

  fun clearReplyTarget() {
    _uiState.update { it.copy(replyTarget = null) }
  }

  fun toggleCommentLike(postId: String, commentId: String, replyId: String?) {
    _uiState.update { state ->
      val list = state.commentsByPostId[postId] ?: CommunitySeedData.defaultComments
      val updated =
        list.map { comment ->
          if (comment.id != commentId) {
            comment
          } else if (replyId == null) {
            val next = !comment.isLiked
            comment.copy(
              isLiked = next,
              likeCount = if (next) comment.likeCount + 1 else (comment.likeCount - 1).coerceAtLeast(0),
            )
          } else {
            comment.copy(
              replies =
                comment.replies.map { r ->
                  if (r.id != replyId) r
                  else {
                    val next = !r.isLiked
                    r.copy(
                      isLiked = next,
                      likeCount = if (next) r.likeCount + 1 else (r.likeCount - 1).coerceAtLeast(0),
                    )
                  }
                }
            )
          }
        }
      state.copy(commentsByPostId = state.commentsByPostId + (postId to updated))
    }
  }

  fun submitComment(postId: String, content: String) {
    val clean = content.trim()
    if (clean.isEmpty()) return
    val target = _uiState.value.replyTarget
    _uiState.update { state ->
      val currentList = state.commentsByPostId[postId] ?: CommunitySeedData.defaultComments
      val nextList =
        if (target != null) {
          currentList.map { c ->
            if (c.id == target.commentId) {
              c.copy(
                replies =
                  c.replies +
                    CommunitySubReply(
                      id = "sub_${System.currentTimeMillis()}",
                      author = "我",
                      authorInitial = "我",
                      avatarRes = R.drawable.img_story_jinghua,
                      replyToAuthor = target.authorName,
                      content = clean,
                      timestamp = "刚刚",
                      likeCount = 0,
                    )
              )
            } else {
              c
            }
          }
        } else {
          listOf(
            CommunityComment(
              id = "comm_${System.currentTimeMillis()}",
              author = "我",
              authorInitial = "我",
              avatarRes = R.drawable.img_story_jinghua,
              content = clean,
              timestamp = "刚刚",
              likeCount = 0,
            )
          ) + currentList
        }
      state.copy(
        commentsByPostId = state.commentsByPostId + (postId to nextList),
        replyTarget = null,
      )
    }
    updatePostById(postId) { it.copy(commentCount = it.commentCount + 1) }
    showToast("评论已发布")
  }

  fun publishNewPost(
    bodyText: String,
    tags: List<String>,
    images: List<CommunityImageItem>,
    poll: CommunityPoll?,
    document: CommunityDocument?,
    linkCard: CommunityLinkCard?,
    bountyCard: CommunityBountyCard?,
    musicTrack: CommunityMusicTrack?,
  ) {
    val primaryTopic = tags.firstOrNull()?.removePrefix("#")?.takeIf { it.isNotBlank() } ?: "OC交流"
    val cleanBody = bodyText.trim().ifEmpty { "分享了一条新动态" }
    val newId = "post_user_${System.currentTimeMillis()}"
    val newPost =
      CommunityPost(
        id = newId,
        topic = primaryTopic,
        authorName = "我 · 岛遇居民",
        avatarInitial = "我",
        avatarRes = R.drawable.img_story_jinghua,
        timeAndSource = "刚刚 · 来自 岛遇社区",
        inlineTagPill = "#$primaryTopic",
        summaryBody = cleanBody,
        showReadFullLink = cleanBody.length > 72 || document != null,
        fullParagraphs = listOf(cleanBody),
        detailTags = tags.map { if (it.startsWith("#")) it else "#$it" },
        detailTimestamp = "发布于 刚刚",
        detailReadsLabel = "阅读 1",
        images = images,
        poll = poll,
        document = document,
        linkCard = linkCard,
        bountyCard = bountyCard,
        musicTrack = musicTrack,
        likeCount = 0,
        commentCount = 0,
        bookmarkCount = 0,
        repostCount = 0,
        searchKeywords = "$cleanBody ${tags.joinToString(" ")}",
      )
    _uiState.update { state ->
      state.copy(
        posts = listOf(newPost) + state.posts,
        selectedTopic = "推荐",
        searchQuery = "",
      )
    }
    showToast("动态发布成功")
  }

  fun showToast(msg: String) {
    toastJob?.cancel()
    _uiState.update { it.copy(toastMessage = msg) }
    toastJob =
      viewModelScope.launch {
        delay(1800L)
        _uiState.update { it.copy(toastMessage = null) }
      }
  }

  private fun updatePostById(postId: String, transform: (CommunityPost) -> CommunityPost) {
    _uiState.update { state ->
      state.copy(
        posts = state.posts.map { if (it.id == postId) transform(it) else it },
        showcasePosts = state.showcasePosts.map { if (it.id == postId) transform(it) else it },
      )
    }
  }
}

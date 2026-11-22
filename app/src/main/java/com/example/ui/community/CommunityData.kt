package com.example.ui.community

import androidx.annotation.DrawableRes
import com.example.R
import com.example.ui.plaza.VBadgeTier

enum class CommunityDocFormat(val badgeLabel: String, val desc: String) {
  PDF("PDF", "PDF 文档"),
  DOC("DOC", "Word 文档"),
  TXT("TXT", "纯文本文档"),
  MD("MD", "Markdown 文档");

  companion object {
    fun fromFileName(name: String): CommunityDocFormat {
      return when (name.substringAfterLast('.', "").lowercase()) {
        "pdf" -> PDF
        "doc", "docx" -> DOC
        "md" -> MD
        else -> TXT
      }
    }
  }
}

enum class CommunityQuoteTarget {
  SCRIPT_LOBBY,
  WECHAT_SKIN,
}

data class CommunityImageItem(
  @DrawableRes val drawableRes: Int? = null,
  val contentUri: String? = null,
)

data class CommunityPollOption(
  val id: String,
  val label: String,
  val votes: Int,
)

data class CommunityPoll(
  val title: String,
  val modeLabel: String,
  val deadlineLabel: String,
  val maxSelectCount: Int = 1,
  val options: List<CommunityPollOption>,
  val pendingSelectedIds: Set<String> = emptySet(),
  val confirmedSelectedIds: Set<String> = emptySet(),
  val hasVoted: Boolean = false,
) {
  val totalVotes: Int
    get() = options.sumOf { it.votes } + if (hasVoted) 1 else 0
}

data class CommunityDocument(
  val fileName: String,
  val sizeLabel: String,
  val format: CommunityDocFormat,
  val metaNote: String = "动态附件",
  val previewSnippet: String = "包含《京华深处》主角十年宿敌档案、青瓦旧书店触发词表及 AI 演绎语气示例，共 14 页设定正文。",
)

data class CommunityLinkCard(
  val title: String,
  val url: String,
  val domain: String,
)

data class CommunityBountyCard(
  val amountYuan: Int,
  val deadlineLabel: String,
  val statusLabel: String = "招募中",
  val requirements: String,
  val applicantCount: Int = 3,
  val hasApplied: Boolean = false,
)

data class CommunityMusicTrack(
  val title: String,
  val metaLabel: String,
  val durationSec: Int,
  val elapsedSec: Int = 0,
)

data class CommunityQuoteCard(
  val eyebrow: String,
  val title: String,
  val subtitle: String,
  val ctaLabel: String? = null,
  @DrawableRes val thumbRes: Int? = null,
  val target: CommunityQuoteTarget,
)

data class CommunityPost(
  val id: String,
  val topic: String,
  val authorName: String,
  val avatarInitial: String,
  @DrawableRes val avatarRes: Int? = null,
  val badgeTier: VBadgeTier = VBadgeTier.NONE,
  val timeAndSource: String,
  val inlineTagPill: String,
  val summaryBody: String,
  val showReadFullLink: Boolean = false,
  val fullParagraphs: List<String> = emptyList(),
  val detailTags: List<String> = emptyList(),
  val detailTimestamp: String = "发布于 2026-11-19 04:45",
  val detailReadsLabel: String = "阅读 4,820",
  val accessoryLabel: String? = null,
  val images: List<CommunityImageItem> = emptyList(),
  val poll: CommunityPoll? = null,
  val document: CommunityDocument? = null,
  val linkCard: CommunityLinkCard? = null,
  val bountyCard: CommunityBountyCard? = null,
  val musicTrack: CommunityMusicTrack? = null,
  val quoteCard: CommunityQuoteCard? = null,
  val likeCount: Int,
  val isLiked: Boolean = false,
  val commentCount: Int,
  val bookmarkCount: Int,
  val isBookmarked: Boolean = false,
  val repostCount: Int,
  val isFollowed: Boolean = false,
  val searchKeywords: String = "",
)

data class CommunitySubReply(
  val id: String,
  val author: String,
  val authorInitial: String,
  @DrawableRes val avatarRes: Int? = null,
  val isAuthorBadge: Boolean = false,
  val replyToAuthor: String? = null,
  val content: String,
  val timestamp: String,
  val likeCount: Int,
  val isLiked: Boolean = false,
)

data class CommunityComment(
  val id: String,
  val author: String,
  val authorInitial: String,
  @DrawableRes val avatarRes: Int? = null,
  val hotBadge: Boolean = false,
  val content: String,
  val timestamp: String,
  val likeCount: Int,
  val isLiked: Boolean = false,
  val replies: List<CommunitySubReply> = emptyList(),
)

data class CommunityReplyTarget(
  val commentId: String,
  val authorName: String,
)

object CommunitySeedData {
  val topics = listOf("推荐", "OC交流", "剧本吐槽", "AI演绎晒图", "约稿避雷", "同好扩列")

  val sampleGalleryDrawables =
    listOf(
      R.drawable.img_story_jinghua,
      R.drawable.img_story_nightboat,
      R.drawable.img_floating_island_hero,
      R.drawable.img_update_banner,
    )

  val feedPosts =
    listOf(
      CommunityPost(
        id = "post_lingdu_jinghua",
        topic = "OC交流",
        authorName = "零度写作",
        avatarInitial = "零",
        avatarRes = R.drawable.img_story_jinghua,
        badgeTier = VBadgeTier.RED,
        timeAndSource = "12分钟前 · 来自 岛遇客户端",
        inlineTagPill = "#OC交流",
        summaryBody = "把《京华深处》两位主角的早年档案补全了！表面上是政商两界的死对头，结果十年前雨夜在同一个旧书店避过雨。大家更喜欢看他们“暗中试探”还是“直接摊牌”？",
        showReadFullLink = true,
        fullParagraphs =
          listOf(
            "把《京华深处》两位主角的早年档案彻底补全了！表面上是政商两界水火不容的死对头，每次同台都要明里暗里互相拆台，结果十年前那场暴雨夜里，两个人其实在同一家旧书店屋檐下避过雨。",
            "这次顺便把 AI 演绎时的提示词逻辑也重写了一遍：如果玩家在对话里提到“青瓦旧书店”或者“薄荷烟盒”，对方会先沉默三秒再反问，不会立刻把底牌交出来，拉扯感比初版强太多。",
            "大家在游玩长文线的时候，更喜欢看他们前期继续“暗中试探”，还是第三章就直接“摊牌对峙”？欢迎在评论区告诉我，呼声高的话今晚就把分支剧情更新到主页！",
          ),
        detailTags = listOf("#OC交流", "#京华深处", "#双人宿敌向"),
        detailTimestamp = "发布于 2026-11-19 04:45",
        detailReadsLabel = "阅读 4,820",
        images = listOf(CommunityImageItem(drawableRes = R.drawable.img_story_jinghua)),
        document =
          CommunityDocument(
            fileName = "《京华深处》角色档案.pdf",
            sizeLabel = "1.8 MB",
            format = CommunityDocFormat.PDF,
            metaNote = "动态附件",
          ),
        likeCount = 892,
        commentCount = 64,
        bookmarkCount = 52,
        repostCount = 128,
        searchKeywords = "零度写作 京华深处 宿敌 人设 设定图",
      ),
      CommunityPost(
        id = "post_linshen_poll",
        topic = "OC交流",
        authorName = "林深见鹿",
        avatarInitial = "林",
        avatarRes = R.drawable.img_story_nightboat,
        badgeTier = VBadgeTier.BLUE,
        timeAndSource = "38分钟前 · 话题发起人",
        inlineTagPill = "#OC交流",
        summaryBody = "写权谋线卡在第三章转折了，如果是你们来选，更想看到主角团怎么破局？投个票帮我定方向👇",
        poll =
          CommunityPoll(
            title = "剧情走向投票",
            modeLabel = "单选",
            deadlineLabel = "3天截止",
            options =
              listOf(
                CommunityPollOption("opt_a", "A. 假意投诚敌对阵营，反手拿捏底牌", 268),
                CommunityPollOption("opt_b", "B. 直接掀桌公开对峙，逼出幕后真凶", 150),
              ),
          ),
        likeCount = 316,
        commentCount = 95,
        bookmarkCount = 19,
        repostCount = 42,
        searchKeywords = "林深见鹿 投票 剧情 阵营 贴吧讨论",
      ),
      CommunityPost(
        id = "post_nanfeng_script",
        topic = "剧本吐槽",
        authorName = "南风知意",
        avatarInitial = "南",
        avatarRes = R.drawable.img_floating_island_hero,
        badgeTier = VBadgeTier.VIOLET,
        timeAndSource = "1小时前 · 剧本局玩家",
        inlineTagPill = "#剧本吐槽",
        summaryBody = "刚打完《雨夜旧怀表》四人局，AI扮演的管家最后十分钟突然反水给我整懵了😂 建议拿“私家侦探”牌的姐妹一定先去查二楼钟表匠的抽屉，千万别信他递过来的热茶！",
        quoteCard =
          CommunityQuoteCard(
            eyebrow = "关联剧本局",
            title = "《雨夜旧怀表》4人沉浸推理局",
            subtitle = "AI实时演绎分支 · 已报名 3/4 人",
            thumbRes = R.drawable.img_story_nightboat,
            target = CommunityQuoteTarget.SCRIPT_LOBBY,
          ),
        likeCount = 409,
        commentCount = 51,
        bookmarkCount = 24,
        repostCount = 73,
        searchKeywords = "南风知意 雨夜旧怀表 剧本局 推理 吐槽",
      ),
      CommunityPost(
        id = "post_shenqingyan_skin",
        topic = "AI演绎晒图",
        authorName = "沈清晏",
        avatarInitial = "沈",
        avatarRes = R.drawable.img_update_banner,
        timeAndSource = "3小时前 · 游玩达人",
        inlineTagPill = "#AI演绎晒图",
        summaryBody = "换了仿微信聊天皮肤之后代入感直接拉满！刚才让角色半夜发消息问我“楼下便利店关门了吗”，AI居然自己接了一句“给你留了最后一份关东煮”，差点以为真人在聊天😭",
        quoteCard =
          CommunityQuoteCard(
            eyebrow = "同款角色卡：周砚白 · 仿微信聊天皮肤",
            title = "周砚白 · 仿微信聊天皮肤",
            subtitle = "点击立即进入游玩对话",
            ctaLabel = "去试试",
            target = CommunityQuoteTarget.WECHAT_SKIN,
          ),
        likeCount = 214,
        commentCount = 28,
        bookmarkCount = 46,
        repostCount = 35,
        searchKeywords = "沈清晏 AI演绎 微信皮肤 对话 晒图",
      ),
      CommunityPost(
        id = "post_acc_music",
        topic = "AI演绎晒图",
        authorName = "零度写作",
        avatarInitial = "零",
        avatarRes = R.drawable.img_story_jinghua,
        badgeTier = VBadgeTier.RED,
        timeAndSource = "3小时前 · 创作BGM",
        inlineTagPill = "#创作BGM",
        summaryBody = "写雨夜重逢那场戏时单曲循环的纯音乐，戴上耳机再读正文更有代入感！",
        musicTrack =
          CommunityMusicTrack(
            title = "雨夜旧书店 · 氛围纯音乐",
            metaLabel = "MP3 · 4.2 MB",
            durationSec = 204,
            elapsedSec = 68,
          ),
        likeCount = 189,
        commentCount = 22,
        bookmarkCount = 114,
        repostCount = 45,
        searchKeywords = "音乐 BGM 雨夜旧书店 纯音乐",
      ),
      CommunityPost(
        id = "post_acc_bounty",
        topic = "约稿避雷",
        authorName = "鹿栖",
        avatarInitial = "鹿",
        avatarRes = R.drawable.img_floating_island_hero,
        timeAndSource = "2小时前 · 约稿需求",
        inlineTagPill = "#定制悬赏",
        summaryBody = "想约一位擅长宿敌拉扯感的老师写一条支线，预算和具体需求都写在下面悬赏卡里了！",
        bountyCard =
          CommunityBountyCard(
            amountYuan = 100,
            deadlineLabel = "7天截稿",
            statusLabel = "招募中",
            requirements = "具体要求：征集双人宿敌向雨夜对峙支线剧本，约 3000 字，要求情绪递进细腻、台词有交锋感，交付格式为 Markdown 或 Word。",
            applicantCount = 3,
          ),
        likeCount = 128,
        commentCount = 19,
        bookmarkCount = 44,
        repostCount = 11,
        searchKeywords = "鹿栖 定制悬赏 宿敌 约稿",
      ),
    )

  // Stitch 29 集中展示的 6 种配件发布卡片
  val accessoryShowcasePosts =
    listOf(
      CommunityPost(
        id = "acc_1_images",
        topic = "AI演绎晒图",
        authorName = "零度写作",
        avatarInitial = "零",
        avatarRes = R.drawable.img_story_jinghua,
        timeAndSource = "3分钟前",
        accessoryLabel = "1 · 图片",
        inlineTagPill = "#AI演绎晒图",
        summaryBody = "把《京华深处》的三张氛围概念图导出来了，雨夜旧书店那张最有宿敌对峙的张力！",
        images =
          listOf(
            CommunityImageItem(drawableRes = R.drawable.img_story_jinghua),
            CommunityImageItem(drawableRes = R.drawable.img_story_nightboat),
            CommunityImageItem(drawableRes = R.drawable.img_floating_island_hero),
          ),
        likeCount = 156,
        commentCount = 18,
        bookmarkCount = 42,
        repostCount = 24,
      ),
      CommunityPost(
        id = "acc_2_link",
        topic = "OC交流",
        authorName = "林深见鹿",
        avatarInitial = "林",
        avatarRes = R.drawable.img_story_nightboat,
        timeAndSource = "12分钟前",
        accessoryLabel = "2 · 链接",
        inlineTagPill = "#OC交流",
        summaryBody = "整理了一版完整的人物关系网页时间线，点开可以直接看各阵营十年前的羁绊节点：",
        linkCard =
          CommunityLinkCard(
            title = "《京华深处》人物关系网页档案",
            url = "https://daoyu.app/story/jinghua-oc",
            domain = "daoyu.app",
          ),
        likeCount = 94,
        commentCount = 14,
        bookmarkCount = 61,
        repostCount = 39,
      ),
      CommunityPost(
        id = "acc_3_doc",
        topic = "同好扩列",
        authorName = "零度写作",
        avatarInitial = "零",
        avatarRes = R.drawable.img_story_jinghua,
        timeAndSource = "28分钟前",
        accessoryLabel = "3 · 文档",
        inlineTagPill = "#设定共享",
        summaryBody = "把完整的角色卡与世界观设定集导出成 PDF 了，欢迎同好下载参考或二创演绎！",
        document =
          CommunityDocument(
            fileName = "《京华深处》角色档案.pdf",
            sizeLabel = "1.8 MB",
            format = CommunityDocFormat.PDF,
            metaNote = "PDF 文档",
          ),
        likeCount = 312,
        commentCount = 31,
        bookmarkCount = 195,
        repostCount = 87,
      ),
      CommunityPost(
        id = "acc_4_poll",
        topic = "OC交流",
        authorName = "夜航船",
        avatarInitial = "夜",
        avatarRes = R.drawable.img_update_banner,
        timeAndSource = "1小时前",
        accessoryLabel = "4 · 投票",
        inlineTagPill = "#剧情共创",
        summaryBody = "下一章的转折点大家来投个票，票数最高的那条线我今晚就动笔！",
        poll =
          CommunityPoll(
            title = "大家更喜欢主角团哪种破局方式？",
            modeLabel = "单选",
            deadlineLabel = "3天截止",
            options =
              listOf(
                CommunityPollOption("acc_opt_a", "A. 假意投诚敌对阵营，暗中拿捏底牌", 248),
                CommunityPollOption("acc_opt_b", "B. 直接掀桌公开对峙，逼出幕后真凶", 124),
                CommunityPollOption("acc_opt_c", "C. 制造假死脱身，重开马甲暗线调查", 56),
              ),
          ),
        likeCount = 204,
        commentCount = 72,
        bookmarkCount = 29,
        repostCount = 16,
      ),
      CommunityPost(
        id = "acc_5_bounty",
        topic = "约稿避雷",
        authorName = "鹿栖",
        avatarInitial = "鹿",
        avatarRes = R.drawable.img_floating_island_hero,
        timeAndSource = "2小时前",
        accessoryLabel = "5 · 悬赏",
        inlineTagPill = "#定制悬赏",
        summaryBody = "想约一位擅长宿敌拉扯感的老师写一条支线，预算和具体需求都写在下面悬赏卡里了！",
        bountyCard =
          CommunityBountyCard(
            amountYuan = 100,
            deadlineLabel = "7天截稿",
            statusLabel = "招募中",
            requirements = "具体要求：征集双人宿敌向雨夜对峙支线剧本，约 3000 字，要求情绪递进细腻、台词有交锋感，交付格式为 Markdown 或 Word。",
            applicantCount = 3,
          ),
        likeCount = 128,
        commentCount = 19,
        bookmarkCount = 44,
        repostCount = 11,
      ),
      CommunityPost(
        id = "acc_6_music",
        topic = "AI演绎晒图",
        authorName = "零度写作",
        avatarInitial = "零",
        avatarRes = R.drawable.img_story_jinghua,
        timeAndSource = "3小时前",
        accessoryLabel = "6 · 音乐",
        inlineTagPill = "#创作BGM",
        summaryBody = "写雨夜重逢那场戏时单曲循环的纯音乐，戴上耳机再读正文更有代入感！",
        musicTrack =
          CommunityMusicTrack(
            title = "雨夜旧书店 · 氛围纯音乐",
            metaLabel = "MP3 · 4.2 MB",
            durationSec = 204,
            elapsedSec = 68,
          ),
        likeCount = 189,
        commentCount = 22,
        bookmarkCount = 114,
        repostCount = 45,
      ),
    )

  val defaultComments =
    listOf(
      CommunityComment(
        id = "comm_c1",
        author = "月色微凉",
        authorInitial = "月",
        avatarRes = R.drawable.img_floating_island_hero,
        hotBadge = true,
        content = "十年前旧书店避雨的设定太戳了！一定要先暗中试探再掉马，拉扯感拉满！",
        timestamp = "11-07 · 江苏",
        likeCount = 146,
        replies =
          listOf(
            CommunitySubReply(
              id = "comm_r1",
              author = "零度写作",
              authorInitial = "零",
              avatarRes = R.drawable.img_story_jinghua,
              isAuthorBadge = true,
              content = "收到！今晚先把试探分支的心理描写再加厚一点。",
              timestamp = "8分钟前",
              likeCount = 36,
            )
          ),
      ),
      CommunityComment(
        id = "comm_c2",
        author = "星河不渡",
        authorInitial = "星",
        avatarRes = R.drawable.img_story_nightboat,
        content = "刚才去试了下“薄荷烟盒”关键词，AI 真的停顿了一下才反问，细节太绝了！",
        timestamp = "14分钟前 · 浙江",
        likeCount = 58,
      ),
      CommunityComment(
        id = "comm_c3",
        author = "苏念安",
        authorInitial = "苏",
        content = "已经收藏准备今晚去游玩模式里和沈总对戏了，这个人物设定写得好细腻。",
        timestamp = "昨天 21:14",
        likeCount = 19,
      ),
    )
}

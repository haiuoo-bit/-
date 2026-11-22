package com.example.ui.plaza

import androidx.annotation.DrawableRes
import com.example.R

enum class VBadgeTier { NONE, RED, BLUE, VIOLET }

data class PlazaBannerSlide(
  val id: String,
  val badge: String,
  val meta: String,
  val title: String,
  val subtitle: String,
  val targetAction: String,
)

data class PlazaNoticeItem(
  val id: String,
  val tag: String,
  val title: String,
  val body: String,
  val isBlueTag: Boolean,
)

data class PlazaLatestWork(
  val id: String,
  val storyRouteId: String,
  val title: String,
  val author: String,
  val tags: List<String>,
  val likesText: String,
  @DrawableRes val coverRes: Int?,
)

data class PlazaActivityItem(
  val id: String,
  val kind: String,
  val title: String,
  val meta: String,
  val tag: String,
  val actionLabel: String,
  val targetRoute: String,
)

enum class RankingBoardType(val label: String) {
  TOTAL("总热度榜"),
  SURGING("长文飙升榜"),
  SCRIPTS("高能剧本榜"),
  CREATORS("人气创作者"),
}

data class RankingEntry(
  val id: String,
  val rank: Int,
  val title: String,
  val author: String,
  val tag: String,
  val score: Int,
  val riseBadge: String? = null,
  val voted: Boolean = false,
  val storyRouteId: String = "jinghua",
)

enum class BountyPanel { CUSTOM, BOUNTY }

data class CreatorShowcaseItem(
  val id: String,
  val name: String,
  val avatarInitial: String,
  val vTier: VBadgeTier,
  val topics: List<String>,
  val priceText: String,
  val responseSpeed: String,
  val deliveredCount: Int,
  val intro: String,
)

data class BountyRequestItem(
  val id: String,
  val title: String,
  val topic: String,
  val budgetText: String,
  val deadlineText: String,
  val applicantsCount: Int,
  val description: String,
  val hasApplied: Boolean = false,
)

enum class ScriptStatus(val label: String) {
  OPEN("报名中"),
  ACTIVE("进行中"),
  ENDED("已截止"),
}

enum class ScriptFilter(val label: String) {
  ALL("全部"),
  OPEN("报名中"),
  ACTIVE("进行中"),
  ENDED("已截止"),
}

data class ScriptLobbyItem(
  val id: String,
  val title: String,
  val status: ScriptStatus,
  val tags: List<String>,
  val timeLabel: String,
  val currentPlayers: Int,
  val maxPlayers: Int,
  val roles: List<String>,
  val selectedRole: String? = null,
)

enum class RelayFilter(val label: String) {
  ALL("综合推荐"),
  SPEED("稳定首选"),
  VALUE("性价比高"),
  FREE("免费额度"),
}

data class RelayStationItem(
  val id: String,
  val name: String,
  val score: Double,
  val reviewsCount: Int,
  val isVerified: Boolean,
  val category: RelayFilter,
  val priceText: String,
  val uptimeText: String,
  val modelsText: String,
  val latencyMs: Int,
  val userRatedStars: Int? = null,
)

data class TagMeta(
  val id: String,
  val name: String,
  val category: String,
  val count: Int,
  val hot: Boolean = false,
)

data class TagWorkItem(
  val id: String,
  val storyRouteId: String,
  val title: String,
  val author: String,
  val vTier: VBadgeTier,
  val desc: String,
  val tags: List<String>,
  val likes: Int,
  val players: Int,
  val isNew: Boolean = false,
  @DrawableRes val coverRes: Int,
)

enum class SearchScope(val label: String) {
  ALL("全部"),
  WORKS("作品设定"),
  COMMUNITY("社区动态"),
  CREATORS("创作者"),
  GROUPS("群聊群号"),
}

enum class GroupJoinMode { DIRECT, APPROVAL }
enum class GroupJoinStatus { IDLE, PENDING, JOINED }

data class SearchGroupItem(
  val id: String,
  val groupNumber: String,
  val name: String,
  val membersCount: Int,
  val intro: String,
  val joinMode: GroupJoinMode,
  val status: GroupJoinStatus = GroupJoinStatus.IDLE,
)

data class SearchWorkItem(
  val id: String,
  val storyRouteId: String,
  val title: String,
  val author: String,
  val tags: List<String>,
  val summary: String,
)

data class SearchChatItem(
  val id: String,
  val title: String,
  val snippet: String,
  val storyRouteId: String,
)

data class SearchCreatorItem(
  val id: String,
  val name: String,
  val vTier: VBadgeTier,
  val worksCount: Int,
  val followersText: String,
  val bio: String,
  val isFollowed: Boolean = false,
)

data class SearchCommunityPostItem(
  val id: String,
  val author: String,
  val timeAgo: String,
  val content: String,
  val likes: Int,
)

data class HotSearchEntry(
  val rank: Int,
  val term: String,
  val tag: String,
  val isHotTag: Boolean,
)

object PlazaSeedData {
  val plazaTags = listOf("全部", "#现代都市", "#古风权谋", "#悬疑推理", "#星际科幻", "#治愈日常", "#双强宿敌", "#港风")

  val bannerSlides =
    listOf(
      PlazaBannerSlide(
        id = "slide_contest",
        badge = "活动",
        meta = "征稿至 11.30",
        title = "浮空岛秋季长文创作大赛",
        subtitle = "最高奖池 ￥12,000 + 蓝V认证绿色通道",
        targetAction = "notice",
      ),
      PlazaBannerSlide(
        id = "slide_script",
        badge = "剧本局",
        meta = "还差 1 位",
        title = "《雨夜旧怀表》4人沉浸推理局",
        subtitle = "今晚 20:00 开车 · 剧本已完成平台存证",
        targetAction = "scripts",
      ),
      PlazaBannerSlide(
        id = "slide_custom",
        badge = "定制",
        meta = "平台担保",
        title = "认证创作者开放「专属OC长文定制」",
        subtitle = "阶段验收交付 · 全程交易可追溯",
        targetAction = "bounty",
      ),
      PlazaBannerSlide(
        id = "slide_lantern",
        badge = "平台公告",
        meta = "即将开启",
        title = "浮空岛新春灯会 · 联名主题企划 (v2.6)",
        subtitle = "全岛创作者联欢活动预告，敬请期待",
        targetAction = "toast_v26",
      ),
    )

  val plazaNotices =
    listOf(
      PlazaNoticeItem(
        id = "notice_1",
        tag = "蓝V认证公示",
        title = "@零度叙事 通过优质创作者审核",
        body = "公示期 3 日，认证条件：近 30 天持续原创且获赞数 ≥ 1,000。",
        isBlueTag = true,
      ),
      PlazaNoticeItem(
        id = "notice_2",
        tag = "紫V晋升公示",
        title = "@浮光旧梦 晋升核心生态创作者",
        body = "累计完结长篇设定集 5 部，社区互动良好。",
        isBlueTag = false,
      ),
      PlazaNoticeItem(
        id = "notice_3",
        tag = "征稿公示",
        title = "秋季长文创作大赛第 2 期入围名单",
        body = "共 18 篇作品进入终审，点击榜单页面查看本周热度变化。",
        isBlueTag = true,
      ),
    )

  val latestWorks =
    listOf(
      PlazaLatestWork(
        id = "w_jinghua",
        storyRouteId = "jinghua",
        title = "《京华深处》番外：长廊撑伞",
        author = "@墨染浮生",
        tags = listOf("#古风权谋", "#长文"),
        likesText = "248赞",
        coverRes = R.drawable.img_story_jinghua,
      ),
      PlazaLatestWork(
        id = "w_baizhou",
        storyRouteId = "nightboat",
        title = "《白昼焰火》破镜重圆人设卡",
        author = "@白昼焰火",
        tags = listOf("#现代都市", "#双强宿敌"),
        likesText = "189赞",
        coverRes = R.drawable.img_story_nightboat,
      ),
      PlazaLatestWork(
        id = "w_wugang",
        storyRouteId = "jinghua",
        title = "雾港回响：悬疑双男主开篇",
        author = "@零度叙事",
        tags = listOf("#悬疑推理", "#港风"),
        likesText = "96赞",
        coverRes = null,
      ),
    )

  val featuredActivities =
    listOf(
      PlazaActivityItem(
        id = "act_rank",
        kind = "榜单",
        title = "本周热榜 Top 1 ·《京华深处》",
        meta = "@墨染浮生 · 8,420 热度",
        tag = "#古风权谋",
        actionLabel = "看榜",
        targetRoute = "rankings",
      ),
      PlazaActivityItem(
        id = "act_custom_yue",
        kind = "定制",
        title = "开放 OC 长文定制 & 人设卡约稿",
        meta = "@月见草 · 蓝V · 24h内排单",
        tag = "#现代都市",
        actionLabel = "约稿",
        targetRoute = "bounty_custom",
      ),
      PlazaActivityItem(
        id = "act_bounty_watch",
        kind = "悬赏",
        title = "悬赏征集：雨夜旧怀表（4,000字+）",
        meta = "赏金 ￥1,500 · 平台担保托管中",
        tag = "#悬疑推理",
        actionLabel = "接赏",
        targetRoute = "bounty_task",
      ),
      PlazaActivityItem(
        id = "act_script_sleep",
        kind = "剧本局",
        title = "剧本局组局中：星际休眠舱 4人本",
        meta = "还差 1 位船长位 · 明晚 20:30 开车",
        tag = "#星际科幻",
        actionLabel = "上车",
        targetRoute = "scripts",
      ),
      PlazaActivityItem(
        id = "act_custom_nanfeng",
        kind = "定制",
        title = "港风双男主短篇番外定制",
        meta = "@南风知意 · 已交付 34 单",
        tag = "#港风",
        actionLabel = "约稿",
        targetRoute = "bounty_custom",
      ),
    )

  val rankingsByBoard: Map<RankingBoardType, List<RankingEntry>> =
    mapOf(
      RankingBoardType.TOTAL to listOf(
        RankingEntry("r1", 1, "《京华深处》", "@墨染浮生", "#古风权谋", 8420, "飙升 +18%", storyRouteId = "jinghua"),
        RankingEntry("r2", 2, "《雨夜旧怀表》", "@零度叙事", "#悬疑推理", 6150, storyRouteId = "jinghua"),
        RankingEntry("r3", 3, "《星际休眠舱》", "@云深不知", "#星际科幻", 5820, storyRouteId = "nightboat"),
        RankingEntry("r4", 4, "《浮空岛长歌》", "@南风知意", "#治愈日常", 4390, storyRouteId = "nightboat"),
      ),
      RankingBoardType.SURGING to listOf(
        RankingEntry("s1", 1, "《白昼焰火》", "@白昼焰火", "#现代都市", 7290, "热度 +42%", storyRouteId = "nightboat"),
        RankingEntry("s2", 2, "《京华深处》", "@墨染浮生", "#古风权谋", 6940, "热度 +18%", storyRouteId = "jinghua"),
        RankingEntry("s3", 3, "《雾港回响》", "@零度叙事", "#港风", 5110, "热度 +15%", storyRouteId = "jinghua"),
        RankingEntry("s4", 4, "《霓虹与玫瑰》", "@月见草", "#双强宿敌", 4080, storyRouteId = "nightboat"),
      ),
      RankingBoardType.SCRIPTS to listOf(
        RankingEntry("sc1", 1, "《星际休眠舱 · 4人局》", "@云深不知", "#星际科幻", 6680, "开局率 98%", storyRouteId = "nightboat"),
        RankingEntry("sc2", 2, "《京华深处·雨夜审讯室》", "@墨染浮生", "#古风权谋", 5930, storyRouteId = "jinghua"),
        RankingEntry("sc3", 3, "《旧怀表时间回廊》", "@零度叙事", "#无限流", 4820, storyRouteId = "jinghua"),
        RankingEntry("sc4", 4, "《浮空岛秋日祭典》", "@南风知意", "#全员群像", 4250, storyRouteId = "nightboat"),
      ),
      RankingBoardType.CREATORS to listOf(
        RankingEntry("cr1", 1, "@墨染浮生", "紫V · 12篇公开长文", "#古风权谋", 9120, "本周新增 +1.2k", storyRouteId = "jinghua"),
        RankingEntry("cr2", 2, "@月见草", "蓝V · 48单零差评", "#现代都市", 7430, storyRouteId = "nightboat"),
        RankingEntry("cr3", 3, "@零度叙事", "蓝V · 推理悬疑专栏", "#悬疑推理", 6810, storyRouteId = "jinghua"),
        RankingEntry("cr4", 4, "@南风知意", "红V · 日更温暖叙事", "#治愈日常", 5540, storyRouteId = "nightboat"),
      ),
    )

  val bountyTopics = listOf("全部", "现代都市", "古风权谋", "星际科幻", "悬疑推理")

  val creators =
    listOf(
      CreatorShowcaseItem(
        id = "c_yue",
        name = "月见草",
        avatarInitial = "月",
        vTier = VBadgeTier.BLUE,
        topics = listOf("#古风权谋", "#现代都市"),
        priceText = "￥300 起",
        responseSpeed = "24h内回复",
        deliveredCount = 48,
        intro = "擅长古风朝堂暗涌与现代双强人物小传，支持分阶段大纲试写与角色语音台词卡。",
      ),
      CreatorShowcaseItem(
        id = "c_lingdu",
        name = "零度写作",
        avatarInitial = "零",
        vTier = VBadgeTier.VIOLET,
        topics = listOf("#悬疑推理", "#星际科幻"),
        priceText = "￥450 起",
        responseSpeed = "48h内回复",
        deliveredCount = 19,
        intro = "核心生态紫V，专注硬核时空推理线索链与多人剧本杀架构，提供完整世界观文档。",
      ),
      CreatorShowcaseItem(
        id = "c_nanfeng",
        name = "南风知意",
        avatarInitial = "南",
        vTier = VBadgeTier.RED,
        topics = listOf("#现代都市", "#古风权谋"),
        priceText = "￥260 起",
        responseSpeed = "当天可接",
        deliveredCount = 34,
        intro = "高产出红V创作者，主打细腻氛围感短篇番外、OC人设卡润色及治愈日常剧情。",
      ),
    )

  val bounties =
    listOf(
      BountyRequestItem(
        id = "b_watch",
        title = "【悬赏】写一篇《雨夜旧怀表》4000字完整剧本杀",
        topic = "悬疑推理",
        budgetText = "￥1,500 · 已托管",
        deadlineText = "截止 3 天后",
        applicantsCount = 6,
        description = "要求包含 4 位嫌疑人独立视角线索卡、时间线还原表与 2 个反转结局分支，平台已全额托管赏金。",
      ),
      BountyRequestItem(
        id = "b_space",
        title = "【悬赏】星际太空歌剧开篇三章",
        topic = "星际科幻",
        budgetText = "￥2,200 · 已托管",
        deadlineText = "截止 5 天后",
        applicantsCount = 3,
        description = "需要塑造冷峻舰长与仿生领航员之间的张力关系，附带飞船设定术语表（不少于 6000 字）。",
      ),
    )

  val scripts =
    listOf(
      ScriptLobbyItem(
        id = "sc_sleep",
        title = "星际休眠舱",
        status = ScriptStatus.OPEN,
        tags = listOf("阵营", "微恐", "4人本"),
        timeLabel = "今晚 20:30 开车",
        currentPlayers = 3,
        maxPlayers = 4,
        roles = listOf("船长位（空缺）", "AI领航员（已选）", "随行医生（已选）", "机械师（已选）"),
      ),
      ScriptLobbyItem(
        id = "sc_jinghua",
        title = "京华深处 · 雨夜审讯室",
        status = ScriptStatus.ACTIVE,
        tags = listOf("权谋", "对峙", "6人本"),
        timeLabel = "已开局 2 小时",
        currentPlayers = 5,
        maxPlayers = 6,
        roles = listOf("大理寺卿", "东宫幕僚", "锦衣卫密使", "宫廷画师", "旁观记录位"),
      ),
      ScriptLobbyItem(
        id = "sc_autumn",
        title = "浮空岛秋日祭",
        status = ScriptStatus.ENDED,
        tags = listOf("治愈", "群像", "16人本"),
        timeLabel = "本周五 20:00",
        currentPlayers = 16,
        maxPlayers = 16,
        roles = listOf("全员已满"),
      ),
    )

  val relayStations =
    listOf(
      RelayStationItem(
        id = "rs_island",
        name = "云屿直连 IslandAPI",
        score = 4.9,
        reviewsCount = 42,
        isVerified = true,
        category = RelayFilter.SPEED,
        priceText = "￥0.8 / 1M",
        uptimeText = "99.9%",
        modelsText = "Claude 3.5 / GPT-4o",
        latencyMs = 420,
      ),
      RelayStationItem(
        id = "rs_star",
        name = "星海算力 StarToken",
        score = 4.7,
        reviewsCount = 29,
        isVerified = true,
        category = RelayFilter.VALUE,
        priceText = "￥0.65 / 1M",
        uptimeText = "99.5%",
        modelsText = "DeepSeek / Gemini",
        latencyMs = 560,
      ),
      RelayStationItem(
        id = "rs_lime",
        name = "青柠公益 LimeFree",
        score = 4.3,
        reviewsCount = 17,
        isVerified = false,
        category = RelayFilter.FREE,
        priceText = "免费额度",
        uptimeText = "96.8%",
        modelsText = "开源模型",
        latencyMs = 890,
      ),
    )

  val tagCategories = listOf("热门", "全部", "背景风格", "角色身份", "关系情节", "氛围偏好")

  val allTags =
    listOf(
      TagMeta("t1", "古风权谋", "背景风格", 184, hot = true),
      TagMeta("t2", "现代都市", "背景风格", 245, hot = true),
      TagMeta("t3", "悬疑推理", "氛围偏好", 112, hot = true),
      TagMeta("t4", "星际科幻", "背景风格", 76),
      TagMeta("t5", "双强宿敌", "关系情节", 168, hot = true),
      TagMeta("t6", "青梅竹马", "关系情节", 134),
      TagMeta("t7", "无限流", "背景风格", 92, hot = true),
      TagMeta("t8", "治愈日常", "氛围偏好", 156),
      TagMeta("t9", "久别重逢", "关系情节", 128),
      TagMeta("t10", "反派逆袭", "角色身份", 88),
      TagMeta("t11", "温柔腹黑", "角色身份", 142),
      TagMeta("t12", "民国往事", "背景风格", 64),
      TagMeta("t13", "全员群像", "关系情节", 71),
      TagMeta("t14", "高岭之花", "角色身份", 119),
      TagMeta("t15", "慢热", "氛围偏好", 83),
      TagMeta("t16", "末世废土", "背景风格", 58),
    )

  val tagWorks =
    listOf(
      TagWorkItem(
        id = "tw1",
        storyRouteId = "jinghua",
        title = "《京华深处》",
        author = "@墨染浮生",
        vTier = VBadgeTier.VIOLET,
        desc = "风雨欲来的京城长廊，一个握着伞，一个站在暗影里……",
        tags = listOf("古风权谋", "双强宿敌", "久别重逢"),
        likes = 1240,
        players = 480,
        isNew = true,
        coverRes = R.drawable.img_story_jinghua,
      ),
      TagWorkItem(
        id = "tw2",
        storyRouteId = "jinghua",
        title = "《雨夜旧怀表》",
        author = "@零度叙事",
        vTier = VBadgeTier.BLUE,
        desc = "停在零点十二分的怀表，藏着七年前未解的密码。",
        tags = listOf("悬疑推理", "无限流", "现代都市"),
        likes = 856,
        players = 620,
        coverRes = R.drawable.img_story_nightboat,
      ),
      TagWorkItem(
        id = "tw3",
        storyRouteId = "nightboat",
        title = "《浮空岛长歌》",
        author = "@南风知意",
        vTier = VBadgeTier.RED,
        desc = "风起云涌的云端浮岛，记录每位过客的小故事。",
        tags = listOf("治愈日常", "全员群像", "慢热"),
        likes = 642,
        players = 310,
        isNew = true,
        coverRes = R.drawable.img_floating_island_hero,
      ),
      TagWorkItem(
        id = "tw4",
        storyRouteId = "nightboat",
        title = "《霓虹与玫瑰》",
        author = "@月见草",
        vTier = VBadgeTier.NONE,
        desc = "赛博雨夜下，两位宿敌在天台交换了彼此的筹码。",
        tags = listOf("现代都市", "双强宿敌", "温柔腹黑"),
        likes = 918,
        players = 540,
        coverRes = R.drawable.img_story_nightboat,
      ),
    )

  val defaultRecentSearches = listOf("双强宿敌", "882019", "古风权谋", "星际休眠舱")

  val hotSearches =
    listOf(
      HotSearchEntry(1, "#京华深处", "热", true),
      HotSearchEntry(2, "星际休眠舱", "剧", false),
      HotSearchEntry(3, "破镜重圆", "热", true),
      HotSearchEntry(4, "882019", "群", false),
      HotSearchEntry(5, "月见草", "人", false),
      HotSearchEntry(6, "云屿直连", "测", false),
    )

  val searchGroups =
    listOf(
      SearchGroupItem(
        id = "sg1",
        groupNumber = "882019",
        name = "古风权谋 · 设定灵感互助站",
        membersCount = 128,
        intro = "日常讨论古代官制、建筑服饰与长文叙事节奏",
        joinMode = GroupJoinMode.DIRECT,
      ),
      SearchGroupItem(
        id = "sg2",
        groupNumber = "661042",
        name = "星际与无限流剧本开车群",
        membersCount = 86,
        intro = "每周五/六晚固定开 4-6 人阵营推理剧本局",
        joinMode = GroupJoinMode.APPROVAL,
      ),
      SearchGroupItem(
        id = "sg3",
        groupNumber = "518024",
        name = "双强宿敌OC互换扩列",
        membersCount = 214,
        intro = "欢迎带自家高光人设卡和片段进来玩",
        joinMode = GroupJoinMode.DIRECT,
      ),
      SearchGroupItem(
        id = "sg4",
        groupNumber = "740916",
        name = "中转站测速与自建讨论群",
        membersCount = 52,
        intro = "交流延迟实测、模型适配和踩坑避雷经验",
        joinMode = GroupJoinMode.APPROVAL,
      ),
    )

  val searchWorks =
    listOf(
      SearchWorkItem("sw1", "jinghua", "《京华深处》", "@墨染浮生", listOf("#古风权谋", "#双强宿敌"), "雨夜长廊重逢，暗潮涌动的朝堂双强人设卡与长文开篇…"),
      SearchWorkItem("sw2", "jinghua", "《雨夜旧怀表》", "@零度叙事", listOf("#悬疑推理", "#无限流"), "停摆的怀表记录着 12 次时间回廊，四位嫌疑人各怀隐情…"),
      SearchWorkItem("sw3", "nightboat", "《星际休眠舱》", "@云深不知", listOf("#星际科幻", "#群像"), "深空航行第 412 天，休眠舱苏醒协议被未知权限提前触发…"),
      SearchWorkItem("sw4", "nightboat", "《白昼焰火》", "@白昼焰火", listOf("#现代都市", "#久别重逢"), "五年后再见于香港中环天桥，旧相机胶卷里的秘密…"),
    )

  val searchChats =
    listOf(
      SearchChatItem("sch1", "《京华深处》 · 与顾长渊对话", "顾长渊撑伞垂眸：「大人深夜造访寒舍，所为何事？」", "jinghua"),
      SearchChatItem("sch2", "《星际休眠舱》 · AI领航员交互", "「航线偏移 0.4°，休眠舱内还有 1 位乘客未唤醒。」", "nightboat"),
    )

  val searchCreators =
    listOf(
      SearchCreatorItem("scr1", "墨染浮生", VBadgeTier.VIOLET, 12, "8.4k", "专注于古风权谋与双强羁绊长文设定创作"),
      SearchCreatorItem("scr2", "月见草", VBadgeTier.BLUE, 8, "3.2k", "认证约稿创作者，擅长都市破镜重圆题材"),
      SearchCreatorItem("scr3", "零度叙事", VBadgeTier.BLUE, 15, "5.1k", "悬疑剧本杀与无限流世界观架构师"),
    )

  val searchCommunityPosts =
    listOf(
      SearchCommunityPostItem("scp1", "@浮光旧梦", "15 分钟前", "刚更新了《京华深处》第 4 章伏笔梳理，双强宿敌之间的微表情描写实在太带感了！", 42),
      SearchCommunityPostItem("scp2", "@南风知意", "1 小时前", "分享一个快速写「久别重逢」开场白的小技巧：先给一个具体的天气或物件动作。", 68),
    )
}

package com.example.ui.plaza

import androidx.annotation.DrawableRes
import com.example.R

// --- 54 & 55 小手机与模拟器 ---
enum class EmulatorCategory(val label: String) {
  ALL("全部"),
  FREE("免费开源"),
  PAID("主题壳"),
}

data class EmulatorShowcaseSlide(
  @DrawableRes val imageRes: Int,
  val caption: String,
)

data class EmulatorItem(
  val id: String,
  val badge: String,
  val platform: String,
  val title: String,
  val shortDesc: String,
  val intro: String,
  val version: String,
  val sizeMb: Double,
  val priceYuan: Double,
  val isFreeCategory: Boolean,
  val tags: List<String>,
  val downloadUrl: String,
  val isWebLink: Boolean = false,
  @DrawableRes val coverRes: Int,
  val showcaseImages: List<EmulatorShowcaseSlide>,
  val isUnlocked: Boolean = priceYuan <= 0.0,
  val isUnlocking: Boolean = false,
  val downloadProgressPct: Int = 0,
  val isDownloading: Boolean = false,
  val isDownloadDone: Boolean = false,
)

// --- 56 & 70 破破甲集市 ---
enum class MarketCategory(val label: String) {
  ALL("全部好物"),
  PROMPTS("指令预设"),
  SKINS("聊天皮肤"),
  TEMPLATES("设定模板"),
  PLUGINS("功能插件"),
}

data class MarketItem(
  val id: String,
  val category: MarketCategory,
  val categoryBadge: String,
  val formatLabel: String,
  val salesLabel: String,
  val title: String,
  val summary: String,
  val detailIntro: String,
  val priceText: String,
  val seller: String,
  val fileName: String,
  val fileMeta: String,
  val ctaLabel: String,
  val previewContent: String,
  @DrawableRes val galleryRes: List<Int>,
  val isPurchased: Boolean = false,
  val isFavorited: Boolean = false,
)

enum class MarketBuyStep {
  HIDDEN,
  CONFIRM,
  LOADING,
  SUCCESS,
}

// --- 22 中转测评 · 站点详情 ---
data class RelayModelRate(
  val name: String,
  val usageNote: String,
  val priceTag: String,
  val isDiscount: Boolean = false,
)

data class RelayStationExtended(
  val id: String,
  val badgeChar: String,
  val badgeTone: String, // "dark", "amber", "green"
  val tagLabel: String,
  val rateText: String,
  val latencyText: String,
  val modelCountText: String,
  val endpointUrl: String,
  val supportedModels: List<RelayModelRate>,
  val selectedImpressions: Set<String> = emptySet(),
  val feedbackNote: String = "",
)

// --- 21 剧本局 · 活动详情 ---
data class ScriptFactionOption(
  val name: String,
  val currentCount: Int,
  val maxCount: Int,
) {
  val isFull: Boolean get() = currentCount >= maxCount
  val remainCount: Int get() = (maxCount - currentCount).coerceAtLeast(0)
}

data class ScriptActivityDetailState(
  val scriptId: String = "sc_sleep",
  val title: String = "《星际休眠舱：第7号觉醒者》",
  val hostInfo: String = "房主：零度写作 · #星际科幻",
  val rewardSummary: String = "奖励：星海徽章 +￥200",
  val currentPlayers: Int = 3,
  val maxPlayers: Int = 4,
  @DrawableRes val coverRes: Int = R.drawable.img_story_nightboat,
  val storyBackground: String =
    "公元 2198 年，“方舟七号”承载着三千名休眠殖民者驶向开普勒星域。深空航行第 40 年，7 号休眠舱提前 120 天意外开启，主控记录已被人为擦除。四位苏醒者各自握有一段残缺记忆——有人隐瞒航线偏离真相，有人必须阻止返航。全程由 AI 主持实时演绎分支剧情。",
  val rulesList: List<String> = listOf(
    "共三幕演绎：每幕 25 分钟，由 AI 主持人同步推进飞船事件。",
    "每位玩家每幕可发起 1 次“私密搜证”或“阵营密谈”。",
    "最终投票决定飞船“返航”或“深空跃迁”，解锁多分支结局。",
  ),
  val factions: List<ScriptFactionOption> = listOf(
    ScriptFactionOption("返航派", 1, 2),
    ScriptFactionOption("机械师团", 1, 2),
    ScriptFactionOption("星港联盟", 0, 2),
    ScriptFactionOption("觉醒派", 2, 2),
    ScriptFactionOption("观测站", 2, 2),
    ScriptFactionOption("深空会", 1, 2),
  ),
  val selectedFaction: String = "返航派",
  val docFileName: String = "方舟七号 · 角色与世界观设定.md",
  val docFileMeta: String = "含 MD / TXT / Word 版本 · 2.4 MB",
  val enrolled: Boolean = false,
  val proofUploaded: Boolean = false,
  val rewardClaimed: Boolean = false,
)

object PlazaExpansionSeedData {
  val emulators =
    listOf(
      EmulatorItem(
        id = "retro-handheld",
        badge = "￥8",
        platform = "Android / HarmonyOS",
        title = "星环复古掌机小手机（主题壳）",
        shortDesc = "像素风掌机边框与角色好感度状态栏，购买后即可直接下载",
        intro = "像素风掌机边框搭配角色好感度状态栏，适合剧情向互动养成。支持十字方向键与 A/B 键触感模拟、顶部实时显示好感度与心情状态，内置 6 款像素外壳皮肤，购买解锁后即可直接下载并同步至「我的岛屿库」。",
        version = "v1.9.3",
        sizeMb = 28.4,
        priceYuan = 8.0,
        isFreeCategory = false,
        tags = listOf("像素主题", "先购后下", "6.7k 次下载"),
        downloadUrl = "https://daoyu.app/dl/star-retro-handheld-1.9.3.apk",
        coverRes = R.drawable.img_story_nightboat,
        showcaseImages = listOf(
          EmulatorShowcaseSlide(R.drawable.img_story_nightboat, "半透明复古掌机外壳实拍 · 十字键与 A/B 键触感反馈"),
          EmulatorShowcaseSlide(R.drawable.img_floating_island_hero, "内置 6 套像素主题皮肤 · 顶部实时显示好感度与心情"),
          EmulatorShowcaseSlide(R.drawable.img_story_jinghua, "剧情向互动对话框 · 支持立绘切换与存档读档"),
        ),
      ),
      EmulatorItem(
        id = "cloud-pwa",
        badge = "免安装",
        platform = "Web / PWA 全平台",
        title = "云端小手机网页版（HTML 离线包）",
        shortDesc = "浏览器直接打开即用，也可下载纯静态离线包到本地保存存档",
        intro = "iOS 与电脑浏览器直接打开即用，Safari 添加到主屏幕即可全屏沉浸使用。也可点击下方下载纯静态 HTML 离线包到本地，断网环境下也能随时翻阅导出的聊天存档，API Key 仅存本地更安心。",
        version = "离线包",
        sizeMb = 14.2,
        priceYuan = 0.0,
        isFreeCategory = true,
        tags = listOf("iOS 友好", "免越狱", "1.2w 次使用"),
        downloadUrl = "https://daoyu.app/cloud-phone/",
        isWebLink = true,
        coverRes = R.drawable.img_floating_island_hero,
        showcaseImages = listOf(
          EmulatorShowcaseSlide(R.drawable.img_floating_island_hero, "浏览器打开即用 · 自适应手机、平板与电脑桌面"),
          EmulatorShowcaseSlide(R.drawable.img_story_jinghua, "添加到主屏幕全屏沉浸体验 · 本地存储聊天存档"),
          EmulatorShowcaseSlide(R.drawable.img_update_banner, "纯静态 HTML 离线包 · 断网也能随时翻阅导出记录"),
        ),
      ),
      EmulatorItem(
        id = "miniphone-os",
        badge = "官方主推",
        platform = "Android / iOS",
        title = "岛遇小手机 OS（拟真桌面版）",
        shortDesc = "拟真微信聊天、朋友圈、备忘录与人设卡一键导入系统",
        intro = "专为 OC 扮演与双人互动打造的拟真虚拟手机系统。内置微信聊天、朋友圈、备忘录、相册、通话记录与锁屏通知，支持一键导入人设卡与世界书，本地离线保存全部聊天记录，也可自由填写个人 API 中转站地址。",
        version = "v2.4.1",
        sizeMb = 42.8,
        priceYuan = 0.0,
        isFreeCategory = true,
        tags = listOf("开源免费", "拟真桌面", "2.1w 次下载"),
        downloadUrl = "https://daoyu.app/dl/miniphone-os-v2.4.1.apk",
        coverRes = R.drawable.img_update_banner,
        showcaseImages = listOf(
          EmulatorShowcaseSlide(R.drawable.img_update_banner, "拟真小手机主桌面 · 聊天、相册、备忘录与角色心情组件"),
          EmulatorShowcaseSlide(R.drawable.img_story_jinghua, "沉浸式微信风格对话界面 · 支持状态栏与气泡主题切换"),
          EmulatorShowcaseSlide(R.drawable.img_floating_island_hero, "人设卡与世界书一键导入 · 多端存档实时同步"),
        ),
      ),
      EmulatorItem(
        id = "lime-desktop",
        badge = "桌面多开",
        platform = "Windows / macOS",
        title = "青柠桌面安卓模拟器（多开版）",
        shortDesc = "电脑大屏同屏多开 2-6 个小手机角色窗口，一键导出长图",
        intro = "电脑大屏畅玩小手机与聊天皮肤，支持同时运行 2–6 个虚拟手机实例，适合多人剧本局、双视角演绎与长文截图拼接。内置键鼠打字直连与一键无水印长截图，轻薄本也能低功耗流畅运行。",
        version = "v5.1.0",
        sizeMb = 318.0,
        priceYuan = 0.0,
        isFreeCategory = true,
        tags = listOf("Win / Mac", "多开同屏", "8.9k 次下载"),
        downloadUrl = "https://daoyu.app/dl/lime-emu-win-mac-5.1.zip",
        coverRes = R.drawable.img_story_jinghua,
        showcaseImages = listOf(
          EmulatorShowcaseSlide(R.drawable.img_story_jinghua, "电脑大屏多开同屏 · 同时运行多个小手机角色窗口"),
          EmulatorShowcaseSlide(R.drawable.img_story_nightboat, "独立虚拟手机桌面 · 支持键鼠快捷打字与长截图拼接"),
          EmulatorShowcaseSlide(R.drawable.img_update_banner, "高清群聊与双视角演绎视图 · 一键导出无水印对话长图"),
        ),
      ),
    )

  val marketItems =
    listOf(
      MarketItem(
        id = "doc-ooc-anchor",
        category = MarketCategory.PROMPTS,
        categoryBadge = "指令预设",
        formatLabel = "MD / TXT",
        salesLabel = "已售 1,284",
        title = "《零OOC角色锚定铁律 · 通用防崩坏指令》",
        summary = "通过性格底线、口癖校验与情绪阈值三段式约束，防止长对话后期人设漂移、突然降智或语气失真，粘贴即用。",
        detailIntro = "通过性格底线、口癖校验与情绪阈值三段式约束，防止长对话后期人设漂移、突然降智或语气失真。适合长期养卡和长线剧情使用，下载文件后直接复制或导入即可。",
        priceText = "￥6",
        seller = "零度写作",
        fileName = "零OOC角色锚定铁律.md",
        fileMeta = "48 KB · 购买后可下载",
        ctaLabel = "购买文档",
        previewContent = "【角色锚定铁律】\n1. 性格底线：任何情况下保持核心性格与说话习惯；\n2. 口癖校验：每轮回复保持自然口癖；\n3. 情绪阈值：根据好感度渐进变化，禁止突兀跳变。",
        galleryRes = listOf(R.drawable.img_story_jinghua, R.drawable.img_floating_island_hero, R.drawable.img_story_nightboat),
      ),
      MarketItem(
        id = "doc-deepseek-summary",
        category = MarketCategory.PROMPTS,
        categoryBadge = "指令预设",
        formatLabel = "PDF / MD",
        salesLabel = "已售 2,410",
        title = "《DeepSeek 长对话记忆压缩与续聊指令》",
        summary = "专为 DeepSeek 优化的对话总结指令：把 50 轮以上剧情压缩进 800 字上下文，告别遗忘，和角色聊得更久。",
        detailIntro = "专为 DeepSeek 优化的对话总结指令，把 50 轮以上剧情压缩进 800 字上下文，告别遗忘，和角色聊得更久。",
        priceText = "￥9.9",
        seller = "林深见鹿",
        fileName = "DeepSeek长对话续聊压缩指令.pdf",
        fileMeta = "124 KB · 购买后可下载",
        ctaLabel = "购买文档",
        previewContent = "【DeepSeek 续聊记忆压缩模板】\n请将前序对话浓缩为以下4段：\n1. 已发生关键事件（按时间线）；\n2. 当前场景、物品状态与未解伏笔；\n3. 双方关系进展与情绪基调；\n4. 下一轮衔接指令（直接续写不复述）。",
        galleryRes = listOf(R.drawable.img_story_nightboat, R.drawable.img_update_banner),
      ),
      MarketItem(
        id = "goods-wechat-skins",
        category = MarketCategory.SKINS,
        categoryBadge = "聊天皮肤",
        formatLabel = "主题资源包",
        salesLabel = "已售 678",
        title = "《拟真微信与深夜信纸聊天皮肤 · 6套合集》",
        summary = "包含仿微信气泡、手机备忘录、旧书店信纸等 6 款对话皮肤与头像框，支持导入小手机或游玩页直接套用。",
        detailIntro = "包含仿微信气泡、手机备忘录、旧书店信纸等 6 款对话皮肤与头像框，下载资源包后支持导入小手机或游玩页直接套用。",
        priceText = "￥5",
        seller = "沈清晏",
        fileName = "拟真聊天皮肤6套合集.zip",
        fileMeta = "3.2 MB · 购买后可下载",
        ctaLabel = "购买合集",
        previewContent = "已解锁 6 套聊天皮肤：\n- 经典微信白绿气泡\n- 深夜暗色护眼气泡\n- 旧书店手工信纸\n- 极简备忘录卡片\n- 复古短信蓝绿\n- 雨夜雾面玻璃主题",
        galleryRes = listOf(R.drawable.img_update_banner, R.drawable.img_story_jinghua, R.drawable.img_floating_island_hero),
      ),
      MarketItem(
        id = "goods-rival-worldbook",
        category = MarketCategory.TEMPLATES,
        categoryBadge = "设定模板",
        formatLabel = "DOCX / MD",
        salesLabel = "已售 512",
        title = "《双人宿敌向 · 完整世界观与关系线模板》",
        summary = "内置 12 个冲突事件节点与羁绊好感度触发条件，支持一键导入角色卡或自定义修改，省去从零搭设定的时间。",
        detailIntro = "内置 12 个冲突事件节点与羁绊好感度触发条件，支持一键导入角色卡或自定义修改，省去从零搭设定的时间。",
        priceText = "￥12",
        seller = "岛遇官方严选",
        fileName = "双人宿敌世界观与关系线模板.docx",
        fileMeta = "210 KB · 购买后可下载",
        ctaLabel = "购买模板",
        previewContent = "包含内容清单：\n- 12 个核心宿敌冲突事件节点\n- 5 阶段双人羁绊好感度触发条件\n- 3 套开局冷启动对话示例\n- 隐藏支线与黑化分支开关设定",
        galleryRes = listOf(R.drawable.img_floating_island_hero, R.drawable.img_story_nightboat),
      ),
      MarketItem(
        id = "plugin-status-panel",
        category = MarketCategory.PLUGINS,
        categoryBadge = "功能插件",
        formatLabel = "JSON / JS",
        salesLabel = "已售 936",
        title = "《动态好感度与状态栏美化插件 · 小手机通用》",
        summary = "非纯文档，内含正则渲染脚本与状态栏配置文件；导入后可在对话底部实时显示好感度、场景时间与角色心境标签。",
        detailIntro = "内含正则渲染脚本与状态栏配置文件；下载后在小手机或游玩设置的插件管理中导入，即可在对话底部实时显示好感度、场景时间与角色心境标签。",
        priceText = "￥8.8",
        seller = "夜航船",
        fileName = "动态好感度状态栏插件包.zip",
        fileMeta = "340 KB · 购买后可下载",
        ctaLabel = "购买插件",
        previewContent = "【插件安装与导入说明】\n1. 本资源为功能扩展包（内含正则渲染规则与状态栏 JSON 配置）；\n2. 使用方式：点击「下载插件包」保存到本地 → 打开小手机或游玩设置 → 插件管理 → 导入配置即可自动显示好感度、时间与心境标签。",
        galleryRes = listOf(R.drawable.img_story_nightboat, R.drawable.img_story_jinghua, R.drawable.img_update_banner),
      ),
    )

  val relayDetailsByStationId: Map<String, RelayStationExtended> =
    mapOf(
      "rs_island" to RelayStationExtended(
        id = "rs_island",
        badgeChar = "云",
        badgeTone = "dark",
        tagLabel = "高分口碑",
        rateText = "1:1 原价",
        latencyText = "280 ms",
        modelCountText = "24 个",
        endpointUrl = "https://api.island-relay.top/v1",
        supportedModels = listOf(
          RelayModelRate("Claude 3.7 Sonnet", "长文演绎 · 细腻文风", "200K · 1:1 官价"),
          RelayModelRate("DeepSeek-R1", "推理剧情 · 性价比高", "64K · 0.8x 优惠", isDiscount = true),
          RelayModelRate("GPT-4o", "仿微信短句 · 首字极快", "128K · 1:1 官价"),
          RelayModelRate("Gemini 2.5 Pro", "超大记忆 · 多人剧本局", "1M · 1:1 官价"),
        ),
      ),
      "rs_star" to RelayStationExtended(
        id = "rs_star",
        badgeChar = "星",
        badgeTone = "amber",
        tagLabel = "性价比高",
        rateText = "0.7x 优惠",
        latencyText = "340 ms",
        modelCountText = "18 个",
        endpointUrl = "https://api.startoken-relay.cn/v1",
        supportedModels = listOf(
          RelayModelRate("DeepSeek-V3 / R1", "长线对话 · 低成本首选", "64K · 0.7x 优惠", isDiscount = true),
          RelayModelRate("Gemini 2.5 Flash", "轻量高并发 · 极速响应", "1M · 0.7x 优惠", isDiscount = true),
          RelayModelRate("Claude 3.5 Sonnet", "角色扮演 · 稳定拟人", "200K · 0.9x 优惠", isDiscount = true),
        ),
      ),
      "rs_lime" to RelayStationExtended(
        id = "rs_lime",
        badgeChar = "柠",
        badgeTone = "green",
        tagLabel = "免费试用",
        rateText = "赠体验金",
        latencyText = "520 ms",
        modelCountText = "12 个",
        endpointUrl = "https://api.limefree.org/v1",
        supportedModels = listOf(
          RelayModelRate("DeepSeek 开源通道", "每日免费签到体验", "32K · 免费额度", isDiscount = true),
          RelayModelRate("Qwen 2.5 72B", "中文长文 · 轻量日常", "64K · 免费额度", isDiscount = true),
          RelayModelRate("Llama 3.3 70B", "自由英文扮演", "128K · 免费额度", isDiscount = true),
        ),
      ),
    )

  val impressionTags = listOf("稳定不掉线", "首字很快", "价格透明", "适合长文", "高峰偶有波动")
}

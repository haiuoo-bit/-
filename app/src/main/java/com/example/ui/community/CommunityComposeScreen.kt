package com.example.ui.community

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.HowToVote
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.home.OverlayToastPill
import com.example.ui.theme.LightOnPrimary
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightOutlineVariant
import com.example.ui.theme.LightPrimary
import com.example.ui.theme.LightSecondary
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceContainer
import com.example.ui.theme.LightSurfaceContainerHigh
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommunityComposeScreen(
  toastMessage: String?,
  onCancel: () -> Unit,
  onOpenAccessoryShowcase: () -> Unit,
  onShowToast: (String) -> Unit,
  onPublish: (
    bodyText: String,
    tags: List<String>,
    images: List<CommunityImageItem>,
    poll: CommunityPoll?,
    document: CommunityDocument?,
    linkCard: CommunityLinkCard?,
    bountyCard: CommunityBountyCard?,
    musicTrack: CommunityMusicTrack?,
  ) -> Unit,
) {
  val context = LocalContext.current

  var bodyText by remember { mutableStateOf("") }
  val selectedTags = remember { mutableStateListOf("#OC交流") }
  var inlineTagInput by remember { mutableStateOf("") }

  var showImageMod by remember { mutableStateOf(true) }
  var showLinkMod by remember { mutableStateOf(false) }
  var showDocMod by remember { mutableStateOf(true) }
  var showPollMod by remember { mutableStateOf(true) }
  var showBountyMod by remember { mutableStateOf(false) }
  var showMusicMod by remember { mutableStateOf(false) }

  val images = remember { mutableStateListOf(CommunityImageItem(drawableRes = R.drawable.img_story_jinghua)) }

  var pollQuestion by remember { mutableStateOf("大家更喜欢主角团哪种破局方式？") }
  val pollOptions = remember { mutableStateListOf("假意投诚敌对阵营，暗中拿捏底牌", "直接掀桌公开对峙，逼出幕后真凶") }
  var pollMultiSelect by remember { mutableStateOf(false) }
  val deadlineChoices = listOf("24小时截止", "3天截止", "7天截止")
  var pollDeadlineIdx by remember { mutableStateOf(1) }

  var attachedDoc by remember {
    mutableStateOf(
      CommunityDocument(
        fileName = "《京华深处》角色档案.pdf",
        sizeLabel = "1.8 MB",
        format = CommunityDocFormat.PDF,
        metaNote = "动态附件",
      )
    )
  }

  var linkUrl by remember { mutableStateOf("https://daoyu.app/story/jinghua-oc") }
  var parsedLink by remember {
    mutableStateOf<CommunityLinkCard?>(
      CommunityLinkCard(
        title = "《京华深处》人物关系网页档案",
        url = "https://daoyu.app/story/jinghua-oc",
        domain = "daoyu.app",
      )
    )
  }

  var bountyAmount by remember { mutableStateOf("50") }
  val bountyDeadlines = listOf("3天截稿", "7天截稿", "15天截稿")
  var bountyDeadlineIdx by remember { mutableStateOf(1) }
  var bountyRequirements by remember { mutableStateOf("") }

  var musicTrack by remember { mutableStateOf<CommunityMusicTrack?>(null) }
  var isMusicPreviewPlaying by remember { mutableStateOf(false) }

  LaunchedEffect(isMusicPreviewPlaying, musicTrack != null) {
    if (isMusicPreviewPlaying && musicTrack != null) {
      while (true) {
        delay(1000L)
        musicTrack?.let { current ->
          val next = if (current.elapsedSec >= current.durationSec) 0 else current.elapsedSec + 1
          musicTrack = current.copy(elapsedSec = next)
        }
      }
    }
  }

  val photoPickerLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(9)) { uris ->
      if (uris.isNotEmpty()) {
        val slotsLeft = (9 - images.size).coerceAtLeast(0)
        uris.take(slotsLeft).forEach { uri ->
          images.add(CommunityImageItem(contentUri = uri.toString()))
        }
        showImageMod = true
        onShowToast("已添加 ${uris.take(slotsLeft).size} 张图片")
      }
    }

  val documentPickerLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
      if (uri != null) {
        val meta = queryFileMetadata(context, uri, fallbackName = "新设定档案.pdf")
        val fmt = CommunityDocFormat.fromFileName(meta.first)
        attachedDoc =
          CommunityDocument(
            fileName = meta.first,
            sizeLabel = meta.second,
            format = fmt,
            metaNote = fmt.desc,
          )
        showDocMod = true
        onShowToast("已选择文档：${meta.first}")
      }
    }

  val audioPickerLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
      if (uri != null) {
        val meta = queryFileMetadata(context, uri, fallbackName = "氛围创作配乐.mp3")
        val ext = meta.first.substringAfterLast('.', "MP3").uppercase()
        musicTrack =
          CommunityMusicTrack(
            title = meta.first.substringBeforeLast('.'),
            metaLabel = "$ext · ${meta.second}",
            durationSec = 195,
            elapsedSec = 0,
          )
        isMusicPreviewPlaying = false
        showMusicMod = true
        onShowToast("已载入音频：${meta.first}")
      }
    }

  fun tryAddTag(raw: String) {
    val clean = raw.trim().removePrefix("#").trim()
    if (clean.isEmpty()) return
    val normalized = "#$clean"
    if (normalized in selectedTags) {
      onShowToast("标签已存在")
      return
    }
    if (selectedTags.size >= 6) {
      onShowToast("最多添加 6 个标签")
      return
    }
    selectedTags.add(normalized)
    inlineTagInput = ""
  }

  Scaffold(
    containerColor = LightSurface,
    topBar = {
      Surface(color = LightSurface, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Text(
              text = stringResource(R.string.comm_compose_cancel),
              style = MaterialTheme.typography.bodyMedium,
              color = LightOnSurfaceVariant,
              modifier =
                Modifier.clip(CircleShape)
                  .clickable(onClick = onCancel)
                  .padding(horizontal = 10.dp, vertical = 8.dp)
                  .testTag("comm_compose_cancel_btn"),
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                text = stringResource(R.string.comm_compose_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = LightOnSurface,
              )
              Surface(
                shape = CircleShape,
                color = LightSurfaceContainer,
                contentColor = LightOnSurfaceVariant,
                modifier = Modifier.clip(CircleShape).clickable(onClick = onOpenAccessoryShowcase).testTag("comm_compose_preview_btn"),
              ) {
                Text("效果预览", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp))
              }
            }

            Surface(
              shape = CircleShape,
              color = LightPrimary,
              contentColor = LightOnPrimary,
              modifier =
                Modifier.height(36.dp)
                  .clip(CircleShape)
                  .clickable {
                    val hasAnyAttachment =
                      (showImageMod && images.isNotEmpty()) ||
                        showPollMod ||
                        showDocMod ||
                        (showLinkMod && parsedLink != null) ||
                        showBountyMod ||
                        (showMusicMod && musicTrack != null)
                    if (bodyText.isBlank() && !hasAnyAttachment) {
                      onShowToast("写点正文或挂载一个配件再发布吧")
                      return@clickable
                    }
                    val pollOut =
                      if (!showPollMod) null
                      else {
                        val validOpts = pollOptions.mapIndexedNotNull { i, text ->
                          val t = text.trim()
                          if (t.isEmpty()) null else CommunityPollOption("u_opt_$i", "${'A' + i}. $t", 1)
                        }
                        if (validOpts.size < 2) null
                        else {
                          CommunityPoll(
                            title = pollQuestion.trim().ifEmpty { "话题投票" },
                            modeLabel = if (pollMultiSelect) "多选" else "单选",
                            deadlineLabel = deadlineChoices[pollDeadlineIdx],
                            maxSelectCount = if (pollMultiSelect) 2 else 1,
                            options = validOpts,
                          )
                        }
                      }
                    val linkOut = if (showLinkMod) parseUrlToCard(linkUrl) ?: parsedLink else null
                    val bountyOut =
                      if (!showBountyMod) null
                      else {
                        CommunityBountyCard(
                          amountYuan = bountyAmount.toIntOrNull()?.coerceAtLeast(10) ?: 50,
                          deadlineLabel = bountyDeadlines[bountyDeadlineIdx],
                          requirements = bountyRequirements.trim().ifEmpty { "欢迎擅长 OC 人设或支线创作的老师接单沟通！" },
                          applicantCount = 0,
                        )
                      }
                    onPublish(
                      bodyText,
                      selectedTags.toList(),
                      if (showImageMod) images.toList() else emptyList(),
                      pollOut,
                      if (showDocMod) attachedDoc else null,
                      linkOut,
                      bountyOut,
                      if (showMusicMod) musicTrack else null,
                    )
                  }
                  .testTag("comm_compose_publish_btn"),
            ) {
              Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(stringResource(R.string.comm_compose_publish), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
              }
            }
          }
          HorizontalDivider(color = LightOutlineVariant.copy(alpha = 0.7f))
        }
      }
    },
    bottomBar = {
      Surface(color = LightSurface, shadowElevation = 4.dp) {
        Column(modifier = Modifier.fillMaxWidth().imePadding().navigationBarsPadding()) {
          HorizontalDivider(color = LightOutlineVariant.copy(alpha = 0.7f))
          Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            ComposeToolbarAction(Icons.Outlined.Image, "图片", showImageMod, Modifier.weight(1f).testTag("tb_image")) { showImageMod = !showImageMod }
            ComposeToolbarAction(Icons.Outlined.Link, "链接", showLinkMod, Modifier.weight(1f).testTag("tb_link")) { showLinkMod = !showLinkMod }
            ComposeToolbarAction(Icons.Outlined.Description, "文档", showDocMod, Modifier.weight(1f).testTag("tb_doc")) { showDocMod = !showDocMod }
            ComposeToolbarAction(Icons.Outlined.HowToVote, "投票", showPollMod, Modifier.weight(1f).testTag("tb_poll")) { showPollMod = !showPollMod }
            ComposeToolbarAction(Icons.Outlined.MonetizationOn, "悬赏", showBountyMod, Modifier.weight(1f).testTag("tb_bounty")) { showBountyMod = !showBountyMod }
            ComposeToolbarAction(Icons.Outlined.MusicNote, "音乐", showMusicMod, Modifier.weight(1f).testTag("tb_music")) { showMusicMod = !showMusicMod }
          }
        }
      }
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize().widthIn(max = 600.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        item {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              selectedTags.forEach { tag ->
                Surface(shape = CircleShape, color = LightPrimary, contentColor = LightOnPrimary) {
                  Row(
                    modifier = Modifier.padding(start = 10.dp, end = 6.dp, top = 5.dp, bottom = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                  ) {
                    Text(tag, style = MaterialTheme.typography.labelSmall)
                    Icon(
                      imageVector = Icons.Filled.Close,
                      contentDescription = "删除标签 $tag",
                      modifier = Modifier.size(14.dp).clickable { selectedTags.remove(tag) },
                    )
                  }
                }
              }

              listOf("#剧本吐槽", "#AI演绎晒图", "#约稿避雷").filter { it !in selectedTags }.forEach { quick ->
                Surface(
                  shape = CircleShape,
                  color = LightSurfaceContainer,
                  contentColor = LightOnSurfaceVariant,
                  modifier = Modifier.clip(CircleShape).clickable { tryAddTag(quick) },
                ) {
                  Text("+ $quick", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                }
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Box(
                modifier =
                  Modifier.weight(1f)
                    .height(36.dp)
                    .clip(CircleShape)
                    .background(LightSurfaceContainer)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart,
              ) {
                BasicTextField(
                  value = inlineTagInput,
                  onValueChange = { next ->
                    if (next.endsWith(" ")) {
                      tryAddTag(next)
                    } else {
                      inlineTagInput = next.take(18)
                    }
                  },
                  singleLine = true,
                  textStyle = MaterialTheme.typography.bodySmall.copy(color = LightOnSurface),
                  cursorBrush = SolidColor(LightSecondary),
                  keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                  keyboardActions = KeyboardActions(onDone = { tryAddTag(inlineTagInput) }),
                  modifier = Modifier.fillMaxWidth().testTag("compose_inline_tag_input"),
                  decorationBox = { inner ->
                    if (inlineTagInput.isEmpty()) {
                      Text(stringResource(R.string.comm_compose_tag_hint), style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
                    }
                    inner()
                  },
                )
              }
              Surface(
                shape = CircleShape,
                color = LightSurfaceContainerHigh,
                contentColor = LightOnSurface,
                modifier = Modifier.clip(CircleShape).clickable { tryAddTag(inlineTagInput.ifBlank { "同好扩列" }) },
              ) {
                Text(stringResource(R.string.comm_compose_add_tag), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp))
              }
            }
          }
        }

        item {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
              modifier = Modifier.fillMaxWidth().heightIn(min = 126.dp).padding(vertical = 4.dp),
              contentAlignment = Alignment.TopStart,
            ) {
              BasicTextField(
                value = bodyText,
                onValueChange = { bodyText = it.take(2000) },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = LightOnSurface),
                cursorBrush = SolidColor(LightSecondary),
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp).testTag("compose_body_input"),
                decorationBox = { inner ->
                  if (bodyText.isEmpty()) {
                    Text(stringResource(R.string.comm_compose_body_hint), style = MaterialTheme.typography.bodyMedium, color = LightOnSurfaceVariant)
                  }
                  inner()
                },
              )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
              Text(
                text = "${bodyText.length}/2000",
                style = MaterialTheme.typography.bodySmall,
                color = if (bodyText.length >= 1900) LightSecondary else LightOnSurfaceVariant,
              )
            }
          }
        }

        if (showImageMod) {
          item {
            ComposeImageModule(
              images = images,
              onPickFromGallery = {
                if (images.size >= 9) onShowToast("最多上传 9 张图片")
                else photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
              },
              onAddSampleImage = {
                if (images.size >= 9) {
                  onShowToast("最多上传 9 张图片")
                } else {
                  val pool = CommunitySeedData.sampleGalleryDrawables
                  images.add(CommunityImageItem(drawableRes = pool[images.size % pool.size]))
                }
              },
              onRemoveImage = { idx -> if (idx in images.indices) images.removeAt(idx) },
              onCloseModule = { showImageMod = false },
            )
          }
        }

        if (showPollMod) {
          item {
            ComposePollModule(
              question = pollQuestion,
              onQuestionChange = { pollQuestion = it.take(60) },
              options = pollOptions,
              onOptionChange = { idx, value -> if (idx in pollOptions.indices) pollOptions[idx] = value.take(36) },
              onAddOption = {
                if (pollOptions.size >= 4) onShowToast("最多支持 4 个投票选项")
                else pollOptions.add("")
              },
              onRemoveOption = { idx ->
                if (pollOptions.size <= 2) onShowToast("投票至少保留 2 个选项")
                else pollOptions.removeAt(idx)
              },
              isMultiSelect = pollMultiSelect,
              onToggleMultiSelect = { pollMultiSelect = !pollMultiSelect },
              deadlineLabel = deadlineChoices[pollDeadlineIdx],
              onCycleDeadline = { pollDeadlineIdx = (pollDeadlineIdx + 1) % deadlineChoices.size },
              onCloseModule = { showPollMod = false },
            )
          }
        }

        if (showDocMod) {
          item {
            ComposeDocModule(
              document = attachedDoc,
              onPickDocument = {
                documentPickerLauncher.launch(
                  arrayOf(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "text/plain",
                    "text/markdown",
                    "*/*",
                  )
                )
              },
              onSelectFormatPreset = { fmt ->
                val sampleName =
                  when (fmt) {
                    CommunityDocFormat.PDF -> "《京华深处》角色档案.pdf"
                    CommunityDocFormat.DOC -> "《京华深处》设定汇编.docx"
                    CommunityDocFormat.TXT -> "旧书店触发词清单.txt"
                    CommunityDocFormat.MD -> "AI演绎提示词模板.md"
                  }
                attachedDoc = attachedDoc.copy(fileName = sampleName, format = fmt, metaNote = fmt.desc)
              },
              onCloseModule = { showDocMod = false },
            )
          }
        }

        if (showLinkMod) {
          item {
            ComposeLinkModule(
              urlInput = linkUrl,
              onUrlChange = { linkUrl = it },
              parsedLink = parsedLink,
              onParseLink = {
                val parsed = parseUrlToCard(linkUrl)
                if (parsed == null) onShowToast("请输入有效的 https:// 链接")
                else {
                  parsedLink = parsed
                  onShowToast("已解析：${parsed.domain}")
                }
              },
              onCloseModule = { showLinkMod = false },
            )
          }
        }

        if (showBountyMod) {
          item {
            ComposeBountyModule(
              amountText = bountyAmount,
              onAmountChange = { bountyAmount = it },
              deadlineLabel = bountyDeadlines[bountyDeadlineIdx],
              onCycleDeadline = { bountyDeadlineIdx = (bountyDeadlineIdx + 1) % bountyDeadlines.size },
              requirements = bountyRequirements,
              onRequirementsChange = { bountyRequirements = it.take(300) },
              onCloseModule = { showBountyMod = false },
            )
          }
        }

        if (showMusicMod) {
          item {
            ComposeMusicModule(
              track = musicTrack,
              isPreviewPlaying = isMusicPreviewPlaying,
              onPickAudio = { audioPickerLauncher.launch(arrayOf("audio/*")) },
              onUseSampleAudio = {
                musicTrack =
                  CommunityMusicTrack(
                    title = "雨夜旧书店 · 氛围纯音乐",
                    metaLabel = "MP3 · 4.2 MB",
                    durationSec = 204,
                    elapsedSec = 0,
                  )
                isMusicPreviewPlaying = false
              },
              onTogglePreviewPlay = { isMusicPreviewPlaying = !isMusicPreviewPlaying },
              onCloseModule = {
                isMusicPreviewPlaying = false
                showMusicMod = false
              },
            )
          }
        }
      }

      OverlayToastPill(
        message = toastMessage,
        darkPolarity = false,
        modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
      )
    }
  }
}

@Composable
private fun ComposeToolbarAction(
  icon: ImageVector,
  label: String,
  active: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Column(
    modifier =
      modifier
        .heightIn(min = 48.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(if (active) LightSurfaceContainer else LightSurface)
        .clickable(onClick = onClick)
        .padding(vertical = 4.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = if (active) LightPrimary else LightOnSurfaceVariant,
      modifier = Modifier.size(20.dp),
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal),
      color = if (active) LightPrimary else LightOnSurfaceVariant,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

private fun parseUrlToCard(rawUrl: String): CommunityLinkCard? {
  val trimmed = rawUrl.trim()
  if (trimmed.isEmpty()) return null
  val normalized = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) trimmed else "https://$trimmed"
  val host = runCatching { Uri.parse(normalized).host }.getOrNull().orEmpty().ifEmpty { "daoyu.app" }
  return CommunityLinkCard(
    title = "《京华深处》网页设定档案",
    url = normalized,
    domain = host,
  )
}

private fun queryFileMetadata(context: Context, uri: Uri, fallbackName: String): Pair<String, String> {
  var name = fallbackName
  var sizeBytes = -1L
  runCatching {
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
      val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
      val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
      if (cursor.moveToFirst()) {
        if (nameIdx >= 0) name = cursor.getString(nameIdx) ?: fallbackName
        if (sizeIdx >= 0) sizeBytes = cursor.getLong(sizeIdx)
      }
    }
  }
  val sizeLabel =
    when {
      sizeBytes <= 0L -> "1.2 MB"
      sizeBytes < 1024L * 1024L -> "${(sizeBytes / 1024L).coerceAtLeast(1)} KB"
      else -> String.format("%.1f MB", sizeBytes / (1024f * 1024f))
    }
  return name to sizeLabel
}

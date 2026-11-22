package com.example.ui.community

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LightOnPrimary
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightOutlineVariant
import com.example.ui.theme.LightPrimary
import com.example.ui.theme.LightSecondary
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceContainer
import com.example.ui.theme.LightSurfaceContainerHigh
import com.example.ui.theme.LightSurfaceContainerLow
import com.example.ui.theme.WarningAmber

@Composable
fun ComposeModuleShell(
  title: String,
  trailingLabel: String? = null,
  onRemove: () -> Unit,
  content: @Composable () -> Unit,
) {
  Column(
    modifier =
      Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(LightSurfaceContainerLow)
        .border(1.dp, LightOutlineVariant.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
        .padding(12.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          color = LightOnSurface,
        )
        if (trailingLabel != null) {
          Text(
            text = trailingLabel,
            style = MaterialTheme.typography.bodySmall,
            color = LightOnSurfaceVariant,
          )
        }
      }
      Box(
        modifier =
          Modifier.size(26.dp)
            .clip(CircleShape)
            .background(LightSurfaceContainerHigh)
            .clickable(onClick = onRemove),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Filled.Close,
          contentDescription = "关闭$title",
          tint = LightOnSurfaceVariant,
          modifier = Modifier.size(14.dp),
        )
      }
    }
    content()
  }
}

@Composable
fun ComposeImageModule(
  images: List<CommunityImageItem>,
  onPickFromGallery: () -> Unit,
  onAddSampleImage: () -> Unit,
  onRemoveImage: (Int) -> Unit,
  onCloseModule: () -> Unit,
) {
  ComposeModuleShell(
    title = "图片配图 (${images.size}/9)",
    trailingLabel = if (images.size < 9) "点击上传或添加示例" else "已达上限",
    onRemove = onCloseModule,
  ) {
    val totalSlots = (images.size + if (images.size < 9) 1 else 0)
    val rows = (0 until totalSlots).chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      rows.forEach { rowIndices ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          rowIndices.forEach { slot ->
            if (slot < images.size) {
              Box(
                modifier =
                  Modifier.weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LightSurfaceContainer),
              ) {
                CommunityImageView(
                  item = images[slot],
                  contentDescription = "已选配图 ${slot + 1}",
                  modifier = Modifier.fillMaxSize(),
                )
                Box(
                  modifier =
                    Modifier.align(Alignment.TopEnd)
                      .padding(4.dp)
                      .size(22.dp)
                      .clip(CircleShape)
                      .background(LightPrimary.copy(alpha = 0.72f))
                      .clickable { onRemoveImage(slot) },
                  contentAlignment = Alignment.Center,
                ) {
                  Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "移除图片",
                    tint = LightOnPrimary,
                    modifier = Modifier.size(12.dp),
                  )
                }
              }
            } else {
              Column(
                modifier =
                  Modifier.weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LightSurface)
                    .border(1.dp, LightOutlineVariant, RoundedCornerShape(10.dp))
                    .clickable(onClick = onPickFromGallery)
                    .testTag("compose_pick_image_tile"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
              ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = LightOnSurfaceVariant, modifier = Modifier.size(22.dp))
                Spacer(Modifier.height(2.dp))
                Text("上传图片", style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
              }
            }
          }
          repeat(3 - rowIndices.size) {
            Spacer(Modifier.weight(1f))
          }
        }
      }
      if (images.size < 9) {
        Surface(
          shape = CircleShape,
          color = LightSurface,
          contentColor = LightOnSurfaceVariant,
          modifier =
            Modifier.border(1.dp, LightOutlineVariant, CircleShape)
              .clip(CircleShape)
              .clickable(onClick = onAddSampleImage)
              .testTag("compose_add_sample_img_btn"),
        ) {
          Text(
            text = "+ 加入一张示例氛围图",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComposePollModule(
  question: String,
  onQuestionChange: (String) -> Unit,
  options: List<String>,
  onOptionChange: (Int, String) -> Unit,
  onAddOption: () -> Unit,
  onRemoveOption: (Int) -> Unit,
  isMultiSelect: Boolean,
  onToggleMultiSelect: () -> Unit,
  deadlineLabel: String,
  onCycleDeadline: () -> Unit,
  onCloseModule: () -> Unit,
) {
  ComposeModuleShell(
    title = "互动投票",
    trailingLabel = "${options.size}/4 选项",
    onRemove = onCloseModule,
  ) {
    ComposeInlineInputBox(
      value = question,
      onValueChange = onQuestionChange,
      placeholder = "输入投票问题（例如：大家更喜欢主角团哪种破局方式？）",
      testTag = "compose_poll_question_input",
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      options.forEachIndexed { idx, optText ->
        val letter = ('A' + idx).toString()
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(
            modifier = Modifier.size(26.dp).clip(CircleShape).background(LightSurfaceContainerHigh),
            contentAlignment = Alignment.Center,
          ) {
            Text(letter, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = LightOnSurface)
          }
          Box(modifier = Modifier.weight(1f)) {
            ComposeInlineInputBox(
              value = optText,
              onValueChange = { onOptionChange(idx, it) },
              placeholder = "输入选项 $letter",
              testTag = "compose_poll_option_$idx",
            )
          }
          Box(
            modifier =
              Modifier.size(30.dp)
                .clip(CircleShape)
                .background(LightSurface)
                .border(1.dp, LightOutlineVariant, CircleShape)
                .clickable { onRemoveOption(idx) },
            contentAlignment = Alignment.Center,
          ) {
            Icon(Icons.Filled.Close, contentDescription = "删除选项", tint = LightOnSurfaceVariant, modifier = Modifier.size(14.dp))
          }
        }
      }
    }

    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Surface(
        shape = CircleShape,
        color = LightPrimary,
        contentColor = LightOnPrimary,
        modifier = Modifier.clip(CircleShape).clickable(onClick = onAddOption).testTag("compose_poll_add_opt_btn"),
      ) {
        Text("+ 添加选项", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
      }
      Surface(
        shape = CircleShape,
        color = LightSurface,
        contentColor = LightOnSurface,
        modifier = Modifier.border(1.dp, LightOutlineVariant, CircleShape).clip(CircleShape).clickable(onClick = onToggleMultiSelect),
      ) {
        Text(
          text = if (isMultiSelect) "多选(最多2项)" else "单选",
          style = MaterialTheme.typography.labelSmall,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
      }
      Surface(
        shape = CircleShape,
        color = LightSurface,
        contentColor = LightOnSurface,
        modifier = Modifier.border(1.dp, LightOutlineVariant, CircleShape).clip(CircleShape).clickable(onClick = onCycleDeadline),
      ) {
        Text(
          text = deadlineLabel,
          style = MaterialTheme.typography.labelSmall,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
      }
    }
  }
}

@Composable
fun ComposeDocModule(
  document: CommunityDocument,
  onPickDocument: () -> Unit,
  onSelectFormatPreset: (CommunityDocFormat) -> Unit,
  onCloseModule: () -> Unit,
) {
  ComposeModuleShell(
    title = "文档附件",
    trailingLabel = document.format.desc,
    onRemove = onCloseModule,
  ) {
    Row(
      modifier =
        Modifier.fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(LightSurface)
          .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
          .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      CommunityDocBadgeIcon(format = document.format)
      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = document.fileName,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          color = LightOnSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = "${document.sizeLabel} · ${document.format.badgeLabel}",
          style = MaterialTheme.typography.bodySmall,
          color = LightOnSurfaceVariant,
        )
      }
      Surface(
        shape = CircleShape,
        color = LightPrimary,
        contentColor = LightOnPrimary,
        modifier = Modifier.clip(CircleShape).clickable(onClick = onPickDocument).testTag("compose_pick_doc_btn"),
      ) {
        Text("选择文件", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp))
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
      Text("示例格式:", style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
      CommunityDocFormat.entries.forEach { fmt ->
        val selected = fmt == document.format
        Surface(
          shape = CircleShape,
          color = if (selected) LightPrimary else LightSurface,
          contentColor = if (selected) LightOnPrimary else LightOnSurfaceVariant,
          modifier =
            Modifier.then(if (!selected) Modifier.border(1.dp, LightOutlineVariant, CircleShape) else Modifier)
              .clip(CircleShape)
              .clickable { onSelectFormatPreset(fmt) },
        ) {
          Text(fmt.badgeLabel, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
        }
      }
    }
  }
}

@Composable
fun ComposeLinkModule(
  urlInput: String,
  onUrlChange: (String) -> Unit,
  parsedLink: CommunityLinkCard?,
  onParseLink: () -> Unit,
  onCloseModule: () -> Unit,
) {
  ComposeModuleShell(
    title = "网页链接",
    trailingLabel = "粘贴链接一键识别",
    onRemove = onCloseModule,
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(modifier = Modifier.weight(1f)) {
        ComposeInlineInputBox(
          value = urlInput,
          onValueChange = onUrlChange,
          placeholder = "输入 https:// 网页链接",
          testTag = "compose_link_url_input",
        )
      }
      Surface(
        shape = CircleShape,
        color = LightPrimary,
        contentColor = LightOnPrimary,
        modifier = Modifier.clip(CircleShape).clickable(onClick = onParseLink).testTag("compose_parse_link_btn"),
      ) {
        Text("识别", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp))
      }
    }

    if (parsedLink != null) {
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LightSurface)
            .border(1.dp, LightOutlineVariant, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Box(
          modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(LightPrimary),
          contentAlignment = Alignment.Center,
        ) {
          Icon(Icons.Outlined.Link, contentDescription = null, tint = LightOnPrimary, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
          Text(parsedLink.title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = LightOnSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
          Text(parsedLink.domain, style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComposeBountyModule(
  amountText: String,
  onAmountChange: (String) -> Unit,
  deadlineLabel: String,
  onCycleDeadline: () -> Unit,
  requirements: String,
  onRequirementsChange: (String) -> Unit,
  onCloseModule: () -> Unit,
) {
  ComposeModuleShell(
    title = "创作悬赏",
    trailingLabel = "公开征集支线/约稿",
    onRemove = onCloseModule,
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(shape = CircleShape, color = WarningAmber, contentColor = LightOnPrimary) {
        Text("￥", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
      }
      Box(modifier = Modifier.weight(1f)) {
        ComposeInlineInputBox(
          value = amountText,
          onValueChange = { next -> onAmountChange(next.filter { it.isDigit() }.take(5)) },
          placeholder = "输入金额",
          keyboardType = KeyboardType.Number,
          testTag = "compose_bounty_amount_input",
        )
      }
      Surface(
        shape = CircleShape,
        color = LightSurface,
        contentColor = LightOnSurface,
        modifier = Modifier.border(1.dp, LightOutlineVariant, CircleShape).clip(CircleShape).clickable(onClick = onCycleDeadline),
      ) {
        Text(deadlineLabel, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
      }
    }

    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
      listOf(30, 50, 100, 200).forEach { preset ->
        val selected = amountText == preset.toString()
        Surface(
          shape = CircleShape,
          color = if (selected) LightPrimary else LightSurface,
          contentColor = if (selected) LightOnPrimary else LightOnSurfaceVariant,
          modifier =
            Modifier.then(if (!selected) Modifier.border(1.dp, LightOutlineVariant, CircleShape) else Modifier)
              .clip(CircleShape)
              .clickable { onAmountChange(preset.toString()) },
        ) {
          Text("￥$preset", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
        }
      }
    }

    ComposeInlineInputBox(
      value = requirements,
      onValueChange = onRequirementsChange,
      placeholder = "说明字数、人设风格、交付格式与要求...",
      singleLine = false,
      testTag = "compose_bounty_req_input",
    )
  }
}

@Composable
fun ComposeMusicModule(
  track: CommunityMusicTrack?,
  isPreviewPlaying: Boolean,
  onPickAudio: () -> Unit,
  onUseSampleAudio: () -> Unit,
  onTogglePreviewPlay: () -> Unit,
  onCloseModule: () -> Unit,
) {
  ComposeModuleShell(
    title = "创作配乐 / BGM",
    trailingLabel = if (track == null) "未上传" else track.metaLabel,
    onRemove = onCloseModule,
  ) {
    if (track == null) {
      Column(
        modifier =
          Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LightSurface)
            .border(1.dp, LightOutlineVariant, RoundedCornerShape(12.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(Icons.Outlined.AudioFile, contentDescription = null, tint = LightSecondary, modifier = Modifier.size(28.dp))
        Text(
          text = "上传本地音频文件 · 支持 MP3 / WAV / FLAC / M4A",
          style = MaterialTheme.typography.bodySmall,
          color = LightOnSurfaceVariant,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Surface(
            shape = CircleShape,
            color = LightPrimary,
            contentColor = LightOnPrimary,
            modifier = Modifier.clip(CircleShape).clickable(onClick = onPickAudio).testTag("compose_pick_audio_btn"),
          ) {
            Text("上传音乐", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp))
          }
          Surface(
            shape = CircleShape,
            color = LightSurfaceContainer,
            contentColor = LightOnSurface,
            modifier = Modifier.clip(CircleShape).clickable(onClick = onUseSampleAudio).testTag("compose_sample_audio_btn"),
          ) {
            Text("试用示例BGM", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp))
          }
        }
      }
    } else {
      val transition = rememberInfiniteTransition(label = "compose_wave")
      val wavePhase by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "compose_wave_phase",
      )
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LightSurface)
            .border(1.dp, LightOutlineVariant, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Box(
          modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(LightSecondary.copy(alpha = 0.12f)),
          contentAlignment = Alignment.Center,
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(20.dp)) {
            listOf(0.4f, 0.7f, 1.0f, 0.7f, 0.4f).forEachIndexed { idx, base ->
              val f = if (!isPreviewPlaying) base else (base * if (idx % 2 == 0) wavePhase else 1.55f - wavePhase).coerceIn(0.25f, 1.0f)
              Box(modifier = Modifier.width(3.dp).fillMaxHeight(f).clip(CircleShape).background(LightSecondary))
            }
          }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(track.title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = LightOnSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
          Text("${formatSeconds(track.elapsedSec)} / ${formatSeconds(track.durationSec)}", style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
        }
        Surface(
          shape = CircleShape,
          color = LightSurfaceContainer,
          contentColor = LightOnSurface,
          modifier = Modifier.clip(CircleShape).clickable(onClick = onPickAudio),
        ) {
          Text("更换", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
        }
        Surface(
          shape = CircleShape,
          color = LightSecondary.copy(alpha = 0.15f),
          contentColor = LightOnSurface,
          modifier = Modifier.clip(CircleShape).clickable(onClick = onTogglePreviewPlay).testTag("compose_music_play_btn"),
        ) {
          Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Icon(if (isPreviewPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, contentDescription = null, tint = LightSecondary, modifier = Modifier.size(15.dp))
            Text(if (isPreviewPlaying) "暂停" else "试听", style = MaterialTheme.typography.labelSmall)
          }
        }
      }
    }
  }
}

@Composable
fun ComposeInlineInputBox(
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String,
  singleLine: Boolean = true,
  keyboardType: KeyboardType = KeyboardType.Text,
  testTag: String = "",
) {
  Box(
    modifier =
      Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(LightSurface)
        .border(1.dp, LightOutlineVariant.copy(alpha = 0.75f), RoundedCornerShape(10.dp))
        .padding(horizontal = 12.dp, vertical = 9.dp),
    contentAlignment = Alignment.CenterStart,
  ) {
    BasicTextField(
      value = value,
      onValueChange = onValueChange,
      singleLine = singleLine,
      keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
      textStyle = MaterialTheme.typography.bodySmall.copy(color = LightOnSurface),
      cursorBrush = SolidColor(LightSecondary),
      modifier = Modifier.fillMaxWidth().then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
      decorationBox = { inner ->
        if (value.isEmpty()) {
          Text(placeholder, style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
        }
        inner()
      },
    )
  }
}

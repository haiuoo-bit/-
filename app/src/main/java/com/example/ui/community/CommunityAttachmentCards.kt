package com.example.ui.community

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DocMdBadge
import com.example.ui.theme.DocMdBorder
import com.example.ui.theme.DocMdFoldBg
import com.example.ui.theme.DocMdTintBg
import com.example.ui.theme.DocTxtBadge
import com.example.ui.theme.DocTxtBorder
import com.example.ui.theme.DocTxtFoldBg
import com.example.ui.theme.DocTxtTintBg
import com.example.ui.theme.DocWordBadge
import com.example.ui.theme.DocWordBorder
import com.example.ui.theme.DocWordFoldBg
import com.example.ui.theme.DocWordTintBg
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LightOnPrimary
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightOutline
import com.example.ui.theme.LightOutlineVariant
import com.example.ui.theme.LightPrimary
import com.example.ui.theme.LightSecondary
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceContainer
import com.example.ui.theme.LightSurfaceContainerLow
import com.example.ui.theme.PdfBorderRed
import com.example.ui.theme.PdfFoldBackground
import com.example.ui.theme.PdfLinePink
import com.example.ui.theme.PdfTintBackground
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CommunityImageView(
  item: CommunityImageItem,
  contentDescription: String,
  modifier: Modifier = Modifier,
) {
  if (item.drawableRes != null) {
    Image(
      painter = painterResource(id = item.drawableRes),
      contentDescription = contentDescription,
      contentScale = ContentScale.Crop,
      modifier = modifier,
    )
    return
  }

  val context = LocalContext.current
  val decodedBitmap by
    produceState<ImageBitmap?>(initialValue = null, key1 = item.contentUri) {
      val uriStr = item.contentUri ?: return@produceState
      value =
        withContext(Dispatchers.IO) {
          runCatching {
            val uri = Uri.parse(uriStr)
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { stream ->
              BitmapFactory.decodeStream(stream, null, bounds)
            }
            var sample = 1
            while (bounds.outWidth / sample > 900 || bounds.outHeight / sample > 900) {
              sample *= 2
            }
            val opts = BitmapFactory.Options().apply { inSampleSize = sample }
            context.contentResolver.openInputStream(uri)?.use { stream ->
              BitmapFactory.decodeStream(stream, null, opts)?.asImageBitmap()
            }
          }.getOrNull()
        }
    }

  if (decodedBitmap != null) {
    Image(
      bitmap = decodedBitmap!!,
      contentDescription = contentDescription,
      contentScale = ContentScale.Crop,
      modifier = modifier,
    )
  } else {
    Box(
      modifier = modifier.background(LightSurfaceContainer),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = "图片",
        style = MaterialTheme.typography.labelSmall,
        color = LightOnSurfaceVariant,
      )
    }
  }
}

@Composable
fun CommunityImagesBlock(
  images: List<CommunityImageItem>,
  modifier: Modifier = Modifier,
) {
  if (images.isEmpty()) return
  if (images.size == 1) {
    CommunityImageView(
      item = images.first(),
      contentDescription = "动态配图",
      modifier =
        modifier
          .fillMaxWidth()
          .height(176.dp)
          .clip(RoundedCornerShape(14.dp))
          .border(1.dp, LightOutlineVariant.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
    )
  } else {
    Column(
      modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)),
      verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      images.chunked(3).forEach { rowItems ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          rowItems.forEachIndexed { idx, img ->
            CommunityImageView(
              item = img,
              contentDescription = "配图 ${idx + 1}",
              modifier =
                Modifier.weight(1f)
                  .aspectRatio(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(LightSurfaceContainer),
            )
          }
          repeat(3 - rowItems.size) {
            Spacer(Modifier.weight(1f))
          }
        }
      }
    }
  }
}

@Composable
fun CommunityDocBadgeIcon(
  format: CommunityDocFormat,
  modifier: Modifier = Modifier,
) {
  val palette =
    when (format) {
      CommunityDocFormat.PDF -> listOf(PdfTintBackground, PdfFoldBackground, PdfBorderRed, ErrorRed)
      CommunityDocFormat.DOC -> listOf(DocWordTintBg, DocWordFoldBg, DocWordBorder, DocWordBadge)
      CommunityDocFormat.TXT -> listOf(DocTxtTintBg, DocTxtFoldBg, DocTxtBorder, DocTxtBadge)
      CommunityDocFormat.MD -> listOf(DocMdTintBg, DocMdFoldBg, DocMdBorder, DocMdBadge)
    }
  val docBody = remember {
    PathParser()
      .parsePathString(
        "M9 10C9 6.69 11.69 4 15 4H27.2C28.53 4 29.8 4.53 30.74 5.46L37.54 12.26C38.47 13.2 39 14.47 39 15.8V38C39 41.31 36.31 44 33 44H15C11.69 44 9 41.31 9 38V10Z"
      )
      .toPath()
  }
  val docFold = remember {
    PathParser().parsePathString("M28 4.2V11C28 12.66 29.34 14 31 14H38.8").toPath()
  }
  val lines = remember {
    PathParser().parsePathString("M15.5 19H24.5M15.5 23H21.5").toPath()
  }
  val textMeasurer = rememberTextMeasurer()
  val labelStyle = TextStyle(color = Color.White, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)

  Canvas(modifier = modifier.size(40.dp)) {
    withTransform({
      scale(scaleX = size.width / 48f, scaleY = size.height / 48f, pivot = Offset.Zero)
    }) {
      drawPath(docBody, color = palette[0], style = Fill)
      drawPath(docBody, color = palette[2], style = Stroke(width = 1.6f))
      drawPath(docFold, color = palette[1], style = Fill)
      drawPath(docFold, color = palette[2], style = Stroke(width = 1.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
      drawPath(lines, color = PdfLinePink.copy(alpha = 0.7f), style = Stroke(width = 2f, cap = StrokeCap.Round))
      drawRoundRect(
        color = palette[3],
        topLeft = Offset(12f, 27.5f),
        size = Size(24f, 12f),
        cornerRadius = CornerRadius(6f, 6f),
      )
      val measured = textMeasurer.measure(format.badgeLabel, style = labelStyle)
      drawText(
        textLayoutResult = measured,
        topLeft = Offset(24f - measured.size.width / 2f, 33.5f - measured.size.height / 2f),
      )
    }
  }
}

@Composable
fun CommunityPollCard(
  postId: String,
  poll: CommunityPoll,
  onSelectOption: (String) -> Unit,
  onConfirmOrReset: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val total = poll.totalVotes.coerceAtLeast(1)
  Column(
    modifier =
      modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(LightSurfaceContainerLow)
        .border(1.dp, LightOutlineVariant.copy(alpha = 0.85f), RoundedCornerShape(14.dp))
        .padding(12.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = poll.title,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
        color = LightOnSurface,
        modifier = Modifier.weight(1f),
      )
      Text(
        text = "${poll.modeLabel} · ${poll.deadlineLabel}",
        style = MaterialTheme.typography.bodySmall,
        color = LightOnSurfaceVariant,
      )
    }

    poll.options.forEach { option ->
      val isPendingSelected = option.id in poll.pendingSelectedIds
      val isMineConfirmed = option.id in poll.confirmedSelectedIds
      val pct = if (poll.hasVoted) ((option.votes.toFloat() / total.toFloat()) * 100f).toInt().coerceIn(0, 100) else 0
      val animatedBar by animateFloatAsState(
        targetValue = if (poll.hasVoted) pct / 100f else 0f,
        animationSpec = tween(durationMillis = 280),
        label = "poll_bar_${option.id}",
      )

      Box(
        modifier =
          Modifier.fillMaxWidth()
            .heightIn(min = 42.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(LightSurface)
            .border(
              width = 1.dp,
              color = if (isPendingSelected || isMineConfirmed) LightPrimary else LightOutlineVariant,
              shape = RoundedCornerShape(10.dp),
            )
            .clickable(enabled = !poll.hasVoted) { onSelectOption(option.id) }
            .testTag("poll_option_${postId}_${option.id}"),
      ) {
        if (poll.hasVoted && animatedBar > 0f) {
          Box(
            modifier =
              Modifier.fillMaxWidth(animatedBar)
                .matchParentSize()
                .background(
                  if (isMineConfirmed) LightSecondary.copy(alpha = 0.14f) else LightSurfaceContainer
                )
          )
        }
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            if (!poll.hasVoted) {
              Box(
                modifier =
                  Modifier.size(16.dp)
                    .clip(CircleShape)
                    .border(
                      1.5.dp,
                      if (isPendingSelected) LightPrimary else LightOutline,
                      CircleShape,
                    ),
                contentAlignment = Alignment.Center,
              ) {
                if (isPendingSelected) {
                  Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(LightPrimary))
                }
              }
            }
            Text(
              text = option.label,
              style = MaterialTheme.typography.bodySmall,
              color = LightOnSurface,
            )
          }
          if (poll.hasVoted) {
            Text(
              text = "$pct%",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = if (isMineConfirmed) LightSecondary else LightOnSurfaceVariant,
            )
          }
        }
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text =
          if (poll.hasVoted) {
            "已投票 · 共 ${poll.totalVotes} 人参与"
          } else {
            "共 ${poll.totalVotes} 人参与 · 请选择后投票"
          },
        style = MaterialTheme.typography.bodySmall,
        color = LightOnSurfaceVariant,
      )
      Surface(
        shape = CircleShape,
        color = if (poll.hasVoted) LightSurface else LightPrimary,
        contentColor = if (poll.hasVoted) LightOnSurfaceVariant else LightOnPrimary,
        modifier =
          Modifier.heightIn(min = 34.dp)
            .then(
              if (poll.hasVoted) Modifier.border(1.dp, LightOutlineVariant, CircleShape) else Modifier
            )
            .clip(CircleShape)
            .clickable(onClick = onConfirmOrReset)
            .testTag("poll_submit_${postId}"),
      ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)) {
          Text(
            text =
              stringResource(
                if (poll.hasVoted) R.string.comm_poll_revote else R.string.comm_poll_confirm
              ),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          )
        }
      }
    }
  }
}

@Composable
fun CommunityDocumentCard(
  document: CommunityDocument,
  buttonLabel: String,
  onViewClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier =
      modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(LightSurfaceContainerLow)
        .border(1.dp, LightOutlineVariant.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
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
        text = "${document.sizeLabel} · ${document.metaNote}",
        style = MaterialTheme.typography.bodySmall,
        color = LightOnSurfaceVariant,
      )
    }
    Surface(
      shape = CircleShape,
      color = LightSurface,
      contentColor = LightOnSurface,
      modifier =
        Modifier.border(1.dp, LightOutlineVariant, CircleShape)
          .clip(CircleShape)
          .clickable(onClick = onViewClick)
          .testTag("view_doc_btn"),
    ) {
      Text(
        text = buttonLabel,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
      )
    }
  }
}

@Composable
fun CommunityLinkCardView(
  linkCard: CommunityLinkCard,
  onToast: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  Row(
    modifier =
      modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(LightSurfaceContainerLow)
        .border(1.dp, LightOutlineVariant.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
        .clickable {
          val opened =
            runCatching {
              context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(linkCard.url)))
            }.isSuccess
          if (!opened) {
            onToast("已解析网页：${linkCard.domain}")
          }
        }
        .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Box(
      modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(LightPrimary),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        imageVector = Icons.Outlined.Link,
        contentDescription = linkCard.domain,
        tint = LightOnPrimary,
        modifier = Modifier.size(22.dp),
      )
    }
    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(
        text = linkCard.title,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
        color = LightOnSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = "${linkCard.domain} · 点击打开外部网页",
        style = MaterialTheme.typography.bodySmall,
        color = LightOnSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
    Icon(
      imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
      contentDescription = null,
      tint = LightOnSurfaceVariant,
      modifier = Modifier.size(18.dp),
    )
  }
}

@Composable
fun CommunityBountyCardView(
  postId: String,
  bounty: CommunityBountyCard,
  onApplyClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier =
      modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(LightSurfaceContainerLow)
        .border(1.dp, LightOutlineVariant.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
        .padding(12.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = WarningAmber, contentColor = LightOnPrimary) {
          Text(
            text = "悬赏 ￥${bounty.amountYuan}",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
          )
        }
        Surface(
          shape = CircleShape,
          color = LightSurface,
          modifier = Modifier.border(1.dp, LightOutlineVariant, CircleShape),
        ) {
          Text(
            text = bounty.deadlineLabel,
            style = MaterialTheme.typography.labelSmall,
            color = LightOnSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
          )
        }
      }
      Text(
        text = bounty.statusLabel,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
        color = SuccessGreen,
      )
    }

    Text(
      text = bounty.requirements,
      style = MaterialTheme.typography.bodySmall,
      color = LightOnSurface,
    )

    Row(
      modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = "已有 ${bounty.applicantCount} 位创作者应征",
        style = MaterialTheme.typography.bodySmall,
        color = LightOnSurfaceVariant,
      )
      Surface(
        shape = CircleShape,
        color = if (bounty.hasApplied) LightSurfaceContainer else LightPrimary,
        contentColor = if (bounty.hasApplied) LightOnSurfaceVariant else LightOnPrimary,
        modifier =
          Modifier.clip(CircleShape)
            .clickable(onClick = onApplyClick)
            .testTag("bounty_apply_${postId}"),
      ) {
        Text(
          text = if (bounty.hasApplied) "已应征 ✓" else "接单应征",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
        )
      }
    }
  }
}

@Composable
fun CommunityQuoteCardView(
  quote: CommunityQuoteCard,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier =
      modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(LightSurfaceContainerLow)
        .border(1.dp, LightOutlineVariant, RoundedCornerShape(14.dp))
        .clickable(onClick = onClick)
        .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    if (quote.thumbRes != null) {
      Image(
        painter = painterResource(id = quote.thumbRes),
        contentDescription = quote.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
      )
    }
    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(
        text = quote.eyebrow,
        style = MaterialTheme.typography.labelSmall,
        color = LightOnSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = quote.title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = LightOnSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = quote.subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = LightOnSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
    if (quote.ctaLabel != null) {
      Surface(shape = CircleShape, color = LightPrimary, contentColor = LightOnPrimary) {
        Text(
          text = quote.ctaLabel,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
      }
    } else {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = LightOnSurfaceVariant,
        modifier = Modifier.size(18.dp),
      )
    }
  }
}

@Composable
fun MistyBlueMusicPlayerCard(
  track: CommunityMusicTrack,
  isPlaying: Boolean,
  onTogglePlay: () -> Unit,
  onSeek: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  val infiniteTransition = rememberInfiniteTransition(label = "music_wave")
  val wavePhase by infiniteTransition.animateFloat(
    initialValue = 0.45f,
    targetValue = 1.15f,
    animationSpec =
      infiniteRepeatable(
        animation = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse,
      ),
    label = "wave_scale",
  )

  Column(
    modifier =
      modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(LightSurface)
        .border(1.dp, LightOutlineVariant.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
        .padding(12.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Box(
        modifier =
          Modifier.size(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(LightSecondary.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center,
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(3.dp),
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.height(26.dp),
        ) {
          val baseHeights = listOf(0.32f, 0.65f, 1.0f, 0.65f, 0.32f)
          baseHeights.forEachIndexed { idx, base ->
            val factor =
              if (!isPlaying) base
              else {
                val offsetWave = if (idx % 2 == 0) wavePhase else (1.6f - wavePhase)
                (base * offsetWave).coerceIn(0.25f, 1.0f)
              }
            Box(
              modifier =
                Modifier.width(3.dp)
                  .fillMaxHeight(factor)
                  .clip(CircleShape)
                  .background(LightSecondary.copy(alpha = if (idx == 2) 0.85f else 0.6f))
            )
          }
        }
      }

      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = track.title,
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
          color = LightOnSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = track.metaLabel,
          style = MaterialTheme.typography.bodySmall,
          color = LightOnSurfaceVariant,
        )
        Text(
          text = "${formatSeconds(track.elapsedSec)} / ${formatSeconds(track.durationSec)} · ${if (isPlaying) "正在播放" else "点击播放"}",
          style = MaterialTheme.typography.bodySmall,
          color = LightOnSurfaceVariant,
        )
      }

      Surface(
        shape = CircleShape,
        color = LightSecondary.copy(alpha = 0.15f),
        contentColor = LightOnSurface,
        modifier =
          Modifier.clip(CircleShape)
            .clickable(onClick = onTogglePlay)
            .testTag("music_play_toggle_btn"),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Icon(
            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            contentDescription = if (isPlaying) "暂停音乐" else "播放音乐",
            tint = LightSecondary,
            modifier = Modifier.size(16.dp),
          )
          Text(
            text = if (isPlaying) "暂停" else "播放",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          )
        }
      }
    }

    Slider(
      value = track.elapsedSec.toFloat(),
      onValueChange = { onSeek(it.toInt()) },
      valueRange = 0f..track.durationSec.coerceAtLeast(1).toFloat(),
      colors =
        SliderDefaults.colors(
          thumbColor = LightSecondary.copy(alpha = 0.85f),
          activeTrackColor = LightSecondary.copy(alpha = 0.55f),
          inactiveTrackColor = LightSecondary.copy(alpha = 0.15f),
        ),
      modifier = Modifier.fillMaxWidth().height(26.dp).testTag("music_seek_slider"),
    )
  }
}

fun formatSeconds(totalSeconds: Int): String {
  val safe = totalSeconds.coerceAtLeast(0)
  val m = safe / 60
  val s = safe % 60
  return "%02d:%02d".format(m, s)
}

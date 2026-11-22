package com.example.ui.plaza

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.home.BlueVBadge
import com.example.ui.theme.AmberTintBg
import com.example.ui.theme.AmberTintDark
import com.example.ui.theme.GoldMedal
import com.example.ui.theme.IndigoDeep
import com.example.ui.theme.IndigoSoft
import com.example.ui.theme.IndigoTintBg
import com.example.ui.theme.LightOnPrimary
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightOutlineVariant
import com.example.ui.theme.LightPrimary
import com.example.ui.theme.LightSecondary
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceContainer
import com.example.ui.theme.LightSurfaceContainerLow
import com.example.ui.theme.RedVDeep
import com.example.ui.theme.RedVPrimary
import com.example.ui.theme.RedVSky
import com.example.ui.theme.RoseBadge
import com.example.ui.theme.RoseTintBg
import com.example.ui.theme.SkyTintBg
import com.example.ui.theme.SkyTintDark
import com.example.ui.theme.VioletBadge
import com.example.ui.theme.VioletDeep
import com.example.ui.theme.VioletTintBg
import com.example.ui.theme.WarningAmber

@Composable
fun CreatorVBadge(
  tier: VBadgeTier,
  size: Dp = 15.dp,
  modifier: Modifier = Modifier,
) {
  when (tier) {
    VBadgeTier.NONE -> Unit
    VBadgeTier.BLUE -> BlueVBadge(modifier = modifier, size = size)
    VBadgeTier.VIOLET -> ScallopedVBadge(
      topColor = IndigoSoft,
      midColor = VioletBadge,
      bottomColor = VioletDeep,
      size = size,
      modifier = modifier,
    )
    VBadgeTier.RED -> ScallopedVBadge(
      topColor = RedVSky,
      midColor = RedVPrimary,
      bottomColor = RedVDeep,
      size = size,
      modifier = modifier,
    )
  }
}

@Composable
private fun ScallopedVBadge(
  topColor: Color,
  midColor: Color,
  bottomColor: Color,
  size: Dp,
  modifier: Modifier = Modifier,
) {
  val scallopedPath = remember {
    PathParser()
      .parsePathString(
        "M20 3.2C22.4 3.2 23.8 5.2 26 5.8C28.2 6.4 30.4 5.4 32.1 7.1C33.8 8.8 32.8 11 33.4 13.2C34 15.4 36.8 16.8 36.8 19.2C36.8 21.6 34.4 23 33.8 25.2C33.2 27.4 34.2 29.8 32.5 31.5C30.8 33.2 28.4 32.2 26.2 32.8C24 33.4 22.4 36.8 20 36.8C17.6 36.8 16 33.4 13.8 32.8C11.6 32.2 9.2 33.2 7.5 31.5C5.8 29.8 6.8 27.4 6.2 25.2C5.6 23 3.2 21.6 3.2 19.2C3.2 16.8 6 15.4 6.6 13.2C7.2 11 6.2 8.8 7.9 7.1C9.6 5.4 11.8 6.4 14 5.8C16.2 5.2 17.6 3.2 20 3.2Z"
      )
      .toPath()
  }
  val vPath = remember {
    PathParser().parsePathString("M13.8 14.2L20 26.8L26.2 14.2").toPath()
  }
  Canvas(modifier = modifier.size(size)) {
    withTransform({
      scale(scaleX = this@Canvas.size.width / 40f, scaleY = this@Canvas.size.height / 40f, pivot = Offset.Zero)
    }) {
      drawPath(
        path = scallopedPath,
        brush = Brush.linearGradient(listOf(topColor, midColor, bottomColor), Offset(6f, 4f), Offset(34f, 36f)),
        style = Fill,
      )
      drawPath(
        path = vPath,
        color = Color.White,
        style = Stroke(width = 3.6f, cap = StrokeCap.Round, join = StrokeJoin.Round),
      )
    }
  }
}

@Composable
fun PlazaRankingsTileIcon(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(AmberTintBg),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.size(26.dp)) {
      withViewport36 {
        val cupPath = PathParser().parsePathString("M11 8H25V16C25 19.87 21.87 23 18 23C14.13 23 11 19.87 11 16V8Z").toPath()
        drawPath(
          path = cupPath,
          brush = Brush.verticalGradient(listOf(GoldMedal, WarningAmber)),
          style = Fill,
        )
        val handles = PathParser().parsePathString("M11 10H8C7.4 10 7 10.5 7 11.2V13.5C7 15.2 8.4 16.5 10.2 16.5H11M25 10H28C28.6 10 29 10.5 29 11.2V13.5C29 15.2 27.6 16.5 25.8 16.5H25").toPath()
        drawPath(handles, color = WarningAmber, style = Stroke(width = 2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(WarningAmber, Offset(18f, 23f), Offset(18f, 28f), strokeWidth = 2.4f, cap = StrokeCap.Round)
        drawLine(WarningAmber, Offset(12.5f, 28f), Offset(23.5f, 28f), strokeWidth = 2.6f, cap = StrokeCap.Round)
      }
    }
  }
}

@Composable
fun PlazaBountyTileIcon(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(SkyTintBg),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.size(26.dp)) {
      withViewport36 {
        drawRoundRect(
          brush = Brush.verticalGradient(listOf(LightSecondary, IndigoDeep)),
          topLeft = Offset(9f, 8f),
          size = Size(18f, 22f),
          cornerRadius = CornerRadius(4f, 4f),
        )
        drawRoundRect(
          color = Color.White,
          topLeft = Offset(13.5f, 5.5f),
          size = Size(9f, 4.5f),
          cornerRadius = CornerRadius(2f, 2f),
        )
        drawLine(Color.White, Offset(13f, 15.5f), Offset(23f, 15.5f), strokeWidth = 2f, cap = StrokeCap.Round)
        drawLine(Color.White, Offset(13f, 20.5f), Offset(21f, 20.5f), strokeWidth = 2f, cap = StrokeCap.Round)
        drawLine(Color.White, Offset(13f, 25f), Offset(18f, 25f), strokeWidth = 2f, cap = StrokeCap.Round)
      }
    }
  }
}

@Composable
fun PlazaScriptsTileIcon(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(VioletTintBg),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.size(26.dp)) {
      withViewport36 {
        val body = PathParser().parsePathString("M7 16C7 12.69 9.69 10 13 10H23C26.31 10 29 12.69 29 16L30 23.5C30.2 25.5 28.6 27 26.8 27C25.6 27 24.5 26.3 24 25.2L22.6 22.5H13.4L12 25.2C11.5 26.3 10.4 27 9.2 27C7.4 27 5.8 25.5 6 23.5L7 16Z").toPath()
        drawPath(
          path = body,
          brush = Brush.verticalGradient(listOf(VioletBadge, VioletDeep)),
          style = Fill,
        )
        drawLine(Color.White, Offset(11.5f, 16f), Offset(15.5f, 16f), strokeWidth = 2f, cap = StrokeCap.Round)
        drawLine(Color.White, Offset(13.5f, 14f), Offset(13.5f, 18f), strokeWidth = 2f, cap = StrokeCap.Round)
        drawCircle(Color.White, radius = 1.3f, center = Offset(22.5f, 14.5f))
        drawCircle(Color.White, radius = 1.3f, center = Offset(24.5f, 17.5f))
      }
    }
  }
}

@Composable
fun PlazaRelayTileIcon(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(RoseTintBg),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.size(26.dp)) {
      withViewport36 {
        drawRoundRect(
          brush = Brush.verticalGradient(listOf(RoseBadge, RedVDeep)),
          topLeft = Offset(7f, 8f),
          size = Size(22f, 8f),
          cornerRadius = CornerRadius(3f, 3f),
        )
        drawRoundRect(
          brush = Brush.verticalGradient(listOf(RoseBadge, RedVDeep)),
          topLeft = Offset(7f, 19.5f),
          size = Size(22f, 8f),
          cornerRadius = CornerRadius(3f, 3f),
        )
        drawCircle(Color.White, radius = 1.4f, center = Offset(11.5f, 12f))
        drawCircle(Color.White, radius = 1.4f, center = Offset(11.5f, 23.5f))
        val bolt = Path().apply {
          moveTo(22.5f, 13f)
          lineTo(19f, 19.5f)
          lineTo(22f, 19.5f)
          lineTo(20.2f, 26f)
          lineTo(25.2f, 18f)
          lineTo(22.2f, 18f)
          close()
        }
        drawPath(bolt, color = GoldMedal, style = Fill)
      }
    }
  }
}

@Composable
fun PlazaSubScreenHeader(
  title: String,
  onBack: () -> Unit,
  backTestTag: String,
  modifier: Modifier = Modifier,
  trailingContent: @Composable () -> Unit = {},
) {
  Surface(
    color = LightSurface,
    modifier = modifier.fillMaxWidth(),
  ) {
    Column {
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .statusBarsPadding()
            .height(54.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          IconButton(
            onClick = onBack,
            modifier = Modifier.size(48.dp).testTag(backTestTag),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.cd_back_home),
              tint = LightOnSurface,
            )
          }
          Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = LightOnSurface,
          )
        }
        trailingContent()
      }
      Box(
        modifier = Modifier.fillMaxWidth().height(1.dp).background(LightOutlineVariant.copy(alpha = 0.7f))
      )
    }
  }
}

@Composable
fun PlazaNoticeDialog(
  onDismiss: () -> Unit,
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = LightSurface,
      shadowElevation = 8.dp,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Box(
              modifier = Modifier.size(36.dp).clip(CircleShape).background(IndigoTintBg),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Outlined.Campaign,
                contentDescription = null,
                tint = IndigoDeep,
                modifier = Modifier.size(20.dp),
              )
            }
            Column {
              Text(
                text = stringResource(R.string.plaza_notice_title),
                style = MaterialTheme.typography.titleMedium,
                color = LightOnSurface,
              )
              Text(
                text = stringResource(R.string.plaza_notice_subtitle),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = LightOnSurfaceVariant,
              )
            }
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close), tint = LightOnSurfaceVariant)
          }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          PlazaSeedData.plazaNotices.forEach { notice ->
            Column(
              modifier =
                Modifier.fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .background(LightSurfaceContainerLow)
                  .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                  .padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Surface(
                  shape = CircleShape,
                  color = if (notice.isBlueTag) SkyTintBg else VioletTintBg,
                ) {
                  Text(
                    text = notice.tag,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = if (notice.isBlueTag) SkyTintDark else VioletDeep,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                  )
                }
                Text(
                  text = notice.title,
                  style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                  color = LightOnSurface,
                )
              }
              Text(
                text = notice.body,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = LightOnSurfaceVariant,
              )
            }
          }
        }

        Surface(
          shape = CircleShape,
          color = LightPrimary,
          contentColor = LightOnPrimary,
          modifier =
            Modifier.fillMaxWidth()
              .height(44.dp)
              .clip(CircleShape)
              .clickable(onClick = onDismiss)
              .testTag("plaza_notice_confirm_btn"),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = stringResource(R.string.modal_known),
              style = MaterialTheme.typography.labelLarge,
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlazaCategoryFilterSheet(
  activeKind: String,
  onSelectKind: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  val kinds = listOf("all" to "全部活动", "榜单" to "榜单排行", "定制" to "定制约稿", "悬赏" to "悬赏征集", "剧本局" to "多人剧本局")
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = LightSurface,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Text(
        text = stringResource(R.string.plaza_filter_category),
        style = MaterialTheme.typography.titleMedium,
        color = LightOnSurface,
      )
      kinds.forEach { (key, label) ->
        val selected = activeKind == key
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = if (selected) LightPrimary else LightSurfaceContainer,
          contentColor = if (selected) LightOnPrimary else LightOnSurface,
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .clickable { onSelectKind(key) },
        ) {
          Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
          )
        }
      }
    }
  }
}

private inline fun DrawScope.withViewport36(block: DrawScope.() -> Unit) {
  withTransform({
    scale(scaleX = size.width / 36f, scaleY = size.height / 36f, pivot = Offset.Zero)
  }) {
    block()
  }
}

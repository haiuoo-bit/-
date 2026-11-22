package com.example.ui.update

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Downloading
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.home.UpdateModalPhase
import com.example.ui.theme.LightOnPrimary
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightOutlineVariant
import com.example.ui.theme.LightPrimary
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceContainer
import com.example.ui.theme.LightSurfaceContainerHigh
import com.example.ui.theme.LightSurfaceContainerLow
import java.util.Locale

@Composable
fun VersionUpdateDialog(
  phase: UpdateModalPhase,
  progressPct: Int,
  isReadyToInstall: Boolean,
  onStartDownload: () -> Unit,
  onReplayProgressDemo: () -> Unit,
  onMinimizeToBackground: () -> Unit,
  onLater: () -> Unit,
  onInstallNow: () -> Unit,
) {
  if (phase == UpdateModalPhase.HIDDEN) return

  Dialog(
    onDismissRequest = {
      if (phase == UpdateModalPhase.DOWNLOADING) onMinimizeToBackground() else onLater()
    },
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = LightSurface,
      contentColor = LightOnSurface,
      shadowElevation = 18.dp,
      modifier = Modifier.width(304.dp).testTag("version_update_dialog"),
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Header banner with scrim, close X, version pills, and title
        Box(
          modifier = Modifier.fillMaxWidth().height(112.dp).background(LightPrimary),
        ) {
          Image(
            painter = painterResource(id = R.drawable.img_update_banner),
            contentDescription = stringResource(R.string.update_title),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
          )
          Box(
            modifier =
              Modifier.fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    colors =
                      listOf(
                        Color.Black.copy(alpha = 0.12f),
                        Color.Black.copy(alpha = 0.42f),
                        Color.Black.copy(alpha = 0.82f),
                      )
                  )
                )
          )

          IconButton(
            onClick = {
              if (phase == UpdateModalPhase.DOWNLOADING) onMinimizeToBackground() else onLater()
            },
            modifier =
              Modifier.align(Alignment.TopEnd)
                .padding(8.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(LightPrimary.copy(alpha = 0.6f))
                .testTag("update_close_btn"),
          ) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = stringResource(R.string.cd_close),
              tint = LightOnPrimary,
              modifier = Modifier.size(16.dp),
            )
          }

          Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
          ) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Surface(
                shape = CircleShape,
                color = LightSurface,
                contentColor = LightOnSurface,
              ) {
                Text(
                  text = stringResource(R.string.update_version_tag),
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                )
              }
              Surface(
                shape = CircleShape,
                color = LightPrimary.copy(alpha = 0.85f),
                contentColor = LightOnPrimary,
                modifier = Modifier.border(1.dp, LightOnPrimary.copy(alpha = 0.25f), CircleShape),
              ) {
                Text(
                  text = stringResource(R.string.update_new_badge),
                  style = MaterialTheme.typography.labelSmall,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                )
              }
            }

            Text(
              text = stringResource(R.string.update_title),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = LightOnPrimary,
            )
          }
        }

        // Body content
        Column(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          // Package size & Wi-Fi recommendation
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(
                imageVector = Icons.Outlined.Inventory2,
                contentDescription = null,
                tint = LightOnSurface,
                modifier = Modifier.size(15.dp),
              )
              val downloadedMb = String.format(Locale.US, "%.1f", 48.6f * progressPct / 100f)
              Text(
                text =
                  if (phase == UpdateModalPhase.DOWNLOADING) {
                    stringResource(R.string.update_size_progress_format, downloadedMb)
                  } else {
                    stringResource(R.string.update_size_idle)
                  },
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = LightOnSurfaceVariant,
              )
            }
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(
                imageVector = Icons.Outlined.Wifi,
                contentDescription = null,
                tint = LightOnSurface,
                modifier = Modifier.size(15.dp),
              )
              Text(
                text = stringResource(R.string.update_wifi_tip),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = LightOnSurfaceVariant,
              )
            }
          }

          HorizontalDivider(color = LightOutlineVariant, thickness = 1.dp)

          // Release notes box
          Column(
            modifier =
              Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(LightSurfaceContainerLow)
                .border(1.dp, LightOutlineVariant, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Text(
              text = stringResource(R.string.update_notes_label),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
              color = LightOnSurface,
            )
            Text(
              text = stringResource(R.string.update_notes_body),
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp, lineHeight = 19.sp),
              color = LightOnSurfaceVariant,
            )
          }

          if (phase == UpdateModalPhase.PROMPT) {
            Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Surface(
                shape = CircleShape,
                color = LightPrimary,
                contentColor = LightOnPrimary,
                shadowElevation = 2.dp,
                modifier =
                  Modifier.fillMaxWidth()
                    .height(42.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onStartDownload)
                    .testTag("update_download_install_btn"),
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center,
                  modifier = Modifier.fillMaxSize(),
                ) {
                  Icon(
                    imageVector = Icons.Outlined.Download,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp),
                  )
                  Box(Modifier.width(6.dp))
                  Text(
                    text = stringResource(R.string.update_btn_download_install),
                    style = MaterialTheme.typography.labelLarge,
                  )
                }
              }

              Surface(
                shape = CircleShape,
                color = LightSurfaceContainer,
                contentColor = LightOnSurface,
                modifier =
                  Modifier.fillMaxWidth()
                    .height(42.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onLater)
                    .testTag("update_later_btn"),
              ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                  Text(
                    text = stringResource(R.string.update_btn_later),
                    style = MaterialTheme.typography.labelLarge,
                  )
                }
              }
            }
          } else {
            // 26 下载中状态
            Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Column(
                modifier =
                  Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onReplayProgressDemo)
                    .padding(vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                  ) {
                    Box(
                      modifier = Modifier.size(28.dp),
                      contentAlignment = Alignment.Center,
                    ) {
                      val clamped = progressPct.coerceIn(0, 100)
                      Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokePx = 2.5.dp.toPx()
                        drawCircle(
                          color = LightSurfaceContainerHigh,
                          style = Stroke(width = strokePx),
                        )
                        drawArc(
                          color = LightPrimary,
                          startAngle = -90f,
                          sweepAngle = 360f * (clamped / 100f),
                          useCenter = false,
                          style = Stroke(width = strokePx, cap = StrokeCap.Round),
                        )
                      }
                      Icon(
                        imageVector =
                          if (isReadyToInstall) Icons.Filled.Check else Icons.Filled.ArrowDownward,
                        contentDescription = null,
                        tint = LightOnSurface,
                        modifier = Modifier.size(14.dp),
                      )
                    }

                    Text(
                      text =
                        if (isReadyToInstall) {
                          stringResource(R.string.update_status_ready)
                        } else {
                          stringResource(R.string.update_status_downloading)
                        },
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                      color = LightOnSurface,
                    )
                  }

                  Text(
                    text = "${progressPct.coerceIn(0, 100)}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = LightOnSurface,
                    modifier = Modifier.testTag("update_percent_label"),
                  )
                }

                // Linear progress track
                Box(
                  modifier =
                    Modifier.fillMaxWidth()
                      .height(8.dp)
                      .clip(CircleShape)
                      .background(LightSurfaceContainer)
                ) {
                  Box(
                    modifier =
                      Modifier.fillMaxWidth((progressPct.coerceIn(1, 100) / 100f))
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(LightPrimary)
                  )
                }
              }

              Surface(
                shape = CircleShape,
                color = if (isReadyToInstall) LightPrimary else LightSurfaceContainer,
                contentColor = if (isReadyToInstall) LightOnPrimary else LightOnSurface,
                shadowElevation = if (isReadyToInstall) 2.dp else 0.dp,
                modifier =
                  Modifier.fillMaxWidth()
                    .height(44.dp)
                    .clip(CircleShape)
                    .clickable {
                      if (isReadyToInstall) onInstallNow() else onMinimizeToBackground()
                    }
                    .testTag("update_action_bg_or_install_btn"),
              ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                  Text(
                    text =
                      if (isReadyToInstall) {
                        stringResource(R.string.update_btn_install_now)
                      } else {
                        stringResource(R.string.update_btn_background)
                      },
                    style = MaterialTheme.typography.labelLarge,
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun FloatingUpdatePill(
  visible: Boolean,
  progressPct: Int,
  isReady: Boolean,
  onReopenModal: () -> Unit,
  modifier: Modifier = Modifier,
) {
  AnimatedVisibility(
    visible = visible,
    enter = fadeIn() + scaleIn(initialScale = 0.9f),
    exit = fadeOut() + scaleOut(targetScale = 0.9f),
    modifier = modifier,
  ) {
    Surface(
      shape = CircleShape,
      color = LightPrimary,
      contentColor = LightOnPrimary,
      shadowElevation = 8.dp,
      modifier =
        Modifier.height(42.dp)
          .clip(CircleShape)
          .clickable(onClick = onReopenModal)
          .testTag("floating_update_pill"),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp),
      ) {
        Icon(
          imageVector = if (isReady) Icons.Filled.Check else Icons.Outlined.Downloading,
          contentDescription = null,
          modifier = Modifier.size(18.dp),
        )
        Text(
          text =
            if (isReady) {
              stringResource(R.string.update_pill_ready)
            } else {
              stringResource(R.string.update_pill_downloading_format, progressPct.coerceIn(1, 100))
            },
          style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
        )
      }
    }
  }
}

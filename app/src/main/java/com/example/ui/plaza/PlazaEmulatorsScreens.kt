package com.example.ui.plaza

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.home.OverlayToastPill
import com.example.ui.theme.DarkHeroBg
import com.example.ui.theme.DarkOnSurface
import com.example.ui.theme.LightOnPrimary
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightOutlineVariant
import com.example.ui.theme.LightPrimary
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceContainer
import com.example.ui.theme.LightSurfaceContainerHigh
import com.example.ui.theme.LightSurfaceContainerLow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlazaEmulatorsScreen(
  uiState: PlazaExpansionUiState,
  onBack: () -> Unit,
  onSelectCategory: (EmulatorCategory) -> Unit,
  onOpenDetail: (String) -> Unit,
  onPurchaseEmulator: (String) -> Unit,
  onDownloadEmulator: (String) -> Unit,
  onOpenGuide: () -> Unit,
  onDismissGuide: () -> Unit,
  onShowToast: (String) -> Unit,
) {
  @Suppress("DEPRECATION")
  val clipboardManager = LocalClipboardManager.current

  Scaffold(
    containerColor = LightSurfaceContainerLow,
    topBar = {
      Column {
        PlazaSubScreenHeader(
          title = stringResource(R.string.emu_title),
          onBack = onBack,
          backTestTag = "emu_back_btn",
          trailingContent = {
            IconButton(
              onClick = onOpenGuide,
              modifier = Modifier.size(48.dp).testTag("emu_help_btn"),
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                contentDescription = stringResource(R.string.emu_guide_title),
                tint = LightOnSurfaceVariant,
              )
            }
          },
        )
        Surface(color = LightSurface, modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier =
              Modifier.fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            EmulatorCategory.entries.forEach { cat ->
              val selected = uiState.emulatorCategory == cat
              Surface(
                shape = CircleShape,
                color = if (selected) LightPrimary else LightSurface,
                contentColor = if (selected) LightOnPrimary else LightOnSurfaceVariant,
                modifier =
                  Modifier.border(
                    1.dp,
                    if (selected) LightPrimary else LightOutlineVariant,
                    CircleShape,
                  )
                    .clip(CircleShape)
                    .clickable { onSelectCategory(cat) }
                    .testTag("emu_cat_${cat.name}"),
              ) {
                Text(
                  text = cat.label,
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                )
              }
            }
          }
        }
      }
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding).navigationBarsPadding(),
      contentAlignment = Alignment.TopCenter,
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize().widthIn(max = 600.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        items(uiState.filteredEmulators, key = { it.id }) { item ->
          Surface(
            shape = RoundedCornerShape(24.dp),
            color = LightSurface,
            shadowElevation = 1.dp,
            modifier =
              Modifier.fillMaxWidth()
                .border(1.dp, LightOutlineVariant, RoundedCornerShape(24.dp))
                .testTag("emu_card_${item.id}"),
          ) {
            Column {
              // Hero preview thumbnail (clickable -> 55 Emulator Detail)
              Box(
                modifier =
                  Modifier.fillMaxWidth()
                    .height(176.dp)
                    .background(LightSurfaceContainer)
                    .clickable { onOpenDetail(item.id) },
              ) {
                Image(
                  painter = painterResource(id = item.coverRes),
                  contentDescription = item.title,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize(),
                )
                Box(
                  modifier =
                    Modifier.fillMaxSize()
                      .background(Brush.verticalGradient(listOf(Color.Transparent, DarkHeroBg.copy(alpha = 0.55f))))
                )
                Row(
                  modifier = Modifier.align(Alignment.TopStart).padding(10.dp),
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  Surface(shape = CircleShape, color = LightPrimary) {
                    Text(
                      text = item.badge,
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                      color = LightOnPrimary,
                      modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                    )
                  }
                  Surface(shape = CircleShape, color = LightSurface.copy(alpha = 0.92f)) {
                    Text(
                      text = item.platform,
                      style = MaterialTheme.typography.labelSmall,
                      color = LightOnSurface,
                      modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                    )
                  }
                }
                Surface(
                  shape = CircleShape,
                  color = DarkHeroBg.copy(alpha = 0.78f),
                  modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp),
                ) {
                  Text(
                    text = "${item.version} · ${String.format("%.1f", item.sizeMb)} MB",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = DarkOnSurface,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                  )
                }
              }

              Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Row(
                  modifier =
                    Modifier.fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .clickable { onOpenDetail(item.id) },
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.Top,
                ) {
                  Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                      text = item.title,
                      style = MaterialTheme.typography.titleMedium,
                      color = LightOnSurface,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                      text = item.shortDesc,
                      style = MaterialTheme.typography.bodySmall,
                      color = LightOnSurfaceVariant,
                    )
                  }
                  Text(
                    text = "详情 ›",
                    style = MaterialTheme.typography.labelSmall,
                    color = LightOnSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                  )
                }

                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  item.tags.forEach { tag ->
                    Surface(shape = RoundedCornerShape(6.dp), color = LightSurfaceContainer) {
                      Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = LightOnSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                      )
                    }
                  }
                }

                if (!item.isUnlocked) {
                  Surface(
                    shape = CircleShape,
                    color = LightPrimary,
                    contentColor = LightOnPrimary,
                    modifier =
                      Modifier.fillMaxWidth()
                        .height(40.dp)
                        .clip(CircleShape)
                        .clickable(enabled = !item.isUnlocking) { onPurchaseEmulator(item.id) }
                        .testTag("emu_buy_btn_${item.id}"),
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Text(
                        text =
                          if (item.isUnlocking) "正在支付并解锁…"
                          else "${item.badge} · 购买解锁下载",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                      )
                    }
                  }
                } else {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                  ) {
                    Surface(
                      shape = CircleShape,
                      color = LightPrimary,
                      contentColor = LightOnPrimary,
                      modifier =
                        Modifier.weight(1f)
                          .height(40.dp)
                          .clip(CircleShape)
                          .clickable { onDownloadEmulator(item.id) }
                          .testTag("emu_dl_btn_${item.id}"),
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 8.dp),
                      ) {
                        Icon(Icons.Outlined.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                          text =
                            when {
                              item.isDownloadDone -> stringResource(R.string.emu_btn_ready_install)
                              item.isDownloading -> "下载中 ${item.downloadProgressPct}%"
                              else -> stringResource(R.string.emu_btn_direct_download)
                            },
                          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                          maxLines = 1,
                        )
                      }
                    }

                    Surface(
                      shape = CircleShape,
                      color = LightSurfaceContainer,
                      contentColor = LightOnSurface,
                      modifier =
                        Modifier.weight(1f)
                          .height(40.dp)
                          .clip(CircleShape)
                          .clickable {
                            clipboardManager.setText(AnnotatedString(item.downloadUrl))
                            onShowToast(if (item.isWebLink) "已复制在线访问网址" else "已复制下载链接")
                          }
                          .testTag("emu_copy_btn_${item.id}"),
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 8.dp),
                      ) {
                        Icon(Icons.Outlined.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                          text = stringResource(if (item.isWebLink) R.string.emu_btn_copy_web else R.string.emu_btn_copy_link),
                          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                          maxLines = 1,
                        )
                      }
                    }
                  }
                }

                if (item.isDownloading || item.isDownloadDone) {
                  EmulatorDownloadProgressBar(
                    pct = item.downloadProgressPct,
                    sizeMb = item.sizeMb,
                    isDone = item.isDownloadDone,
                  )
                }
              }
            }
          }
        }
      }

      OverlayToastPill(
        message = uiState.toastMessage,
        darkPolarity = false,
        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp),
      )
    }
  }

  if (uiState.isEmulatorGuideOpen) {
    ModalBottomSheet(
      onDismissRequest = onDismissGuide,
      containerColor = LightSurface,
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(text = stringResource(R.string.emu_guide_title), style = MaterialTheme.typography.titleMedium, color = LightOnSurface)
          IconButton(onClick = onDismissGuide, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close), tint = LightOnSurfaceVariant)
          }
        }
        Column(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(LightSurfaceContainerLow)
              .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = "1. Android 安装包：点击「平台直接下载」后按系统提示保存并安装即可。",
            style = MaterialTheme.typography.bodySmall,
            color = LightOnSurfaceVariant,
          )
          Text(
            text = "2. iOS 设备：推荐使用「云端小手机网页版」，在浏览器中添加到主屏幕全屏使用。",
            style = MaterialTheme.typography.bodySmall,
            color = LightOnSurfaceVariant,
          )
          Text(
            text = "3. 人设与中转配置：安装后可在设置中粘贴个人 API 中转站地址，或在广场一键导入设定卡。",
            style = MaterialTheme.typography.bodySmall,
            color = LightOnSurfaceVariant,
          )
        }
        Surface(
          shape = CircleShape,
          color = LightPrimary,
          contentColor = LightOnPrimary,
          modifier =
            Modifier.fillMaxWidth()
              .height(44.dp)
              .clip(CircleShape)
              .clickable(onClick = onDismissGuide)
              .testTag("emu_guide_confirm_btn"),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(text = stringResource(R.string.modal_known), style = MaterialTheme.typography.labelLarge)
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
      }
    }
  }
}

@Composable
fun EmulatorDetailScreen(
  uiState: PlazaExpansionUiState,
  onBack: () -> Unit,
  onPurchaseEmulator: (String) -> Unit,
  onDownloadEmulator: (String) -> Unit,
  onShowToast: (String) -> Unit,
) {
  val item = uiState.selectedEmulator
  @Suppress("DEPRECATION")
  val clipboardManager = LocalClipboardManager.current

  Scaffold(
    containerColor = LightSurfaceContainerLow,
    topBar = {
      PlazaSubScreenHeader(
        title = item.title,
        onBack = onBack,
        backTestTag = "emu_detail_back_btn",
      )
    },
    bottomBar = {
      Surface(color = LightSurface, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.navigationBarsPadding()) {
          Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(LightOutlineVariant.copy(alpha = 0.7f)))
          Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            if (!item.isUnlocked) {
              Surface(
                shape = CircleShape,
                color = LightPrimary,
                contentColor = LightOnPrimary,
                modifier =
                  Modifier.fillMaxWidth()
                    .height(44.dp)
                    .clip(CircleShape)
                    .clickable(enabled = !item.isUnlocking) { onPurchaseEmulator(item.id) }
                    .testTag("emu_detail_buy_btn"),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = if (item.isUnlocking) "正在支付并解锁…" else "${item.badge} · 购买解锁下载",
                    style = MaterialTheme.typography.labelLarge,
                  )
                }
              }
            } else {
              Surface(
                shape = CircleShape,
                color = LightSurfaceContainer,
                contentColor = LightOnSurface,
                modifier =
                  Modifier.height(44.dp)
                    .clip(CircleShape)
                    .clickable {
                      clipboardManager.setText(AnnotatedString(item.downloadUrl))
                      onShowToast("已复制下载地址：${item.downloadUrl}")
                    }
                    .testTag("emu_detail_copy_btn"),
              ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 18.dp)) {
                  Text(text = "复制链接", style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp))
                }
              }

              Surface(
                shape = CircleShape,
                color = LightPrimary,
                contentColor = LightOnPrimary,
                modifier =
                  Modifier.weight(1f)
                    .height(44.dp)
                    .clip(CircleShape)
                    .clickable { onDownloadEmulator(item.id) }
                    .testTag("emu_detail_download_btn"),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text =
                      when {
                        item.isDownloadDone -> stringResource(R.string.emu_btn_ready_install)
                        item.isDownloading -> "下载中 ${item.downloadProgressPct}%"
                        else -> "平台直接下载（${String.format("%.1f", item.sizeMb)} MB）"
                      },
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                    maxLines = 1,
                  )
                }
              }
            }
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
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        item {
          Surface(
            shape = RoundedCornerShape(22.dp),
            color = LightSurface,
            modifier = Modifier.fillMaxWidth().border(1.dp, LightOutlineVariant, RoundedCornerShape(22.dp)),
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(shape = CircleShape, color = LightSurfaceContainer) {
                  Text(
                    text = item.platform,
                    style = MaterialTheme.typography.labelSmall,
                    color = LightOnSurface,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                  )
                }
                Surface(shape = CircleShape, color = LightSurfaceContainer) {
                  Text(
                    text = "${item.version} · ${String.format("%.1f", item.sizeMb)} MB",
                    style = MaterialTheme.typography.labelSmall,
                    color = LightOnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                  )
                }
              }
              Text(text = item.title, style = MaterialTheme.typography.titleLarge, color = LightOnSurface)
              Text(text = item.intro, style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
            }
          }
        }

        if (item.isDownloading || item.isDownloadDone) {
          item {
            Surface(
              shape = RoundedCornerShape(18.dp),
              color = LightSurface,
              modifier = Modifier.fillMaxWidth().border(1.dp, LightOutlineVariant, RoundedCornerShape(18.dp)),
            ) {
              Box(modifier = Modifier.padding(14.dp)) {
                EmulatorDownloadProgressBar(
                  pct = item.downloadProgressPct,
                  sizeMb = item.sizeMb,
                  isDone = item.isDownloadDone,
                )
              }
            }
          }
        }

        items(item.showcaseImages) { slide ->
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = LightSurface,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth().border(1.dp, LightOutlineVariant, RoundedCornerShape(16.dp)),
          ) {
            Column {
              Image(
                painter = painterResource(id = slide.imageRes),
                contentDescription = slide.caption,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
              )
              Text(
                text = slide.caption,
                style = MaterialTheme.typography.bodySmall,
                color = LightOnSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
              )
            }
          }
        }
      }

      OverlayToastPill(
        message = uiState.toastMessage,
        darkPolarity = false,
        modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp),
      )
    }
  }
}

@Composable
private fun EmulatorDownloadProgressBar(
  pct: Int,
  sizeMb: Double,
  isDone: Boolean,
) {
  val currentMb = String.format("%.1f", sizeMb * pct / 100.0)
  Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(
        text = if (isDone) "校验通过，已保存至本地下载" else "正在拉取安装包...",
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
        color = LightOnSurfaceVariant,
      )
      Text(
        text = "$currentMb / ${String.format("%.1f", sizeMb)} MB",
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
        color = LightOnSurfaceVariant,
      )
    }
    Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(LightSurfaceContainerHigh)) {
      Box(
        modifier =
          Modifier.fillMaxWidth((pct / 100f).coerceIn(0f, 1f))
            .height(6.dp)
            .clip(CircleShape)
            .background(LightPrimary),
      )
    }
  }
}

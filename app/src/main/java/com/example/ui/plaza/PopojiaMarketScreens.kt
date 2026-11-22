package com.example.ui.plaza

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.home.OverlayToastPill
import com.example.ui.theme.DarkHeroBg
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.LightOnPrimary
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightOutlineVariant
import com.example.ui.theme.LightPrimary
import com.example.ui.theme.LightSecondary
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceContainer
import com.example.ui.theme.LightSurfaceContainerLow
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopojiaMarketScreen(
  uiState: PlazaExpansionUiState,
  onBack: () -> Unit,
  onSelectCategory: (MarketCategory) -> Unit,
  onOpenDetail: (String) -> Unit,
  onCardBuyOrPreview: (MarketItem) -> Unit,
  onConfirmPayment: () -> Unit,
  onSwitchToPreview: () -> Unit,
  onStartSheetDownload: () -> Unit,
  onDismissBuySheet: () -> Unit,
  onDismissPreviewSheet: () -> Unit,
  onOpenSellFlow: () -> Unit,
  onVerifyBetaCode: (String) -> Unit,
  onDismissBetaModal: () -> Unit,
  onPublishItem: (String, MarketCategory, String, String) -> Unit,
  onDismissSellSheet: () -> Unit,
  onShowToast: (String) -> Unit,
) {
  @Suppress("DEPRECATION")
  val clipboardManager = LocalClipboardManager.current

  Scaffold(
    containerColor = LightSurfaceContainerLow,
    topBar = {
      Column {
        PlazaSubScreenHeader(
          title = stringResource(R.string.market_title),
          onBack = onBack,
          backTestTag = "market_back_btn",
          trailingContent = {
            Surface(
              shape = CircleShape,
              color = LightPrimary,
              contentColor = LightOnPrimary,
              modifier =
                Modifier.clip(CircleShape)
                  .clickable(onClick = onOpenSellFlow)
                  .testTag("market_sell_btn"),
            ) {
              Text(
                text = stringResource(R.string.market_sell_btn),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
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
            MarketCategory.entries.forEach { cat ->
              val selected = uiState.marketCategory == cat
              Surface(
                shape = CircleShape,
                color = if (selected) LightPrimary else LightSurface,
                contentColor = if (selected) LightOnPrimary else LightOnSurfaceVariant,
                modifier =
                  Modifier.border(1.dp, if (selected) LightPrimary else LightOutlineVariant, CircleShape)
                    .clip(CircleShape)
                    .clickable { onSelectCategory(cat) }
                    .testTag("market_cat_${cat.name}"),
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
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        // Banner card
        item {
          Surface(
            shape = RoundedCornerShape(22.dp),
            color = LightSurface,
            modifier = Modifier.fillMaxWidth().border(1.dp, LightOutlineVariant, RoundedCornerShape(22.dp)),
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
              ) {
                Box(
                  modifier = Modifier.size(40.dp).clip(CircleShape).background(LightPrimary),
                  contentAlignment = Alignment.Center,
                ) {
                  Icon(Icons.Outlined.ShoppingBag, contentDescription = null, tint = LightOnPrimary, modifier = Modifier.size(20.dp))
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                  Text(
                    text = stringResource(R.string.market_banner_title),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = LightOnSurface,
                  )
                  Text(
                    text = stringResource(R.string.market_banner_sub),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = LightOnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                }
              }
              Surface(shape = CircleShape, color = LightSurfaceContainer) {
                Text(
                  text = stringResource(R.string.market_banner_badge),
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  color = SuccessGreen,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
              }
            }
          }
        }

        items(uiState.filteredMarketItems, key = { it.id }) { item ->
          val isPlugin = item.category == MarketCategory.PLUGINS
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = LightSurface,
            shadowElevation = 1.dp,
            modifier =
              Modifier.fillMaxWidth()
                .border(1.dp, LightOutlineVariant, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .clickable { onOpenDetail(item.id) }
                .testTag("market_card_${item.id}"),
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Surface(shape = CircleShape, color = LightSurfaceContainer) {
                    Text(
                      text = item.categoryBadge,
                      style = MaterialTheme.typography.labelSmall,
                      color = if (isPlugin) LightSecondary else SuccessGreen,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                  }
                  Surface(shape = CircleShape, color = LightSurfaceContainerLow) {
                    Text(
                      text = item.formatLabel,
                      style = MaterialTheme.typography.labelSmall,
                      color = LightOnSurfaceVariant,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                  }
                }
                Text(
                  text = item.salesLabel,
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                  color = LightOnSurfaceVariant,
                )
              }

              Text(text = item.title, style = MaterialTheme.typography.titleMedium, color = LightOnSurface)
              Text(text = item.summary, style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)

              HorizontalDivider(color = LightOutlineVariant.copy(alpha = 0.6f))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  Box(
                    modifier = Modifier.size(24.dp).clip(CircleShape).background(LightSurfaceContainer),
                    contentAlignment = Alignment.Center,
                  ) {
                    Text(
                      text = item.seller.take(1),
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                      color = LightOnSurface,
                    )
                  }
                  Text(text = item.seller, style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                  Text(
                    text = item.priceText,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = LightOnSurface,
                  )
                  Surface(
                    shape = CircleShape,
                    color = if (item.isPurchased) LightSurfaceContainer else LightPrimary,
                    contentColor = if (item.isPurchased) SuccessGreen else LightOnPrimary,
                    modifier =
                      Modifier.clip(CircleShape)
                        .clickable { onCardBuyOrPreview(item) }
                        .testTag("market_buy_btn_${item.id}"),
                  ) {
                    Text(
                      text =
                        when {
                          item.isPurchased && isPlugin -> "已购 · 下载/说明"
                          item.isPurchased -> "已购 · 下载/查看"
                          else -> item.ctaLabel
                        },
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                      modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    )
                  }
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

  // 3-step Buy BottomSheet
  val buyItem = uiState.buySheetItem
  if (buyItem != null && uiState.buyStep != MarketBuyStep.HIDDEN) {
    val isPlugin = buyItem.category == MarketCategory.PLUGINS
    ModalBottomSheet(onDismissRequest = onDismissBuySheet, containerColor = LightSurface) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Text(
            text =
              when (uiState.buyStep) {
                MarketBuyStep.CONFIRM -> "确认购买"
                MarketBuyStep.LOADING -> "正在解锁"
                MarketBuyStep.SUCCESS -> "购买成功"
                MarketBuyStep.HIDDEN -> ""
              },
            style = MaterialTheme.typography.titleLarge,
            color = LightOnSurface,
          )
          IconButton(onClick = onDismissBuySheet, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close), tint = LightOnSurfaceVariant)
          }
        }

        when (uiState.buyStep) {
          MarketBuyStep.CONFIRM -> {
            Column(
              modifier =
                Modifier.fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .background(LightSurfaceContainerLow)
                  .border(1.dp, LightOutlineVariant, RoundedCornerShape(14.dp))
                  .padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Text(text = buyItem.title, style = MaterialTheme.typography.titleMedium, color = LightOnSurface)
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "${buyItem.seller} · ${buyItem.formatLabel}", style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
                Text(text = buyItem.priceText, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = LightOnSurface)
              }
            }
            Surface(
              shape = CircleShape,
              color = LightPrimary,
              contentColor = LightOnPrimary,
              modifier =
                Modifier.fillMaxWidth()
                  .height(46.dp)
                  .clip(CircleShape)
                  .clickable(onClick = onConfirmPayment)
                  .testTag("market_confirm_pay_btn"),
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(text = "立即支付并解锁", style = MaterialTheme.typography.labelLarge)
              }
            }
          }

          MarketBuyStep.LOADING -> {
            Column(
              modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              CircularProgressIndicator(color = LightPrimary, strokeWidth = 3.dp, modifier = Modifier.size(36.dp))
              Text(text = "正在确认支付并打包文档…", style = MaterialTheme.typography.titleSmall, color = LightOnSurface)
              Text(text = "正在校验权限并生成专属下载资源", style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
            }
          }

          MarketBuyStep.SUCCESS -> {
            Row(
              modifier =
                Modifier.fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .background(LightSurfaceContainerLow)
                  .border(1.dp, LightOutlineVariant, RoundedCornerShape(14.dp))
                  .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(EmeraldSoftBg),
                contentAlignment = Alignment.Center,
              ) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
              }
              Column(modifier = Modifier.weight(1f)) {
                Text(text = buyItem.title, style = MaterialTheme.typography.titleSmall, color = LightOnSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = "已解锁成功，可立即下载或在线预览", style = MaterialTheme.typography.bodySmall, color = SuccessGreen)
              }
            }

            if (uiState.isSheetDownloading || uiState.sheetDownloadPct > 0) {
              SheetDownloadProgressBar(pct = uiState.sheetDownloadPct)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              Surface(
                shape = CircleShape,
                color = LightSurfaceContainer,
                contentColor = LightOnSurface,
                modifier = Modifier.weight(1f).height(44.dp).clip(CircleShape).clickable(onClick = onSwitchToPreview),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = if (isPlugin) "查看安装说明" else "在线预览 / 复制",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  )
                }
              }
              Surface(
                shape = CircleShape,
                color = LightPrimary,
                contentColor = LightOnPrimary,
                modifier = Modifier.weight(1f).height(44.dp).clip(CircleShape).clickable(onClick = onStartSheetDownload),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = if (isPlugin) "下载插件包" else "下载到本地",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  )
                }
              }
            }
          }
          MarketBuyStep.HIDDEN -> Unit
        }
        Spacer(modifier = Modifier.height(6.dp))
      }
    }
  }

  // Preview & Copy BottomSheet
  val previewItem = uiState.previewSheetItem
  if (previewItem != null) {
    val isPlugin = previewItem.category == MarketCategory.PLUGINS
    ModalBottomSheet(onDismissRequest = onDismissPreviewSheet, containerColor = LightSurface) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = if (isPlugin) "插件安装说明" else previewItem.title,
            style = MaterialTheme.typography.titleMedium,
            color = LightOnSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          IconButton(onClick = onDismissPreviewSheet, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close), tint = LightOnSurfaceVariant)
          }
        }

        Box(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(LightSurfaceContainerLow)
              .border(1.dp, LightOutlineVariant, RoundedCornerShape(14.dp))
              .padding(14.dp),
        ) {
          Text(text = previewItem.previewContent, style = MaterialTheme.typography.bodySmall, color = LightOnSurface)
        }

        if (uiState.isSheetDownloading || uiState.sheetDownloadPct > 0) {
          SheetDownloadProgressBar(pct = uiState.sheetDownloadPct)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          Surface(
            shape = CircleShape,
            color = LightSurfaceContainer,
            contentColor = LightOnSurface,
            modifier = Modifier.weight(1f).height(44.dp).clip(CircleShape).clickable(onClick = onStartSheetDownload),
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(text = if (isPlugin) "下载插件包" else "下载到本地", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
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
                .clickable {
                  clipboardManager.setText(AnnotatedString(previewItem.previewContent))
                  onDismissPreviewSheet()
                  onShowToast("内容已复制到剪贴板")
                },
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(text = "一键复制内容", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
            }
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
      }
    }
  }

  // Beta Code Verification Modal
  if (uiState.isBetaModalOpen) {
    var code by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismissBetaModal) {
      Surface(shape = RoundedCornerShape(24.dp), color = LightSurface, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(text = stringResource(R.string.market_beta_title), style = MaterialTheme.typography.titleLarge, color = LightOnSurface)
              Text(text = stringResource(R.string.market_beta_desc), style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
            }
            IconButton(onClick = onDismissBetaModal, modifier = Modifier.size(32.dp)) {
              Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close), tint = LightOnSurfaceVariant)
            }
          }
          OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("内测邀请码") },
            placeholder = { Text("请输入内测码（演示可填任意内容）") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("market_beta_input"),
            shape = RoundedCornerShape(14.dp),
          )
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(
              shape = CircleShape,
              color = LightSurfaceContainer,
              contentColor = LightOnSurface,
              modifier = Modifier.weight(1f).height(42.dp).clip(CircleShape).clickable(onClick = onDismissBetaModal),
            ) {
              Box(contentAlignment = Alignment.Center) { Text("取消", style = MaterialTheme.typography.labelLarge) }
            }
            Surface(
              shape = CircleShape,
              color = LightPrimary,
              contentColor = LightOnPrimary,
              modifier =
                Modifier.weight(1f)
                  .height(42.dp)
                  .clip(CircleShape)
                  .clickable { onVerifyBetaCode(code) }
                  .testTag("market_beta_submit_btn"),
            ) {
              Box(contentAlignment = Alignment.Center) { Text("验证并继续", style = MaterialTheme.typography.labelLarge) }
            }
          }
        }
      }
    }
  }

  // Sell Form Sheet
  if (uiState.isSellSheetOpen) {
    var title by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf(MarketCategory.PROMPTS) }
    var price by remember { mutableStateOf("6.0") }
    var desc by remember { mutableStateOf("") }
    ModalBottomSheet(onDismissRequest = onDismissSellSheet, containerColor = LightSurface) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Text(text = "上架到破破甲集市", style = MaterialTheme.typography.titleLarge, color = LightOnSurface)
          IconButton(onClick = onDismissSellSheet, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close), tint = LightOnSurfaceVariant)
          }
        }
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("商品名称") },
          placeholder = { Text("例如：DeepSeek 防降智记忆总结模板") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("sell_title_input"),
          shape = RoundedCornerShape(14.dp),
        )
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf(MarketCategory.PROMPTS, MarketCategory.SKINS, MarketCategory.TEMPLATES, MarketCategory.PLUGINS).forEach { c ->
            val selected = selectedCat == c
            Surface(
              shape = CircleShape,
              color = if (selected) LightPrimary else LightSurfaceContainer,
              contentColor = if (selected) LightOnPrimary else LightOnSurfaceVariant,
              modifier = Modifier.clip(CircleShape).clickable { selectedCat = c },
            ) {
              Text(text = c.label, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
            }
          }
        }
        OutlinedTextField(
          value = price,
          onValueChange = { price = it },
          label = { Text("定价（元）") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
        )
        OutlinedTextField(
          value = desc,
          onValueChange = { desc = it },
          label = { Text("简介说明") },
          placeholder = { Text("简要介绍适用场景、文档格式与亮点") },
          minLines = 2,
          modifier = Modifier.fillMaxWidth().testTag("sell_desc_input"),
          shape = RoundedCornerShape(14.dp),
        )
        Surface(
          shape = CircleShape,
          color = LightPrimary,
          contentColor = LightOnPrimary,
          modifier =
            Modifier.fillMaxWidth()
              .height(46.dp)
              .clip(CircleShape)
              .clickable { onPublishItem(title, selectedCat, price, desc) }
              .testTag("sell_submit_btn"),
        ) {
          Box(contentAlignment = Alignment.Center) { Text("提交并上架集市", style = MaterialTheme.typography.labelLarge) }
        }
        Spacer(modifier = Modifier.height(6.dp))
      }
    }
  }
}

@Composable
fun MarketItemDetailScreen(
  uiState: PlazaExpansionUiState,
  onBack: () -> Unit,
  onToggleFavorite: (String) -> Unit,
  onPurchaseOrDownload: (String) -> Unit,
  onShowToast: (String) -> Unit,
) {
  val item = uiState.selectedMarketItem
  var lightboxIndex by remember { mutableIntStateOf(-1) }

  Scaffold(
    containerColor = LightSurfaceContainerLow,
    topBar = {
      PlazaSubScreenHeader(
        title = stringResource(R.string.market_detail_title),
        onBack = onBack,
        backTestTag = "market_detail_back_btn",
        trailingContent = {
          IconButton(
            onClick = { onShowToast("已复制商品分享链接") },
            modifier = Modifier.size(48.dp).testTag("market_detail_share_btn"),
          ) {
            Icon(Icons.Outlined.Share, contentDescription = stringResource(R.string.action_share), tint = LightOnSurface)
          }
        },
      )
    },
    bottomBar = {
      Surface(color = LightSurface, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.navigationBarsPadding()) {
          HorizontalDivider(color = LightOutlineVariant.copy(alpha = 0.7f))
          Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier =
                Modifier.clip(RoundedCornerShape(10.dp))
                  .clickable { onToggleFavorite(item.id) }
                  .padding(horizontal = 10.dp, vertical = 4.dp)
                  .testTag("market_detail_fav_btn"),
            ) {
              Icon(
                imageVector = if (item.isFavorited) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = "收藏",
                tint = if (item.isFavorited) LightSecondary else LightOnSurfaceVariant,
                modifier = Modifier.size(21.dp),
              )
              Text(
                text = "收藏",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = if (item.isFavorited) LightSecondary else LightOnSurfaceVariant,
              )
            }

            Surface(
              shape = CircleShape,
              color = if (item.isPurchased) LightSurfaceContainer else LightPrimary,
              contentColor = if (item.isPurchased) SuccessGreen else LightOnPrimary,
              modifier =
                Modifier.weight(1f)
                  .height(44.dp)
                  .clip(CircleShape)
                  .clickable(enabled = !uiState.isDetailUnlocking) { onPurchaseOrDownload(item.id) }
                  .testTag("market_detail_buy_btn"),
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text =
                    when {
                      uiState.isDetailUnlocking -> "正在解锁…"
                      item.isPurchased -> "已购 · 下载原文件"
                      else -> item.ctaLabel
                    },
                  style = MaterialTheme.typography.labelLarge,
                )
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
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        // 1. Header Card
        item {
          Surface(
            shape = RoundedCornerShape(22.dp),
            color = LightSurface,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth().border(1.dp, LightOutlineVariant, RoundedCornerShape(22.dp)),
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Surface(shape = CircleShape, color = LightSurfaceContainer) {
                    Text(item.categoryBadge, style = MaterialTheme.typography.labelSmall, color = SuccessGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                  }
                  Surface(shape = CircleShape, color = LightSurfaceContainerLow) {
                    Text(item.formatLabel, style = MaterialTheme.typography.labelSmall, color = LightOnSurfaceVariant, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                  }
                }
                Text(item.salesLabel, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = LightOnSurfaceVariant)
              }
              Text(item.title, style = MaterialTheme.typography.titleLarge, color = LightOnSurface)
              HorizontalDivider(color = LightOutlineVariant.copy(alpha = 0.5f))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(item.priceText, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = LightOnSurface)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Icon(Icons.Filled.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                  Text("购买后可直接下载", style = MaterialTheme.typography.bodySmall, color = SuccessGreen)
                }
              }
            }
          }
        }

        // 2. Preview Gallery
        item {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Text("效果图", style = MaterialTheme.typography.titleMedium, color = LightOnSurface)
              Text("${item.galleryRes.size} 张 · 点击放大", style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
            }
            Row(
              modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              item.galleryRes.forEachIndexed { idx, resId ->
                Image(
                  painter = painterResource(id = resId),
                  contentDescription = "效果图 ${idx + 1}",
                  contentScale = ContentScale.Crop,
                  modifier =
                    Modifier.size(width = 176.dp, height = 112.dp)
                      .clip(RoundedCornerShape(16.dp))
                      .border(1.dp, LightOutlineVariant, RoundedCornerShape(16.dp))
                      .clickable { lightboxIndex = idx },
                )
              }
            }
          }
        }

        // 3. Intro
        item {
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = LightSurface,
            modifier = Modifier.fillMaxWidth().border(1.dp, LightOutlineVariant.copy(alpha = 0.8f), RoundedCornerShape(20.dp)),
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text("商品介绍", style = MaterialTheme.typography.titleMedium, color = LightOnSurface)
              Text(item.detailIntro, style = MaterialTheme.typography.bodyMedium, color = LightOnSurfaceVariant)
            }
          }
        }

        // 4. Original File Card
        item {
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = LightSurface,
            modifier = Modifier.fillMaxWidth().border(1.dp, LightOutlineVariant.copy(alpha = 0.8f), RoundedCornerShape(20.dp)),
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("原文件", style = MaterialTheme.typography.titleMedium, color = LightOnSurface)
                Surface(shape = CircleShape, color = LightSurfaceContainer) {
                  Text(
                    text = if (item.isPurchased) "已解锁" else "未解锁",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (item.isPurchased) SuccessGreen else LightOnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                  )
                }
              }

              Row(
                modifier =
                  Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(LightSurfaceContainerLow)
                    .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Box(
                  modifier =
                    Modifier.size(40.dp)
                      .clip(RoundedCornerShape(12.dp))
                      .background(LightSurface)
                      .border(1.dp, LightOutlineVariant, RoundedCornerShape(12.dp)),
                  contentAlignment = Alignment.Center,
                ) {
                  Icon(Icons.Outlined.Description, contentDescription = null, tint = LightOnSurface, modifier = Modifier.size(20.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                  Text(item.fileName, style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp), color = LightOnSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                  Text(item.fileMeta, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = LightOnSurfaceVariant)
                }
                Surface(
                  shape = CircleShape,
                  color = if (item.isPurchased) LightSurfaceContainer else LightPrimary,
                  contentColor = if (item.isPurchased) SuccessGreen else LightOnPrimary,
                  modifier =
                    Modifier.clip(CircleShape)
                      .clickable(enabled = !uiState.isDetailUnlocking) { onPurchaseOrDownload(item.id) }
                      .testTag("market_detail_file_btn"),
                ) {
                  Text(
                    text =
                      when {
                        uiState.isDetailUnlocking -> "解锁中…"
                        item.isPurchased -> "下载文件"
                        else -> "立即购买"
                      },
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
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
        modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp),
      )
    }
  }

  if (lightboxIndex in item.galleryRes.indices) {
    Dialog(onDismissRequest = { lightboxIndex = -1 }) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        IconButton(
          onClick = { lightboxIndex = -1 },
          modifier = Modifier.size(36.dp).clip(CircleShape).background(LightSurface),
        ) {
          Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close), tint = LightOnSurface)
        }
        Image(
          painter = painterResource(id = item.galleryRes[lightboxIndex]),
          contentDescription = "效果图放大预览",
          contentScale = ContentScale.Fit,
          modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(DarkHeroBg),
        )
      }
    }
  }
}

@Composable
private fun SheetDownloadProgressBar(pct: Int) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(
        text = if (pct >= 100) "下载完成，已保存至本地" else "正在打包并下载文档…",
        style = MaterialTheme.typography.bodySmall,
        color = LightOnSurfaceVariant,
      )
      Text("$pct%", style = MaterialTheme.typography.labelSmall, color = LightOnSurface)
    }
    Box(modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape).background(LightSurfaceContainer)) {
      Box(
        modifier = Modifier.fillMaxWidth((pct / 100f).coerceIn(0f, 1f)).height(7.dp).clip(CircleShape).background(LightPrimary)
      )
    }
  }
}

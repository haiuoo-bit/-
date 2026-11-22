package com.example.ui.plaza

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.home.OverlayToastPill
import com.example.ui.theme.AmberTintBg
import com.example.ui.theme.AmberTintDark
import com.example.ui.theme.EmeraldDarkText
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.LightOnPrimary
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightOutlineVariant
import com.example.ui.theme.LightPrimary
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceContainer
import com.example.ui.theme.LightSurfaceContainerLow
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RelayStationDetailScreen(
  station: RelayStationItem,
  extended: RelayStationExtended,
  toastMessage: String?,
  onBack: () -> Unit,
  onToggleImpression: (String, String) -> Unit,
  onFeedbackNoteChange: (String, String) -> Unit,
  onSubmitRating: (String, Int) -> Unit,
  onShowToast: (String) -> Unit,
) {
  @Suppress("DEPRECATION")
  val clipboardManager = LocalClipboardManager.current
  var chosenStars by remember(station.id, station.userRatedStars) {
    mutableIntStateOf(station.userRatedStars ?: 0)
  }
  val starLabels = remember {
    listOf("点击星星评分", "1 星 · 较差", "2 星 · 一般", "3 星 · 可用", "4 星 · 推荐", "5 星 · 非常稳定")
  }

  val badgeBg =
    when (extended.badgeTone) {
      "amber" -> WarningAmber
      "green" -> SuccessGreen
      else -> LightPrimary
    }
  val tagBg =
    when (extended.badgeTone) {
      "amber" -> AmberTintBg
      "green" -> LightSurfaceContainer
      else -> EmeraldSoftBg
    }
  val tagFg =
    when (extended.badgeTone) {
      "amber" -> AmberTintDark
      "green" -> LightOnSurfaceVariant
      else -> EmeraldDarkText
    }

  Scaffold(
    containerColor = LightSurfaceContainerLow,
    topBar = {
      PlazaSubScreenHeader(
        title = stringResource(R.string.relay_detail_title),
        onBack = onBack,
        backTestTag = "relay_detail_back_btn",
        trailingContent = {
          Surface(
            shape = CircleShape,
            color = LightSurfaceContainer,
            contentColor = LightOnSurface,
            modifier =
              Modifier.clip(CircleShape)
                .clickable {
                  clipboardManager.setText(AnnotatedString(extended.endpointUrl))
                  onShowToast("已复制接口地址：${extended.endpointUrl}")
                }
                .testTag("relay_header_copy_btn"),
          ) {
            Text(
              text = stringResource(R.string.relay_copy_url),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            )
          }
        },
      )
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
        // Module 1: Station summary, metrics & Base URL copy bar
        item {
          Surface(
            shape = RoundedCornerShape(18.dp),
            color = LightSurface,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth().border(1.dp, LightOutlineVariant, RoundedCornerShape(18.dp)),
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
              ) {
                Row(
                  modifier = Modifier.weight(1f),
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                  verticalAlignment = Alignment.Top,
                ) {
                  Box(
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(badgeBg),
                    contentAlignment = Alignment.Center,
                  ) {
                    Text(
                      text = extended.badgeChar,
                      style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                      color = Color.White,
                    )
                  }
                  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                      Text(
                        text = station.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = LightOnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                      )
                      Surface(shape = RoundedCornerShape(6.dp), color = tagBg) {
                        Text(
                          text = extended.tagLabel,
                          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                          color = tagFg,
                          modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                        )
                      }
                    }
                    Text(
                      text = "岛民评分汇总 · 数据仅供参考",
                      style = MaterialTheme.typography.bodySmall,
                      color = LightOnSurfaceVariant,
                    )
                  }
                }

                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = String.format("%.1f", station.score),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = LightOnSurface,
                    modifier = Modifier.testTag("relay_detail_score"),
                  )
                  Text(
                    text = "${station.reviewsCount} 人评分",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = LightOnSurfaceVariant,
                  )
                }
              }

              // 3 metrics
              Row(
                modifier =
                  Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(LightSurfaceContainerLow)
                    .border(1.dp, LightOutlineVariant.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                    .padding(vertical = 10.dp, horizontal = 8.dp),
              ) {
                RelayDetailStatCol("计费倍率", extended.rateText, LightOnSurface, Modifier.weight(1f))
                RelayDetailStatCol("参考延迟", extended.latencyText, SuccessGreen, Modifier.weight(1f))
                RelayDetailStatCol("支持模型", extended.modelCountText, LightOnSurface, Modifier.weight(1f))
              }

              HorizontalDivider(color = LightOutlineVariant.copy(alpha = 0.6f))

              // Base URL bar
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("中转站网址（接口地址）", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = LightOnSurface)
                  Text("兼容 OpenAI 格式", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = LightOnSurfaceVariant)
                }
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  Box(
                    modifier =
                      Modifier.weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightSurfaceContainer)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.CenterStart,
                  ) {
                    Text(
                      text = extended.endpointUrl,
                      style = MaterialTheme.typography.bodySmall,
                      color = LightOnSurface,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                  }
                  Surface(
                    shape = CircleShape,
                    color = LightPrimary,
                    contentColor = LightOnPrimary,
                    modifier =
                      Modifier.height(38.dp)
                        .clip(CircleShape)
                        .clickable {
                          clipboardManager.setText(AnnotatedString(extended.endpointUrl))
                          onShowToast("已复制接口地址：${extended.endpointUrl}")
                        }
                        .testTag("relay_copy_url_btn"),
                  ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                      Text("复制链接", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
                    }
                  }
                  Surface(
                    shape = CircleShape,
                    color = LightSurface,
                    contentColor = LightOnSurface,
                    modifier =
                      Modifier.height(38.dp)
                        .border(1.dp, LightOutlineVariant, CircleShape)
                        .clip(CircleShape)
                        .clickable { onShowToast("请复制地址后前往「游玩设置 → 自定义接口」粘贴使用") }
                        .testTag("relay_go_config_btn"),
                  ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                      Text("去配置", style = MaterialTheme.typography.labelSmall)
                    }
                  }
                }
              }
            }
          }
        }

        // Module 2: Supported models & rates table
        item {
          Surface(
            shape = RoundedCornerShape(18.dp),
            color = LightSurface,
            modifier = Modifier.fillMaxWidth().border(1.dp, LightOutlineVariant, RoundedCornerShape(18.dp)),
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("支持模型与参考倍率", style = MaterialTheme.typography.titleMedium, color = LightOnSurface)
                Text("共 ${extended.modelCountText}可用模型", style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
              }

              Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth().background(LightSurfaceContainerLow).padding(horizontal = 10.dp, vertical = 8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Text("模型名称", style = MaterialTheme.typography.labelSmall, color = LightOnSurfaceVariant)
                  Text("上下文 / 计费参考", style = MaterialTheme.typography.labelSmall, color = LightOnSurfaceVariant)
                }
                extended.supportedModels.forEach { model ->
                  HorizontalDivider(color = LightOutlineVariant.copy(alpha = 0.6f))
                  Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(model.name, style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp), color = LightOnSurface)
                      Text(model.usageNote, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = LightOnSurfaceVariant)
                    }
                    Text(
                      text = model.priceTag,
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                      color = if (model.isDiscount) SuccessGreen else LightOnSurface,
                    )
                  }
                }
              }
            }
          }
        }

        // Module 3: Station notes
        item {
          Surface(
            shape = RoundedCornerShape(18.dp),
            color = LightSurface,
            modifier = Modifier.fillMaxWidth().border(1.dp, LightOutlineVariant, RoundedCornerShape(18.dp)),
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("站点信息与说明", style = MaterialTheme.typography.titleMedium, color = LightOnSurface)
                Surface(shape = RoundedCornerShape(6.dp), color = LightSurfaceContainer) {
                  Text("岛民反馈参考", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = LightOnSurfaceVariant, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                }
              }
              RelayInfoSubBox("岛民反馈亮点", "多位岛民反馈晚间高峰长上下文流式输出较稳，首字响应较快，日常长文对话表现稳定。")
              RelayInfoSubBox("新手配置说明", "复制上方「中转站网址」后，前往「游玩设置 → 自定义接口」，粘贴到 Base URL 并填入你在该站申请的 API Key 即可开始游玩。")
              RelayInfoSubBox("充值与额度提醒", "支持按量计费且余额永不过期；建议新手首次先充最低档位体验，确认常用模型延迟满意后再长期使用。")
            }
          }
        }

        // Module 4: Interactive star rating + impressions + feedback note
        item {
          Surface(
            shape = RoundedCornerShape(18.dp),
            color = LightSurface,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth().border(1.dp, LightOutlineVariant, RoundedCornerShape(18.dp)),
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("岛民反馈与打分", style = MaterialTheme.typography.titleMedium, color = LightOnSurface)
                Text(
                  text = starLabels.getOrElse(chosenStars) { "点击星星评分" },
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  color = if (chosenStars > 0) SuccessGreen else LightOnSurfaceVariant,
                )
              }

              // 5 star row
              Row(
                modifier =
                  Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightSurfaceContainerLow)
                    .border(1.dp, LightOutlineVariant.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                  (1..5).forEach { s ->
                    val active = s <= chosenStars
                    IconButton(
                      onClick = { chosenStars = s },
                      modifier = Modifier.size(44.dp).testTag("relay_detail_star_$s"),
                    ) {
                      Icon(
                        imageVector = if (active) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = "$s 分",
                        tint = if (active) SuccessGreen else LightOutlineVariant,
                        modifier = Modifier.size(28.dp),
                      )
                    }
                  }
                }
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                  Text(
                    text = if (chosenStars > 0) "$chosenStars.0" else "--",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (chosenStars > 0) SuccessGreen else LightOnSurfaceVariant,
                  )
                  Text("/ 5", style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
                }
              }

              // Impression multi-select tags
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("使用印象（可多选）", style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  PlazaExpansionSeedData.impressionTags.forEach { tag ->
                    val active = extended.selectedImpressions.contains(tag)
                    Surface(
                      shape = CircleShape,
                      color = if (active) LightPrimary else LightSurfaceContainer,
                      contentColor = if (active) LightOnPrimary else LightOnSurfaceVariant,
                      modifier =
                        Modifier.clip(CircleShape)
                          .clickable { onToggleImpression(station.id, tag) }
                          .testTag("relay_impression_$tag"),
                    ) {
                      Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                      )
                    }
                  }
                }
              }

              OutlinedTextField(
                value = extended.feedbackNote,
                onValueChange = { onFeedbackNoteChange(station.id, it) },
                placeholder = { Text("补充一句真实体验，帮其他岛民避坑（选填）", style = MaterialTheme.typography.bodySmall) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("relay_feedback_input"),
                shape = RoundedCornerShape(12.dp),
              )

              if (station.userRatedStars != null) {
                Row(
                  modifier =
                    Modifier.fillMaxWidth()
                      .clip(RoundedCornerShape(10.dp))
                      .background(SuccessGreen.copy(alpha = 0.1f))
                      .border(1.dp, SuccessGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                      .padding(horizontal = 12.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  Icon(Icons.Filled.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                  Text(
                    text = "已提交 ${station.userRatedStars} 星反馈，最新均分 ${String.format("%.1f", station.score)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SuccessGreen,
                  )
                }
              }

              val canSubmit = chosenStars > 0
              val isUpdate = station.userRatedStars != null
              Surface(
                shape = CircleShape,
                color =
                  when {
                    !canSubmit -> LightSurfaceContainer
                    isUpdate && chosenStars == station.userRatedStars -> SuccessGreen
                    else -> LightPrimary
                  },
                contentColor = if (canSubmit) LightOnPrimary else LightOnSurfaceVariant,
                modifier =
                  Modifier.fillMaxWidth()
                    .height(44.dp)
                    .clip(CircleShape)
                    .clickable(enabled = canSubmit) { onSubmitRating(station.id, chosenStars) }
                    .testTag("relay_detail_submit_btn"),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text =
                      when {
                        !canSubmit -> "请先选择星级"
                        isUpdate && chosenStars == station.userRatedStars -> "已提交反馈 · 点星级可修改"
                        isUpdate -> "更新 ${chosenStars} 星反馈"
                        else -> "提交 ${chosenStars} 星反馈"
                      },
                    style = MaterialTheme.typography.labelLarge,
                  )
                }
              }
            }
          }
        }
      }

      OverlayToastPill(
        message = toastMessage,
        darkPolarity = false,
        modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp),
      )
    }
  }
}

@Composable
private fun RelayDetailStatCol(
  label: String,
  value: String,
  valueColor: Color,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
    Text(text = label, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = LightOnSurfaceVariant)
    Text(text = value, style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp), color = valueColor)
  }
}

@Composable
private fun RelayInfoSubBox(title: String, body: String) {
  Column(
    modifier =
      Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(LightSurfaceContainerLow)
        .padding(10.dp),
    verticalArrangement = Arrangement.spacedBy(3.dp),
  ) {
    Text(text = title, style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp), color = LightOnSurface)
    Text(text = body, style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
  }
}

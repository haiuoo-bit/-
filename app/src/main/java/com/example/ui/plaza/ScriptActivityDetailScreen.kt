package com.example.ui.plaza

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.home.OverlayToastPill
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.ErrorRed
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
import com.example.ui.theme.VioletDeep
import com.example.ui.theme.VioletTintBg
import com.example.ui.theme.WarningAmber

@Composable
fun ScriptActivityDetailScreen(
  detail: ScriptActivityDetailState,
  toastMessage: String?,
  onBack: () -> Unit,
  onSelectFaction: (String) -> Unit,
  onPrimaryAction: () -> Unit,
  onUploadProof: () -> Unit,
  onShowToast: (String) -> Unit,
) {
  @Suppress("DEPRECATION")
  val clipboardManager = LocalClipboardManager.current
  var isScheduleExpanded by remember { mutableStateOf(false) }
  var isStoryExpanded by remember { mutableStateOf(true) }
  var isRulesExpanded by remember { mutableStateOf(true) }

  val selectedFactionObj = detail.factions.find { it.name == detail.selectedFaction }
  val remainSeats = selectedFactionObj?.remainCount ?: 1

  val stageTitle =
    when {
      detail.rewardClaimed -> "活动奖励已到账（星海徽章 +￥200）"
      detail.proofUploaded -> "已上传通关截图 · 奖励可领取"
      detail.enrolled -> "已锁定阵营：${detail.selectedFaction}"
      else -> "报名中 · 剩 $remainSeats 席（${detail.selectedFaction}）"
    }
  val stageSub =
    when {
      detail.rewardClaimed -> "感谢参与本场剧本局"
      detail.proofUploaded -> "点击右侧按钮即可立即领取"
      detail.enrolled -> "请下载设定文档并在通关后上传截图"
      else -> "11-24 20:00 报名截止"
    }
  val ctaLabel =
    when {
      detail.rewardClaimed -> "已领取"
      detail.proofUploaded -> "领取奖励"
      detail.enrolled -> "去下载 / 传图"
      else -> "立即报名"
    }
  val ctaBg =
    when {
      detail.rewardClaimed -> LightSurfaceContainer
      detail.proofUploaded -> SuccessGreen
      detail.enrolled -> LightSecondary
      else -> LightPrimary
    }
  val ctaFg = if (detail.rewardClaimed) LightOnSurfaceVariant else LightOnPrimary

  Scaffold(
    containerColor = LightSurfaceContainerLow,
    topBar = {
      PlazaSubScreenHeader(
        title = stringResource(R.string.script_detail_title),
        onBack = onBack,
        backTestTag = "script_detail_back_btn",
        trailingContent = {
          IconButton(
            onClick = {
              clipboardManager.setText(AnnotatedString("https://daoyu.app/script/${detail.scriptId}"))
              onShowToast("剧本局邀请链接已复制")
            },
            modifier = Modifier.size(48.dp).testTag("script_detail_share_btn"),
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
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Column(modifier = Modifier.weight(1f).padding(end = 10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = stageTitle,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                color = LightOnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
              Text(
                text = stageSub,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = LightOnSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
            Surface(
              shape = CircleShape,
              color = ctaBg,
              contentColor = ctaFg,
              modifier =
                Modifier.height(44.dp)
                  .clip(CircleShape)
                  .clickable(enabled = !detail.rewardClaimed, onClick = onPrimaryAction)
                  .testTag("script_detail_primary_cta"),
            ) {
              Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(text = ctaLabel, style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp))
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
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        // Module 1: Cover + Hero Meta
        item {
          Surface(color = LightSurface, modifier = Modifier.fillMaxWidth()) {
            Column {
              Image(
                painter = painterResource(id = detail.coverRes),
                contentDescription = detail.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(188.dp),
              )
              Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Surface(
                    shape = CircleShape,
                    color = if (detail.enrolled) LightSecondary else SuccessGreen,
                  ) {
                    Text(
                      text = if (detail.enrolled) "已报名" else "报名中",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                    )
                  }

                  Surface(
                    shape = CircleShape,
                    color = LightSurfaceContainer,
                    modifier =
                      Modifier.border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), CircleShape)
                        .clip(CircleShape)
                        .clickable { isScheduleExpanded = !isScheduleExpanded }
                        .testTag("script_time_toggle_btn"),
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp),
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    ) {
                      Icon(Icons.Outlined.Schedule, contentDescription = null, tint = LightOnSurfaceVariant, modifier = Modifier.size(14.dp))
                      Text("11-18 ~ 11-26", style = MaterialTheme.typography.labelSmall, color = LightOnSurface)
                      Icon(
                        imageVector = if (isScheduleExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = LightOnSurfaceVariant,
                        modifier = Modifier.size(15.dp),
                      )
                    }
                  }

                  Text(
                    text = "${detail.currentPlayers} / ${detail.maxPlayers} 人",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = LightOnSurfaceVariant,
                  )
                }

                AnimatedVisibility(visible = isScheduleExpanded) {
                  Column(
                    modifier =
                      Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightSurfaceContainerLow)
                        .border(1.dp, LightOutlineVariant, RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                  ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      ScheduleNodeChip("报名开始", "11-18 10:00", LightOnSurfaceVariant, Modifier.weight(1f))
                      ScheduleNodeChip("报名截止", "11-24 20:00", WarningAmber, Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      ScheduleNodeChip("活动开始", "11-24 20:30", LightOnSurfaceVariant, Modifier.weight(1f))
                      ScheduleNodeChip("活动截止", "11-26 22:00", LightOnSurfaceVariant, Modifier.weight(1f))
                    }
                  }
                }

                Text(text = detail.title, style = MaterialTheme.typography.titleLarge, color = LightOnSurface)

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                      modifier = Modifier.size(24.dp).clip(CircleShape).background(VioletTintBg),
                      contentAlignment = Alignment.Center,
                    ) {
                      Text("零", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold), color = VioletDeep)
                    }
                    Text(detail.hostInfo, style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
                  }
                  Text(detail.rewardSummary, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = WarningAmber)
                }
              }
              HorizontalDivider(color = LightOutlineVariant.copy(alpha = 0.6f))
            }
          }
        }

        // Collapsible Chapter 1: Story Background
        item {
          CollapsibleDetailCard(
            title = "故事背景与世界观",
            expanded = isStoryExpanded,
            onToggle = { isStoryExpanded = !isStoryExpanded },
            modifier = Modifier.padding(horizontal = 16.dp),
          ) {
            Text(text = detail.storyBackground, style = MaterialTheme.typography.bodyMedium, color = LightOnSurfaceVariant)
          }
        }

        // Collapsible Chapter 2: Rules & Rewards
        item {
          CollapsibleDetailCard(
            title = "玩法规则与奖励",
            expanded = isRulesExpanded,
            onToggle = { isRulesExpanded = !isRulesExpanded },
            modifier = Modifier.padding(horizontal = 16.dp),
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              detail.rulesList.forEach { rule ->
                Text("• $rule", style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
              }
              Column(
                modifier =
                  Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(LightSurfaceContainerLow)
                    .border(1.dp, LightOutlineVariant.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("通关基础奖励", style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
                  Text("星海限定徽章 + 300 积分", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = WarningAmber)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("获胜阵营 / MVP", style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
                  Text("￥200 奖池平分", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = WarningAmber)
                }
              }
            }
          }
        }

        // Chapter 3: Faction Selection Grid (3 cols x 2 rows)
        item {
          Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Text("选择阵营", style = MaterialTheme.typography.titleMedium, color = LightOnSurface)
              Text(
                text = if (detail.enrolled) "已锁定阵营：${detail.selectedFaction}" else "点击可选阵营报名",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = if (detail.enrolled) LightSecondary else SuccessGreen,
              )
            }

            detail.factions.chunked(3).forEach { rowFactions ->
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowFactions.forEach { faction ->
                  val isSelected = detail.selectedFaction == faction.name
                  val borderColor =
                    when {
                      isSelected -> LightPrimary
                      else -> LightOutlineVariant
                    }
                  val cardBg =
                    when {
                      faction.isFull -> LightSurfaceContainerLow
                      isSelected -> LightPrimary.copy(alpha = 0.05f)
                      else -> LightSurface
                    }

                  Column(
                    modifier =
                      Modifier.weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(cardBg)
                        .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(14.dp))
                        .clickable(enabled = !faction.isFull) { onSelectFaction(faction.name) }
                        .padding(vertical = 10.dp, horizontal = 6.dp)
                        .testTag("script_faction_${faction.name}"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                  ) {
                    Text(
                      text = faction.name,
                      style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                      color = if (faction.isFull) LightOnSurfaceVariant else LightOnSurface,
                      maxLines = 1,
                    )
                    Text(
                      text = "${faction.currentCount} / ${faction.maxCount} 人",
                      style = MaterialTheme.typography.labelSmall,
                      color = LightOnSurfaceVariant,
                    )
                    val (pillBg, pillFg, pillText) =
                      when {
                        faction.isFull -> Triple(ErrorRed.copy(alpha = 0.1f), ErrorRed, "已满员")
                        isSelected -> Triple(LightPrimary, LightOnPrimary, "已选中")
                        else -> Triple(EmeraldSoftBg, SuccessGreen, "可报名")
                      }
                    Surface(shape = CircleShape, color = pillBg) {
                      Text(
                        text = pillText,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                        color = pillFg,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                      )
                    }
                  }
                }
              }
            }
          }
        }

        // Chapter 4: Script Document Hub
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (detail.enrolled) LightSurface else LightSurfaceContainerLow,
            modifier =
              Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(1.dp, LightOutlineVariant, RoundedCornerShape(16.dp)),
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("剧本设定文档", style = MaterialTheme.typography.titleMedium, color = LightOnSurface)
                Surface(
                  shape = CircleShape,
                  color = if (detail.enrolled) EmeraldSoftBg else LightSurfaceContainer,
                ) {
                  Text(
                    text = if (detail.enrolled) "已解锁 · 可下载/上传" else "报名后解锁",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                    color = if (detail.enrolled) SuccessGreen else LightOnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                  )
                }
              }

              Row(
                modifier =
                  Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(LightSurface)
                    .border(1.dp, LightOutlineVariant.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Box(
                  modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(VioletTintBg),
                  contentAlignment = Alignment.Center,
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.Description, contentDescription = null, tint = VioletDeep, modifier = Modifier.size(18.dp))
                    Text("MD", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = VioletDeep)
                  }
                }
                Column(modifier = Modifier.weight(1f)) {
                  Text(detail.docFileName, style = MaterialTheme.typography.titleSmall, color = LightOnSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                  Text(detail.docFileMeta, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = LightOnSurfaceVariant)
                }
              }

              if (!detail.enrolled) {
                Text(
                  text = "选好阵营并点击底部「立即报名」后，即可下载完整设定文档并上传通关截图。",
                  style = MaterialTheme.typography.bodySmall,
                  color = LightOnSurfaceVariant,
                )
              } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LightSurface,
                    modifier =
                      Modifier.weight(1f)
                        .height(44.dp)
                        .border(1.dp, LightOutlineVariant, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onShowToast("开始下载《${detail.docFileName}》") }
                        .testTag("script_download_doc_btn"),
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                      Icon(Icons.Outlined.Download, contentDescription = null, tint = LightOnSurface, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.size(4.dp))
                      Text("下载设定文档", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = LightOnSurface)
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LightSurface,
                    modifier =
                      Modifier.weight(1f)
                        .height(44.dp)
                        .border(1.dp, if (detail.proofUploaded) SuccessGreen else LightOutlineVariant, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onUploadProof)
                        .testTag("script_upload_proof_btn"),
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                      Icon(Icons.Outlined.Upload, contentDescription = null, tint = if (detail.proofUploaded) SuccessGreen else LightOnSurface, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.size(4.dp))
                      Text(
                        text = if (detail.proofUploaded) "重新上传截图" else "上传通关截图",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (detail.proofUploaded) SuccessGreen else LightOnSurface,
                      )
                    }
                  }
                }
              }

              if (detail.proofUploaded) {
                Text(
                  text = "已上传通关结算截图（待房主核验发放奖励）",
                  style = MaterialTheme.typography.bodySmall,
                  color = SuccessGreen,
                )
              }
            }
          }
        }

        // Chapter 5: Notices
        item {
          Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Text("注意事项", style = MaterialTheme.typography.titleMedium, color = LightOnSurface)
            Text("• 请于活动开始前 15 分钟下载设定文档并熟悉个人人物小传。", style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
            Text("• 严禁剧透或场外贴脸，通关后需在截止时间前上传结算截图方可领取奖励。", style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
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
private fun ScheduleNodeChip(
  label: String,
  time: String,
  labelColor: Color,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.clip(RoundedCornerShape(8.dp)).background(LightSurface).padding(horizontal = 8.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = labelColor)
    Text(text = time, style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold), color = LightOnSurface)
  }
}

@Composable
private fun CollapsibleDetailCard(
  title: String,
  expanded: Boolean,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = LightSurface,
    modifier = modifier.fillMaxWidth().border(1.dp, LightOutlineVariant.copy(alpha = 0.8f), RoundedCornerShape(16.dp)),
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle).padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = LightOnSurface)
        Icon(
          imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
          contentDescription = null,
          tint = LightOnSurfaceVariant,
          modifier = Modifier.size(20.dp),
        )
      }
      AnimatedVisibility(visible = expanded) {
        Box(modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp)) {
          content()
        }
      }
    }
  }
}

package com.example.ui.plaza

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.home.OverlayToastPill
import com.example.ui.theme.DarkHeroBg
import com.example.ui.theme.DarkOnSurface
import com.example.ui.theme.DarkVioletHero
import com.example.ui.theme.EmeraldDarkText
import com.example.ui.theme.EmeraldSoftBg
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
import com.example.ui.theme.SkyTintBg
import com.example.ui.theme.SkyTintDark
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.VioletDeep
import com.example.ui.theme.VioletTintBg
import com.example.ui.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlazaBountyScreen(
  uiState: PlazaUiState,
  onBack: () -> Unit,
  onSelectPanel: (BountyPanel) -> Unit,
  onSelectTopic: (String) -> Unit,
  onOpenCreatorSheet: (CreatorShowcaseItem?) -> Unit,
  onSubmitCustomRequest: (String, String) -> Unit,
  onOpenBountySheet: (BountyRequestItem?) -> Unit,
  onSubmitBountyApplication: (String) -> Unit,
  onShowToast: (String) -> Unit,
) {
  Scaffold(
    containerColor = LightSurfaceContainerLow,
    topBar = {
      Column {
        PlazaSubScreenHeader(
          title = stringResource(R.string.bounty_title),
          onBack = onBack,
          backTestTag = "bounty_back_btn",
          trailingContent = {
            Surface(
              shape = CircleShape,
              color = LightPrimary,
              contentColor = LightOnPrimary,
              modifier =
                Modifier.clip(CircleShape)
                  .clickable { onShowToast("创作者认证通过后即可开通个人接单橱窗") }
                  .testTag("bounty_accept_orders_btn"),
            ) {
              Text(
                text = stringResource(R.string.bounty_action_accept),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
              )
            }
          },
        )

        Surface(color = LightSurface, modifier = Modifier.fillMaxWidth()) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            // Segmented Switcher
            Row(
              modifier =
                Modifier.fillMaxWidth()
                  .clip(CircleShape)
                  .background(LightSurfaceContainer)
                  .padding(4.dp),
            ) {
              val isCustom = uiState.activeBountyPanel == BountyPanel.CUSTOM
              Surface(
                shape = CircleShape,
                color = if (isCustom) LightSurface else Color.Transparent,
                shadowElevation = if (isCustom) 1.dp else 0.dp,
                modifier =
                  Modifier.weight(1f)
                    .height(36.dp)
                    .clip(CircleShape)
                    .clickable { onSelectPanel(BountyPanel.CUSTOM) }
                    .testTag("bounty_tab_custom"),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = stringResource(R.string.bounty_tab_custom),
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                    color = if (isCustom) LightOnSurface else LightOnSurfaceVariant,
                  )
                }
              }

              Surface(
                shape = CircleShape,
                color = if (!isCustom) LightSurface else Color.Transparent,
                shadowElevation = if (!isCustom) 1.dp else 0.dp,
                modifier =
                  Modifier.weight(1f)
                    .height(36.dp)
                    .clip(CircleShape)
                    .clickable { onSelectPanel(BountyPanel.BOUNTY) }
                    .testTag("bounty_tab_bounty"),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = stringResource(R.string.bounty_tab_bounty),
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                    color = if (!isCustom) LightOnSurface else LightOnSurfaceVariant,
                  )
                }
              }
            }

            // Topic Filter Pills
            Row(
              modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              PlazaSeedData.bountyTopics.forEach { topic ->
                val selected = uiState.activeBountyTopic == topic
                Surface(
                  shape = CircleShape,
                  color = if (selected) LightPrimary else LightSurfaceContainer,
                  contentColor = if (selected) LightOnPrimary else LightOnSurfaceVariant,
                  modifier =
                    Modifier.clip(CircleShape)
                      .clickable { onSelectTopic(topic) }
                      .testTag("bounty_topic_$topic"),
                ) {
                  Text(
                    text = topic,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
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
        // Escrow Flow Card
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = SkyTintBg,
            modifier = Modifier.fillMaxWidth().border(1.dp, LightSecondary.copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  Icon(Icons.Outlined.VerifiedUser, contentDescription = null, tint = SkyTintDark, modifier = Modifier.size(16.dp))
                  Text(
                    text = stringResource(R.string.bounty_escrow_label),
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                    color = SkyTintDark,
                  )
                }
                Text(
                  text = stringResource(R.string.bounty_escrow_hint),
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                  color = LightOnSurfaceVariant,
                )
              }

              val steps =
                if (uiState.activeBountyPanel == BountyPanel.CUSTOM) {
                  listOf("1 沟通人设", "2 担保托管", "3 满意交付")
                } else {
                  listOf("1 发布赏金", "2 多人投稿", "3 中标结算")
                }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                steps.forEach { step ->
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LightSurface,
                    modifier = Modifier.weight(1f),
                  ) {
                    Text(
                      text = step,
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                      color = LightOnSurface,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    )
                  }
                }
              }
            }
          }
        }

        if (uiState.activeBountyPanel == BountyPanel.CUSTOM) {
          if (uiState.filteredCreators.isEmpty()) {
            item {
              Text(
                text = "当前题材下暂无接单创作者",
                style = MaterialTheme.typography.bodySmall,
                color = LightOnSurfaceVariant,
                modifier = Modifier.padding(20.dp),
              )
            }
          } else {
            items(uiState.filteredCreators, key = { it.id }) { creator ->
              Surface(
                shape = RoundedCornerShape(20.dp),
                color = LightSurface,
                modifier =
                  Modifier.fillMaxWidth()
                    .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                    .testTag("bounty_creator_${creator.id}"),
              ) {
                Column(
                  modifier = Modifier.padding(16.dp),
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
                        modifier = Modifier.size(44.dp).clip(CircleShape).background(IndigoTintBg),
                        contentAlignment = Alignment.Center,
                      ) {
                        Text(
                          text = creator.avatarInitial,
                          style = MaterialTheme.typography.titleMedium,
                          color = LightSecondary,
                        )
                      }
                      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                          Text(text = creator.name, style = MaterialTheme.typography.titleMedium, color = LightOnSurface)
                          CreatorVBadge(tier = creator.vTier, size = 15.dp)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                          creator.topics.forEach { t ->
                            Surface(shape = CircleShape, color = LightSurfaceContainer) {
                              Text(
                                text = t,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = LightOnSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                              )
                            }
                          }
                        }
                      }
                    }

                    Text(
                      text = creator.priceText,
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = LightSecondary,
                    )
                  }

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Text(
                      text = "${creator.responseSpeed} · 已交付 ${creator.deliveredCount} 单",
                      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                      color = LightOnSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                      Surface(
                        shape = CircleShape,
                        color = LightSurfaceContainer,
                        contentColor = LightOnSurface,
                        modifier = Modifier.clip(CircleShape).clickable { onOpenCreatorSheet(creator) },
                      ) {
                        Text(
                          text = stringResource(R.string.bounty_view_showcase),
                          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                          modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        )
                      }
                      Surface(
                        shape = CircleShape,
                        color = LightPrimary,
                        contentColor = LightOnPrimary,
                        modifier =
                          Modifier.clip(CircleShape)
                            .clickable { onOpenCreatorSheet(creator) }
                            .testTag("bounty_order_${creator.id}"),
                      ) {
                        Text(
                          text = stringResource(R.string.bounty_go_order),
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
        } else {
          if (uiState.filteredBounties.isEmpty()) {
            item {
              Text(
                text = "当前题材下暂无悬赏任务",
                style = MaterialTheme.typography.bodySmall,
                color = LightOnSurfaceVariant,
                modifier = Modifier.padding(20.dp),
              )
            }
          } else {
            items(uiState.filteredBounties, key = { it.id }) { bounty ->
              Surface(
                shape = RoundedCornerShape(20.dp),
                color = LightSurface,
                modifier =
                  Modifier.fillMaxWidth()
                    .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                    .testTag("bounty_task_${bounty.id}"),
              ) {
                Column(
                  modifier = Modifier.padding(16.dp),
                  verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Surface(shape = CircleShape, color = EmeraldSoftBg) {
                      Text(
                        text = bounty.budgetText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldDarkText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                      )
                    }
                    Text(
                      text = bounty.deadlineText,
                      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                      color = LightOnSurfaceVariant,
                    )
                  }

                  Text(
                    text = bounty.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = LightOnSurface,
                  )
                  Text(
                    text = bounty.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = LightOnSurfaceVariant,
                  )

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Text(
                      text = "#${bounty.topic} · 已有 ${bounty.applicantsCount} 人投稿",
                      style = MaterialTheme.typography.labelSmall,
                      color = LightOnSurfaceVariant,
                    )
                    Surface(
                      shape = CircleShape,
                      color = if (bounty.hasApplied) EmeraldSoftBg else LightSecondary,
                      contentColor = if (bounty.hasApplied) EmeraldDarkText else Color.White,
                      modifier =
                        Modifier.clip(CircleShape)
                          .clickable { onOpenBountySheet(bounty) }
                          .testTag("bounty_bid_${bounty.id}"),
                    ) {
                      Text(
                        text = if (bounty.hasApplied) "已投稿 ✓" else stringResource(R.string.bounty_submit_bid),
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
      }

      OverlayToastPill(
        message = uiState.toastMessage,
        darkPolarity = false,
        modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 24.dp),
      )
    }
  }

  val creator = uiState.selectedCreatorForSheet
  if (creator != null) {
    var note by remember(creator.id) { mutableStateOf("") }
    ModalBottomSheet(
      onDismissRequest = { onOpenCreatorSheet(null) },
      containerColor = LightSurface,
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(text = "@${creator.name} 约稿橱窗", style = MaterialTheme.typography.headlineMedium, color = LightOnSurface)
          CreatorVBadge(tier = creator.vTier, size = 16.dp)
        }
        Text(text = creator.intro, style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
        Text(
          text = "参考报价：${creator.priceText} · ${creator.responseSpeed}",
          style = MaterialTheme.typography.labelLarge,
          color = LightSecondary,
        )
        OutlinedTextField(
          value = note,
          onValueChange = { note = it },
          placeholder = { Text("简述你的 OC 设定、字数要求与预期截稿日（选填）") },
          modifier = Modifier.fillMaxWidth(),
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
              .clickable { onSubmitCustomRequest(creator.name, note) }
              .testTag("bounty_confirm_order_btn"),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(text = "发送担保约稿意向", style = MaterialTheme.typography.labelLarge)
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
      }
    }
  }

  val bounty = uiState.selectedBountyForSheet
  if (bounty != null) {
    ModalBottomSheet(
      onDismissRequest = { onOpenBountySheet(null) },
      containerColor = LightSurface,
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text(text = bounty.title, style = MaterialTheme.typography.titleLarge, color = LightOnSurface)
        Text(text = bounty.description, style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
        Text(
          text = "${bounty.budgetText} · ${bounty.deadlineText}",
          style = MaterialTheme.typography.labelLarge,
          color = EmeraldDarkText,
        )
        Surface(
          shape = CircleShape,
          color = LightSecondary,
          contentColor = Color.White,
          modifier =
            Modifier.fillMaxWidth()
              .height(46.dp)
              .clip(CircleShape)
              .clickable { onSubmitBountyApplication(bounty.id) }
              .testTag("bounty_confirm_bid_btn"),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = if (bounty.hasApplied) "已提交过投稿" else "确认投稿并参与赏金评选",
              style = MaterialTheme.typography.labelLarge,
            )
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlazaScriptsScreen(
  uiState: PlazaUiState,
  onBack: () -> Unit,
  onSelectFilter: (ScriptFilter) -> Unit,
  onOpenCreateLockModal: () -> Unit,
  onDismissCreateLockModal: () -> Unit,
  onApplyEarlyScript: () -> Unit,
  onOpenScriptJoinSheet: (ScriptLobbyItem?) -> Unit,
  onConfirmJoinScript: (String, String) -> Unit,
  onShowToast: (String) -> Unit,
) {
  Scaffold(
    containerColor = LightSurfaceContainerLow,
    topBar = {
      Column {
        PlazaSubScreenHeader(
          title = stringResource(R.string.scripts_title),
          onBack = onBack,
          backTestTag = "scripts_back_btn",
          trailingContent = {
            Surface(
              shape = CircleShape,
              color = VioletDeep,
              contentColor = Color.White,
              modifier =
                Modifier.clip(CircleShape)
                  .clickable(onClick = onOpenCreateLockModal)
                  .testTag("scripts_create_btn"),
            ) {
              Text(
                text = stringResource(R.string.scripts_action_create),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
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
            ScriptFilter.entries.forEach { filter ->
              val selected = uiState.activeScriptFilter == filter
              Surface(
                shape = CircleShape,
                color = if (selected) LightPrimary else LightSurfaceContainer,
                contentColor = if (selected) LightOnPrimary else LightOnSurfaceVariant,
                modifier =
                  Modifier.clip(CircleShape)
                    .clickable { onSelectFilter(filter) }
                    .testTag("scripts_filter_${filter.name}"),
              ) {
                Text(
                  text = filter.label,
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
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize().widthIn(max = 600.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        item {
          Box(
            modifier =
              Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.horizontalGradient(listOf(DarkHeroBg, DarkVioletHero)))
                .padding(16.dp),
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = stringResource(R.string.scripts_banner_title),
                style = MaterialTheme.typography.titleMedium,
                color = DarkOnSurface,
              )
              Text(
                text = stringResource(R.string.scripts_banner_sub),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = DarkOnSurface.copy(alpha = 0.72f),
              )
            }
          }
        }

        items(uiState.filteredScripts, key = { it.id }) { script ->
          val (badgeBg, badgeFg) =
            when (script.status) {
              ScriptStatus.OPEN -> EmeraldSoftBg to EmeraldDarkText
              ScriptStatus.ACTIVE -> SkyTintBg to SkyTintDark
              ScriptStatus.ENDED -> LightSurfaceContainer to LightOnSurfaceVariant
            }
          val progress = (script.currentPlayers.toFloat() / script.maxPlayers.toFloat()).coerceIn(0f, 1f)

          Surface(
            shape = RoundedCornerShape(20.dp),
            color = LightSurface,
            modifier =
              Modifier.fillMaxWidth()
                .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                .testTag("script_card_${script.id}"),
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Surface(shape = CircleShape, color = badgeBg) {
                    Text(
                      text = script.status.label,
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                      color = badgeFg,
                      modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                    )
                  }
                  script.tags.forEach { tag ->
                    Surface(shape = CircleShape, color = LightSurfaceContainer) {
                      Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = LightOnSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                      )
                    }
                  }
                }
                Text(
                  text = script.timeLabel,
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                  color = LightOnSurfaceVariant,
                )
              }

              Text(
                text = script.title,
                style = MaterialTheme.typography.titleMedium,
                color = LightOnSurface,
              )

              Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Text(
                    text = "房间席位",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = LightOnSurfaceVariant,
                  )
                  Text(
                    text = "${script.currentPlayers} / ${script.maxPlayers} 人",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = LightOnSurface,
                  )
                }
                Box(
                  modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(LightSurfaceContainer)
                ) {
                  Box(
                    modifier = Modifier.fillMaxWidth(progress).height(6.dp).clip(CircleShape).background(VioletDeep)
                  )
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text(
                  text =
                    if (script.selectedRole != null) "已锁定身份：${script.selectedRole}"
                    else script.roles.take(2).joinToString(" · "),
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                  color = LightOnSurfaceVariant,
                )

                val actionText =
                  when {
                    script.selectedRole != null -> stringResource(R.string.scripts_btn_joined)
                    script.status == ScriptStatus.OPEN -> stringResource(R.string.scripts_btn_join)
                    script.status == ScriptStatus.ACTIVE -> stringResource(R.string.scripts_btn_observe)
                    else -> stringResource(R.string.scripts_btn_ended)
                  }
                val actionBg =
                  when {
                    script.selectedRole != null -> EmeraldSoftBg
                    script.status == ScriptStatus.OPEN -> VioletDeep
                    script.status == ScriptStatus.ACTIVE -> SkyTintBg
                    else -> LightSurfaceContainer
                  }
                val actionFg =
                  when {
                    script.selectedRole != null -> EmeraldDarkText
                    script.status == ScriptStatus.OPEN -> Color.White
                    script.status == ScriptStatus.ACTIVE -> SkyTintDark
                    else -> LightOnSurfaceVariant
                  }

                Surface(
                  shape = CircleShape,
                  color = actionBg,
                  contentColor = actionFg,
                  modifier =
                    Modifier.clip(CircleShape)
                      .clickable(enabled = script.status != ScriptStatus.ENDED) {
                        when (script.status) {
                          ScriptStatus.OPEN -> onOpenScriptJoinSheet(script)
                          ScriptStatus.ACTIVE -> onShowToast("已进入《${script.title}》实时旁观席位")
                          ScriptStatus.ENDED -> Unit
                        }
                      }
                      .testTag("script_action_${script.id}"),
                ) {
                  Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
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
        modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 24.dp),
      )
    }
  }

  if (uiState.isCreateScriptLockOpen) {
    Dialog(onDismissRequest = onDismissCreateLockModal) {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = LightSurface,
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
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Box(
                modifier = Modifier.size(34.dp).clip(CircleShape).background(VioletTintBg),
                contentAlignment = Alignment.Center,
              ) {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = VioletDeep, modifier = Modifier.size(18.dp))
              }
              Text(
                text = stringResource(R.string.scripts_lock_title),
                style = MaterialTheme.typography.titleMedium,
                color = LightOnSurface,
              )
            }
            IconButton(onClick = onDismissCreateLockModal, modifier = Modifier.size(32.dp)) {
              Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close), tint = LightOnSurfaceVariant)
            }
          }

          Text(
            text = stringResource(R.string.scripts_lock_desc),
            style = MaterialTheme.typography.bodySmall,
            color = LightOnSurfaceVariant,
          )

          Column(
            modifier =
              Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(LightSurfaceContainerLow)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text("创作 3 篇公开作品", style = MaterialTheme.typography.bodySmall, color = LightOnSurface)
              Text("2 / 3", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = WarningAmber)
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text("账号完成实名认证", style = MaterialTheme.typography.bodySmall, color = LightOnSurface)
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                Text("已认证", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
              }
            }
          }

          Surface(
            shape = CircleShape,
            color = VioletDeep,
            contentColor = Color.White,
            modifier =
              Modifier.fillMaxWidth()
                .height(44.dp)
                .clip(CircleShape)
                .clickable(onClick = onApplyEarlyScript)
                .testTag("scripts_lock_apply_btn"),
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(text = stringResource(R.string.scripts_lock_apply), style = MaterialTheme.typography.labelLarge)
            }
          }
        }
      }
    }
  }

  val activeScript = uiState.activeScriptJoinSheet
  if (activeScript != null) {
    var chosenRole by remember(activeScript.id) {
      mutableStateOf(activeScript.selectedRole ?: "船长位")
    }
    ModalBottomSheet(
      onDismissRequest = { onOpenScriptJoinSheet(null) },
      containerColor = LightSurface,
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text(text = "选择阵营角色 · ${activeScript.title}", style = MaterialTheme.typography.titleLarge, color = LightOnSurface)
        Text(text = "请选择本局扮演身份，上车后人数将实时同步至大厅。", style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          listOf("船长位", "秘密观察者", "替补接龙席").forEach { role ->
            val selected = chosenRole == role
            Surface(
              shape = CircleShape,
              color = if (selected) VioletDeep else LightSurfaceContainer,
              contentColor = if (selected) Color.White else LightOnSurface,
              modifier = Modifier.clip(CircleShape).clickable { chosenRole = role },
            ) {
              Text(
                text = role,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              )
            }
          }
        }
        Surface(
          shape = CircleShape,
          color = VioletDeep,
          contentColor = Color.White,
          modifier =
            Modifier.fillMaxWidth()
              .height(46.dp)
              .clip(CircleShape)
              .clickable { onConfirmJoinScript(activeScript.id, chosenRole) }
              .testTag("script_confirm_join_btn"),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(text = "确认锁定「$chosenRole」并上车", style = MaterialTheme.typography.labelLarge)
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
      }
    }
  }
}

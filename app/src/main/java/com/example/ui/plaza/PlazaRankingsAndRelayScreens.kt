package com.example.ui.plaza

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.home.OverlayToastPill
import com.example.ui.theme.AmberTintBg
import com.example.ui.theme.AmberTintDark
import com.example.ui.theme.BronzeMedal
import com.example.ui.theme.DarkHeroBg
import com.example.ui.theme.DarkHeroEnd
import com.example.ui.theme.DarkHeroMid
import com.example.ui.theme.DarkOnSurface
import com.example.ui.theme.EmeraldDarkText
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.GoldMedal
import com.example.ui.theme.LightOnPrimary
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightOutlineVariant
import com.example.ui.theme.LightPrimary
import com.example.ui.theme.LightSecondary
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceContainer
import com.example.ui.theme.LightSurfaceContainerLow
import com.example.ui.theme.SilverMedal
import com.example.ui.theme.SkyTintBg
import com.example.ui.theme.SkyTintDark
import com.example.ui.theme.SuccessGreen

@Composable
fun PlazaRankingsScreen(
  uiState: PlazaUiState,
  onBack: () -> Unit,
  onSelectBoard: (RankingBoardType) -> Unit,
  onToggleVote: (String) -> Unit,
  onOpenStoryDetail: (String) -> Unit,
) {
  val entries = uiState.currentRankings
  val topScore = (entries.maxOfOrNull { it.score } ?: 1).coerceAtLeast(1)

  Scaffold(
    containerColor = LightSurfaceContainerLow,
    topBar = {
      Column {
        PlazaSubScreenHeader(
          title = stringResource(R.string.rankings_title),
          onBack = onBack,
          backTestTag = "rankings_back_btn",
        )
        Surface(color = LightSurface, modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier =
              Modifier.fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            RankingBoardType.entries.forEach { board ->
              val selected = uiState.activeBoard == board
              Surface(
                shape = CircleShape,
                color = if (selected) LightPrimary else LightSurfaceContainer,
                contentColor = if (selected) LightOnPrimary else LightOnSurfaceVariant,
                modifier =
                  Modifier.clip(CircleShape)
                    .clickable { onSelectBoard(board) }
                    .testTag("rankings_tab_${board.name}"),
              ) {
                Text(
                  text = board.label,
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
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = LightSurface,
            modifier = Modifier.fillMaxWidth().border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = LightSecondary, modifier = Modifier.size(18.dp))
                Text(
                  text = stringResource(R.string.rankings_quota_label),
                  style = MaterialTheme.typography.titleSmall,
                  color = LightOnSurface,
                )
              }
              Surface(shape = CircleShape, color = SkyTintBg) {
                Text(
                  text = stringResource(R.string.rankings_quota_format, uiState.remainingVotes),
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = SkyTintDark,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp).testTag("rankings_remaining_votes"),
                )
              }
            }
          }
        }

        if (entries.isNotEmpty()) {
          val topItem = entries.first()
          item {
            RankingTopHeroCard(
              entry = topItem,
              onToggleVote = { onToggleVote(topItem.id) },
              onOpenDetail = { onOpenStoryDetail(topItem.storyRouteId) },
            )
          }
        }

        items(entries.drop(1), key = { it.id }) { entry ->
          RankingRunnerCard(
            entry = entry,
            ratio = (entry.score.toFloat() / topScore.toFloat()).coerceIn(0.15f, 1f),
            onToggleVote = { onToggleVote(entry.id) },
            onOpenDetail = { onOpenStoryDetail(entry.storyRouteId) },
          )
        }

        item {
          Text(
            text = stringResource(R.string.rankings_footer_note),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = LightOnSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
          )
        }
      }

      OverlayToastPill(
        message = uiState.toastMessage,
        darkPolarity = false,
        modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 24.dp),
      )
    }
  }
}

@Composable
private fun RankingTopHeroCard(
  entry: RankingEntry,
  onToggleVote: () -> Unit,
  onOpenDetail: () -> Unit,
) {
  Box(
    modifier =
      Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(22.dp))
        .background(Brush.linearGradient(listOf(DarkHeroBg, DarkHeroMid, DarkHeroEnd)))
        .clickable(onClick = onOpenDetail)
        .padding(18.dp)
        .testTag("ranking_card_${entry.id}"),
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
            modifier = Modifier.size(30.dp).clip(CircleShape).background(GoldMedal),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              text = "1",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
              color = DarkHeroBg,
            )
          }
          Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.12f)) {
            Text(
              text = "TOP 1 · 热度领跑",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
              color = GoldMedal,
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
            )
          }
        }
        if (entry.riseBadge != null) {
          Surface(shape = CircleShape, color = EmeraldSoftBg.copy(alpha = 0.2f)) {
            Text(
              text = entry.riseBadge,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
              color = GoldMedal,
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
            )
          }
        }
      }

      Text(
        text = entry.title,
        style = MaterialTheme.typography.headlineMedium,
        color = DarkOnSurface,
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(
            text = "${entry.author} · ${entry.tag}",
            style = MaterialTheme.typography.bodySmall,
            color = DarkOnSurface.copy(alpha = 0.72f),
          )
          Text(
            text = "热度指数 ${entry.score}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = GoldMedal,
          )
        }

        Surface(
          shape = CircleShape,
          color = if (entry.voted) LightSecondary else Color.White,
          contentColor = if (entry.voted) Color.White else LightOnSurface,
          modifier = Modifier.clip(CircleShape).clickable(onClick = onToggleVote).testTag("vote_btn_${entry.id}"),
        ) {
          Text(
            text = stringResource(if (entry.voted) R.string.rankings_voted_btn else R.string.rankings_vote_btn),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun RankingRunnerCard(
  entry: RankingEntry,
  ratio: Float,
  onToggleVote: () -> Unit,
  onOpenDetail: () -> Unit,
) {
  val rankBg =
    when (entry.rank) {
      2 -> SilverMedal
      3 -> BronzeMedal
      else -> LightSurfaceContainerHighBg
    }
  val rankFg = if (entry.rank <= 3) Color.White else LightOnSurfaceVariant

  Surface(
    shape = RoundedCornerShape(18.dp),
    color = LightSurface,
    modifier =
      Modifier.fillMaxWidth()
        .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
        .clip(RoundedCornerShape(18.dp))
        .clickable(onClick = onOpenDetail)
        .testTag("ranking_card_${entry.id}"),
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Box(
          modifier = Modifier.size(28.dp).clip(CircleShape).background(rankBg),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = entry.rank.toString(),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = rankFg,
          )
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(
            text = entry.title,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
            color = LightOnSurface,
          )
          Text(
            text = "${entry.author} · ${entry.tag} · 热度 ${entry.score}",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = LightOnSurfaceVariant,
          )
        }

        Surface(
          shape = CircleShape,
          color = if (entry.voted) SkyTintBg else LightSurfaceContainer,
          contentColor = if (entry.voted) SkyTintDark else LightOnSurface,
          modifier =
            Modifier.border(
              1.dp,
              if (entry.voted) LightSecondary.copy(alpha = 0.4f) else LightOutlineVariant,
              CircleShape,
            )
              .clip(CircleShape)
              .clickable(onClick = onToggleVote)
              .testTag("vote_btn_${entry.id}"),
        ) {
          Text(
            text = stringResource(if (entry.voted) R.string.rankings_voted_btn else R.string.rankings_vote_btn),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
          )
        }
      }

      Box(
        modifier = Modifier.fillMaxWidth().height(5.dp).clip(CircleShape).background(LightSurfaceContainer)
      ) {
        Box(
          modifier = Modifier.fillMaxWidth(ratio).height(5.dp).clip(CircleShape).background(LightSecondary)
        )
      }
    }
  }
}

private val LightSurfaceContainerHighBg = Color(0xFFE5E7EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlazaRelayReviewsScreen(
  uiState: PlazaUiState,
  onBack: () -> Unit,
  onSelectFilter: (RelayFilter) -> Unit,
  onOpenStationDetail: (RelayStationItem?) -> Unit,
  onSubmitRating: (String, Int) -> Unit,
) {
  Scaffold(
    containerColor = LightSurfaceContainerLow,
    topBar = {
      Column {
        PlazaSubScreenHeader(
          title = stringResource(R.string.relay_title),
          onBack = onBack,
          backTestTag = "relay_back_btn",
        )
        Surface(color = LightSurface, modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier =
              Modifier.fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            RelayFilter.entries.forEach { filter ->
              val selected = uiState.activeRelayFilter == filter
              Surface(
                shape = CircleShape,
                color = if (selected) LightPrimary else LightSurfaceContainer,
                contentColor = if (selected) LightOnPrimary else LightOnSurfaceVariant,
                modifier =
                  Modifier.clip(CircleShape)
                    .clickable { onSelectFilter(filter) }
                    .testTag("relay_filter_${filter.name}"),
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
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = SkyTintBg,
            modifier = Modifier.fillMaxWidth().border(1.dp, LightSecondary.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              verticalAlignment = Alignment.Top,
            ) {
              Icon(Icons.Outlined.Info, contentDescription = null, tint = SkyTintDark, modifier = Modifier.size(18.dp))
              Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                  text = stringResource(R.string.relay_notice_title),
                  style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                  color = LightOnSurface,
                )
                Text(
                  text = stringResource(R.string.relay_notice_body),
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                  color = LightOnSurfaceVariant,
                )
              }
            }
          }
        }

        items(uiState.filteredRelayStations, key = { it.id }) { station ->
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = LightSurface,
            modifier =
              Modifier.fillMaxWidth()
                .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                .testTag("relay_card_${station.id}"),
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  modifier = Modifier.weight(1f),
                ) {
                  Text(
                    text = station.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = LightOnSurface,
                  )
                  if (station.isVerified) {
                    Surface(shape = CircleShape, color = EmeraldSoftBg) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                      ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SuccessGreen))
                        Text(
                          text = stringResource(R.string.relay_tag_verified),
                          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                          color = EmeraldDarkText,
                        )
                      }
                    }
                  }
                }

                Surface(shape = CircleShape, color = AmberTintBg) {
                  Text(
                    text = "★ ${station.score} (${station.reviewsCount}人)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = AmberTintDark,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                  )
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                RelayMetricPill(stringResource(R.string.relay_metric_price), station.priceText, Modifier.weight(1f))
                RelayMetricPill(stringResource(R.string.relay_metric_uptime), station.uptimeText, Modifier.weight(1f))
                RelayMetricPill(stringResource(R.string.relay_metric_models), station.modelsText, Modifier.weight(1.25f))
              }

              Surface(
                shape = CircleShape,
                color = LightSurfaceContainer,
                contentColor = LightOnSurface,
                modifier =
                  Modifier.fillMaxWidth()
                    .height(44.dp)
                    .clip(CircleShape)
                    .clickable { onOpenStationDetail(station) }
                    .testTag("relay_detail_btn_${station.id}"),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = stringResource(R.string.relay_btn_detail),
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
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

  val detailStation = uiState.selectedRelayStation
  if (detailStation != null) {
    var pendingStars by remember(detailStation.id, detailStation.userRatedStars) {
      mutableIntStateOf(detailStation.userRatedStars ?: 5)
    }
    ModalBottomSheet(
      onDismissRequest = { onOpenStationDetail(null) },
      containerColor = LightSurface,
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        Text(text = detailStation.name, style = MaterialTheme.typography.headlineMedium, color = LightOnSurface)
        Text(
          text = "首字平均延迟 ${detailStation.latencyMs}ms · 7日可用率 ${detailStation.uptimeText} · 支持 ${detailStation.modelsText}",
          style = MaterialTheme.typography.bodySmall,
          color = LightOnSurfaceVariant,
        )
        Column(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(LightSurfaceContainerLow)
              .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = "为该中转站实测打分",
            style = MaterialTheme.typography.labelLarge,
            color = LightOnSurface,
          )
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            (1..5).forEach { star ->
              IconButton(
                onClick = { pendingStars = star },
                modifier = Modifier.size(44.dp).testTag("relay_star_$star"),
              ) {
                Icon(
                  imageVector = if (star <= pendingStars) Icons.Filled.Star else Icons.Outlined.StarOutline,
                  contentDescription = "$star 星",
                  tint = if (star <= pendingStars) GoldMedal else LightOnSurfaceVariant,
                  modifier = Modifier.size(28.dp),
                )
              }
            }
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
              .clickable { onSubmitRating(detailStation.id, pendingStars) }
              .testTag("relay_submit_rating_btn"),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(text = "提交 ${pendingStars} 星评价", style = MaterialTheme.typography.labelLarge)
          }
        }
        Spacer(modifier = Modifier.height(8.dp))
      }
    }
  }
}

@Composable
private fun RelayMetricPill(
  label: String,
  value: String,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.clip(RoundedCornerShape(12.dp)).background(LightSurfaceContainerLow).padding(10.dp),
    verticalArrangement = Arrangement.spacedBy(3.dp),
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
      color = LightOnSurfaceVariant,
      maxLines = 1,
    )
    Text(
      text = value,
      style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
      color = LightOnSurface,
      maxLines = 1,
    )
  }
}

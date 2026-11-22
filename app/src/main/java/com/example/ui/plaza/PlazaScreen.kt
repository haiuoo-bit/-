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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AmberTintBg
import com.example.ui.theme.AmberTintDark
import com.example.ui.theme.DarkHeroBg
import com.example.ui.theme.DarkHeroEnd
import com.example.ui.theme.DarkHeroMid
import com.example.ui.theme.DarkOnSurface
import com.example.ui.theme.EmeraldDarkText
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.IndigoDeep
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
import com.example.ui.theme.VioletDeep
import com.example.ui.theme.VioletTintBg
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PlazaScreen(
  uiState: PlazaUiState,
  onOpenGlobalSearch: () -> Unit,
  onOpenRankings: () -> Unit,
  onOpenBounty: (BountyPanel) -> Unit,
  onOpenScripts: () -> Unit,
  onOpenRelayReviews: () -> Unit,
  onOpenTagFinder: (String?) -> Unit,
  onOpenEmulators: () -> Unit,
  onOpenMarket: () -> Unit,
  onOpenArticleDetail: (String) -> Unit,
  onJumpToHomeTab: () -> Unit,
  onSelectTag: (String) -> Unit,
  onSelectKind: (String) -> Unit,
  onResetFilters: () -> Unit,
  onOpenNoticeModal: () -> Unit,
  onDismissNoticeModal: () -> Unit,
  onOpenCategorySheet: () -> Unit,
  onDismissCategorySheet: () -> Unit,
  onShowToast: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val slides = PlazaSeedData.bannerSlides
  val pagerState = rememberPagerState(pageCount = { slides.size })
  val scope = rememberCoroutineScope()

  LaunchedEffect(pagerState.settledPage) {
    delay(4200)
    val next = (pagerState.currentPage + 1) % slides.size
    pagerState.animateScrollToPage(next)
  }

  Column(
    modifier = modifier.fillMaxSize().background(LightSurfaceContainerLow),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    // Sticky Top Search + Notice Pill
    Surface(
      color = LightSurface,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
        Row(
          modifier =
            Modifier.fillMaxWidth()
              .widthIn(max = 600.dp)
              .padding(horizontal = 16.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Row(
            modifier =
              Modifier.weight(1f)
                .height(40.dp)
                .clip(CircleShape)
                .background(LightSurfaceContainer)
                .clickable(onClick = onOpenGlobalSearch)
                .padding(horizontal = 14.dp)
                .testTag("plaza_search_bar"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Icon(
              imageVector = Icons.Outlined.Search,
              contentDescription = null,
              tint = LightOnSurfaceVariant,
              modifier = Modifier.size(18.dp),
            )
            Text(
              text = stringResource(R.string.plaza_search_placeholder),
              style = MaterialTheme.typography.bodySmall,
              color = LightOnSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }

          Surface(
            shape = CircleShape,
            color = IndigoTintBg,
            modifier =
              Modifier.height(40.dp)
                .clip(CircleShape)
                .clickable(onClick = onOpenNoticeModal)
                .testTag("plaza_notice_btn"),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              modifier = Modifier.padding(horizontal = 12.dp),
            ) {
              Icon(
                imageVector = Icons.Outlined.Campaign,
                contentDescription = null,
                tint = IndigoDeep,
                modifier = Modifier.size(16.dp),
              )
              Text(
                text = stringResource(R.string.plaza_notice_pill),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = IndigoDeep,
              )
            }
          }
        }
        Box(
          modifier = Modifier.fillMaxWidth().height(1.dp).background(LightOutlineVariant.copy(alpha = 0.7f))
        )
      }
    }

    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 600.dp).testTag("plaza_lazy_list"),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // 16:9 Featured Carousel
      item {
        Box(
          modifier =
            Modifier.fillMaxWidth()
              .aspectRatio(16f / 9f)
              .clip(RoundedCornerShape(20.dp)),
        ) {
          HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
          ) { page ->
            val slide = slides[page]
            PlazaBannerSlideCard(
              slide = slide,
              onClick = {
                when (slide.targetAction) {
                  "notice" -> onOpenNoticeModal()
                  "scripts" -> onOpenScripts()
                  "bounty" -> onOpenBounty(BountyPanel.CUSTOM)
                  else -> onShowToast("「浮空岛新春灯会 · v2.6」筹备中，敬请期待")
                }
              },
            )
          }

          Row(
            modifier = Modifier.align(Alignment.BottomEnd).padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            repeat(slides.size) { index ->
              val selected = pagerState.currentPage == index
              Box(
                modifier =
                  Modifier.height(6.dp)
                    .width(if (selected) 16.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (selected) Color.White else Color.White.copy(alpha = 0.45f))
                    .clickable { scope.launch { pagerState.animateScrollToPage(index) } },
              )
            }
          }
        }
      }

      // 4-Column Core Channel Grid
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          PlazaGridShortcutCard(
            title = stringResource(R.string.plaza_grid_rankings_title),
            subtitle = stringResource(R.string.plaza_grid_rankings_sub),
            onClick = onOpenRankings,
            modifier = Modifier.weight(1f).testTag("plaza_grid_rankings"),
          ) { PlazaRankingsTileIcon() }

          PlazaGridShortcutCard(
            title = stringResource(R.string.plaza_grid_bounty_title),
            subtitle = stringResource(R.string.plaza_grid_bounty_sub),
            onClick = { onOpenBounty(BountyPanel.CUSTOM) },
            modifier = Modifier.weight(1f).testTag("plaza_grid_bounty"),
          ) { PlazaBountyTileIcon() }

          PlazaGridShortcutCard(
            title = stringResource(R.string.plaza_grid_scripts_title),
            subtitle = stringResource(R.string.plaza_grid_scripts_sub),
            onClick = onOpenScripts,
            modifier = Modifier.weight(1f).testTag("plaza_grid_scripts"),
          ) { PlazaScriptsTileIcon() }

          PlazaGridShortcutCard(
            title = stringResource(R.string.plaza_grid_relay_title),
            subtitle = stringResource(R.string.plaza_grid_relay_sub),
            onClick = onOpenRelayReviews,
            modifier = Modifier.weight(1f).testTag("plaza_grid_relay"),
          ) { PlazaRelayTileIcon() }
        }
      }

      // 2-Column Secondary Tools
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          PlazaToolQuickCard(
            title = stringResource(R.string.plaza_tool_phone_title),
            subtitle = stringResource(R.string.plaza_tool_phone_sub),
            iconBg = LightSurfaceContainer,
            iconTint = LightOnSurfaceVariant,
            icon = { Icon(Icons.Outlined.Smartphone, contentDescription = null, tint = LightOnSurfaceVariant, modifier = Modifier.size(18.dp)) },
            onClick = onOpenEmulators,
            modifier = Modifier.weight(1f).testTag("plaza_tool_phone"),
          )
          PlazaToolQuickCard(
            title = stringResource(R.string.plaza_tool_armor_title),
            subtitle = stringResource(R.string.plaza_tool_armor_sub),
            iconBg = AmberTintBg,
            iconTint = AmberTintDark,
            icon = { Icon(Icons.Outlined.LocalMall, contentDescription = null, tint = AmberTintDark, modifier = Modifier.size(18.dp)) },
            onClick = onOpenMarket,
            modifier = Modifier.weight(1f).testTag("plaza_tool_armor"),
          )
        }
      }

      // Tag Filter Bar + Tag Finder Entry
      item {
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
              Surface(
                shape = CircleShape,
                color = LightSurface,
                modifier =
                  Modifier.border(1.dp, LightOutlineVariant, CircleShape)
                    .clip(CircleShape)
                    .clickable(onClick = onOpenCategorySheet)
                    .testTag("plaza_category_filter_btn"),
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                  Icon(Icons.Outlined.Tune, contentDescription = null, tint = LightOnSurfaceVariant, modifier = Modifier.size(14.dp))
                  Text(
                    text = if (uiState.activeKind == "all") stringResource(R.string.plaza_filter_all) else uiState.activeKind,
                    style = MaterialTheme.typography.labelSmall,
                    color = LightOnSurface,
                  )
                }
              }

              if (uiState.activeTag != "全部" || uiState.activeKind != "all") {
                Surface(
                  shape = CircleShape,
                  color = SkyTintBg,
                  modifier = Modifier.clip(CircleShape).clickable(onClick = onResetFilters),
                ) {
                  Text(
                    text = stringResource(R.string.plaza_tag_reset),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = LightSecondary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                  )
                }
              }
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier =
                Modifier.clip(RoundedCornerShape(8.dp))
                  .clickable { onOpenTagFinder(null) }
                  .padding(horizontal = 4.dp, vertical = 4.dp)
                  .testTag("plaza_tag_finder_link"),
            ) {
              Text(
                text = stringResource(R.string.plaza_tag_finder_link),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = LightSecondary,
              )
              Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = LightSecondary,
                modifier = Modifier.size(16.dp),
              )
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            PlazaSeedData.plazaTags.forEach { tag ->
              val selected = uiState.activeTag == tag
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
                    .clickable { onSelectTag(tag) }
                    .testTag("plaza_tag_chip_$tag"),
              ) {
                Text(
                  text = tag,
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                )
              }
            }
          }
        }
      }

      // Section: 最新发布
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = stringResource(R.string.plaza_section_latest),
            style = MaterialTheme.typography.titleMedium,
            color = LightOnSurface,
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
              Modifier.clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onJumpToHomeTab)
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .testTag("plaza_more_works_btn"),
          ) {
            Text(
              text = stringResource(R.string.plaza_section_more_works),
              style = MaterialTheme.typography.labelSmall,
              color = LightOnSurfaceVariant,
            )
            Icon(
              imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
              contentDescription = null,
              tint = LightOnSurfaceVariant,
              modifier = Modifier.size(16.dp),
            )
          }
        }
      }

      if (uiState.filteredPlazaWorks.isEmpty()) {
        item {
          PlazaEmptyHintCard(
            message = "该标签下暂无最新作品",
            onReset = onResetFilters,
          )
        }
      } else {
        items(uiState.filteredPlazaWorks, key = { it.id }) { work ->
          PlazaLatestWorkCard(
            work = work,
            onClick = { onOpenArticleDetail(work.storyRouteId) },
          )
        }
      }

      // Section: 精选活动
      item {
        Text(
          text = stringResource(R.string.plaza_section_featured),
          style = MaterialTheme.typography.titleMedium,
          color = LightOnSurface,
          modifier = Modifier.padding(top = 4.dp),
        )
      }

      if (uiState.filteredPlazaActivities.isEmpty()) {
        item {
          PlazaEmptyHintCard(
            message = "当前筛选下暂无活动",
            onReset = onResetFilters,
          )
        }
      } else {
        items(uiState.filteredPlazaActivities, key = { it.id }) { activity ->
          PlazaFeaturedActivityCard(
            activity = activity,
            onClick = {
              when (activity.targetRoute) {
                "rankings" -> onOpenRankings()
                "bounty_custom" -> onOpenBounty(BountyPanel.CUSTOM)
                "bounty_task" -> onOpenBounty(BountyPanel.BOUNTY)
                "scripts" -> onOpenScripts()
              }
            },
          )
        }
      }
    }
  }

  if (uiState.isNoticeModalOpen) {
    PlazaNoticeDialog(onDismiss = onDismissNoticeModal)
  }

  if (uiState.isCategorySheetOpen) {
    PlazaCategoryFilterSheet(
      activeKind = uiState.activeKind,
      onSelectKind = onSelectKind,
      onDismiss = onDismissCategorySheet,
    )
  }
}

@Composable
private fun PlazaBannerSlideCard(
  slide: PlazaBannerSlide,
  onClick: () -> Unit,
) {
  Box(
    modifier =
      Modifier.fillMaxSize()
        .background(
          Brush.linearGradient(
            colors = listOf(DarkHeroBg, DarkHeroMid, DarkHeroEnd)
          )
        )
        .clickable(onClick = onClick)
        .padding(20.dp),
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Bottom,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 8.dp),
      ) {
        Surface(shape = CircleShape, color = LightSecondary) {
          Text(
            text = slide.badge,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
          )
        }
        Text(
          text = slide.meta,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = DarkOnSurface.copy(alpha = 0.72f),
        )
      }
      Text(
        text = slide.title,
        style = MaterialTheme.typography.headlineMedium,
        color = DarkOnSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = slide.subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = DarkOnSurface.copy(alpha = 0.78f),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Composable
private fun PlazaGridShortcutCard(
  title: String,
  subtitle: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  iconContent: @Composable () -> Unit,
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = LightSurface,
    shadowElevation = 1.dp,
    modifier =
      modifier
        .border(1.dp, LightOutlineVariant.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
        .clip(RoundedCornerShape(16.dp))
        .clickable(onClick = onClick),
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      iconContent()
      Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
        color = LightOnSurface,
        maxLines = 1,
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
        color = LightOnSurfaceVariant,
        maxLines = 1,
      )
    }
  }
}

@Composable
private fun PlazaToolQuickCard(
  title: String,
  subtitle: String,
  iconBg: Color,
  iconTint: Color,
  icon: @Composable () -> Unit,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = LightSurface,
    modifier =
      modifier
        .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
        .clip(RoundedCornerShape(16.dp))
        .clickable(onClick = onClick),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Box(
        modifier = Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(iconBg),
        contentAlignment = Alignment.Center,
      ) {
        icon()
      }
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
          color = LightOnSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
          color = LightOnSurfaceVariant,
          maxLines = 1,
        )
      }
    }
  }
}

@Composable
private fun PlazaLatestWorkCard(
  work: PlazaLatestWork,
  onClick: () -> Unit,
) {
  Surface(
    shape = RoundedCornerShape(18.dp),
    color = LightSurface,
    modifier =
      Modifier.fillMaxWidth()
        .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
        .clip(RoundedCornerShape(18.dp))
        .clickable(onClick = onClick)
        .testTag("plaza_work_${work.id}"),
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      if (work.coverRes != null) {
        Image(
          painter = painterResource(id = work.coverRes),
          contentDescription = work.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.size(width = 96.dp, height = 64.dp).clip(RoundedCornerShape(12.dp)),
        )
      }
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(5.dp),
      ) {
        Text(
          text = work.title,
          style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
          color = LightOnSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Text(
            text = work.author,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = LightOnSurfaceVariant,
          )
          work.tags.forEach { t ->
            Surface(shape = CircleShape, color = LightSurfaceContainer) {
              Text(
                text = t,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = LightOnSurfaceVariant,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
              )
            }
          }
        }
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Icon(
            imageVector = Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            tint = LightOnSurfaceVariant,
            modifier = Modifier.size(13.dp),
          )
          Text(
            text = work.likesText,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = LightOnSurfaceVariant,
          )
        }
      }
    }
  }
}

@Composable
private fun PlazaFeaturedActivityCard(
  activity: PlazaActivityItem,
  onClick: () -> Unit,
) {
  val (kindBg, kindFg, actionBg, actionFg) =
    when (activity.kind) {
      "榜单" -> listOf(AmberTintBg, AmberTintDark, LightSurfaceContainer, LightOnSurface)
      "定制" -> listOf(SkyTintBg, SkyTintDark, SkyTintBg, LightSecondary)
      "悬赏" -> listOf(EmeraldSoftBg, EmeraldDarkText, LightPrimary, LightOnPrimary)
      else -> listOf(VioletTintBg, VioletDeep, VioletDeep, Color.White)
    }

  Surface(
    shape = RoundedCornerShape(18.dp),
    color = LightSurface,
    modifier =
      Modifier.fillMaxWidth()
        .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
        .clip(RoundedCornerShape(18.dp))
        .clickable(onClick = onClick)
        .testTag("plaza_activity_${activity.id}"),
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(5.dp),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Surface(shape = CircleShape, color = kindBg) {
            Text(
              text = activity.kind,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
              color = kindFg,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            )
          }
          Text(
            text = activity.tag,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = LightOnSurfaceVariant,
          )
        }
        Text(
          text = activity.title,
          style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
          color = LightOnSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = activity.meta,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = LightOnSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }

      Surface(
        shape = CircleShape,
        color = actionBg,
        contentColor = actionFg,
      ) {
        Text(
          text = activity.actionLabel,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
        )
      }
    }
  }
}

@Composable
private fun PlazaEmptyHintCard(
  message: String,
  onReset: () -> Unit,
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = LightSurface,
    modifier = Modifier.fillMaxWidth().border(1.dp, LightOutlineVariant, RoundedCornerShape(16.dp)),
  ) {
    Column(
      modifier = Modifier.padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Text(text = message, style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
      Surface(
        shape = CircleShape,
        color = LightPrimary,
        contentColor = LightOnPrimary,
        modifier = Modifier.clip(CircleShape).clickable(onClick = onReset),
      ) {
        Text(
          text = stringResource(R.string.plaza_tag_reset),
          style = MaterialTheme.typography.labelSmall,
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        )
      }
    }
  }
}

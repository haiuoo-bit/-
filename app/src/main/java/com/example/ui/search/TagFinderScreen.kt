package com.example.ui.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.home.OverlayToastPill
import com.example.ui.plaza.CreatorVBadge
import com.example.ui.plaza.PlazaSeedData
import com.example.ui.plaza.PlazaUiState
import com.example.ui.plaza.TagWorkSort
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
import com.example.ui.theme.SkyTintBg
import com.example.ui.theme.SkyTintDark

@Composable
fun TagFinderScreen(
  uiState: PlazaUiState,
  onBack: () -> Unit,
  onSelectCategory: (String) -> Unit,
  onSearchQueryChange: (String) -> Unit,
  onOpenTagDetail: (String?) -> Unit,
  onSelectWorkSort: (TagWorkSort) -> Unit,
  onOpenArticleDetail: (String) -> Unit,
  onShowToast: (String) -> Unit,
) {
  BackHandler(enabled = uiState.selectedTagDetail != null) {
    onOpenTagDetail(null)
  }

  Box(
    modifier = Modifier.fillMaxSize().background(LightSurface),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(modifier = Modifier.fillMaxSize().widthIn(max = 600.dp)) {
      // Header + Search Input
      Column(
        modifier = Modifier.fillMaxWidth().background(LightSurface).statusBarsPadding()
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().height(54.dp).padding(horizontal = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          IconButton(
            onClick = onBack,
            modifier = Modifier.size(48.dp).testTag("tag_finder_back_btn"),
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back_home), tint = LightOnSurface)
          }
          Text(
            text = stringResource(R.string.tag_finder_title),
            style = MaterialTheme.typography.headlineMedium,
            color = LightOnSurface,
          )
        }

        Row(
          modifier =
            Modifier.fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 8.dp)
              .height(40.dp)
              .clip(CircleShape)
              .background(LightSurfaceContainer)
              .padding(horizontal = 14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Icon(Icons.Outlined.Search, contentDescription = null, tint = LightOnSurfaceVariant, modifier = Modifier.size(18.dp))
          BasicTextField(
            value = uiState.tagSearchQuery,
            onValueChange = onSearchQueryChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall.copy(color = LightOnSurface),
            cursorBrush = SolidColor(LightSecondary),
            modifier = Modifier.weight(1f).testTag("tag_finder_search_input"),
            decorationBox = { inner ->
              if (uiState.tagSearchQuery.isEmpty()) {
                Text(
                  text = stringResource(R.string.tag_finder_search_hint),
                  style = MaterialTheme.typography.bodySmall,
                  color = LightOnSurfaceVariant,
                )
              }
              inner()
            },
          )
          if (uiState.tagSearchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchQueryChange("") }, modifier = Modifier.size(28.dp)) {
              Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close), tint = LightOnSurfaceVariant, modifier = Modifier.size(16.dp))
            }
          }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(LightOutlineVariant.copy(alpha = 0.7f)))
      }

      // Split Layout: Left Category Rail + Right Tag Cards Grid
      Row(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
        Column(
          modifier =
            Modifier.width(96.dp)
              .fillMaxHeight()
              .background(LightSurfaceContainerLow)
              .verticalScroll(rememberScrollState()),
        ) {
          PlazaSeedData.tagCategories.forEach { cat ->
            val selected = uiState.tagSearchQuery.isBlank() && uiState.activeTagCategory == cat
            Row(
              modifier =
                Modifier.fillMaxWidth()
                  .background(if (selected) LightSurface else Color.Transparent)
                  .clickable {
                    onSearchQueryChange("")
                    onSelectCategory(cat)
                  }
                  .padding(vertical = 15.dp, horizontal = 8.dp)
                  .testTag("tag_cat_$cat"),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Box(
                modifier =
                  Modifier.width(3.dp)
                    .height(16.dp)
                    .clip(CircleShape)
                    .background(if (selected) LightSecondary else Color.Transparent),
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = cat,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 12.sp,
                  fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                ),
                color = if (selected) LightOnSurface else LightOnSurfaceVariant,
              )
            }
          }
        }

        val gridTags = uiState.filteredTagsGrid
        if (gridTags.isEmpty()) {
          Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
            Text("没有匹配的标签关键词", style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
          }
        } else {
          LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f).fillMaxHeight(),
            contentPadding = PaddingValues(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            items(gridTags, key = { it.id }) { tag ->
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = LightSurfaceContainerLow,
                modifier =
                  Modifier.fillMaxWidth()
                    .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onOpenTagDetail(tag.name) }
                    .testTag("tag_card_${tag.name}"),
              ) {
                Column(
                  modifier = Modifier.padding(12.dp),
                  verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Text(
                      text = "# ${tag.name}",
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                      color = LightOnSurface,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                    if (tag.hot) {
                      Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(ErrorRed))
                    }
                  }
                  Text(
                    text = "${tag.count} 篇设定",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = LightOnSurfaceVariant,
                  )
                }
              }
            }
          }
        }
      }
    }

    // Sub-view: #Tag Aggregation Works
    val activeTag = uiState.selectedTagDetail
    if (activeTag != null) {
      TagWorksAggregationOverlay(
        tagName = activeTag,
        uiState = uiState,
        onClose = { onOpenTagDetail(null) },
        onSelectSort = onSelectWorkSort,
        onSwitchTag = { onOpenTagDetail(it) },
        onReadWork = onOpenArticleDetail,
        onPlayWork = { title -> onShowToast("已为《${title.removeSurrounding("《", "》")}》初始化演绎档案，即将进入对话") },
      )
    }

    OverlayToastPill(
      message = uiState.toastMessage,
      darkPolarity = false,
      modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 24.dp),
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagWorksAggregationOverlay(
  tagName: String,
  uiState: PlazaUiState,
  onClose: () -> Unit,
  onSelectSort: (TagWorkSort) -> Unit,
  onSwitchTag: (String) -> Unit,
  onReadWork: (String) -> Unit,
  onPlayWork: (String) -> Unit,
) {
  val works = uiState.filteredTagDetailWorks
  Column(
    modifier = Modifier.fillMaxSize().background(LightSurfaceContainerLow).widthIn(max = 600.dp),
  ) {
    Surface(color = LightSurface, modifier = Modifier.fillMaxWidth()) {
      Column(modifier = Modifier.statusBarsPadding()) {
        Row(
          modifier = Modifier.fillMaxWidth().height(54.dp).padding(horizontal = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose, modifier = Modifier.size(48.dp).testTag("tag_detail_back_btn")) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_close), tint = LightOnSurface)
            }
            Column {
              Text(text = "#$tagName", style = MaterialTheme.typography.headlineMedium, color = LightOnSurface)
              Text(
                text = stringResource(R.string.tag_finder_works_count_format, works.size),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = LightOnSurfaceVariant,
              )
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          TagWorkSort.entries.forEach { sort ->
            val selected = uiState.tagWorkSort == sort
            Surface(
              shape = CircleShape,
              color = if (selected) LightPrimary else LightSurfaceContainer,
              contentColor = if (selected) LightOnPrimary else LightOnSurfaceVariant,
              modifier = Modifier.clip(CircleShape).clickable { onSelectSort(sort) },
            ) {
              Text(
                text = sort.label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              )
            }
          }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(LightOutlineVariant.copy(alpha = 0.7f)))
      }
    }

    LazyColumn(
      modifier = Modifier.fillMaxSize().navigationBarsPadding(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      items(works, key = { it.id }) { work ->
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = LightSurface,
          modifier =
            Modifier.fillMaxWidth()
              .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
              .testTag("tag_work_${work.id}"),
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
              Image(
                painter = painterResource(id = work.coverRes),
                contentDescription = work.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(width = 88.dp, height = 68.dp).clip(RoundedCornerShape(12.dp)),
              )
              Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = work.title, style = MaterialTheme.typography.titleMedium, color = LightOnSurface)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text(text = work.author, style = MaterialTheme.typography.labelSmall, color = LightOnSurfaceVariant)
                  CreatorVBadge(tier = work.vTier, size = 14.dp)
                }
                Text(
                  text = work.desc,
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                  color = LightOnSurfaceVariant,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis,
                )
              }
            }

            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              work.tags.forEach { t ->
                val isCurrent = t == tagName
                Surface(
                  shape = CircleShape,
                  color = if (isCurrent) SkyTintBg else LightSurfaceContainer,
                  modifier = Modifier.clip(CircleShape).clickable { onSwitchTag(t) },
                ) {
                  Text(
                    text = "#$t",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = if (isCurrent) SkyTintDark else LightOnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                  )
                }
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Outlined.FavoriteBorder, contentDescription = null, tint = LightOnSurfaceVariant, modifier = Modifier.size(14.dp))
                  Text("${work.likes}", style = MaterialTheme.typography.labelSmall, color = LightOnSurfaceVariant)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Outlined.Group, contentDescription = null, tint = LightOnSurfaceVariant, modifier = Modifier.size(14.dp))
                  Text("${work.players}人在玩", style = MaterialTheme.typography.labelSmall, color = LightOnSurfaceVariant)
                }
              }

              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                  shape = CircleShape,
                  color = LightSurfaceContainer,
                  contentColor = LightOnSurface,
                  modifier = Modifier.clip(CircleShape).clickable { onReadWork(work.storyRouteId) },
                ) {
                  Text(
                    text = stringResource(R.string.tag_finder_read_btn),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                  )
                }
                Surface(
                  shape = CircleShape,
                  color = LightSecondary,
                  contentColor = Color.White,
                  modifier = Modifier.clip(CircleShape).clickable { onPlayWork(work.title) },
                ) {
                  Text(
                    text = stringResource(R.string.tag_finder_play_btn),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
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

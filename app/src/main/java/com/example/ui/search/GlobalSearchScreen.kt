package com.example.ui.search

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.home.OverlayToastPill
import com.example.ui.plaza.CreatorVBadge
import com.example.ui.plaza.GroupJoinMode
import com.example.ui.plaza.GroupJoinStatus
import com.example.ui.plaza.PlazaSeedData
import com.example.ui.plaza.PlazaUiState
import com.example.ui.plaza.SearchScope
import com.example.ui.theme.AmberTintBg
import com.example.ui.theme.AmberTintDark
import com.example.ui.theme.EmeraldDarkText
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightOutlineVariant
import com.example.ui.theme.LightPrimary
import com.example.ui.theme.LightSecondary
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceContainer
import com.example.ui.theme.LightSurfaceContainerLow
import com.example.ui.theme.RoseTintBg
import com.example.ui.theme.RoseTintDark
import com.example.ui.theme.SearchHighlightYellow
import com.example.ui.theme.SkyTintBg
import com.example.ui.theme.SkyTintDark
import com.example.ui.theme.VioletDeep
import com.example.ui.theme.VioletTintBg

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GlobalSearchScreen(
  uiState: PlazaUiState,
  onBack: () -> Unit,
  onQueryChange: (String) -> Unit,
  onSubmitSearch: (String) -> Unit,
  onSelectScope: (SearchScope) -> Unit,
  onRemoveRecent: (String) -> Unit,
  onClearRecent: () -> Unit,
  onToggleGroupJoin: (String) -> Unit,
  onToggleCreatorFollow: (String) -> Unit,
  onOpenArticleDetail: (String) -> Unit,
  onOpenTagFinder: () -> Unit,
  onOpenScripts: () -> Unit,
  onOpenBounty: () -> Unit,
  onShowToast: (String) -> Unit,
) {
  val query = uiState.searchQuery.trim()
  val scope = uiState.searchScope

  val matchedGroups =
    remember(uiState.searchGroups, query, scope) {
      if (query.isEmpty() || (scope != SearchScope.ALL && scope != SearchScope.GROUPS)) emptyList()
      else uiState.searchGroups.filter {
        it.groupNumber.contains(query, true) || it.name.contains(query, true) || it.intro.contains(query, true)
      }
    }
  val matchedWorks =
    remember(uiState.searchWorks, query, scope) {
      if (query.isEmpty() || (scope != SearchScope.ALL && scope != SearchScope.WORKS)) emptyList()
      else uiState.searchWorks.filter { w ->
        w.title.contains(query, true) || w.author.contains(query, true) || w.summary.contains(query, true) || w.tags.any { it.contains(query, true) }
      }
    }
  val matchedChats =
    remember(uiState.searchChats, query, scope) {
      if (query.isEmpty() || scope != SearchScope.ALL) emptyList()
      else uiState.searchChats.filter { it.title.contains(query, true) || it.snippet.contains(query, true) }
    }
  val matchedCreators =
    remember(uiState.searchCreators, query, scope) {
      if (query.isEmpty() || (scope != SearchScope.ALL && scope != SearchScope.CREATORS)) emptyList()
      else uiState.searchCreators.filter { it.name.contains(query, true) || it.bio.contains(query, true) }
    }
  val matchedPosts =
    remember(uiState.searchPosts, query, scope) {
      if (query.isEmpty() || (scope != SearchScope.ALL && scope != SearchScope.COMMUNITY)) emptyList()
      else uiState.searchPosts.filter { it.author.contains(query, true) || it.content.contains(query, true) }
    }
  val hasAnyMatch =
    matchedGroups.isNotEmpty() || matchedWorks.isNotEmpty() || matchedChats.isNotEmpty() ||
      matchedCreators.isNotEmpty() || matchedPosts.isNotEmpty()

  Scaffold(
    containerColor = LightSurfaceContainerLow,
    topBar = {
      Surface(color = LightSurface, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.statusBarsPadding()) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Row(
              modifier =
                Modifier.weight(1f)
                  .height(40.dp)
                  .clip(CircleShape)
                  .background(LightSurfaceContainer)
                  .padding(horizontal = 12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(Icons.Outlined.Search, contentDescription = null, tint = LightOnSurfaceVariant, modifier = Modifier.size(18.dp))
              BasicTextField(
                value = uiState.searchQuery,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(color = LightOnSurface),
                cursorBrush = SolidColor(LightSecondary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSubmitSearch(uiState.searchQuery) }),
                modifier = Modifier.weight(1f).testTag("global_search_input"),
                decorationBox = { inner ->
                  if (uiState.searchQuery.isEmpty()) {
                    Text(
                      text = stringResource(R.string.search_global_hint),
                      style = MaterialTheme.typography.bodySmall,
                      color = LightOnSurfaceVariant,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                  }
                  inner()
                },
              )
              if (uiState.searchQuery.isNotEmpty()) {
                IconButton(
                  onClick = { onQueryChange("") },
                  modifier = Modifier.size(28.dp).testTag("global_search_clear_input"),
                ) {
                  Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close), tint = LightOnSurfaceVariant, modifier = Modifier.size(16.dp))
                }
              }
            }

            Text(
              text = stringResource(R.string.search_cancel),
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
              color = LightOnSurfaceVariant,
              modifier =
                Modifier.clip(RoundedCornerShape(8.dp))
                  .clickable(onClick = onBack)
                  .padding(horizontal = 6.dp, vertical = 8.dp)
                  .testTag("global_search_cancel_btn"),
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
          ) {
            SearchScope.entries.forEach { tab ->
              val selected = scope == tab
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                  Modifier.clickable { onSelectScope(tab) }
                    .padding(vertical = 8.dp)
                    .testTag("search_scope_${tab.name}"),
              ) {
                Text(
                  text = tab.label,
                  style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                  ),
                  color = if (selected) LightOnSurface else LightOnSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                  modifier =
                    Modifier.width(20.dp)
                      .height(2.5.dp)
                      .clip(CircleShape)
                      .background(if (selected) LightPrimary else Color.Transparent),
                )
              }
            }
          }
          Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(LightOutlineVariant.copy(alpha = 0.7f)))
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        if (query.isEmpty()) {
          // Pre-search state: Recent + Hot + Category shortcuts
          if (uiState.recentSearches.isNotEmpty()) {
            item {
              Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Text(
                    text = stringResource(R.string.search_recent_title),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = LightOnSurface,
                  )
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier =
                      Modifier.clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onClearRecent)
                        .padding(4.dp)
                        .testTag("search_clear_recent_btn"),
                  ) {
                    Icon(Icons.Outlined.DeleteOutline, contentDescription = null, tint = LightOnSurfaceVariant, modifier = Modifier.size(14.dp))
                    Text(
                      text = stringResource(R.string.search_clear),
                      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                      color = LightOnSurfaceVariant,
                    )
                  }
                }

                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  uiState.recentSearches.forEach { term ->
                    Surface(
                      shape = CircleShape,
                      color = LightSurface,
                      modifier =
                        Modifier.border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), CircleShape)
                          .clip(CircleShape)
                          .clickable { onSubmitSearch(term) }
                          .testTag("recent_chip_$term"),
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(start = 12.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                      ) {
                        Text(text = term, style = MaterialTheme.typography.bodySmall, color = LightOnSurface)
                        Icon(
                          imageVector = Icons.Filled.Close,
                          contentDescription = stringResource(R.string.cd_close),
                          tint = LightOnSurfaceVariant,
                          modifier = Modifier.size(13.dp).clickable { onRemoveRecent(term) },
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Text(
                  text = stringResource(R.string.search_hot_title),
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                  color = LightOnSurface,
                )
                Surface(shape = CircleShape, color = RoseTintBg) {
                  Text(
                    text = stringResource(R.string.search_hot_tag),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = RoseTintDark,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                  )
                }
              }

              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PlazaSeedData.hotSearches.chunked(2).forEach { pair ->
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                  ) {
                    pair.forEach { hot ->
                      Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = LightSurface,
                        modifier =
                          Modifier.weight(1f)
                            .border(1.dp, LightOutlineVariant.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSubmitSearch(hot.term.removePrefix("#")) }
                            .testTag("hot_search_${hot.rank}"),
                      ) {
                        Row(
                          modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                          Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f),
                          ) {
                            Text(
                              text = hot.rank.toString(),
                              style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                              color = if (hot.rank <= 3) RoseTintDark else LightOnSurfaceVariant,
                            )
                            Text(
                              text = hot.term,
                              style = MaterialTheme.typography.bodySmall,
                              color = LightOnSurface,
                              maxLines = 1,
                              overflow = TextOverflow.Ellipsis,
                            )
                          }
                          Surface(shape = RoundedCornerShape(5.dp), color = if (hot.isHotTag) RoseTintBg else LightSurfaceContainer) {
                            Text(
                              text = hot.tag,
                              style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                              color = if (hot.isHotTag) RoseTintDark else LightOnSurfaceVariant,
                              modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
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

          item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = stringResource(R.string.search_category_title),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = LightOnSurface,
              )
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SearchQuickCategoryCard(
                  title = "标签找设定",
                  iconBg = SkyTintBg,
                  icon = { Icon(Icons.Outlined.LocalOffer, contentDescription = null, tint = SkyTintDark, modifier = Modifier.size(18.dp)) },
                  onClick = onOpenTagFinder,
                  modifier = Modifier.weight(1f),
                )
                SearchQuickCategoryCard(
                  title = "剧本局房间",
                  iconBg = VioletTintBg,
                  icon = { Icon(Icons.Outlined.SportsEsports, contentDescription = null, tint = VioletDeep, modifier = Modifier.size(18.dp)) },
                  onClick = onOpenScripts,
                  modifier = Modifier.weight(1f),
                )
                SearchQuickCategoryCard(
                  title = "悬赏任务",
                  iconBg = AmberTintBg,
                  icon = { Icon(Icons.Outlined.AttachMoney, contentDescription = null, tint = AmberTintDark, modifier = Modifier.size(18.dp)) },
                  onClick = onOpenBounty,
                  modifier = Modifier.weight(1f),
                )
              }
            }
          }
        } else if (!hasAnyMatch) {
          item {
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = LightSurface,
              modifier = Modifier.fillMaxWidth().border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(20.dp)),
            ) {
              Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = LightOnSurfaceVariant, modifier = Modifier.size(34.dp))
                Text(text = stringResource(R.string.search_empty_title), style = MaterialTheme.typography.titleSmall, color = LightOnSurface)
                Text(
                  text = stringResource(R.string.search_empty_sub),
                  style = MaterialTheme.typography.bodySmall,
                  color = LightOnSurfaceVariant,
                )
                Surface(
                  shape = CircleShape,
                  color = SkyTintBg,
                  modifier = Modifier.padding(top = 4.dp).clip(CircleShape).clickable { onQueryChange("") },
                ) {
                  Text(
                    text = "清空关键词，返回搜索首页",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = SkyTintDark,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                  )
                }
              }
            }
          }
        } else {
          // Grouped live results
          if (matchedGroups.isNotEmpty()) {
            item {
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                  text = "群聊与群号 (${matchedGroups.size})",
                  style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                  color = LightOnSurfaceVariant,
                )
                matchedGroups.forEach { group ->
                  Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = LightSurface,
                    modifier =
                      Modifier.fillMaxWidth()
                        .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                        .testTag("search_group_${group.id}"),
                  ) {
                    Row(
                      modifier = Modifier.padding(14.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                      Box(
                        modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(SkyTintBg),
                        contentAlignment = Alignment.Center,
                      ) {
                        Icon(Icons.Outlined.Group, contentDescription = null, tint = SkyTintDark, modifier = Modifier.size(20.dp))
                      }
                      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(text = highlightMatch(group.name, query), style = MaterialTheme.typography.titleSmall, color = LightOnSurface)
                        Text(
                          text = highlightMatch("群号 ${group.groupNumber} · ${group.membersCount} 人 · ${group.intro}", query),
                          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                          color = LightOnSurfaceVariant,
                          maxLines = 2,
                          overflow = TextOverflow.Ellipsis,
                        )
                      }
                      val (btnText, btnBg, btnFg) =
                        when (group.status) {
                          GroupJoinStatus.JOINED -> Triple(stringResource(R.string.search_group_joined), EmeraldSoftBg, EmeraldDarkText)
                          GroupJoinStatus.PENDING -> Triple(stringResource(R.string.search_group_pending), AmberTintBg, AmberTintDark)
                          GroupJoinStatus.IDLE ->
                            if (group.joinMode == GroupJoinMode.DIRECT) {
                              Triple(stringResource(R.string.search_group_join), LightSecondary, Color.White)
                            } else {
                              Triple(stringResource(R.string.search_group_apply), LightSurfaceContainer, LightOnSurface)
                            }
                        }
                      Surface(
                        shape = CircleShape,
                        color = btnBg,
                        contentColor = btnFg,
                        modifier =
                          Modifier.clip(CircleShape)
                            .clickable(enabled = group.status == GroupJoinStatus.IDLE) { onToggleGroupJoin(group.id) }
                            .testTag("search_group_join_${group.id}"),
                      ) {
                        Text(
                          text = btnText,
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

          if (matchedWorks.isNotEmpty()) {
            item {
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                  text = "相关作品设定 (${matchedWorks.size})",
                  style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                  color = LightOnSurfaceVariant,
                )
                matchedWorks.forEach { work ->
                  Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = LightSurface,
                    modifier =
                      Modifier.fillMaxWidth()
                        .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onOpenArticleDetail(work.storyRouteId) }
                        .testTag("search_work_${work.id}"),
                  ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = highlightMatch(work.title, query), style = MaterialTheme.typography.titleSmall, color = LightOnSurface)
                        Text(text = work.author, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = LightOnSurfaceVariant)
                      }
                      Text(
                        text = highlightMatch(work.summary, query),
                        style = MaterialTheme.typography.bodySmall,
                        color = LightOnSurfaceVariant,
                      )
                      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        work.tags.forEach { tag ->
                          Surface(shape = CircleShape, color = LightSurfaceContainer) {
                            Text(
                              text = tag,
                              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                              color = LightSecondary,
                              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
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

          if (matchedChats.isNotEmpty()) {
            item {
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                  text = "对话与演绎 (${matchedChats.size})",
                  style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                  color = LightOnSurfaceVariant,
                )
                matchedChats.forEach { chat ->
                  Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = LightSurface,
                    modifier =
                      Modifier.fillMaxWidth()
                        .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onOpenArticleDetail(chat.storyRouteId) },
                  ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                      Text(text = highlightMatch(chat.title, query), style = MaterialTheme.typography.titleSmall, color = LightOnSurface)
                      Text(text = highlightMatch(chat.snippet, query), style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
                    }
                  }
                }
              }
            }
          }

          if (matchedCreators.isNotEmpty()) {
            item {
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                  text = "相关创作者 (${matchedCreators.size})",
                  style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                  color = LightOnSurfaceVariant,
                )
                matchedCreators.forEach { creator ->
                  Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = LightSurface,
                    modifier = Modifier.fillMaxWidth().border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(18.dp)),
                  ) {
                    Row(
                      modifier = Modifier.padding(14.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                      Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(SkyTintBg),
                        contentAlignment = Alignment.Center,
                      ) {
                        Text(text = creator.name.take(1), style = MaterialTheme.typography.titleSmall, color = SkyTintDark)
                      }
                      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                          Text(text = highlightMatch(creator.name, query), style = MaterialTheme.typography.titleSmall, color = LightOnSurface)
                          CreatorVBadge(tier = creator.vTier, size = 14.dp)
                        }
                        Text(
                          text = highlightMatch("${creator.worksCount} 篇作品 · ${creator.followersText} 关注 · ${creator.bio}", query),
                          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                          color = LightOnSurfaceVariant,
                          maxLines = 2,
                        )
                      }
                      Surface(
                        shape = CircleShape,
                        color = if (creator.isFollowed) LightSurfaceContainer else LightPrimary,
                        contentColor = if (creator.isFollowed) LightOnSurface else Color.White,
                        modifier = Modifier.clip(CircleShape).clickable { onToggleCreatorFollow(creator.id) },
                      ) {
                        Text(
                          text = if (creator.isFollowed) "已关注" else "+ 关注",
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

          if (matchedPosts.isNotEmpty()) {
            item {
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                  text = "社区动态 (${matchedPosts.size})",
                  style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                  color = LightOnSurfaceVariant,
                )
                matchedPosts.forEach { post ->
                  Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = LightSurface,
                    modifier =
                      Modifier.fillMaxWidth()
                        .border(1.dp, LightOutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onShowToast("已预览 ${post.author} 的社区动态") },
                  ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = post.author, style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp), color = LightOnSurface)
                        Text(text = "${post.timeAgo} · ${post.likes} 赞", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = LightOnSurfaceVariant)
                      }
                      Text(text = highlightMatch(post.content, query), style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
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
        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp),
      )
    }
  }
}

@Composable
private fun SearchQuickCategoryCard(
  title: String,
  iconBg: Color,
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
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Box(
        modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(iconBg),
        contentAlignment = Alignment.Center,
      ) {
        icon()
      }
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
        color = LightOnSurface,
        maxLines = 1,
      )
    }
  }
}

private fun highlightMatch(text: String, query: String): AnnotatedString {
  if (query.isBlank()) return AnnotatedString(text)
  return buildAnnotatedString {
    append(text)
    var idx = 0
    while (true) {
      val found = text.indexOf(query, idx, ignoreCase = true)
      if (found < 0) break
      addStyle(
        SpanStyle(background = SearchHighlightYellow, color = LightOnSurface, fontWeight = FontWeight.SemiBold),
        start = found,
        end = found + query.length,
      )
      idx = found + query.length
    }
  }
}

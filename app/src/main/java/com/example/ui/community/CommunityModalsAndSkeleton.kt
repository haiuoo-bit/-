package com.example.ui.community

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.home.StitchHeartIcon
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.IndigoDeep
import com.example.ui.theme.LightOnPrimary
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightOutlineVariant
import com.example.ui.theme.LightPrimary
import com.example.ui.theme.LightSecondary
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceContainer
import com.example.ui.theme.LightSurfaceContainerHigh
import com.example.ui.theme.LightSurfaceContainerLow
import com.example.ui.theme.SkeletonBase
import com.example.ui.theme.SkeletonHighlight
import com.example.ui.theme.SkeletonMid
import com.example.ui.theme.SuccessGreen
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

@Composable
fun rememberSkeletonShimmerBrush(): Brush {
  val transition = rememberInfiniteTransition(label = "skeleton_sweep")
  val shift by transition.animateFloat(
    initialValue = -350f,
    targetValue = 1150f,
    animationSpec =
      infiniteRepeatable(
        animation = tween(durationMillis = 1500, easing = LinearEasing),
        repeatMode = RepeatMode.Restart,
      ),
    label = "skeleton_shift",
  )
  return Brush.linearGradient(
    colors = listOf(SkeletonBase, SkeletonMid, SkeletonHighlight, SkeletonMid, SkeletonBase),
    start = Offset(shift, 0f),
    end = Offset(shift + 380f, 140f),
  )
}

@Composable
fun CommunitySkeletonFeedCard(
  shimmerBrush: Brush,
  mediaHeightDp: Int = 176,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.fillMaxWidth().background(LightSurface).padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(shimmerBrush))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Box(modifier = Modifier.width(96.dp).height(14.dp).clip(RoundedCornerShape(6.dp)).background(shimmerBrush))
          Box(modifier = Modifier.width(140.dp).height(11.dp).clip(RoundedCornerShape(6.dp)).background(shimmerBrush))
        }
      }
      Box(modifier = Modifier.width(56.dp).height(28.dp).clip(CircleShape).background(shimmerBrush))
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Box(modifier = Modifier.fillMaxWidth().height(14.dp).clip(RoundedCornerShape(6.dp)).background(shimmerBrush))
      Box(modifier = Modifier.fillMaxWidth(0.78f).height(14.dp).clip(RoundedCornerShape(6.dp)).background(shimmerBrush))
    }

    Box(
      modifier =
        Modifier.fillMaxWidth()
          .height(mediaHeightDp.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(shimmerBrush)
    )

    Row(
      modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      repeat(4) {
        Box(modifier = Modifier.weight(1f).height(16.dp).clip(CircleShape).background(shimmerBrush))
      }
    }
  }
}

@Composable
fun StaggeredRevealBlock(
  revealed: Boolean,
  staggerIndex: Int,
  cycleKey: Int,
  content: @Composable () -> Unit,
) {
  var blockActive by remember(cycleKey) { mutableStateOf(false) }
  LaunchedEffect(revealed, cycleKey) {
    if (revealed) {
      delay((staggerIndex.coerceAtMost(8) * 60).toLong())
      blockActive = true
    } else {
      blockActive = false
    }
  }
  val alpha by animateFloatAsState(
    targetValue = if (blockActive) 1f else 0f,
    animationSpec = tween(durationMillis = 260),
    label = "reveal_alpha_$staggerIndex",
  )
  val offsetY by animateFloatAsState(
    targetValue = if (blockActive) 0f else 18f,
    animationSpec = tween(durationMillis = 260),
    label = "reveal_offset_$staggerIndex",
  )
  Box(
    modifier = Modifier.alpha(alpha).offset { IntOffset(0, offsetY.roundToInt()) }
  ) {
    content()
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityCommentDrawerSheet(
  totalCommentsCount: Int,
  comments: List<CommunityComment>,
  replyTarget: CommunityReplyTarget?,
  onDismiss: () -> Unit,
  onReplyClick: (commentId: String, authorName: String) -> Unit,
  onClearReplyTarget: () -> Unit,
  onToggleCommentLike: (commentId: String, replyId: String?) -> Unit,
  onSubmitComment: (String) -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var draftText by remember { mutableStateOf("") }
  val inputFocusRequester = remember { FocusRequester() }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = LightSurface,
    contentColor = LightOnSurface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    dragHandle = null,
    modifier = Modifier.testTag("community_comment_drawer_sheet"),
  ) {
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .fillMaxHeight(0.64f)
          .imePadding()
          .navigationBarsPadding()
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Box(
          modifier =
            Modifier.width(36.dp)
              .height(4.dp)
              .clip(CircleShape)
              .background(LightOutlineVariant)
              .align(Alignment.CenterHorizontally)
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            text = stringResource(R.string.comment_total_format, totalCommentsCount),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = LightOnSurface,
          )
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(36.dp).clip(CircleShape).background(LightSurfaceContainer),
          ) {
            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close), tint = LightOnSurfaceVariant)
          }
        }
      }

      HorizontalDivider(color = LightOutlineVariant)

      LazyColumn(
        modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        item { Spacer(Modifier.height(2.dp)) }
        items(comments, key = { it.id }) { comment ->
          CommunityCommentRow(
            comment = comment,
            onReplyClick = { author ->
              onReplyClick(comment.id, author)
              runCatching { inputFocusRequester.requestFocus() }
            },
            onToggleLike = { replyId -> onToggleCommentLike(comment.id, replyId) },
          )
        }
        item { Spacer(Modifier.height(6.dp)) }
      }

      HorizontalDivider(color = LightOutlineVariant)

      Column(
        modifier = Modifier.fillMaxWidth().background(LightSurface).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        if (replyTarget != null) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = stringResource(R.string.comment_hint_reply_format, replyTarget.authorName),
              style = MaterialTheme.typography.labelSmall,
              color = AccentBlue,
            )
            Text(
              text = "取消回复",
              style = MaterialTheme.typography.labelSmall,
              color = LightOnSurfaceVariant,
              modifier = Modifier.clickable(onClick = onClearReplyTarget).padding(4.dp),
            )
          }
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Box(
            modifier =
              Modifier.weight(1f)
                .height(40.dp)
                .clip(CircleShape)
                .background(LightSurfaceContainer)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart,
          ) {
            BasicTextField(
              value = draftText,
              onValueChange = { draftText = it },
              singleLine = true,
              textStyle = MaterialTheme.typography.bodySmall.copy(color = LightOnSurface),
              cursorBrush = SolidColor(LightSecondary),
              keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
              keyboardActions =
                KeyboardActions(
                  onSend = {
                    if (draftText.isNotBlank()) {
                      onSubmitComment(draftText)
                      draftText = ""
                    }
                  }
                ),
              modifier = Modifier.fillMaxWidth().focusRequester(inputFocusRequester).testTag("comm_drawer_input"),
              decorationBox = { inner ->
                if (draftText.isEmpty()) {
                  Text(
                    text =
                      if (replyTarget != null) {
                        stringResource(R.string.comment_hint_reply_format, replyTarget.authorName)
                      } else {
                        stringResource(R.string.comment_hint_drawer)
                      },
                    style = MaterialTheme.typography.bodySmall,
                    color = LightOnSurfaceVariant,
                  )
                }
                inner()
              },
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
                  if (draftText.isNotBlank()) {
                    onSubmitComment(draftText)
                    draftText = ""
                  }
                }
                .testTag("comm_drawer_send_btn"),
          ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
              Text(stringResource(R.string.comment_action_send), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
            }
          }
        }
      }
    }
  }
}

@Composable
fun CommunityCommentRow(
  comment: CommunityComment,
  onReplyClick: (authorName: String) -> Unit,
  onToggleLike: (replyId: String?) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.Top,
  ) {
    if (comment.avatarRes != null) {
      Image(
        painter = painterResource(id = comment.avatarRes),
        contentDescription = comment.author,
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(32.dp).clip(CircleShape),
      )
    } else {
      Box(
        modifier = Modifier.size(32.dp).clip(CircleShape).background(LightSurfaceContainerHigh),
        contentAlignment = Alignment.Center,
      ) {
        Text(comment.authorInitial, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = LightOnSurface)
      }
    }

    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(comment.author, style = MaterialTheme.typography.labelSmall, color = LightOnSurfaceVariant)
        if (comment.hotBadge) {
          Surface(shape = RoundedCornerShape(4.dp), color = LightSurfaceContainer) {
            Text(
              text = "热评",
              style = MaterialTheme.typography.labelSmall,
              color = LightOnSurfaceVariant,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
            )
          }
        }
      }
      Text(
        text = comment.content,
        style = MaterialTheme.typography.bodyMedium,
        color = LightOnSurface,
        modifier = Modifier.clickable { onReplyClick(comment.author) },
      )
      Row(
        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
          Text(comment.timestamp, style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
          Text(
            text = stringResource(R.string.comment_action_reply),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = LightOnSurfaceVariant,
            modifier = Modifier.clickable { onReplyClick(comment.author) }.padding(4.dp),
          )
        }
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { onToggleLike(null) }.padding(6.dp, 4.dp),
        ) {
          StitchHeartIcon(
            filled = comment.isLiked,
            tint = if (comment.isLiked) ErrorRed else LightOnSurfaceVariant,
            modifier = Modifier.size(16.dp),
          )
          Text(
            text = comment.likeCount.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = if (comment.isLiked) ErrorRed else LightOnSurfaceVariant,
          )
        }
      }

      if (comment.replies.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxWidth().padding(top = 6.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          comment.replies.forEach { reply ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
              if (reply.avatarRes != null) {
                Image(
                  painter = painterResource(id = reply.avatarRes),
                  contentDescription = reply.author,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.size(24.dp).clip(CircleShape),
                )
              } else {
                Box(
                  modifier = Modifier.size(24.dp).clip(CircleShape).background(LightSurfaceContainerHigh),
                  contentAlignment = Alignment.Center,
                ) {
                  Text(reply.authorInitial, style = MaterialTheme.typography.labelSmall, color = LightOnSurface)
                }
              }
              Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text(reply.author, style = MaterialTheme.typography.labelSmall, color = LightOnSurfaceVariant)
                  if (reply.isAuthorBadge) {
                    Surface(shape = RoundedCornerShape(4.dp), color = ErrorRed, contentColor = LightOnPrimary) {
                      Text(stringResource(R.string.comment_badge_author), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                  }
                  if (reply.replyToAuthor != null) {
                    Text("▸ ${reply.replyToAuthor}", style = MaterialTheme.typography.labelSmall, color = LightOnSurfaceVariant)
                  }
                }
                Text(reply.content, style = MaterialTheme.typography.bodyMedium, color = LightOnSurface, modifier = Modifier.clickable { onReplyClick(reply.author) })
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(reply.timestamp, style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
                    Text(
                      stringResource(R.string.comment_action_reply),
                      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                      color = LightOnSurfaceVariant,
                      modifier = Modifier.clickable { onReplyClick(reply.author) }.padding(2.dp),
                    )
                  }
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { onToggleLike(reply.id) }.padding(4.dp),
                  ) {
                    StitchHeartIcon(
                      filled = reply.isLiked,
                      tint = if (reply.isLiked) ErrorRed else LightOnSurfaceVariant,
                      modifier = Modifier.size(14.dp),
                    )
                    Text(reply.likeCount.toString(), style = MaterialTheme.typography.labelSmall, color = if (reply.isLiked) ErrorRed else LightOnSurfaceVariant)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityShareSheet(
  postSummary: String,
  onDismiss: () -> Unit,
  onChannelClick: (toastMsg: String) -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  @Suppress("DEPRECATION")
  val clipboardManager = LocalClipboardManager.current

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = LightSurface,
    contentColor = LightOnSurface,
    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    dragHandle = null,
    modifier = Modifier.testTag("community_share_sheet"),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Box(
        modifier = Modifier.width(40.dp).height(4.dp).clip(CircleShape).background(LightOutlineVariant).align(Alignment.CenterHorizontally)
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text("分享作品与动态", style = MaterialTheme.typography.titleMedium, color = LightOnSurface)
        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp).clip(CircleShape).background(LightSurfaceContainer)) {
          Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close), tint = LightOnSurfaceVariant)
        }
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        ShareCircleItem(Icons.Outlined.GroupAdd, "平台好友", LightPrimary, LightOnPrimary, Modifier.weight(1f)) {
          onChannelClick("已发送给平台好友")
        }
        ShareCircleItem(Icons.Outlined.ChatBubbleOutline, "微信好友", SuccessGreen, LightOnPrimary, Modifier.weight(1f)) {
          onChannelClick("已预留微信好友分享接口")
        }
        ShareCircleItem(Icons.Outlined.Mood, "QQ 好友", LightSecondary, LightOnPrimary, Modifier.weight(1f)) {
          onChannelClick("已预留 QQ 好友分享接口")
        }
        ShareCircleItem(Icons.Outlined.Link, "复制链接", LightSurfaceContainerHigh, LightOnSurface, Modifier.weight(1f)) {
          clipboardManager.setText(AnnotatedString("https://daoyu.app/community/post"))
          onChannelClick("已复制动态链接")
        }
        ShareCircleItem(Icons.Filled.AutoAwesome, stringResource(R.string.comm_share_deepseek), IndigoDeep, LightOnPrimary, Modifier.weight(1f)) {
          onChannelClick("正在通过 DeepSeek 打开并解析")
        }
      }
      Surface(
        shape = CircleShape,
        color = LightSurfaceContainer,
        contentColor = LightOnSurface,
        modifier = Modifier.fillMaxWidth().height(44.dp).clip(CircleShape).clickable(onClick = onDismiss),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(stringResource(R.string.share_cancel), style = MaterialTheme.typography.titleSmall)
        }
      }
    }
  }
}

@Composable
private fun ShareCircleItem(
  icon: ImageVector,
  label: String,
  bgColor: Color,
  iconTint: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(6.dp),
    modifier = modifier.clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick).padding(vertical = 8.dp),
  ) {
    Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(bgColor), contentAlignment = Alignment.Center) {
      Icon(icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(20.dp))
    }
    Text(label, style = MaterialTheme.typography.labelSmall, color = LightOnSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
  }
}

@Composable
fun CommunityDocumentPreviewDialog(
  document: CommunityDocument,
  onDismiss: () -> Unit,
  onShowToast: (String) -> Unit,
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = LightSurface,
      shadowElevation = 8.dp,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          CommunityDocBadgeIcon(format = document.format)
          Column(modifier = Modifier.weight(1f)) {
            Text(document.fileName, style = MaterialTheme.typography.titleSmall, color = LightOnSurface)
            Text("${document.sizeLabel} · ${document.format.desc}", style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close), tint = LightOnSurfaceVariant)
          }
        }
        Column(
          modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(LightSurfaceContainerLow).padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Text("档案内容摘要", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = LightOnSurface)
          Text(document.previewSnippet, style = MaterialTheme.typography.bodySmall, color = LightOnSurfaceVariant)
        }
        Surface(
          shape = CircleShape,
          color = LightPrimary,
          contentColor = LightOnPrimary,
          modifier =
            Modifier.fillMaxWidth()
              .height(42.dp)
              .clip(CircleShape)
              .clickable {
                onDismiss()
                onShowToast("已离线缓存 ${document.fileName}")
              },
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text("保存至我的设定库", style = MaterialTheme.typography.labelMedium)
          }
        }
      }
    }
  }
}

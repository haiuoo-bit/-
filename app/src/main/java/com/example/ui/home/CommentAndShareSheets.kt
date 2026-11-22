package com.example.ui.home

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LightOnPrimary
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightOutlineVariant
import com.example.ui.theme.LightPrimary
import com.example.ui.theme.LightSecondary
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceContainer
import com.example.ui.theme.LightSurfaceContainerHigh
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentDrawerSheet(
  totalCommentsCount: Int,
  comments: List<CommentItem>,
  replyTarget: ReplyTarget?,
  onDismiss: () -> Unit,
  onReplyClick: (commentId: String, authorName: String) -> Unit,
  onClearReplyTarget: () -> Unit,
  onToggleCommentLike: (commentId: String, replyId: String?) -> Unit,
  onToggleCollapseReplies: (commentId: String) -> Unit,
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
    modifier = Modifier.testTag("comment_drawer_sheet"),
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
            modifier = Modifier.testTag("drawer_comment_total"),
          )
          IconButton(
            onClick = onDismiss,
            modifier =
              Modifier.size(32.dp)
                .clip(CircleShape)
                .background(LightSurfaceContainer)
                .testTag("close_comment_drawer_btn"),
          ) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = stringResource(R.string.cd_close),
              tint = LightOnSurfaceVariant,
              modifier = Modifier.size(16.dp),
            )
          }
        }
      }

      HorizontalDivider(color = LightOutlineVariant, thickness = 1.dp)

      LazyColumn(
        modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
      ) {
        item { Spacer(Modifier.height(2.dp)) }
        items(comments, key = { it.id }) { comment ->
          CommentItemRow(
            comment = comment,
            onReplyClick = { author ->
              onReplyClick(comment.id, author)
              runCatching { inputFocusRequester.requestFocus() }
            },
            onToggleLike = { replyId -> onToggleCommentLike(comment.id, replyId) },
            onToggleCollapseReplies = { onToggleCollapseReplies(comment.id) },
          )
        }
        item { Spacer(Modifier.height(6.dp)) }
      }

      HorizontalDivider(color = LightOutlineVariant, thickness = 1.dp)

      Column(
        modifier = Modifier.fillMaxWidth().background(LightSurface).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        if (replyTarget != null) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Text(
              text = stringResource(R.string.comment_hint_reply_format, replyTarget.authorName),
              style = MaterialTheme.typography.labelSmall,
              color = AccentBlue,
            )
            Text(
              text = stringResource(R.string.action_collapse),
              style = MaterialTheme.typography.labelSmall,
              color = LightOnSurfaceVariant,
              modifier = Modifier.clickable { onClearReplyTarget() }.padding(horizontal = 6.dp, vertical = 2.dp),
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
              modifier =
                Modifier.fillMaxWidth()
                  .focusRequester(inputFocusRequester)
                  .testTag("drawer_comment_input"),
              decorationBox = { innerTextField ->
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
                innerTextField()
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
                .testTag("drawer_comment_send_btn"),
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier.padding(horizontal = 16.dp),
            ) {
              Text(
                text = stringResource(R.string.comment_action_send),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun CommentItemRow(
  comment: CommentItem,
  onReplyClick: (authorName: String) -> Unit,
  onToggleLike: (replyId: String?) -> Unit,
  onToggleCollapseReplies: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.Top,
  ) {
    Box(
      modifier = Modifier.size(32.dp).clip(CircleShape).background(LightSurfaceContainerHigh),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = comment.authorInitial,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
        color = LightOnSurface,
      )
    }

    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Text(
        text = comment.author,
        style = MaterialTheme.typography.labelSmall,
        color = LightOnSurfaceVariant,
      )
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
        Row(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = comment.timestamp,
            style = MaterialTheme.typography.bodySmall,
            color = LightOnSurfaceVariant,
          )
          Text(
            text = stringResource(R.string.comment_action_reply),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = LightOnSurfaceVariant,
            modifier =
              Modifier.clickable { onReplyClick(comment.author) }
                .padding(horizontal = 4.dp, vertical = 2.dp),
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier =
            Modifier.clip(RoundedCornerShape(8.dp))
              .clickable { onToggleLike(null) }
              .padding(horizontal = 6.dp, vertical = 4.dp),
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
        if (comment.isRepliesCollapsed) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier =
              Modifier.padding(top = 4.dp)
                .clickable { onToggleCollapseReplies() }
                .padding(vertical = 2.dp),
          ) {
            Box(Modifier.width(20.dp).height(1.dp).background(LightOutlineVariant))
            Text(
              text = stringResource(R.string.comment_expand_replies_format, comment.replies.size),
              style = MaterialTheme.typography.labelSmall,
              color = LightSecondary,
            )
            Icon(
              imageVector = Icons.Filled.KeyboardArrowDown,
              contentDescription = null,
              tint = LightSecondary,
              modifier = Modifier.size(14.dp),
            )
          }
        } else {
          Column(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            comment.replies.forEach { reply ->
              SubCommentItemRow(
                reply = reply,
                onReplyClick = { onReplyClick(reply.author) },
                onToggleLike = { onToggleLike(reply.id) },
              )
            }
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier =
                Modifier.clickable { onToggleCollapseReplies() }.padding(vertical = 2.dp),
            ) {
              Box(Modifier.width(20.dp).height(1.dp).background(LightOutlineVariant))
              Text(
                text = stringResource(R.string.action_collapse),
                style = MaterialTheme.typography.labelSmall,
                color = LightOnSurfaceVariant,
              )
              Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = null,
                tint = LightOnSurfaceVariant,
                modifier = Modifier.size(14.dp),
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SubCommentItemRow(
  reply: CommentReply,
  onReplyClick: () -> Unit,
  onToggleLike: () -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.Top,
  ) {
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
        Text(
          text = reply.authorInitial,
          style = MaterialTheme.typography.labelSmall,
          color = LightOnSurface,
        )
      }
    }

    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(
          text = reply.author,
          style = MaterialTheme.typography.labelSmall,
          color = LightOnSurfaceVariant,
        )
        if (reply.isVerifiedBlueV) {
          BlueVBadge(size = 13.dp)
        }
        if (reply.isAuthorBadge) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = ErrorRed,
            contentColor = LightOnPrimary,
          ) {
            Text(
              text = stringResource(R.string.comment_badge_author),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
            )
          }
        }
        if (reply.replyToAuthor != null) {
          Text(
            text = "▸",
            style = MaterialTheme.typography.labelSmall,
            color = LightOnSurfaceVariant,
          )
          Text(
            text = reply.replyToAuthor,
            style = MaterialTheme.typography.labelSmall,
            color = LightOnSurfaceVariant,
          )
        }
      }

      Text(
        text = reply.content,
        style = MaterialTheme.typography.bodyMedium,
        color = LightOnSurface,
        modifier = Modifier.clickable { onReplyClick() },
      )

      Row(
        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = reply.timestamp,
            style = MaterialTheme.typography.bodySmall,
            color = LightOnSurfaceVariant,
          )
          Text(
            text = stringResource(R.string.comment_action_reply),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = LightOnSurfaceVariant,
            modifier =
              Modifier.clickable { onReplyClick() }.padding(horizontal = 4.dp, vertical = 2.dp),
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier =
            Modifier.clip(RoundedCornerShape(8.dp))
              .clickable { onToggleLike() }
              .padding(horizontal = 6.dp, vertical = 4.dp),
        ) {
          StitchHeartIcon(
            filled = reply.isLiked,
            tint = if (reply.isLiked) ErrorRed else LightOnSurfaceVariant,
            modifier = Modifier.size(15.dp),
          )
          Text(
            text = reply.likeCount.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = if (reply.isLiked) ErrorRed else LightOnSurfaceVariant,
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareWorkSheet(
  storyTitle: String,
  authorName: String,
  onDismiss: () -> Unit,
  onShowToast: (String) -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  @Suppress("DEPRECATION")
  val clipboardManager = LocalClipboardManager.current

  val friendMsg = stringResource(R.string.toast_share_friend)
  val wechatMsg = stringResource(R.string.toast_share_wechat)
  val qqMsg = stringResource(R.string.toast_share_qq)
  val copiedMsg = stringResource(R.string.toast_share_copied)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = LightSurface,
    contentColor = LightOnSurface,
    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    dragHandle = null,
    modifier = Modifier.testTag("share_work_sheet"),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Box(
        modifier =
          Modifier.width(40.dp)
            .height(4.dp)
            .clip(CircleShape)
            .background(LightOutlineVariant)
            .align(Alignment.CenterHorizontally)
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(
            text = stringResource(R.string.share_title),
            style = MaterialTheme.typography.titleMedium,
            color = LightOnSurface,
          )
          Text(
            text = "$storyTitle · $authorName",
            style = MaterialTheme.typography.bodySmall,
            color = LightOnSurfaceVariant,
          )
        }
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.size(32.dp).clip(CircleShape).background(LightSurfaceContainer),
        ) {
          Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = stringResource(R.string.cd_close),
            tint = LightOnSurfaceVariant,
            modifier = Modifier.size(16.dp),
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        ShareChannelButton(
          icon = Icons.Outlined.GroupAdd,
          label = stringResource(R.string.share_channel_friend),
          circleBg = AccentBlue.copy(alpha = 0.12f),
          iconTint = AccentBlue,
          modifier = Modifier.weight(1f),
          onClick = {
            onDismiss()
            onShowToast(friendMsg)
          },
        )
        ShareChannelButton(
          icon = Icons.Outlined.ChatBubbleOutline,
          label = stringResource(R.string.share_channel_wechat),
          circleBg = SuccessGreen.copy(alpha = 0.14f),
          iconTint = SuccessGreen,
          modifier = Modifier.weight(1f),
          onClick = {
            onDismiss()
            onShowToast(wechatMsg)
          },
        )
        ShareChannelButton(
          icon = Icons.Outlined.Mood,
          label = stringResource(R.string.share_channel_qq),
          circleBg = AccentBlue.copy(alpha = 0.14f),
          iconTint = AccentBlue,
          modifier = Modifier.weight(1f),
          onClick = {
            onDismiss()
            onShowToast(qqMsg)
          },
        )
        ShareChannelButton(
          icon = Icons.Outlined.Link,
          label = stringResource(R.string.share_channel_copy),
          circleBg = LightSurfaceContainer,
          iconTint = LightOnSurface,
          modifier = Modifier.weight(1f),
          onClick = {
            clipboardManager.setText(AnnotatedString("https://island.app/story/$storyTitle"))
            onDismiss()
            onShowToast(copiedMsg)
          },
        )
      }

      Surface(
        shape = CircleShape,
        color = LightSurfaceContainer,
        contentColor = LightOnSurface,
        modifier =
          Modifier.fillMaxWidth()
            .height(44.dp)
            .clip(CircleShape)
            .clickable { onDismiss() }
            .testTag("cancel_share_sheet_btn"),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(
            text = stringResource(R.string.share_cancel),
            style = MaterialTheme.typography.titleSmall,
          )
        }
      }
    }
  }
}

@Composable
private fun ShareChannelButton(
  icon: ImageVector,
  label: String,
  circleBg: androidx.compose.ui.graphics.Color,
  iconTint: androidx.compose.ui.graphics.Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(6.dp),
    modifier =
      modifier
        .clip(RoundedCornerShape(12.dp))
        .clickable(onClick = onClick)
        .padding(vertical = 8.dp),
  ) {
    Box(
      modifier = Modifier.size(48.dp).clip(CircleShape).background(circleBg),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = iconTint,
        modifier = Modifier.size(22.dp),
      )
    }
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = LightOnSurface,
    )
  }
}

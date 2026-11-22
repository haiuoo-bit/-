package com.example.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import com.example.ui.theme.DarkOnSurface
import com.example.ui.theme.DarkOnSurfaceVariant
import com.example.ui.theme.DarkOutlineVariant
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightOutlineVariant
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceContainer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R

enum class MainTab(val index: Int, val tag: String) {
  HOME(0, "nav_tab_home"),
  PLAZA(1, "nav_tab_plaza"),
  COMMUNITY(2, "nav_tab_community"),
  MESSAGES(3, "nav_tab_messages"),
  ME(4, "nav_tab_me"),
}

private val IndicatorEasing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)

@Composable
fun MainBottomNavBar(
  selectedTab: MainTab,
  hasUnreadMessages: Boolean,
  onSelectTab: (MainTab) -> Unit,
  modifier: Modifier = Modifier,
  darkTheme: Boolean = false,
) {
  val surfaceColor by
    animateColorAsState(
      targetValue = if (darkTheme) DarkSurface else LightSurface,
      animationSpec = tween(180),
      label = "navSurface",
    )
  val activeColor by
    animateColorAsState(
      targetValue = if (darkTheme) DarkOnSurface else LightOnSurface,
      animationSpec = tween(180),
      label = "navActive",
    )
  val inactiveColor by
    animateColorAsState(
      targetValue = if (darkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
      animationSpec = tween(180),
      label = "navInactive",
    )
  val indicatorBg by
    animateColorAsState(
      targetValue = if (darkTheme) DarkSurfaceContainer else LightSurfaceContainer,
      animationSpec = tween(180),
      label = "navIndicator",
    )
  val dividerColor by
    animateColorAsState(
      targetValue = (if (darkTheme) DarkOutlineVariant else LightOutlineVariant).copy(alpha = 0.85f),
      animationSpec = tween(180),
      label = "navDivider",
    )
  val unreadBadgeColor = ErrorRed

  Surface(
    color = surfaceColor,
    shadowElevation = if (darkTheme) 0.dp else 2.dp,
    modifier = modifier.fillMaxWidth(),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      HorizontalDivider(color = dividerColor, thickness = 1.dp)

      BoxWithConstraints(
        modifier =
          Modifier.fillMaxWidth()
            .widthIn(max = 600.dp)
            .padding(horizontal = 12.dp)
            .padding(top = 8.dp, bottom = 12.dp)
      ) {
        val itemWidth = maxWidth / 5
        val pillWidth = 48.dp
        val indicatorOffset by
          animateDpAsState(
            targetValue = (itemWidth * selectedTab.index) + (itemWidth - pillWidth) / 2,
            animationSpec = tween(durationMillis = 260, easing = IndicatorEasing),
            label = "navPillOffset",
          )

        Box(
          modifier =
            Modifier.offset(x = indicatorOffset, y = 4.dp)
              .width(pillWidth)
              .height(28.dp)
              .clip(CircleShape)
              .background(indicatorBg)
        )

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
          MainTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            val label =
              when (tab) {
                MainTab.HOME -> stringResource(R.string.nav_home)
                MainTab.PLAZA -> stringResource(R.string.nav_plaza)
                MainTab.COMMUNITY -> stringResource(R.string.nav_community)
                MainTab.MESSAGES -> stringResource(R.string.nav_messages)
                MainTab.ME -> stringResource(R.string.nav_me)
              }

            NavBarTabItem(
              tab = tab,
              label = label,
              isSelected = isSelected,
              showUnreadDot = tab == MainTab.MESSAGES && hasUnreadMessages,
              activeColor = activeColor,
              inactiveColor = inactiveColor,
              surfaceColor = surfaceColor,
              unreadBadgeColor = unreadBadgeColor,
              onClick = { onSelectTab(tab) },
              modifier = Modifier.weight(1f),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun NavBarTabItem(
  tab: MainTab,
  label: String,
  isSelected: Boolean,
  showUnreadDot: Boolean,
  activeColor: Color,
  inactiveColor: Color,
  surfaceColor: Color,
  unreadBadgeColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val iconTint = if (isSelected) activeColor else inactiveColor
  val popScale = remember { Animatable(1f) }
  val unreadDesc = stringResource(R.string.cd_unread_messages)

  LaunchedEffect(isSelected) {
    if (isSelected) {
      popScale.snapTo(0.78f)
      popScale.animateTo(1.12f, animationSpec = tween(150))
      popScale.animateTo(1f, animationSpec = tween(120))
    } else {
      popScale.snapTo(1f)
    }
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(4.dp),
    modifier =
      modifier
        .heightIn(min = 48.dp)
        .clip(RoundedCornerShape(14.dp))
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          role = Role.Tab,
          onClick = onClick,
        )
        .semantics { selected = isSelected }
        .padding(vertical = 4.dp)
        .testTag(tab.tag),
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.size(28.dp),
    ) {
      NavTabIcon(
        tab = tab,
        filled = isSelected,
        tint = iconTint,
        cutoutColor = surfaceColor,
        modifier =
          Modifier.size(24.dp).graphicsLayer {
            scaleX = popScale.value
            scaleY = popScale.value
          },
      )

      if (showUnreadDot) {
        Box(
          modifier =
            Modifier.align(Alignment.TopEnd)
              .size(11.dp)
              .clip(CircleShape)
              .background(surfaceColor)
              .padding(1.5.dp)
              .clip(CircleShape)
              .background(unreadBadgeColor)
              .semantics { contentDescription = unreadDesc }
              .testTag("messages_unread_dot")
        )
      }
    }

    Text(
      text = label,
      style =
        MaterialTheme.typography.labelSmall.copy(
          fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        ),
      color = iconTint,
      maxLines = 1,
    )
  }
}

@Composable
private fun NavTabIcon(
  tab: MainTab,
  filled: Boolean,
  tint: Color,
  cutoutColor: Color,
  modifier: Modifier = Modifier,
) {
  val homeOutlineOuter = remember {
    PathParser()
      .parsePathString(
        "M3.75 10.4C3.75 9.25 4.28 8.16 5.19 7.45L10.39 3.41C11.34 2.67 12.66 2.67 13.61 3.41L18.81 7.45C19.72 8.16 20.25 9.25 20.25 10.4V17.25C20.25 19.18 18.68 20.75 16.75 20.75H7.25C5.32 20.75 3.75 19.18 3.75 17.25V10.4Z"
      )
      .toPath()
  }
  val homeOutlineDoor = remember {
    PathParser()
      .parsePathString(
        "M9.75 15.75C9.75 14.65 10.65 13.75 11.75 13.75H12.25C13.35 13.75 14.25 14.65 14.25 15.75V20.75H9.75V15.75Z"
      )
      .toPath()
  }
  val homeFilledOuter = remember {
    PathParser()
      .parsePathString(
        "M3.5 10.35C3.5 9.1 4.08 7.92 5.07 7.15L10.27 3.11C11.29 2.31 12.71 2.31 13.73 3.11L18.93 7.15C19.92 7.92 20.5 9.1 20.5 10.35V17.25C20.5 19.32 18.82 21 16.75 21H7.25C5.18 21 3.5 19.32 3.5 17.25V10.35Z"
      )
      .toPath()
  }

  val plazaStarOutline = remember {
    PathParser()
      .parsePathString(
        "M12 7.4C12.35 9.65 14.35 11.65 16.6 12C14.35 12.35 12.35 14.35 12 16.6C11.65 14.35 9.65 12.35 7.4 12C9.65 11.65 11.65 9.65 12 7.4Z"
      )
      .toPath()
  }
  val plazaStarFilled = remember {
    PathParser()
      .parsePathString(
        "M12 7.2C12.42 9.68 14.32 11.58 16.8 12C14.32 12.42 12.42 14.32 12 16.8C11.58 14.32 9.68 12.42 7.2 12C9.68 11.58 11.58 9.68 12 7.2Z"
      )
      .toPath()
  }

  val communityOutlineArc1 = remember {
    PathParser()
      .parsePathString("M3.75 18.75C4.15 15.8 6.45 14 9.25 14C12.05 14 14.35 15.8 14.75 18.75")
      .toPath()
  }
  val communityOutlineArc2 = remember {
    PathParser()
      .parsePathString("M16.25 14.25C18.35 14.45 19.95 15.95 20.25 18.25")
      .toPath()
  }
  val communityFilledBody1 = remember {
    PathParser()
      .parsePathString("M3.5 19C3.95 15.65 6.35 13.5 9.25 13.5C12.15 13.5 14.55 15.65 15 19H3.5Z")
      .toPath()
  }
  val communityFilledBody2 = remember {
    PathParser()
      .parsePathString(
        "M16.3 13.85C18.6 14.1 20.2 15.75 20.5 18.5H16.25C16.05 16.65 15.4 15.05 14.35 13.95C14.95 13.85 15.6 13.8 16.3 13.85Z"
      )
      .toPath()
  }

  val msgOutline = remember {
    PathParser()
      .parsePathString(
        "M12 3.75C16.97 3.75 20.75 7.12 20.75 11.4C20.75 15.68 16.97 19.05 12 19.05C10.65 19.05 9.38 18.8 8.24 18.35C7.82 18.19 7.35 18.21 6.94 18.42L4.95 19.44C4.24 19.8 3.45 19.14 3.68 18.38L4.25 16.51C4.37 16.12 4.28 15.69 4.03 15.37C3.53 14.2 3.25 12.84 3.25 11.4C3.25 7.12 7.03 3.75 12 3.75Z"
      )
      .toPath()
  }
  val msgFilled = remember {
    PathParser()
      .parsePathString(
        "M12 3.5C17.1 3.5 21 6.98 21 11.4C21 15.82 17.1 19.3 12 19.3C10.6 19.3 9.28 19.04 8.1 18.58C7.68 18.42 7.21 18.44 6.81 18.65L4.7 19.73C3.94 20.12 3.1 19.42 3.35 18.61L3.96 16.64C4.08 16.25 4 15.82 3.76 15.48C3.27 14.29 3 12.89 3 11.4C3 6.98 6.9 3.5 12 3.5Z"
      )
      .toPath()
  }

  val meOutlineArc = remember {
    PathParser()
      .parsePathString("M5 19.5C5 16.05 8.1 13.9 12 13.9C15.9 13.9 19 16.05 19 19.5")
      .toPath()
  }
  val meFilledBody = remember {
    PathParser()
      .parsePathString(
        "M4.75 18.85C4.75 15.35 7.95 13.4 12 13.4C16.05 13.4 19.25 15.35 19.25 18.85C19.25 19.68 18.58 20.35 17.75 20.35H6.25C5.42 20.35 4.75 19.68 4.75 18.85Z"
      )
      .toPath()
  }

  Canvas(modifier = modifier) {
    with24Viewport {
      when (tab) {
        MainTab.HOME -> {
          if (filled) {
            drawPath(homeFilledOuter, color = tint, style = Fill)
            drawRoundRect(
              color = cutoutColor,
              topLeft = Offset(10f, 13.5f),
              size = Size(4f, 5f),
              cornerRadius = CornerRadius(2f, 2f),
            )
          } else {
            drawPath(
              homeOutlineOuter,
              color = tint,
              style = Stroke(width = 1.85f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
            drawPath(
              homeOutlineDoor,
              color = tint,
              style = Stroke(width = 1.85f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
          }
        }

        MainTab.PLAZA -> {
          if (filled) {
            drawCircle(color = tint, radius = 9.25f, center = Offset(12f, 12f))
            drawPath(plazaStarFilled, color = cutoutColor, style = Fill)
          } else {
            drawCircle(
              color = tint,
              radius = 8.75f,
              center = Offset(12f, 12f),
              style = Stroke(width = 1.85f),
            )
            drawPath(
              plazaStarOutline,
              color = tint,
              style = Stroke(width = 1.75f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
          }
        }

        MainTab.COMMUNITY -> {
          if (filled) {
            drawCircle(color = tint, radius = 3.25f, center = Offset(9.25f, 8.25f))
            drawPath(communityFilledBody1, color = tint, style = Fill)
            drawCircle(color = tint, radius = 2.35f, center = Offset(16.5f, 9.5f))
            drawPath(communityFilledBody2, color = tint, style = Fill)
          } else {
            drawCircle(
              color = tint,
              radius = 3f,
              center = Offset(9.25f, 8.5f),
              style = Stroke(width = 1.85f),
            )
            drawPath(
              communityOutlineArc1,
              color = tint,
              style = Stroke(width = 1.85f, cap = StrokeCap.Round),
            )
            drawCircle(
              color = tint,
              radius = 2.25f,
              center = Offset(16.25f, 9.5f),
              style = Stroke(width = 1.75f),
            )
            drawPath(
              communityOutlineArc2,
              color = tint,
              style = Stroke(width = 1.75f, cap = StrokeCap.Round),
            )
          }
        }

        MainTab.MESSAGES -> {
          if (filled) {
            drawPath(msgFilled, color = tint, style = Fill)
            drawCircle(color = cutoutColor, radius = 1.15f, center = Offset(8.75f, 11.4f))
            drawCircle(color = cutoutColor, radius = 1.15f, center = Offset(12f, 11.4f))
            drawCircle(color = cutoutColor, radius = 1.15f, center = Offset(15.25f, 11.4f))
          } else {
            drawPath(
              msgOutline,
              color = tint,
              style = Stroke(width = 1.85f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
            drawCircle(color = tint, radius = 1.05f, center = Offset(8.75f, 11.4f))
            drawCircle(color = tint, radius = 1.05f, center = Offset(12f, 11.4f))
            drawCircle(color = tint, radius = 1.05f, center = Offset(15.25f, 11.4f))
          }
        }

        MainTab.ME -> {
          if (filled) {
            drawCircle(color = tint, radius = 3.85f, center = Offset(12f, 8f))
            drawPath(meFilledBody, color = tint, style = Fill)
          } else {
            drawCircle(
              color = tint,
              radius = 3.6f,
              center = Offset(12f, 8.25f),
              style = Stroke(width = 1.85f),
            )
            drawPath(
              meOutlineArc,
              color = tint,
              style = Stroke(width = 1.85f, cap = StrokeCap.Round),
            )
          }
        }
      }
    }
  }
}

private inline fun DrawScope.with24Viewport(block: DrawScope.() -> Unit) {
  withTransform({
    scale(scaleX = size.width / 24f, scaleY = size.height / 24f, pivot = Offset.Zero)
  }) {
    block()
  }
}

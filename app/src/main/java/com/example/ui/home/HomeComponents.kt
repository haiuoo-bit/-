package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BlueVDeep
import com.example.ui.theme.BlueVPrimary
import com.example.ui.theme.BlueVSky
import com.example.ui.theme.DarkOnSurface
import com.example.ui.theme.DarkSurfaceContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LightOnPrimary
import com.example.ui.theme.LightPrimary
import com.example.ui.theme.PdfBorderRed
import com.example.ui.theme.PdfFoldBackground
import com.example.ui.theme.PdfLinePink
import com.example.ui.theme.PdfTintBackground

@Composable
fun BlueVBadge(
  modifier: Modifier = Modifier,
  size: Dp = 16.dp,
) {
  val desc = stringResource(R.string.cd_blue_v_badge)
  val scallopedPath = remember {
    PathParser()
      .parsePathString(
        "M20 3.2C22.4 3.2 23.8 5.2 26 5.8C28.2 6.4 30.4 5.4 32.1 7.1C33.8 8.8 32.8 11 33.4 13.2C34 15.4 36.8 16.8 36.8 19.2C36.8 21.6 34.4 23 33.8 25.2C33.2 27.4 34.2 29.8 32.5 31.5C30.8 33.2 28.4 32.2 26.2 32.8C24 33.4 22.4 36.8 20 36.8C17.6 36.8 16 33.4 13.8 32.8C11.6 32.2 9.2 33.2 7.5 31.5C5.8 29.8 6.8 27.4 6.2 25.2C5.6 23 3.2 21.6 3.2 19.2C3.2 16.8 6 15.4 6.6 13.2C7.2 11 6.2 8.8 7.9 7.1C9.6 5.4 11.8 6.4 14 5.8C16.2 5.2 17.6 3.2 20 3.2Z"
      )
      .toPath()
  }
  val vPath = remember {
    PathParser().parsePathString("M13.8 14.2L20 26.8L26.2 14.2").toPath()
  }

  Canvas(
    modifier = modifier.size(size).semantics { contentDescription = desc }
  ) {
    withTransform({
      scale(scaleX = this@Canvas.size.width / 40f, scaleY = this@Canvas.size.height / 40f, pivot = Offset.Zero)
    }) {
      drawPath(
        path = scallopedPath,
        brush =
          Brush.linearGradient(
            colors = listOf(BlueVSky, BlueVPrimary, BlueVDeep),
            start = Offset(6f, 4f),
            end = Offset(34f, 36f),
          ),
        style = Fill,
      )
      drawPath(
        path = vPath,
        color = Color.White,
        style = Stroke(width = 3.6f, cap = StrokeCap.Round, join = StrokeJoin.Round),
      )
    }
  }
}

@Composable
fun StitchHeartIcon(
  filled: Boolean,
  tint: Color,
  modifier: Modifier = Modifier,
) {
  val heartPath = remember {
    PathParser()
      .parsePathString(
        "M12.001 20.25C11.72 20.25 11.44 20.17 11.19 20.02C7.82 17.98 2.75 13.97 2.75 8.75C2.75 5.82 5.08 3.5 7.98 3.5C9.68 3.5 11.18 4.31 12 5.55C12.82 4.31 14.32 3.5 16.02 3.5C18.92 3.5 21.25 5.82 21.25 8.75C21.25 13.97 16.18 17.98 12.81 20.02C12.56 20.17 12.28 20.25 12.001 20.25Z"
      )
      .toPath()
  }
  Canvas(modifier = modifier) {
    withViewport24 {
      drawPath(
        path = heartPath,
        color = tint,
        style = if (filled) Fill else Stroke(width = 1.75f, cap = StrokeCap.Round, join = StrokeJoin.Round),
      )
    }
  }
}

@Composable
fun StitchCommentBubbleIcon(
  tint: Color,
  modifier: Modifier = Modifier,
) {
  val bubblePath = remember {
    PathParser()
      .parsePathString(
        "M20 11.5C20 15.6421 16.4183 19 12 19C10.59 19 9.26 18.66 8.11 18.06L4 19.2L5.22 15.46C4.45 14.3 4 12.94 4 11.5C4 7.35786 7.58172 4 12 4C16.4183 4 20 7.35786 20 11.5Z"
      )
      .toPath()
  }
  Canvas(modifier = modifier) {
    withViewport24 {
      drawPath(
        path = bubblePath,
        color = tint,
        style = Stroke(width = 1.75f, cap = StrokeCap.Round, join = StrokeJoin.Round),
      )
      drawCircle(color = tint, radius = 1.05f, center = Offset(8.8f, 11.5f))
      drawCircle(color = tint, radius = 1.05f, center = Offset(12f, 11.5f))
      drawCircle(color = tint, radius = 1.05f, center = Offset(15.2f, 11.5f))
    }
  }
}

@Composable
fun StitchStarIcon(
  filled: Boolean,
  tint: Color,
  modifier: Modifier = Modifier,
) {
  val starPath = remember {
    PathParser()
      .parsePathString(
        "M12 3.25L14.65 8.62L20.58 9.48L16.29 13.66L17.3 19.56L12 16.77L6.7 19.56L7.71 13.66L3.42 9.48L9.35 8.62L12 3.25Z"
      )
      .toPath()
  }
  Canvas(modifier = modifier) {
    withViewport24 {
      drawPath(
        path = starPath,
        color = tint,
        style = if (filled) Fill else Stroke(width = 1.75f, cap = StrokeCap.Round, join = StrokeJoin.Round),
      )
    }
  }
}

@Composable
fun StitchShareIcon(
  tint: Color,
  modifier: Modifier = Modifier,
) {
  val sharePath = remember {
    PathParser()
      .parsePathString(
        "M14 4.5L20.25 10.25L14 16V12.5C9.2 12.5 5.9 14.3 4 18.25C4.9 12.75 8.3 8 14 8V4.5Z"
      )
      .toPath()
  }
  Canvas(modifier = modifier) {
    withViewport24 {
      drawPath(
        path = sharePath,
        color = tint,
        style = Stroke(width = 1.75f, cap = StrokeCap.Round, join = StrokeJoin.Round),
      )
    }
  }
}

@Composable
fun StitchGridModeIcon(
  isGridMode: Boolean,
  tint: Color,
  modifier: Modifier = Modifier,
) {
  val singleLines = remember {
    PathParser().parsePathString("M8.5 8.5H15.5M8.5 12H15.5M8.5 15.5H12.5").toPath()
  }
  Canvas(modifier = modifier) {
    withViewport24 {
      if (isGridMode) {
        drawRoundRect(
          color = tint,
          topLeft = Offset(5f, 3.75f),
          size = Size(14f, 16.5f),
          cornerRadius = CornerRadius(2.5f, 2.5f),
          style = Stroke(width = 1.9f),
        )
        drawPath(singleLines, color = tint, style = Stroke(width = 1.75f, cap = StrokeCap.Round))
      } else {
        val rects = listOf(Offset(3.75f, 3.75f), Offset(13.25f, 3.75f), Offset(3.75f, 13.25f), Offset(13.25f, 13.25f))
        rects.forEach { topLeft ->
          drawRoundRect(
            color = tint,
            topLeft = topLeft,
            size = Size(7f, 7f),
            cornerRadius = CornerRadius(2f, 2f),
            style = Stroke(width = 1.9f),
          )
        }
      }
    }
  }
}

@Composable
fun StitchPdfBadgeIcon(modifier: Modifier = Modifier) {
  val docBody = remember {
    PathParser()
      .parsePathString(
        "M9 10C9 6.69 11.69 4 15 4H27.2C28.53 4 29.8 4.53 30.74 5.46L37.54 12.26C38.47 13.2 39 14.47 39 15.8V38C39 41.31 36.31 44 33 44H15C11.69 44 9 41.31 9 38V10Z"
      )
      .toPath()
  }
  val docFold = remember {
    PathParser().parsePathString("M28 4.2V11C28 12.66 29.34 14 31 14H38.8").toPath()
  }
  val lines = remember {
    PathParser().parsePathString("M15.5 19H24.5M15.5 23H21.5").toPath()
  }
  val textMeasurer = rememberTextMeasurer()
  val pdfStyle =
    TextStyle(
      color = Color.White,
      fontSize = 8.5.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.4.sp,
    )

  Box(modifier = modifier.size(40.dp), contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.size(40.dp)) {
      withTransform({
        scale(scaleX = size.width / 48f, scaleY = size.height / 48f, pivot = Offset.Zero)
      }) {
        drawPath(docBody, color = PdfTintBackground, style = Fill)
        drawPath(docBody, color = PdfBorderRed, style = Stroke(width = 1.6f))
        drawPath(docFold, color = PdfFoldBackground, style = Fill)
        drawPath(docFold, color = PdfBorderRed, style = Stroke(width = 1.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(lines, color = PdfLinePink, style = Stroke(width = 2f, cap = StrokeCap.Round))
        drawRoundRect(
          color = ErrorRed,
          topLeft = Offset(12f, 27.5f),
          size = Size(24f, 12f),
          cornerRadius = CornerRadius(6f, 6f),
        )
        val measured = textMeasurer.measure("PDF", style = pdfStyle)
        drawText(
          textLayoutResult = measured,
          topLeft = Offset(24f - measured.size.width / 2f, 33.5f - measured.size.height / 2f),
        )
      }
    }
  }
}

@Composable
fun OverlayToastPill(
  message: String?,
  darkPolarity: Boolean = true,
  modifier: Modifier = Modifier,
) {
  AnimatedVisibility(
    visible = !message.isNullOrEmpty(),
    enter = fadeIn(),
    exit = fadeOut(),
    modifier = modifier,
  ) {
    Surface(
      shape = CircleShape,
      color = if (darkPolarity) DarkSurfaceContainer.copy(alpha = 0.96f) else LightPrimary.copy(alpha = 0.94f),
      contentColor = if (darkPolarity) DarkOnSurface else LightOnPrimary,
      shadowElevation = 6.dp,
    ) {
      Text(
        text = message.orEmpty(),
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
      )
    }
  }
}

private inline fun DrawScope.withViewport24(block: DrawScope.() -> Unit) {
  withTransform({
    scale(scaleX = size.width / 24f, scaleY = size.height / 24f, pivot = Offset.Zero)
  }) {
    block()
  }
}

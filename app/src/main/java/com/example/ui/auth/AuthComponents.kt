package com.example.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.IslandTheme
import com.example.ui.theme.MonoMediumStyle
import kotlin.math.roundToInt

@Composable
fun SharedAuthScaffold(
  modifier: Modifier = Modifier,
  cardContent: @Composable ColumnScope.() -> Unit,
) {
  BoxWithConstraints(
    modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
  ) {
    val heroHeight = (maxHeight * 0.68f).coerceAtLeast(260.dp)

    Image(
      painter = painterResource(id = R.drawable.img_floating_island_hero),
      contentDescription = stringResource(R.string.cd_hero_illustration),
      contentScale = ContentScale.Crop,
      alignment = Alignment.TopCenter,
      modifier = Modifier.fillMaxWidth().height(heroHeight).align(Alignment.TopCenter),
    )

    Column(
      modifier = Modifier.fillMaxSize().statusBarsPadding().imePadding(),
      verticalArrangement = Arrangement.Bottom,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Spacer(modifier = Modifier.weight(1f, fill = true).heightIn(min = 120.dp))

      Box(
        modifier =
          Modifier.fillMaxWidth()
            .widthIn(max = 600.dp)
            .height(10.dp)
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color(0x14141518))
              )
            )
      )

      Surface(
        modifier =
          Modifier.fillMaxWidth()
            .widthIn(max = 600.dp)
            .animateContentSize(animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = MaterialTheme.colorScheme.surface,
      ) {
        Column(
          modifier =
            Modifier.fillMaxWidth()
              .verticalScroll(rememberScrollState())
              .padding(horizontal = 24.dp, vertical = 24.dp)
              .navigationBarsPadding(),
          verticalArrangement = Arrangement.spacedBy(16.dp),
          content = cardContent,
        )
      }
    }
  }
}

@Composable
fun AuthBackHeader(
  title: String,
  subtitle: String,
  backContentDescription: String,
  onBackClick: () -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Box(
        contentAlignment = Alignment.CenterStart,
        modifier =
          Modifier.offset(x = (-8).dp)
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onBackClick)
            .testTag("back_button"),
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier.align(Alignment.Center)
              .size(32.dp)
              .clip(CircleShape)
              .background(IslandTheme.colors.surfaceContainerLow),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = backContentDescription,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp),
          )
        }
      }
      Text(
        text = title,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.offset(x = (-4).dp),
      )
    }
    Text(
      text = subtitle,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
fun LoginModeTabBar(
  selectedTab: Int,
  onSelectCodeTab: () -> Unit,
  onSelectPasswordTab: () -> Unit,
) {
  BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
    val tabWidth = maxWidth / 2
    val indicatorWidth = 40.dp
    val indicatorOffset by
      animateDpAsState(
        targetValue =
          if (selectedTab == 0) (tabWidth - indicatorWidth) / 2
          else tabWidth + (tabWidth - indicatorWidth) / 2,
        animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
        label = "tabIndicatorOffset",
      )

    Column(modifier = Modifier.fillMaxWidth()) {
      Row(modifier = Modifier.fillMaxWidth()) {
        LoginModeTabItem(
          label = stringResource(R.string.tab_code_login),
          selected = selectedTab == 0,
          onClick = onSelectCodeTab,
          modifier = Modifier.weight(1f).testTag("tab_code_login"),
        )
        LoginModeTabItem(
          label = stringResource(R.string.tab_password_login),
          selected = selectedTab == 1,
          onClick = onSelectPasswordTab,
          modifier = Modifier.weight(1f).testTag("tab_password_login"),
        )
      }
      Box(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(
          color = IslandTheme.colors.outlineVariant,
          thickness = 1.dp,
          modifier = Modifier.align(Alignment.BottomCenter),
        )
        Box(
          modifier =
            Modifier.offset(x = indicatorOffset)
              .width(indicatorWidth)
              .height(2.5.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primary)
        )
      }
    }
  }
}

@Composable
private fun LoginModeTabItem(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier =
      modifier
        .heightIn(min = 48.dp)
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          onClick = onClick,
        )
        .padding(top = 6.dp, bottom = 10.dp),
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelLarge,
      color =
        if (selected) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
fun PhoneInputField(
  label: String,
  value: String,
  placeholder: String,
  isError: Boolean,
  errorText: String,
  onValueChange: (String) -> Unit,
  onClear: () -> Unit,
  focusRequester: FocusRequester = remember { FocusRequester() },
  imeAction: ImeAction = ImeAction.Next,
  onNext: () -> Unit = {},
) {
  var isFocused by remember { mutableStateOf(false) }

  Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = IslandTheme.colors.surfaceContainer,
      border =
        when {
          isError -> BorderStroke(1.5.dp, IslandTheme.colors.error)
          isFocused -> BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
          else -> null
        },
      modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
      ) {
        Text(
          text = stringResource(R.string.prefix_china_code),
          style = MonoMediumStyle,
          color = MaterialTheme.colorScheme.onSurface,
        )
        VerticalDivider(
          modifier = Modifier.padding(horizontal = 12.dp).height(18.dp),
          color = IslandTheme.colors.outlineVariant,
        )
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
          if (value.isEmpty()) {
            Text(
              text = placeholder,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.outline,
            )
          }
          BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MonoMediumStyle.copy(color = MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = imeAction),
            keyboardActions = KeyboardActions(onNext = { onNext() }),
            modifier =
              Modifier.fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused }
                .testTag("phone_input"),
          )
        }
        if (value.isNotEmpty()) {
          IconButton(
            onClick = {
              onClear()
              focusRequester.requestFocus()
            },
            modifier = Modifier.size(32.dp).testTag("clear_phone_button"),
          ) {
            Icon(
              imageVector = Icons.Filled.Cancel,
              contentDescription = stringResource(R.string.cd_clear_phone),
              tint = MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(18.dp),
            )
          }
        }
      }
    }
    AnimatedVisibility(visible = isError) {
      Text(
        text = errorText,
        style = MaterialTheme.typography.bodySmall,
        color = IslandTheme.colors.error,
      )
    }
  }
}

@Composable
fun SmsCodeInputField(
  value: String,
  countdownSeconds: Int,
  isError: Boolean,
  onValueChange: (String) -> Unit,
  onSendCodeClick: () -> Unit,
  focusRequester: FocusRequester = remember { FocusRequester() },
  imeAction: ImeAction = ImeAction.Done,
  onNext: () -> Unit = {},
  onDone: () -> Unit = {},
) {
  var isFocused by remember { mutableStateOf(false) }

  Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
    Text(
      text = stringResource(R.string.label_code),
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = IslandTheme.colors.surfaceContainer,
      border =
        when {
          isError -> BorderStroke(1.5.dp, IslandTheme.colors.error)
          isFocused -> BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
          else -> null
        },
      modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 6.dp, top = 2.dp, bottom = 2.dp),
      ) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
          if (value.isEmpty()) {
            Text(
              text = stringResource(R.string.hint_code),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.outline,
            )
          }
          BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MonoMediumStyle.copy(color = MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = imeAction),
            keyboardActions = KeyboardActions(onNext = { onNext() }, onDone = { onDone() }),
            modifier =
              Modifier.fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused }
                .testTag("code_input"),
          )
        }
        TextButton(
          onClick = onSendCodeClick,
          enabled = countdownSeconds == 0,
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
          modifier = Modifier.heightIn(min = 44.dp).testTag("send_code_button"),
        ) {
          Text(
            text =
              if (countdownSeconds > 0) {
                stringResource(R.string.action_resend_code_format, countdownSeconds)
              } else {
                stringResource(R.string.action_get_code)
              },
            style = MaterialTheme.typography.labelSmall,
            color =
              if (countdownSeconds > 0) MaterialTheme.colorScheme.outline
              else MaterialTheme.colorScheme.onSurface,
          )
        }
      }
    }
    AnimatedVisibility(visible = isError) {
      Text(
        text = stringResource(R.string.error_code),
        style = MaterialTheme.typography.bodySmall,
        color = IslandTheme.colors.error,
      )
    }
  }
}

@Composable
fun PasswordInputField(
  label: String,
  value: String,
  placeholder: String,
  isPasswordVisible: Boolean,
  isError: Boolean,
  errorText: String,
  onValueChange: (String) -> Unit,
  onToggleVisibility: () -> Unit,
  focusRequester: FocusRequester = remember { FocusRequester() },
  onDone: () -> Unit = {},
) {
  var isFocused by remember { mutableStateOf(false) }

  Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = IslandTheme.colors.surfaceContainer,
      border =
        when {
          isError -> BorderStroke(1.5.dp, IslandTheme.colors.error)
          isFocused -> BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
          else -> null
        },
      modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 6.dp, top = 2.dp, bottom = 2.dp),
      ) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
          if (value.isEmpty()) {
            Text(
              text = placeholder,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.outline,
            )
          }
          BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle =
              MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            visualTransformation =
              if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions =
              KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            modifier =
              Modifier.fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused }
                .testTag("password_input"),
          )
        }
        IconButton(
          onClick = onToggleVisibility,
          modifier = Modifier.minimumInteractiveComponentSize().testTag("toggle_password_button"),
        ) {
          Icon(
            imageVector =
              if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
            contentDescription = stringResource(R.string.cd_toggle_password),
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp),
          )
        }
      }
    }
    AnimatedVisibility(visible = isError) {
      Text(
        text = errorText,
        style = MaterialTheme.typography.bodySmall,
        color = IslandTheme.colors.error,
      )
    }
  }
}

@Composable
fun PrimaryPillButton(
  text: String,
  loadingText: String,
  isLoading: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Button(
    onClick = onClick,
    enabled = !isLoading,
    shape = CircleShape,
    elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp),
    colors =
      ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.78f),
        disabledContentColor = MaterialTheme.colorScheme.onPrimary,
      ),
    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    modifier = modifier.fillMaxWidth().heightIn(min = 48.dp),
  ) {
    if (isLoading) {
      CircularProgressIndicator(
        modifier = Modifier.size(16.dp),
        color = MaterialTheme.colorScheme.onPrimary,
        strokeWidth = 2.dp,
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(text = loadingText, style = MaterialTheme.typography.labelLarge)
    } else {
      Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
  }
}

@Composable
fun SecondaryPillButton(
  text: String,
  enabled: Boolean = true,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Button(
    onClick = onClick,
    enabled = enabled,
    shape = CircleShape,
    elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp),
    colors =
      ButtonDefaults.buttonColors(
        containerColor = IslandTheme.colors.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = IslandTheme.colors.surfaceContainer.copy(alpha = 0.5f),
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
      ),
    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    modifier = modifier.fillMaxWidth().heightIn(min = 48.dp),
  ) {
    Text(text = text, style = MaterialTheme.typography.labelLarge)
  }
}

@Composable
fun AgreementRow(
  isAgreed: Boolean,
  shakeTick: Int,
  showHighlight: Boolean,
  showTooltip: Boolean,
  onToggleAgreed: (Boolean) -> Unit,
  onPolicyClick: (String) -> Unit,
) {
  val shakeOffset = remember { Animatable(0f) }

  LaunchedEffect(shakeTick) {
    if (shakeTick > 0) {
      val pattern = listOf(-12f, 12f, -10f, 10f, -6f, 0f)
      for (target in pattern) {
        shakeOffset.animateTo(target, animationSpec = tween(durationMillis = 44))
      }
    }
  }

  val highlightBg by
    animateColorAsState(
      targetValue =
        if (showHighlight) IslandTheme.colors.error.copy(alpha = 0.1f) else Color.Transparent,
      animationSpec = tween(140),
      label = "agreeRowHighlight",
    )
  val highlightBorder by
    animateColorAsState(
      targetValue =
        if (showHighlight) IslandTheme.colors.error.copy(alpha = 0.3f) else Color.Transparent,
      animationSpec = tween(140),
      label = "agreeRowBorder",
    )

  val userPolicyText = stringResource(R.string.policy_user)
  val privacyPolicyText = stringResource(R.string.policy_privacy)
  val userPolicyTitle = stringResource(R.string.policy_user_title)
  val privacyPolicyTitle = stringResource(R.string.policy_privacy_title)

  val policyLinkStyle =
    TextLinkStyles(
      style =
        SpanStyle(
          color = MaterialTheme.colorScheme.onSurface,
          fontWeight = FontWeight.Medium,
        )
    )

  val annotatedAgreement =
    buildAnnotatedString {
      withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
        append(stringResource(R.string.agree_prefix))
      }
      withLink(
        LinkAnnotation.Clickable(tag = "USER_POLICY", styles = policyLinkStyle) {
          onPolicyClick(userPolicyTitle)
        }
      ) {
        append(userPolicyText)
      }
      withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
        append(stringResource(R.string.agree_and))
      }
      withLink(
        LinkAnnotation.Clickable(tag = "PRIVACY_POLICY", styles = policyLinkStyle) {
          onPolicyClick(privacyPolicyTitle)
        }
      ) {
        append(privacyPolicyText)
      }
    }

  Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
    Surface(
      shape = RoundedCornerShape(10.dp),
      color = highlightBg,
      border = BorderStroke(1.dp, highlightBorder),
      modifier = Modifier.offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier =
          Modifier.heightIn(min = 48.dp)
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = null,
              onClick = { onToggleAgreed(!isAgreed) },
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("agree_checkbox"),
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier.size(16.dp)
              .clip(CircleShape)
              .background(
                if (isAgreed) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface
              )
              .border(
                width = 1.2.dp,
                color =
                  if (isAgreed) MaterialTheme.colorScheme.primary
                  else MaterialTheme.colorScheme.outline,
                shape = CircleShape,
              ),
        ) {
          if (isAgreed) {
            Icon(
              imageVector = Icons.Filled.Check,
              contentDescription = stringResource(R.string.cd_checked_icon),
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.size(11.dp),
            )
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
          text = annotatedAgreement,
          style = MaterialTheme.typography.bodySmall,
          maxLines = 1,
        )
      }
    }

    // Overlay tooltip so card height never jumps when tooltip appears
    androidx.compose.animation.AnimatedVisibility(
      visible = showTooltip,
      enter = fadeIn(tween(120)),
      exit = fadeOut(tween(140)),
      modifier = Modifier.align(Alignment.TopCenter).offset(y = (-28).dp),
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 3.dp,
      ) {
        Text(
          text = stringResource(R.string.agree_tip),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onPrimary,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthModalsHost(
  activeModal: AuthModal?,
  onDismiss: () -> Unit,
  onAgreementConfirm: () -> Unit,
  onOpenPolicy: (String) -> Unit,
  onAcceptPolicyDrawer: () -> Unit,
  onLoginSuccessEnterHome: () -> Unit,
  onResetSuccessBackToPasswordLogin: () -> Unit,
) {
  when (activeModal) {
    AuthModal.AgreementAlert -> {
      val userPolicyTitle = stringResource(R.string.policy_user_title)
      val privacyPolicyTitle = stringResource(R.string.policy_privacy_title)
      val linkStyle =
        TextLinkStyles(
          style =
            SpanStyle(
              color = MaterialTheme.colorScheme.onSurface,
              fontWeight = FontWeight.Medium,
              textDecoration = TextDecoration.Underline,
            )
        )
      val alertBodyText =
        buildAnnotatedString {
          withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
            append("请先阅读并同意")
          }
          withLink(
            LinkAnnotation.Clickable(tag = "USER_POLICY_MODAL", styles = linkStyle) {
              onOpenPolicy(userPolicyTitle)
            }
          ) {
            append(stringResource(R.string.policy_user))
          }
          withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
            append("和")
          }
          withLink(
            LinkAnnotation.Clickable(tag = "PRIVACY_POLICY_MODAL", styles = linkStyle) {
              onOpenPolicy(privacyPolicyTitle)
            }
          ) {
            append(stringResource(R.string.policy_privacy))
          }
          withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
            append("后继续登录")
          }
        }

      Dialog(onDismissRequest = onDismiss) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = MaterialTheme.colorScheme.surface,
          border = BorderStroke(1.dp, IslandTheme.colors.outlineVariant),
          shadowElevation = 12.dp,
          modifier = Modifier.widthIn(max = 310.dp).fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Text(
              text = stringResource(R.string.modal_agreement_title),
              style = MaterialTheme.typography.headlineMedium,
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = alertBodyText,
              style = MaterialTheme.typography.bodySmall,
              textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              SecondaryPillButton(
                text = stringResource(R.string.modal_agreement_cancel),
                onClick = onDismiss,
                modifier = Modifier.weight(1f).testTag("modal_agreement_cancel"),
              )
              PrimaryPillButton(
                text = stringResource(R.string.modal_agreement_confirm),
                loadingText = stringResource(R.string.action_logging_in),
                isLoading = false,
                onClick = onAgreementConfirm,
                modifier = Modifier.weight(1f).testTag("modal_agreement_confirm"),
              )
            }
          }
        }
      }
    }

    AuthModal.SmsSent -> {
      Dialog(onDismissRequest = onDismiss) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = MaterialTheme.colorScheme.surface,
          border = BorderStroke(1.dp, IslandTheme.colors.outlineVariant),
          shadowElevation = 12.dp,
          modifier = Modifier.widthIn(max = 310.dp).fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Text(
              text = stringResource(R.string.modal_sms_title),
              style = MaterialTheme.typography.headlineMedium,
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = stringResource(R.string.modal_sms_body),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            PrimaryPillButton(
              text = stringResource(R.string.modal_known),
              loadingText = "",
              isLoading = false,
              onClick = onDismiss,
              modifier = Modifier.testTag("modal_sms_close"),
            )
          }
        }
      }
    }

    AuthModal.LoginSuccess -> {
      Dialog(onDismissRequest = onDismiss) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = MaterialTheme.colorScheme.surface,
          border = BorderStroke(1.dp, IslandTheme.colors.outlineVariant),
          shadowElevation = 12.dp,
          modifier = Modifier.widthIn(max = 310.dp).fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier =
                Modifier.size(44.dp)
                  .clip(CircleShape)
                  .background(IslandTheme.colors.success.copy(alpha = 0.1f)),
            ) {
              Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = stringResource(R.string.cd_success_icon),
                tint = IslandTheme.colors.success,
                modifier = Modifier.size(24.dp),
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = stringResource(R.string.modal_success_title),
              style = MaterialTheme.typography.headlineMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = stringResource(R.string.modal_success_desc),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = stringResource(R.string.modal_success_body),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryPillButton(
              text = stringResource(R.string.modal_enter_home),
              loadingText = "",
              isLoading = false,
              onClick = onLoginSuccessEnterHome,
              modifier = Modifier.testTag("modal_success_enter_home"),
            )
            Spacer(modifier = Modifier.height(8.dp))
            SecondaryPillButton(
              text = stringResource(R.string.modal_known),
              onClick = onDismiss,
              modifier = Modifier.testTag("modal_success_close"),
            )
          }
        }
      }
    }

    AuthModal.ResetSuccess -> {
      Dialog(onDismissRequest = onDismiss) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = MaterialTheme.colorScheme.surface,
          border = BorderStroke(1.dp, IslandTheme.colors.outlineVariant),
          shadowElevation = 12.dp,
          modifier = Modifier.widthIn(max = 310.dp).fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier =
                Modifier.size(44.dp)
                  .clip(CircleShape)
                  .background(IslandTheme.colors.success.copy(alpha = 0.1f)),
            ) {
              Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = stringResource(R.string.cd_success_icon),
                tint = IslandTheme.colors.success,
                modifier = Modifier.size(24.dp),
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = stringResource(R.string.modal_reset_title),
              style = MaterialTheme.typography.headlineMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = stringResource(R.string.modal_reset_desc),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = stringResource(R.string.modal_reset_body),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryPillButton(
              text = stringResource(R.string.modal_back_password_login),
              loadingText = "",
              isLoading = false,
              onClick = onResetSuccessBackToPasswordLogin,
              modifier = Modifier.testTag("modal_reset_back_login"),
            )
          }
        }
      }
    }

    is AuthModal.PolicyDrawer -> {
      val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
      val isPrivacy = activeModal.title.contains("隐私")
      ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
      ) {
        Column(
          modifier =
            Modifier.fillMaxWidth()
              .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
              .navigationBarsPadding(),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = activeModal.title,
              style = MaterialTheme.typography.headlineMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Surface(
              onClick = onDismiss,
              shape = CircleShape,
              color = IslandTheme.colors.surfaceContainer,
              modifier = Modifier.minimumInteractiveComponentSize().size(36.dp),
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Filled.Close,
                  contentDescription = stringResource(R.string.cd_close),
                  tint = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.size(18.dp),
                )
              }
            }
          }

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text =
                stringResource(
                  if (isPrivacy) R.string.policy_privacy_p1 else R.string.policy_user_p1
                ),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
              text =
                stringResource(
                  if (isPrivacy) R.string.policy_privacy_p2 else R.string.policy_user_p2
                ),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
              text =
                stringResource(
                  if (isPrivacy) R.string.policy_privacy_p3 else R.string.policy_user_p3
                ),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }

          Spacer(modifier = Modifier.height(8.dp))
          PrimaryPillButton(
            text = stringResource(R.string.action_policy_agreed),
            loadingText = "",
            isLoading = false,
            onClick = onAcceptPolicyDrawer,
            modifier = Modifier.testTag("modal_policy_agree"),
          )
        }
      }
    }

    null -> Unit
  }
}

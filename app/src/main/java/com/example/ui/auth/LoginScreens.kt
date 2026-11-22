package com.example.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.MonoLargeStyle

@Composable
fun AuthFlowCardContent(
  uiState: AuthUiState,
  onNavigateToStep: (AuthStep) -> Unit,
  onOneClickLogin: () -> Unit,
  onPhoneChange: (String) -> Unit,
  onClearPhone: () -> Unit,
  onCodeChange: (String) -> Unit,
  onSendCodeClick: () -> Boolean,
  onPasswordChange: (String) -> Unit,
  onTogglePasswordVisibility: () -> Unit,
  onNewPasswordChange: (String) -> Unit,
  onToggleNewPasswordVisibility: () -> Unit,
  onCodeLoginSubmit: () -> Unit,
  onPasswordLoginSubmit: () -> Unit,
  onResetPasswordSubmit: () -> Unit,
  onToggleAgreed: (Boolean) -> Unit,
  onOpenPolicy: (String) -> Unit,
) {
  val cardGroupKey =
    when (uiState.currentStep) {
      AuthStep.LOGIN_START -> 0
      AuthStep.PHONE_CODE,
      AuthStep.PHONE_PASSWORD -> 1
      AuthStep.FORGOT_PASSWORD -> 2
    }

  AnimatedContent(
    targetState = cardGroupKey,
    transitionSpec = {
      fadeIn(tween(150)) togetherWith fadeOut(tween(100))
    },
    label = "authCardTransition",
  ) { group ->
    when (group) {
      0 -> {
        LoginStartCard(
          uiState = uiState,
          onOneClickLogin = onOneClickLogin,
          onNavigateToPhoneCodeLogin = { onNavigateToStep(AuthStep.PHONE_CODE) },
          onToggleAgreed = onToggleAgreed,
          onOpenPolicy = onOpenPolicy,
        )
      }
      1 -> {
        PhoneLoginCard(
          uiState = uiState,
          isPasswordTab = uiState.currentStep == AuthStep.PHONE_PASSWORD,
          onBackClick = { onNavigateToStep(AuthStep.LOGIN_START) },
          onSelectCodeTab = { onNavigateToStep(AuthStep.PHONE_CODE) },
          onSelectPasswordTab = { onNavigateToStep(AuthStep.PHONE_PASSWORD) },
          onPhoneChange = onPhoneChange,
          onClearPhone = onClearPhone,
          onCodeChange = onCodeChange,
          onSendCodeClick = onSendCodeClick,
          onPasswordChange = onPasswordChange,
          onTogglePasswordVisibility = onTogglePasswordVisibility,
          onForgotPasswordClick = { onNavigateToStep(AuthStep.FORGOT_PASSWORD) },
          onCodeLoginSubmit = onCodeLoginSubmit,
          onPasswordLoginSubmit = onPasswordLoginSubmit,
          onToggleAgreed = onToggleAgreed,
          onOpenPolicy = onOpenPolicy,
        )
      }
      else -> {
        ForgotPasswordCard(
          uiState = uiState,
          onBackClick = { onNavigateToStep(AuthStep.PHONE_PASSWORD) },
          onPhoneChange = onPhoneChange,
          onClearPhone = onClearPhone,
          onCodeChange = onCodeChange,
          onSendCodeClick = onSendCodeClick,
          onNewPasswordChange = onNewPasswordChange,
          onToggleNewPasswordVisibility = onToggleNewPasswordVisibility,
          onSubmitReset = onResetPasswordSubmit,
        )
      }
    }
  }
}

@Composable
private fun LoginStartCard(
  uiState: AuthUiState,
  onOneClickLogin: () -> Unit,
  onNavigateToPhoneCodeLogin: () -> Unit,
  onToggleAgreed: (Boolean) -> Unit,
  onOpenPolicy: (String) -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = uiState.maskedLocalPhone,
      style = MonoLargeStyle,
      color = MaterialTheme.colorScheme.onSurface,
      textAlign = TextAlign.Center,
      modifier = Modifier.fillMaxWidth().testTag("masked_phone_text"),
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
      PrimaryPillButton(
        text = stringResource(R.string.action_one_click_login),
        loadingText = stringResource(R.string.action_logging_in),
        isLoading = uiState.isSubmitting,
        onClick = onOneClickLogin,
        modifier = Modifier.testTag("one_click_login_button"),
      )

      SecondaryPillButton(
        text = stringResource(R.string.action_phone_login),
        enabled = !uiState.isSubmitting,
        onClick = onNavigateToPhoneCodeLogin,
        modifier = Modifier.testTag("other_phone_login_button"),
      )
    }

    AgreementRow(
      isAgreed = uiState.isAgreed,
      shakeTick = uiState.agreeShakeTick,
      showHighlight = uiState.showAgreeHighlight,
      showTooltip = uiState.showAgreeTooltip,
      onToggleAgreed = onToggleAgreed,
      onPolicyClick = onOpenPolicy,
    )
  }
}

@Composable
private fun PhoneLoginCard(
  uiState: AuthUiState,
  isPasswordTab: Boolean,
  onBackClick: () -> Unit,
  onSelectCodeTab: () -> Unit,
  onSelectPasswordTab: () -> Unit,
  onPhoneChange: (String) -> Unit,
  onClearPhone: () -> Unit,
  onCodeChange: (String) -> Unit,
  onSendCodeClick: () -> Boolean,
  onPasswordChange: (String) -> Unit,
  onTogglePasswordVisibility: () -> Unit,
  onForgotPasswordClick: () -> Unit,
  onCodeLoginSubmit: () -> Unit,
  onPasswordLoginSubmit: () -> Unit,
  onToggleAgreed: (Boolean) -> Unit,
  onOpenPolicy: (String) -> Unit,
) {
  val phoneFocus = remember { FocusRequester() }
  val codeFocus = remember { FocusRequester() }
  val passwordFocus = remember { FocusRequester() }

  Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
    AuthBackHeader(
      title = stringResource(R.string.title_phone_login),
      subtitle =
        stringResource(
          if (isPasswordTab) R.string.subtitle_password_login else R.string.subtitle_code_login
        ),
      backContentDescription = stringResource(R.string.cd_back_to_login_start),
      onBackClick = onBackClick,
    )

    LoginModeTabBar(
      selectedTab = if (isPasswordTab) 1 else 0,
      onSelectCodeTab = onSelectCodeTab,
      onSelectPasswordTab = onSelectPasswordTab,
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
      PhoneInputField(
        label = stringResource(R.string.label_phone),
        value = uiState.phone,
        placeholder = stringResource(R.string.hint_phone),
        isError = uiState.phoneError,
        errorText = stringResource(R.string.error_phone),
        onValueChange = onPhoneChange,
        onClear = onClearPhone,
        focusRequester = phoneFocus,
        imeAction = ImeAction.Next,
        onNext = {
          if (isPasswordTab) passwordFocus.requestFocus() else codeFocus.requestFocus()
        },
      )

      if (!isPasswordTab) {
        SmsCodeInputField(
          value = uiState.smsCode,
          countdownSeconds = uiState.smsCountdownSeconds,
          isError = uiState.codeError,
          onValueChange = onCodeChange,
          onSendCodeClick = {
            val sent = onSendCodeClick()
            if (sent) codeFocus.requestFocus() else if (uiState.smsCountdownSeconds == 0) phoneFocus.requestFocus()
          },
          focusRequester = codeFocus,
          imeAction = ImeAction.Done,
          onDone = onCodeLoginSubmit,
        )

        Spacer(modifier = Modifier.height(2.dp))

        PrimaryPillButton(
          text = stringResource(R.string.action_verify_and_login),
          loadingText = stringResource(R.string.action_logging_in),
          isLoading = uiState.isSubmitting,
          onClick = onCodeLoginSubmit,
          modifier = Modifier.testTag("submit_code_login_button"),
        )
      } else {
        Column(modifier = Modifier.fillMaxWidth()) {
          PasswordInputField(
            label = stringResource(R.string.label_password),
            value = uiState.password,
            placeholder = stringResource(R.string.hint_password),
            isPasswordVisible = uiState.isPasswordVisible,
            isError = uiState.passwordError,
            errorText = stringResource(R.string.error_password_login),
            onValueChange = onPasswordChange,
            onToggleVisibility = onTogglePasswordVisibility,
            focusRequester = passwordFocus,
            onDone = onPasswordLoginSubmit,
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(
              onClick = onForgotPasswordClick,
              modifier = Modifier.heightIn(min = 44.dp).testTag("forgot_password_link"),
            ) {
              Text(
                text = stringResource(R.string.action_forgot_password),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }

        PrimaryPillButton(
          text = stringResource(R.string.action_login_now),
          loadingText = stringResource(R.string.action_logging_in),
          isLoading = uiState.isSubmitting,
          onClick = onPasswordLoginSubmit,
          modifier = Modifier.testTag("submit_password_login_button"),
        )
      }
    }

    AgreementRow(
      isAgreed = uiState.isAgreed,
      shakeTick = uiState.agreeShakeTick,
      showHighlight = uiState.showAgreeHighlight,
      showTooltip = uiState.showAgreeTooltip,
      onToggleAgreed = onToggleAgreed,
      onPolicyClick = onOpenPolicy,
    )
  }
}

@Composable
private fun ForgotPasswordCard(
  uiState: AuthUiState,
  onBackClick: () -> Unit,
  onPhoneChange: (String) -> Unit,
  onClearPhone: () -> Unit,
  onCodeChange: (String) -> Unit,
  onSendCodeClick: () -> Boolean,
  onNewPasswordChange: (String) -> Unit,
  onToggleNewPasswordVisibility: () -> Unit,
  onSubmitReset: () -> Unit,
) {
  val phoneFocus = remember { FocusRequester() }
  val codeFocus = remember { FocusRequester() }
  val newPwdFocus = remember { FocusRequester() }

  Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
    AuthBackHeader(
      title = stringResource(R.string.title_forgot_password),
      subtitle = stringResource(R.string.subtitle_forgot_password),
      backContentDescription = stringResource(R.string.cd_back_to_password_login),
      onBackClick = onBackClick,
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
      PhoneInputField(
        label = stringResource(R.string.label_phone),
        value = uiState.phone,
        placeholder = stringResource(R.string.hint_registered_phone),
        isError = uiState.phoneError,
        errorText = stringResource(R.string.error_phone),
        onValueChange = onPhoneChange,
        onClear = onClearPhone,
        focusRequester = phoneFocus,
        imeAction = ImeAction.Next,
        onNext = { codeFocus.requestFocus() },
      )

      SmsCodeInputField(
        value = uiState.smsCode,
        countdownSeconds = uiState.smsCountdownSeconds,
        isError = uiState.codeError,
        onValueChange = onCodeChange,
        onSendCodeClick = {
          val sent = onSendCodeClick()
          if (sent) codeFocus.requestFocus() else if (uiState.smsCountdownSeconds == 0) phoneFocus.requestFocus()
        },
        focusRequester = codeFocus,
        imeAction = ImeAction.Next,
        onNext = { newPwdFocus.requestFocus() },
      )

      PasswordInputField(
        label = stringResource(R.string.label_new_password),
        value = uiState.newPassword,
        placeholder = stringResource(R.string.hint_new_password),
        isPasswordVisible = uiState.isNewPasswordVisible,
        isError = uiState.newPasswordError,
        errorText = stringResource(R.string.error_password_reset),
        onValueChange = onNewPasswordChange,
        onToggleVisibility = onToggleNewPasswordVisibility,
        focusRequester = newPwdFocus,
        onDone = onSubmitReset,
      )

      Spacer(modifier = Modifier.height(2.dp))

      PrimaryPillButton(
        text = stringResource(R.string.action_reset_password),
        loadingText = stringResource(R.string.action_submitting),
        isLoading = uiState.isSubmitting,
        onClick = onSubmitReset,
        modifier = Modifier.testTag("submit_reset_button"),
      )
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
      TextButton(
        onClick = onBackClick,
        modifier = Modifier.heightIn(min = 44.dp).testTag("remember_password_back_link"),
      ) {
        Text(
          text = stringResource(R.string.action_remember_password_back),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

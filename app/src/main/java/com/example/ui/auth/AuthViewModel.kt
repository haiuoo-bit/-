package com.example.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthStep {
  LOGIN_START,
  PHONE_CODE,
  PHONE_PASSWORD,
  FORGOT_PASSWORD,
}

enum class PendingAgreementAction {
  ONE_CLICK_LOGIN,
  CODE_LOGIN,
  PASSWORD_LOGIN,
}

sealed interface AuthModal {
  data object AgreementAlert : AuthModal

  data object SmsSent : AuthModal

  data object LoginSuccess : AuthModal

  data object ResetSuccess : AuthModal

  data class PolicyDrawer(val title: String) : AuthModal
}

sealed interface AuthEvent {
  data object NavigateToHome : AuthEvent
}

data class AuthUiState(
  val currentStep: AuthStep = AuthStep.LOGIN_START,
  val maskedLocalPhone: String = "191****7207",
  val isAgreed: Boolean = false,
  val agreeShakeTick: Int = 0,
  val showAgreeHighlight: Boolean = false,
  val showAgreeTooltip: Boolean = false,
  val phone: String = "",
  val smsCode: String = "",
  val password: String = "",
  val newPassword: String = "",
  val isPasswordVisible: Boolean = false,
  val isNewPasswordVisible: Boolean = false,
  val phoneError: Boolean = false,
  val codeError: Boolean = false,
  val passwordError: Boolean = false,
  val newPasswordError: Boolean = false,
  val smsCountdownSeconds: Int = 0,
  val isSubmitting: Boolean = false,
  val activeModal: AuthModal? = null,
  val pendingAction: PendingAgreementAction? = null,
  val lastLoginMethodLabel: String = "本机号码一键登录",
  val lastAccountDisplay: String = "191****7207",
)

class AuthViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(AuthUiState())
  val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

  private val _events = MutableSharedFlow<AuthEvent>()
  val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

  private var countdownJob: Job? = null
  private var tooltipJob: Job? = null

  fun navigateToStep(step: AuthStep) {
    _uiState.update {
      it.copy(
        currentStep = step,
        phoneError = false,
        codeError = false,
        passwordError = false,
        newPasswordError = false,
        showAgreeHighlight = false,
        showAgreeTooltip = false,
      )
    }
  }

  fun navigateBack(): Boolean {
    return when (_uiState.value.currentStep) {
      AuthStep.FORGOT_PASSWORD -> {
        navigateToStep(AuthStep.PHONE_PASSWORD)
        true
      }
      AuthStep.PHONE_CODE,
      AuthStep.PHONE_PASSWORD -> {
        navigateToStep(AuthStep.LOGIN_START)
        true
      }
      AuthStep.LOGIN_START -> false
    }
  }

  fun setAgreed(agreed: Boolean) {
    _uiState.update {
      it.copy(
        isAgreed = agreed,
        showAgreeHighlight = if (agreed) false else it.showAgreeHighlight,
        showAgreeTooltip = if (agreed) false else it.showAgreeTooltip,
      )
    }
  }

  fun onPhoneChange(raw: String) {
    val digitsOnly = raw.filter { it.isDigit() }.take(11)
    _uiState.update { it.copy(phone = digitsOnly, phoneError = false) }
  }

  fun clearPhone() {
    _uiState.update { it.copy(phone = "", phoneError = false) }
  }

  fun onSmsCodeChange(raw: String) {
    val digitsOnly = raw.filter { it.isDigit() }.take(6)
    _uiState.update { it.copy(smsCode = digitsOnly, codeError = false) }
  }

  fun onPasswordChange(value: String) {
    _uiState.update { it.copy(password = value, passwordError = false) }
  }

  fun togglePasswordVisibility() {
    _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
  }

  fun onNewPasswordChange(value: String) {
    _uiState.update { it.copy(newPassword = value, newPasswordError = false) }
  }

  fun toggleNewPasswordVisibility() {
    _uiState.update { it.copy(isNewPasswordVisible = !it.isNewPasswordVisible) }
  }

  fun openPolicyDrawer(title: String) {
    _uiState.update { it.copy(activeModal = AuthModal.PolicyDrawer(title)) }
  }

  fun dismissModal() {
    _uiState.update { it.copy(activeModal = null) }
  }

  fun acceptPolicyFromDrawer() {
    val currentPending = _uiState.value.pendingAction
    _uiState.update {
      it.copy(
        isAgreed = true,
        showAgreeHighlight = false,
        showAgreeTooltip = false,
        activeModal = null,
      )
    }
    if (currentPending != null) {
      executePendingAction(currentPending)
    }
  }

  fun confirmAgreementDialogAndLogin() {
    val action = _uiState.value.pendingAction ?: PendingAgreementAction.ONE_CLICK_LOGIN
    _uiState.update {
      it.copy(
        isAgreed = true,
        showAgreeHighlight = false,
        showAgreeTooltip = false,
        activeModal = null,
        pendingAction = null,
      )
    }
    executePendingAction(action)
  }

  fun onOneClickLoginClicked() {
    if (_uiState.value.isSubmitting) return
    if (!_uiState.value.isAgreed) {
      triggerAgreeWarning(PendingAgreementAction.ONE_CLICK_LOGIN)
      return
    }
    executePendingAction(PendingAgreementAction.ONE_CLICK_LOGIN)
  }

  fun onSendCodeClicked(): Boolean {
    if (_uiState.value.smsCountdownSeconds > 0) return false
    if (!isValidPhone(_uiState.value.phone)) {
      _uiState.update { it.copy(phoneError = true) }
      return false
    }
    _uiState.update {
      it.copy(smsCountdownSeconds = 60, activeModal = AuthModal.SmsSent)
    }
    startCountdown()
    return true
  }

  fun onCodeLoginSubmit() {
    if (_uiState.value.isSubmitting) return
    val phoneOk = isValidPhone(_uiState.value.phone)
    val codeOk = isValidSmsCode(_uiState.value.smsCode)
    if (!phoneOk || !codeOk) {
      _uiState.update { it.copy(phoneError = !phoneOk, codeError = !codeOk) }
      return
    }
    if (!_uiState.value.isAgreed) {
      triggerAgreeWarning(PendingAgreementAction.CODE_LOGIN)
      return
    }
    executePendingAction(PendingAgreementAction.CODE_LOGIN)
  }

  fun onPasswordLoginSubmit() {
    if (_uiState.value.isSubmitting) return
    val phoneOk = isValidPhone(_uiState.value.phone)
    val pwdOk = _uiState.value.password.length >= 6
    if (!phoneOk || !pwdOk) {
      _uiState.update { it.copy(phoneError = !phoneOk, passwordError = !pwdOk) }
      return
    }
    if (!_uiState.value.isAgreed) {
      triggerAgreeWarning(PendingAgreementAction.PASSWORD_LOGIN)
      return
    }
    executePendingAction(PendingAgreementAction.PASSWORD_LOGIN)
  }

  fun onResetPasswordSubmit() {
    if (_uiState.value.isSubmitting) return
    val phoneOk = isValidPhone(_uiState.value.phone)
    val codeOk = isValidSmsCode(_uiState.value.smsCode)
    val newPwdOk = _uiState.value.newPassword.length in 6..20
    if (!phoneOk || !codeOk || !newPwdOk) {
      _uiState.update {
        it.copy(
          phoneError = !phoneOk,
          codeError = !codeOk,
          newPasswordError = !newPwdOk,
        )
      }
      return
    }
    viewModelScope.launch {
      _uiState.update { it.copy(isSubmitting = true) }
      delay(420)
      _uiState.update {
        it.copy(isSubmitting = false, activeModal = AuthModal.ResetSuccess)
      }
    }
  }

  fun resetDemoAgreement() {
    _uiState.update {
      it.copy(
        currentStep = AuthStep.LOGIN_START,
        isAgreed = false,
        showAgreeHighlight = false,
        showAgreeTooltip = false,
        activeModal = null,
        pendingAction = null,
      )
    }
  }

  private fun executePendingAction(action: PendingAgreementAction) {
    viewModelScope.launch {
      _uiState.update { it.copy(isSubmitting = true, pendingAction = null) }
      when (action) {
        PendingAgreementAction.ONE_CLICK_LOGIN -> {
          delay(600)
          val masked = _uiState.value.maskedLocalPhone
          _uiState.update {
            it.copy(
              isSubmitting = false,
              lastLoginMethodLabel = "本机号码一键登录",
              lastAccountDisplay = masked,
            )
          }
          _events.emit(AuthEvent.NavigateToHome)
        }
        PendingAgreementAction.CODE_LOGIN -> {
          delay(420)
          val masked = maskPhone(_uiState.value.phone)
          _uiState.update {
            it.copy(
              isSubmitting = false,
              activeModal = AuthModal.LoginSuccess,
              lastLoginMethodLabel = "手机号验证码登录",
              lastAccountDisplay = masked,
            )
          }
        }
        PendingAgreementAction.PASSWORD_LOGIN -> {
          delay(420)
          val masked = maskPhone(_uiState.value.phone)
          _uiState.update {
            it.copy(
              isSubmitting = false,
              activeModal = AuthModal.LoginSuccess,
              lastLoginMethodLabel = "手机号密码登录",
              lastAccountDisplay = masked,
            )
          }
        }
      }
    }
  }

  private fun triggerAgreeWarning(action: PendingAgreementAction) {
    _uiState.update {
      it.copy(
        agreeShakeTick = it.agreeShakeTick + 1,
        showAgreeHighlight = true,
        showAgreeTooltip = true,
        pendingAction = action,
        activeModal = AuthModal.AgreementAlert,
      )
    }
    tooltipJob?.cancel()
    tooltipJob =
      viewModelScope.launch {
        delay(2200)
        _uiState.update { it.copy(showAgreeTooltip = false) }
      }
  }

  private fun startCountdown() {
    countdownJob?.cancel()
    countdownJob =
      viewModelScope.launch {
        while (_uiState.value.smsCountdownSeconds > 0) {
          delay(1000)
          _uiState.update { state ->
            state.copy(smsCountdownSeconds = (state.smsCountdownSeconds - 1).coerceAtLeast(0))
          }
        }
      }
  }

  private fun isValidPhone(phone: String): Boolean {
    val trimmed = phone.trim()
    return trimmed.length == 11 && trimmed.startsWith("1") && trimmed.all { it.isDigit() }
  }

  private fun isValidSmsCode(code: String): Boolean {
    val trimmed = code.trim()
    return trimmed.length in 4..6 && trimmed.all { it.isDigit() }
  }

  private fun maskPhone(phone: String): String {
    return if (phone.length == 11) {
      "${phone.take(3)}****${phone.takeLast(4)}"
    } else {
      phone
    }
  }
}

package com.adielcalixto.ifacademico.presentation.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adielcalixto.ifacademico.data.remote.ReleaseInfo
import com.adielcalixto.ifacademico.data.remote.UpdateCheckResult
import com.adielcalixto.ifacademico.data.remote.UpdateChecker
import com.adielcalixto.ifacademico.data.local.SettingsPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UpdateState(
    val isUpdateCheckEnabled: Boolean = true,
    val pendingUpdate: ReleaseInfo? = null,
)

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val updateChecker: UpdateChecker,
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(UpdateState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(isUpdateCheckEnabled = settingsPreferences.isUpdateCheckEnabled.first())
            }
        }
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            when (val result = updateChecker.check()) {
                is UpdateCheckResult.UpdateAvailable -> {
                    _state.update { it.copy(pendingUpdate = result.info) }
                }

                is UpdateCheckResult.UpToDate -> {}
            }
        }
    }

    fun skipVersion() {
        val pending = _state.value.pendingUpdate ?: return
        viewModelScope.launch {
            settingsPreferences.setSkippedVersion(pending.latestVersion)
            _state.update { it.copy(pendingUpdate = null) }
        }
    }

    fun setUpdateCheckEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsPreferences.setUpdateCheckEnabled(enabled)
            _state.update { it.copy(isUpdateCheckEnabled = enabled) }
        }
    }
}

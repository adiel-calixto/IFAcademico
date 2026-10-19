package com.adielcalixto.ifacademico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adielcalixto.ifacademico.data.local.SettingsPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    private val _theme = MutableStateFlow(Theme.SYSTEM)
    val theme = _theme.asStateFlow()

    init {
        viewModelScope.launch {
            settingsPreferences.theme.collect { savedTheme ->
                _theme.value = savedTheme
            }
        }
    }

    fun changeTheme(theme: Theme) {
        viewModelScope.launch {
            settingsPreferences.setTheme(theme)
        }
        _theme.update { theme }
    }
}

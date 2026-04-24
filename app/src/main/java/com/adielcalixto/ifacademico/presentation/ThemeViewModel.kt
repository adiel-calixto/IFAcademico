package com.adielcalixto.ifacademico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adielcalixto.ifacademico.data.local.SettingsPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    private val _theme = MutableStateFlow(Theme.DARK)
    val theme = _theme.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val theme = settingsPreferences.theme.first()
            _theme.update { theme }
        }
    }

    fun changeTheme(theme: Theme) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsPreferences.setTheme(theme)
        }
        _theme.update { theme }
    }
}

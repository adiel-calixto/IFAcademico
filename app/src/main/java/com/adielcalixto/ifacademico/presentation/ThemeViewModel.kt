package com.adielcalixto.ifacademico.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adielcalixto.ifacademico.data.local.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(private val themePreferences: ThemePreferences) :
    ViewModel() {
    private val _theme = MutableStateFlow(Theme.DARK)
    val theme = _theme.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val theme = themePreferences.getTheme()

            if (theme != null)
                _theme.update { theme }
        }
    }

    fun changeTheme(theme: Theme) {
        viewModelScope.launch(Dispatchers.IO) {
            themePreferences.saveTheme(theme)
        }

        _theme.update { theme }
    }
}
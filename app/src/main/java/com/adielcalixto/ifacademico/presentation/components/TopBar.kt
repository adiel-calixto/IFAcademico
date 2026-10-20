package com.adielcalixto.ifacademico.presentation.components

import androidx.compose.runtime.Composable

@Composable
fun TopBar(onProfile: () -> Unit, periodControl: (@Composable () -> Unit)? = null) {
    IFHeader(periodControl = periodControl, onProfile = onProfile)
}

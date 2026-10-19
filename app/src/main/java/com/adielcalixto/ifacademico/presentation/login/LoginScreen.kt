package com.adielcalixto.ifacademico.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.UiText
import com.adielcalixto.ifacademico.presentation.designsystem.IFButton as Button
import com.adielcalixto.ifacademico.presentation.designsystem.IFIcon
import com.adielcalixto.ifacademico.presentation.designsystem.IFIconButton as IconButton
import com.adielcalixto.ifacademico.presentation.designsystem.IFOutlinedTextField as OutlinedTextField
import com.adielcalixto.ifacademico.presentation.designsystem.IFTextButton as TextButton
import com.adielcalixto.ifacademico.presentation.designsystem.IFTheme

@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel(), onLoginSuccess: () -> Unit) {
    val state by viewModel.state.collectAsState()
    val isDark = IFTheme.isDark

    Column(
        Modifier.padding(horizontal = 24.dp, vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            painterResource(if (isDark) R.drawable.logo else R.drawable.logo_inv),
            contentDescription = "Logo",
            modifier = Modifier
                .size(96.dp)
        )

        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = "LOGIN",
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.registration,
            onValueChange = { viewModel.onAction(LoginAction.UsernameChanged(it)) },
            label = { Text(UiText.StringResource(R.string.registration).asString()) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            visualTransformation = if (state.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { viewModel.onAction(LoginAction.PasswordChanged(it)) },
            label = { Text(UiText.StringResource(R.string.password).asString()) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                val image =
                    if (state.showPassword) IFIcon.Visibility.vector else IFIcon.VisibilityOff.vector
                val description =
                    UiText.StringResource(if (state.showPassword) R.string.hide_password else R.string.show_password)
                        .asString()

                IconButton(onClick = { viewModel.onAction(LoginAction.TogglePasswordClicked) }) {
                    Icon(
                        imageVector = image,
                        description
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (state.registration.isNotEmpty() && state.password.isNotEmpty()) {
                    viewModel.onAction(LoginAction.LoginClicked(onLoginSuccess))
                }
            },
            modifier = Modifier
                .heightIn(min = 48.dp)
                .fillMaxWidth(),
            enabled = !state.loading,
        ) {
            Text(UiText.StringResource(R.string.login).asString())
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = { viewModel.onAction(LoginAction.ShowPrivacyPolicy) }) {
            Text(UiText.StringResource(R.string.privacy_policy).asString())
        }

        TextButton(onClick = { viewModel.onAction(LoginAction.ShowTOS) }) {
            Text(UiText.StringResource(R.string.terms_of_service).asString())
        }

        if (state.loading) {
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }

        if (state.showPrivacyPolicy) {
            AlertDialog(
                onDismissRequest = { viewModel.onAction(LoginAction.DismissPrivacyPolicy) },
                confirmButton = { },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.onAction(LoginAction.DismissPrivacyPolicy) }
                    ) {
                        Text(UiText.StringResource(R.string.dismiss).asString())
                    }
                },
                text = {
                    Text(UiText.StringResource(R.string.privacy_policy_details).asString())
                }
            )
        }

        if (state.showTOS) {
            AlertDialog(
                onDismissRequest = { viewModel.onAction(LoginAction.DismissTOS) },
                confirmButton = { },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.onAction(LoginAction.DismissTOS) }
                    ) {
                        Text(UiText.StringResource(R.string.dismiss).asString())
                    }
                },
                text = {
                    Text(UiText.StringResource(R.string.terms_of_service_details).asString())
                }
            )
        }

        if (state.error != null)
            AlertDialog(
                icon = {
                    Icon(
                        IFIcon.Warning.vector,
                        contentDescription = UiText.StringResource(R.string.warning).asString()
                    )
                },
                title = {
                    Text(text = UiText.StringResource(R.string.error_ocurred).asString())
                },
                text = {
                    Text(text = state.error!!.asString(), textAlign = TextAlign.Center)
                },
                onDismissRequest = { viewModel.onAction(LoginAction.DismissErrorClicked) },
                confirmButton = {},
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.onAction(LoginAction.DismissErrorClicked) }
                    ) {
                        Text(UiText.StringResource(R.string.dismiss).asString())
                    }
                }
            )
    }
}
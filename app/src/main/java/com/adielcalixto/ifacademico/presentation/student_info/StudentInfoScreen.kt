package com.adielcalixto.ifacademico.presentation.student_info

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adielcalixto.ifacademico.MainActivity
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.entities.Student
import com.adielcalixto.ifacademico.presentation.Theme
import com.adielcalixto.ifacademico.presentation.ThemeViewModel
import com.adielcalixto.ifacademico.presentation.UiText
import com.adielcalixto.ifacademico.presentation.asUiText

@Composable
fun StudentInfoScreen(
    student: Student,
    themeViewModel: ThemeViewModel = hiltViewModel(LocalContext.current as ViewModelStoreOwner),
    onLogout: () -> Unit
) {
    var openLogoutDialog by remember { mutableStateOf(false) }
    var dropDownExpanded by remember { mutableStateOf(false) }
    val theme by themeViewModel.theme.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            tonalElevation = 1.dp
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .size(50.dp)
                    .padding(16.dp),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = student.name,
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = student.courseName,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = UiText.StringResource(R.string.preferences).asString(),
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { dropDownExpanded = !dropDownExpanded })
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.DarkMode, contentDescription = "Theme Icon")
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        UiText.StringResource(R.string.theme).asString(),
                        modifier = Modifier.weight(1f)
                    )

                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                        Row {
                            Text(theme.asUiText().asString())
                            Spacer(modifier = Modifier.padding(2.dp))
                            Icon(Icons.Filled.ArrowDropDown, "Arrow down")
                        }
                        DropdownMenu(
                            modifier = Modifier.padding(4.dp),
                            expanded = dropDownExpanded,
                            onDismissRequest = {
                                dropDownExpanded = false
                            }) {
                            Theme.entries.forEach { colorscheme ->
                                DropdownMenuItem(
                                    onClick = {
                                        dropDownExpanded = false
                                        themeViewModel.changeTheme(colorscheme)
                                    }) {
                                    Box(contentAlignment = Alignment.CenterEnd) {
                                        Text(colorscheme.asUiText().asString())
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                // Logout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openLogoutDialog = true }
                        .clip(RoundedCornerShape(12.dp))
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        UiText.StringResource(R.string.logout).asString(),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (openLogoutDialog) {
                    AlertDialog(
                        icon = {
                            Icon(
                                Icons.Filled.Warning,
                                contentDescription = "Logout Warning",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        title = {
                            Text(text = UiText.StringResource(R.string.logout).asString())
                        },
                        text = {
                            Text(
                                text = UiText.StringResource(R.string.logout_warning).asString(),
                                textAlign = TextAlign.Center
                            )
                        },
                        onDismissRequest = { openLogoutDialog = false },
                        confirmButton = {
                            TextButton(
                                onClick = { openLogoutDialog = false; onLogout() }
                            ) {
                                Text(UiText.StringResource(R.string.confirm).asString())
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { openLogoutDialog = false }
                            ) {
                                Text(UiText.StringResource(R.string.dismiss).asString())
                            }
                        }
                    )
                }
            }
        }
    }
}
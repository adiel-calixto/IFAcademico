package com.adielcalixto.ifacademico.presentation.student_info

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.domain.entities.Student
import com.adielcalixto.ifacademico.presentation.IFSize
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.Theme
import com.adielcalixto.ifacademico.presentation.ThemeViewModel
import com.adielcalixto.ifacademico.presentation.components.*
import com.adielcalixto.ifacademico.presentation.student_info.components.IFInitialsAvatar
import com.adielcalixto.ifacademico.presentation.student_info.components.IFThemeSelector
import com.adielcalixto.ifacademico.presentation.student_info.components.IFUpdateSwitch

@Composable
fun StudentInfoScreen(
    student: Student,
    isUpdateCheckEnabled: Boolean,
    onUpdateCheckEnabledChange: (Boolean) -> Unit,
    onLogout: () -> Unit,
) {
    val themeViewModel: ThemeViewModel = hiltViewModel(LocalContext.current as ViewModelStoreOwner)
    val theme by themeViewModel.theme.collectAsState()
    val uriHandler = LocalUriHandler.current
    StudentInfoContent(
        student, theme, themeViewModel::changeTheme,
        isUpdateCheckEnabled, onUpdateCheckEnabledChange, onLogout,
        onOpenProject = { uriHandler.openUri("https://github.com/adiel-calixto/IFAcademico") },
    )
}

@Composable
internal fun StudentInfoContent(
    student: Student,
    theme: Theme,
    onThemeChange: (Theme) -> Unit,
    isUpdateCheckEnabled: Boolean,
    onUpdateCheckEnabledChange: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onOpenProject: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showLogout by rememberSaveable { mutableStateOf(false) }
    var dismissLogout by remember { mutableStateOf(false) }
    val logoutFocus = remember { FocusRequester() }

    BoxWithConstraints(modifier.fillMaxSize()) {
        val viewportHeight = maxHeight
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                .heightIn(min = viewportHeight)
                .padding(horizontal = IFTheme.pageInset, vertical = IFSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IFInitialsAvatar(student.name)
            Spacer(Modifier.height(IFSpacing.md))
            Text(
                student.name, Modifier.semantics { heading() },
                style = IFTheme.typography.titleLg, color = IFTheme.colors.onSurface,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(IFSpacing.micro))
            Text(
                student.courseName, style = IFTheme.typography.bodySm,
                color = IFTheme.colors.secondary, textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(IFSpacing.xl))
            Surface(
                Modifier.fillMaxWidth(), shape = IFTheme.shapes.lg,
                color = IFTheme.colors.surface,
            ) {
                Column(
                    Modifier.padding(IFSpacing.panel),
                    verticalArrangement = Arrangement.spacedBy(IFSpacing.md),
                ) {
                    IFSectionTitle(stringResource(R.string.preferences))
                    Row(
                        Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(IFSpacing.sm),
                    ) {
                        Text(
                            stringResource(R.string.theme), Modifier.weight(1f),
                            style = IFTheme.typography.labelMd, color = IFTheme.colors.onSurface,
                        )
                        IFThemeSelector(theme, onThemeChange, compact = true)
                    }
                    HorizontalDivider(color = IFTheme.colors.outline)
                    Row(
                        Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(IFSpacing.sm),
                    ) {
                        Text(
                            stringResource(R.string.check_for_updates), Modifier.weight(1f),
                            style = IFTheme.typography.labelMd, color = IFTheme.colors.onSurface,
                        )
                        IFUpdateSwitch(isUpdateCheckEnabled, onUpdateCheckEnabledChange)
                    }
                    HorizontalDivider(color = IFTheme.colors.outline)
                    IFTextButton(
                        onClick = { dismissLogout = false; showLogout = true },
                        modifier = Modifier.align(Alignment.End).focusRequester(logoutFocus),
                        destructive = true,
                    ) {
                        Icon(IFIcon.Logout.vector, null, Modifier.size(IFSize.icon))
                        Spacer(Modifier.width(IFSpacing.xs))
                        Text(stringResource(R.string.logout))
                    }
                }
            }
            Spacer(Modifier.height(IFSpacing.xl))
            Spacer(Modifier.weight(1f))
            Text(
                stringResource(R.string.app_name), style = IFTheme.typography.titleSm,
                color = IFTheme.colors.secondary,
            )
            IFTextButton(onOpenProject) {
                Icon(IFIcon.Code.vector, null, Modifier.size(IFSize.icon))
                Spacer(Modifier.width(IFSpacing.xs))
                Text(stringResource(R.string.view_on_github), style = IFTheme.typography.bodySm)
            }
        }
    }
    if (showLogout) {
        IFSlidingSheet(
            title = stringResource(R.string.logout),
            onDismiss = { showLogout = false },
            returnFocus = logoutFocus,
            dismissRequested = dismissLogout,
        ) {
            IFConfirmationContent(
                message = stringResource(R.string.logout_warning),
                confirmLabel = stringResource(R.string.logout),
                onConfirm = { showLogout = false; onLogout() },
                onCancel = { dismissLogout = true },
            )
        }
    }
}

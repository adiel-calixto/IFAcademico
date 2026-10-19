package com.adielcalixto.ifacademico.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme

@Preview(name = "Light", widthDp = 412, heightDp = 1000, showBackground = true)
@Preview(name = "Dark", widthDp = 412, heightDp = 1000, uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Narrow, enlarged text", widthDp = 360, heightDp = 1200, fontScale = 1.6f, showBackground = true)
@Preview(name = "Narrow, 200% text", widthDp = 360, heightDp = 1200, fontScale = 2f, showBackground = true)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class IFCatalogPreviews

@Composable
internal fun Catalog(content: @Composable ColumnScope.() -> Unit) {
    IFTheme {
        Surface(color = IFTheme.colors.background) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(IFTheme.pageInset),
                verticalArrangement = Arrangement.spacedBy(IFSpacing.lg), content = content)
        }
    }
}

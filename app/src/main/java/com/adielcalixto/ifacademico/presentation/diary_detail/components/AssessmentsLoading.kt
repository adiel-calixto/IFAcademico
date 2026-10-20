package com.adielcalixto.ifacademico.presentation.diary_detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.adielcalixto.ifacademico.R
import com.adielcalixto.ifacademico.presentation.IFSpacing
import com.adielcalixto.ifacademico.presentation.IFTheme
import com.adielcalixto.ifacademico.presentation.components.*

@Composable
internal fun AssessmentsLoading() {
    IFLoadingRegion(stringResource(R.string.loading_assessments)) {
        IFSectionTitle(stringResource(R.string.exams), detail = true)
        Spacer(Modifier.height(IFSpacing.md))
        IFSkeletonLine(.3f, style = IFTheme.typography.labelMd)
        repeat(2) {
            BoxWithConstraints(Modifier.fillMaxWidth().padding(vertical = IFSpacing.sm)) {
                val stacked = maxWidth < 280.dp * LocalDensity.current.fontScale
                if (stacked) {
                    Column(verticalArrangement = Arrangement.spacedBy(IFSpacing.xs)) {
                        AssessmentIdentitySkeleton()
                        AssessmentScoreSkeleton(Modifier.fillMaxWidth(.4f))
                    }
                } else {
                    val scoreWidth = maxWidth * .35f
                    Row(horizontalArrangement = Arrangement.spacedBy(IFSpacing.md)) {
                        AssessmentIdentitySkeleton(Modifier.weight(1f))
                        AssessmentScoreSkeleton(Modifier.width(scoreWidth))
                    }
                }
            }
            HorizontalDivider(color = IFTheme.colors.outline)
        }
    }
}

@Composable
private fun AssessmentIdentitySkeleton(modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(IFSpacing.micro)) {
        IFSkeletonLine(.9f, style = IFTheme.typography.bodyMd)
        IFSkeletonLine(.6f)
    }
}

@Composable
private fun AssessmentScoreSkeleton(modifier: Modifier) {
    Column(modifier) {
        IFSkeletonLine(.9f, style = IFTheme.typography.bodyMd)
        IFSkeletonLine(.75f)
    }
}

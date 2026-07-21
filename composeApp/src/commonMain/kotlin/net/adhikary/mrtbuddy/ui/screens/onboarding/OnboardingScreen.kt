package net.adhikary.mrtbuddy.ui.screens.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.calculate
import mrtbuddy.composeapp.generated.resources.card
import mrtbuddy.composeapp.generated.resources.getStarted
import mrtbuddy.composeapp.generated.resources.next
import mrtbuddy.composeapp.generated.resources.onboardingOfflineBody
import mrtbuddy.composeapp.generated.resources.onboardingOfflineTitle
import mrtbuddy.composeapp.generated.resources.onboardingScanBody
import mrtbuddy.composeapp.generated.resources.onboardingScanTitle
import mrtbuddy.composeapp.generated.resources.onboardingWelcomeBody
import mrtbuddy.composeapp.generated.resources.onboardingWelcomeTitle
import mrtbuddy.composeapp.generated.resources.skip
import mrtbuddy.composeapp.generated.resources.station_map
import net.adhikary.mrtbuddy.ui.theme.MrtMotion
import net.adhikary.mrtbuddy.ui.theme.MrtSpacing
import net.adhikary.mrtbuddy.utils.LocalHapticManager
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private data class OnboardingPage(
    val icon: DrawableResource,
    val title: StringResource,
    val body: StringResource,
)

private val pages = listOf(
    OnboardingPage(Res.drawable.station_map, Res.string.onboardingWelcomeTitle, Res.string.onboardingWelcomeBody),
    OnboardingPage(Res.drawable.card, Res.string.onboardingScanTitle, Res.string.onboardingScanBody),
    OnboardingPage(Res.drawable.calculate, Res.string.onboardingOfflineTitle, Res.string.onboardingOfflineBody),
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticManager.current

    LaunchedEffect(pagerState.currentPage) {
        haptics.tick()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF11250F), Color(0xFF1B5E20), Color(0xFF0C1A0A))
                )
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MrtSpacing.lg),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onFinish) {
                    Text(
                        text = stringResource(Res.string.skip),
                        color = Color.White.copy(alpha = 0.7f),
                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { pageIndex ->
                val page = pages[pageIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = MrtSpacing.xxl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.10f),
                        modifier = Modifier.size(140.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(page.icon),
                                contentDescription = null,
                                tint = Color(0xFFA0D683),
                                modifier = Modifier.size(64.dp),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(MrtSpacing.xxl))
                    Text(
                        text = stringResource(page.title),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(MrtSpacing.md))
                    Text(
                        text = stringResource(page.body),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MrtSpacing.xl),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    pages.indices.forEach { index ->
                        val selected = pagerState.currentPage == index
                        val dotWidth by animateDpAsState(
                            targetValue = if (selected) 24.dp else 8.dp,
                            animationSpec = tween(MrtMotion.Medium2, easing = MrtMotion.EmphasizedDecelerate),
                        )
                        val dotColor by animateColorAsState(
                            targetValue = if (selected) Color(0xFFA0D683) else Color.White.copy(alpha = 0.3f),
                            animationSpec = tween(MrtMotion.Short4),
                        )
                        Box(
                            modifier = Modifier
                                .padding(end = MrtSpacing.sm)
                                .width(dotWidth)
                                .height(8.dp)
                                .background(dotColor, CircleShape),
                        )
                    }
                }
                Button(
                    onClick = {
                        if (pagerState.currentPage == pages.lastIndex) {
                            haptics.success()
                            onFinish()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF1B5E20),
                    ),
                    modifier = Modifier.height(52.dp),
                ) {
                    Text(
                        text = stringResource(
                            if (pagerState.currentPage == pages.lastIndex) Res.string.getStarted else Res.string.next
                        ),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    )
                }
            }
        }
        Icon(
            painter = painterResource(Res.drawable.station_map),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(220.dp)
                .padding(top = 40.dp)
                .alpha(0.05f),
        )
    }
}

package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class OnboardingPage(
    val title: String,
    val description: String,
    val illustration: @Composable () -> Unit
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableStateOf(0) }

    val pages = remember {
        listOf(
            OnboardingPage(
                title = "Welcome to TaskFlow",
                description = "Organize, track, and complete your daily goals with maximum speed and elegance.",
                illustration = { WelcomeIllustration(Modifier.size(240.dp)) }
            ),
            OnboardingPage(
                title = "Organize Instantly",
                description = "Add tasks in under 5 seconds, prioritize what matters, and structure your focus hours.",
                illustration = { ProductivityIllustration(Modifier.size(240.dp)) }
            ),
            OnboardingPage(
                title = "Stay Productive Daily",
                description = "Gain valuable insights into daily completion quotas, build streaks, and stay all caught up.",
                illustration = { EmptyStateIllustration(Modifier.size(240.dp)) }
            )
        )
    }

    val page = pages[currentPage]

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("onboarding_screen"),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentPage < pages.size - 1) {
                    TextButton(
                        onClick = { viewModel.completeOnboarding() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("onboarding_skip_button")
                    ) {
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(36.dp))
                }
            }

            // Carousel Illustration & Info content with slide animations
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { width -> width / 2 } + fadeIn() with
                                slideOutHorizontally { width -> -width / 2 } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width / 2 } + fadeIn() with
                                slideOutHorizontally { width -> width / 2 } + fadeOut()
                    }.using(SizeTransform(clip = false))
                },
                modifier = Modifier.weight(1f).fillMaxWidth(),
                label = "OnboardingSlideTransitions"
            ) { targetPage ->
                val currentPageInfo = pages[targetPage]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        currentPageInfo.illustration()
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier.weight(0.8f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentPageInfo.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = currentPageInfo.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = GrayTextLight,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            // Bottom Buttons and indicators
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    pages.forEachIndexed { index, _ ->
                        val isSelected = index == currentPage
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = if (isSelected) 24.dp else 8.dp, height = 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else GrayTextLight.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                // CTA Action button (Next / Get Started)
                Button(
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            currentPage++
                        } else {
                            viewModel.completeOnboarding()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("onboarding_cta_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = if (currentPage == pages.size - 1) "Get Started" else "Next",
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp),
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

package com.carlos.blurredcasousel

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.carlos.blurredcasousel.ui.theme.BlurredCasouselTheme
import com.carlos.blurredcasousel.ui.theme.locations
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlurredCasouselTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val pagerState = rememberPagerState {
                        locations.count()
                    }
                    Column {
                        HorizontalPager(
                            state = pagerState, modifier =
                            Modifier
                                .weight(.7f)
                                .padding(top = 32.dp),
                            pageSpacing = 1.dp,
                            beyondBoundsPageCount = locations.count()

                        ) { page ->
                            Box(
                                modifier = Modifier
                                    .zIndex(page * 2f)
                                    .padding(
                                        start = 64.dp,
                                        end = 32.dp
                                    )
                                    .graphicsLayer {

                                        val startOffSet = pagerState.startOffsetForPage(page)
                                        translationX = size.width * (startOffSet * .99f)

                                        alpha = (2f - startOffSet) / 2

                                        val blur = (startOffSet * 20).coerceAtLeast(.1f)
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                            renderEffect = RenderEffect
                                                .createBlurEffect(
                                                    blur, blur, Shader.TileMode.DECAL
                                                )
                                                .asComposeRenderEffect()

                                        }

                                        val scale = 1f - (startOffSet * .1f)
                                        scaleX = scale
                                        scaleY = scale

                                    }
                                    .clip(RoundedCornerShape(20.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = locations[page].image),
                                    contentDescription = locations[page].title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                        }

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .weight(.3f),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val verticalState = rememberPagerState {
                                locations.count()
                            }
                            VerticalPager(
                                state = verticalState,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(86.dp),
                                userScrollEnabled = false,
                                horizontalAlignment = Alignment.Start
                            ) { page ->
                                Column(
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = locations[page].title,
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontWeight = FontWeight.Thin,
                                            fontSize = 28.sp,
                                            color = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = locations[page].subtitle,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                    )

                                }
                            }

                            LaunchedEffect(key1 = Unit) {
                                snapshotFlow {
                                    Pair(
                                        pagerState.currentPage,
                                        pagerState.currentPageOffsetFraction
                                    )
                                }.collect { (page, offset) ->
                                    verticalState.scrollToPage(page, offset)
                                }
                            }

                            val interpolatedRating by remember {
                                derivedStateOf {
                                    val position = pagerState.offsetForPage(0)

                                    val from = floor(position).roundToInt()
                                    val to = ceil(position).roundToInt()

                                    val fromRating = locations[from].rating.toFloat()
                                    val toRating = locations[to].rating.toFloat()

                                    val fraction = position - position.toInt()

                                    fromRating + ((toRating - fromRating) * fraction)

                                }
                            }
                            RatingBar(rating = interpolatedRating)
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun RatingBar(modifier: Modifier = Modifier, rating: Float) {
    Row(modifier = Modifier) {
        for (i in 1..5) {
            val animatedScale by animateFloatAsState(
                targetValue = if (floor(rating) >= i) {
                    1f
                } else if (ceil(rating) < i) {
                    0f
                } else {
                    rating - rating.toInt()
                },
                animationSpec = spring(
                    stiffness = Spring.StiffnessMedium
                ), label = ""
            )
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.Star),
                    contentDescription = null,
                    modifier = Modifier.alpha(.1f)
                )
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.Star),
                    contentDescription = null,
                    modifier = Modifier.alpha(animatedScale),
                    tint = Color(0xFFCA9220)
                )
            }

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction


@OptIn(ExperimentalFoundationApi::class)
fun PagerState.startOffsetForPage(page: Int) = offsetForPage(page).coerceAtLeast(0f)

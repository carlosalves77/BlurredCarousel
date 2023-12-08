package com.carlos.blurredcasousel

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import android.graphics.RenderEffect
import android.graphics.Shader
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.carlos.blurredcasousel.ui.theme.BlurredCasouselTheme
import com.carlos.blurredcasousel.ui.theme.locations

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
                                .padding(top = 32.dp)

                        ) { page ->
                            Box(
                                modifier = Modifier
                                    .zIndex(page * 2f)
                                    .graphicsLayer {

                                        val startOffSet = pagerState.startOffsetForPage(page)
                                        translationX = size.width * (startOffSet*.99f)

                                        alpha = (2f-startOffSet)/2

                                        val blur = (startOffSet*20).coerceAtLeast(.1f)
                                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                            renderEffect = RenderEffect
                                                .createBlurEffect(
                                                    blur, blur, Shader.TileMode.DECAL
                                                )
                                                .asComposeRenderEffect()

                                        }



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
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun PagerState.offsetForPage(page:Int) = (currentPage-page)+currentPageOffsetFraction


@OptIn(ExperimentalFoundationApi::class)
fun PagerState.startOffsetForPage(page:Int) = offsetForPage(page).coerceAtLeast(0f)

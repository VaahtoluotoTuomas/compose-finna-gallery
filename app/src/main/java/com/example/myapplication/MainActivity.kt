package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.ui.text.style.TextOverflow
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    ArchiveNoiseApp()
                }
            }
        }
    }
}

@Composable
fun ArchiveNoiseApp(
    viewModel: FinnaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedImageIndex by remember { mutableStateOf<Int?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                val gridState = rememberLazyStaggeredGridState()

                LaunchedEffect(gridState.firstVisibleItemIndex) {
                    val lastVisibleIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                    val totalItems = gridState.layoutInfo.totalItemsCount

                    if (totalItems > 0 && lastVisibleIndex >= totalItems - 5) {
                        viewModel.loadMore()
                    }
                }

                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalItemSpacing = 4.dp,
                    contentPadding = PaddingValues(top = 40.dp, bottom = 8.dp, start = 4.dp, end = 4.dp)
                ) {
                    itemsIndexed(uiState.images) { index, record ->
                        LandscapeCard(record, onClick = { selectedImageIndex = index })
                    }
                }

                if (uiState.isLoading && uiState.images.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }
            }

            TimeSlider(
                year = uiState.year.toFloat(),
                onYearChange = { viewModel.onYearChanged(it) },
                onYearSelected = { viewModel.onYearSelected() }
            )
        }

        selectedImageIndex?.let { initialIndex ->
            FullscreenImageView(
                images = uiState.images,
                initialIndex = initialIndex,
                onClose = { selectedImageIndex = null },
                onLoadMore = { viewModel.loadMore() }
            )
        }
    }
}

@Composable
fun FullscreenImageView(
    images: List<FinnaRecord>,
    initialIndex: Int,
    onClose: () -> Unit,
    onLoadMore: () -> Unit
) {
    var showInfo by remember { mutableStateOf(true) }

    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { images.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage >= images.size - 3) {
            onLoadMore()
        }
    }

    BackHandler {
        onClose()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val record = images[page]
            val imageUrl = record.getFullImageUrl()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showInfo = !showInfo }
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = record.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))
                }

                if (showInfo) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .background(Color.Black.copy(alpha = 0.8f))
                            .padding(24.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "ID: ${record.id}", color = Color.Green, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            Text(text = "YEAR: ${record.year ?: "Unknown"}", color = Color.White, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                            Text(text = record.title, color = Color.LightGray, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 20.dp)
                .background(Color.Black.copy(alpha = 0.6f), shape = MaterialTheme.shapes.small)
                .clickable { onClose() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "[X]",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun LandscapeCard(
    record: FinnaRecord,
    onClick: () -> Unit
) {
    val imageUrl = record.getFullImageUrl() ?: return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
    ) {
        Box {
            AsyncImage(
                model = imageUrl,
                contentDescription = record.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(6.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy((-2).dp)) {
                    Text(
                        text = "ID: ${record.id}",
                        color = Color.Green.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 10.sp
                    )
                    Text(
                        text = "YR: ${record.year ?: "????"}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 12.sp
                    )
                    Text(
                        text = record.title,
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TimeSlider(
    year: Float,
    onYearChange: (Float) -> Unit,
    onYearSelected: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A1A))
            .padding(bottom = 32.dp, top = 16.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "YEAR: ${year.toInt()}",
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontSize = 20.sp
        )

        Slider(
            value = year,
            onValueChange = onYearChange,
            onValueChangeFinished = onYearSelected,
            valueRange = 1880f..2020f,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.DarkGray
            )
        )
    }
}
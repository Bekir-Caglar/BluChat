package com.bekircaglar.bluchat.presentation.chatinfo

import VideoThumbnailComposable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBottomSheetContent(
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    selectedTabIndex: Int,
    onMediaSelected: (String) -> Unit,
    imageUrls: List<String> = emptyList(),
    onTabSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Tab(selected = selectedTabIndex == 0,
                onClick = { onTabSelected(0) },
                text = { Text("Media") })
            Tab(selected = selectedTabIndex == 1,
                onClick = { onTabSelected(1) },
                text = { Text("Links") })
            Tab(selected = selectedTabIndex == 2,
                onClick = { onTabSelected(2) },
                text = { Text("Documents") })
        }

        when (selectedTabIndex) {
            0 -> MediaContent(imageUrls = imageUrls, onItemClick = { onMediaSelected(it) })
            1 -> LinkContent()
            2 -> DocumentsContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBottomSheetScaffold(
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    imageUrls: List<String>,
    onMediaSelected: (String) -> Unit,
    content: @Composable () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContainerColor = MaterialTheme.colorScheme.background,
        sheetContent = {
            MyBottomSheetContent(
                bottomSheetScaffoldState = bottomSheetScaffoldState,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it },
                imageUrls = imageUrls,
                onMediaSelected = { onMediaSelected(it) },

                )
        },
        sheetPeekHeight = 0.dp,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        content()
    }
}

@Composable
fun MediaContent(imageUrls: List<String> = emptyList(), onItemClick: (String) -> Unit) {
    if (imageUrls.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp), contentAlignment = Alignment.Center
        ) {
            Text(text = "No media available", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(300.dp),
            contentPadding = PaddingValues(8.dp),
        ) {
            items(imageUrls) { imageUrl ->
                if (imageUrl.contains(".mp4")) {
                    VideoThumbnailComposable(
                        context = LocalContext.current,
                        videoUrl = imageUrl,
                        size = 100.dp,
                        onVideoClick ={

                        }
                    )
                }else{
                    val painter = rememberAsyncImagePainter(model = imageUrl)
                    val painterState = painter.state
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .shadow(elevation = 5.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(color = Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        if (painterState is AsyncImagePainter.State.Success) {

                        } else {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .background(color = Color.White, shape = CircleShape)
                                    .size(80.dp)
                            )
                        }
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(4.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onItemClick(imageUrl)
                                },
                            contentScale = ContentScale.Crop
                        )

                    }

                }

            }
        }
    }
}

@Composable
fun LinkContent(linkUrls: List<String> = emptyList()) {
    if (linkUrls.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp), contentAlignment = Alignment.Center
        ) {
            Text(text = "No links available", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(300.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(linkUrls) { imageUrl ->
                Image(
                    painter = rememberImagePainter(data = imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {

                        },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun DocumentsContent(documentUrls: List<String> = emptyList()) {
    if (documentUrls.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp), contentAlignment = Alignment.Center
        ) {
            Text(text = "No documents available", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(300.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(documentUrls) { imageUrl ->
                Image(
                    painter = rememberImagePainter(data = imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
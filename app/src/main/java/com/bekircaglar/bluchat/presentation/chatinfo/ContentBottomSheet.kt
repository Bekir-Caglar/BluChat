package com.bekircaglar.bluchat.presentation.chatinfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBottomSheetContent(
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    selectedTabIndex: Int,
    onMediaSelected : (String) -> Unit,
    imageUrls: List<String> = emptyList(),
    onTabSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(selected = selectedTabIndex == 0, onClick = { onTabSelected(0) }, text = { Text("Media") })
            Tab(selected = selectedTabIndex == 1, onClick = { onTabSelected(1) }, text = { Text("Links") })
            Tab(selected = selectedTabIndex == 2, onClick = { onTabSelected(2) }, text = { Text("Documents") })
        }

        when (selectedTabIndex) {
            0 -> MediaContent(imageUrls =imageUrls, onItemClick = {onMediaSelected(it) } )
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
        sheetContent = {
            MyBottomSheetContent(
                bottomSheetScaffoldState = bottomSheetScaffoldState,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it },
                imageUrls = imageUrls,
                onMediaSelected = {onMediaSelected(it)}
            )
        },
        sheetPeekHeight = 0.dp
    ) {
        content()
    }
}

@Composable
fun MediaContent(imageUrls: List<String> = emptyList(),onItemClick: (String) -> Unit) {


    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.height(300.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(imageUrls) { imageUrl ->
            Image(
                painter = rememberImagePainter(data = imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onItemClick(imageUrl)
                    },
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun LinkContent(linkUrls: List<String> = emptyList()) {
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

@Composable
fun DocumentsContent(documentUrls: List<String> = emptyList()) {
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

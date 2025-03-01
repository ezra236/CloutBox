package com.example.cloutbox

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter


data class IconItem(val iconRes: Int, val width: Dp, val height: Dp)


class EditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Retrieve the DTO list passed via the Intent extras.
            val selectedMediaDTOList = intent.getParcelableArrayListExtra<MediaItemDTO>("selectedMedia") ?: listOf()

            // Get the screen width for each media item.
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp

            MaterialTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Main content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "CloutBox",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            modifier = Modifier.padding(start = 20.dp)
                        )
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 50.dp)
                                .padding(start = 15.dp)
                                .height(500.dp),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            items(selectedMediaDTOList) { mediaDTO ->
                                Box(
                                    modifier = Modifier
                                        .width(screenWidth * 0.8f)
                                        .border(
                                            width = 1.dp,
                                            color = Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    if (mediaDTO.type == "Video") {
                                        // Use our VideoThumbnail composable to show an actual video thumbnail.
                                        VideoThumbnail(uri = mediaDTO.uri)
                                    } else {
                                        Image(
                                            painter = rememberAsyncImagePainter(mediaDTO.uri),
                                            contentDescription = "Gallery image",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { /* Handle button click */ },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .width(170.dp)
                                    .height(50.dp)
                            ) {
                                Text("Next ->", color = Color.White)
                            }
                        }
                    }

                    // Side column with 7 buttons positioned on the far right (15% of the screen width)
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(screenWidth * 0.12f)
                            .padding(top = 70.dp)
                            .align(Alignment.CenterEnd),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        // List of icon items with specific dimensions for each button.
                        val iconItems = listOf(
                            IconItem(iconRes = R.drawable.set, width = 30.dp, height = 30.dp),
                            IconItem(iconRes = R.drawable.sha,  width = 30.dp, height = 30.dp),
                            IconItem(iconRes = R.drawable.vidd, width = 30.dp, height = 30.dp),
                            IconItem(iconRes = R.drawable.pl,  width = 30.dp, height = 30.dp),
                            IconItem(iconRes = R.drawable.le,  width = 30.dp, height = 30.dp),
                            IconItem(iconRes = R.drawable.gif, width = 30.dp, height = 30.dp),
                            IconItem(iconRes = R.drawable.fil,  width = 30.dp, height = 30.dp)
                        )

                        // Iterate over the list and create a button for each icon.
                        iconItems.forEachIndexed { index, iconItem ->
                            Button(
                                onClick = { /* Handle click for button $index */ },
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .size(iconItem.width), // Use size() to set both width and height (e.g., 40.dp)
                                shape = RoundedCornerShape(5.dp),
                                contentPadding = PaddingValues(0.dp) // Remove default padding
                            ) {
                                Image(
                                    painter = painterResource(id = iconItem.iconRes),
                                    contentDescription = "Button $index",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper function already defined:
fun loadVideoThumbnail(activity: ComponentActivity, uri: Uri): Bitmap? {
    return try {
        val retriever = android.media.MediaMetadataRetriever()
        retriever.setDataSource(activity, uri)
        retriever.getFrameAtTime(0)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// New composable to load and display a video thumbnail.
@Composable
fun VideoThumbnail(uri: String) {
    val context = LocalContext.current
    var thumbnail by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(uri) {
        // Convert the string URI to a Uri object.
        val uriObj = Uri.parse(uri)
        // Attempt to load the video thumbnail.
        thumbnail = loadVideoThumbnail(context as ComponentActivity, uriObj)
    }

    if (thumbnail != null) {
        Image(
            bitmap = thumbnail!!.asImageBitmap(),
            contentDescription = "Video thumbnail",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else {
        // Optionally show a placeholder while the thumbnail loads.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
        )
    }
}

// ---- Compose Preview Code Below ----
@Composable
fun EditScreenContent(selectedMediaDTOList: List<MediaItemDTO>) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(10.dp)
            ) {
                Text(
                    text = "CloutBox",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.padding(start = 20.dp)
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp)
                        .padding(start = 15.dp)
                        .height(500.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(selectedMediaDTOList) { mediaDTO ->
                        Box(
                            modifier = Modifier
                                .width(screenWidth * 0.8f)
                                .border(
                                    width = 1.dp,
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            if (mediaDTO.type == "Video") {
                                // For preview, use a placeholder image for videos.
                                Image(
                                    painter = rememberAsyncImagePainter("https://via.placeholder.com/300.png?text=Video"),
                                    contentDescription = "Video thumbnail",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = rememberAsyncImagePainter(mediaDTO.uri),
                                    contentDescription = "Gallery image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { /* Handle button click */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .width(170.dp)
                            .height(50.dp)
                    ) {
                        Text("Next ->", color = Color.White)
                    }
                }
            }

            // Side column with 7 buttons positioned on the far right (15% of the screen width)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(screenWidth * 0.12f)
                    .padding(top = 70.dp)
                    .align(Alignment.CenterEnd),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                // List of icon items with specific dimensions for each button.
                val iconItems = listOf(
                    IconItem(iconRes = R.drawable.set, width = 30.dp, height = 30.dp),
                    IconItem(iconRes = R.drawable.sha,  width = 30.dp, height = 30.dp),
                    IconItem(iconRes = R.drawable.vidd, width = 30.dp, height = 30.dp),
                    IconItem(iconRes = R.drawable.pl,  width = 30.dp, height = 30.dp),
                    IconItem(iconRes = R.drawable.le,  width = 30.dp, height = 30.dp),
                    IconItem(iconRes = R.drawable.gif, width = 30.dp, height = 30.dp),
                    IconItem(iconRes = R.drawable.fil,  width = 30.dp, height = 30.dp)
                )

                // Iterate over the list and create a button for each icon.
                iconItems.forEachIndexed { index, iconItem ->
                    Button(
                        onClick = { /* Handle click for button $index */ },
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .size(iconItem.width), // Use size() to set both width and height (e.g., 40.dp)
                        shape = RoundedCornerShape(5.dp),
                        contentPadding = PaddingValues(0.dp) // Remove default padding
                    ) {
                        Image(
                            painter = painterResource(id = iconItem.iconRes),
                            contentDescription = "Button $index",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

        }
    }
}



@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun EditScreenPreview() {
    // Sample data for preview.
    val sampleMediaDTOList = listOf(
        MediaItemDTO(uri = "https://via.placeholder.com/300.png?text=Image1", type = "Image"),
        MediaItemDTO(uri = "https://via.placeholder.com/300.png?text=Video1", type = "Video"),
        MediaItemDTO(uri = "https://via.placeholder.com/300.png?text=Image2", type = "Image")
    )
    EditScreenContent(selectedMediaDTOList = sampleMediaDTOList)
}


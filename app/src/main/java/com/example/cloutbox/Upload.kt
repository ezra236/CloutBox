package com.example.cloutbox

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.tooling.preview.Preview
import java.io.InputStream
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.common.MediaItem as ExoMediaItem
import android.os.Parcelable
import kotlinx.parcelize.Parcelize



@Parcelize
data class MediaItemDTO(
    val uri: String,
    val type: String
) : Parcelable



// Data class representing a media item (image or video)
data class MediaItem(val id: Long, val contentUri: Uri, val type: String)

data class Album(
    val albumName: String,
    val thumbnailUri: Uri,
    val mediaType: String // "Image" or "Video"
)




class Upload : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Use the wrapper now that hoists the gallery state.
            UploadScreenWrapper(isAndroid14OrAbove = Build.VERSION.SDK_INT >= 34)
        }
    }
}


@Composable
fun UploadScreenWrapper(isAndroid14OrAbove: Boolean) {
    // Get a Context to load resources.
    val context = LocalContext.current

    // Hoist the gallery state and albums.
    var showGallery by remember { mutableStateOf(false) }
    val albums by remember { mutableStateOf(loadAlbums(context)) }

    // Use a Box to stack the main screen and the overlay.
    Box {
        // Pass the gallery state and a callback to toggle it.
        UploadScreen(
            isAndroid14OrAbove = isAndroid14OrAbove,
            showGallery = showGallery,
            onToggleGallery = { showGallery = it }
        )
        // Place the animated gallery overlay outside of UploadScreen.
        GalleryOverlay(
            showGallery = showGallery,
            albums = albums,
            onAlbumClick = { album ->
                // Handle album selection.
                // For example, load album media items.
            },
            onClose = {
                showGallery = false  // This will hide the overlay.
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    isAndroid14OrAbove: Boolean,
    showGallery: Boolean, // now provided as a parameter
    onToggleGallery: (Boolean) -> Unit
) {
    val context = LocalContext.current

    var mediaItems by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var selectedMediaUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedMediaItem by remember { mutableStateOf<MediaItem?>(null) }
    var isMultipleSelectionMode by remember { mutableStateOf(false) }
    val selectedMediaSet = remember { mutableStateListOf<MediaItem>() }

    // Launcher for picking multiple media items.
    rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        selectedMediaUris = uris
    }

    // Choose the proper permission.
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val hasPermission = ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            mediaItems = loadMediaItems(context)
        }
    }

    LaunchedEffect(Unit) {
        if (!isAndroid14OrAbove) {
            if (!hasPermission) {
                permissionLauncher.launch(permission)
            } else {
                mediaItems = loadMediaItems(context)
                selectedMediaItem = mediaItems.firstOrNull()
            }
        }
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {

            // Top row with a height of 20.dp and a Next button at the far right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        // Convert MediaItem to MediaItemDTO without altering the original MediaItem class.
                        val mediaDTOList = selectedMediaSet.map { media ->
                            MediaItemDTO(media.contentUri.toString(), media.type)
                        }
                        val intent = Intent(context, EditActivity::class.java).apply {
                            putParcelableArrayListExtra("selectedMedia", ArrayList(mediaDTOList))
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier.padding(end = 16.dp)
                        .height(45.dp) // Ensures the button matches the row's height
                ) {
                    Text(text = "Next", color = Color.White, fontSize = 20.sp)
                }
            }

            if (!isAndroid14OrAbove) {
                selectedMediaItem?.let { media ->
                    key(media.contentUri) {
                        if (media.type == "Video") {
                            // Video logic: load thumbnail, calculate aspect ratio, and create ExoPlayer.
                            val videoThumbnail =
                                remember { loadVideoThumbnail(context, media.contentUri) }
                            if (videoThumbnail != null) {
                                val aspectRatio =
                                    videoThumbnail.width.toFloat() / videoThumbnail.height.toFloat()
                                val isTallVideo = aspectRatio < 0.8f

                                val exoPlayer = remember(context) {
                                    ExoPlayer.Builder(context).build().apply {
                                        setMediaItem(ExoMediaItem.fromUri(media.contentUri))
                                        prepare()
                                        playWhenReady = true // Auto-play
                                    }
                                }

                                DisposableEffect(Unit) {
                                    onDispose { exoPlayer.release() }
                                }

                                AndroidView(
                                    factory = { ctx ->
                                        PlayerView(ctx).apply {
                                            player = exoPlayer
                                            useController = false
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(400.dp)
                                        .graphicsLayer(
                                            scaleX = if (isTallVideo) 0.9f else if (aspectRatio > 1) 1f else 1f / aspectRatio,
                                            scaleY = if (isTallVideo) 0.9f else if (aspectRatio > 1) aspectRatio else 1f
                                        )
                                        .padding(bottom = 8.dp)
                                )
                            }
                        } else {
                            // Image logic: load bitmap and allow pinch-zoom.
                            val imageBitmap =
                                remember { loadImageBitmap(context, media.contentUri) }
                            var scale by remember { mutableStateOf(1f) }
                            val aspectRatio =
                                imageBitmap?.let { it.width.toFloat() / it.height.toFloat() } ?: 1f

                            if (imageBitmap != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(400.dp)
                                        .pointerInput(Unit) {
                                            detectTransformGestures { _, _, zoom, _ ->
                                                scale = (scale * zoom).coerceIn(0.5f, 3f)
                                            }
                                        }
                                ) {
                                    Image(
                                        bitmap = imageBitmap,
                                        contentDescription = "Selected image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .graphicsLayer(
                                                scaleX = if (aspectRatio > 1) scale else scale / aspectRatio,
                                                scaleY = if (aspectRatio > 1) scale * aspectRatio else scale
                                            )
                                            .padding(bottom = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Top control bar.
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(3.dp)
                            .height(50.dp) // Adjust as needed
                            .background(Color(0xFF5D4037)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(0.9f)) {
                            Button(
                                onClick = { onToggleGallery(!showGallery) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF5D4037
                                    )
                                ),
                                modifier = Modifier
                                    .padding(1.dp)
                                    .weight(0.3f)
                            ) {
                                Text(text = "Recents", color = Color.White, fontSize = 11.sp)
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Download",
                                    tint = Color.White
                                )
                            }

                            Button(
                                onClick = { isMultipleSelectionMode = !isMultipleSelectionMode },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF5D4037
                                    )
                                ),
                                modifier = Modifier
                                    .padding(1.dp)
                                    .clip(RoundedCornerShape(50))
                                    .weight(0.6f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Collections,
                                    contentDescription = "Select Multiple",
                                    tint = Color.White
                                )
                                Text(
                                    text = "SELECT MULTIPLE",
                                    color = Color.White,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { /* Handle Camera Click */ },
                            modifier = Modifier
                                .weight(0.1f)
                                .size(80.dp)
                                .background(Color(0xFF5D4037), shape = RoundedCornerShape(1.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Camera,
                                contentDescription = "Camera",
                                modifier = Modifier.size(80.dp),
                                tint = Color.White
                            )
                        }
                    }
                }



                // Grid of media items.
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(mediaItems.chunked(4)) { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            rowItems.forEach { media ->
                                // Wrap each media item in a Box to allow overlaying selection indicators.
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .border(BorderStroke(1.dp, Color.White))
                                        .clickable {
                                            if (isMultipleSelectionMode) {
                                                if (selectedMediaSet.contains(media)) {
                                                    selectedMediaSet.remove(media)
                                                } else {
                                                    selectedMediaSet.add(media)
                                                }
                                            } else {
                                                selectedMediaItem = media
                                            }
                                        }
                                ) {
                                    // Display the media (Video or Image)
                                    if (media.type == "Video") {
                                        val videoThumbnail =
                                            remember {
                                                loadVideoThumbnail(
                                                    context,
                                                    media.contentUri
                                                )
                                            }
                                        if (videoThumbnail != null) {
                                            Image(
                                                bitmap = videoThumbnail,
                                                contentDescription = "Video thumbnail",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    } else {
                                        Image(
                                            painter = rememberAsyncImagePainter(media.contentUri),
                                            contentDescription = "Gallery image",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    // If in multiple selection mode, overlay a circle at bottom right.
                                    if (isMultipleSelectionMode) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(4.dp)
                                                .size(24.dp)
                                                .background(
                                                    color = if (selectedMediaSet.contains(media)) Color.White else Color.White,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (selectedMediaSet.contains(media)) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = Color.Black,
                                                    modifier = Modifier.size(17.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun GalleryOverlay(
    showGallery: Boolean,
    albums: List<Album>,
    onAlbumClick: (Album) -> Unit,
    onClose: () -> Unit
) {
    AnimatedVisibility(
        visible = showGallery,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = Modifier.zIndex(1f)
    ) {
        BackHandler(enabled = showGallery) {
            onClose()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.DarkGray)
        ) {
            Column {
                // Top Bar with Cancel Button & Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.Black),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cancel Button (Left)
                    TextButton(
                        onClick = { onClose() }, // Close Gallery
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Cancel", color = Color.White, fontSize = 16.sp)
                    }

                    // Spacer to push the title to the center
                    Spacer(modifier = Modifier.weight(1f))

                    // Title (Center)
                    Text(
                        text = "Select Album",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Spacer for balance
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Album Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(albums) { album ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                                .clickable { onAlbumClick(album) }
                                .border(2.dp, Color.White, shape = RoundedCornerShape(8.dp))
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = album.thumbnailUri,
                                    error = painterResource(id = R.drawable.ba) // Placeholder
                                ),
                                contentDescription = "Album Thumbnail",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                text = album.albumName,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(4.dp),
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}




// Function to load image bitmap
fun loadImageBitmap(context: Context, imageUri: Uri): androidx.compose.ui.graphics.ImageBitmap? {
    val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
    return inputStream?.use { stream ->
        val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
        bitmap?.asImageBitmap()
    }
}


fun loadAlbums(context: Context): List<Album> {
    // Map for image albums: album name -> thumbnail URI (last image encountered)
    val imageAlbums = mutableMapOf<String, Uri>()
    // Map for video albums: album name -> thumbnail URI (last video encountered)
    val videoAlbums = mutableMapOf<String, Uri>()

    // Query images for album information.
    val imageProjection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )
    val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    context.contentResolver.query(imageUri, imageProjection, null, null, null)?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val albumName = cursor.getString(albumColumn) ?: "Unknown Album"
            // Always update so that the last image is used as the thumbnail.
            val contentUri = ContentUris.withAppendedId(imageUri, id)
            imageAlbums[albumName] = contentUri
        }
    }

    // Query videos for album information.
    val videoProjection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.BUCKET_DISPLAY_NAME
    )
    val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

    context.contentResolver.query(videoUri, videoProjection, null, null, null)?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val albumName = cursor.getString(albumColumn) ?: "Unknown Album"
            // Always update so that the last video is used as the thumbnail.
            val contentUri = ContentUris.withAppendedId(videoUri, id)
            videoAlbums[albumName] = contentUri
        }
    }

    // Create a list of Album objects for images.
    val albumList = mutableListOf<Album>()
    imageAlbums.forEach { (albumName, uri) ->
        albumList.add(Album(albumName, uri, "Image"))
    }
    // Create a list of Album objects for videos.
    videoAlbums.forEach { (albumName, uri) ->
        albumList.add(Album(albumName, uri, "Video"))
    }

    return albumList
}




// Function to load both images and videos from MediaStore
fun loadMediaItems(context: Context): List<MediaItem> {
    val mediaItems = mutableListOf<MediaItem>()

    // Query images
    val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATE_ADDED)

    // Load Images
    context.contentResolver.query(
        imageUri, projection, null, null, "${MediaStore.MediaColumns.DATE_ADDED} DESC"
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val contentUri = ContentUris.withAppendedId(imageUri, id)
            mediaItems.add(MediaItem(id, contentUri, "Image"))
        }
    }

    // Load Videos
    context.contentResolver.query(
        videoUri, projection, null, null, "${MediaStore.MediaColumns.DATE_ADDED} DESC"
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val contentUri = ContentUris.withAppendedId(videoUri, id)
            mediaItems.add(MediaItem(id, contentUri, "Video"))
        }
    }

    return mediaItems.sortedByDescending { it.id } // Sort by most recent
}

// Function to load video thumbnails
fun loadVideoThumbnail(context: Context, videoUri: Uri): androidx.compose.ui.graphics.ImageBitmap? {
    // Fetching the thumbnail
    val thumbnail = MediaStore.Video.Thumbnails.getThumbnail(
        context.contentResolver,
        ContentUris.parseId(videoUri),
        MediaStore.Video.Thumbnails.MINI_KIND,
        null
    )

    // If the thumbnail is not null, convert it to ImageBitmap
    return thumbnail?.asImageBitmap()
}

@Preview(showBackground = true)
@Composable
fun UploadScreenPreviewAndroid10to13() {
    // Use the wrapper in the preview
    UploadScreenWrapper(isAndroid14OrAbove = false)
}

@Preview(showBackground = true)
@Composable
fun UploadScreenPreviewAndroid14AndAbove() {
    // Use the wrapper in the preview
    UploadScreenWrapper(isAndroid14OrAbove = true)
}
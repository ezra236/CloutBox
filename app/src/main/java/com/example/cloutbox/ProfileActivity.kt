package com.example.cloutbox

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create a ComposeView and set its content.
        val composeView = ComposeView(this).apply {
            setContent {
                ProfileScreen()
            }
        }
        // Set the ComposeView as the activity's content view.
        setContentView(composeView)
    }
}

@Composable
fun ProfileScreen() {
    // Check if we're in preview mode.
    val isInPreview = LocalInspectionMode.current
    // In preview mode, FirebaseAuth isn't available so we provide a dummy username.
    val username = if (isInPreview) {
        "Preview User"
    } else {
        FirebaseAuth.getInstance().currentUser?.displayName ?: "No username available"
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(0.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = username,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(start = 10.dp, top = 8.dp)
                )

                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu Icon",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(end = 10.dp, top = 8.dp)
                        .size(35.dp) // Increase the icon size to 32dp
                )
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // First Box: occupies 30% of the width
                Box(
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxHeight()
                )
                // Next three boxes share the remaining 70% equally (each gets ~23.33% of width)
                Box(
                    modifier = Modifier
                        .weight(0.7f / 3)
                        .fillMaxHeight()
                )
                Box(
                    modifier = Modifier
                        .weight(0.7f / 3)
                        .fillMaxHeight()
                )
                Box(
                    modifier = Modifier
                        .weight(0.7f / 3)
                        .fillMaxHeight()
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), // optional outer padding
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { /* Action for Button 1 */ },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282525)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = "Edit profile",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }
                Button(
                    onClick = { /* Action for Button 1 */ },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282525)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = "Share profile",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth() .padding(top = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Button(
                    onClick = { /* Action for first button */ },
                    modifier = Modifier.size(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp) // Remove default padding
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cam), // Your drawable resource for cam
                        contentDescription = "Cam Button",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(100.dp))
                Button(
                    onClick = { /* Action for second button */ },
                    modifier = Modifier.size(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.reel), // Your drawable resource for reel
                        contentDescription = "Reel Button",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}

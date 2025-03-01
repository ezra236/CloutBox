package com.example.cloutbox // Make sure this matches your project package

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class HomeActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(
                containerColor = Color.White,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "CloutBox",
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold, // Set font weight
                                    fontSize = 25.sp, // Set font size
                                    color = Color.Black
                                )
                            )
                        },
                        actions = {
                            IconButton(
                                onClick = { /* Handle notification click */ },
                                modifier = Modifier.size(30.dp)  .offset(x = (-40).dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.heart),
                                    contentDescription = "Notifications",
                                    modifier = Modifier.fillMaxSize(), // makes the image fill the button
                                    contentScale = ContentScale.Crop   // adjust as needed
                                )
                            }
                            IconButton(
                                onClick = { /* Handle notification click */ },
                                modifier = Modifier.size(40.dp)  .offset(x = (-11).dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.m),
                                    contentDescription = "Notifications",
                                    modifier = Modifier.fillMaxSize(), // makes the image fill the button
                                    contentScale = ContentScale.Crop   // adjust as needed
                                )
                            }
                        },
                        // Set background color for TopAppBar
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White // Set your desired color here
                        )
                    )
                },
                bottomBar = { BottomNavigationBar() },
                content = { /* Your content here */ }
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewHomeActivity() {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "CloutBox",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold, // Set font weight
                            fontSize = 25.sp, // Set font size
                            color = Color.Black
                        )
                    )
                },
                actions = {
                    IconButton(
                        onClick = { /* Handle notification click */ },
                        modifier = Modifier.size(30.dp)  .offset(x = (-40).dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.heart),
                            contentDescription = "Notifications",
                            modifier = Modifier.fillMaxSize(), // makes the image fill the button
                            contentScale = ContentScale.Crop   // adjust as needed
                        )
                    }
                    IconButton(
                        onClick = { /* Handle notification click */ },
                        modifier = Modifier.size(40.dp)  .offset(x = (-11).dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.m),
                            contentDescription = "Notifications",
                            modifier = Modifier.fillMaxSize(), // makes the image fill the button
                            contentScale = ContentScale.Crop   // adjust as needed
                        )
                    }
                },
                // Set background color for TopAppBar
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White // Set your desired color here
                )
            )
        },
        bottomBar = { BottomNavigationBar() },
        content = { /* Your content here */ }
    )
}

@Composable
fun BottomNavigationBar() {
    // Obtain the current context
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Home Icon at the left
        IconButton(
            onClick = { /* Handle notification click */ },
            modifier = Modifier.size(40.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.home),
                contentDescription = "Notifications",
                modifier = Modifier.fillMaxSize(), // makes the image fill the button
                contentScale = ContentScale.Crop   // adjust as needed
            )
        }

        // '+' Icon inside a square in the center
        IconButton(
            onClick = {
                // Launch Upload activity when clicked
                context.startActivity(Intent(context, Upload::class.java))
            }
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RectangleShape) // Ensures the shape of the image if needed
            ) {
                Image(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Add",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // or ContentScale.Fit depending on your needs
                )
            }
        }


        // Circle at the far right
        IconButton(
            onClick = {
                // Launch ProfileActivity when clicked
                val intent = Intent(context, ProfileActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.size(40.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile",
                contentScale = ContentScale.Crop,  // Ensures the image fills the space
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)           // Clips the image to a circle
                    .border(width = 2.dp, color = Color.Black, shape = CircleShape) // Optional border
            )
        }
    }
}

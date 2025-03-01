package com.example.cloutbox

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Hide the status bar and make the app full screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        // Set the content directly to the SplashScreen composable
        setContent {
            SplashScreen() // This will be shown immediately on app launch
        }
    }
}

@Composable
fun SplashScreen() {
    val backgroundImage = painterResource(id = R.drawable.cb) // Your splash image
    var isLoading by remember { mutableStateOf(true) }

    // Show splash screen for 1 second before navigating to the Sign In screen
    LaunchedEffect(true) {
        delay(2500) // 2-second delay for the splash screen
        isLoading = false
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center // Center the image
        ) {
            // Set fixed width and height for the image
            Image(
                painter = backgroundImage,
                contentDescription = "Splash Image",
                contentScale = ContentScale.Crop, // Adjust the image's aspect ratio to fit within the fixed size
                modifier = Modifier
                    .width(150.dp) // Set the width of the image
                    .height(100.dp) // Set the height of the image
            )
        }
    } else {
        // Navigate to the Sign In screen after splash screen delay
        SignInScreen()
    }
}

@Composable
fun SignInScreen() {
    val backgroundImage = painterResource(id = R.drawable.ba) // Your background image
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var keepMeSignedIn by remember { mutableStateOf(false) }

    val context = LocalContext.current // Ensure this is inside a composable function

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "SIGN IN",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp)) // Space between text and underline
                    Box(
                        modifier = Modifier
                            .width(60.dp) // Adjust underline width as needed
                            .height(2.dp) // Thickness of underline
                            .background(Color(0xFFFF1493)) // Pink color for underline
                    )
                }

                Text(
                    "SIGN UP",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White),
                    modifier = Modifier.clickable {
                        val intent = Intent(context, SignUpActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            InputField(label = "EMAIL", value = username, onValueChange = { username = it })
            Spacer(modifier = Modifier.height(16.dp))
            InputField(label = "PASSWORD", value = password, onValueChange = { password = it }, isPassword = true)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = keepMeSignedIn, onCheckedChange = { keepMeSignedIn = it })
                Text("KEEP ME SIGNED IN", color = Color.White, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    signInWithEmailPassword(username, password, context)
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),  // Corrected for Material3
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("SIGN IN", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "FORGOT PASSWORD?",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

fun signInWithEmailPassword(username: String, password: String, context: Context) {
    val auth = FirebaseAuth.getInstance()

    if (username.isNotEmpty() && password.isNotEmpty()) {
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-in successful
                    val intent = Intent(context, HomeActivity::class.java)
                    context.startActivity(intent)
                    // Optionally finish the current activity to prevent going back
                } else {
                    // Sign-in failed
                    Toast.makeText(context, "Sign In Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    } else {
        Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit, isPassword: Boolean = false) {
    Column {
        Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(12.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
            ),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignInScreen() {
    SignInScreen()
}

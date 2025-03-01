package com.example.cloutbox

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloutbox.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth
        setContent {
            SignUpScreen()
        }
    }



    @Composable
    fun SignUpScreen() {
        val backgroundImage = painterResource(id = R.drawable.ba) // Your background image
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }

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
                Text(
                    "SIGN UP",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )

                Spacer(modifier = Modifier.height(24.dp))

                InputField(label = "USERNAME", value = username, onValueChange = { username = it })
                Spacer(modifier = Modifier.height(16.dp))
                InputField(label = "EMAIL", value = email, onValueChange = { email = it })
                Spacer(modifier = Modifier.height(16.dp))
                InputField(label = "PASSWORD", value = password, onValueChange = { password = it }, isPassword = true)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        signUpWithEmailPassword(email, password, username)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("SIGN UP", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }

    private fun signUpWithEmailPassword(email: String, password: String, username: String) {
        if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Get the newly created user
                        val user = auth.currentUser
                        // Update the Firebase Auth profile with the username
                        user?.updateProfile(
                            userProfileChangeRequest {
                                displayName = username
                            }
                        )?.addOnCompleteListener { profileUpdateTask ->
                            if (profileUpdateTask.isSuccessful) {
                                // Save additional user data to Firestore
                                val db = FirebaseFirestore.getInstance()
                                val userData = hashMapOf(
                                    "uid" to user.uid,
                                    "username" to username,
                                    "email" to email
                                )
                                db.collection("users").document(user.uid)
                                    .set(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "User data saved", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Failed to update username", Toast.LENGTH_SHORT).show()
                            }
                        }
                        // Navigate to HomeActivity
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish() // Optional: finish SignUpActivity to prevent going back to it
                    } else {
                        // Sign-up failed
                        Toast.makeText(this, "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignUpScreen() {
    SignUpActivity().SignUpScreen() // Reference the SignUpScreen inside the SignUpActivity
}

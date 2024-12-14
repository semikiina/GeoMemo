package com.example.notes.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notes.R
import com.example.notes.components.PrimaryButton

@Composable
fun AuthenticationScreen(navController: NavController) {

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Image (
            painter = painterResource(id = R.drawable.authentication_image),
            contentDescription = "Notes Logo",
            modifier = Modifier
                .fillMaxWidth()
                .height(707.dp)
                .align(Alignment.TopCenter)

        )
        Card (
            shape = RoundedCornerShape(50.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
            )  {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(vertical = 80.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "Let’s Get Started",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDCF91B),
                        contentColor = Color.Black,
                    ) ,
                    onClick = {
                        navController.navigate("register")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Create Account",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                }
                OutlinedButton(
                    onClick = {
                        navController.navigate("login")
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                ) {
                    Text(
                        text = "Log In",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

    }
}
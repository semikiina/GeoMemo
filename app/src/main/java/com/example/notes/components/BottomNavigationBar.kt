package com.example.notes.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController


@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = Color.White,
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = {
                Text(
                    text ="Home",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            },
            selected = navController.currentDestination?.route == "home",
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AddCircleOutline, contentDescription = "Note") },
            label = { Text(
                text ="Note",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            ) },
            selected = navController.currentDestination?.route == "note",
            onClick = { navController.navigate("note") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.PermIdentity, contentDescription = "Profile") },
            label = {
                Text(
                    text ="Profile",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            },
            selected = navController.currentDestination?.route == "profile",
            onClick = { navController.navigate("profile") }
        )

    }
}
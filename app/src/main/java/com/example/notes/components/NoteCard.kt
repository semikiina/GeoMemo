package com.example.notes.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.notes.R
import com.example.notes.models.Note
import com.example.notes.models.User
import com.example.notes.models.UserViewModel
import com.example.notes.utils.Screen
import com.google.android.material.chip.Chip
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteCard(note: Note, navController: NavController) {

    var user by remember { mutableStateOf<User?>(null) }

    UserViewModel().getUser(note.uid) { u ->
        user = u
    }



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()) {
            // Posted time ago
            val timeAgo = getTimeAgo(note.date)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = "Posted $timeAgo",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    color = Color.Gray,
                    fontStyle = FontStyle.Italic
                )
                Text(
                    text = note.type,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                    color = getNoteTypeColor(note.type),
                    modifier = Modifier.padding(8.dp),
                )
            }


            Spacer(modifier = Modifier.height(8.dp))

            // Note text
            Text(
                text = note.noteText,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),

            )

            Spacer(modifier = Modifier.height(16.dp))

            // User info and chip

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            navController.navigate(route = Screen.UserProfile.createRoute(user?.uid ?: "" ))
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Avatar
                AsyncImage(
                    model = user?.avatarUrl,
                    error = painterResource(id = R.drawable.default_avatar),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Name and username
                Column(modifier = Modifier.weight(1f)) {
                    user?.let {
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Text(
                        text = "@${note.username}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }

            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun getTimeAgo(date: String): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
    val postedDate = LocalDateTime.parse(date, formatter)
    val duration = Duration.between(postedDate, LocalDateTime.now())

    return when {
        duration.toDays() > 0 -> "${duration.toDays()} days ago"
        duration.toHours() > 0 -> "${duration.toHours()} hours ago"
        duration.toMinutes() > 0 -> "${duration.toMinutes()} minutes ago"
        else -> "just now"
    }
}

@Composable
fun getNoteTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "Daily Note" -> Color.Green // Green
        "Weekly Note" -> Color.Yellow // Yellow
        "Monthly Note" -> Color.Blue // Blue
        else -> Color.Gray
    }
}

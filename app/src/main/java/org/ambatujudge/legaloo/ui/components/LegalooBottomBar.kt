package org.ambatujudge.legaloo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ambatujudge.legaloo.navigation.Routes

data class BottomNavItem(val label: String, val route: String, val icon: ImageVector)

@Composable
fun LegalooBottomBar(currentRoute: String?, onItemClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val items = listOf(
        BottomNavItem("Beranda", Routes.HOME, Icons.Outlined.Home),
        BottomNavItem("Kursus", Routes.COURSES, Icons.Outlined.MenuBook),
        BottomNavItem("Tanya", Routes.CHAT, Icons.Outlined.ChatBubbleOutline),
        BottomNavItem("Profil", Routes.PROFILE, Icons.Outlined.PersonOutline)
    )
    Column(modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outline)
        Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp)) {
            items.forEach { item ->
                val selected = if (item.route == Routes.COURSES) currentRoute == Routes.COURSES || currentRoute == Routes.COURSE_CONTENT else currentRoute == item.route
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(
                        Modifier.width(94.dp).clip(RoundedCornerShape(20.dp)).clickable { onItemClick(item.route) }
                            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else androidx.compose.ui.graphics.Color.Transparent)
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(item.icon, contentDescription = null, tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(26.dp))
                        Spacer(Modifier.height(2.dp))
                        Text(item.label, style = MaterialTheme.typography.labelSmall, color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }
    }
}

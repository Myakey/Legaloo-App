package org.ambatujudge.legaloo.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.ambatujudge.legaloo.ui.components.NotificationHelper
import org.ambatujudge.legaloo.ui.components.pressClickable
import org.ambatujudge.legaloo.ui.components.pressOffset
import org.ambatujudge.legaloo.ui.components.rememberPressState
import org.ambatujudge.legaloo.ui.theme.LegalooTheme
import org.ambatujudge.legaloo.ui.theme.LocalExtraColors

private data class ReminderMessage(val label: String, val title: String, val body: String)

private val reminderMessages = listOf(
    ReminderMessage("Ramah", "Waktunya belajar hukum! 📚", "Lanjutkan \"Hak dan Kewajiban\", tinggal sedikit lagi."),
    ReminderMessage("Pak Singa", "Pak Singa kangen kamu! 🦁", "Ayo kita lanjut, jangan putus streak 7 harimu!"),
    ReminderMessage("Singkat", "Streak-mu menunggu 🔥", "Belajar 10 menit hari ini yuk."),
)

private val dayLabels = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

@Composable
fun ReminderScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var enabled by rememberSaveable { mutableStateOf(false) }
    var permissionGranted by rememberSaveable { mutableStateOf(false) }
    var showPermissionDialog by rememberSaveable { mutableStateOf(false) }
    var hour by rememberSaveable { mutableIntStateOf(19) }
    var minute by rememberSaveable { mutableIntStateOf(0) }
    var days by rememberSaveable { mutableStateOf(setOf(0, 1, 2, 3, 4)) }
    var messageIndex by rememberSaveable { mutableIntStateOf(0) }
    var isSaving by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    LaunchedEffect(Unit) { NotificationHelper.createChannel(context) }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val msg = reminderMessages[messageIndex]
            NotificationHelper.show(context, msg.title, msg.body)
        } else {
            scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar("Izin notifikasi ditolak. Aktifkan lewat Pengaturan.")
            }
        }
    }

    // Validasi: pengingat aktif wajib punya minimal 1 hari
    val daysError = enabled && days.isEmpty()
    val canSave = enabled && !daysError && !isSaving
    val timeText = "%02d.%02d".format(hour, minute)

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(40.dp).clip(CircleShape).clickable { onBack() }.padding(8.dp),
                )
                Text("Pengingat Belajar", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(start = 8.dp))
            }

            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Pratinjau notifikasi
                NotificationPreview(message = reminderMessages[messageIndex], dimmed = !enabled)

                // Switch utama
                SectionCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Aktifkan pengingat", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                if (enabled) "Aktif setiap $timeText" else "Dapatkan notifikasi untuk lanjut belajar",
                                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(
                            checked = enabled,
                            onCheckedChange = { want ->
                                // Mockup izin notifikasi (hanya sekali)
                                if (want && !permissionGranted) showPermissionDialog = true else enabled = want
                            },
                            colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary),
                        )
                    }
                }

                // Pemilih jam
                SectionCard(dimmed = !enabled) {
                    SectionTitle("Waktu pengingat")
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        TimeColumn(value = hour, enabled = enabled, onUp = { hour = (hour + 1) % 24 }, onDown = { hour = (hour + 23) % 24 })
                        Text(":", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(horizontal = 12.dp))
                        TimeColumn(value = minute, enabled = enabled, onUp = { minute = (minute + 5) % 60 }, onDown = { minute = (minute + 55) % 60 })
                    }
                }

                // Pemilih hari
                SectionCard(dimmed = !enabled) {
                    SectionTitle("Ulangi pada hari")
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        dayLabels.forEachIndexed { index, label ->
                            DayChip(label = label, selected = index in days, enabled = enabled) {
                                days = if (index in days) days - index else days + index
                            }
                        }
                    }
                    if (daysError) {
                        Spacer(Modifier.height(8.dp))
                        Text("Pilih minimal satu hari", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                    }
                }

                // Gaya pesan
                SectionCard(dimmed = !enabled) {
                    SectionTitle("Gaya pesan")
                    reminderMessages.forEachIndexed { index, msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium).clickable(enabled = enabled) { messageIndex = index },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(selected = messageIndex == index, onClick = { if (enabled) messageIndex = index }, enabled = enabled)
                            Text(msg.label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // Tombol bawah
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ReminderButton(
                    text = "COBA NOTIFIKASI", outlined = true,
                    onClick = {
                        if (NotificationHelper.hasPermission(context)) {
                            val msg = reminderMessages[messageIndex]
                            NotificationHelper.show(context, msg.title, msg.body)
                        } else {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    },
                )
                ReminderButton(
                    text = "SIMPAN PENGINGAT", loading = isSaving, enabled = canSave,
                    onClick = {
                        scope.launch {
                            isSaving = true
                            delay(800)
                            isSaving = false
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar("Pengingat disimpan untuk pukul $timeText")
                        }
                    },
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 156.dp, start = 16.dp, end = 16.dp, top = 16.dp),
        ) { data ->
            Snackbar(shape = MaterialTheme.shapes.large, containerColor = MaterialTheme.colorScheme.onSurface, contentColor = MaterialTheme.colorScheme.surface) {
                Text(text = data.visuals.message, style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp))
            }
        }
    }

    // Dialog izin (mockup)
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Izinkan Legaloo mengirim notifikasi?") },
            text = { Text("Notifikasi dipakai untuk mengingatkanmu melanjutkan kursus.") },
            confirmButton = {
                TextButton(onClick = { permissionGranted = true; enabled = true; showPermissionDialog = false }) { Text("Izinkan") }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) { Text("Jangan izinkan") }
            },
        )
    }
}

/* ---------- Komponen ---------- */

@Composable
private fun NotificationPreview(message: ReminderMessage, dimmed: Boolean) {
    Column(modifier = Modifier.alpha(if (dimmed) 0.5f else 1f)) {
        Text("PRATINJAU NOTIFIKASI", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surface)
                .border(2.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
                .padding(14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Filled.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp)) }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Legaloo • sekarang", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(message.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(message.body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun SectionCard(dimmed: Boolean = false, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().alpha(if (dimmed) 0.5f else 1f)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .border(2.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
            .padding(16.dp),
    ) { content() }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
}

@Composable
private fun TimeColumn(value: Int, enabled: Boolean, onUp: () -> Unit, onDown: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            Icons.Filled.KeyboardArrowUp, contentDescription = "Tambah", tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp).clip(CircleShape).clickable(enabled = enabled) { onUp() }.padding(6.dp),
        )
        Text("%02d".format(value), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
        Icon(
            Icons.Filled.KeyboardArrowDown, contentDescription = "Kurangi", tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp).clip(CircleShape).clickable(enabled = enabled) { onDown() }.padding(6.dp),
        )
    }
}

@Composable
private fun DayChip(label: String, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(40.dp).clip(CircleShape)
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            .border(2.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, CircleShape)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label, fontSize = 11.sp, style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ReminderButton(
    text: String,
    loading: Boolean = false,
    enabled: Boolean = true,
    outlined: Boolean = false,
    onClick: () -> Unit,
) {
    val press = rememberPressState(maxDepth = 4.dp)
    val face = if (outlined) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.secondary
    val edge = if (outlined) MaterialTheme.colorScheme.outline else LocalExtraColors.current.secondaryEdge
    val contentColor = if (outlined) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    Box(modifier = Modifier.fillMaxWidth().height(56.dp).alpha(if (enabled || loading) 1f else 0.5f).pressClickable(press) { if (enabled) onClick() }) {
        Box(modifier = Modifier.fillMaxWidth().height(52.dp).offset(y = 4.dp).background(edge, MaterialTheme.shapes.large))
        Box(
            modifier = Modifier.pressOffset(press).fillMaxWidth().height(52.dp)
                .background(face, MaterialTheme.shapes.large)
                .then(if (outlined) Modifier.border(2.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            if (loading) CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 3.dp, color = contentColor)
            else Text(text, style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp), color = contentColor)
        }
    }
}

@Preview(showBackground = true, widthDp = 395, heightDp = 850)
@Composable
private fun ReminderScreenPreview() { LegalooTheme { ReminderScreen(onBack = {}) } }
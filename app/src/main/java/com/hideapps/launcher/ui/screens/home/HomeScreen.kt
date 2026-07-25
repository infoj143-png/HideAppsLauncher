package com.hideapps.launcher.ui.screens.home

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hideapps.launcher.domain.model.AppInfo

/**
 * A custom [Painter] to render Android's system [Drawable]s, such as adaptive icons.
 */
class DrawablePainter(private val drawable: Drawable) : Painter() {
    override val intrinsicSize: Size
        get() = if (drawable.intrinsicWidth >= 0 && drawable.intrinsicHeight >= 0) {
            Size(drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())
        } else {
            Size.Unspecified
        }

    override fun DrawScope.onDraw() {
        drawIntoCanvas { canvas ->
            drawable.setBounds(0, 0, size.width.toInt(), size.height.toInt())
            drawable.draw(canvas.nativeCanvas)
        }
    }
}

@Composable
fun rememberDrawablePainter(drawable: Drawable?): Painter {
    return remember(drawable) {
        if (drawable != null) {
            DrawablePainter(drawable)
        } else {
            object : Painter() {
                override val intrinsicSize: Size = Size.Unspecified
                override fun DrawScope.onDraw() {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: AppsViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToHiddenAppsPin: () -> Unit,
    onNavigateToPinSetup: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val isPinSetup by viewModel.isPinSetup.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedAppForSheet by remember { mutableStateOf<AppInfo?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var appToHide by remember { mutableStateOf<AppInfo?>(null) }

    val sheetState = rememberModalBottomSheetState()

    // Periodically refresh pin setup status when screen becomes visible/active
    LaunchedEffect(Unit) {
        viewModel.checkPinSetup()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HideApps Launcher") },
                actions = {
                    // Hidden Apps Badge
                    IconButton(
                        onClick = {
                            viewModel.checkPinSetup()
                            if (isPinSetup) {
                                onNavigateToHiddenAppsPin()
                            } else {
                                onNavigateToPinSetup()
                            }
                        }
                    ) {
                        BadgedBox(
                            badge = {
                                if (state.hiddenAppsCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ) {
                                        Text(
                                            text = state.hiddenAppsCount.toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Hidden Apps"
                            )
                        }
                    }

                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Real-time SearchBar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search apps...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Search"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (state.error != null) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.error ?: "An unexpected error occurred",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadApps() },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Retry")
                        }
                    }
                } else if (state.apps.isEmpty()) {
                    val emptyMessage = if (state.searchQuery.isNotEmpty()) {
                        "No results match your search"
                    } else {
                        "No apps found"
                    }
                    Text(
                        text = emptyMessage,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = state.apps,
                            key = { it.packageName }
                        ) { app ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {
                                            val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                            if (launchIntent != null) {
                                                context.startActivity(launchIntent)
                                            }
                                        },
                                        onLongClick = {
                                            selectedAppForSheet = app
                                            showBottomSheet = true
                                        }
                                    )
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    Card(
                                        modifier = Modifier.fillMaxSize(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (app.icon != null) {
                                                Image(
                                                    painter = rememberDrawablePainter(app.icon),
                                                    contentDescription = "${app.label} Icon",
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else {
                                                // Fallback
                                                Text(
                                                    text = app.label.firstOrNull()?.toString() ?: "?",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontSize = 24.sp
                                                )
                                            }
                                        }
                                    }

                                    // Small Hide icon on the app card as a quick action button
                                    IconButton(
                                        onClick = {
                                            appToHide = app
                                            showConfirmDialog = true
                                        },
                                        modifier = Modifier
                                            .size(20.dp)
                                            .align(Alignment.TopEnd)
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Hide App Directly",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = app.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet on Long Press
    if (showBottomSheet && selectedAppForSheet != null) {
        val app = selectedAppForSheet!!
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                selectedAppForSheet = null
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = app.label,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Hide App Option
                ListItem(
                    headlineContent = { Text("Hide App") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Hide App"
                        )
                    },
                    modifier = Modifier.combinedClickable(
                        onClick = {
                            showBottomSheet = false
                            selectedAppForSheet = null
                            appToHide = app
                            showConfirmDialog = true
                        }
                    )
                )

                // App Info Option
                ListItem(
                    headlineContent = { Text("App Info") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "App Info"
                        )
                    },
                    modifier = Modifier.combinedClickable(
                        onClick = {
                            showBottomSheet = false
                            selectedAppForSheet = null
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:${app.packageName}")
                            )
                            context.startActivity(intent)
                        }
                    )
                )

                // Cancel Option
                ListItem(
                    headlineContent = { Text("Cancel") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel"
                        )
                    },
                    modifier = Modifier.combinedClickable(
                        onClick = {
                            showBottomSheet = false
                            selectedAppForSheet = null
                        }
                    )
                )
            }
        }
    }

    // Confirmation Dialog before hiding an app
    if (showConfirmDialog && appToHide != null) {
        val app = appToHide!!
        AlertDialog(
            onDismissRequest = {
                showConfirmDialog = false
                appToHide = null
            },
            title = { Text("Hide App?") },
            text = { Text("Are you sure you want to hide '${app.label}'? It will only be visible in the secure Hidden Apps screen.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.hideApp(app.packageName)
                        showConfirmDialog = false
                        appToHide = null
                    }
                ) {
                    Text("Hide", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        appToHide = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

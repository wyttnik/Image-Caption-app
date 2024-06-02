package com.example.imagecaptionapp.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.imagecaptionapp.BuildConfig
import com.example.imagecaptionapp.General.createImageFile
import com.example.imagecaptionapp.General.currentImageUri
import com.example.imagecaptionapp.General.deleteCacheDir
import com.example.imagecaptionapp.General.uri
import com.example.imagecaptionapp.R

import com.example.imagecaptionapp.ui.AppViewModelProvider
import com.example.imagecaptionapp.ui.TopAppBar
import com.example.imagecaptionapp.ui.navigation.NavigationDestination
import java.io.File
import java.util.Objects

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navigateToImageChooser: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        HomeBody(
            onItemClick = navigateToImageChooser,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            uploadImage = viewModel::uploadImage,
            resetCaption = viewModel::resetAnswer,
            caption = viewModel.captionState.string,
            voiceCaption = viewModel::textToSpeech
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
private fun HomeBody(
    onItemClick: () -> Unit, modifier: Modifier = Modifier,
    uploadImage: (file:File) -> Unit, resetCaption: () -> Unit, caption: String,
    voiceCaption: (context: Context) -> Unit
) {
    val context = LocalContext.current

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            capturedImageUri = uri
            currentImageUri = uri
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (currentImageUri.path?.isNotEmpty() == true) {
            val conf = LocalConfiguration.current
            AsyncImage(
                model = if (capturedImageUri.path?.isNotEmpty() == true) capturedImageUri
                    else currentImageUri,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.height(conf.screenWidthDp.dp).width(conf.screenWidthDp.dp)
            )
            Button(
                onClick = {
                    resetCaption()
                    val stream = context.contentResolver.openInputStream(
                        currentImageUri
                    )
                    val file = File.createTempFile("image_${System.currentTimeMillis()}",
                        ".jpg",context.externalCacheDir)
                    if (stream != null) {
                        file.outputStream().use { output ->
                            stream.copyTo(output)
                        }
                    }

                    uploadImage(file)
                    stream?.close()
                },
                shape = MaterialTheme.shapes.small,
                enabled = true,
                modifier = Modifier.padding(end = 45.dp)
            ){
                Text("Upload Image")
            }
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = {voiceCaption(context)},
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            enabled = caption.isNotEmpty()
        ) {
            Text(caption)
        }
        Spacer(Modifier.weight(1f))
        Row {
            Button(
                onClick = {
                    resetCaption()
                    context.deleteCacheDir()
                    onItemClick()
                          },
                shape = MaterialTheme.shapes.small,
                enabled = true,
                modifier = Modifier.padding(end = 45.dp)
            ){
                Text("Select Image")
            }
            Button(
                onClick = {
                    resetCaption()
                    context.deleteCacheDir()
                    val file = context.createImageFile()
                    uri = FileProvider.getUriForFile(
                        Objects.requireNonNull(context),
                        BuildConfig.APPLICATION_ID + ".provider", file
                    )
                    cameraLauncher.launch(uri)
                          },
                shape = MaterialTheme.shapes.small,
                enabled = true,
                modifier = Modifier.padding(start = 45.dp)
            ){
                Text("Take Photo")
            }
        }

        Spacer(Modifier.weight(1f))
    }
}
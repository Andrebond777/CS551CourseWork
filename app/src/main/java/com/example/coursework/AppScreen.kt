package com.example.coursework

import android.annotation.SuppressLint
import android.text.Highlights
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.coursework.ui.AppViewModel
import com.example.coursework.ui.EnterDataScreen
import com.example.coursework.ui.HighlightsScreen
import com.example.coursework.ui.MainScreen
import com.example.coursework.ui.TipsScreen


enum class AppScreen(@StringRes val title: Int) {
    Main(title = R.string.main_screen),
    EnterData(title = R.string.enter_data_screen),
    Tips(title = R.string.tips_screen),
    Highlights(title = R.string.highlights_screen)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title), color = Color.White) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = colorResource(id = R.color.blue)
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppScreen(
    viewModel: AppViewModel,
    navController: NavHostController = rememberNavController())
{
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Main.name
    )

    Scaffold(
        topBar = {
            MainScreenBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ){ innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = AppScreen.Main.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = AppScreen.Main.name) {
                MainScreen(
                    viewModel,
                    modifier = Modifier,
                    navController
                )
            }
            composable(route = AppScreen.EnterData.name) {
                EnterDataScreen(
                    navController,
                    viewModel
                )
            }
            composable(route = AppScreen.Tips.name) {
                TipsScreen(
                    navController
                )
            }
            composable(route = AppScreen.Highlights.name) {
                HighlightsScreen(
                    navController
                )
            }
        }
    }
}


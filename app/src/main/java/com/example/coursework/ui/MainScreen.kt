package com.example.coursework.ui

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.coursework.AppScreen
import com.example.coursework.R
import com.example.coursework.ui.theme.CourseWorkTheme
import com.example.coursework.worker.NotificationWorker
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Date
import com.example.coursework.model.StepsData
import com.example.coursework.model.WaterData
import com.example.coursework.repository.UserRepository
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)

fun getDate(daysAgo: Int): DayOfWeek? {
    val date = LocalDate.now().minusDays(daysAgo.toLong())
    return date.dayOfWeek;
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)

@Composable
fun MainScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier,
    navHostController: NavHostController
)
{


    val uiState by viewModel.uiState.collectAsState()
    val dailyKCalProgress = uiState.dayKCal.toFloat() / uiState.dailyKCalIntake.toFloat()

    val recommendedSteps by viewModel.recommendedSteps.collectAsState()
    // steps today
    val stepsToday by viewModel.stepsToday.collectAsState()
    // steps week
    val stepsWeek by viewModel.stepsWeek.collectAsState()

    val waterGiven by viewModel.waterGiven.collectAsState()
    val key = remember { mutableStateOf(0) }

    val isTrg by viewModel.isTrigger.collectAsState()


    val dateToday by viewModel.dateToday.collectAsState()

    val dailyStepsProgress = stepsToday?.div(recommendedSteps.toFloat());

    // Weather Data
    val weatherState by viewModel.weatherState.collectAsState()
    viewModel.fetchWeatherData()

    // Local Context
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    // Call notification function from NotificationWorker
    val notificationWorker = NotificationWorker()

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val locPermission           = rememberPermissionState(Manifest.permission.ACCESS_COARSE_LOCATION)
    val activityPermissionState = rememberPermissionState(Manifest.permission.ACTIVITY_RECOGNITION)
    val notiPermissionState     = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    // Request permission
    val requestMultiplePermissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val deniedPermissions = permissionsMap.entries.filter { !it.value }.map { it.key }
        if (deniedPermissions.isNotEmpty()) {
            // Handle denied permissions
        } else {
            // All requested permissions were granted
        }
    }

    // WHen the apps first Launch, run this
    LaunchedEffect(Unit) {
        notificationWorker.createNotificationChannel("ChannelID", context)

        val requiredPermissions = mutableListOf<String>()

        if (!locationPermissionState.status.isGranted) {
            requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (!activityPermissionState.status.isGranted) {
            requiredPermissions.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }

        if (!locPermission.status.isGranted) {
            requiredPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (!notiPermissionState.status.isGranted) {
            requiredPermissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (requiredPermissions.isNotEmpty()) {
            requestMultiplePermissionsLauncher.launch(requiredPermissions.toTypedArray())
        }

        viewModel.getStepsLastSevenDays()
        viewModel.getStepsEveryDayLastSevenDays()
        viewModel.getStepsToday()
    }

    LaunchedEffect(key.value) {
//        viewModel.getStepsLastSevenDays()
//        viewModel.getStepsEveryDayLastSevenDays()
//        viewModel.getStepsToday()
    }


    //Runs the Weather Watcher worker for testing purposes
    //viewModel.testWeatherWatcherWorker()

//    viewModel.runWeatherWatcherWorker()
    viewModel.getNewWaterData()
    viewModel.runWaterTrigger()
  

    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {

        Spacer(modifier = Modifier.size(30.dp))
        // Bryant - Start of weather box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20))
                .background(Color.LightGray)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {

                    when (weatherState) {
                        is WeatherState.Success -> {
                            val weatherData = (weatherState as WeatherState.Success).weatherData

                            Text(
                                text = "${weatherData.location.name}, ${weatherData.location.country}",
                                color = colorResource(id = R.color.blue),
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )

                            Row(
                                modifier = Modifier
                            ){
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data("https://${weatherData.current.condition.icon.removePrefix("//")}")
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "${weatherData.current.condition.text}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.clip(CircleShape)
                                )
                                Text(
                                    text = "${weatherData.current.temp_c} °C , ${weatherData.current.condition.text}",
                                    color = colorResource(id = R.color.blue),
                                    fontSize = 16.sp
                                )
                            }

                        } // end of weather api load success

                        is WeatherState.Loading -> {
                            CircularProgressIndicator()
                        } // end of weather is still loading

                        is WeatherState.Error -> {
                            Text(
                                text = "Failed to fetch weather data.",
                                color = colorResource(id = R.color.blue),
                                fontSize = 16.sp
                            )
                        } // end of weather is failed to load

                    } // end of when

                } // end of Column

            } // end of Row

        } // end of weather box
        Spacer(modifier = Modifier.size(20.dp))




        Button(onClick = { navHostController.navigate(AppScreen.Highlights.name) },
            colors= ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue) ),
            shape = RoundedCornerShape(20),
            modifier = Modifier
                .padding(20.dp, 10.dp)
                .height(110.dp)
                .fillMaxWidth()
                .align(alignment = Alignment.Start))

        {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    var stepCount = 0;
                    var date : DayOfWeek? = getDate(0);
                    if(viewModel._stepsEveryDayWeek.size > 0)
                    {
                        stepCount = viewModel._stepsEveryDayWeek.maxByOrNull { x -> x!! }!!
                        date = getDate(viewModel._stepsEveryDayWeek.size - 1 - viewModel._stepsEveryDayWeek.indexOf(stepCount));
                    }
                    Text(
                        text = "You made " + stepCount + " steps on " + date + "!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Tap to see more",
                        color = Color.LightGray,
                        fontSize = 16.sp
                    )
                }
            }

        }

        Spacer(modifier = Modifier.size(10.dp))

        Button(onClick = { navHostController.navigate(AppScreen.StepsProgress.name) },
            colors= ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue) ),
            shape = RoundedCornerShape(20),
            modifier = Modifier
                .padding(20.dp, 10.dp)
                .height(110.dp)
                .fillMaxWidth()
                .align(alignment = Alignment.Start))

        {

            Box(contentAlignment = Alignment.Center) {
                if (dailyStepsProgress != null) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(90.dp),
                        color = Color.White,
                        progress = dailyStepsProgress,
                        strokeWidth = 8.dp
                    )
                }
                if (dailyStepsProgress != null) {
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = "" + (dailyStepsProgress*100).toInt() +"%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                }
            }

            Column (modifier = Modifier.padding(horizontal = 15.dp)) {
                Text(
                    text = "Highlights",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "You've walked ${stepsToday} steps out of your daily goal of $recommendedSteps!",
                    color = Color.LightGray,
                    fontSize = 16.sp
                )
            }

        }

        Spacer(modifier = Modifier.size(10.dp))

        Button(onClick = { navHostController.navigate(AppScreen.Tips.name) },
            colors= ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue) ),
            shape = RoundedCornerShape(20),
            modifier = Modifier
                .padding(20.dp, 10.dp)
                .height(110.dp)
                .fillMaxWidth()
                .align(alignment = Alignment.Start))

        {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Tips and tricks",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Get an additional advice on losing weight.",
                        color = Color.LightGray,
                        fontSize = 16.sp
                    )

                }
            }

        }


        Button(onClick = { navHostController.navigate(AppScreen.EnterData.name) },
            colors= ButtonDefaults.buttonColors(containerColor = Color.LightGray ),
            shape = RoundedCornerShape(20),
            modifier = Modifier
                .padding(22.dp)
                .height(90.dp)
                .fillMaxWidth()
                .align(alignment = Alignment.CenterHorizontally))

        {

            Icon(
                imageVector = Icons.Outlined.Create,
                contentDescription = "icon",
                modifier = Modifier.size(30.dp),
                tint = colorResource(R.color.blue)
            )

            Text(text = "Enter your parameters",
                color = colorResource(R.color.blue),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp)
        }

    }
}

@Preview
@Composable
fun MainPreview() {
    CourseWorkTheme {
        MainScreen(
            modifier = Modifier
                .fillMaxSize(),
            navHostController = rememberNavController(),
            viewModel = viewModel()
        )
    }
}

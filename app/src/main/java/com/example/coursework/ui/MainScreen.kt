package com.example.coursework.ui

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.coursework.AppScreen
import com.example.coursework.R
import com.example.coursework.ui.theme.CourseWorkTheme
import com.example.coursework.worker.NotificationWorker


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    viewModel: AppViewModel = viewModel(),
    modifier: Modifier = Modifier,
    navHostController: NavHostController
)
{
    val uiState by viewModel.uiState.collectAsState()
    val dailyKCalProgress = uiState.dayKCal.toFloat() / uiState.dailyKCalIntake.toFloat()

    // Weather Data
    val weatherState by viewModel.weatherState.collectAsState()
    viewModel.fetchWeatherData()

    // Local Context
    val context = LocalContext.current

    // Call notification function from NotificationWorker
    val notificationWorker = NotificationWorker()

    // When apps run, will enable the notification channel first
    LaunchedEffect(Unit) {
        notificationWorker.createNotificationChannel("ChannelID", context)
    }

    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {

        Button(
            onClick = { navHostController.navigate(AppScreen.Tips.name) },
            colors= ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue) ),
            shape = RoundedCornerShape(20),
            modifier = Modifier
                .padding(20.dp, 10.dp).fillMaxWidth().height(60.dp)
                .align(alignment = Alignment.Start))
        {

            Text(
                text = "Tips and tricks",
                color = colorResource(R.color.white),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }



        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20))
                .background(colorResource(id = R.color.blue))
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Highlights",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "On average you're walking less this year compared to last year.",
                        color = Color.LightGray,
                        fontSize = 16.sp
                    )


                }
            }

        }

        // Spacer(modifier = Modifier.size(230.dp))

        // Bryant - Start of weather box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20))
                .background(colorResource(id = R.color.blue))
                .combinedClickable(
                    onLongClick = {
                        notificationWorker.triggerNotification(
                            context,
                            "Testing Notification123",
                            "Its working now"
                        )
                    },
                    onClick = {
                        // do ntg
                    }
                )
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

                            // When successfully getting the data from the api, call the notification funciton
//                            notificationWorker.triggerNotification(
//                                context,
//                                "Testing Notification123",
//                                "Notification Message"
//                            )

                            Text(
                                text = "${weatherData.location.name}, ${weatherData.location.country}",
                                color = Color.White,
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
                                    color = Color.LightGray,
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
                                color = Color.LightGray,
                                fontSize = 16.sp
                            )
                        } // end of weather is failed to load

                    } // end of when

                } // end of Column

            } // end of Row

        } // end of weather box

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20))
                .background(colorResource(id = R.color.blue))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(75.dp),
                        color = Color.LightGray,
                        progress = dailyKCalProgress,
                        strokeWidth = 8.dp,
                    )

                    Text(
                        text = "" + (dailyKCalProgress*100).toInt() +"%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                }

                Column {
                    Text(
                        text = "Great!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "You've lost "+ (dailyKCalProgress*100).toInt()+ "% of your \ndaily calorie intake",
                        color = Color.LightGray,
                        fontSize = 16.sp
                    )
                }


            }
        }


        Button(onClick = { navHostController.navigate(AppScreen.EnterData.name) },
            colors= ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue) ),
            shape = RoundedCornerShape(20),
            modifier = Modifier
                .padding(22.dp).fillMaxWidth().height(60.dp)
                .align(alignment = Alignment.CenterHorizontally))

        {

            Icon(
                imageVector = Icons.Outlined.Create,
                contentDescription = "icon",
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )

            Text(text = "Enter your parameters",
                color = colorResource(R.color.white),
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
            navHostController = rememberNavController()
        )
    }
}
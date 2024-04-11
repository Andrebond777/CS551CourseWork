package com.example.coursework.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coursework.AppScreen
import com.example.coursework.R
import com.example.coursework.data.UiState
import com.example.coursework.ui.theme.CourseWorkTheme


@Composable
fun MainScreen(    viewModel: AppViewModel = viewModel(),
                   modifier: Modifier = Modifier,
               navHostController: NavHostController
)
{
    val uiState by viewModel.uiState.collectAsState()
    val dailyKCalProgress = uiState.dayKCal.toFloat() / uiState.dailyKCalIntake.toFloat()


    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {




        Button(onClick = { navHostController.navigate(AppScreen.Tips.name) },
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

        Spacer(modifier = Modifier.size(230.dp))

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
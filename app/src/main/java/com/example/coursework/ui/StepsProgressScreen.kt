package com.example.coursework.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coursework.R
import com.example.coursework.model.StepsData

@Composable
fun StepsProgressScreen(navController: NavController, viewModel: AppViewModel) {
    val stepsToday by viewModel.stepsToday.collectAsState()
    val stepsWeek by viewModel.stepsWeek.collectAsState()
    val recommendedSteps by viewModel.recommendedSteps.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(colorResource(id = R.color.white)),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp)) // Space at the top for better alignment

        stepsToday?.let {
            ProgressRing(
                progress = it.toFloat(),
                max = recommendedSteps.toFloat(),
                label = "${stepsToday} / $recommendedSteps Daily Steps"
            )
        }

        stepsWeek?.let {
            ProgressRing(
                progress = it.toFloat(),
                max = (recommendedSteps * 7).toFloat(),
                label = "${stepsWeek} / ${recommendedSteps * 7} Weekly Steps"
            )
        }

        Button(
            onClick = { viewModel.addSteps(StepsData(stepCount = 500)) },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue)),
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Add 500 Steps",
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(Modifier.height(16.dp)) // Space at the bottom for better alignment
    }
}

@Composable
fun ProgressRing(progress: Float, max: Float, label: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(230.dp)
            .padding(8.dp)
    ) {
        CircularProgressIndicator(
            progress = progress / max,
            color = colorResource(id = R.color.blue),
            strokeWidth = 8.dp,
            modifier = Modifier.matchParentSize()
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStepsProgressScreen() {
    val navController = rememberNavController()
    StepsProgressScreen(navController, viewModel = viewModel())
}

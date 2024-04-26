package com.example.coursework.ui

import android.text.Highlights
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.model.Point
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarChartType
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import com.example.coursework.AppScreen
import com.example.coursework.R
import com.example.coursework.ui.theme.CourseWorkTheme
import java.time.DayOfWeek
import java.util.Date
import kotlin.random.Random


@Composable
fun HighlightsScreen(navHostController: NavHostController, viewModel: AppViewModel)
{
    val stepsEveryDayOfWeek = viewModel._stepsEveryDayWeek

    Column(
        modifier = Modifier
            .padding(5.dp, 0.dp),
        verticalArrangement = Arrangement.spacedBy(-10.dp)
    ) {

        Spacer(modifier = Modifier.padding(20.dp))

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
                }
            }
        }


        Spacer(modifier = Modifier.padding(15.dp))


        val listSize = stepsEveryDayOfWeek.size;
        if(listSize > 0)
        {
            var maxRange = stepsEveryDayOfWeek.maxBy { x -> x!! }!!;
            maxRange = Math.floor((maxRange/1000).toDouble()).toInt() + 1
            val barData = arrayListOf<BarData>()
            for (index in 0 until listSize) {
                val point = Point(index.toFloat(), stepsEveryDayOfWeek[index]!!.toFloat())
                val cntr = index + 1
                barData.add(
                    BarData(
                        point = point,
                        colorResource(id = R.color.blue),
                        dataCategoryOptions = DataCategoryOptions(),
                        label = "Day $cntr",
                    )
                )
            }

            val yStepSize = maxRange

            val xAxisData = AxisData.Builder()
                .axisStepSize(30.dp)
                .steps(barData.size - 1)
                .bottomPadding(40.dp)
                .axisLabelAngle(20f)
                .startDrawPadding(48.dp)
                .labelData { index -> barData[index].label }
                .build()
            val yAxisData = AxisData.Builder()
                .steps(yStepSize)
                .labelAndAxisLinePadding(20.dp)
                .axisOffset(20.dp)
                .labelData { index -> (index * (maxRange.toDouble() / yStepSize.toDouble())).toInt().toString() + "k" }
                .build()
            val barChartData = BarChartData(
                chartData = barData,
                xAxisData = xAxisData,
                yAxisData = yAxisData,
                barStyle = BarStyle(
                    paddingBetweenBars = 20.dp,
                    barWidth = 25.dp
                ),
                showYAxis = true,
                showXAxis = true,
                horizontalExtraSpace = 10.dp,
            )
            BarChart(modifier = Modifier.height(350.dp).padding(top = 20.dp), barChartData = barChartData)
        }
    }
}

@Preview
@Composable
fun HighlightsPreview() {
    CourseWorkTheme {
        HighlightsScreen(
            navHostController = rememberNavController(),
            viewModel = viewModel()
        )
    }
}
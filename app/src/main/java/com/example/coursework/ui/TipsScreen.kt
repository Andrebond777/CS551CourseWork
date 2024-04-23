package com.example.coursework.ui

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.coursework.R
import com.example.coursework.ui.theme.CourseWorkTheme

val doTips = listOf("get active for 150 minutes a week – you can break this up into shorter sessions",
            "aim to get your 5 A Day – 80g of fresh, canned or frozen fruit or vegetables count as 1 portion",
    "aim to lose 1 to 2lbs, or 0.5 to 1kg, a week",
    "read food labels – products with more green colour coding than amber and red are often a healthier option",
    "swap sugary drinks for water – if you do not like the taste, add slices of lemon or lime for flavour")

val doNotTips = listOf("do not lose weight suddenly with diets",
    "do not stock unhealthy food – popcorn, fruit and rice cakes can be healthier alternatives",
    "do not skip meals – you might end up snacking more because you feel hungry",
    "do not finish your plate if you're full – you can save leftover food for the next day")

@Composable
fun TipsScreen(navHostController: NavHostController)
{
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

        Spacer(modifier = Modifier.padding(15.dp))

        Box(
            modifier = Modifier
                .align(Alignment.Start)
                .fillMaxWidth()
                .padding(22.dp)
                .height(200.dp)
                .clip(RoundedCornerShape(20))
                .background(colorResource(id = R.color.blue))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp, 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Column {
                    Box(Modifier.clip(RoundedCornerShape(100)).background(colorResource(id = R.color.green)).padding(10.dp, 0.dp)
                        )
                    {
                        Text(
                            text = "Do",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                        )
                    }

                    LazyColumn {
                        items(doTips) {
                            Row (modifier = Modifier.padding(0.dp, 10.dp))
                            {

                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = "icon",
                                    modifier = Modifier.size(30.dp),
                                    tint = colorResource(R.color.green)
                                )
                                Text(
                                    text = "$it",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Start)
                .fillMaxWidth()
                .padding(22.dp)
                .clip(RoundedCornerShape(20))
                .background(colorResource(id = R.color.blue))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp, 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Column {
                    Box(Modifier.clip(RoundedCornerShape(100)).background(Color.Red).padding(12.dp, 0.dp)
                    )
                    {
                        Text(
                            text = "Don`t",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                        )
                    }

                    LazyColumn {
                        items(doNotTips) {
                            Row (modifier = Modifier.padding(0.dp, 10.dp))
                            {

                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = "icon",
                                    modifier = Modifier.size(30.dp),
                                    tint = Color.Red
                                )
                                Text(
                                    text = "$it",
                                    color = Color.White
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TipsPreview() {
    CourseWorkTheme {
        TipsScreen(
            navHostController = rememberNavController()
        )
    }
}
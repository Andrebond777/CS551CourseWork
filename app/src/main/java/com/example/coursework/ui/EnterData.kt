package com.example.coursework.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.coursework.R
import com.example.coursework.model.UserData
import com.example.coursework.ui.theme.CourseWorkTheme

@Composable
fun EnterDataScreen(navHostController: NavHostController, viewModel: AppViewModel) {

    val (height, setHeight) = remember { mutableStateOf("") }
    val (weight, setWeight) = remember { mutableStateOf("") }
    val (age, setAge) = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = stringResource(id = R.string.enter_parameters),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.padding(0.dp))

        Text(
            text = stringResource(id = R.string.enter_height),
            style = MaterialTheme.typography.bodyLarge
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = height,
            onValueChange = setHeight,
            placeholder = { Text(text = "e.g. 180") },
            isError = height.isNotEmpty() && !isValidText(height)
        )

        Text(
            text = stringResource(id = R.string.enter_weight),
            style = MaterialTheme.typography.bodyLarge
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = weight,
            onValueChange = setWeight,
            placeholder = { Text(text = "e.g. 75") },
            isError = weight.isNotEmpty() && !isValidText(weight)
        )

        Text(
            text = stringResource(id = R.string.enter_age),
            style = MaterialTheme.typography.bodyLarge
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = age,
            onValueChange = setAge,
            placeholder = { Text(text = "e.g. 30") },
            isError = age.isNotEmpty() && !isValidText(age)
        )

        if(weight.isNotEmpty() && height.isNotEmpty() && age.isNotEmpty()) {
            if (!isValidText(weight) || !isValidText(height) || !isValidText(age))
                Text(text = "Please enter valid values in all fields\n" +
                        "Allowed numbers from 1 to 500", color = Color.Red)
        }

        Spacer(modifier = Modifier.padding(0.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                var newData = UserData(height = height, weight = weight, age = age)
                viewModel.insertUserData(newData)
                setHeight("")
                setWeight("")
                setAge("")
            },colors= ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue) ),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Text(
                text = stringResource(id = R.string.save),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(2.dp),
                fontSize = 23.sp
            )
        }
    }
}

fun isValidText(text: String): Boolean {
    // Add your custom validation rules here
    var num = text.toIntOrNull() ?: 0
    if(num >= 1 && num <= 500)
        return true;
    return false;
}

@Preview
@Composable
fun EnterDataPreview() {
    CourseWorkTheme {
        EnterDataScreen(
            navHostController = rememberNavController(),
            viewModel = viewModel()
        )
    }
}

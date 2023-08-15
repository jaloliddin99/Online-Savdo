package org.don.bottomappbar.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun HomeRoute(
    modifier: Modifier = Modifier
) {
    HomeScreen(modifier = modifier)
}

@Composable
fun HomeScreen(
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(16.dp, 0.dp),
        horizontalAlignment = Alignment.Start
    ) {
        LazyColumn{
            items(100){
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Home Screen $it",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .fillMaxWidth()
                    ,
                    fontSize = 20.sp
                )
            }
        }
    }
}
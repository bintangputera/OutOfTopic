package com.poetralabs.outoftopic.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poetralabs.outoftopic.R
import com.poetralabs.outoftopic.core.theme.DarkOrange
import com.poetralabs.outoftopic.core.theme.DarkSkyBlue
import com.poetralabs.outoftopic.core.theme.DarkTaro
import com.poetralabs.outoftopic.core.theme.LightOrange
import com.poetralabs.outoftopic.core.theme.SkyBlue
import com.poetralabs.outoftopic.core.theme.Taro

enum class HomeMenu(
    val title: String, val desc: String,
    val bgColor: Color, val ornamentColor: Color, val icon: Int
) {
    TruthOrDare(
        title = "Truth or Dare",
        desc = "Pilih tantangan atau jujur-jujuran",
        bgColor = Taro,
        ornamentColor = DarkTaro,
        icon = R.drawable.ic_tod,
    ),
    Question(
        title = "Random Question",
        desc = "Bikin obrolan makin seru dengan pertanyaan random.",
        bgColor = LightOrange,
        ornamentColor = DarkOrange,
        icon = R.drawable.ic_question
    ),
    MiniGames(
        title = "Mini Games",
        desc = "Koleksi mini game seru buat tongkrongan!",
        bgColor = SkyBlue,
        ornamentColor = DarkSkyBlue,
        icon = R.drawable.ic_light_bulb
    )
}

@Composable
fun HomeMenuCard(
    menu: HomeMenu,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = menu.bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                painter = painterResource(R.drawable.quote),
                contentDescription = null,
                tint = menu.ornamentColor,
                modifier = Modifier
                    .padding(16.dp)
                    .size(32.dp)
                    .align(Alignment.TopStart)
            )
            Image(
                painter = painterResource(menu.icon),
                contentDescription = null,
                modifier = Modifier
                    .size(140.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 16.dp)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = menu.title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = menu.desc,
                    style = MaterialTheme.typography.titleLarge.copy(lineHeight = 18.sp,
                        fontSize = 24.sp, fontWeight = FontWeight.Normal),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
@Preview(device = "id:pixel_4_xl", showSystemUi = true)
fun HomeMenuPreview() {
    Column {
        HomeMenuCard(menu = HomeMenu.Question, onClick = {})
    }
}
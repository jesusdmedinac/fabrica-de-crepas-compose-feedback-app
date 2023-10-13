package com.jesusdmedinac.feedbackapp.presentation.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesusdmedinac.feedbackapp.domain.model.CommonDomainPage

@Composable
fun MessagePage(
    page: CommonDomainPage,
    onSendNewAnswerClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            page.text,
            fontSize = 32.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onSendNewAnswerClick() },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
        ) {
            Text(
                "Enviar otra respuesta",
                fontSize = 32.sp,
            )
        }
    }
}

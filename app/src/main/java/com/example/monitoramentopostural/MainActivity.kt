package com.example.monitoramentopostural

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.monitoramentopostural.ui.theme.MonitoramentoPosturalTheme

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private val isPostureGood = mutableStateOf(false) // Postura inicialmente inadequada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar sensor de acelerômetro
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        setContent {
            MonitoramentoPosturalTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFFFF6F00) // Fundo laranja
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Ícone principal
                        Image(
                            painter = painterResource(id = R.drawable.ic_postura),
                            contentDescription = "Ícone de Postura",
                            modifier = Modifier.size(150.dp)
                        )

                        // Título
                        Text(
                            text = "Monitoramento Postural",
                            color = Color.Black,
                            modifier = Modifier.padding(16.dp)
                        )

                        // Ícones de status (correto ou alerta)
                        if (isPostureGood.value) {
                            Image(
                                painter = painterResource(id = R.drawable.correto),
                                contentDescription = "Postura Adequada",
                                modifier = Modifier.size(100.dp)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.alerta),
                                contentDescription = "Postura Inadequada",
                                modifier = Modifier.size(100.dp)
                            )
                        }

                        // Mensagem de status com fundo arredondado
                        val backgroundColor = if (isPostureGood.value) Color(0xFF388E3C) else Color(0xFFD32F2F)
                        Text(
                            text = if (isPostureGood.value) "Postura Adequada" else "Postura Inadequada",
                            color = Color.White,
                            modifier = Modifier
                                .padding(16.dp)
                                .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val (x, y, z) = event.values

            // Determina se a postura é adequada (vertical) ou inadequada (horizontal)
            isPostureGood.value = Math.abs(z) > Math.abs(x) && Math.abs(z) > Math.abs(y)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }
}

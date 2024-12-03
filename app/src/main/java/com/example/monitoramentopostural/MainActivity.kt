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
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.monitoramentopostural.ui.theme.MonitoramentoPosturalTheme

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    // Threshold de inclinação para determinar se o celular está na vertical ou horizontal
    private val threshold = 3.0f

    // Variável que controla o estado da postura
    private val isPostureGood = mutableStateOf(false) // Inicialmente, postura inadequada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar o SensorManager e obter o sensor de acelerômetro
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        } else {
            // Se o acelerômetro não for encontrado, exibe uma mensagem para o usuário
            isPostureGood.value = false
        }

        setContent {
            MonitoramentoPosturalTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFFFF6F00) // Cor de fundo laranja (FF6F00)
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Ícone de boneco humano
                        Image(
                            painter = painterResource(id = R.drawable.ic_postura),
                            contentDescription = "Ícone de Postura",
                            modifier = Modifier.size(150.dp)
                        )

                        Text(
                            text = "Monitoramento Postural",
                            color = Color.Black,
                            modifier = Modifier.padding(16.dp)
                        )

                        // Substituindo o círculo verde por ícone de "correto"
                        if (isPostureGood.value) {
                            // Exibe o ícone "correto.png" quando a postura for adequada (vertical)
                            Image(
                                painter = painterResource(id = R.drawable.correto),  // Seu ícone de correto
                                contentDescription = "Postura Adequada",
                                modifier = Modifier.size(100.dp)
                            )
                        } else {
                            // Exibe o ícone de alerta se a postura for inadequada (horizontal)
                            Image(
                                painter = painterResource(id = R.drawable.alerta),  // Seu ícone de alerta
                                contentDescription = "Alerta de Postura Inadequada",
                                modifier = Modifier.size(100.dp)
                            )
                        }

                        // Mensagem com fundo arredondado
                        val backgroundColor = if (isPostureGood.value) Color(0xFF388E3C) else Color(0xFFD32F2F)

                        Text(
                            text = if (isPostureGood.value) "Postura Adequada" else "Postura Inadequada",
                            color = Color.White,
                            modifier = Modifier
                                .padding(16.dp)
                                .background(
                                    color = backgroundColor, // Cor de fundo verde ou vermelho
                                    shape = RoundedCornerShape(12.dp) // Bordas arredondadas
                                )
                                .padding(16.dp) // Padding para garantir o espaço interno
                        )

                        Button(
                            onClick = { /* Ação para acessar dados históricos */ },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = "Ver Dados Históricos")
                        }
                    }
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        // Acelerômetro (X, Y, Z) valores
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Alteração na lógica: inverter a definição de postura adequada e inadequada
        // Agora, se a inclinação do eixo z for maior, significa que está em pé (vertical) -> postura adequada
        if (Math.abs(z) > Math.abs(x) && Math.abs(z) > Math.abs(y)) {
            // Celular na vertical (postura adequada)
            isPostureGood.value = true
        } else {
            // Celular na horizontal (postura inadequada)
            isPostureGood.value = false
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Não é necessário para o nosso caso
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MonitoramentoPosturalTheme {
        Text(text = "Monitoramento Postural")
    }
}

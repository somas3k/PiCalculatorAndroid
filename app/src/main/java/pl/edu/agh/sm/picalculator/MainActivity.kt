package pl.edu.agh.sm.picalculator

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.AsyncTask
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.edu.agh.sm.picalculator.LogicService.LocalBinder
import pl.edu.agh.sm.picalculator.R.id.*


class MainActivity : AppCompatActivity() {
    private var number1: EditText? = null
    private var number2: EditText? = null
    private var result: EditText? = null
    private var progressBar: ProgressBar? = null

    var logicService: LogicService? = null
    var mBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        number1 = findViewById(inputNumber1)
        number2 = findViewById(inputNumber2)
        result = findViewById(resultOutput)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun performOperation(operation: Operation) {
        if (mBound) {
            val inputs = getInputs()
            if (inputs == null) {
                Toast.makeText(
                    this@MainActivity, "Wrong Inputs",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val resultText = when (operation) {
                    Operation.ADD -> logicService!!.add(inputs.first, inputs.second)
                    Operation.SUBTRACT -> logicService!!.subtract(inputs.first, inputs.second)
                    Operation.MULTIPLY -> logicService!!.multiply(inputs.first, inputs.second)
                    Operation.DIVIDE -> logicService!!.divide(inputs.first, inputs.second)
                }
                result?.setText(resultText)
            }
        }
    }

    private fun getInputs(): Pair<Double, Double>? {
        val num1: String = number1!!.text.toString()
        val num2: String = number2!!.text.toString()

        if (num1 == "" || num2 == "") {
            return null
        }
        return try {
            Pair(num1.toDouble(), num2.toDouble())
        } catch (e: NumberFormatException) {
            null
        }
    }

    private val logicConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as LocalBinder
            logicService = binder.service
            mBound = true
            Toast.makeText(
                this@MainActivity, "Logic Service Connected!",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onServiceDisconnected(className: ComponentName) {
            logicService = null
            mBound = false
            Toast.makeText(
                this@MainActivity, "Logic Service Disconnected!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (!mBound) {
            this.bindService(
                Intent(this@MainActivity, LogicService::class.java),
                logicConnection, Context.BIND_AUTO_CREATE
            )
        }
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            mBound = false
            unbindService(logicConnection)
        }
    }

    enum class Operation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

    fun onClickPlus(v: View) {
        performOperation(Operation.ADD)
    }

    fun onClickMinus(v: View) {
        performOperation(Operation.SUBTRACT)
    }

    fun onClickStar(v: View) {
        performOperation(Operation.MULTIPLY)
    }

    fun onClickSlash(v: View) {
        performOperation(Operation.DIVIDE)
    }

    fun onClickPiCalculation(v: View) {
        PiComputeTask().execute()
    }

    inner class PiComputeTask : AsyncTask<Void, Int, Double>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressBar!!.visibility = View.VISIBLE
            progressBar!!.progress = 0
        }

        override fun doInBackground(vararg params: Void?): Double {
            val n = 1_000_000
            var x: Double
            var y: Double
            var k = 0
            for (i in 1..n) {
                if (i % 1000 == 0) {
                    publishProgress((i * 100 / n))
                }
                x = Math.random()
                y = Math.random()
                if (x * x + y * y <= 1) k++
            }
            return 4.0 * k / n
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            progressBar!!.progress = values[0] as Int
        }

        override fun onPostExecute(result: Double?) {
            super.onPostExecute(result)
            number1?.setText(result.toString())
        }
    }
}

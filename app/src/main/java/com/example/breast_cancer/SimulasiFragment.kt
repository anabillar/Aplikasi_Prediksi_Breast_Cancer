package com.example.breast_cancer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.AssetManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel.MapMode.READ_ONLY

class SimulasiFragment : Fragment() {
    private lateinit var interpreter: Interpreter
    private val mModelPath = "breast.tflite"

    private lateinit var predictionResult: TextView
    private lateinit var radiusMean: EditText
    private lateinit var textureMean: EditText
    private lateinit var perimeterMean: EditText
    private lateinit var areaMean: EditText
    private lateinit var smoothnessMean: EditText
    private lateinit var compactnessMean: EditText
    private lateinit var concavityMean: EditText
    private lateinit var areaSE: EditText
    private lateinit var smoothnessSE: EditText
    private lateinit var predictButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_simulasi, container, false)

        predictionResult = view.findViewById(R.id.predictionResult)
        radiusMean = view.findViewById(R.id.radiusMean)
        textureMean = view.findViewById(R.id.textureMean)
        perimeterMean = view.findViewById(R.id.perimeterMean)
        areaMean = view.findViewById(R.id.areaMean)
        smoothnessMean = view.findViewById(R.id.smoothnessMean)
        compactnessMean = view.findViewById(R.id.compactnessMean)
        concavityMean = view.findViewById(R.id.concavityMean)
        areaSE = view.findViewById(R.id.areaSE)
        smoothnessSE = view.findViewById(R.id.smoothnessSE)
        predictButton = view.findViewById(R.id.predictButton)

        predictButton.setOnClickListener {
            try {
                val result = doInference(
                    radiusMean.text.toString(),
                    textureMean.text.toString(),
                    perimeterMean.text.toString(),
                    areaMean.text.toString(),
                    smoothnessMean.text.toString(),
                    compactnessMean.text.toString(),
                    concavityMean.text.toString(),
                    areaSE.text.toString(),
                    smoothnessSE.text.toString()
                )
                activity?.runOnUiThread {
                    val resultText = when (result) {
                        0 -> "Benign"
                        else -> "Malignant"
                    }
                    predictionResult.text = resultText
                    showPredictionDialog(resultText)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Column cannot be blank :)", Toast.LENGTH_SHORT).show()
            }
        }
        initInterpreter()

        return view
    }

    private fun initInterpreter() {
        val options = Interpreter.Options()
        options.setNumThreads(5)
        options.setUseNNAPI(true)
        interpreter = Interpreter(loadModelFile(requireContext().assets, mModelPath), options)
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(READ_ONLY, startOffset, declaredLength)
    }

    private fun doInference(vararg inputs: String): Int {
        val inputVal = FloatArray(inputs.size)
        for (i in inputs.indices) {
            inputVal[i] = inputs[i].toFloat()
        }
        val output = Array(1) { FloatArray(1) }
        interpreter.run(inputVal, output)

        Log.e("result", output[0].contentToString())

        return if (output[0][0] > 0.5) 1 else 0
    }

    private fun showPredictionDialog(result: String) {
        val builder = AlertDialog.Builder(requireContext())

        val dialogView = layoutInflater.inflate(R.layout.dialog_prediction, null)
        val resultTextView: TextView = dialogView.findViewById(R.id.resultTextView)
        resultTextView.text = result

        builder.setView(dialogView)
        builder.setPositiveButton("Close", null)
        builder.create().show()
    }

}

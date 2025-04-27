package com.example.matrixcalculator20

import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import android.widget.GridLayout
import android.graphics.Color
import androidx.core.graphics.toColorInt

class MainActivity : AppCompatActivity() {

    external fun calculateMatrix(
        operation: Int,
        matA: DoubleArray,
        rowsA: Int,
        colsA: Int,
        matB: DoubleArray,
        rowsB: Int,
        colsB: Int
    ): String

    private lateinit var gridMatrixA: GridLayout
    private lateinit var gridMatrixB: GridLayout
    private lateinit var resultMatrixGrid: GridLayout

    private var matrixAInputs = mutableListOf<EditText>()
    private var matrixBInputs = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rowsAInput = findViewById<EditText>(R.id.rowsAInput)
        val colsAInput = findViewById<EditText>(R.id.colsAInput)
        val rowsBInput = findViewById<EditText>(R.id.rowsBInput)
        val colsBInput = findViewById<EditText>(R.id.colsBInput)

        val generateAButton = findViewById<Button>(R.id.generateMatrixAButton)
        val generateBButton = findViewById<Button>(R.id.generateMatrixBButton)

        val operationSpinner = findViewById<Spinner>(R.id.operationSpinner)
        val calculateButton = findViewById<Button>(R.id.calculateButton)

        gridMatrixA = findViewById(R.id.matrixAGrid)
        gridMatrixB = findViewById(R.id.matrixBGrid)
        resultMatrixGrid = findViewById(R.id.resultGrid)

        val operations = arrayOf("Add", "Subtract", "Multiply", "Divide")
        operationSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, operations)

        generateAButton.setOnClickListener {
            val rows = rowsAInput.text.toString().toIntOrNull()
            val cols = colsAInput.text.toString().toIntOrNull()
            if (rows != null && cols != null && rows > 0 && cols > 0) {
                generateMatrixGrid(gridMatrixA, rows, cols, matrixAInputs)
            } else {
                Toast.makeText(this, "Enter valid rows and columns for Matrix A", Toast.LENGTH_SHORT).show()
            }
        }

        generateBButton.setOnClickListener {
            val rows = rowsBInput.text.toString().toIntOrNull()
            val cols = colsBInput.text.toString().toIntOrNull()
            if (rows != null && cols != null && rows > 0 && cols > 0) {
                generateMatrixGrid(gridMatrixB, rows, cols, matrixBInputs)
            } else {
                Toast.makeText(this, "Enter valid rows and columns for Matrix B", Toast.LENGTH_SHORT).show()
            }
        }

        calculateButton.setOnClickListener {
            try {
                val rowsA = rowsAInput.text.toString().toInt()
                val colsA = colsAInput.text.toString().toInt()
                val rowsB = rowsBInput.text.toString().toInt()
                val colsB = colsBInput.text.toString().toInt()
                val operation = operationSpinner.selectedItemPosition

                val matA = getMatrixValues(matrixAInputs)
                val matB = getMatrixValues(matrixBInputs)

                when (operation) {
                    0, 1, 3 -> {
                        if (rowsA != rowsB || colsA != colsB) {
                            Toast.makeText(this, "Rows and columns must match for Add/Subtract/Divide", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                    }
                    2 -> {
                        if (colsA != rowsB) {
                            Toast.makeText(this, "Columns of A must equal Rows of B for Multiply", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                    }
                }

                val resultString = calculateMatrix(operation, matA, rowsA, colsA, matB, rowsB, colsB)
                showResultMatrix(resultString)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun generateMatrixGrid(gridLayout: GridLayout, rows: Int, cols: Int, inputs: MutableList<EditText>) {
        gridLayout.removeAllViews()
        inputs.clear()
        gridLayout.rowCount = rows
        gridLayout.columnCount = cols

        val density = resources.displayMetrics.density
        val cellSize = (80 * density).toInt()
        val marginSize = (4 * density).toInt()

        for (i in 0 until rows * cols) {
            val editText = EditText(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = cellSize
                    height = cellSize
                    setMargins(marginSize, marginSize, marginSize, marginSize)
                }
                gravity = Gravity.CENTER
                textSize = 18f
                setPadding(8)
                setBackgroundResource(android.R.drawable.edit_text)
                setHintTextColor(Color.GRAY)
                setTextColor(Color.BLACK)
                inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                        android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
            }
            inputs.add(editText)
            gridLayout.addView(editText)
        }
    }

    private fun getMatrixValues(inputs: List<EditText>): DoubleArray {
        return inputs.map { it.text.toString().toDoubleOrNull() ?: 0.0 }.toDoubleArray()
    }

    private fun showResultMatrix(resultString: String) {
        resultMatrixGrid.removeAllViews()

        // Ensure the string is properly formatted by replacing any commas and trimming extra spaces.
        val cleanedResultString = resultString.replace(",", "").trim()

        // Split the result string into rows based on newlines
        val lines = cleanedResultString.split("\n")
        val rows = lines.size
        val cols = lines[0].split("\\s+".toRegex()).size  // Split the first line to determine column count

        resultMatrixGrid.rowCount = rows
        resultMatrixGrid.columnCount = cols

        val density = resources.displayMetrics.density
        val cellSize = (80 * density).toInt()
        val margin = (4 * density).toInt()

        for (line in lines) {
            // Split each line by spaces to get individual numbers
            val numbers = line.trim().split("\\s+".toRegex())  // Split by any whitespace.
            for (num in numbers) {
                val textView = TextView(this).apply {
                    text = num  // Each number is displayed separately.
                    textSize = 18f
                    gravity = Gravity.CENTER
                    setPadding(8)
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = cellSize
                        height = cellSize
                        setMargins(margin, margin, margin, margin)
                    }
                    setBackgroundColor("#D3E3FC".toColorInt())
                    setTextColor(Color.BLACK)
                }
                resultMatrixGrid.addView(textView)
            }
        }
    }


    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
}

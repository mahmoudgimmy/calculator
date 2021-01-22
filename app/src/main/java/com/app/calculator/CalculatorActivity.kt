package com.app.calculator

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.calculator.databinding.ActivityCalculatorBinding
import com.google.android.material.chip.Chip
import java.util.*

class CalculatorActivity : AppCompatActivity(), OperationEvents {
    private lateinit var binding: ActivityCalculatorBinding
    private val calculatorViewModel: CalculatorViewModel by viewModels()
    private lateinit var operationHistoryAdapter: OperationHistoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeVariables()
        initializeClickListeners()
        initializeObservers()
        prepareRecycleView()

    }

    private fun initializeVariables() {
        operationHistoryAdapter = OperationHistoryAdapter(this)
    }

    private fun prepareRecycleView() {
        binding.rvOperationHistory.apply {
            layoutManager =
                GridLayoutManager(
                    this@CalculatorActivity,
                    Utility.calculateNoOfColumns(this@CalculatorActivity),
                    LinearLayoutManager.VERTICAL,
                    false
                )
            adapter = operationHistoryAdapter
        }
    }

    private fun initializeObservers() {
        calculatorViewModel.calculatorViewStateLiveData.observe(this, {
            render(it)
        })
    }

    private fun render(viewState: CalculatorViewState) {

        when (viewState) {
            is CalculatorViewState.Payload -> {
                binding.apply {

                    tvUndoOperand.isEnabled = viewState.isUndoEnabled
                    tvRedoOperand.isEnabled = viewState.isRedoEnabled

                    etSecondOperand.text.clear()
                    operationsChipGroup.clearCheck()
                    (getString(R.string.result) + " " + viewState.result).also {
                        tvResult.text = it
                    }
                    operationHistoryAdapter.submitList(viewState.operationHistory)

                }
            }
        }


    }

    private fun initializeClickListeners() {
        binding.apply {

            tvUndoOperand.setOnClickListener {
                calculatorViewModel.undo()
            }
            tvRedoOperand.setOnClickListener {
                calculatorViewModel.redo()
            }

            tvEqualOperand.setOnClickListener {
                val secondOperand = etSecondOperand.text.toString()

                if (secondOperand.isEmpty() || secondOperand.isBlank()) {
                    Toast.makeText(
                        this@CalculatorActivity,
                        getString(R.string.message_missing_second_operand), Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                // returns selected chip id which represent current selected operation
                val selectedChipId = binding.operationsChipGroup.checkedChipId

                // no selected operation
                if (selectedChipId == View.NO_ID) {
                    Toast.makeText(
                        this@CalculatorActivity,
                        getString(R.string.message_missing_operation), Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                // operation is name of chip
                val operation = operationsChipGroup.findViewById<Chip>(selectedChipId).text[0]
                calculatorViewModel.calculateResult(operation, secondOperand.toDouble())

            }
        }
    }

    override fun clickedForUndo(index: Int) {
        calculatorViewModel.undo(index)
    }
}
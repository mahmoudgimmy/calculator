package com.app.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList

class CalculatorViewModel : ViewModel() {

    private var result = 0.0
    // main idea of using stack in redo and undo operations to preserve last operation and apply it to current result
    private val undoStack = Stack<Pair<Char, Double>>() // Pair of operation and second operand
    private val redoStack = Stack<Pair<Char, Double>>()

    private val calculatorStateMutableLiveData = MutableLiveData<CalculatorViewState>()
    val calculatorViewStateLiveData: LiveData<CalculatorViewState> = calculatorStateMutableLiveData


    fun calculateResult(
        operation: Char,
        operand: Double,
        fromUser: Boolean = true
    ) {

        when (operation) {
            '+' -> result += operand
            '-' -> result -= operand
            '*' -> result *= operand
            '/' -> result /= operand
        }

        // push to undoStack in case it's from user input
        // in not from user then it's undo or redo operation so no need to add it to stack again
        if (fromUser)
            undoStack.push(Pair(operation, operand))

        val operationsHistory = ArrayList<Pair<Char, Double>>()

        // undoStack represent operationsHistory list that will shown to user
        undoStack.map {
            operationsHistory.add(it)
        }

        calculatorStateMutableLiveData.value = CalculatorViewState.Payload(
            result,
            // map each pair of operation and second operand to one string
            // ex:(+2, -3 , *5, /3)
            operationsHistory.map { it.first + it.second.toString() },
            isUndoEnabled = undoStack.isNotEmpty(),
            isRedoEnabled = redoStack.isNotEmpty()
        )
    }

    // a function to inverse operation before add it to stack (undo, redo)
    private fun inverseOperation(operation: Char): Char {
        return when (operation) {
            '+' -> '-'
            '-' -> '+'
            '*' -> '/'
            '/' -> '*'
            else -> '+'
        }
    }

    fun undo(index: Int? = null) {

        // if stack is empty this mean no undo operations are available
        if (undoStack.isNotEmpty()) {

            // in case there is index (clicked operation) then get operation by index from stack and then remove it
            var operationHistory = if (index == null) undoStack.pop() else {
                undoStack.elementAt(index)
                undoStack.removeAt(index)
            }

            // get copy of operationHistory and reverse operation before add it into redoStack
            operationHistory =
                operationHistory.copy(first = inverseOperation(operationHistory.first))

            redoStack.push(operationHistory)

            // then calculate result from inverted operation returned from undo stack
            calculateResult(
                operationHistory.first,
                operationHistory.second,
                fromUser = false
            )
        }

    }

    fun redo() {

        // if stack is empty this mean no redo operations are available
        if (redoStack.isNotEmpty()) {

            var operationHistory = redoStack.pop()
            // get copy of operationHistory and reverse operation before add it to undoStack
            operationHistory =
                operationHistory.copy(first = inverseOperation(operationHistory.first))
            undoStack.push(operationHistory)
            // then calculate result from inverted operation returned from redo stack
            calculateResult(
                operationHistory.first,
                operationHistory.second,
                fromUser = false
            )
        }
    }

    // initial values for calculator views
    init {
        calculatorStateMutableLiveData.value = CalculatorViewState.Payload(
            0.0,
            emptyList(),
            isUndoEnabled = false,
            isRedoEnabled = false
        )
    }
}
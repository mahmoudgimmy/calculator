package com.app.calculator

sealed class CalculatorViewState{
    class Payload(val result: Double, val operationHistory:List<String>,
                  val isUndoEnabled:Boolean, val isRedoEnabled:Boolean):CalculatorViewState()
}

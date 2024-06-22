package com.example.finance_expense_tracker

import java.util.Stack

fun EvaluateExpression(expression: String): Double {
    val tokens = expression.toCharArray()
    val values: Stack<Double> = Stack()
    val operators: Stack<Char> = Stack()

    var i = 0
    while (i < tokens.size) {
        if (tokens[i] == ' ') {
            i++
            continue
        }
        if (tokens[i] in '0'..'9' || tokens[i] == '.') {
            val sbuf = StringBuilder()
            while (i < tokens.size && (tokens[i] in '0'..'9' || tokens[i] == '.')) sbuf.append(tokens[i++])
            values.push(sbuf.toString().toDouble())
            i--
        } else if (tokens[i] == '(') {
            operators.push(tokens[i])
        } else if (tokens[i] == ')') {
            while (operators.peek() != '(') values.push(applyOp(operators.pop(), values.pop(), values.pop()))
            operators.pop()
        } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {
            while (!operators.isEmpty() && hasPrecedence(tokens[i], operators.peek()))
                values.push(applyOp(operators.pop(), values.pop(), values.pop()))
            operators.push(tokens[i])
        }
        i++
    }
    while (!operators.isEmpty()) values.push(applyOp(operators.pop(), values.pop(), values.pop()))

    return values.pop()
}

fun hasPrecedence(op1: Char, op2: Char): Boolean {
    if (op2 == '(' || op2 == ')') return false
    if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false
    return true
}

fun applyOp(op: Char, b: Double, a: Double): Double {
    return when (op) {
        '+' -> a + b
        '-' -> a - b
        '*' -> a * b
        '/' -> {
            if (b == 0.0) throw UnsupportedOperationException("Cannot divide by zero")
            a / b
        }
        else -> 0.0
    }
}
package lox

class Interpreter : Expr.Visitor<Any?> {

    fun interpret(expression: Expr?) {
        try {
            val value = evaluate(expression!!)
            println(stringify(value))
        } catch (error: RuntimeError) {
            Lox.runtimeError(error)
        }
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.MINUS -> visitBinaryExprMinus(expr, left, right)
            TokenType.SLASH -> visitBinaryExprSlash(expr, left, right)
            TokenType.STAR -> visitBinaryExprStar(expr, left, right)
            TokenType.PLUS -> visitBinaryExprPlus(expr, left, right)

            TokenType.GREATER -> visitBinaryExprGreater(expr, left, right)
            TokenType.GREATER_EQUAL -> visitBinaryExprGreaterEqual(expr, left, right)
            TokenType.LESS -> visitBinaryExprLess(expr, right, left)
            TokenType.LESS_EQUAL -> visitBinaryExprLessEqual(expr, right, left)

            TokenType.BANG_EQUAL -> !isEqual(left, right)
            TokenType.EQUAL_EQUAL -> isEqual(left, right)

            else -> null
        }
    }

    private fun visitBinaryExprMinus(
        expr: Expr.Binary,
        left: Any?,
        right: Any?,
    ): Any {
        checkNumberOperands(expr.operator, left, right)
        return (left as Double) - (right as Double)
    }

    private fun visitBinaryExprSlash(
        expr: Expr.Binary,
        left: Any?,
        right: Any?,
    ): Any {
        checkNumberOperands(expr.operator, left, right)
        return (left as Double) / (right as Double)
    }

    private fun visitBinaryExprStar(
        expr: Expr.Binary,
        left: Any?,
        right: Any?,
    ): Any {
        checkNumberOperands(expr.operator, left, right)
        return (left as Double) * (right as Double)
    }

    private fun visitBinaryExprPlus(
        expr: Expr.Binary,
        left: Any?,
        right: Any?,
    ): Any? {
        checkNumberOperands(expr.operator, left, right)
        return if (left is Double && right is Double) {
            left + right
        } else if (left is String && right is String) {
            left + right
        } else null
    }

    private fun visitBinaryExprGreater(
        expr: Expr.Binary,
        left: Any?,
        right: Any?,
    ): Any {
        checkNumberOperands(expr.operator, left, right)
        return (left as Double) > (right as Double)
    }

    private fun visitBinaryExprGreaterEqual(
        expr: Expr.Binary,
        left: Any?,
        right: Any?,
    ): Any {
        checkNumberOperands(expr.operator, left, right)
        return (left as Double) >= (right as Double)
    }

    private fun visitBinaryExprLess(
        expr: Expr.Binary,
        left: Any?,
        right: Any?,
    ): Any {
        checkNumberOperands(expr.operator, left, right)
        return (left as Double) < (right as Double)
    }

    private fun visitBinaryExprLessEqual(
        expr: Expr.Binary,
        left: Any?,
        right: Any?,
    ): Any {
        checkNumberOperands(expr.operator, left, right)
        return (left as Double) <= (right as Double)
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.BANG -> !isTruthy(right)
            TokenType.MINUS -> -1 * (right as Double)
            else -> null
        }
    }

    private fun isTruthy(`object`: Any?): Boolean {
        if (`object` == null) return false
        return if (`object` is Boolean) `object` else true
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) return true
        return if (a == null) false else a == b
    }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }

    private fun checkNumberOperands(
        operator: Token,
        left: Any?,
        right: Any?,
    ) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operands must be numbers.")
    }

    private fun stringify(`object`: Any?): String? {
        if (`object` == null) return "nil"
        if (`object` is Double) {
            var text = `object`.toString()
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length - 2)
            }
            return text
        }
        return `object`.toString()
    }

}

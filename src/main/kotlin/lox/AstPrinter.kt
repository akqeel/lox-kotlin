package lox

import lox.Expr.Visitor

class AstPrinter: Visitor<String> {

    fun print(expr: Expr) = expr.accept(this)

    override fun visitBinaryExpr(expr: Expr.Binary?): String {
        TODO("Not yet implemented")
    }

    override fun visitGroupingExpr(expr: Expr.Grouping?): String {
        TODO("Not yet implemented")
    }

    override fun visitLiteralExpr(expr: Expr.Literal?): String {
        TODO("Not yet implemented")
    }

    override fun visitUnaryExpr(expr: Expr.Unary?): String {
        TODO("Not yet implemented")
    }
}
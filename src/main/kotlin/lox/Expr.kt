package lox

abstract class Expr

internal class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr()

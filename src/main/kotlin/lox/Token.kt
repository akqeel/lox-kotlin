package lox

data class Token(
    var type: TokenType? = null,
    var lexeme: String? = null,
    var literal: Any? = null,
    var line: Int? = null,
) {
    override fun toString(): String {
        return type.toString() + " " + lexeme + " " + literal
    }

}

import Lox.Companion.error
import TokenType.*


data class Scanner(
    private val source: String,
) {

    companion object {
        @JvmStatic val keywords: Map<String, TokenType> = HashMap<String, TokenType>().apply {
            put("and", AND)
            put("class", CLASS)
            put("else", ELSE)
            put("false", FALSE)
            put("for", FOR)
            put("fun", FUN)
            put("if", IF)
            put("nil", NIL)
            put("or", OR)
            put("print", PRINT)
            put("return", RETURN)
            put("super", SUPER)
            put("this", THIS)
            put("true", TRUE)
            put("var", VAR)
            put("while", WHILE)
        }
    }

    private val tokens = arrayListOf<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current
            scanToken()
        }

        return tokens.apply {
            add(Token(EOF, "", null, line))
        }
    }

    private fun isAtEnd() = (current >= source.length)

    private fun scanToken() {
        val c = advance()
        when (c) {
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)

            '!' -> addToken(if (match('=')) BANG_EQUAL else BANG)
            '=' -> addToken(if (match('=')) EQUAL_EQUAL else EQUAL)
            '<' -> addToken(if (match('=')) LESS_EQUAL else LESS)
            '>' -> addToken(if (match('=')) GREATER_EQUAL else GREATER)
            '/' -> scanTokenForwardSlash()

            ' ', '\r', '\t' -> {} // ignore
            '\n' -> scanTokenNewLine()

            '"' -> scanTokenString()

            else -> scanTokenDefault(c)
        }
    }

    private fun scanTokenForwardSlash() = if (match('/')) {
        while (peek() != '\n' && !isAtEnd()) advance() // A comment goes until the end of the line.
    } else {
        addToken(SLASH)
    }

    private fun scanTokenNewLine() {
        line++
    }

    private fun scanTokenString() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }

        if (isAtEnd()) {
            error(line, "Unterminated string.")
            return
        }

        // The closing ".
        advance()

        // Trim the surrounding quotes.
        val value = source.substring(start + 1, current - 1)
        addToken(STRING, value)
    }

    private fun scanTokenDefault(c: Char) = if (isDigit(c)) {
        scanTokenDigit(c)
    } else if(isAlpha(c)) {
        scanTokenIdentifier(c)
    } else {
        error(line, "Unexpected character.")
    }

    private fun scanTokenDigit(c: Char) {
        while (isDigit(peek())) advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER, source.substring(start, current).toDouble());
    }

    private fun scanTokenIdentifier(c: Char) {
        while (isAlphaNumeric(peek())) advance()

        val text = source.substring(start, current)
        var type = keywords[text]
        if (type == null) type = IDENTIFIER
        addToken(IDENTIFIER);
    }

    private fun advance() = source[current++]
    private fun addToken(
        type: TokenType,
        literal: Any? = null,
    ) = addToken(
        type,
        text = source.substring(start, current),
        literal = literal,
    )

    private fun addToken(
        type: TokenType,
        text: String?,
        literal: Any?,
    ) {
        tokens.add(Token(type, text, literal, line))
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (!isSourceAtCurrentEquals(expected)) return false
        current++
        return true
    }

    private fun isSourceAtCurrentEquals(expected: Char) =
        source[current] == expected

    private fun isAlphaNumeric(c: Char) = isDigit(c) || isAlpha(c)

    private fun isDigit(c: Char) = c in '0'..'9'

    private fun isAlpha(c: Char) = (c in 'a'..'z') || (c in 'A'..'Z') || c == '_';

    private fun peek() = if (isAtEnd()) '\u0000' else source[current]

    private fun peekNext(): Char {
        return if (current + 1 >= source.length) '\u0000' else source[current + 1]
    }

}

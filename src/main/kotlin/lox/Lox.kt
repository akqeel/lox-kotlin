package lox

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import kotlin.system.exitProcess


class Lox {

    companion object {
        @JvmStatic private var hadError = false
        @JvmStatic private var hadRuntimeError = false

        @JvmStatic fun main(args: Array<String>) {
            if (args.size > 1) {
                println("Usage: jlox [script]");
                exitProcess(64);
            } else if (args.size == 1) {
                runFile(args[0]);
            } else {
                runPrompt();
            }
        }

        @JvmStatic
        @Throws(IOException::class)
        private fun runFile(path: String) {
            val bytes: ByteArray = Files.readAllBytes(Paths.get(path))
            run(String(bytes, Charset.defaultCharset()))
        }

        @JvmStatic
        @Throws(IOException::class)
        private fun runPrompt() {
            val input = InputStreamReader(System.`in`)
            val reader = BufferedReader(input)
            while (true) {
                print("> ")
                val line = reader.readLine() ?: break
                run(line)
                hadError = false
            }
        }

        @JvmStatic
        private fun run(source: String) {
            val scanner = Scanner(source)
            val tokens: List<Token> = scanner.scanTokens()
            val parser = Parser(tokens)
            val expr = parser.parse() ?: return

            if(hadError) return

            println(Interpreter().interpret(expr))
        }

        @JvmStatic
        fun error(
            token: Token,
            message: String,
        ) = report(token.line, "", message)

        @JvmStatic
        fun error(
            line: Int,
            message: String,
        ) = report(line, "", message)

        @JvmStatic
        fun report(
            line: Int,
            where: String,
            message: String
        ) {
            println("[line $line] Error$where: $message")
            hadError = true
        }

        @JvmStatic
        fun runtimeError(error: RuntimeError) {
            System.err.println("""${error.message}[line ${error.token.line}]""".trimIndent())
            hadRuntimeError = true
        }

    }

}

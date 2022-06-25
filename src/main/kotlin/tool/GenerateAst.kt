package tool

import java.io.IOException
import java.io.PrintWriter
import java.util.*
import kotlin.system.exitProcess


class GenerateAst {

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            if (args.size !== 1) {
                System.err.println("Usage: generate_ast <output directory>")
                exitProcess(64)
            }
            val outputDir = args[0]
            defineAst(outputDir, "Expr", arrayListOf(
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : @Nullable Object value",
                "Unary    : Token operator, Expr right"
            ))
        }

        @Throws(IOException::class)
        @JvmStatic private fun defineAst(outputDir: String, baseName: String, types: List<String>) {
            val path = "$outputDir/$baseName.java"
            val writer = PrintWriter(path, "UTF-8")
            writer.println("package lox;")
            writer.println()
            writer.println("import java.util.List;")
            writer.println("import lox.Token;")
            writer.println()
            writer.println("public abstract class $baseName {")
            defineVisitor(writer, baseName, types)
            defineAstClasses(baseName, types, writer)
            writer.println();
            writer.println("  abstract <R> R accept(Visitor<R> visitor);"); // The base accept() method.
            writer.println("}")
            writer.close()
        }

        @JvmStatic
        private fun defineVisitor(
            writer: PrintWriter,
            baseName: String,
            types: List<String>,
        ) {
            writer.println("  interface Visitor<R> {")
            for (type: String in types) {
                val typeName =
                    type.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].trim { it <= ' ' }
                writer.println("    public R visit" + typeName + baseName + "(" + typeName + " " + baseName.lowercase(Locale.getDefault()) + ");")
            }
            writer.println("  }")
        }

        @JvmStatic private fun defineAstClasses(
            baseName: String,
            types: List<String>,
            writer: PrintWriter,
        ) = types.forEach { type ->
            val className: String = type.split(":")[0].trim()
            val fields: String = type.split(":")[1].trim()
            defineType(writer, baseName, className, fields)
        }

        @JvmStatic private fun defineType(
            writer: PrintWriter,
            baseName: String,
            className: String,
            fieldList: String,
        ) {
            writer.println("  static class $className extends $baseName {")

            // Constructor.
            writer.println("    $className($fieldList) {")

            // Store parameters in fields.
            val fields = fieldList.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (field: String in fields) {
                val name = field.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                writer.println("      this.$name = $name;")
            }
            writer.println("    }")

            // Visitor pattern.
            writer.println();
            writer.println("    @Override");
            writer.println("    <R> R accept(Visitor<R> visitor) {")
            writer.println("      return visitor.visit" + className + baseName + "(this);")
            writer.println("    }")

            // Fields.
            writer.println()
            for (field: String in fields) {
                writer.println("    final $field;")
            }

            writer.println("  }")
        }

    }

}
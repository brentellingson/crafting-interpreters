package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {
    private static Object evaluate(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        Expr expression = parser.expression();

        Interpreter interpreter = new Interpreter(System.out);
        Object result = interpreter.evaluate(expression);

        return result;
    }

    private static String execute(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        Stmt statement = parser.statement();


        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
            Interpreter interpreter = new Interpreter(ps);
            interpreter.execute(statement);
        }

        return baos.toString(StandardCharsets.UTF_8);
    }

    private static String intepret(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
            Interpreter interpreter = new Interpreter(ps);
            interpreter.interpret(statements);
        }

        return baos.toString(StandardCharsets.UTF_8);
    }

    @Test
    void evaluateDoubleExpression() {
        assertAll(
                () -> assertEquals(5.0, evaluate("3 + 2")),
                () -> assertEquals(6.0, evaluate("3 * 2")),
                () -> assertEquals(1.0, evaluate("3 - 2")),
                () -> assertEquals(1.5, evaluate("3 / 2")),
                () -> assertEquals(5.0, evaluate("3 - -2"))
        );
    }

    @Test
    void evaluateStringExpression() {
        assertEquals("foobar", evaluate("\"foo\" + \"bar\""));
    }

    @Test
    void evaluateBoolExpression() {
        assertAll(
                () -> assertEquals(true, evaluate("3 > 2")),
                () -> assertEquals(true, evaluate("3 >= 2")),
                () -> assertEquals(false, evaluate("3 < 2")),
                () -> assertEquals(false, evaluate("3 <= 2")),
                () -> assertEquals(false, evaluate("3 == 2")),
                () -> assertEquals(true, evaluate("3 != 2")),
                () -> assertEquals(false, evaluate("!3"))
        );
    }

    @Test
    void evaluateGroupExpression() {
        assertAll(
                () -> assertEquals(20.0, evaluate("(2 + 3) * 4")),
                () -> assertEquals(true, evaluate("(2 < 3) == true"))
        );
    }

    @Test
    void executePrint() {
        assertEquals("3\r\n", execute("print 1 + 2;"));
    }

    @Test
    void interpret() {
        assertEquals("3\r\ntrue\r\n", intepret("print 1 + 2; print 1 + 2 == 3;"));
    }
}

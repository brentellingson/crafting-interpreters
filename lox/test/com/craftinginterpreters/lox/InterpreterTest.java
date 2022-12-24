package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private static String interpret(String source) {
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

    private static void testInterpret(String source, String... expected) {
        List<String> actual = Arrays.stream(interpret(source).split("\\r?\\n")).map(s -> s.trim()).collect(Collectors.toList());
        assertIterableEquals(Arrays.asList(expected), actual);
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
    void interpretPrint() {
        assertAll(
                () -> testInterpret("print 1 + 2;", "3"),
                () -> testInterpret("print 1 + 2; print 1 + 2 == 3;", "3", "true")
        );
    }

    @Test
    void interpretVar() {
        assertAll(
                () -> testInterpret("var foo = 1; print foo;", "1"),
                () -> testInterpret("var foo = 1; print foo + 2;", "3"),
                () -> testInterpret("var foo = 1; var bar = 2 + foo; print bar + 3;", "6")
        );
    }

    @Test
    void interpretAssign() {
        assertAll(
                () -> testInterpret("var foo = 1; foo = 2; print foo;", "2"),
                () -> testInterpret("var foo = 1; foo = foo + 2; print foo;", "3"),
                () -> testInterpret("var foo = 1; var bar = 2; foo = (bar = 3) + 4; print foo; print bar;", "7", "3"),
                () -> testInterpret("var foo = \"apple\"; foo = \"banana\"; print foo;", "banana"),
                () -> testInterpret("var foo = \"apple\"; foo = foo + \"banana\"; print foo;", "applebanana"),
                () -> testInterpret("var foo = \"apple\"; var bar = \"banana\"; foo = !(bar = false); print foo; print bar;", "true", "false")
        );
    }

    @Test
    void interpretScope() {
        assertAll(
                () -> testInterpret("var foo = 1; { foo = 2; } print foo;", "2"),
                () -> testInterpret("var foo = 1; var bar = 2; { var foo = 3; print foo; print bar; } print foo; print bar;", "3", "2", "1", "2")
        );
    }
}

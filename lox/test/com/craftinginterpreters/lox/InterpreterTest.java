package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {
    private static Object evaluate(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        Interpreter interpreter = new Interpreter();
        Object result = interpreter.evaluate(expression);

        return result;
    }

    @Test
    void evaluateDouble() {
        assertAll(
                () -> assertEquals(5.0, evaluate("3 + 2")),
                () -> assertEquals(6.0, evaluate("3 * 2")),
                () -> assertEquals(1.0, evaluate("3 - 2")),
                () -> assertEquals(1.5, evaluate("3 / 2")),
                () -> assertEquals(5.0, evaluate("3 - -2"))
        );
    }

    @Test
    void evaluateString() {
        assertEquals("foobar", evaluate("\"foo\" + \"bar\""));
    }

    @Test
    void evaluateBool() {
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
    void evaluateGroup() {
        assertAll(
                () -> assertEquals(20.0, evaluate("(2 + 3) * 4")),
                () -> assertEquals(true, evaluate("(2 < 3) == true"))
        );
    }
}

package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    private static String expression(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        Expr expression = parser.expression();

         return new AstPrinter().print(expression);
    }

    @Test
    void expressionEquality() {
        assertAll(
                () -> assertEquals("(!= (== 1.0 2.0) 3.0)", expression("1 == 2 != 3"))
        );
    }

    @Test
    void expressionComparison() {
        assertAll(
                () -> assertEquals("(>= (> (<= (< 1.0 2.0) 3.0) 4.0) 5.0)", expression("1 < 2 <= 3 > 4 >= 5"))
        );
    }

    @Test
    void expressionTerm() {
        assertAll(
                () -> assertEquals("(- (+ 1.0 2.0) 3.0)", expression("1 + 2 - 3"))
        );
    }

    @Test
    void expressionFactor() {
        assertAll(
                () -> assertEquals("(/ (* 1.0 2.0) 3.0)", expression("1 * 2 / 3"))
        );
    }

    @Test
    void expressionUnary() {
        assertAll(
                () -> assertEquals("(! (- 1.0))", expression("! - 1"))
        );
    }

    @Test
    void expressionPrimary() {
        assertAll(
                () -> assertEquals("true", expression("true")),
                () -> assertEquals("false", expression("false")),
                () -> assertEquals("nil", expression("nil")),
                () -> assertEquals("1.0", expression("1")),
                () -> assertEquals("hello", expression("\"hello\""))
        );
    }

    @Test
    void expressionPrecedence() {
        assertAll(
                () -> assertEquals("(== 1.0 (< 2.0 3.0))", expression("1 == 2 < 3")),
                () -> assertEquals("(== (< 1.0 2.0) 3.0)", expression("1 < 2 == 3")),
                () -> assertEquals("(> 1.0 (+ 2.0 3.0))", expression("1 > 2 + 3")),
                () -> assertEquals("(> (+ 1.0 2.0) 3.0)", expression("1 + 2 > 3")),
                () -> assertEquals("(+ 1.0 (* 2.0 3.0))", expression("1 + 2 * 3")),
                () -> assertEquals("(+ (* 1.0 2.0) 3.0)", expression("1 * 2 + 3")),
                () -> assertEquals("(* (- 1.0) 2.0)", expression("- 1 * 2")),
                () -> assertEquals("(* 1.0 (- 2.0))", expression("1 * - 2")),
                () -> assertEquals("(- (group 1.0))", expression("-(1)")),
                () -> assertEquals("(group (- 1.0))", expression("(-1)")),
                () -> assertEquals("(== 1.0 (< 2.0 (+ 3.0 (* 4.0 (- (group (+ 5.0 6.0)))))))", expression("1 == 2 < 3 + 4 * - (5 + 6)")),
                () -> assertEquals("(== (< (+ (* (- (group (+ 1.0 2.0))) 3.0) 4.0) 5.0) 6.0)", expression("-(1 + 2) * 3 + 4 < 5 == 6"))
        );
    }
}

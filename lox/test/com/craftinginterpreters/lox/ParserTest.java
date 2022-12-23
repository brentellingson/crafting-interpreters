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
    void expressionAssignment() {
        assertAll(
                () -> assertEquals("(= foo 1.0)", expression("foo = 1")),
                () -> assertEquals("(= foo (= bar 1.0))", expression("foo = bar = 1"))
        );
    }

    @Test
    void expressionEquality() {
        assertAll(
                () -> assertEquals("(!= (== 1.0 2.0) 3.0)", expression("1 == 2 != 3")),
                () -> assertEquals("(!= (== foo bar) baz)", expression("foo == bar != baz"))
        );
    }

    @Test
    void expressionComparison() {
        assertAll(
                () -> assertEquals("(>= (> (<= (< 1.0 2.0) 3.0) 4.0) 5.0)", expression("1 < 2 <= 3 > 4 >= 5")),
                () -> assertEquals("(>= (> (<= (< a b) c) 4.0) 5.0)", expression("a < b <= c > 4 >= 5"))
        );
    }

    @Test
    void expressionTerm() {
        assertAll(
                () -> assertEquals("(- (+ 1.0 2.0) 3.0)", expression("1 + 2 - 3")),
                () -> assertEquals("(- (+ a b) c)", expression("a + b - c"))
        );
    }

    @Test
    void expressionFactor() {
        assertAll(
                () -> assertEquals("(/ (* 1.0 2.0) 3.0)", expression("1 * 2 / 3")),
                () -> assertEquals("(/ (* x y) z)", expression("x * y / z"))
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
                () -> assertEquals("(= foo (== 1.0 2.0))", expression("foo = 1 == 2")),
                () -> assertEquals("(= foo (+ (group (= bar 1.0)) 2.0))", expression("foo = (bar = 1) + 2")),
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

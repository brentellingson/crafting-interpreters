package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    private static String parse(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

         return new AstPrinter().print(expression);
    }

    @Test
    void parseEquality() {
        assertAll(
                () -> assertEquals("(!= (== 1.0 2.0) 3.0)", parse("1 == 2 != 3"))
        );
    }

    @Test
    void parseComparison() {
        assertAll(
                () -> assertEquals("(>= (> (<= (< 1.0 2.0) 3.0) 4.0) 5.0)", parse("1 < 2 <= 3 > 4 >= 5"))
        );
    }

    @Test
    void parseTerm() {
        assertAll(
                () -> assertEquals("(- (+ 1.0 2.0) 3.0)", parse("1 + 2 - 3"))
        );
    }

    @Test
    void parseFactor() {
        assertAll(
                () -> assertEquals("(/ (* 1.0 2.0) 3.0)", parse("1 * 2 / 3"))
        );
    }

    @Test
    void parseUnary() {
        assertAll(
                () -> assertEquals("(! (- 1.0))", parse("! - 1"))
        );
    }

    @Test
    void parsePrimary() {
        assertAll(
                () -> assertEquals("true", parse("true")),
                () -> assertEquals("false", parse("false")),
                () -> assertEquals("nil", parse("nil")),
                () -> assertEquals("1.0", parse("1")),
                () -> assertEquals("hello", parse("\"hello\""))
        );
    }

    @Test
    void parsePrecedence() {
        assertAll(
                () -> assertEquals("(== 1.0 (< 2.0 3.0))", parse("1 == 2 < 3")),
                () -> assertEquals("(== (< 1.0 2.0) 3.0)", parse("1 < 2 == 3")),
                () -> assertEquals("(> 1.0 (+ 2.0 3.0))", parse("1 > 2 + 3")),
                () -> assertEquals("(> (+ 1.0 2.0) 3.0)", parse("1 + 2 > 3")),
                () -> assertEquals("(+ 1.0 (* 2.0 3.0))", parse("1 + 2 * 3")),
                () -> assertEquals("(+ (* 1.0 2.0) 3.0)", parse("1 * 2 + 3")),
                () -> assertEquals("(* (- 1.0) 2.0)", parse("- 1 * 2")),
                () -> assertEquals("(* 1.0 (- 2.0))", parse("1 * - 2")),
                () -> assertEquals("(- (group 1.0))", parse("-(1)")),
                () -> assertEquals("(group (- 1.0))", parse("(-1)")),
                () -> assertEquals("(== 1.0 (< 2.0 (+ 3.0 (* 4.0 (- (group (+ 5.0 6.0)))))))", parse("1 == 2 < 3 + 4 * - (5 + 6)")),
                () -> assertEquals("(== (< (+ (* (- (group (+ 1.0 2.0))) 3.0) 4.0) 5.0) 6.0)", parse("-(1 + 2) * 3 + 4 < 5 == 6"))
        );
    }
}

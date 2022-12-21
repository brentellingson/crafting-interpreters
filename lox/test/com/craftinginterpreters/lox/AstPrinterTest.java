package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AstPrinterTest {
    private static String print(Expr expr) {
        AstPrinter printer = new AstPrinter();
        return printer.print(expr);
    }

    @Test
    void print() {
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(123)
                ),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Binary(
                                new Expr.Literal(456),
                                new Token(TokenType.PLUS, "+", null, 1),
                                new Expr.Literal(789)
                        )));

        assertEquals("(* (- 123) (group (+ 456 789)))", print(expression));
    }
}

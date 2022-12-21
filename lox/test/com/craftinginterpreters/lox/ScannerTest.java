package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.craftinginterpreters.lox.TokenType.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class ScannerTest {
    private static List<TokenType> list(TokenType... tokens) {
        return Arrays.asList(tokens);
    }

    private static List<Object> scanLiterals(String source) {
        return scan(source).stream().map(elem -> elem.literal).collect((Collectors.toList()));
    }

    private static List<TokenType> scanTypes(String source) {
        return scan(source).stream().map(elem -> elem.type).collect((Collectors.toList()));
    }

    private static List<Token> scan(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        return tokens;
    }

    private static void testTypes(String source, TokenType... tokens) {
        assertIterableEquals(Arrays.asList(tokens), scanTypes(source));
    }

    private static void testLiterals(String source, Object... literals) {
        assertIterableEquals(Arrays.asList(literals), scanLiterals(source));
    }

    @Test
    void scanOperators() {
        assertAll(
                () -> testTypes("(){},.-+;*", LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, COMMA, DOT, MINUS, PLUS, SEMICOLON, STAR, EOF),
                () -> testTypes("! != = == < <= > >=", BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL, LESS, LESS_EQUAL, GREATER, GREATER_EQUAL, EOF),
                () -> testTypes("(( )){}", LEFT_PAREN, LEFT_PAREN, RIGHT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, EOF),
                () -> testTypes("!*+-/=<> <= ==", BANG, STAR, PLUS, MINUS, SLASH, EQUAL, LESS, GREATER, LESS_EQUAL, EQUAL_EQUAL, EOF)
        );
    }

    @Test
    void scanWhitespace() {
        assertAll(
                () -> testTypes("", EOF),
                () -> testTypes(" \t\r\n", EOF)
        );
    }

    @Test
    void scanComments() {
        assertAll(
                () -> testTypes("/ // foo bar baz", SLASH, EOF)
        );
    }

    @Test
    void scanStrings() {
        assertAll(
                () -> testTypes("\"hello, world\"", STRING, EOF),
                () -> testLiterals("\"hello, world\"", "hello, world", null)
        );
    }

    @Test
    void scanNumbers() {
        assertAll(
                () -> testTypes("123", NUMBER, EOF),
                () -> testTypes("123.456", NUMBER, EOF),
                () -> testLiterals("123", 123.0, null),
                () -> testLiterals("123.456", 123.456, null)
        );
    }

    @Test
    void scanIdentifier() {
        assertAll(
                () -> testTypes("_foo123 abc xyz", IDENTIFIER, IDENTIFIER, IDENTIFIER, EOF),
                () -> testTypes(
                        "and class else false fun for if nil or print return super this true var while",
                        AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR, PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE, EOF)
        );
    }
}
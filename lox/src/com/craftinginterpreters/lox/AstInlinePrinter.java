package com.craftinginterpreters.lox;

class AstInlinePrinter implements Expr.Visitor<String> {
    public String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return print(expr.left) + " " + expr.operator.lexeme + " " + print(expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return "( " + print(expr.expression) + " )";
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return expr.operator.lexeme + " " + print(expr.right);
    }
}

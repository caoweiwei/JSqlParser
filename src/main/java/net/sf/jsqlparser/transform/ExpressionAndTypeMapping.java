package net.sf.jsqlparser.transform;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.transform.context.TransformContext;
import net.sf.jsqlparser.transform.model.ExpressionType;

public class ExpressionAndTypeMapping {

    public static ExpressionType getExpressionReturnType(Expression expr, TransformContext context) {
        ExpressionType expressionType = null;
        if (expr instanceof Function
                || expr instanceof Addition
                || expr instanceof Division
                || expr instanceof CastExpression
                || expr instanceof TimeKeyExpression) {
            expressionType = context.getReturnType(expr);
        }
        if (expr instanceof ComparisonOperator
                || expr instanceof InExpression
                || expr instanceof IsNullExpression) {
            expressionType = ExpressionType.BOOL;
        }
        if (expr instanceof Column || expr instanceof StringValue) {
            expressionType = ExpressionType.STRING;
        }
        if (expr instanceof LongValue || expr instanceof DoubleValue || expr instanceof SignedExpression) {
            expressionType = ExpressionType.NUMBER;
        }

        return expressionType;
    }
}

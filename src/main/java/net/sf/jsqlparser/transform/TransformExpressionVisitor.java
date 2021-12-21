package net.sf.jsqlparser.transform;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.transform.context.TransformContext;
import net.sf.jsqlparser.transform.rule.ItemType;
import net.sf.jsqlparser.transform.rule.RuleMappingManager;
import net.sf.jsqlparser.transform.rule.TransformRule;

public class TransformExpressionVisitor extends ExpressionVisitorAdapter {

    private RuleMappingManager ruleMappingManager;

    private TransformContext transformContext;

    public TransformExpressionVisitor(RuleMappingManager ruleMappingManager, TransformContext transformContext) {
        this.ruleMappingManager = ruleMappingManager;
        this.transformContext = transformContext;
    }

    @Override
    public Expression visit(Function function) {
        TransformRule rule = ruleMappingManager.getRule(transformContext, function, ItemType.FUNCTION);
        if (rule != null) {
            Expression expression = rule.transformFunction.apply(rule, function);
            if (expression instanceof Function) {
                return super.visit((Function) expression);
            } else {
                return expression.acceptAndReturn(this);
            }
        }

        super.visit(function);
        return function;
    }

    @Override
    public Expression visit(Addition expr) {
        TransformRule rule = ruleMappingManager.getRule(transformContext, expr, ItemType.ADDITION);
        if (rule != null) {
            Expression expression = rule.transformFunction.apply(rule, expr);
            return expression.acceptAndReturn(this);
        }

        super.visit(expr);
        return expr;
    }

    @Override
    public Expression visit(Division expr) {
        TransformRule rule = ruleMappingManager.getRule(transformContext, expr, ItemType.DIVISION);
        if (rule != null) {
            Expression expression = rule.transformFunction.apply(rule, expr);
            return expression.acceptAndReturn(this);
        }

        super.visit(expr);
        return expr;
    }

    @Override
    public Expression visit(CastExpression expr) {
        TransformRule rule = ruleMappingManager.getRule(transformContext, expr, ItemType.CAST);
        if (rule != null) {
            Expression expression = rule.transformFunction.apply(rule, expr);
            return expression.acceptAndReturn(this);
        }

        super.visit(expr);
        return expr;
    }


    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {
        TransformRule rule = ruleMappingManager.getRule(transformContext, timeKeyExpression, ItemType.TIMEKEY);
        timeKeyExpression = (TimeKeyExpression)rule.transformFunction.apply(rule, timeKeyExpression);
    }
}

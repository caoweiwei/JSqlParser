package net.sf.jsqlparser.transform;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.transform.context.TransformContext;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.rule.item.ItemType;
import net.sf.jsqlparser.transform.rule.RuleMappingManager;
import net.sf.jsqlparser.transform.rule.TransformRule;

import java.util.HashMap;
import java.util.Map;

public class TransformExpressionVisitor extends ExpressionVisitorAdapter {

    private RuleMappingManager ruleMappingManager;

    private TransformContext transformContext;

    public TransformExpressionVisitor(RuleMappingManager ruleMappingManager, TransformContext transformContext) {
        this.ruleMappingManager = ruleMappingManager;
        this.transformContext = transformContext;
    }

    @Override
    public Expression visit(Function function) {
        super.visit(function);

        TransformRule rule = ruleMappingManager.getRule(transformContext, function);

        if (rule == null) {
            throw new RuntimeException(String.format("miss %s function %s.", transformContext.from, function));
        }
        Expression expression = rule.transformFunction.apply(rule, function);
        transformContext.putReturnType(expression, transformContext.to);
        return expression;
    }

    @Override
    public Expression visit(Addition expr) {
        super.visit(expr);

        TransformRule rule = ruleMappingManager.getRule(transformContext, expr);

        if (rule == null) {
            transformContext.putReturnType(expr, transformContext.from);
            return expr;
        }
        Expression expression = rule.transformFunction.apply(rule, expr);
        transformContext.putReturnType(expression, transformContext.to);

        return expression;
    }

    @Override
    public Expression visit(Division expr) {
        super.visit(expr);

        TransformRule rule = ruleMappingManager.getRule(transformContext, expr);

        if (rule == null) {
            transformContext.putReturnType(expr, transformContext.from);
            return expr;
        }
        Expression expression = rule.transformFunction.apply(rule, expr);
        transformContext.putReturnType(expression, transformContext.to);

        return expression;
    }

    @Override
    public Expression visit(CastExpression expr) {
        super.visit(expr);
        TransformRule rule = ruleMappingManager.getRule(transformContext, expr);

        if (rule == null) {
            transformContext.putReturnType(expr, transformContext.from);
            return expr;
        }
        Expression expression = rule.transformFunction.apply(rule, expr);
        transformContext.putReturnType(expression, transformContext.to);

        return expression;
    }


    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {
        TransformRule rule = ruleMappingManager.getRule(transformContext, timeKeyExpression);
        if (rule == null) {
            transformContext.putReturnType(timeKeyExpression, transformContext.from);
        } else {
            Expression expression = rule.transformFunction.apply(rule, timeKeyExpression);
            transformContext.putReturnType(expression, transformContext.to);
        }
    }
}

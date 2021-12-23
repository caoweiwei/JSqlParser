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
        transformContext.putReturnType(function);
        TransformRule rule = ruleMappingManager.getRule(transformContext, function, ItemType.FUNCTION);
        return rule == null ? function : rule.transformFunction.apply(rule, function);
    }

    @Override
    public Expression visit(Addition expr) {
        super.visit(expr);
        transformContext.putReturnType(expr);
        TransformRule rule = ruleMappingManager.getRule(transformContext, expr, ItemType.ADDITION);

        return rule == null ? expr : rule.transformFunction.apply(rule, expr);
    }

    @Override
    public Expression visit(Division expr) {
        super.visit(expr);
        transformContext.putReturnType(expr);
        TransformRule rule = ruleMappingManager.getRule(transformContext, expr, ItemType.DIVISION);

        return rule == null ? expr : rule.transformFunction.apply(rule, expr);
    }

    @Override
    public Expression visit(CastExpression expr) {
        super.visit(expr);
        transformContext.putReturnType(expr);
        TransformRule rule = ruleMappingManager.getRule(transformContext, expr, ItemType.CAST);

        return rule == null ? expr : rule.transformFunction.apply(rule, expr);
    }


    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {
        TransformRule rule = ruleMappingManager.getRule(transformContext, timeKeyExpression, ItemType.TIMEKEY);
        timeKeyExpression = (TimeKeyExpression)rule.transformFunction.apply(rule, timeKeyExpression);
    }
}

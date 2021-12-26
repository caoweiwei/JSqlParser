package net.sf.jsqlparser.transform.rule;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.transform.ExpressionAndTypeMapping;
import net.sf.jsqlparser.transform.context.TransformContext;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.rule.item.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RuleMappingManager {

    static {
        RuleItemCollector.init();
        RuleMapping.init();
    }

    public TransformRule getRule(TransformContext transformContext, Function function) {
        List<TransformRule> ruleList = RuleMapping.getTransformRules().stream()
                .filter(transformRule -> transformRule.from.itemType.equals(ItemType.FUNCTION))
                .filter(transformRule -> function.getName().equalsIgnoreCase(((FunctionRuleItem)transformRule.from).functionName))
                .filter(transformRule -> transformRule.from.sqlEngine.equals(transformContext.from))
                .filter(transformRule -> transformRule.to.sqlEngine.equals(transformContext.to))
                .map(transformRule -> {
                    List<ExpressionType> expressionTypeList = function.getParameters().getExpressions()
                            .stream()
                            .map(expr -> ExpressionAndTypeMapping.getExpressionReturnType(expr, transformContext))
                            .collect(Collectors.toList());

                    if (((FunctionRuleItem)transformRule.from).params
                            .stream()
                            .map(p -> p.expressionType)
                            .collect(Collectors.toList())
                            .equals(expressionTypeList)) {
                        return transformRule;
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());

        return ruleList.size() > 0 ? ruleList.get(0) : null;
    }

    public TransformRule getRule(TransformContext transformContext, Addition addition) {
        ExpressionType leftExpressionType = ExpressionAndTypeMapping.getExpressionReturnType(addition.getLeftExpression(), transformContext);
        ExpressionType rightExpressionType = ExpressionAndTypeMapping.getExpressionReturnType(addition.getLeftExpression(), transformContext);

        List<TransformRule> ruleList = RuleMapping.getTransformRules().stream()
                .filter(transformRule -> transformRule.from.itemType.equals(ItemType.ADDITION))
                .filter(transformRule -> transformRule.from.sqlEngine.equals(transformContext.from))
                .filter(transformRule -> transformRule.to.sqlEngine.equals(transformContext.to))
                .filter(transformRule -> ((AdditionRuleItem)transformRule.from).leftType.equals(leftExpressionType))
                .filter(transformRule -> ((AdditionRuleItem)transformRule.from).rightType.equals(rightExpressionType))
                .collect(Collectors.toList());

        return ruleList.size() > 0 ? ruleList.get(0) : null;
    }

    public TransformRule getRule(TransformContext transformContext, Division division) {
        ExpressionType leftExpressionType = ExpressionAndTypeMapping.getExpressionReturnType(division.getLeftExpression(), transformContext);
        ExpressionType rightExpressionType = ExpressionAndTypeMapping.getExpressionReturnType(division.getLeftExpression(), transformContext);

        List<TransformRule> ruleList = RuleMapping.getTransformRules().stream()
                .filter(transformRule -> transformRule.from.itemType.equals(ItemType.DIVISION))
                .filter(transformRule -> transformRule.from.sqlEngine.equals(transformContext.from))
                .filter(transformRule -> transformRule.to.sqlEngine.equals(transformContext.to))
                .filter(transformRule -> ((AdditionRuleItem)transformRule.from).leftType.equals(leftExpressionType))
                .filter(transformRule -> ((AdditionRuleItem)transformRule.from).rightType.equals(rightExpressionType))
                .collect(Collectors.toList());

        return ruleList.size() > 0 ? ruleList.get(0) : null;
    }

    public TransformRule getRule(TransformContext transformContext, CastExpression castExpression) {
        ExpressionType leftExpressionType = ExpressionAndTypeMapping.getExpressionReturnType(castExpression.getLeftExpression(), transformContext);
        List<TransformRule> ruleList = RuleMapping.getTransformRules().stream()
                .filter(transformRule -> transformRule.from.itemType.equals(ItemType.CAST))
                .filter(transformRule -> transformRule.from.sqlEngine.equals(transformContext.from))
                .filter(transformRule -> transformRule.to.sqlEngine.equals(transformContext.to))
                .filter(transformRule -> ((CastRuleItem)transformRule.from).leftType.equals(leftExpressionType))
                .filter(transformRule -> ((CastRuleItem)transformRule.from).toType.equals(castExpression.getType().getDataType()))
                .collect(Collectors.toList());

        return ruleList.size() > 0 ? ruleList.get(0) : null;
    }

    public TransformRule getRule(TransformContext transformContext, TimeKeyExpression timeKeyExpression) {
        List<TransformRule> ruleList = RuleMapping.getTransformRules().stream()
                .filter(transformRule -> transformRule.from.itemType.equals(ItemType.TIMEKEY))
                .filter(transformRule -> transformRule.from.sqlEngine.equals(transformContext.from))
                .filter(transformRule -> transformRule.to.sqlEngine.equals(transformContext.to))
                .filter(transformRule -> ((TimeKeyRuleItem)transformRule.from).timekeyName.equals(timeKeyExpression.getStringValue()))
                .collect(Collectors.toList());

        return ruleList.size() > 0 ? ruleList.get(0) : null;
    }

}

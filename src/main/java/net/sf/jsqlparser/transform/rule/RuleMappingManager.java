package net.sf.jsqlparser.transform.rule;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.transform.ExpressionAndTypeMapping;
import net.sf.jsqlparser.transform.context.TransformContext;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.rule.dialect.clickhouse.*;
import net.sf.jsqlparser.transform.rule.dialect.phoenix.ToDate;
import net.sf.jsqlparser.transform.rule.dialect.phoenix.ToNumber;
import net.sf.jsqlparser.transform.rule.dialect.phoenix.Xxhash32;
import net.sf.jsqlparser.transform.rule.item.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class RuleMappingManager {
    private static List<TransformRule> TRANSFORM_RULE = new ArrayList<>();

    static {
        RuleItemCollector.init();
        RuleMappingManager.init();
    }

    public static void init() {

        addRule1();
        addRule2();
        addRule3();
        addRule4();


    }

    private static void addRule4() {
        TransformRule transformRule = new TransformRule();
        transformRule.from = Xxhash32.getItem_string_string();
        transformRule.to = XxHash32.getItem();

        transformRule.transformFunction = new BiFunction<TransformRule, Expression, Expression>() {
            @Override
            public Expression apply(TransformRule transformRule, Expression from) {

                if(!transformRule.condition.apply(transformRule.from)) {
                    return from;
                }

                Function function = (Function)from;
                Function returnFunction = new Function();

                FunctionRuleItem functionRuleItem = (FunctionRuleItem)transformRule.to;

                returnFunction.setName(functionRuleItem.functionName);

                Function concatFunction = new Function();
                concatFunction.setName(Concat.FUNCTION_NAME);
                concatFunction.setParameters(new ExpressionList(
                        Lists.newArrayList(
                                function.getParameters().getExpressions().get(1),
                                function.getParameters().getExpressions().get(0)
                        )
                ));

                returnFunction.setParameters(new ExpressionList(Lists.newArrayList(
                        concatFunction
                )));
                return returnFunction;
            }
        };
        TRANSFORM_RULE.add(transformRule);
    }

    private static void addRule3() {
        TransformRule transformRule = new TransformRule();
        transformRule.from = ToDateTime64OrNull.getItem();
        transformRule.to = ToDate.getItem_string_string_string();

        transformRule.transformFunction = new BiFunction<TransformRule, Expression, Expression>() {
            @Override
            public Expression apply(TransformRule transformRule, Expression from) {

                if(!transformRule.condition.apply(transformRule.from)) {
                    return from;
                }

                Function function = (Function)from;
                Function returnFunction = new Function();

                FunctionRuleItem functionRuleItem = (FunctionRuleItem)transformRule.to;

                returnFunction.setName(functionRuleItem.functionName);

                List<Expression> expressions = Lists.newArrayList(
                        function.getParameters().getExpressions().get(0),
                        new StringValue("yyyy-MM-dd HH:mm:ss.SSS"),
                        new StringValue("GMT+8")
                );
                ExpressionList expressionList = new ExpressionList(expressions);
                returnFunction.setParameters(expressionList);

                return returnFunction;
            }
        };
        TRANSFORM_RULE.add(transformRule);
    }

    private static void addRule2() {
        TransformRule transformRule = new TransformRule();
        transformRule.from = AddSeconds.getItem();
        transformRule.to = net.sf.jsqlparser.transform.rule.dialect.phoenix.Addition.getItem_datetime_number();

        transformRule.transformFunction = new BiFunction<TransformRule, Expression, Expression>() {
            @Override
            public Expression apply(TransformRule transformRule, Expression from) {

                if(!transformRule.condition.apply(transformRule.from)) {
                    return from;
                }

                Function function = (Function)from;

                Addition addition = new Addition();
                addition.setLeftExpression(function.getParameters().getExpressions().get(0));

                Expression param = function.getParameters().getExpressions().get(1);
                double value = 0;
                LongValue longValue = null;
                if (param instanceof SignedExpression) {
                    SignedExpression signedExpression = (SignedExpression) param;
                    longValue = (LongValue) signedExpression.getExpression();
                    longValue.setValue(longValue.getValue() * (signedExpression.getSign() == '-' ? -1 : 1));
                } else if (param instanceof LongValue) {
                    longValue = (LongValue) param;
                }

                value = longValue.getValue() / 86400d;
                addition.setRightExpression(new DoubleValue(String.valueOf(value)));

                return addition;
            }
        };
        TRANSFORM_RULE.add(transformRule);
    }

    public static void addRule1(){
        TransformRule transformRule = new TransformRule();
        transformRule.from = ToInt32OrNull.getItem();
        transformRule.to = ToNumber.getItem_string();

        transformRule.transformFunction = new BiFunction<TransformRule, Expression, Expression>() {
            @Override
            public Expression apply(TransformRule transformRule, Expression from) {

                if(!transformRule.condition.apply(transformRule.from)) {
                    return from;
                }

                Function function = (Function)from;

                FunctionRuleItem functionRuleItem = (FunctionRuleItem)transformRule.to;

                Function returnFunction = new Function();
                returnFunction.setName(functionRuleItem.functionName);
                returnFunction.setParameters(function.getParameters());
                return returnFunction;
            }
        };
        TRANSFORM_RULE.add(transformRule);
    }

    public TransformRule getRule(TransformContext transformContext, Function function) {
        List<TransformRule> ruleList = TRANSFORM_RULE.stream()
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

        List<TransformRule> ruleList = TRANSFORM_RULE.stream()
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

        List<TransformRule> ruleList = TRANSFORM_RULE.stream()
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
        List<TransformRule> ruleList = TRANSFORM_RULE.stream()
                .filter(transformRule -> transformRule.from.itemType.equals(ItemType.CAST))
                .filter(transformRule -> transformRule.from.sqlEngine.equals(transformContext.from))
                .filter(transformRule -> transformRule.to.sqlEngine.equals(transformContext.to))
                .filter(transformRule -> ((CastRuleItem)transformRule.from).leftType.equals(leftExpressionType))
                .filter(transformRule -> ((CastRuleItem)transformRule.from).toType.equals(castExpression.getType().getDataType()))
                .collect(Collectors.toList());

        return ruleList.size() > 0 ? ruleList.get(0) : null;
    }

    public TransformRule getRule(TransformContext transformContext, TimeKeyExpression timeKeyExpression) {
        List<TransformRule> ruleList = TRANSFORM_RULE.stream()
                .filter(transformRule -> transformRule.from.itemType.equals(ItemType.TIMEKEY))
                .filter(transformRule -> transformRule.from.sqlEngine.equals(transformContext.from))
                .filter(transformRule -> transformRule.to.sqlEngine.equals(transformContext.to))
                .filter(transformRule -> ((TimeKeyRuleItem)transformRule.from).timekeyName.equals(timeKeyExpression.getStringValue()))
                .collect(Collectors.toList());

        return ruleList.size() > 0 ? ruleList.get(0) : null;
    }

}

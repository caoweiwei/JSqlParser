package net.sf.jsqlparser.transform.rule;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.transform.context.TransformContext;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.dialect.clickhouse.*;
import net.sf.jsqlparser.transform.rule.dialect.phoenix.ToDate;
import net.sf.jsqlparser.transform.rule.dialect.phoenix.ToNumber;
import net.sf.jsqlparser.transform.rule.dialect.phoenix.Xxhash32;
import net.sf.jsqlparser.transform.rule.item.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public TransformRule getRule(TransformContext transformContext, Expression expression, ItemType from) {
        List<TransformRule> ruleList = TRANSFORM_RULE.stream()
                .filter(transformRule ->
                        transformRule.from.sqlEngine.equals(transformContext.from)
                            &&
                        transformRule.to.sqlEngine.equals(transformContext.to)).collect(Collectors.toList());

        if (ItemType.FUNCTION.equals(from)) {
            Function function = (Function)expression;
            ruleList = ruleList.stream().filter(transformRule -> transformRule.from.itemType.equals(ItemType.FUNCTION)).collect(Collectors.toList());
            for (TransformRule transformRule : ruleList) {
                List<Expression> paramExpressions = function.getParameters().getExpressions();
                List<FunctionRuleItem.FunctionParam> functionRuleParams = ((FunctionRuleItem)transformRule.from).params;
                if (function.getName().equalsIgnoreCase(((FunctionRuleItem)transformRule.from).functionName)
                        &&
                        paramExpressions.size() == functionRuleParams.size()) {
                    List<ExpressionType> expressionTypeList = new ArrayList<>();
                    for (Expression expr : function.getParameters().getExpressions()) {
                        if (expr instanceof Function
                                || expr instanceof Addition
                                || expr instanceof Division
                                || expr instanceof CastExpression
                                || expr instanceof TimeKeyExpression) {
                            expressionTypeList.add(transformContext.getReturnType(expr));
                        }
                        if (expr instanceof ComparisonOperator
                                || expr instanceof InExpression
                                || expr instanceof IsNullExpression) {
                            expressionTypeList.add(ExpressionType.BOOL);
                        }
                        if (expr instanceof Column || expr instanceof StringValue) {
                            expressionTypeList.add(ExpressionType.STRING);
                        }
                        if (expr instanceof LongValue || expr instanceof DoubleValue || expr instanceof SignedExpression) {
                            expressionTypeList.add(ExpressionType.NUMBER);
                        }
                    }

                    if (functionRuleParams
                            .stream()
                            .map(p -> p.expressionType)
                            .collect(Collectors.toList())
                            .equals(expressionTypeList)) {
                        return transformRule;
                    }
                }
            }
        }

        if (ItemType.ADDITION.equals(from)) {
            Addition addition = (Addition)expression;
            Expression leftExpression = addition.getLeftExpression();
            Expression rightExpression = addition.getRightExpression();
            ExpressionType leftExpressionType = null;
            if (leftExpression instanceof Function
                    || leftExpression instanceof Addition
                    || leftExpression instanceof Division
                    || leftExpression instanceof CastExpression
                    || leftExpression instanceof TimeKeyExpression) {
                leftExpressionType = transformContext.getReturnType(leftExpression);
            }
            if (leftExpression instanceof ComparisonOperator
                    || leftExpression instanceof InExpression
                    || leftExpression instanceof IsNullExpression) {
                leftExpressionType = ExpressionType.BOOL;
            }
            if (leftExpression instanceof Column || leftExpression instanceof StringValue) {
                leftExpressionType = ExpressionType.STRING;
            }
            if (leftExpression instanceof LongValue || leftExpression instanceof DoubleValue || leftExpression instanceof SignedExpression) {
                leftExpressionType = ExpressionType.NUMBER;
            }

            ExpressionType rightExpressionType = null;
            if (rightExpression instanceof Function
                    || rightExpression instanceof Addition
                    || rightExpression instanceof Division
                    || rightExpression instanceof CastExpression
                    || rightExpression instanceof TimeKeyExpression) {
                rightExpressionType = transformContext.getReturnType(rightExpression);
            }
            if (rightExpression instanceof ComparisonOperator
                    || rightExpression instanceof InExpression
                    || rightExpression instanceof IsNullExpression) {
                rightExpressionType = ExpressionType.BOOL;
            }
            if (rightExpression instanceof Column || rightExpression instanceof StringValue) {
                rightExpressionType = ExpressionType.STRING;
            }
            if (rightExpression instanceof LongValue || rightExpression instanceof DoubleValue || rightExpression instanceof SignedExpression) {
                rightExpressionType = ExpressionType.NUMBER;
            }

            ruleList = ruleList.stream().filter(transformRule -> transformRule.from.itemType.equals(ItemType.ADDITION)).collect(Collectors.toList());

            ExpressionType finalLeftExpressionType = leftExpressionType;
            ExpressionType finalRightExpressionType = rightExpressionType;
            ruleList = ruleList.stream().filter(transformRule ->
                    ((AdditionRuleItem)transformRule.from).leftType.equals(finalLeftExpressionType)
                    && ((AdditionRuleItem)transformRule.from).rightType.equals(finalRightExpressionType)
            ).collect(Collectors.toList());
            if (ruleList.size()>0) {
                return ruleList.get(0);
            }
        }

        if (ItemType.DIVISION.equals(from)) {
            Division division = (Division)expression;
            Expression leftExpression = division.getLeftExpression();
            Expression rightExpression = division.getRightExpression();
            ExpressionType leftExpressionType = null;
            if (leftExpression instanceof Function
                    || leftExpression instanceof Addition
                    || leftExpression instanceof Division
                    || leftExpression instanceof CastExpression
                    || leftExpression instanceof TimeKeyExpression) {
                leftExpressionType = transformContext.getReturnType(leftExpression);
            }
            if (leftExpression instanceof ComparisonOperator
                    || leftExpression instanceof InExpression
                    || leftExpression instanceof IsNullExpression) {
                leftExpressionType = ExpressionType.BOOL;
            }
            if (leftExpression instanceof Column || leftExpression instanceof StringValue) {
                leftExpressionType = ExpressionType.STRING;
            }
            if (leftExpression instanceof LongValue || leftExpression instanceof DoubleValue || leftExpression instanceof SignedExpression) {
                leftExpressionType = ExpressionType.NUMBER;
            }

            ExpressionType rightExpressionType = null;
            if (rightExpression instanceof Function
                    || rightExpression instanceof Addition
                    || rightExpression instanceof Division
                    || rightExpression instanceof CastExpression
                    || rightExpression instanceof TimeKeyExpression) {
                rightExpressionType = transformContext.getReturnType(rightExpression);
            }
            if (rightExpression instanceof ComparisonOperator
                    || rightExpression instanceof InExpression
                    || rightExpression instanceof IsNullExpression) {
                rightExpressionType = ExpressionType.BOOL;
            }
            if (rightExpression instanceof Column || rightExpression instanceof StringValue) {
                rightExpressionType = ExpressionType.STRING;
            }
            if (rightExpression instanceof LongValue || rightExpression instanceof DoubleValue || rightExpression instanceof SignedExpression) {
                rightExpressionType = ExpressionType.NUMBER;
            }

            ruleList = ruleList.stream().filter(transformRule -> transformRule.from.itemType.equals(ItemType.DIVISION)).collect(Collectors.toList());

            ExpressionType finalLeftExpressionType = leftExpressionType;
            ExpressionType finalRightExpressionType = rightExpressionType;
            ruleList = ruleList.stream().filter(transformRule ->
                    ((DivisionRuleItem)transformRule.from).leftType.equals(finalLeftExpressionType)
                            && ((DivisionRuleItem)transformRule.from).rightType.equals(finalRightExpressionType)
            ).collect(Collectors.toList());
            if (ruleList.size()>0) {
                return ruleList.get(0);
            }
        }

        if (ItemType.CAST.equals(from)) {

            CastExpression castExpression = (CastExpression)expression;
            Expression leftExpression = castExpression.getLeftExpression();
            ExpressionType leftExpressionType = null;
            if (leftExpression instanceof Function
                    || leftExpression instanceof Addition
                    || leftExpression instanceof Division
                    || leftExpression instanceof CastExpression
                    || leftExpression instanceof TimeKeyExpression) {
                leftExpressionType = transformContext.getReturnType(leftExpression);
            }
            if (leftExpression instanceof ComparisonOperator
                    || leftExpression instanceof InExpression
                    || leftExpression instanceof IsNullExpression) {
                leftExpressionType = ExpressionType.BOOL;
            }
            if (leftExpression instanceof Column || leftExpression instanceof StringValue) {
                leftExpressionType = ExpressionType.STRING;
            }
            if (leftExpression instanceof LongValue || leftExpression instanceof DoubleValue || leftExpression instanceof SignedExpression) {
                leftExpressionType = ExpressionType.NUMBER;
            }
            ruleList = ruleList.stream().filter(transformRule -> transformRule.from.itemType.equals(ItemType.CAST)).collect(Collectors.toList());

            ExpressionType finalLeftExpressionType = leftExpressionType;
            ruleList = ruleList.stream().filter(transformRule -> ((CastRuleItem)transformRule.from).leftType.equals(finalLeftExpressionType) &&
                            ((CastRuleItem)transformRule.from).toType.equals(castExpression.getType().getDataType())
                    ).collect(Collectors.toList());
            if (ruleList.size()>0) {
                return ruleList.get(0);
            }
        }
        
        if (ItemType.TIMEKEY.equals(from)) {
            TimeKeyExpression timeKeyExpression = (TimeKeyExpression) expression;
            ruleList = ruleList.stream().filter(transformRule -> transformRule.from.itemType.equals(ItemType.TIMEKEY)).collect(Collectors.toList());
            ruleList = ruleList.stream().filter(transformRule -> ((TimeKeyRuleItem)transformRule.from).timekeyName.equals(timeKeyExpression.getStringValue())).collect(Collectors.toList());
            if (ruleList.size()>0) {
                return ruleList.get(0);
            }
        }

        return null;
    }

}

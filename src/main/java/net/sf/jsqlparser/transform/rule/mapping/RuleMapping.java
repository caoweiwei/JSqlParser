package net.sf.jsqlparser.transform.rule.mapping;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.transform.rule.manager.TransformRule;
import net.sf.jsqlparser.transform.rule.mapping.dialect.clickhouse.*;
import net.sf.jsqlparser.transform.rule.mapping.dialect.phoenix.ToDate;
import net.sf.jsqlparser.transform.rule.mapping.dialect.phoenix.ToNumber;
import net.sf.jsqlparser.transform.rule.mapping.dialect.phoenix.Xxhash32;
import net.sf.jsqlparser.transform.rule.manager.item.FunctionRuleItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class RuleMapping {
    private static final List<TransformRule> TRANSFORM_RULES = new ArrayList<>();

    public static List<TransformRule> getTransformRules() {
        return TRANSFORM_RULES;
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
        TRANSFORM_RULES.add(transformRule);
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
        TRANSFORM_RULES.add(transformRule);
    }

    private static void addRule2() {
        TransformRule transformRule = new TransformRule();
        transformRule.from = AddSeconds.getItem();
        transformRule.to = net.sf.jsqlparser.transform.rule.mapping.dialect.phoenix.Addition.getItem_datetime_number();

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
        TRANSFORM_RULES.add(transformRule);
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
        TRANSFORM_RULES.add(transformRule);
    }
}

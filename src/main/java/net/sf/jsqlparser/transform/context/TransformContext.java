package net.sf.jsqlparser.transform.context;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.RuleItemCollector;
import net.sf.jsqlparser.transform.rule.item.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransformContext {
    public SQLEngine from;

    public SQLEngine to;

    public Map<Expression, ExpressionType> returnTypeMap = new HashMap<>();

    public ExpressionType getReturnType(Expression expression) {

        return returnTypeMap.get(expression);
    }


    public void putReturnType(Expression expression) {
        List<AbstractRuleItem> abstractRuleItemList = RuleItemCollector.getSqlRuleMap().get(from);
        if (expression instanceof Function) {
            Function function = (Function)expression;
            List<FunctionRuleItem> ruleItemList = abstractRuleItemList
                    .stream()
                    .filter(rule -> rule.itemType.equals(ItemType.FUNCTION))
                    .map(rule -> (FunctionRuleItem)rule)
                    .filter(functionRuleItem -> functionRuleItem.functionName.equals(function.getName()))
                    .collect(Collectors.toList());

            List<Expression> expressionList = function.getParameters().getExpressions();

            List<ExpressionType> expressionTypeList = new ArrayList<>();
            for (Expression expr : expressionList) {
                if (expr instanceof Function
                        || expr instanceof Addition
                        || expr instanceof Division
                        || expr instanceof CastExpression
                        || expr instanceof TimeKeyExpression) {
                    expressionTypeList.add(returnTypeMap.get(expr));
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
            ruleItemList = ruleItemList
                    .stream()
                    .filter(ruleItem -> ruleItem.params
                            .stream()
                            .map(functionParam -> functionParam.expressionType)
                            .collect(Collectors.toList()).equals(expressionTypeList)
                    ).collect(Collectors.toList());
            if (ruleItemList.size() > 0) {
                returnTypeMap.put(expression, ruleItemList.get(0).returnType);
            }
        }

        if (expression instanceof Addition) {
            Addition addition = (Addition)expression;
            Expression leftExpression = addition.getLeftExpression();
            Expression rightExpression = addition.getRightExpression();

            ExpressionType leftExpressionType = null;
            if (leftExpression instanceof Function
                    || leftExpression instanceof Addition
                    || leftExpression instanceof Division
                    || leftExpression instanceof CastExpression
                    || leftExpression instanceof TimeKeyExpression) {
                leftExpressionType = returnTypeMap.get(leftExpression);
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
                rightExpressionType = returnTypeMap.get(rightExpression);
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

            ExpressionType finalLeftExpressionType = leftExpressionType;
            ExpressionType finalRightExpressionType = rightExpressionType;
            List<AdditionRuleItem> ruleItemList = abstractRuleItemList
                    .stream()
                    .filter(rule -> rule.itemType.equals(ItemType.ADDITION))
                    .map(rule -> (AdditionRuleItem)rule)
                    .filter(additionRuleItem -> additionRuleItem.leftType.equals(finalLeftExpressionType)
                            && additionRuleItem.rightType.equals(finalRightExpressionType))
                    .collect(Collectors.toList());

            if (ruleItemList.size() > 0) {
                returnTypeMap.put(expression, ruleItemList.get(0).returnType);
            }

        }
        if (expression instanceof Division) {
            Division division = (Division)expression;
            Expression leftExpression = division.getLeftExpression();
            Expression rightExpression = division.getRightExpression();

            ExpressionType leftExpressionType = null;
            if (leftExpression instanceof Function
                    || leftExpression instanceof Addition
                    || leftExpression instanceof Division
                    || leftExpression instanceof CastExpression
                    || leftExpression instanceof TimeKeyExpression) {
                leftExpressionType = returnTypeMap.get(leftExpression);
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
                rightExpressionType = returnTypeMap.get(rightExpression);
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

            ExpressionType finalLeftExpressionType = leftExpressionType;
            ExpressionType finalRightExpressionType = rightExpressionType;
            List<DivisionRuleItem> ruleItemList = abstractRuleItemList
                    .stream()
                    .filter(rule -> rule.itemType.equals(ItemType.DIVISION))
                    .map(rule -> (DivisionRuleItem)rule)
                    .filter(additionRuleItem -> additionRuleItem.leftType.equals(finalLeftExpressionType)
                            && additionRuleItem.rightType.equals(finalRightExpressionType))
                    .collect(Collectors.toList());

            if (ruleItemList.size() > 0) {
                returnTypeMap.put(expression, ruleItemList.get(0).returnType);
            }
        }
        if (expression instanceof CastExpression) {
            CastExpression castExpression = (CastExpression)expression;
            Expression leftExpression = castExpression.getLeftExpression();
            ExpressionType leftExpressionType = null;
            if (leftExpression instanceof Function
                    || leftExpression instanceof Addition
                    || leftExpression instanceof Division
                    || leftExpression instanceof CastExpression
                    || leftExpression instanceof TimeKeyExpression) {
                leftExpressionType = returnTypeMap.get(leftExpression);
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

            ExpressionType finalLeftExpressionType = leftExpressionType;
            List<CastRuleItem> ruleItemList = abstractRuleItemList
                    .stream()
                    .filter(rule -> rule.itemType.equals(ItemType.CAST))
                    .map(rule -> (CastRuleItem)rule)
                    .filter(castRuleItem ->  castRuleItem.leftType.equals(finalLeftExpressionType)
                    && castRuleItem.toType.equals(castExpression.getType().getDataType()))
                    .collect(Collectors.toList());
            if (ruleItemList.size() > 0) {
                returnTypeMap.put(expression, ruleItemList.get(0).returnType);
            }
        }
        if (expression instanceof TimeKeyExpression) {
            TimeKeyExpression timeKeyExpression = (TimeKeyExpression)expression;
            List<TimeKeyRuleItem> ruleItemList = abstractRuleItemList
                    .stream()
                    .filter(rule -> rule.itemType.equals(ItemType.TIMEKEY))
                    .map(rule -> (TimeKeyRuleItem)rule)
                    .filter(timeKeyRuleItem -> timeKeyRuleItem.timekeyName.equals(timeKeyExpression.getStringValue()))
                    .collect(Collectors.toList());
            if (ruleItemList.size() > 0) {
                returnTypeMap.put(expression, ruleItemList.get(0).returnType);
            }
        }

    }
}

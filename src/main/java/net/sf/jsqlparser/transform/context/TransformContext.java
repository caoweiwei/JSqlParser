package net.sf.jsqlparser.transform.context;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.transform.ExpressionAndTypeMapping;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.RuleItemCollector;
import net.sf.jsqlparser.transform.rule.item.*;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class TransformContext {
    public SQLEngine from;

    public SQLEngine to;

    public Map<Expression, ExpressionType> returnTypeMap = new HashMap<>();

    public ExpressionType getReturnType(Expression expression) {

        return returnTypeMap.get(expression);
    }

    public void putReturnType(Expression expression, SQLEngine sqlEngine) {
        if (expression instanceof Function) {
            putReturnType((Function) expression, sqlEngine);
        }
        if (expression instanceof Addition) {
            putReturnType((Addition) expression, sqlEngine);
        }
        if (expression instanceof Division) {
            putReturnType((Division) expression, sqlEngine);
        }
        if (expression instanceof CastExpression) {
            putReturnType((CastExpression) expression, sqlEngine);
        }
        if (expression instanceof TimeKeyExpression) {
            putReturnType((TimeKeyExpression) expression, sqlEngine);
        }
    }

    private void putReturnType(Function function, SQLEngine sqlEngine) {

        List<AbstractRuleItem> abstractRuleItemList = RuleItemCollector.getSqlRuleMap().get(sqlEngine);
        List<ExpressionType> expressionTypeList = function
                .getParameters().getExpressions()
                .stream().map(expression ->  ExpressionAndTypeMapping.getExpressionReturnType(expression, this))
                .filter(Objects::nonNull).collect(Collectors.toList());

        List<FunctionRuleItem> ruleItemList = abstractRuleItemList
                .stream().filter(rule -> rule.itemType.equals(ItemType.FUNCTION))
                .map(rule -> (FunctionRuleItem)rule)
                .filter(ruleItem -> ruleItem.functionName.equals(function.getName())
                        && ruleItem.params.stream()
                        .map(functionParam -> functionParam.expressionType)
                        .collect(Collectors.toList()).equals(expressionTypeList))
                .collect(Collectors.toList());

        if (ruleItemList.size() > 0) {
            returnTypeMap.put(function, ruleItemList.get(0).returnType);
        }
    }

    private void putReturnType(Addition addition, SQLEngine sqlEngine) {
        List<AbstractRuleItem> abstractRuleItemList = RuleItemCollector.getSqlRuleMap().get(sqlEngine);

        ExpressionType leftExpressionType = ExpressionAndTypeMapping.getExpressionReturnType(addition.getLeftExpression(), this);
        ExpressionType rightExpressionType = ExpressionAndTypeMapping.getExpressionReturnType(addition.getLeftExpression(), this);

        List<AdditionRuleItem> ruleItemList = abstractRuleItemList
                .stream().filter(rule -> rule.itemType.equals(ItemType.ADDITION))
                .map(rule -> (AdditionRuleItem)rule)
                .filter(additionRuleItem ->
                        additionRuleItem.leftType.equals(leftExpressionType)
                                && additionRuleItem.rightType.equals(rightExpressionType))
                .collect(Collectors.toList());

        if (ruleItemList.size() > 0) {
            returnTypeMap.put(addition, ruleItemList.get(0).returnType);
        }
    }

    private void putReturnType(Division division, SQLEngine sqlEngine) {
        List<AbstractRuleItem> abstractRuleItemList = RuleItemCollector.getSqlRuleMap().get(sqlEngine);

        ExpressionType leftExpressionType = ExpressionAndTypeMapping.getExpressionReturnType(division.getLeftExpression(), this);
        ExpressionType rightExpressionType = ExpressionAndTypeMapping.getExpressionReturnType(division.getLeftExpression(), this);

        List<AdditionRuleItem> ruleItemList = abstractRuleItemList
                .stream().filter(rule -> rule.itemType.equals(ItemType.DIVISION))
                .map(rule -> (AdditionRuleItem)rule)
                .filter(additionRuleItem ->
                        additionRuleItem.leftType.equals(leftExpressionType)
                                && additionRuleItem.rightType.equals(rightExpressionType))
                .collect(Collectors.toList());

        if (ruleItemList.size() > 0) {
            returnTypeMap.put(division, ruleItemList.get(0).returnType);
        }
    }



    private void putReturnType(CastExpression castExpression, SQLEngine sqlEngine) {
        List<AbstractRuleItem> abstractRuleItemList = RuleItemCollector.getSqlRuleMap().get(sqlEngine);

        ExpressionType leftExpressionType = ExpressionAndTypeMapping.getExpressionReturnType(castExpression.getLeftExpression(), this);

        List<CastRuleItem> ruleItemList = abstractRuleItemList
                .stream().filter(rule -> rule.itemType.equals(ItemType.CAST))
                .map(rule -> (CastRuleItem)rule)
                .filter(castRuleItem ->  castRuleItem.leftType.equals(leftExpressionType)
                        && castRuleItem.toType.equals(castExpression.getType().getDataType()))
                .collect(Collectors.toList());
        if (ruleItemList.size() > 0) {
            returnTypeMap.put(castExpression, ruleItemList.get(0).returnType);
        }

    }

    private void putReturnType(TimeKeyExpression timeKeyExpression, SQLEngine sqlEngine) {
        List<AbstractRuleItem> abstractRuleItemList = RuleItemCollector.getSqlRuleMap().get(sqlEngine);
        List<TimeKeyRuleItem> ruleItemList = abstractRuleItemList
                .stream()
                .filter(rule -> rule.itemType.equals(ItemType.TIMEKEY))
                .map(rule -> (TimeKeyRuleItem)rule)
                .filter(timeKeyRuleItem -> timeKeyRuleItem.timekeyName.equals(timeKeyExpression.getStringValue()))
                .collect(Collectors.toList());
        if (ruleItemList.size() > 0) {
            returnTypeMap.put(timeKeyExpression, ruleItemList.get(0).returnType);
        }
    }


}

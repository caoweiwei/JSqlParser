package net.sf.jsqlparser.transform.rule.mapping.dialect.phoenix;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.manager.item.ItemType;
import net.sf.jsqlparser.transform.rule.manager.item.FunctionRuleItem;

public class ToChar {
    public static final String FUNCTION_NAME = "to_char";

    public static FunctionRuleItem getItem_datetime() {

        return new FunctionRuleItem(
                SQLEngine.PHOENIX,
                ItemType.FUNCTION,
                FUNCTION_NAME,
                ExpressionType.STRING,
                Lists.newArrayList(
                        FunctionRuleItem
                                .FunctionParam.builder()
                                .expressionType(ExpressionType.DATETIME)
                                .build()
                )
        );
    }

    public static FunctionRuleItem getItem_number() {

        return new FunctionRuleItem(
                SQLEngine.PHOENIX,
                ItemType.FUNCTION,
                FUNCTION_NAME,
                ExpressionType.STRING,
                Lists.newArrayList(
                        FunctionRuleItem
                                .FunctionParam.builder()
                                .expressionType(ExpressionType.NUMBER)
                                .build()
                )
        );
    }

    public static FunctionRuleItem getItem_number_string() {

        return new FunctionRuleItem(
                SQLEngine.PHOENIX,
                ItemType.FUNCTION,
                FUNCTION_NAME,
                ExpressionType.STRING,
                Lists.newArrayList(
                        FunctionRuleItem
                                .FunctionParam.builder()
                                .expressionType(ExpressionType.NUMBER)
                                .build(),
                        FunctionRuleItem
                                .FunctionParam.builder()
                                .expressionType(ExpressionType.STRING)
                                .build()
                )
        );
    }

    public static FunctionRuleItem getItem_datetime_string() {

        return new FunctionRuleItem(
                SQLEngine.PHOENIX,
                ItemType.FUNCTION,
                FUNCTION_NAME,
                ExpressionType.STRING,
                Lists.newArrayList(
                        FunctionRuleItem
                                .FunctionParam.builder()
                                .expressionType(ExpressionType.DATETIME)
                                .build(),
                        FunctionRuleItem
                                .FunctionParam.builder()
                                .expressionType(ExpressionType.STRING)
                                .build()
                )
        );
    }
}

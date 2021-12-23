package net.sf.jsqlparser.transform.rule.dialect.phoenix;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.item.ItemType;
import net.sf.jsqlparser.transform.rule.item.FunctionRuleItem;

public class SafeToDate {
    public static final String FUNCTION_NAME = "safe_to_date";

    public static FunctionRuleItem getItem_string_string() {

        return new FunctionRuleItem(
                SQLEngine.PHOENIX,
                ItemType.FUNCTION,
                FUNCTION_NAME,
                ExpressionType.DATE,
                Lists.newArrayList(
                        FunctionRuleItem
                                .FunctionParam.builder()
                                .expressionType(ExpressionType.STRING)
                                .build(),
                        FunctionRuleItem
                                .FunctionParam.builder()
                                .expressionType(ExpressionType.STRING)
                                .build()
                )
        );
    }
    public static FunctionRuleItem getItem_string_string_string() {

        return new FunctionRuleItem(
                SQLEngine.PHOENIX,
                ItemType.FUNCTION,
                FUNCTION_NAME,
                ExpressionType.DATE,
                Lists.newArrayList(
                        FunctionRuleItem
                                .FunctionParam.builder()
                                .expressionType(ExpressionType.STRING)
                                .build(),
                        FunctionRuleItem
                                .FunctionParam.builder()
                                .expressionType(ExpressionType.STRING)
                                .build(),
                        FunctionRuleItem
                                .FunctionParam.builder()
                                .expressionType(ExpressionType.STRING)
                                .build()
                )
        );
    }
}

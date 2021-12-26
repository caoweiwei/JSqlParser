package net.sf.jsqlparser.transform.rule.mapping.dialect.clickhouse;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.manager.item.ItemType;
import net.sf.jsqlparser.transform.rule.manager.item.FunctionRuleItem;

public class ToString {
    public static final String FUNCTION_NAME = "toString";

    public static FunctionRuleItem getItem_datetime() {

        return new FunctionRuleItem(
                SQLEngine.CLICKHOUSE,
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
                SQLEngine.CLICKHOUSE,
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
}

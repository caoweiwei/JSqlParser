package net.sf.jsqlparser.transform.rule.dialect.clickhouse;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.item.ItemType;
import net.sf.jsqlparser.transform.rule.item.FunctionRuleItem;

public class ToDateTime64OrNull {
    public static final String FUNCTION_NAME = "toDateTime64OrNull";

    public static FunctionRuleItem getItem() {

        return new FunctionRuleItem(
                SQLEngine.CLICKHOUSE,
                ItemType.FUNCTION,
                FUNCTION_NAME,
                ExpressionType.DATETIME,
                Lists.newArrayList(
                        FunctionRuleItem
                                .FunctionParam.builder()
                                .expressionType(ExpressionType.STRING)
                                .build()
                )
        );
    }
}

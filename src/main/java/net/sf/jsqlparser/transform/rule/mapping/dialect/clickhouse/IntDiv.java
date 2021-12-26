package net.sf.jsqlparser.transform.rule.mapping.dialect.clickhouse;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.manager.item.ItemType;
import net.sf.jsqlparser.transform.rule.manager.item.FunctionRuleItem;

public class IntDiv {
    public static final String FUNCTION_NAME = "intDiv";

    public static FunctionRuleItem getItem() {

        return new FunctionRuleItem(
                SQLEngine.CLICKHOUSE,
                ItemType.FUNCTION,
                FUNCTION_NAME,
                ExpressionType.NUMBER,
                Lists.newArrayList(
                        FunctionRuleItem
                                .FunctionParam.builder()
                                .expressionType(ExpressionType.NUMBER)
                                .build(),
                        FunctionRuleItem
                                .FunctionParam.builder()
                                .expressionType(ExpressionType.NUMBER)
                                .build()
                )
        );
    }
}

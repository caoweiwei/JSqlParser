package net.sf.jsqlparser.transform.rule.config.dialect.clickhouse;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.manager.item.ItemType;
import net.sf.jsqlparser.transform.rule.manager.item.FunctionRuleItem;

public class AddSeconds {
    public static final String FUNCTION_NAME = "addSeconds";

    public static FunctionRuleItem getItem() {

        return new FunctionRuleItem(
                SQLEngine.CLICKHOUSE,
                ItemType.FUNCTION,
                FUNCTION_NAME,
                ExpressionType.DATETIME,
                Lists.newArrayList(
                        FunctionRuleItem
                                .FunctionParam.builder()
                                .expressionType(ExpressionType.DATETIME)
                                .build(),
                        FunctionRuleItem
                                .FunctionParam.builder()
                                .expressionType(ExpressionType.NUMBER)
                                .build()
                )
        );
    }
}

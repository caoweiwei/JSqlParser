package net.sf.jsqlparser.transform.rule.dialect.clickhouse;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.item.ItemType;
import net.sf.jsqlparser.transform.rule.item.FunctionRuleItem;

public class Now64 {
    public static final String FUNCTION_NAME = "now64";

    public static FunctionRuleItem getItem() {

        return new FunctionRuleItem(
                SQLEngine.CLICKHOUSE,
                ItemType.FUNCTION,
                FUNCTION_NAME,
                ExpressionType.DATETIME,
                Lists.newArrayList()
        );
    }
}

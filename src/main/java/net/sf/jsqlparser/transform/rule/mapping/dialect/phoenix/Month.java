package net.sf.jsqlparser.transform.rule.mapping.dialect.phoenix;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.manager.item.ItemType;
import net.sf.jsqlparser.transform.rule.manager.item.FunctionRuleItem;

public class Month {
    public static final String FUNCTION_NAME = "month";

    public static FunctionRuleItem getItem() {

          return new FunctionRuleItem(
                SQLEngine.PHOENIX,
                ItemType.FUNCTION,
                FUNCTION_NAME,
                ExpressionType.NUMBER,
                Lists.newArrayList(
                        FunctionRuleItem
                                .FunctionParam.builder()
                                .expressionType(ExpressionType.DATETIME)
                                .build()
                )
        );
    }
}

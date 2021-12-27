package net.sf.jsqlparser.transform.rule.config.dialect.phoenix;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.manager.item.ItemType;
import net.sf.jsqlparser.transform.rule.manager.item.FunctionRuleItem;

public class Trunc {
    public static final String FUNCTION_NAME = "trunc";

    public static FunctionRuleItem getItem() {

        return new FunctionRuleItem(
                SQLEngine.PHOENIX,
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
                                .expressionType(ExpressionType.STRING)
                                .build()
                )
        );
    }
}

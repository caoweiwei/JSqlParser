package net.sf.jsqlparser.transform.rule.mapping.dialect.phoenix;

import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.manager.item.ItemType;
import net.sf.jsqlparser.transform.rule.manager.item.TimeKeyRuleItem;

public class CurrentDate {
    public static final String TIMEKEY_NAME = "CURRENT_DATE";

    public static TimeKeyRuleItem getItem() {

        return new TimeKeyRuleItem(
                SQLEngine.PHOENIX,
                ItemType.TIMEKEY,
                TIMEKEY_NAME,
                ExpressionType.DATETIME
        );
    }
}

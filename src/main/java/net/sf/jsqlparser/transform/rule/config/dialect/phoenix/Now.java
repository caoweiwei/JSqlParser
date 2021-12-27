package net.sf.jsqlparser.transform.rule.config.dialect.phoenix;

import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.manager.item.ItemType;
import net.sf.jsqlparser.transform.rule.manager.item.TimeKeyRuleItem;

public class Now {
    public static final String TIMEKEY_NAME = "now";

    public static TimeKeyRuleItem getItem() {

        return new TimeKeyRuleItem(
                SQLEngine.PHOENIX,
                ItemType.TIMEKEY,
                TIMEKEY_NAME,
                ExpressionType.DATETIME
        );
    }
}

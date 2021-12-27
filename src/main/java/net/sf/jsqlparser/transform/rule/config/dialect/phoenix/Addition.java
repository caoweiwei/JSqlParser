package net.sf.jsqlparser.transform.rule.config.dialect.phoenix;

import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.manager.item.AdditionRuleItem;
import net.sf.jsqlparser.transform.rule.manager.item.ItemType;

public class Addition {

    public static AdditionRuleItem getItem_datetime_number() {

        return new AdditionRuleItem(
                SQLEngine.PHOENIX,
                ItemType.ADDITION,
                ExpressionType.DATETIME,
                ExpressionType.DATETIME,
                ExpressionType.NUMBER
        );
    }

    public static AdditionRuleItem getItem_number_datetime() {

        return new AdditionRuleItem(
                SQLEngine.PHOENIX,
                ItemType.ADDITION,
                ExpressionType.DATETIME,
                ExpressionType.NUMBER,
                ExpressionType.DATETIME
        );
    }
}

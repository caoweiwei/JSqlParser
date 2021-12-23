package net.sf.jsqlparser.transform.rule.dialect.phoenix;

import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.item.AdditionRuleItem;
import net.sf.jsqlparser.transform.rule.item.ItemType;
import net.sf.jsqlparser.transform.rule.item.DivisionRuleItem;

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

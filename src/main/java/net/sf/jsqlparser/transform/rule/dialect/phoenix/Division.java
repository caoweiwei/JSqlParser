package net.sf.jsqlparser.transform.rule.dialect.phoenix;

import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.item.ItemType;
import net.sf.jsqlparser.transform.rule.item.DivisionRuleItem;

public class Division {

    public static DivisionRuleItem getItem() {

        return new DivisionRuleItem(
                SQLEngine.PHOENIX,
                ItemType.DIVISION,
                ExpressionType.NUMBER,
                ExpressionType.NUMBER,
                ExpressionType.NUMBER
        );
    }
}

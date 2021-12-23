package net.sf.jsqlparser.transform.rule.dialect.phoenix;

import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.item.ItemType;
import net.sf.jsqlparser.transform.rule.item.CastRuleItem;

public class Cast {

    public static CastRuleItem getItem() {

   return new CastRuleItem(
                SQLEngine.PHOENIX,
                ItemType.CAST,
                ExpressionType.NUMBER,
                ExpressionType.STRING,
                "int"
        );
    }
}

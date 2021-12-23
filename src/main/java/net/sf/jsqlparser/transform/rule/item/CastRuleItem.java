package net.sf.jsqlparser.transform.rule.item;

import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;

public class CastRuleItem extends AbstractRuleItem {

    public ExpressionType leftType;

    public String toType;

    public ExpressionType returnType;

    public CastRuleItem(SQLEngine sqlEngine, ItemType itemType, ExpressionType returnType, ExpressionType leftType, String toType) {
        super(sqlEngine, itemType);
        this.returnType = returnType;
        this.leftType = leftType;
        this.toType = toType;
    }
}

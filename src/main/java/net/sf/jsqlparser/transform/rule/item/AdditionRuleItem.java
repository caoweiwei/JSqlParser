package net.sf.jsqlparser.transform.rule.item;

import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;

public class AdditionRuleItem extends AbstractRuleItem {

    public ExpressionType leftType;

    public ExpressionType rightType;

    public ExpressionType returnType;

    public AdditionRuleItem(SQLEngine sqlEngine, ItemType itemType, ExpressionType returnType, ExpressionType leftType, ExpressionType rightType) {
        super(sqlEngine, itemType);
        this.returnType = returnType;
        this.leftType = leftType;
        this.rightType = rightType;
    }
}

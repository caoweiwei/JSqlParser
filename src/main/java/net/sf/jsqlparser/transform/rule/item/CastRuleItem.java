package net.sf.jsqlparser.transform.rule.item;

import net.sf.jsqlparser.transform.model.ExpressionType;

public class CastRuleItem extends AbstractRuleItem {

    public ExpressionType leftType;

    public ExpressionType toType;
}

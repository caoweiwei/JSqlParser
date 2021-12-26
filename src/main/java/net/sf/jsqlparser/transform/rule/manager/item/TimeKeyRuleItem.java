package net.sf.jsqlparser.transform.rule.manager.item;

import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;


public class TimeKeyRuleItem extends AbstractRuleItem {
    public String timekeyName;

    public ExpressionType returnType;

    public TimeKeyRuleItem(){}

    public TimeKeyRuleItem(SQLEngine sqlEngine, ItemType itemType, String timekeyName, ExpressionType returnType) {
        super(sqlEngine, itemType);
        this.returnType = returnType;
        this.timekeyName = timekeyName;
    }
}

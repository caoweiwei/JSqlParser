package net.sf.jsqlparser.transform.rule.manager.item;

import net.sf.jsqlparser.transform.model.SQLEngine;

public class AbstractRuleItem {

    public SQLEngine sqlEngine;

    public ItemType itemType;

    public AbstractRuleItem() {}

    public AbstractRuleItem(SQLEngine sqlEngine, ItemType itemType) {
        this.sqlEngine = sqlEngine;
        this.itemType = itemType;
    }
}

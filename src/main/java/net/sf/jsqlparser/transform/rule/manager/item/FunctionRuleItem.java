package net.sf.jsqlparser.transform.rule.manager.item;

import lombok.Builder;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;

import java.util.List;

public class FunctionRuleItem extends AbstractRuleItem {
    public String functionName;

    public List<FunctionParam> params;

    public ExpressionType returnType;

    public FunctionRuleItem(){}

    public FunctionRuleItem( SQLEngine sqlEngine, ItemType itemType, String functionName, ExpressionType returnType, List<FunctionParam> params) {
        super(sqlEngine, itemType);
        this.returnType = returnType;
        this.functionName = functionName;
        this.params = params;
    }

    @Builder
    public static class FunctionParam {

        public ExpressionType expressionType;

        public Object defaultValue;

        public List<FunctionRuleItem> functionRuleItems;

    }
}

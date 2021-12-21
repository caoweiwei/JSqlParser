package net.sf.jsqlparser.transform.rule.item;

import net.sf.jsqlparser.transform.model.ExpressionType;

import java.util.List;

public class FunctionRuleItem extends AbstractRuleItem {
    public String functionName;

    public List<FunctionParam> params;

    public static class FunctionParam {

        public ExpressionType expressionType;

        public Object defaultValue;

        public List<FunctionRuleItem> functionRuleItems;

    }
}

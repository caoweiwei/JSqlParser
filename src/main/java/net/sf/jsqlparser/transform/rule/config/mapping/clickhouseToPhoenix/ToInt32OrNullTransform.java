package net.sf.jsqlparser.transform.rule.config.mapping.clickhouseToPhoenix;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.transform.rule.config.dialect.clickhouse.ToInt32OrNull;
import net.sf.jsqlparser.transform.rule.config.dialect.phoenix.ToNumber;
import net.sf.jsqlparser.transform.rule.manager.TransformRule;
import net.sf.jsqlparser.transform.rule.manager.item.FunctionRuleItem;

public class ToInt32OrNullTransform {
    public static TransformRule build() {
        TransformRule transformRule = new TransformRule();
        transformRule.from = ToInt32OrNull.getItem();
        transformRule.to = ToNumber.getItem_string();

        transformRule.transformFunction = (rule, from) -> {

            if(!rule.condition.apply(rule.from)) {
                return from;
            }

            Function function = (Function)from;

            FunctionRuleItem functionRuleItem = (FunctionRuleItem) rule.to;

            Function returnFunction = new Function();
            returnFunction.setName(functionRuleItem.functionName);
            returnFunction.setParameters(function.getParameters());
            return returnFunction;
        };
        return transformRule;
    }
}

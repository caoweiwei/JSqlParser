package net.sf.jsqlparser.transform.rule.config.mapping.phoenixToClickhouse;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.transform.rule.config.dialect.clickhouse.Concat;
import net.sf.jsqlparser.transform.rule.config.dialect.clickhouse.XxHash32;
import net.sf.jsqlparser.transform.rule.config.dialect.phoenix.Xxhash32;
import net.sf.jsqlparser.transform.rule.manager.TransformRule;
import net.sf.jsqlparser.transform.rule.manager.item.FunctionRuleItem;

public class Xxhash32Transform {
    public static TransformRule build() {
        TransformRule transformRule = new TransformRule();
        transformRule.from = Xxhash32.getItem_string_string();
        transformRule.to = XxHash32.getItem();

        transformRule.transformFunction = (rule, from) -> {

            if(!rule.condition.apply(rule.from)) {
                return from;
            }

            Function function = (Function)from;
            Function returnFunction = new Function();

            FunctionRuleItem functionRuleItem = (FunctionRuleItem) rule.to;

            returnFunction.setName(functionRuleItem.functionName);

            Function concatFunction = new Function();
            concatFunction.setName(Concat.FUNCTION_NAME);
            concatFunction.setParameters(new ExpressionList(
                    Lists.newArrayList(
                            function.getParameters().getExpressions().get(1),
                            function.getParameters().getExpressions().get(0)
                    )
            ));

            returnFunction.setParameters(new ExpressionList(Lists.newArrayList(
                    concatFunction
            )));
            return returnFunction;
        };
        return transformRule;
    }
}

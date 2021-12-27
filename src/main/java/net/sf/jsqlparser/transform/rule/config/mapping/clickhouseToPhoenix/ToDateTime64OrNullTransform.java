package net.sf.jsqlparser.transform.rule.config.mapping.clickhouseToPhoenix;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.transform.rule.config.dialect.clickhouse.ToDateTime64OrNull;
import net.sf.jsqlparser.transform.rule.config.dialect.phoenix.ToDate;
import net.sf.jsqlparser.transform.rule.manager.TransformRule;
import net.sf.jsqlparser.transform.rule.manager.item.FunctionRuleItem;

import java.util.List;

public class ToDateTime64OrNullTransform {
    public static TransformRule build() {
        TransformRule transformRule = new TransformRule();
        transformRule.from = ToDateTime64OrNull.getItem();
        transformRule.to = ToDate.getItem_string_string_string();

        transformRule.transformFunction = (rule, from) -> {

            if(!rule.condition.apply(rule.from)) {
                return from;
            }

            Function function = (Function)from;
            Function returnFunction = new Function();

            FunctionRuleItem functionRuleItem = (FunctionRuleItem) rule.to;

            returnFunction.setName(functionRuleItem.functionName);

            List<Expression> expressions = Lists.newArrayList(
                    function.getParameters().getExpressions().get(0),
                    new StringValue("yyyy-MM-dd HH:mm:ss.SSS"),
                    new StringValue("GMT+8")
            );
            ExpressionList expressionList = new ExpressionList(expressions);
            returnFunction.setParameters(expressionList);

            return returnFunction;
        };
        return transformRule;
    }
}

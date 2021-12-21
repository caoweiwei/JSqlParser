package net.sf.jsqlparser.transform.rule;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.transform.context.TransformContext;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.item.AbstractRuleItem;
import net.sf.jsqlparser.transform.rule.item.FunctionRuleItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RuleMappingManager {
    private static List<TransformRule> TRANSFORM_RULE = new ArrayList<>();

    static {
        init();
    }

    public static void init() {
        TransformRule transformRule = new TransformRule();
        FunctionRuleItem from = new FunctionRuleItem();
        from.functionName = "toint32ornull";
        from.itemType = ItemType.FUNCTION;
        from.sqlEngine = SQLEngine.CLICKHOUSE;
        from.params = new ArrayList<>();
        FunctionRuleItem.FunctionParam functionParam1 = new FunctionRuleItem.FunctionParam();
        functionParam1.expressionType = ExpressionType.STRING;

        from.params.add(functionParam1);

        FunctionRuleItem to = new FunctionRuleItem();
        to.functionName = "to_number";
        to.itemType = ItemType.FUNCTION;
        to.sqlEngine = SQLEngine.PHOENIX;
        to.params = new ArrayList<>();
        FunctionRuleItem.FunctionParam functionParam2 = new FunctionRuleItem.FunctionParam();
        functionParam2.expressionType = ExpressionType.STRING;

        to.params.add(functionParam2);
        transformRule.from = from;
        transformRule.to = to;

        transformRule.condition = new Function<AbstractRuleItem, Boolean>() {
            @Override
            public Boolean apply(AbstractRuleItem abstractRuleItem) {
                return true;
            }
        };

        transformRule.transformFunction = new BiFunction<TransformRule, Expression, Expression>() {
            @Override
            public Expression apply(TransformRule transformRule, Expression expression) {
                net.sf.jsqlparser.expression.Function function = (net.sf.jsqlparser.expression.Function)expression;
                net.sf.jsqlparser.expression.Function function1 = new net.sf.jsqlparser.expression.Function();

                FunctionRuleItem functionRuleItem = (FunctionRuleItem)transformRule.to;

                function1.setName(functionRuleItem.functionName);
                function1.setParameters(function.getParameters());
                return function1;
            }
        };


        TRANSFORM_RULE.add(transformRule);
    }

    public TransformRule getRule(TransformContext transformContext, Expression expression, ItemType from) {

        if (ItemType.FUNCTION.equals(from)) {

            net.sf.jsqlparser.expression.Function function = (net.sf.jsqlparser.expression.Function)expression;
            List<TransformRule> list = TRANSFORM_RULE.stream()
                    .filter(transformRule ->
                    transformRule.from.sqlEngine.equals(transformContext.from)
                            &&
                    transformRule.to.sqlEngine.equals(transformContext.to)
                            &&
                    function.getName().equals(((FunctionRuleItem)transformRule.from).functionName)
                            &&
                    function.getParameters().getExpressions().size() == ((FunctionRuleItem)transformRule.from).params.size()
                    ).collect(Collectors.toList());
            if (list.size() > 0) {
                return list.get(0);
            }
        }

        return null;
    }

}

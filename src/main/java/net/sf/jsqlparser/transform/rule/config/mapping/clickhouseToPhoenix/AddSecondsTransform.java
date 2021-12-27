package net.sf.jsqlparser.transform.rule.config.mapping.clickhouseToPhoenix;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.transform.rule.config.dialect.clickhouse.AddSeconds;
import net.sf.jsqlparser.transform.rule.manager.TransformRule;

public class AddSecondsTransform {
    public static TransformRule build() {
        TransformRule transformRule = new TransformRule();
        transformRule.from = AddSeconds.getItem();
        transformRule.to = net.sf.jsqlparser.transform.rule.config.dialect.phoenix.Addition.getItem_datetime_number();

        transformRule.transformFunction = (rule, from) -> {

            if(!rule.condition.apply(rule.from)) {
                return from;
            }

            Function function = (Function)from;

            Addition addition = new Addition();
            addition.setLeftExpression(function.getParameters().getExpressions().get(0));

            Expression param = function.getParameters().getExpressions().get(1);
            double value = 0;
            LongValue longValue = null;
            if (param instanceof SignedExpression) {
                SignedExpression signedExpression = (SignedExpression) param;
                longValue = (LongValue) signedExpression.getExpression();
                longValue.setValue(longValue.getValue() * (signedExpression.getSign() == '-' ? -1 : 1));
            } else if (param instanceof LongValue) {
                longValue = (LongValue) param;
            }

            value = longValue.getValue() / 86400d;
            addition.setRightExpression(new DoubleValue(String.valueOf(value)));

            return addition;
        };
        return transformRule;
    }
}

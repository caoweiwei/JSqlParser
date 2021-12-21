package net.sf.jsqlparser.transform.rule;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.transform.rule.item.AbstractRuleItem;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class TransformRule {
    public AbstractRuleItem from;

    public AbstractRuleItem to;

    public Function<AbstractRuleItem, Boolean> condition;

    public BiFunction<TransformRule, Expression, Expression> transformFunction;
}

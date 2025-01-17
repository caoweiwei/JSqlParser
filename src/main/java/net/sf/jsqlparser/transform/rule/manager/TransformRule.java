package net.sf.jsqlparser.transform.rule.manager;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.transform.rule.manager.item.AbstractRuleItem;

import java.util.function.BiFunction;
import java.util.function.Function;

public class TransformRule {
    public AbstractRuleItem from;

    public AbstractRuleItem to;

    public Function<AbstractRuleItem, Boolean> condition = abstractRuleItem -> Boolean.TRUE;

    public BiFunction<TransformRule, Expression, Expression> transformFunction;
}

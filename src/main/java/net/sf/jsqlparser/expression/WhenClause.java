/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.expression;

import net.sf.jsqlparser.parser.ASTNodeAccessImpl;

/**
 * A clause of following syntax: WHEN condition THEN expression. Which is part
 * of a CaseExpression.
 */
public class WhenClause extends ASTNodeAccessImpl implements Expression {

    private Expression whenExpression;
    private Expression thenExpression;

    @Override
    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    @Override
    public Expression acceptAndReturn(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
        return this;
    }
    public Expression getThenExpression() {
        return thenExpression;
    }

    public void setThenExpression(Expression thenExpression) {
        this.thenExpression = thenExpression;
    }

    /**
     * @return Returns the whenExpression.
     */
    public Expression getWhenExpression() {
        return whenExpression;
    }

    /**
     * @param whenExpression The whenExpression to set.
     */
    public void setWhenExpression(Expression whenExpression) {
        this.whenExpression = whenExpression;
    }

    @Override
    public String toString() {
        return "WHEN " + whenExpression + " THEN " + thenExpression;
    }

    public WhenClause withWhenExpression(Expression whenExpression) {
        this.setWhenExpression(whenExpression);
        return this;
    }

    public WhenClause withThenExpression(Expression thenExpression) {
        this.setThenExpression(thenExpression);
        return this;
    }

    public <E extends Expression> E getThenExpression(Class<E> type) {
        return type.cast(getThenExpression());
    }

    public <E extends Expression> E getWhenExpression(Class<E> type) {
        return type.cast(getWhenExpression());
    }
}

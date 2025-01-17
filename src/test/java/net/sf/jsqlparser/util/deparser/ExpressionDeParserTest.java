/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.util.deparser;

import java.util.ArrayList;
import java.util.List;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.KeepExpression;
import net.sf.jsqlparser.expression.WindowElement;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.will;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
public class ExpressionDeParserTest {

    private ExpressionDeParser expressionDeParser;

    @Mock
    private SelectVisitor selectVisitor;

    private StringBuilder buffer;

    @Mock
    private OrderByDeParser orderByDeParser;

    @BeforeEach
    public void setUp() {
        buffer = new StringBuilder();
        expressionDeParser = new ExpressionDeParser(selectVisitor, buffer, orderByDeParser);
    }

    @Test
    public void shouldDeParseSimplestAnalyticExpression() {
        AnalyticExpression analyticExpression = new AnalyticExpression();
        analyticExpression.setName("name");
        expressionDeParser.visit(analyticExpression);
        assertEquals("name() OVER ()", buffer.toString());
    }

    @Test
    public void shouldDeParseAnalyticExpressionWithExpression() {
        AnalyticExpression analyticExpression = new AnalyticExpression();
        Expression expression = mock(Expression.class);

        analyticExpression.setName("name");
        analyticExpression.setExpression(expression);

        will(appendToBuffer("expression")).given(expression).acceptAndReturn(expressionDeParser);

        expressionDeParser.visit(analyticExpression);

        assertEquals("name(expression) OVER ()", buffer.toString());
    }

    @Test
    public void shouldDeParseAnalyticExpressionWithOffset() {
        AnalyticExpression analyticExpression = new AnalyticExpression();
        Expression expression = mock(Expression.class);
        Expression offset = mock(Expression.class);

        analyticExpression.setName("name");
        analyticExpression.setExpression(expression);
        analyticExpression.setOffset(offset);

        will(appendToBuffer("expression")).given(expression).acceptAndReturn(expressionDeParser);
        will(appendToBuffer("offset")).given(offset).acceptAndReturn(expressionDeParser);

        expressionDeParser.visit(analyticExpression);

        assertEquals("name(expression, offset) OVER ()", buffer.toString());
    }

    @Test
    public void shouldDeParseAnalyticExpressionWithDefaultValue() {
        AnalyticExpression analyticExpression = new AnalyticExpression();
        Expression expression = mock(Expression.class);
        Expression offset = mock(Expression.class);
        Expression defaultValue = mock(Expression.class);

        analyticExpression.setName("name");
        analyticExpression.setExpression(expression);
        analyticExpression.setOffset(offset);
        analyticExpression.setDefaultValue(defaultValue);

        will(appendToBuffer("expression")).given(expression).acceptAndReturn(expressionDeParser);
        will(appendToBuffer("offset")).given(offset).acceptAndReturn(expressionDeParser);
        will(appendToBuffer("default value")).given(defaultValue).acceptAndReturn(expressionDeParser);

        expressionDeParser.visit(analyticExpression);

        assertEquals("name(expression, offset, default value) OVER ()", buffer.toString());
    }

    @Test
    public void shouldDeParseAnalyticExpressionWithAllColumns() {
        AnalyticExpression analyticExpression = new AnalyticExpression();

        analyticExpression.setName("name");
        analyticExpression.setAllColumns(true);

        expressionDeParser.visit(analyticExpression);

        assertEquals("name(*) OVER ()", buffer.toString());
    }

    @Test
    public void shouldDeParseComplexAnalyticExpressionWithKeep() {
        AnalyticExpression analyticExpression = new AnalyticExpression();
        KeepExpression keep = mock(KeepExpression.class);

        analyticExpression.setName("name");
        analyticExpression.setKeep(keep);

        will(appendToBuffer("keep")).given(keep).acceptAndReturn(expressionDeParser);

        expressionDeParser.visit(analyticExpression);

        assertEquals("name() keep OVER ()", buffer.toString());
    }

    @Test
    public void shouldDeParseComplexAnalyticExpressionWithPartitionExpressionList() {
        AnalyticExpression analyticExpression = new AnalyticExpression();
        ExpressionList partitionExpressionList = new ExpressionList();
        List<Expression> partitionExpressions = new ArrayList<Expression>();
        Expression partitionExpression1 = mock(Expression.class);
        Expression partitionExpression2 = mock(Expression.class);

        analyticExpression.setName("name");
        analyticExpression.setPartitionExpressionList(partitionExpressionList);
        partitionExpressionList.setExpressions(partitionExpressions);
        partitionExpressions.add(partitionExpression1);
        partitionExpressions.add(partitionExpression2);

        will(appendToBuffer("partition expression 1")).given(partitionExpression1).acceptAndReturn(expressionDeParser);
        will(appendToBuffer("partition expression 2")).given(partitionExpression2).acceptAndReturn(expressionDeParser);

        expressionDeParser.visit(analyticExpression);

        assertEquals("name() OVER (PARTITION BY partition expression 1, partition expression 2 )", buffer.toString());
    }

    @Test
    public void shouldDeParseAnalyticExpressionWithOrderByElements() {
        AnalyticExpression analyticExpression = new AnalyticExpression();
        List<OrderByElement> orderByElements = new ArrayList<OrderByElement>();
        OrderByElement orderByElement1 = mock(OrderByElement.class);
        OrderByElement orderByElement2 = mock(OrderByElement.class);

        analyticExpression.setName("name");
        analyticExpression.setOrderByElements(orderByElements);
        orderByElements.add(orderByElement1);
        orderByElements.add(orderByElement2);

        will(appendToBuffer("order by element 1")).given(orderByDeParser).deParseElement(orderByElement1);
        will(appendToBuffer("order by element 2")).given(orderByDeParser).deParseElement(orderByElement2);

        expressionDeParser.visit(analyticExpression);

        assertEquals("name() OVER (ORDER BY order by element 1, order by element 2)", buffer.toString());
    }

    @Test
    public void shouldDeParseAnalyticExpressionWithWindowElement() {
        AnalyticExpression analyticExpression = new AnalyticExpression();
        List<OrderByElement> orderByElements = new ArrayList<OrderByElement>();
        OrderByElement orderByElement1 = mock(OrderByElement.class);
        OrderByElement orderByElement2 = mock(OrderByElement.class);
        WindowElement windowElement = mock(WindowElement.class);

        analyticExpression.setName("name");
        analyticExpression.setOrderByElements(orderByElements);
        analyticExpression.setWindowElement(windowElement);
        orderByElements.add(orderByElement1);
        orderByElements.add(orderByElement2);

        will(appendToBuffer("order by element 1")).given(orderByDeParser).deParseElement(orderByElement1);
        will(appendToBuffer("order by element 2")).given(orderByDeParser).deParseElement(orderByElement2);
        given(windowElement.toString()).willReturn("window element");

        expressionDeParser.visit(analyticExpression);

        assertEquals("name() OVER (ORDER BY order by element 1, order by element 2 window element)", buffer.toString());
    }

    private Answer<Void> appendToBuffer(final String string) {
        return new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                buffer.append(string);
                return null;
            }
        };
    }
}

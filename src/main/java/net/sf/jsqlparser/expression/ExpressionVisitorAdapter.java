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

import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.conditional.XorExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.ExpressionListItem;
import net.sf.jsqlparser.statement.select.FunctionItem;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.Pivot;
import net.sf.jsqlparser.statement.select.PivotVisitor;
import net.sf.jsqlparser.statement.select.PivotXml;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.UnPivot;
import net.sf.jsqlparser.statement.select.WithItem;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.UncommentedEmptyMethodBody"})
public class ExpressionVisitorAdapter implements ExpressionVisitor, ItemsListVisitor, PivotVisitor, SelectItemVisitor {

    private SelectVisitor selectVisitor;

    public SelectVisitor getSelectVisitor() {
        return selectVisitor;
    }

    public void setSelectVisitor(SelectVisitor selectVisitor) {
        this.selectVisitor = selectVisitor;
    }

    @Override
    public void visit(NullValue value) {

    }

    @Override
    public Expression visit(Function function) {
        if (function.getParameters() != null) {
            function.getParameters().accept(this);
        }
        if (function.getKeep() != null) {
            function.getKeep().accept(this);
        }
        if (function.getOrderByElements() != null) {
            for (OrderByElement orderByElement : function.getOrderByElements()) {

                orderByElement.setExpression(orderByElement.getExpression().acceptAndReturn(this));
            }
        }
        return function;
    }

    @Override
    public void visit(SignedExpression expr) {
        expr.setExpression(expr.getExpression().acceptAndReturn(this));

    }

    @Override
    public void visit(JdbcParameter parameter) {

    }

    @Override
    public void visit(JdbcNamedParameter parameter) {

    }

    @Override
    public void visit(DoubleValue value) {

    }

    @Override
    public void visit(LongValue value) {

    }

    @Override
    public void visit(DateValue value) {

    }

    @Override
    public void visit(TimeValue value) {

    }

    @Override
    public void visit(TimestampValue value) {

    }

    @Override
    public void visit(Parenthesis parenthesis) {
        parenthesis.setExpression(parenthesis.getExpression().acceptAndReturn(this));

    }

    @Override
    public void visit(StringValue value) {

    }

    @Override
    public Expression visit(Addition expr) {
        visitBinaryExpression(expr);
        return expr;
    }

    @Override
    public Expression visit(Division expr) {
        visitBinaryExpression(expr);
        return expr;
    }

    @Override
    public void visit(IntegerDivision expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(Multiplication expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(Subtraction expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(AndExpression expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(OrExpression expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(XorExpression expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(Between expr) {
        expr.setLeftExpression(expr.getLeftExpression().acceptAndReturn(this));
        expr.setBetweenExpressionEnd(expr.getBetweenExpressionStart().acceptAndReturn(this));
        expr.setBetweenExpressionEnd(expr.getBetweenExpressionEnd().acceptAndReturn(this));
    }

    @Override
    public void visit(EqualsTo expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(GreaterThan expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(GreaterThanEquals expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(InExpression expr) {
        if (expr.getLeftExpression() != null) {
            expr.setLeftExpression(expr.getLeftExpression().acceptAndReturn(this));
        }
        if (expr.getRightExpression() != null) {
            expr.setRightExpression(expr.getRightExpression().acceptAndReturn(this));
        } else if (expr.getRightItemsList() != null) {
            expr.getRightItemsList().accept(this);
        }
    }

    @Override
    public void visit(IsNullExpression expr) {

        expr.setLeftExpression(expr.getLeftExpression().acceptAndReturn(this));
    }

    @Override
    public void visit(FullTextSearch expr) {
        for (Column col : expr.getMatchColumns()) {
            col.acceptAndReturn(this);
        }
    }

    @Override
    public void visit(IsBooleanExpression expr) {
        expr.setLeftExpression(expr.getLeftExpression().acceptAndReturn(this));
    }

    @Override
    public void visit(LikeExpression expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(MinorThan expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(MinorThanEquals expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(NotEqualsTo expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(Column column) {

    }

    @Override
    public void visit(SubSelect subSelect) {
        if (selectVisitor != null) {
            if (subSelect.getWithItemsList() != null) {
                for (WithItem item : subSelect.getWithItemsList()) {
                    item.accept(selectVisitor);
                }
            }
            subSelect.getSelectBody().accept(selectVisitor);
        }
        if (subSelect.getPivot() != null) {
            subSelect.getPivot().accept(this);
        }
    }

    @Override
    public void visit(CaseExpression expr) {
        if (expr.getSwitchExpression() != null) {
            expr.setSwitchExpression(expr.getSwitchExpression().acceptAndReturn(this));
        }
        for (Expression x : expr.getWhenClauses()) {
            x.acceptAndReturn(this);
        }
        if (expr.getElseExpression() != null) {
            expr.setElseExpression(expr.getElseExpression().acceptAndReturn(this));

        }
    }

    @Override
    public void visit(WhenClause expr) {
        expr.setWhenExpression(expr.getWhenExpression().acceptAndReturn(this));
        expr.setThenExpression(expr.getThenExpression().acceptAndReturn(this));
    }

    @Override
    public void visit(ExistsExpression expr) {
        expr.setRightExpression(expr.getRightExpression().acceptAndReturn(this));
    }
   
    @Override
    public void visit(AnyComparisonExpression expr) {

    }

    @Override
    public void visit(Concat expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(Matches expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(BitwiseAnd expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(BitwiseOr expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(BitwiseXor expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public Expression visit(CastExpression expr) {
        expr.setLeftExpression(expr.getLeftExpression().acceptAndReturn(this));

        return expr;
    }

    @Override
    public void visit(TryCastExpression expr) {
        expr.setLeftExpression(expr.getLeftExpression().acceptAndReturn(this));
    }

    @Override
    public void visit(Modulo expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(AnalyticExpression expr) {
        if (expr.getExpression() != null) {
            expr.getExpression().acceptAndReturn(this);
        }
        if (expr.getDefaultValue() != null) {
            expr.getDefaultValue().acceptAndReturn(this);
        }
        if (expr.getOffset() != null) {
            expr.getOffset().acceptAndReturn(this);
        }
        if (expr.getKeep() != null) {
            expr.getKeep().acceptAndReturn(this);
        }
        for (OrderByElement element : expr.getOrderByElements()) {
            element.getExpression().acceptAndReturn(this);
        }

        if (expr.getWindowElement() != null) {
            expr.getWindowElement().getRange().getStart().getExpression().acceptAndReturn(this);
            expr.getWindowElement().getRange().getEnd().getExpression().acceptAndReturn(this);
            expr.getWindowElement().getOffset().getExpression().acceptAndReturn(this);
        }
    }

    @Override
    public void visit(ExtractExpression expr) {
        expr.getExpression().acceptAndReturn(this);
    }

    @Override
    public void visit(IntervalExpression expr) {
    }

    @Override
    public void visit(OracleHierarchicalExpression expr) {
        expr.getConnectExpression().acceptAndReturn(this);
        expr.getStartExpression().acceptAndReturn(this);
    }

    @Override
    public void visit(RegExpMatchOperator expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(ExpressionList expressionList) {
        for (Expression expr : expressionList.getExpressions()) {
            expr = expr.acceptAndReturn(this);
        }
    }

    @Override
    public void visit(NamedExpressionList namedExpressionList) {
        for (Expression expr : namedExpressionList.getExpressions()) {
            expr = expr.acceptAndReturn(this);
        }
    }

    @Override
    public void visit(MultiExpressionList multiExprList) {
        for (ExpressionList list : multiExprList.getExprList()) {
            visit(list);
        }
    }

    @Override
    public void visit(NotExpression notExpr) {
        notExpr.setExpression(notExpr.getExpression().acceptAndReturn(this));

    }

    @Override
    public void visit(BitwiseRightShift expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(BitwiseLeftShift expr) {
        visitBinaryExpression(expr);
    }

    protected void visitBinaryExpression(BinaryExpression expr) {
        expr.setLeftExpression(expr.getLeftExpression().acceptAndReturn(this));
        expr.setRightExpression(expr.getRightExpression().acceptAndReturn(this));
    }

    @Override
    public void visit(JsonExpression jsonExpr) {
        jsonExpr.getExpression().acceptAndReturn(this);
    }

    @Override
    public void visit(JsonOperator expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(RegExpMySQLOperator expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(UserVariable var) {

    }

    @Override
    public void visit(NumericBind bind) {

    }

    @Override
    public void visit(KeepExpression expr) {
        for (OrderByElement element : expr.getOrderByElements()) {
            element.setExpression(element.getExpression().acceptAndReturn(this));

        }
    }

    @Override
    public void visit(MySQLGroupConcat groupConcat) {
        for (Expression expr : groupConcat.getExpressionList().getExpressions()) {
            expr = expr.acceptAndReturn(this);
        }
        if (groupConcat.getOrderByElements() != null) {
            for (OrderByElement element : groupConcat.getOrderByElements()) {
                element.setExpression(element.getExpression().acceptAndReturn(this));

            }
        }
    }

    @Override
    public void visit(ValueListExpression valueListExpression) {
        for (Expression expr : valueListExpression.getExpressionList().getExpressions()) {
            expr = expr.acceptAndReturn(this);
        }
    }

    @Override
    public void visit(Pivot pivot) {
        for (FunctionItem item : pivot.getFunctionItems()) {
            item.getFunction().acceptAndReturn(this);
        }
        for (Column col : pivot.getForColumns()) {
            col.acceptAndReturn(this);
        }
        if (pivot.getSingleInItems() != null) {
            for (SelectExpressionItem item : pivot.getSingleInItems()) {
                item.accept(this);
            }
        }

        if (pivot.getMultiInItems() != null) {
            for (ExpressionListItem item : pivot.getMultiInItems()) {
                item.getExpressionList().accept(this);
            }
        }
    }

    @Override
    public void visit(PivotXml pivot) {
        for (FunctionItem item : pivot.getFunctionItems()) {
            item.getFunction().acceptAndReturn(this);
        }
        for (Column col : pivot.getForColumns()) {
            col.acceptAndReturn(this);
        }
        if (pivot.getInSelect() != null && selectVisitor != null) {
            pivot.getInSelect().accept(selectVisitor);
        }
    }

    @Override
    public void visit(UnPivot unpivot) {
        unpivot.accept(this);
    }

    @Override
    public void visit(AllColumns allColumns) {
        allColumns.acceptAndReturn((ExpressionVisitor) this);
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
        allTableColumns.acceptAndReturn((ExpressionVisitor) this);
    }

    @Override
    public void visit(AllValue allValue) {
    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        selectExpressionItem.setExpression(selectExpressionItem.getExpression().acceptAndReturn(this));

    }

    @Override
    public void visit(RowConstructor rowConstructor) {
        if (rowConstructor.getColumnDefinitions().isEmpty()) {
            for (Expression expression: rowConstructor.getExprList().getExpressions()) {
                expression = expression.acceptAndReturn(this);
              }
        } else {
            for (ColumnDefinition columnDefinition : rowConstructor.getColumnDefinitions()) {
                columnDefinition.accept(this);
            }
        }
    }

    @Override
    public void visit(RowGetExpression rowGetExpression) {
        rowGetExpression.setExpression(rowGetExpression.getExpression().acceptAndReturn(this));
    }

    @Override
    public void visit(HexValue hexValue) {

    }

    @Override
    public void visit(OracleHint hint) {

    }

    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {

    }

    @Override
    public void visit(DateTimeLiteralExpression literal) {
    }

    @Override
    public void visit(NextValExpression nextVal) {
    }

    @Override
    public void visit(CollateExpression col) {
        col.setLeftExpression(col.getLeftExpression().acceptAndReturn(this));

    }

    @Override
    public void visit(SimilarToExpression expr) {
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(ArrayExpression array) {
        array.getObjExpression().acceptAndReturn(this);
        if (array.getIndexExpression() != null) {
            array.getIndexExpression().acceptAndReturn(this);
        }
        if (array.getStartIndexExpression() != null) {
            array.getStartIndexExpression().acceptAndReturn(this);
        }
        if (array.getStopIndexExpression() != null) {
            array.getStopIndexExpression().acceptAndReturn(this);
        }
    }

    @Override
    public void visit(ArrayConstructor aThis) {
        for (Expression expression : aThis.getExpressions()) {
            expression = expression.acceptAndReturn(this);
        }
    }

    @Override
    public void visit(VariableAssignment var) {
        var.getVariable().acceptAndReturn(this);
        var.setExpression(var.getExpression().acceptAndReturn(this));

    }

    @Override
    public void visit(XMLSerializeExpr expr) {
        expr.getExpression().acceptAndReturn(this);
        for (OrderByElement elm : expr.getOrderByElements()) {
            elm.getExpression().acceptAndReturn(this);
        }
    }

    @Override
    public void visit(TimezoneExpression expr) {
        expr.setLeftExpression(expr.getLeftExpression().acceptAndReturn(this));

    }

    @Override
    public void visit(JsonAggregateFunction expression) {
        Expression expr = expression.getExpression();
        if (expr!=null) {
            expr.acceptAndReturn(this);
        }
        
        expr = expression.getFilterExpression();
        if (expr!=null) {
            expr.acceptAndReturn(this);
        }
    }

    @Override
    public void visit(JsonFunction expression) {
        for (JsonFunctionExpression expr: expression.getExpressions()) {
            expr.getExpression().acceptAndReturn(this);
        }
    }

    @Override
    public void visit(ConnectByRootOperator connectByRootOperator) {
        connectByRootOperator.getColumn().acceptAndReturn(this);
    }
    
    @Override
    public void visit(OracleNamedFunctionParameter oracleNamedFunctionParameter) {
        oracleNamedFunctionParameter.getExpression().acceptAndReturn(this);
    }
    
    public void visit(ColumnDefinition columnDefinition) {
       columnDefinition.accept(this);
     }
}

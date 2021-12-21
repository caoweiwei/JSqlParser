package net.sf.jsqlparser.transform;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.transform.context.TransformContext;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.RuleMappingManager;
import net.sf.jsqlparser.util.deparser.SelectDeParser;

public class SQLTransform {

    public static String transform(SQLEngine from, SQLEngine to, String sql) throws JSQLParserException {
        Statement stmt = CCJSqlParserUtil.parse(sql);
        Select select = (Select) stmt;
        TransformContext transformContext = new TransformContext();
        transformContext.from = from;
        transformContext.to = to;
        SelectDeParser selectDeParser = new SelectDeParser(
                new TransformExpressionVisitor(new RuleMappingManager(), transformContext),
                new StringBuilder());
        select.getSelectBody().accept(selectDeParser);

        return select.toString();
    }

    public static void main(String[] args) throws JSQLParserException {
        System.out.println(
                transform(SQLEngine.CLICKHOUSE, SQLEngine.PHOENIX,
"select toint32ornull('1') from (select toint32ornull('1') from dual) where toint32ornull('1') = 1 and toint32ornull('1') order by uid desc")
        );
    }
}

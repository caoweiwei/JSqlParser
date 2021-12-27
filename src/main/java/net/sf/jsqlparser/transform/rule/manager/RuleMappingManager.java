package net.sf.jsqlparser.transform.rule.manager;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.transform.ExpressionAndTypeMapping;
import net.sf.jsqlparser.transform.context.TransformContext;
import net.sf.jsqlparser.transform.model.ExpressionType;
import net.sf.jsqlparser.transform.rule.manager.item.*;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class RuleMappingManager {
    private static final List<TransformRule> TRANSFORM_RULES = new ArrayList<>();

    static {
        RuleItemCollector.init();
        RuleMappingManager.collectRule();
    }

    public static void collectRule() {
        List<ClassLoader> classLoadersList = Lists.newArrayList(ClasspathHelper.contextClassLoader(), ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().includePackage("net.sf.jsqlparser.transform.rule.config.mapping.*")));

        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);

        classes.forEach(clazz -> {
            try {
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.getName().startsWith("build")) {
                        TRANSFORM_RULES.add((TransformRule)method.invoke(null));
                    }
                }
            } catch (Exception e) {
                log.error("clazz:{}, build rule invoke error.", clazz.getSimpleName());
            }
        });
    }

    public TransformRule getRule(TransformContext transformContext, Function function) {
        List<TransformRule> ruleList = getTransformRules().stream()
                .filter(transformRule -> transformRule.from.itemType.equals(ItemType.FUNCTION))
                .filter(transformRule -> function.getName().equalsIgnoreCase(((FunctionRuleItem)transformRule.from).functionName))
                .filter(transformRule -> transformRule.from.sqlEngine.equals(transformContext.from))
                .filter(transformRule -> transformRule.to.sqlEngine.equals(transformContext.to))
                .map(transformRule -> {
                    List<ExpressionType> expressionTypeList = function.getParameters().getExpressions()
                            .stream()
                            .map(expr -> ExpressionAndTypeMapping.getExpressionReturnType(expr, transformContext))
                            .collect(Collectors.toList());

                    if (((FunctionRuleItem)transformRule.from).params
                            .stream()
                            .map(p -> p.expressionType)
                            .collect(Collectors.toList())
                            .equals(expressionTypeList)) {
                        return transformRule;
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());

        return ruleList.size() > 0 ? ruleList.get(0) : null;
    }

    public TransformRule getRule(TransformContext transformContext, Addition addition) {
        ExpressionType leftExpressionType = ExpressionAndTypeMapping.getExpressionReturnType(addition.getLeftExpression(), transformContext);
        ExpressionType rightExpressionType = ExpressionAndTypeMapping.getExpressionReturnType(addition.getLeftExpression(), transformContext);

        List<TransformRule> ruleList = getTransformRules().stream()
                .filter(transformRule -> transformRule.from.itemType.equals(ItemType.ADDITION))
                .filter(transformRule -> transformRule.from.sqlEngine.equals(transformContext.from))
                .filter(transformRule -> transformRule.to.sqlEngine.equals(transformContext.to))
                .filter(transformRule -> ((AdditionRuleItem)transformRule.from).leftType.equals(leftExpressionType))
                .filter(transformRule -> ((AdditionRuleItem)transformRule.from).rightType.equals(rightExpressionType))
                .collect(Collectors.toList());

        return ruleList.size() > 0 ? ruleList.get(0) : null;
    }

    public TransformRule getRule(TransformContext transformContext, Division division) {
        ExpressionType leftExpressionType = ExpressionAndTypeMapping.getExpressionReturnType(division.getLeftExpression(), transformContext);
        ExpressionType rightExpressionType = ExpressionAndTypeMapping.getExpressionReturnType(division.getLeftExpression(), transformContext);

        List<TransformRule> ruleList = getTransformRules().stream()
                .filter(transformRule -> transformRule.from.itemType.equals(ItemType.DIVISION))
                .filter(transformRule -> transformRule.from.sqlEngine.equals(transformContext.from))
                .filter(transformRule -> transformRule.to.sqlEngine.equals(transformContext.to))
                .filter(transformRule -> ((AdditionRuleItem)transformRule.from).leftType.equals(leftExpressionType))
                .filter(transformRule -> ((AdditionRuleItem)transformRule.from).rightType.equals(rightExpressionType))
                .collect(Collectors.toList());

        return ruleList.size() > 0 ? ruleList.get(0) : null;
    }

    public TransformRule getRule(TransformContext transformContext, CastExpression castExpression) {
        ExpressionType leftExpressionType = ExpressionAndTypeMapping.getExpressionReturnType(castExpression.getLeftExpression(), transformContext);
        List<TransformRule> ruleList = getTransformRules().stream()
                .filter(transformRule -> transformRule.from.itemType.equals(ItemType.CAST))
                .filter(transformRule -> transformRule.from.sqlEngine.equals(transformContext.from))
                .filter(transformRule -> transformRule.to.sqlEngine.equals(transformContext.to))
                .filter(transformRule -> ((CastRuleItem)transformRule.from).leftType.equals(leftExpressionType))
                .filter(transformRule -> ((CastRuleItem)transformRule.from).toType.equals(castExpression.getType().getDataType()))
                .collect(Collectors.toList());

        return ruleList.size() > 0 ? ruleList.get(0) : null;
    }

    public TransformRule getRule(TransformContext transformContext, TimeKeyExpression timeKeyExpression) {
        List<TransformRule> ruleList = getTransformRules().stream()
                .filter(transformRule -> transformRule.from.itemType.equals(ItemType.TIMEKEY))
                .filter(transformRule -> transformRule.from.sqlEngine.equals(transformContext.from))
                .filter(transformRule -> transformRule.to.sqlEngine.equals(transformContext.to))
                .filter(transformRule -> ((TimeKeyRuleItem)transformRule.from).timekeyName.equals(timeKeyExpression.getStringValue()))
                .collect(Collectors.toList());

        return ruleList.size() > 0 ? ruleList.get(0) : null;
    }

    private static List<TransformRule> getTransformRules() {
        return TRANSFORM_RULES;
    }
}

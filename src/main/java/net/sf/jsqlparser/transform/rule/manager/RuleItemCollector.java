package net.sf.jsqlparser.transform.rule.manager;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.transform.model.SQLEngine;
import net.sf.jsqlparser.transform.rule.manager.item.AbstractRuleItem;
import org.reflections.Reflections;
import org.reflections.scanners.*;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class RuleItemCollector {
    private static Map<SQLEngine, List<AbstractRuleItem>> sqlRuleMap = new HashMap<>();

    public static Map<SQLEngine, List<AbstractRuleItem>> getSqlRuleMap() {
        return sqlRuleMap;
    }

    public static void init() {
        List<ClassLoader> classLoadersList = Lists.newArrayList(ClasspathHelper.contextClassLoader(), ClasspathHelper.staticClassLoader());

        for (SQLEngine sqlEngine : SQLEngine.values()) {

            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .setScanners(new SubTypesScanner(false), new ResourcesScanner())
                    .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                    .filterInputsBy(new FilterBuilder().includePackage(String.format("net.sf.jsqlparser.transform.rule.mapping.dialect.%s", sqlEngine.name().toLowerCase()))));

            Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);

            List<AbstractRuleItem> ruleItemList = Lists.newArrayList();
            classes.forEach(clazz -> {
                try {
                    Method[] methods = clazz.getMethods();
                    for (Method method : methods) {
                        if (method.getName().startsWith("getItem")) {
                            ruleItemList.add((AbstractRuleItem)method.invoke(null));
                        }
                    }
                } catch (Exception e) {
                    log.error("SQLEngine:{}, clazz:{}, getItem invoke error.", sqlEngine.name(), clazz.getSimpleName());
                }
            });
            sqlRuleMap.put(sqlEngine, ruleItemList);
        }
    }
}

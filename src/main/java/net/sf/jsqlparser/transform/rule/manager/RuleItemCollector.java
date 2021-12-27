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
import java.util.stream.Collectors;

@Slf4j
public class RuleItemCollector {
    private static List<AbstractRuleItem> abstractRuleItemList = Lists.newArrayList();

    public static Map<SQLEngine, List<AbstractRuleItem>> getSqlRuleMap() {
        return abstractRuleItemList
                .stream()
                .collect(Collectors.groupingBy(abstractRuleItem -> abstractRuleItem.sqlEngine));
    }

    public static void init() {
        List<ClassLoader> classLoadersList = Lists.newArrayList(ClasspathHelper.contextClassLoader(), ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().includePackage("net.sf.jsqlparser.transform.rule.config.dialect.*")));

        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);

        classes.forEach(clazz -> {
            try {
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.getName().startsWith("getItem")) {
                        abstractRuleItemList.add((AbstractRuleItem)method.invoke(null));
                    }
                }
            } catch (Exception e) {
                log.error("clazz:{}, getItem invoke error.", clazz.getSimpleName());
            }
        });
    }
}

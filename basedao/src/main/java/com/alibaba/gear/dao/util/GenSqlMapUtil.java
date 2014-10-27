package com.alibaba.gear.dao.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/10/10
 * Time: 下午4:31
 */
public class GenSqlMapUtil {

    private static Velocity ve;

    /**
     * 获取所有getXXX的方法
     * * @param path
     *
     * @param clasz
     */
    public static List<String> getGetters(Class clasz) {
        if (clasz == null) {
            return null;
        }
        Method[] ms = clasz.getMethods();
        List<String> list = new ArrayList();
        if (ms != null) {
            for (Method m : ms) {
                String methodName = m.getName();
                if (methodName.length() > 4 && methodName.startsWith("get") && !methodName.equals("getClass")) {
                    String s = "" + m.getName().charAt(3);
                    if (s.equals(s.toUpperCase())) {
                        list.add(s.toLowerCase() + methodName.substring(4));
                    }
                }
            }
            return list;
        }
        return null;
    }

    public static void initVe() {
        // 初始化并取得Velocity引擎
        ve = new Velocity();
        ve.setProperty(Velocity.RESOURCE_LOADER, "classpath");
        ve.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        Velocity.setProperty(
                "classpath." + Velocity.RESOURCE_LOADER + ".class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogChute");
        try {
            ve.init();
        } catch (Exception e) {
            System.exit(-1);
        }
    }

    static {
        initVe();
    }


    public final static String getColumnName(String s) {
        if (StringUtils.isBlank(s)) {
            return "";
        }
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 || !String.valueOf(s.charAt(i)).toUpperCase().equals(String.valueOf(s.charAt(i)))) {
                sb.append(s.charAt(i));
            } else {
                sb.append("_").append(String.valueOf(s.charAt(i)).toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * 生成sqlmap文件，
     * @param path
     * @param clasz
     * @param tableName
     */
    public static void genXML(String path, Class clasz, String tableName) {
        if (StringUtils.isBlank(path) || clasz == null) {
            return;
        }
        String classSimpleNameWithLowerCase = new String(clasz.getSimpleName().charAt(0) + "").toLowerCase() + clasz.getSimpleName().substring(1);
        String classSimpleName = clasz.getSimpleName();
        List<String> getters = getGetters(clasz);


        Context context = new VelocityContext();
        context.put("csnlc", classSimpleNameWithLowerCase);
        context.put("csn", classSimpleName);
        context.put("cn", clasz.getName());
        context.put("getters", getters);
        context.put("tn", StringUtils.isBlank(tableName) ? getColumnName(classSimpleName) : tableName);
        Map m_getters = new HashMap<String, String>();
        for (String s : getters) {
            m_getters.put(s, getColumnName(s));
        }
        context.put("m_getters", m_getters);


        FileWriter fw = null;
        try {
            File file = new File(path);
            fw = new FileWriter(file);
            Template t = ve.getTemplate("sql.vm");
            t.merge(context, fw);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * 生成sqlmap文件，并支持一般的featuresupport
     * @param path
     * @param clasz
     * @param tableName
     */
    public static void genXMLWithFeature(String path, Class clasz, String tableName) {
        if (StringUtils.isBlank(path) || clasz == null) {
            return;
        }

        String classSimpleNameWithLowerCase = new String(clasz.getSimpleName().charAt(0) + "").toLowerCase() + clasz.getSimpleName().substring(1);
        String classSimpleName = clasz.getSimpleName();
        List<String> getters = getGettersWithFeature(clasz);

        Context context = new VelocityContext();
        context.put("csnlc", classSimpleNameWithLowerCase);
        context.put("csn", classSimpleName);
        context.put("cn", clasz.getName());
        context.put("getters", getters);
        context.put("tn", StringUtils.isBlank(tableName) ? getColumnName(classSimpleName) : tableName);
        Map m_getters = new HashMap<String, String>();
        for (String s : getters) {
            m_getters.put(s, getColumnName(s));
        }
        context.put("m_getters", m_getters);

        FileWriter fw = null;
        try {
            File file = new File(path);
            fw = new FileWriter(file);
            Template t = ve.getTemplate("sql.vm");
            t.merge(context, fw);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private static List<String> getGettersWithFeature(Class clasz) {
        if (clasz == null) {
            return null;
        }
        Method[] ms = clasz.getMethods();
        List<String> list = new ArrayList();
        if (ms != null) {
            for (Method m : ms) {
                String methodName = m.getName();
                if (methodName.length() > 4 && methodName.startsWith("get") && !methodName.equals("getClass")) {
                    if (methodName.equalsIgnoreCase("getFeatureMap") || methodName.equalsIgnoreCase("getFeaturesAsMap")
                            || methodName.equalsIgnoreCase("getFeature")) {
                        continue;
                    }
                    String s = "" + m.getName().charAt(3);
                    if (s.equals(s.toUpperCase())) {
                        list.add(s.toLowerCase() + methodName.substring(4));
                    }
                }
            }
            return list;
        }
        return null;
    }

}

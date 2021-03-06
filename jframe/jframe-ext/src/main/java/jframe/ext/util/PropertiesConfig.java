/**
 * 
 */
package jframe.ext.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.conf.VarHandler;
import jframe.ext.dispatch.MqConf;

/**
 * <p>
 * Feature:
 * <li>属性分组,自定义组ID和默认组</li>
 * <li>属性值支持jframe配置变量${conf.key}</li>
 * <li>属性查询优先级,自定义ID->默认组</li>
 * </p>
 * 
 * @author dzh
 * @date Nov 17, 2014 4:52:33 PM
 * @since 1.0
 */
public class PropertiesConfig {

    static final Logger LOG = LoggerFactory.getLogger(MqConf.class);

    boolean init = false;

    public synchronized void init(String file) throws Exception {
        if (init)
            return;

        try {
            init(new FileInputStream(file));
        } catch (Exception e) {
            throw e;
        }
        init = true;
    }

    private Map<String, String> conf;

    public synchronized void init(InputStream is) throws Exception {
        if (is == null || init)
            return;

        conf = new HashMap<String, String>();
        try {
            Properties p = new Properties();
            p.load(is);

            for (Object o : p.keySet()) {
                conf.put(o.toString().trim(), p.getProperty(o.toString(), "").trim());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            is.close();
        }
    }

    public synchronized void replace(VarHandler vh) {
        if (conf == null)
            return;
        for (String key : conf.keySet()) {
            conf.put(key, vh.replace(conf.get(key)));
        }
    }

    public synchronized String[] getGroupIds() {
        String groups = conf.get("group.id");
        if (groups == null)
            return new String[0];
        return groups.split("\\s+");
    }

    /**
     * 
     * @param group
     * @param key
     * @return "" if not matched value or value
     */
    public synchronized String getConf(String group, String key) {
        if (conf == null)
            return "";
        String val = null;
        if (group != null) {
            val = conf.get("@" + group + "." + key);
        }

        if (val == null) {
            val = conf.get(key);
        }
        return val == null ? "" : val;
    }

    /**
     * 
     * @param group
     * @param key
     * @param defVal
     * @return defVal if not matched value or value
     */
    public synchronized String getConf(String group, String key, String defVal) {
        if (conf == null)
            return defVal;
        String val = null;
        if (group != null) {
            val = conf.get("@" + group + "." + key);
        }

        if (val == null) {
            val = conf.get(key);
        }
        return val == null ? defVal : val;
    }

    public synchronized Properties clone2Properties() {
        Properties p = new Properties();
        if (conf == null)
            return p;
        for (String k : conf.keySet()) {
            p.setProperty(k, conf.get(k));
        }
        return p;
    }
}

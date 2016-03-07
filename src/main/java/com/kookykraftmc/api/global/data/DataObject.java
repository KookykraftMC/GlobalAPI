package com.kookykraftmc.api.global.data;

import com.google.common.base.Joiner;
import com.kookykraftmc.api.global.plugin.KookyHubObject;
import com.kookykraftmc.api.global.sql.SQLConnection;
import com.kookykraftmc.api.global.sql.SQLUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataObject {

    public static Map<String, String> loadData(ResultSet set) throws SQLException {
        Map<String, String> datamap = new HashMap<>();
        while (set.next()) {
            datamap.put(set.getString("key"), set.getString("value"));
        }
        set.close();
        return datamap;
    }

    private Map<String, String> data;

    public DataObject(Map<String, String> data) {
        this.data = data;
    }

    public Map<String, String> getRaw() {
        return data;
    }

    public Boolean getBoolean(String indentifier) throws InvalidBaseException {
        String s = getString(indentifier);
        return Boolean.parseBoolean(s);
    }

    public Number getNumber(String indentifier) throws InvalidBaseException {
        String s = getString(indentifier);
        try {
            return NumberFormat.getInstance().parse(s);
        } catch (NumberFormatException ex) {
            throw new InvalidBaseException(ex);
        } catch (ParseException ex) {
            throw new InvalidBaseException(ex);
        }
    }

    public String getString(String indentifier) throws InvalidBaseException {
        check(indentifier);
        return getRaw().get(indentifier);
    }


    protected void check(String indentifier) throws InvalidBaseException {
        if (!getRaw().containsKey(indentifier)) {
            throw new InvalidBaseException("Could not find raw data: " + indentifier);
        }
    }


    @SuppressWarnings("unchecked")
    public void save(String table, String var, Object object) throws SQLException, ClassNotFoundException {
        SQLConnection connection = KookyHubObject.getInstance().getConnection();
        connection.executeSQL("DELETE FROM `" + table + "` WHERE " + new SQLUtil.WhereVar(var, object).getWhere());
        String preset = "INSERT INTO `" + table + "` (`" + var + "`,`key`,`value`) VALUES ";
        Set<String> stringSet = new HashSet<>();
        for (Map.Entry<String, String> stringEntry : getRaw().entrySet()) {
            stringSet.add("('" + object + "','" + stringEntry.getKey() + "','" + stringEntry.getValue() + "')");
        }
        String insert = Joiner.on(",").join(stringSet) + ";";
        if (stringSet.size() > 0) {
            connection.executeSQL(preset + insert);
        }
    }

    public void set(String s, String s2) {
        if (s2 == null) {
            getRaw().remove(s);
        } else {
            getRaw().put(s, s2);
        }
    }

    public void set(String s, Integer i) {
        if (i == null) {
            getRaw().remove(s);
        } else {
            getRaw().put(s, String.valueOf(i));
        }
    }

    public void set(String s, Boolean b) {
        if (b == null) {
            getRaw().remove(s);
        } else {
            getRaw().put(s, String.valueOf(b));
        }
    }

}

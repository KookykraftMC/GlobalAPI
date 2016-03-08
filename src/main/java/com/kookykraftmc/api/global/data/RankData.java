package com.kookykraftmc.api.global.data;

import java.util.Map;

public class RankData extends DataObject {

    public static final String PREFIX = "prefix", SUFFIX = "suffix", INHERITANCE = "inherit";

    public static String table = "ranks";

    public RankData(Map<String, String> data) {
        super(data);
    }

}

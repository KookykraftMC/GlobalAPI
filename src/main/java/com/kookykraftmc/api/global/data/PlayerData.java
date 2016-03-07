package com.kookykraftmc.api.global.data;

import java.util.*;

public class PlayerData extends DataObject {

    public static final String NICKNAME = "nickname", RANKBASE = "rank", STATSBASE = "stats", FRIENDSBASE = "friends", ITEMSBASE = "items", PACKS = "packs", KITBASE = "kits", CURRENCYBASE = "currency", NAME = "name", TOKENS = "tokens", KEYS = "keys", MAINRANK = RANKBASE + ".mainrank", SUBRANKS = RANKBASE + ".subranks", FRIENDSLIST = FRIENDSBASE + ".current", FRIENDINCOMINGRQ = FRIENDSBASE + ".incoming",PETNAME = "petname",GADGETS = "gadgets";

    public static String table = "playerdata";

    public PlayerData(Map<String, String> loaded) {
        super(loaded);
    }

    public UUID[] getUUIDList(String indentifier) throws InvalidBaseException {
        String[] list = getString(indentifier).split(",");
        Set<UUID> uuids = new HashSet<>();
        for (String s : list) {
            try {
                uuids.add(UUID.fromString(s));
            } catch (Exception ex) {
            }
        }
        return uuids.toArray(new UUID[uuids.size()]);
    }

    public Map<String, Integer> getMapRaw(String indentifier) {
        Map<String, Integer> map = new HashMap<>();
        for (Object o : getRaw().keySet()) {
            if (o instanceof String) {
                String s = (String) o;
                if (s.startsWith(indentifier)) {
                    int i;
                    try {
                        i = getNumber(s).intValue();
                    } catch (InvalidBaseException ex) {
                        continue;
                    }
                    map.put(s.replace(indentifier + ".", ""), i);
                }
            }
        }
        return map;
    }

    public Map<String, Integer> getMap(String id, String uid) {
        String indentifier = id + "." + uid.toLowerCase();
        return getMapRaw(indentifier);
    }

}

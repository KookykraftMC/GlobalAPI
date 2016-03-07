package com.kookykraftmc.api.global.ranks;

import com.google.common.collect.Iterables;
import com.kookykraftmc.api.global.data.InvalidBaseException;
import com.kookykraftmc.api.global.data.RankData;
import de.mickare.xserver.util.ChatColor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Rank {

    public static Set<Rank> getRanks() {
        return ranks;
    }

    public static Rank getDefault() {
        for (Rank r : getRanks()) {
            if (r.isDefault()) {
                return r;
            }
        }
        Rank r;
        return (r = getRank("default")) != null ? r : ranks.size() > 0 ? Iterables.get(ranks, 0) : null;
    }

    public static Rank getRank(String s) {
        for (Rank r : getRanks()) {
            if (r.getName().equalsIgnoreCase(s)) {
                return r;
            }
        }
        return null;
    }

    public static void loadRank(String name, Map<String, String> map) {
        Rank r;
        if ((r = getRank(name)) != null) {
            if (map == null) {
                ranks.remove(r);
            } else {
                r.getData().getRaw().clear();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    r.getData().getRaw().put(entry.getKey(), entry.getValue());
                }
            }
        }
        ranks.add(new Rank(name, new RankData(map)));
    }

    private static boolean isAuthorized(Rank r, String indentifier) {
        try {
            return isAuthorizedPrimative(r, indentifier).decision();
        } catch (UnsupportedOperationException e1) {
            try {
                return isAuthorizedPrimative(r, "*").decision();
            } catch (UnsupportedOperationException e2) {
                return r.getInheritance() != null && isAuthorized(r, indentifier);
            }
        }
    }

    private static Decision isAuthorizedPrimative(Rank r, String indentifier) {
        Decision d;
        try {
            d = Decision.getDecision(r.getData().getBoolean(indentifier));
        } catch (Exception e) {
            d = Decision.CONTINUE;
        }
        return d;
    }

    private static Set<Rank> ranks = new HashSet<>();
    private RankData data;
    private String name;

    public Rank(String name, RankData data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        try {
            return ChatColor.translateAlternateColorCodes('&', getData().getString(RankData.PREFIX));
        } catch (InvalidBaseException ex) {
            return "";
        }
    }

    public void setPrefix(String s) {
        getData().set(RankData.PREFIX, s);
    }

    public String getSuffix() {
        try {
            return ChatColor.translateAlternateColorCodes('&', getData().getString(RankData.SUFFIX));
        } catch (InvalidBaseException ex) {
            return "";
        }
    }

    public void setSuffix(String s) {
        getData().set(RankData.SUFFIX, s);
    }

    protected String getInheritanceString() {
        try {
            return getData().getString(RankData.INHERITANCE);
        } catch (InvalidBaseException ex) {
            return null;
        }
    }

    public boolean isDefault() {
        try {
            return getData().getBoolean("default");
        } catch (InvalidBaseException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    public Rank getInheritance() {
        return getRank(getInheritanceString());
    }

    public void setInheritance(Rank r) {
        getData().set(RankData.INHERITANCE, r == null ? null : r.getName());
    }

    public boolean isAuthorized(String indentifier) {
        return isAuthorized(this, indentifier);
    }

    public RankData getData() {
        return data;
    }

    enum Decision {
        CONTINUE(), TRUE(), FALSE();

        static Decision getDecision(boolean b) {
            if (b) {
                return TRUE;
            }
            return FALSE;
        }

        boolean decision() {
            if (this == TRUE) {
                return true;
            }
            if (this == FALSE) {
                return false;
            }
            throw new UnsupportedOperationException();
        }
    }

}

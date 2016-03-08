package com.kookykraftmc.api.global.data;

import java.util.Map;

public class PunishmentData extends DataObject {

    public static final String BAN = "ban", MUTE = "mute", TEMPBAN = "tempban", TEMPMUTE = "tempmute", IPBAN = "ipban", REASON = "reason";

    public static String table = "punishments";

    public PunishmentData(Map<String, String> data) {
        super(data);
    }

}

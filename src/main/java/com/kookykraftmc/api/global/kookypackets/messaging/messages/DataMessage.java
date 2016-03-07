package com.kookykraftmc.api.global.kookypackets.messaging.messages;

import com.kookykraftmc.api.global.kookypackets.messaging.IPluginMessage;

import java.util.Map;

public interface DataMessage extends IPluginMessage {

    Map<String, String> getData();

}

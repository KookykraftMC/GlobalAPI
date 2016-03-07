package com.kookykraftmc.api.global.kookypackets.messaging;

public interface IPluginMessage {

    byte[] getBytes();

    void process(byte[] bytes);

    MessageType getType();

}

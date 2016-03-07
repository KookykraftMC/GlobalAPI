package com.kookykraftmc.api.global.kookypackets.messaging.messages.handshake;

import com.kookykraftmc.api.global.kookypackets.messaging.AbstractMessageObject;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerCountUpdate extends AbstractMessageObject {

    private int online;

    public PlayerCountUpdate(int online) {
        super();
        this.online = online;
    }

    public PlayerCountUpdate(byte[] bytes) {
        super(bytes);
    }

    public void serialize(ByteArrayDataInput in) {
        online = in.readInt();
    }

    public void parse(DataOutputStream out) throws IOException {
        out.writeInt(online);
    }

    public int getOnline() {
        return online;
    }

}

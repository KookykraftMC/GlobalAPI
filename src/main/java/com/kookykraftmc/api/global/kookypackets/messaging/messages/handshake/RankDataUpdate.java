package com.kookykraftmc.api.global.kookypackets.messaging.messages.handshake;

import com.kookykraftmc.api.global.kookypackets.messaging.messages.AbstractDataMapMessageObject;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class RankDataUpdate extends AbstractDataMapMessageObject {

    private String name;

    public RankDataUpdate(String name, Map<String, String> map) {
        super(map);
        this.name = name;
    }

    public RankDataUpdate(byte[] bytes) {
        super(bytes);
    }

    public void serializeInfo(ByteArrayDataInput in) {
        name = in.readUTF();
    }

    public void parseInfo(DataOutputStream out) throws IOException {
        out.writeUTF(name);
    }

    public String getName() {
        return name;
    }

}

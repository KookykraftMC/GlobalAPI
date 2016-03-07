package com.kookykraftmc.api.global.kookypackets.messaging.messages.response;

import com.kookykraftmc.api.global.kookypackets.messaging.messages.AbstractDataMapMessageObject;
import com.kookykraftmc.api.global.kookypackets.messaging.messages.PlayerMessage;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class PlayerDataResponse extends AbstractDataMapMessageObject implements PlayerMessage {

    private String name;

    public PlayerDataResponse(String name, Map<String, String> data) {
        super(data);
        this.name = name;
    }

    public PlayerDataResponse(byte[] bytes) {
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

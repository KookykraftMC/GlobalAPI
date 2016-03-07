package com.kookykraftmc.api.global.kookypackets.messaging.messages.request;

import com.kookykraftmc.api.global.kookypackets.messaging.AbstractMessageObject;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerDataRequest extends AbstractMessageObject {

    private String name;

    public PlayerDataRequest(String name) {
        super();
        this.name = name;
    }

    public PlayerDataRequest(byte[] bytes) {
        super(bytes);
    }


    public void serialize(ByteArrayDataInput in) {
        this.name = in.readUTF();
    }

    public void parse(DataOutputStream out) throws IOException {
        out.writeUTF(name);
    }

    public String getName() {
        return name;
    }

}

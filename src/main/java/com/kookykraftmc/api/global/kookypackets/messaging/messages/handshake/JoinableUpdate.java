package com.kookykraftmc.api.global.kookypackets.messaging.messages.handshake;

import com.kookykraftmc.api.global.kookypackets.messaging.AbstractMessageObject;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;

public class JoinableUpdate extends AbstractMessageObject {
    private boolean joinable;

    public JoinableUpdate(boolean joinable) {
        this.joinable = joinable;
    }

    public JoinableUpdate(byte[] bytes) {
        super(bytes);
    }

    public void serialize(ByteArrayDataInput in) {
        joinable = in.readBoolean();
    }

    public void parse(DataOutputStream out) throws IOException {
        out.writeBoolean(joinable);
    }

    public boolean isJoinable() {
        return joinable;
    }

}

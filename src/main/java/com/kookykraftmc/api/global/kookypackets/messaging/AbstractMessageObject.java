package com.kookykraftmc.api.global.kookypackets.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class AbstractMessageObject implements IPluginMessage {

    private MessageType type;

    public AbstractMessageObject() {
        this.type = MessageType.register(getClass());
    }

    public AbstractMessageObject(byte[] bytes) {
        this();
        process(bytes);
    }

    public MessageType getType() {
        return type;
    }

    public byte[] getBytes() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            parse(out);
            out.close();
        } catch (IOException e) {
            //Cannot be thrown
        }
        return stream.toByteArray();
    }

    public void process(byte[] bytes) {
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        serialize(in);
    }

    public abstract void serialize(ByteArrayDataInput in);

    public abstract void parse(DataOutputStream out) throws IOException;

}

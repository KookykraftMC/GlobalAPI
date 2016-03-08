package com.kookykraftmc.api.global.kookypackets.messaging.messages;

import com.kookykraftmc.api.global.plugin.KookyHub;
import com.kookykraftmc.api.global.kookypackets.messaging.AbstractMessageObject;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public abstract class AbstractDataMapMessageObject extends AbstractMessageObject implements DataMessage {

    private Map<String, String> data;

    public AbstractDataMapMessageObject(Map<String, String> data) {
        this.data = data;
    }

    public AbstractDataMapMessageObject(byte[] bytes) {
        super(bytes);
    }

    public void serialize(ByteArrayDataInput in) {
        serializeInfo(in);
        int size = in.readInt();
        if (size == -1) {
            data = null;
        } else {
            data = new HashMap<>();
            for (int done = 0; done < size; done++) {
                try {
                    String key = in.readUTF();
                    String value = in.readUTF();
                    data.put(key, value);
                } catch (Exception e) {
                    KookyHub.getInstance().getLogger().log(Level.SEVERE, e.getMessage());
                    KookyHub.getInstance().getLogger().log(Level.SEVERE, "Failed to proccess map object");
                    break;
                }
            }
        }
    }

    public void parse(DataOutputStream out) throws IOException {
        parseInfo(out);
        if (getData() == null) {
            out.writeInt(-1);
        } else {
            out.writeInt(getData().size());
            for (Map.Entry<String, String> entry : getData().entrySet()) {
                out.writeUTF(entry.getKey());
                out.writeUTF(entry.getValue());
            }
        }
    }

    public abstract void serializeInfo(ByteArrayDataInput in);

    public abstract void parseInfo(DataOutputStream out) throws IOException;

    public Map<String, String> getData() {
        return data;
    }

}


package com.mattprecious.notisync.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mattprecious.notisync.util.MyLog;

public class BaseMessage {
    @SuppressWarnings("unused")
    private final String DATA_TYPE;

    public BaseMessage() {
        DATA_TYPE = getClass().getName();
    }

    public static String toJsonString(BaseMessage message) {
        return new Gson().toJson(message);
    }

    public static BaseMessage fromJsonString(String jsonString) {
        BaseMessage message = null;
        try {
            JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
            String dataTypeStr = json.get("DATA_TYPE").getAsString();
            if (ClearMessage.class.getName().equals(dataTypeStr)) {
                json = json.getAsJsonObject("message");
                Class<?> dataType = Class.forName(json.get("DATA_TYPE").getAsString());

                BaseMessage subMessage = (BaseMessage) new Gson().fromJson(json, dataType);
                message = new ClearMessage(subMessage);
            } else {
                Class<?> dataType = Class.forName(json.get("DATA_TYPE").getAsString());
                message = (BaseMessage) new Gson().fromJson(json, dataType);
            }
        } catch (ClassNotFoundException e) {
            MyLog.e("BaseMessage", "Failed to convert to BaseMessage", e);
        }

        return message;
    }

}

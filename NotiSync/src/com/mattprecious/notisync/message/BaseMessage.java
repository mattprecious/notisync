/*
 * Copyright 2013 Matthew Precious
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mattprecious.notisync.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mattprecious.notisync.util.MyLog;

public class BaseMessage {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((DATA_TYPE == null) ? 0 : DATA_TYPE.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BaseMessage other = (BaseMessage) obj;
        if (DATA_TYPE == null) {
            if (other.DATA_TYPE != null)
                return false;
        } else if (!DATA_TYPE.equals(other.DATA_TYPE))
            return false;
        return true;
    }

}

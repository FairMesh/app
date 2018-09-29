package com.fairmesh.mhao.meshnetwork;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
    private JSONObject messageData;
    private boolean belongsToCurrentUser;

    public Message(JSONObject data, boolean currentUser) {
        belongsToCurrentUser = currentUser;
        messageData = data;
    }

    public String getText() {
        if (messageData.has("message")) {
            try {
                return messageData.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public long getID() {
        if (messageData.has("sender")) {
            try {
                return messageData.getLong("sender");
            } catch (JSONException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public String getTime() {
        if (messageData.has("time")) {
            try {
                return messageData.getString("time");
            } catch (JSONException e) {
                e.printStackTrace();
                return "Error 0";
            }
        }
        return "Error 1";
    }

    public JSONObject getMessageData() {
        return messageData;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}

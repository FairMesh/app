package com.fairmesh.mhao.meshnetwork.util;

import com.fairmesh.mhao.meshnetwork.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EnumSet;

import io.underdark.Underdark;
import io.underdark.transport.Link;
import io.underdark.transport.Transport;
import io.underdark.transport.TransportKind;
import io.underdark.transport.TransportListener;

public class Node implements TransportListener {
    private boolean running;
    private MainActivity activity;
    private long nodeId;
    private Transport transport;
    private JSONObject receivedMessage;

    private ArrayList<Link> links = new ArrayList<>();

    private ArrayList<JSONObject> newMessages = new ArrayList<>(); // Messages that have not been displayed
    private ArrayList<JSONObject> allMessages = new ArrayList<>(); // All received messages
    private ArrayList<String> connections = new ArrayList<>(); // IDs of all pings within a time period
    private ArrayList<String> oldConnections = new ArrayList<>(); // Connections gets pushed to this, this gets displayed to user
    public ArrayList<JSONObject> unsentMessages = new ArrayList<>(); // Messages that have not been uploaded to server
    public ArrayList<JSONObject> unreadMessages = new ArrayList<>(); // Messages that have not been read by user

    public Node(MainActivity activity, long ID) {
        this.activity = activity;

        receivedMessage = new JSONObject();

        nodeId = ID;

        configureLogging();

        EnumSet<TransportKind> kinds = EnumSet.of(TransportKind.BLUETOOTH, TransportKind.WIFI);

        this.transport = Underdark.configureTransport(
                234235,
                nodeId,
                this,
                null,
                activity.getApplicationContext(),
                kinds
        );
    }

    private void configureLogging() {

        Underdark.configureLogging(true);

    }

    public void start() {
        if (running)
            return;

        running = true;
        transport.start();
    }

    public void stop() {
        if (!running)
            return;

        running = false;
        transport.stop();
    }

    public ArrayList<JSONObject> getAllMessages() {
        return allMessages;
    }

    public long getNodeID() {
        return nodeId;
    }

    public void clearConnections() {
        oldConnections.clear();
        oldConnections.addAll(connections);
        connections.clear();
    }

    public ArrayList<String> getPeerIDs() {
        return oldConnections;
    }

    public ArrayList<JSONObject> getNewMessages() {
        return newMessages;
    }

    public void clearNewMessages() {
        newMessages.clear();
    }

    public void broadcastData(byte[] frameData) {
        if (links.isEmpty())
            return;

        try {
            activity.refreshFrames();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (Link link : links) {
            link.sendFrame(frameData);
        }
    }

    public void addMessageToDatabase(JSONObject message) {
        allMessages.add(message);
        unsentMessages.add(message);
    }

    public void clearUnsentMessages() {
        unsentMessages.clear();
    }

    //region TransportListener
    @Override
    public void transportNeedsActivity(Transport transport, ActivityCallback callback) {
        callback.accept(activity);
    }

    @Override
    public void transportLinkConnected(Transport transport, Link link) {
        links.add(link);
    }

    @Override
    public void transportLinkDisconnected(Transport transport, Link link) {
        links.remove(link);

        if (links.isEmpty()) {
            try {
                activity.refreshFrames();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void transportLinkDidReceiveFrame(Transport transport, Link link, byte[] frameData) {
        try {
            receivedMessage = new JSONObject(new String(frameData));

            boolean isDuplicatePing = false;
            for (String connection : connections) {
                if (connection.equals(String.valueOf(receivedMessage.get("sender")))) {
                    isDuplicatePing = true;
                }
            }

            if (!isDuplicatePing && !receivedMessage.get("sender").equals(nodeId)) {
                connections.add(receivedMessage.get("sender").toString());
                JSONObject pingJSON = new JSONObject();
                pingJSON.put("type", "ping");
                pingJSON.put("sender", receivedMessage.get("sender"));
                pingJSON.put("time", receivedMessage.get("time"));
                broadcastData(pingJSON.toString().getBytes());
            }

            if (receivedMessage.get("type").equals("message")) {
                boolean isDuplicate = false;
                for (JSONObject message : allMessages) {
                    if (message.get("sender").equals(receivedMessage.get("sender")) &&
                            message.get("message").equals(receivedMessage.get("message")) &&
                            message.get("time").equals(receivedMessage.get("time"))) {
                        isDuplicate = true;
                    }
                }
                if (!isDuplicate) {
                    newMessages.add(receivedMessage);
                    allMessages.add(receivedMessage);
                    unsentMessages.add(receivedMessage);
                    broadcastData(frameData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            receivedMessage = null;
        }


        try {
            activity.refreshFrames();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //endregion
} // Node

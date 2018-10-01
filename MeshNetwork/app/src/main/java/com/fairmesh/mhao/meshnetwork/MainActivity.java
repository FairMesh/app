package com.fairmesh.mhao.meshnetwork;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fairmesh.mhao.meshnetwork.model.Node;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

class UploadMessages extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... strings) {
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(strings[0]).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            Log.i("POST_MESSAGE", strings[1]);
            httpURLConnection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(strings[1]);
            Log.i("STATUS_CODE", String.valueOf(httpURLConnection.getResponseCode()));
            wr.flush();
            wr.close();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return null;
    }
}

public class MainActivity extends AppCompatActivity {

    private EditText messageEditText;
    private ListView messagesView;
    private Toolbar toolbar;
    private PopupWindow popupWindow;
    private TextView unreadMessagesTextView;
    private TextView connectionsTextView;

    private boolean isOnline = false;

    private MessageAdapter messageAdapter;
    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = null;
            if (connectivityManager != null) {
                activeNetwork = connectivityManager.getActiveNetworkInfo();
            }
            isOnline = activeNetwork != null && activeNetwork.isConnected();
        }
    };

    private int delay = 1000;
    private int refreshDelay = 2000;

    private double latitude = 0;
    private double longitude = 0;
    private long recipient = 0;
    private int unreadMessagesCount = 0;

    Node node;
    private static boolean started = false;

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void getPermissions() {
        // Required for location services
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }

        // Required for location services
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        // Required to get phone number
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
        }
    }

    private void setTimers() {
        // Set up timer for ping
        final Handler timeHandler = new Handler();
        timeHandler.postDelayed(new Runnable() {
            public void run() {

                String time = Calendar.getInstance().getTime().toString();

                JSONObject pingJSON = new JSONObject();
                try {
                    pingJSON.put("type", "ping");
                    pingJSON.put("sender", node.getNodeID());
                    pingJSON.put("time", time);
                } catch (JSONException e) {
                    e.printStackTrace();
                    pingJSON = null;
                }

                if (pingJSON != null) {
                    node.broadcastData(pingJSON.toString().getBytes());
                }

                timeHandler.postDelayed(this, delay);
            }
        }, delay);

        // Set up timer for connection display reset
        final Handler refreshHandler = new Handler();
        refreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshPeers();
                node.clearConnections();
                refreshHandler.postDelayed(this, refreshDelay);

                if (isOnline) {
                    ArrayList<JSONObject> unsentMessages = new ArrayList<>(node.unsentMessages);
                    for (JSONObject message : unsentMessages) {
                        UploadMessages uploadMessages = new UploadMessages();
                        uploadMessages.execute("https://fairmesh-dashboard.mybluemix.net/api/uploadMessage", message.toString());
                    }
                    node.clearUnsentMessages();
                }
            }
        }, refreshDelay);
    }

    @SuppressLint("HardwareIds")
    private void setupNodeID() {
        // Set up ID number and set up node

        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        long mPhoneNumber;
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) && tMgr != null) {
            try {
                mPhoneNumber = Long.valueOf(tMgr.getLine1Number());
                node = new Node(this, mPhoneNumber);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                String hex = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                long nodeId = Long.parseLong(hex.substring(7), 16);
                node = new Node(this, nodeId);
            }
        } else {
            String hex = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            long nodeId = Long.parseLong(hex.substring(7), 16);
            node = new Node(this, nodeId);
        }

    }

    private void setupLocationServices() {
        // Set up location services
        LocationManager locationManager;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED &&
                locationManager != null)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private void setupBadge() {
        if (unreadMessagesTextView != null) {
            if (unreadMessagesCount != 0) {
                unreadMessagesTextView.setVisibility(View.VISIBLE);
            } else {
                unreadMessagesTextView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        started = true;
        node.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(networkReceiver);

        if (node != null) {
            node.stop();
            started = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // menu.getItem(0).setTitle("ID: " + node.getNodeID());

        menu.getItem(1).setTitle("ID: " + node.getNodeID());
        MenuItem messageMenuItem = menu.getItem(0);
        messageMenuItem.setActionView(R.layout.notification_badge);
        View actionView = messageMenuItem.getActionView();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConnections(v);
            }
        });
        unreadMessagesTextView = actionView.findViewById(R.id.message_badge);
        setupBadge();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("EDIT_TEXT", messageEditText.getText().toString());
        String[] messageData = new String[messageAdapter.getCount()];
        boolean[] isOwnMessage = new boolean[messageAdapter.getCount()];
        for (int i = 0; i < messageAdapter.getCount(); i++) {
            messageData[i] = messageAdapter.getItem(i).getMessageData().toString();
            isOwnMessage[i] = messageAdapter.getItem(i).isBelongsToCurrentUser();
        }
        outState.putStringArray("MESSAGE_DATA", messageData);
        outState.putBooleanArray("IS_OWN_MESSAGE", isOwnMessage);
        outState.putInt("MESSAGE_COUNT", messageAdapter.getCount());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageEditText = findViewById(R.id.createMessage);
        messagesView = findViewById(R.id.messagesListView);
        toolbar = findViewById(R.id.mainActivityToolbar);
        connectionsTextView = findViewById(R.id.connectionsTextView);
        setSupportActionBar(toolbar);

        getPermissions();
        setupNodeID();
        setupLocationServices();
        setTimers();

        this.registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // Set ID
        messageAdapter = new MessageAdapter(this);
        messagesView.setAdapter(messageAdapter);

        // Keep messages when rotating phone
        if (savedInstanceState != null) {
            messageEditText.setText(savedInstanceState.getString("EDIT_TEXT"));
            boolean[] isOwnMessage = savedInstanceState.getBooleanArray("IS_OWN_MESSAGE");
            String[] messageData = savedInstanceState.getStringArray("MESSAGE_DATA");
            int count = savedInstanceState.getInt("MESSAGE_COUNT");
            if (messageData != null && isOwnMessage != null) {
                for (int i = 0; i < count; i++) {
                    JSONObject messageJSON;
                    try {
                        messageJSON = new JSONObject(messageData[i]);
                        Message message = new Message(messageJSON, isOwnMessage[i]);
                        messageAdapter.add(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void sendData(View view) {
        if (!started) {
            started = true;
            node = new Node(this);
            node.start();
            return;
        }

        if (messageEditText.getText().toString().trim().length() > 0) {
            String messageText = messageEditText.getText().toString();
            JSONObject messageJSON = new JSONObject();
            try {
                messageJSON.put("message", messageText);
                messageJSON.put("sender", node.getNodeID());
                messageJSON.put("time", new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date()));
                messageJSON.put("type", "message");
                messageJSON.put("latitude", latitude);
                messageJSON.put("longitude", longitude);
                messageJSON.put("recipient", recipient);
            } catch (JSONException e) {
                e.printStackTrace();
                messageJSON = null;
            }

            if (messageJSON != null) {
                byte[] byteArray = messageJSON.toString().getBytes();
                node.broadcastData(byteArray);
            }

            Message sentMessage = new Message(messageJSON, true);
            messageAdapter.add(sentMessage);
            messagesView.smoothScrollToPosition(messageAdapter.getCount() - 1);
            node.addMessageToDatabase(messageJSON);

            messageEditText.setText("");
        }
    }

    public void refreshPeers() {

        int peerIDSize = node.getPeerIDs().size();
        if (connectionsTextView != null) {
            String connectionsString = "Users in Mesh: " + String.valueOf(peerIDSize);
            connectionsTextView.setText(connectionsString);
        }
        if (peerIDSize == 0 && messageEditText != null && !isOnline) {
            messageEditText.setEnabled(false);
            messageEditText.setHint("Messaging Disabled: No Users");
        }
        if ((peerIDSize != 0 || isOnline) && messageEditText != null && !messageEditText.isEnabled()) {
            messageEditText.setEnabled(true);
            messageEditText.setHint("Message");
        }
    }

    public void showConnections(View item) {
        hideKeyboard(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = null;
        if (inflater != null) {
            popupView = inflater.inflate(R.layout.connections_popup, new LinearLayout(this), false);
        }

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setAnimationStyle(R.style.popup_window_animation);

        ListView peerList = null;
        if (popupView != null) {
            peerList = popupView.findViewById(R.id.connections_listview);
        }

        ArrayList<String> IDArray = node.getPeerIDs();
        for (int i = 0; i < IDArray.size(); i++) {
            int individualUnreadMessages = 0;
            String id = IDArray.get(i);
            for (JSONObject unreadMessage : node.unreadMessages) {
                try {
                    if (unreadMessage.get("sender").equals(Long.valueOf(id)) && !unreadMessage.get("recipient").equals(0)) {
                        individualUnreadMessages++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (individualUnreadMessages != 0) {
                IDArray.set(i, id + " (" + individualUnreadMessages + ")");
            }
        }
        int generalUnreadMessages = 0;
        for (JSONObject unreadMessage : node.unreadMessages) {
            try {
                if (unreadMessage.get("recipient").equals(0)) {
                    generalUnreadMessages++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        PeerListAdapter peerListAdapter = new PeerListAdapter(this);
        if (peerList != null) {
            peerList.setAdapter(peerListAdapter);
        }
        peerListAdapter.addAll(IDArray, generalUnreadMessages);

        popupWindow.setElevation(10);
        popupWindow.showAtLocation(messagesView, Gravity.CENTER, 0, 0);

        if (popupView != null) {
            popupView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        }
    }

    public void refreshFrames() throws JSONException {
        ArrayList<JSONObject> newMessages = node.getNewMessages();
        if (!newMessages.isEmpty()) {
            for (JSONObject message : newMessages) {
                addMessageToAdapter(message);
            }
        }
        node.clearNewMessages();
    }

    public void changeMessageRecipient(View v) throws JSONException {
        messageAdapter.clear();
        popupWindow.dismiss();
        TextView peerEntry = v.findViewById(R.id.peer_textview_individual);
        String peerEntryString = peerEntry.getText().toString().split(" ", 2)[0];
        if (peerEntryString.contains("General")) {
            recipient = 0;
            toolbar.setTitle("General Chat");
        } else {
            recipient = Long.valueOf(peerEntryString);
            toolbar.setTitle("Sending To: " + peerEntryString);
        }
        ArrayList<JSONObject> messages = new ArrayList<>(node.getAllMessages());
        for (JSONObject message : messages) {
            addMessageToAdapter(message);
            if (node.unreadMessages.contains(message)) {
                node.unreadMessages.remove(message);
                unreadMessagesCount--;
            }
        }
        unreadMessagesTextView.setText(String.valueOf(unreadMessagesCount));
        setupBadge();
    }

    void addMessageToAdapter(JSONObject message) throws JSONException {
        if ((recipient == 0 && message.get("recipient").equals(0)) ||
                (message.get("sender").equals(recipient) && message.get("recipient").equals(node.getNodeID())) ||
                (message.get("sender").equals(node.getNodeID()) && message.get("recipient").equals(recipient))) {
            messageAdapter.add(new Message(message, message.get("sender").equals(node.getNodeID())));
            messagesView.smoothScrollToPosition(messagesView.getCount() - 1);
        } else {
            node.unreadMessages.add(message);
            unreadMessagesCount++;
        }
        unreadMessagesTextView.setText(String.valueOf(unreadMessagesCount));
        setupBadge();
    }
} // MainActivity

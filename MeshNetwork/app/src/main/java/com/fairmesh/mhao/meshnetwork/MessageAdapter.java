package com.fairmesh.mhao.meshnetwork;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class MessageViewHolder {
    public TextView name;
    public TextView messageBody;
}


public class MessageAdapter extends BaseAdapter {

    private List<Message> messages = new ArrayList<Message>();
    private Context context;
    private boolean recentEntryAnimated = true;

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public void addList(List<Message> messageData) {
        messages.addAll(messageData);
    }

    public void add(Message message) {
        messages.add(message);
        notifyDataSetChanged();
        recentEntryAnimated = false;
    }

    public void clear() {
        messages.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<Message> getArrayList() {
        return messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(position);
        String messageText = message.getText();
        if (messageText != null) {
            if (message.isBelongsToCurrentUser()) {
                convertView = messageInflater.inflate(R.layout.sent_message, null);
                holder.messageBody = (TextView) convertView.findViewById(R.id.sent_message_body);
                convertView.setTag(holder);
                holder.messageBody.setText(messageText);
            } else {
                convertView = messageInflater.inflate(R.layout.received_message, null);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                convertView.setTag(holder);

                String sender = message.getTime() + "\nID: " + message.getID();
                holder.name.setText(sender);
                holder.messageBody.setText(message.getText());
            }
        }

        if (position == getCount()-1 && !recentEntryAnimated) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.popup_in);
            convertView.startAnimation(animation);
            recentEntryAnimated = true;
        }

        return convertView;
    }
}

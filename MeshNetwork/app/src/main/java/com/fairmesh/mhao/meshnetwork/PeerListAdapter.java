package com.fairmesh.mhao.meshnetwork;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class PeerListViewHolder {
    public TextView peer;
}

public class PeerListAdapter extends BaseAdapter {

    private List<String> peers = new ArrayList<>();
    private Context context;

    public PeerListAdapter(Context context) {
        this.context = context;
    }

    public void addAll(List<String> peerInput, int generalUnreadMessages) {
        peers.clear();
        if (generalUnreadMessages != 0) {
            peers.add("General Chat (" + generalUnreadMessages + ")");
        } else {
            peers.add("General Chat");
        }
        peers.addAll(peerInput);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return peers.size();
    }

    @Override
    public Object getItem(int position) {
        return peers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PeerListViewHolder holder = new PeerListViewHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        String peer = peers.get(position);
        convertView = inflater.inflate(R.layout.peer_entry, null);
        holder.peer = (TextView) convertView.findViewById(R.id.peer_textview_individual);
        holder.peer.setText(peer);
        return convertView;
    }
}

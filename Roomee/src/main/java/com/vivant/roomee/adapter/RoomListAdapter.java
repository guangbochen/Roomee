package com.vivant.roomee.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.vivant.roomee.R;
import com.vivant.roomee.model.Room;

import java.util.List;

/**
 * Created by guangbo on 10/10/13.
 */
public class RoomListAdapter extends BaseAdapter {
    Context context;
    List<Room> roomList;

    public RoomListAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    private class ViewHolder {
        ImageView imageStatus;
        TextView txtRoom;
        TextView txtStatus;
        TextView txtTime;
        ImageView imageCheck;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        Room room = (Room) getItem(position);
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(view == null)
        {
            view = mInflater.inflate(R.layout.room_list_item , null);
            holder  = new ViewHolder();
            if(room.getStatus() == 1)
                holder.txtTime.setTextColor(Color.GREEN);
            holder.imageStatus = (ImageView) view.findViewById(R.id.imageStatus);
            holder.txtRoom = (TextView) view.findViewById(R.id.txtRoom);
            holder.txtStatus = (TextView) view.findViewById(R.id.txtStatus);
            holder.txtTime = (TextView) view.findViewById(R.id.txtTime);
            holder.imageCheck = (ImageView) view.findViewById(R.id.imageCheck);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        //check room status
        String roomStatus;
        if(room.getStatus() == 0)
        {
            roomStatus = " Free for ";
            holder.txtTime.setTextColor(Color.GREEN);
        }
        else
        {
            roomStatus = " Busy for ";
        }

        holder.imageStatus.setImageResource(room.getStatus());
        holder.txtRoom.setText(room.getName());
        holder.txtStatus.setText(roomStatus);
        holder.txtTime.setText(room.getTime());
        holder.imageCheck.setImageResource(room.getCheckDetails());

        return view;
    }

    @Override
    public int getCount() {
        return roomList.size();
    }

    @Override
    public Object getItem(int position) {
        return roomList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return roomList.indexOf(getItemId(position));
    }

}

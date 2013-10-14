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

    private Context context;
    private List<Room> roomList;
    private Integer imageCheckButton = new Integer(R.drawable.check);

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
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(view == null)
        {
            view = mInflater.inflate(R.layout.room_list_item , null);
            holder  = new ViewHolder();
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

        Room room = (Room) getItem(position);
        setHolderDetails(holder, room);

        return view;
    }

    private void setHolderDetails(ViewHolder holder, Room room)
    {
        //check room status
        String roomStatus;
        if(room.getStatus() == 0)
        {
            roomStatus = " Free for ";
            holder.txtTime.setTextColor(Color.GREEN);
            holder.imageStatus.setImageResource(R.drawable.free);
        }
        else
        {
            roomStatus = " Busy for ";
            holder.txtTime.setTextColor(Color.RED);
            holder.imageStatus.setImageResource(R.drawable.busy);
        }

        holder.txtRoom.setText(room.getName());
        holder.txtStatus.setText(roomStatus);
        holder.txtTime.setText(room.getTime());
        holder.imageCheck.setImageResource(imageCheckButton);
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
        return roomList.indexOf(getItem(position));
    }

}

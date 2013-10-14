package com.vivant.roomee;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.vivant.roomee.adapter.RoomListAdapter;
import com.vivant.roomee.model.Room;
import java.util.ArrayList;
import java.util.List;

public class RoomListActivity extends Activity implements OnItemClickListener {

    private static final int[] id = new int [] { 1,2,3,4,5,6,7,8  };
    private static final String[] name = new String [] { "Gondwana" , "Hall of Justice",
            "Black Mesa", "The Green Room",
            "Gondwana 2" , "Hall of Justice 2",
            "Black Mesa 2", "The Green Room 2"};
    private static final int[] status = new int[] { 1, 0 ,1, 0, 1, 1, 0, 0};
    private static final String[] time = new String [] { "45 minutes" , "1 hour",
            "45 minutes", "1 hour and 40 minutes",
            "45 minutes" , "1 hour",
            "45 minutes", "1 hour and 40 minutes"};

    private ListView roomListView;
    private List<Room> roomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        roomList = new ArrayList<Room>();
        for(int i=0; i < id.length; i++)
        {
            Room room = new Room(id[i], name[i], status[i], time[i]);
            roomList.add(room);
        }
        roomListView = (ListView) findViewById(R.id.roomListView);
        RoomListAdapter adapter = new RoomListAdapter(this,roomList);
        roomListView.setAdapter(adapter);
        roomListView.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.room_list, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(RoomListActivity.this,RoomDetails.class);
        intent.putExtra("status", status[position]);
        startActivity(intent);

    }
}

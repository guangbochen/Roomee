package com.vivant.roomee;

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
    private static final Integer[] status = new Integer[] { R.drawable.free, R.drawable.busy, R.drawable.free, R.drawable.busy,
            R.drawable.free, R.drawable.free, R.drawable.free, R.drawable.busy};
    private static final String[] time = new String [] { "45 minutes" , "1 hour",
            "45 minutes", "1 hour and 40 minutes",
            "45 minutes" , "1 hour",
            "45 minutes", "1 hour and 40 minutes"};
    private Integer imageCheckButton = new Integer(R.drawable.check);

    private ListView roomListView;
    private List<Room> roomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        roomList = new ArrayList<Room>();
        for(int i=0; i < id.length; i++)
        {
            Room room = new Room(id[i], name[i], status[i], time[i], imageCheckButton);
            roomList.add(room);
        }
        roomListView = (ListView) findViewById(R.id.roomListView);
        RoomListAdapter adapter = new RoomListAdapter(this,roomList);
        roomListView.setAdapter(adapter);
//        roomList.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.room_list, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }
}

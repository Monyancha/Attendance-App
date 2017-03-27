package com.indiansportsnews.attendanceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> names = new ArrayList<String>() ;
    private ArrayList<Integer> attendance = new ArrayList<Integer>() ;
    private ArrayList<Integer> total_attendance = new ArrayList<Integer>() ;
    private ArrayList<Integer> points = new ArrayList<Integer>() ;
    private ArrayList<Integer> total_points = new ArrayList<Integer>() ;
    private ArrayList<Integer> ids = new ArrayList<Integer>() ;
    private CustomListAdapter adapter ;
    private SwipeRefreshLayout mySwipeRefreshLayout ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh) ;
        //fetchData();
        fetchDataRemote() ;
        ListView listview = (ListView) findViewById(R.id.listview) ;
        listview . setOnItemClickListener(new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> adapterView , View view , int pos , long l) {
                showOptionsDialog(ids . get(pos));
            }
        } ) ;
        mySwipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    fetchDataRemote();
                    mySwipeRefreshLayout . setRefreshing(false) ;
                    //finish();
                    //startActivity(getIntent());
                }
            }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_add) {
            showAddDialog(1 , 0) ;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddDialog(final int opt , final int att_id) {
        String title = null , btnStr = null ;
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_add_new , null) ;
        final EditText ename = (EditText) view . findViewById(R.id.add_name);
        final EditText eatt = (EditText) view . findViewById(R.id.add_att);
        final EditText etotatt = (EditText) view . findViewById(R.id.tot_att);
        final EditText epoint = (EditText) view . findViewById(R.id.add_point);
        final EditText etotpoint = (EditText) view . findViewById(R.id.tot_point);

        if(opt == 1) { // Add
            title = "ADD NEW" ;
            btnStr = "ADD" ;
        }
        if(opt == 2) { // Edit
            title = "EDIT" ;
            btnStr = "EDIT" ;
            int index = ids . indexOf(att_id) ;
            ename . setText(names . get(index));
            String att = attendance . get(index) + "" ;
            eatt . setText(att);
            String tot_att = total_attendance . get(index) + "" ;
            etotatt . setText(tot_att);
            String pt = points . get(index) + "" ;
            epoint . setText(pt);
            String tot_pt = total_points . get(index) + "" ;
            etotpoint . setText(tot_pt);
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        try {
            // Get the layout inflater

            builder.setTitle(title) ;
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(btnStr , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String name = ename.getText().toString();
                            if (name.length() == 0) {
                                Toast.makeText(getApplicationContext(), "Enter Name ...", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String att_str = eatt.getText().toString() ;
                            int att = 0 ;
                            if(att_str.length() != 0) {
                                try {
                                    att = Integer.parseInt(att_str);
                                } catch(Exception ex) {
                                    Toast.makeText(MainActivity.this, "Please enter a number in Attendance ...", Toast.LENGTH_SHORT).show();
                                    return ;
                                }
                            }
                            String att_tot_str = etotatt.getText().toString() ;
                            int tot_att = 0 ;
                            if(att_tot_str.length() != 0) {
                                try {
                                    tot_att = Integer.parseInt(att_tot_str);
                                } catch(Exception ex) {
                                    Toast.makeText(MainActivity.this, "Please enter a number in Total Attendance ...", Toast.LENGTH_SHORT).show();
                                    return ;
                                }
                            }
                            String point_str = epoint.getText().toString() ;
                            int point = 0 ;
                            if(point_str.length() != 0) {
                                try {
                                    point = Integer.parseInt(point_str);
                                } catch(Exception ex) {
                                    Toast.makeText(MainActivity.this, "Please enter a number in Points ...", Toast.LENGTH_SHORT).show();
                                    return ;
                                }
                            }
                            String tot_point_str = etotpoint.getText().toString() ;
                            int tot_point = 0 ;
                            if(tot_point_str.length() != 0) {
                                try {
                                    tot_point = Integer.parseInt(tot_point_str);
                                } catch(Exception ex) {
                                    Toast.makeText(MainActivity.this, "Please enter a number in Total Points ...", Toast.LENGTH_SHORT).show();
                                    return ;
                                }
                            }
                            try {
                                if(opt == 1)
                                    insertData(name , att , tot_att , point , tot_point);
                                if(opt == 2) {
                                    editData(att_id , name , att , tot_att , point , tot_point);
                                }
                                fetchDataRemote() ;
                                try {
                                    adapter.notifyDataSetChanged();
                                } catch(Exception ex) {

                                }
                            } catch(Exception ex) {

                            }
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } catch(Exception ex) {

        }
    }

    private void showOptionsDialog(final int list_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option")
                .setMessage("What do you want to do ?")
                .setCancelable(true)
                .setPositiveButton("Edit",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showAddDialog(2 , list_id) ;
                    }
                })
                .setNegativeButton("Delete",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showDeleteConfirmationDialog(list_id) ;
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showDeleteConfirmationDialog(final int list_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String name = names .get(ids . indexOf(list_id)) ;
        builder.setTitle("Delete " + name)
                .setMessage("Are you sure ?")
                .setCancelable(true)
                .setPositiveButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog . cancel() ;
                    }
                })
                .setNegativeButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            deleteData(list_id);
                        } catch(Exception ex) {

                        }
                        int pos = ids . indexOf(list_id) ;
                        names . remove(pos) ;
                        attendance . remove(pos) ;
                        total_attendance . remove(pos) ;
                        points . remove(pos) ;
                        total_points . remove(pos) ;
                        ids . remove(pos) ;
                        try {
                            adapter.notifyDataSetChanged();
                        } catch(Exception ex) {

                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void fetchData() {
        AttendanceDbHelper resDbHelper = new AttendanceDbHelper(getApplicationContext());
        SQLiteDatabase mydb = resDbHelper.getReadableDatabase();
        String qry = "SELECT * FROM " + AttendanceDbContract.Entry.TABLE_NAME + " ORDER BY " + AttendanceDbContract.Entry.COLUMN_NAME_ID + " DESC ;" ;
        Cursor c = mydb.rawQuery(qry , null);
        while(c.moveToNext()) {
            String name = c.getString(c.getColumnIndexOrThrow(AttendanceDbContract.Entry.COLUMN_NAME_NAME));
            int att = c.getInt(c.getColumnIndexOrThrow(AttendanceDbContract.Entry.COLUMN_NAME_ATT));
            int tot_att = c.getInt(c.getColumnIndexOrThrow(AttendanceDbContract.Entry.COLUMN_NAME_TOT_ATT));
            int point = c.getInt(c.getColumnIndexOrThrow(AttendanceDbContract.Entry.COLUMN_NAME_POINTS));
            int tot_point = c.getInt(c.getColumnIndexOrThrow(AttendanceDbContract.Entry.COLUMN_NAME_TOT_POINTS));
            int id = c.getInt(c.getColumnIndexOrThrow(AttendanceDbContract.Entry.COLUMN_NAME_ID));
            names . add(name) ;
            attendance . add(att) ;
            total_attendance . add(tot_att) ;
            points . add(point) ;
            total_points . add(tot_point) ;
            ids . add(id) ;
        }
        try {
            adapter.notifyDataSetChanged();
        } catch(Exception ex) {

        }
        c.close();
    }

    private void insertData(String name , int att , int tot_att , int point , int tot_point) {
        /*AttendanceDbHelper resDbHelper = new AttendanceDbHelper(getApplicationContext());
        SQLiteDatabase mydb = resDbHelper.getWritableDatabase();
        String qry = "INSERT INTO " + AttendanceDbContract.Entry.TABLE_NAME + " ('" + AttendanceDbContract.Entry.COLUMN_NAME_NAME + "' , '" + AttendanceDbContract.Entry.COLUMN_NAME_ATT + "' , '" + AttendanceDbContract.Entry.COLUMN_NAME_TOT_ATT + "' , '" + AttendanceDbContract.Entry.COLUMN_NAME_POINTS + "' , '" + AttendanceDbContract.Entry.COLUMN_NAME_TOT_POINTS + "') VALUES ('" + name + "' , " + att + " , " + tot_att + " , " + point + " , " + tot_point + " ) ;" ;
        mydb.execSQL(qry);
        */
        String qry = "INSERT INTO `attendance`(`name` , `att` , `tot_att` , `points` , `tot_points`) VALUES('" + name + "' , " + att + " , " + tot_att + " , " + point + " , " + tot_point + " ) ;" ;
        do_operation(qry);
    }

    private void deleteData(int id) {
        /*
        AttendanceDbHelper resDbHelper = new AttendanceDbHelper(getApplicationContext());
        SQLiteDatabase mydb = resDbHelper.getWritableDatabase();
        String qry = "DELETE FROM " + AttendanceDbContract.Entry.TABLE_NAME + " WHERE " + AttendanceDbContract.Entry.COLUMN_NAME_ID + " = " + id + " ;" ;
        mydb.execSQL(qry);
        */
        String qry = "DELETE FROM `attendance` WHERE `id` = " + id + " ;" ;
        do_operation(qry);
    }

    private void editData(int id , String name , int att , int tot_att , int point , int tot_point) {
        /*
        AttendanceDbHelper resDbHelper = new AttendanceDbHelper(getApplicationContext());
        SQLiteDatabase mydb = resDbHelper.getWritableDatabase();
        String qry = "UPDATE " + AttendanceDbContract.Entry.TABLE_NAME + " SET " + AttendanceDbContract.Entry.COLUMN_NAME_NAME + " = '" + name + "' , " + AttendanceDbContract.Entry.COLUMN_NAME_ATT + " = " + att + " , " + AttendanceDbContract.Entry.COLUMN_NAME_TOT_ATT + " = " + tot_att + " , " + AttendanceDbContract.Entry.COLUMN_NAME_POINTS + " = " + point + " , " + AttendanceDbContract.Entry.COLUMN_NAME_TOT_POINTS + " = " + tot_point + " WHERE " + AttendanceDbContract.Entry.COLUMN_NAME_ID + " = " + id + " ;" ;
        mydb.execSQL(qry);
        */
        String qry = "UPDATE `attendance` SET `name` = '" + name + "' , `att` = " + att + " , `tot_att` = " + tot_att + " , `points` = " + point + " , `tot_points` = " + tot_point + " WHERE `id` = " + id + " ;" ;
        do_operation(qry);
    }

    private void fetchDataRemote() {
        class wrapper {
            int id ;
            String name ;
            int att ;
            int tot_att ;
            int points ;
            int tot_points ;
        }

        class SendPostReqAsyncTask extends AsyncTask<String, Void, wrapper[]> {
            wrapper[] w;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mySwipeRefreshLayout . setRefreshing(true) ;
            }

            @Override
            protected wrapper[] doInBackground(String... params) {

                String data = "data";

                BufferedReader reader = null;
                HttpURLConnection conn = null;

                // Send data
                try {

                    // Defined URL  where to send data
                    URL url = new URL("https://screenbiz.000webhostapp.com/fetch_attendance.php");

                    // Send POST data request

                    conn = (HttpURLConnection) url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();


                    // Get the server response

                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    int fetchedData = reader.read();
                    // Reading json string from server
                    String json_str = "{ \"attendance\": ";
                    while (fetchedData != -1) {
                        char current = (char) fetchedData;
                        fetchedData = reader.read();
                        json_str = json_str + current;
                    }

                    json_str = json_str + "}";
                    Log . v ("Test2" , json_str) ;
                    final JSONObject obj = new JSONObject(json_str);
                    final JSONArray geodata = obj.getJSONArray("attendance");
                    final int n = geodata.length();
                    if (n == 0)
                        return null;
                    w = new wrapper[n];
                    //int lastId = SaveSharedPreferences . getLastId(getApplicationContext()) ;
                    for (int i = 0; i < n; i++) {
                        final JSONObject att_status = geodata.getJSONObject(i);
                        w[i] = new wrapper() ;
                        w[i].id = att_status . getInt("id") ;
                        w[i].name = att_status . getString("name") ;
                        w[i].att = att_status . getInt("att") ;
                        w[i].tot_att = att_status . getInt("tot_att") ;
                        w[i].points = att_status . getInt("points") ;
                        w[i].tot_points = att_status . getInt("tot_points") ;
                    }

                } catch (Exception j) {
                    j . printStackTrace();
                    Log . v ("Test1" , "WTF") ;
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                        try {
                            reader.close();
                        } catch (Exception e) {

                        }
                    }
                }
                return w;
            }

            @Override
            protected void onPostExecute(wrapper[] w) {
                super.onPostExecute(w) ;
                mySwipeRefreshLayout . setRefreshing(false) ;
                //dialog . dismiss() ;
                if(w != null) {
                    ids . clear() ;
                    names . clear() ;
                    attendance . clear() ;
                    total_attendance . clear() ;
                    points . clear();
                    total_points .clear();
                    try {
                        for(int i = 0 ; i < w . length ; i ++) {
                            ids . add(w[i].id) ;
                            names . add(w[i].name) ;
                            attendance . add(w[i].att) ;
                            total_attendance . add(w[i].tot_att) ;
                            points . add(w[i].points) ;
                            total_points . add(w[i].tot_points) ;
                        }
                    } catch (Exception ex) {
                        Log.v("Test" , "Exception") ;
                        ex . printStackTrace();
                    }
                    adapter = new CustomListAdapter(MainActivity.this , names , attendance , total_attendance , points , total_points) ;
                    ListView listview = (ListView) findViewById(R.id.listview) ;
                    try {
                        listview.setAdapter(adapter);
                    } catch(Exception ex) {

                    }
                }
            }
        }
        if(checkConnection(getApplicationContext())) {
            SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
            sendPostReqAsyncTask.execute();
        } else {
            Toast. makeText(getApplicationContext() , "No internet" , Toast . LENGTH_SHORT) . show() ;
        }
    }

    private void do_operation(final String qry) {

        class SendPostReqAsyncTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //mySwipeRefreshLayout . setRefreshing(true) ;
            }

            @Override
            protected Void doInBackground(Void... params) {

                String data = "";
                try {
                    data = URLEncoder.encode("qry", "UTF-8")
                            + "=" + URLEncoder.encode(qry , "UTF-8") ;
                } catch (UnsupportedEncodingException e) {

                }

                BufferedReader reader = null;
                HttpURLConnection conn = null;

                // Send data
                try {

                    // Defined URL  where to send data
                    URL url = new URL("https://screenbiz.000webhostapp.com/operation_attendance.php");

                    // Send POST data request

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    // Get the server response

                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    int fetchedData = reader.read();
                    // Reading json string from server
                    String str = "" ;
                    while (fetchedData != -1) {
                        char current = (char) fetchedData;
                        fetchedData = reader.read();
                        str = str + current;
                    }

                } catch (Exception j) {

                } finally {
                    if (conn != null) {
                        conn.disconnect();
                        try {
                            reader.close();
                        } catch (Exception e) {

                        }
                    }
                }
                return null ;
            }

            @Override
            protected void onPostExecute(Void v) {
                super.onPostExecute(v) ;
                fetchDataRemote() ;
            }
        }
        if(checkConnection(getApplicationContext())) {
            SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
            sendPostReqAsyncTask.execute();
        } else {
            Toast. makeText(getApplicationContext() , "No internet" , Toast . LENGTH_SHORT) . show() ;
        }
    }

    boolean checkConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected ;
    }
}
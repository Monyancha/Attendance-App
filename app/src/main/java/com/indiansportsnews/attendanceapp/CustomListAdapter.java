package com.indiansportsnews.attendanceapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> names = new ArrayList<String>() ;
    private ArrayList<Integer> attendance = new ArrayList<Integer>() ;
    private ArrayList<Integer> total_attendance = new ArrayList<Integer>() ;
    private ArrayList<Integer> points = new ArrayList<Integer>() ;
    private ArrayList<Integer> total_points = new ArrayList<Integer>() ;

    public CustomListAdapter(Activity context, ArrayList<String> name , ArrayList<Integer> attendance , ArrayList<Integer> total_attendance , ArrayList<Integer> points , ArrayList<Integer> total_points) {
        super(context, R.layout.list_item , name);
        this . context = context;
        this . names = name ;
        this . attendance = attendance ;
        this . total_attendance = total_attendance ;
        this . points = points ;
        this . total_points = total_points ;
    }

    public View getView(int pos , View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item, null,true);
        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView att_textview = (TextView) rowView.findViewById(R.id.attendance);
        TextView point = (TextView) rowView.findViewById(R.id.points);
        name . setText(names . get(pos));
        int att = attendance . get(pos) ;
        int tot_att = total_attendance . get(pos) ;
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        float per_att = 0 ;
        if(att != 0 || tot_att != 0)
            per_att = (att * 100) / (float)tot_att ;
        att_textview . setText("Attendance : " + att + "/" + tot_att + "\n(" + df.format(per_att) + "%)");
        int pt = points . get(pos) ;
        int tot_pt = total_points . get(pos) ;
        float per_pt = 0 ;
        if(pt != 0 || tot_pt != 0)
            per_pt = (pt * 100) / (float)tot_pt ;
        point . setText("Points : " + pt + "/" + tot_pt + "\n(" + df.format(per_pt) + "%)");
        return rowView;
    }
}
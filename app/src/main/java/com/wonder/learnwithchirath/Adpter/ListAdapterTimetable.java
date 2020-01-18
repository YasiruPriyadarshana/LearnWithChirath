package com.wonder.learnwithchirath.Adpter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.wonder.learnwithchirath.Firebase.FirebaseDatabaseHelper2;
import com.wonder.learnwithchirath.Object.Timetable;
import com.wonder.learnwithchirath.R;
import java.util.ArrayList;
import java.util.List;

public class ListAdapterTimetable extends ArrayAdapter<Timetable>{
    private static final String TAG="ListAdapterTimetable";
    private Context mContext;
    private CallbackInterface2 mCallback;
    int mResource;
    private ArrayList<String> keys;

    public interface CallbackInterface2{
        void onHandleSelection2(int position);
    }

    public ListAdapterTimetable(Context context, int resource, ArrayList<Timetable> objects,ArrayList<String> keys,CallbackInterface2 mCallback) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
        this.mCallback=mCallback;
        this.keys=keys;
        keys=new ArrayList<>();
        this.keys.addAll(keys);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

         String date=getItem(position).getDate();
         String grade=getItem(position).getGrade();
         String time=getItem(position).getTime();
         String institute=getItem(position).getInstitute();
         String gcalss=getItem(position).getGcalss();
         final String key=keys.get(position);



        LayoutInflater inflater=LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView dateb=(TextView)convertView.findViewById(R.id.dayofweek);
        Button gradeb=(Button)convertView.findViewById(R.id.mgrade);
        Button timeb=(Button)convertView.findViewById(R.id.mtime);
        Button instituteb=(Button)convertView.findViewById(R.id.minstitute);
        Button gcalssb=(Button)convertView.findViewById(R.id.mclass);

        if (date.isEmpty()){
            dateb.setVisibility(View.GONE);
        }else {
            dateb.setText(date);
        }
        gradeb.setText(grade);
        timeb.setText(time);
        instituteb.setText(institute);
        gcalssb.setText(gcalss);


        Button delete=(Button)convertView.findViewById(R.id.deleteclass);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                new FirebaseDatabaseHelper2().deleteClassDetails(key, new FirebaseDatabaseHelper2.DataStatus() {
                                    @Override
                                    public void DataIsLoaded(List<Timetable> timetables, List<String> keys) {

                                    }

                                    @Override
                                    public void DataIsInserted() {

                                    }

                                    @Override
                                    public void DataIsUpdated() {

                                    }

                                    @Override
                                    public void DataIsDeleted() {
                                        mCallback.onHandleSelection2(position);
                                    }
                                });
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

            }
        });




        return convertView;
    }





}

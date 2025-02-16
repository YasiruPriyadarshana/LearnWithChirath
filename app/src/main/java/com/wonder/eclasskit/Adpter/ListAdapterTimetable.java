package com.wonder.eclasskit.Adpter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wonder.eclasskit.Firebase.FirebaseDatabaseHelper2;
import com.wonder.eclasskit.Object.Common;
import com.wonder.eclasskit.Object.TimetableObj;
import com.wonder.eclasskit.R;
import java.util.ArrayList;
import java.util.List;

public class ListAdapterTimetable extends BaseAdapter implements Filterable{
    private static final String TAG="ListAdapterTimetable";
    private Context mContext;
    private CallbackInterface2 mCallback;
    private int mResource;
    private ArrayList<String> keys;
    private ArrayList<TimetableObj> object,original;
    private CustomFilter cm;


    public interface CallbackInterface2{
        void onHandleSelection2();
    }

    public ListAdapterTimetable(Context context, int resource, ArrayList<TimetableObj> objects, ArrayList<String> keys, CallbackInterface2 mCallback) {
        mContext=context;
        mResource=resource;
        this.mCallback=mCallback;
        this.keys=keys;
        keys=new ArrayList<>();
        this.keys.addAll(keys);
        object=objects;
        original=objects;
    }

    @Override
    public int getCount() {
        return original.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

         final String key=keys.get(position);



        LayoutInflater inflater=LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView dateb=(TextView)convertView.findViewById(R.id.dayofweek);
        Button gradeb=(Button)convertView.findViewById(R.id.mgrade);
        Button timeb=(Button)convertView.findViewById(R.id.mtime);
        Button instituteb=(Button)convertView.findViewById(R.id.minstitute);
        Button gcalssb=(Button)convertView.findViewById(R.id.mclass);

        if (original.get(position).getDate().isEmpty()){
            dateb.setVisibility(View.GONE);
        }else {
            dateb.setText(original.get(position).getDate());
        }
        gradeb.setText(original.get(position).getGrade());
        timeb.setText(original.get(position).getTime());
        instituteb.setText(original.get(position).getInstitute());
        gcalssb.setText(original.get(position).getGcalss());


        Button delete=(Button)convertView.findViewById(R.id.deleteclass);
        if (Common.limit == 1){
            delete.setVisibility(View.GONE);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                mCallback.onHandleSelection2();
                                new FirebaseDatabaseHelper2().deleteClassDetails(key, new FirebaseDatabaseHelper2.DataStatus() {
                                    @Override
                                    public void DataIsLoaded(List<TimetableObj> timetables, List<String> keys) {

                                    }

                                    @Override
                                    public void DataIsInserted() {

                                    }

                                    @Override
                                    public void DataIsUpdated() {

                                    }

                                    @Override
                                    public void DataIsDeleted() {

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
                builder.setMessage("Are you sure you want to delete class details?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

            }
        });




        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (cm==null){
            cm=new CustomFilter();
        }
        return cm;
    }

    class CustomFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results= new FilterResults();

            if (constraint!=null && constraint.length()>0) {
                constraint = constraint.toString().toUpperCase();
                ArrayList<TimetableObj> filters = new ArrayList<>();

                for (int i = 0; i < object.size(); i++) {
                    if (object.get(i).getInstitute().toUpperCase().contains(constraint)) {
                        TimetableObj singlerow = new TimetableObj(object.get(i).getDate(), object.get(i).getGrade(), object.get(i).getTime(), object.get(i).getInstitute(), object.get(i).getGcalss());
                        filters.add(singlerow);
                    }
                }
                results.count = filters.size();
                results.values = filters;
            }else {
                results.count=object.size();
                results.values=object;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            original=(ArrayList<TimetableObj>)results.values;
            notifyDataSetChanged();
        }
    }

}

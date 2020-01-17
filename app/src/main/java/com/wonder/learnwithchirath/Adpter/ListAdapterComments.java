package com.wonder.learnwithchirath.Adpter;


import android.Manifest;

import android.app.AlertDialog;
import android.content.Context;


import android.content.DialogInterface;
import android.content.pm.PackageManager;

import android.net.Uri;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.core.content.ContextCompat;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import com.wonder.learnwithchirath.Comments;
import com.wonder.learnwithchirath.Object.CommentM;
import com.wonder.learnwithchirath.Object.Reply;
import com.wonder.learnwithchirath.R;


import java.util.ArrayList;

public class ListAdapterComments extends ArrayAdapter<CommentM> {
    private static final String TAG="ListAdapterComment";
    private CallbackInterface mCallback;
    private Context mContext;
    int mResource;
    private DatabaseReference databaseReference2;
    private String name1,name;
    private ArrayList<String> keys;
    private StorageReference storage;
    private ListAdapterReply adapter;
    Uri p;



    public interface CallbackInterface{
        void onHandleSelection();
        void onHandleSelectionClear();
        Uri getimage();
        void popUp(String key,String uri);
        void popUpReply(final String key,final String uri,final DatabaseReference dr);

    }
    public ListAdapterComments(Context context, int resource, ArrayList<CommentM> objects, String name1, ArrayList<String> keys, String name, CallbackInterface mCallback) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
        this.keys=keys;
        keys=new ArrayList<>();
        this.keys.addAll(keys);
        this.name1=name1;
        this.name=name;
        this.mCallback=mCallback;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String user=getItem(position).getUsercmt();
        final String comment=getItem(position).getCommentdesc();
        final String uri=getItem(position).getUricmt();
        final String key=keys.get(position);
        databaseReference2 = FirebaseDatabase.getInstance().getReference("comments/"+name1.substring(0, name1.length() - 4)+"/"+key+"/reply");
        storage= FirebaseStorage.getInstance().getReference();
//        Toast.makeText(getContext(),"comments/"+name1.substring(0, name1.length() - 4)+"/"+key, Toast.LENGTH_SHORT).show();
        LayoutInflater inflater=LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView nameC=(TextView)convertView.findViewById(R.id.cmt_user);
        TextView commentC=(TextView)convertView.findViewById(R.id.cmt_comment);
        ImageView imgC=(ImageView)convertView.findViewById(R.id.cmt_image);
        ListView ReplyListView=(ListView)convertView.findViewById(R.id.recyclerviewreply);

        nameC.setText(user);
        commentC.setText(comment);
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mCallback.popUp(key,uri);
                return true;
            }

        });


        if(uri!=null) {
            Picasso.with(getContext()).load(uri).into(imgC);
        }
        else{
            imgC.setVisibility(View.GONE);
        }
        viewAllFiles(ReplyListView);



        return convertView;
    }


    private void viewAllFiles(final ListView ReplyListView) {
        final ArrayList<Reply> replies= new ArrayList<>();
        final DatabaseReference dr=databaseReference2;

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<String> keys = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Reply reply = postSnapshot.getValue(Reply.class);
                    replies.add(reply);
                    String mkey = postSnapshot.getKey();
                    keys.add(mkey);
                }


                adapter = new ListAdapterReply(mContext,R.layout.itemreply,replies,keys,dr);





                View v=LayoutInflater.from(mContext).inflate(R.layout.footerviewreply, null);
                if (ReplyListView.getFooterViewsCount() > 0)
                {
                    ReplyListView.removeFooterView(v);
                }
                ReplyListView.addFooterView(v);
                Button  updateReply = (Button) v.findViewById(R.id.addrep);
                final EditText desc = (EditText)v.findViewById(R.id.rep_in);
                Button addImage=(Button)v.findViewById(R.id.repaddimage);
                ImageView repimage=(ImageView)v.findViewById(R.id.repimage_in);


                addImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            mCallback.onHandleSelection();
//                            Toast.makeText(mContext, "sa:"+ReplyListView.getPositionForView(v), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getContext(), "please provide permission", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                updateReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String rep_str=desc.getText().toString();
                        if (rep_str.isEmpty()){
                            Toast.makeText(mContext, "Type Reply", Toast.LENGTH_SHORT).show();
                        }else {
                            uplodeFile(mCallback.getimage(), dr, rep_str);
                        }

                    }
                });



                ReplyListView.setAdapter(adapter);
                setListViewHeightBasedOnChildren(ReplyListView);


                ReplyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        String key=keys.get(position);
                        Reply reply=replies.get(position);


                        mCallback.popUpReply(key,reply.getUrirep(),dr);
//                        Toast.makeText(mContext, "posi:"+position, Toast.LENGTH_SHORT).show();

                        return false;
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void uplodeFile(final Uri imgUri,final DatabaseReference dr,final String rep_st) {
        //imageuploade
        if (imgUri == null) {
            Reply replytobj = new Reply(name, rep_st,null);

            dr.child(dr.push().getKey()).setValue(replytobj);
            Toast.makeText(getContext(), "Add new Reply", Toast.LENGTH_SHORT).show();
            mCallback.onHandleSelectionClear();


        }else {
            StorageReference reference2 = storage.child("uploads5/" + System.currentTimeMillis() + ".png");
            reference2.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();

                    while (!uri.isComplete()) ;
                    p = uri.getResult();


                    Reply replytobj = new Reply(name, rep_st, p.toString());

                    dr.child(dr.push().getKey()).setValue(replytobj);
                    Toast.makeText(getContext(), "Add new Reply", Toast.LENGTH_SHORT).show();

                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    mCallback.onHandleSelectionClear();
                }
            });
            ;//end


        }

    }

    public static void setListViewHeightBasedOnChildren(ListView myListView) {
        ListAdapter adapter = myListView.getAdapter();
        if (myListView != null) {
            int totalHeight = 0;
            for (int i = 0; i < adapter.getCount(); i++) {
                View item= adapter.getView(i, null, myListView);
                item.measure(0, 0);
                totalHeight += item.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = myListView.getLayoutParams();
            params.height = totalHeight + (myListView.getDividerHeight() * (adapter.getCount() - 1));
            myListView.setLayoutParams(params);
        }
    }


}

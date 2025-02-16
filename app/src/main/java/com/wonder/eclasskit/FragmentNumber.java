package com.wonder.eclasskit;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class FragmentNumber extends Fragment {
    private Button bt_Number;
    private EditText Number;
    private View view;
    private String name;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment_number, container, false);


        bt_Number=(Button)view.findViewById(R.id.bt_number);
        Number=(EditText) view.findViewById(R.id.in_number);
        bt_Number.setEnabled(false);
        Number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name=Number.getText().toString().trim();
                bt_Number.setEnabled(!name.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        bt_Number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int length=Number.getText().length();
                if (length==10) {
                    wirteFile();
                    FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragmentplace, new FragmentEmail());
                    ft.commit();
                }else {
                    Toast.makeText(getContext(), "Enter valid Phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void wirteFile() {
        String textToSave = Number.getText().toString();
        String space = ",";
        try {
            FileOutputStream fileOutputStream = requireActivity().openFileOutput("apprequirement.txt", Context.MODE_APPEND);
            fileOutputStream.write((textToSave + space).getBytes());
            fileOutputStream.close();

            Toast.makeText(getActivity(), "text Saved", Toast.LENGTH_SHORT).show();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

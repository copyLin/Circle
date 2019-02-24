package com.example.linxl.circle;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linxl.circle.utils.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditInfoFragment extends Fragment {

    private EditText userName;
    private EditText department;
    private EditText major;
    private EditText words;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_info, container, false);
        userName = (EditText) view.findViewById(R.id.user_name);
        department = (EditText) view.findViewById(R.id.user_department);
        major = (EditText) view.findViewById(R.id.user_major);
        words = (EditText) view.findViewById(R.id.user_words);

        HomepageActivity activity = (HomepageActivity) getActivity();
        TextView nameText = activity.findViewById(R.id.user_name);
        TextView departmentText = activity.findViewById(R.id.user_department);
        TextView majorText = activity.findViewById(R.id.user_major);
        TextView wordsText = activity.findViewById(R.id.user_words);
        final FloatingActionButton button = activity.findViewById(R.id.button_edit);
        userName.setText(nameText.getText());
        department.setText(departmentText.getText());
        major.setText(majorText.getText());
        words.setText(wordsText.getText());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = getString(R.string.server_ip) + "updateUserInfo";
                RequestBody requestBody = new FormBody.Builder()
                        .add("userName", userName.getText().toString())
                        .add("department", department.getText().toString())
                        .add("major", major.getText().toString())
                        .add("words", words.getText().toString())
                        .build();
                HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "修改失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                button.setImageResource(R.drawable.ic_edit);
                                Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            }
                        });

                    }
                });
            }
        });
        return view;
    }

}

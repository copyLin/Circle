package com.example.linxl.circle;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageDetailFragment extends Fragment {

    private String imgPath;
    private int num;
    private int count;

    public ImageDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imgPath = (String) getArguments().get("imgPath");
        num = (int) getArguments().get("num");
        count = (int) getArguments().get("count");
        View view = inflater.inflate(R.layout.fragment_image_detail, container, false);
        TextView location = (TextView) view.findViewById(R.id.text_view);
        PhotoView photoView = (PhotoView) view.findViewById(R.id.photo_view);
        String position = count + "/" + num;
        location.setText(position);
        Glide.with(getContext()).load(imgPath).into(photoView);
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                ((Activity)getContext()).finish();
            }
        });
        return view;
    }

}

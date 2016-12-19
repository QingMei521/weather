package com.example.zuo.weather.view;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zuo.weather.R;

public class TitleBar implements View.OnClickListener {

    private FragmentActivity activity;

    private ImageView titleLeft;
    private TextView titleLabel;
    //private ImageView titleRight;


    public TitleBar(FragmentActivity activity) {
        this.activity = activity;
        initView();
    }

    private void initView() {
        titleLeft = (ImageView) activity.findViewById(R.id.title_bar_left);
        titleLabel = (TextView) activity.findViewById(R.id.title_bar_lable);
        //titleRight = (ImageView) activity.findViewById(R.id.title_bar_right);
        titleLeft.setOnClickListener(this);

    }

    public ImageView getTitleLeft() {
        return titleLeft;
    }

//    public ImageView getTitleRight() {
//        return titleRight;
//    }

    public void setTitleLabel(String title) {
        titleLabel.setText(title);
    }

    public void hideTitleLeft() {
        titleLeft.setVisibility(View.GONE);
    }

//    public void hideTitleRight() {
//        titleRight.setVisibility(View.GONE);
//    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.title_bar_left:
                activity.finish();
                break;
            default:
                break;
        }

    }
}

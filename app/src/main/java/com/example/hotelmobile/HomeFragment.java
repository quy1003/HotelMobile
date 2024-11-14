package com.example.hotelmobile;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hotelmobile.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewFlipper adsViewFlipper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo ViewFlipper từ layout của Fragment
        adsViewFlipper = view.findViewById(R.id.adsViewFlipper);

        List<String> adsArr = new ArrayList<>();
        adsArr.add("https://3.bp.blogspot.com/--2G5-blY_NE/U8LBLJLDwDI/AAAAAAAAIrQ/wQtHbEhbzNI/s1600/luxury-hotel-HD-Wallpapers.jpg");
        adsArr.add("https://th.bing.com/th/id/R.80aea698218905fddad8d5dd851bac54?rik=kdsQoBtEBrZ1yw&riu=http%3a%2f%2fimages.huffingtonpost.com%2f2014-05-09-HuffPo_hotelrestaurants_1.jpeg&ehk=C8ep8WAnAscYD94ZzXG1nCNiTn5MkxR05VlZ29sYrfU%3d&risl=&pid=ImgRaw&r=0");
        adsArr.add("https://th.bing.com/th/id/OIP.VkSuys0o_e-rY6_40bsu6gHaEo?rs=1&pid=ImgDetMain");

        for(int i = 0; i < adsArr.size(); i++){
            ImageView imageView = new ImageView(getContext());
            Glide.with(getContext()).load(adsArr.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            adsViewFlipper.addView(imageView);
        }
        adsViewFlipper.setFlipInterval(2000);
        adsViewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);

        adsViewFlipper.setInAnimation(slide_in);
        adsViewFlipper .setOutAnimation(slide_out);

        return view;
    }

}

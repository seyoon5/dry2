//package com.example.dry.none;
//
//import android.os.Bundle;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.ContextCompat;
//import androidx.viewpager2.widget.ViewPager2;
//
//import com.example.dry.Adapter.ImageSliderAdapter;
//import com.example.dry.R;
//
//public class MainActivity2 extends AppCompatActivity {
//
//    private ViewPager2 sliderViewPager;
//    private LinearLayout layoutIndicator;
//
//    private String[] images = new String[] {
//            "https://cdn.pixabay.com/photo/2018/01/15/08/34/woman-3083453_960_720.jpg",
//            "https://cdn.pixabay.com/photo/2021/06/28/18/36/cereals-6372410_960_720.jpg",
//            "https://cdn.pixabay.com/photo/2021/07/15/17/55/fishermans-bastion-6469128_960_720.jpg",
//            "https://cdn.pixabay.com/photo/2021/07/22/01/01/parrot-6484206_960_720.jpg"
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main2);
//
//        sliderViewPager = findViewById(R.id.sliderViewPager);
//        layoutIndicator = findViewById(R.id.layoutIndicators);
//
//        sliderViewPager.setOffscreenPageLimit(1);
//        sliderViewPager.setAdapter(new ImageSliderAdapter(this, images));
//
//        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                setCurrentIndicator(position);
//            }
//        });
//
//        setupIndicators(images.length);
//    }
//
//    private void setupIndicators(int count) {
//        ImageView[] indicators = new ImageView[count];
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        params.setMargins(16, 8, 16, 8);
//
//        for (int i = 0; i < indicators.length; i++) {
//            indicators[i] = new ImageView(this);
//            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,
//                    R.drawable.bg_indicator_inactive));
//            indicators[i].setLayoutParams(params);
//            layoutIndicator.addView(indicators[i]);
//        }
//        setCurrentIndicator(0);
//    }
//
//    private void setCurrentIndicator(int position) {
//        int childCount = layoutIndicator.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
//            if (i == position) {
//                imageView.setImageDrawable(ContextCompat.getDrawable(
//                        this,
//                        R.drawable.bg_indicator_active
//                ));
//            } else {
//                imageView.setImageDrawable(ContextCompat.getDrawable(
//                        this,
//                        R.drawable.bg_indicator_inactive
//                ));
//            }
//        }
//    }
//}

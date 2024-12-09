package com.example.hotelmobile.adapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;

import java.util.List;

import io.getstream.photoview.PhotoView;

public class ImagePagerAdapter extends PagerAdapter {
    private Context context;
    private List<String> imageUrls;

    public ImagePagerAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return imageUrls.size();  // Trả về số lượng ảnh trong danh sách
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // Xác định nếu view và object giống nhau (phải trả về true nếu view này là một phần của object)
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String imageUrl = imageUrls.get(position);

        // Tạo PhotoView để hiển thị ảnh có thể phóng to
        PhotoView photoView = new PhotoView(context);
        Glide.with(context)
                .load(imageUrl)
                .into(photoView);

        // Thêm PhotoView vào container của ViewPager
        container.addView(photoView);

        return photoView; // Trả về đối tượng này để quản lý
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Xóa PhotoView khỏi container khi không còn dùng
        container.removeView((View) object);
    }
}

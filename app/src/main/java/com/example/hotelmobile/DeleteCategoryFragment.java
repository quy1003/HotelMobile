package com.example.hotelmobile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hotelmobile.databaseHelper.CategoryDBHelper;
import com.example.hotelmobile.model.Category;

import java.util.ArrayList;
import java.util.List;

public class DeleteCategoryFragment extends Fragment {

    private ListView listCategories;
    private Button btnDeleteCategory;
    private CategoryDBHelper categoryHelper;
    private ArrayAdapter<String> adapter;
    private List<Category> categoryList; // Danh sách đầy đủ Category
    private List<String> categoryNames; // Tên Category để hiển thị

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_category, container, false);

        // Ánh xạ các view
        listCategories = view.findViewById(R.id.listCategories);
        btnDeleteCategory = view.findViewById(R.id.btnDeleteCategory);

        // Khởi tạo CategoryDBHelper
        categoryHelper = new CategoryDBHelper();
        categoryList = new ArrayList<>();
        categoryNames = new ArrayList<>();

        // Tải dữ liệu Category từ Firebase
        loadCategories();

        // Xử lý sự kiện xóa
        btnDeleteCategory.setOnClickListener(v -> {
            List<Category> selectedCategories = new ArrayList<>();
            for (int i = 0; i < listCategories.getCount(); i++) {
                if (listCategories.isItemChecked(i)) {
                    selectedCategories.add(categoryList.get(i)); // Thêm Category vào danh sách
                }
            }

            if (!selectedCategories.isEmpty()) {
                categoryHelper.deleteCategories(selectedCategories);

                // Cập nhật danh sách sau khi xóa
                new android.os.Handler().postDelayed(this::loadCategories, 1000); // Đợi Firebase xóa xong
                Toast.makeText(getContext(), "Selected categories deleted successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "No category selected!", Toast.LENGTH_SHORT).show();
            }
        });




        return view;
    }

    private void loadCategories() {
        categoryHelper.getAllCategories(new CategoryDBHelper.DataStatus() {
            @Override
            public void onDataLoaded(List<Category> categories) {
                Log.d("DeleteCategoryFragment", "onDataLoaded called. Categories count: " + categories.size());

                // Clear dữ liệu cũ
                categoryList.clear();
                categoryNames.clear();

                // Thêm dữ liệu mới vào danh sách
                categoryList.addAll(categories);
                for (Category category : categories) {
                    categoryNames.add(category.getName());
                }

                Log.d("DeleteCategoryFragment", "Categories names: " + categoryNames);

                // Đảm bảo cập nhật UI trên UI thread
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Cập nhật adapter
                        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_multiple_choice, categoryNames);
                        listCategories.setAdapter(adapter);
                        listCategories.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                        // Thông báo cho adapter biết dữ liệu đã thay đổi
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e("DeleteCategoryFragment", "Failed to load categories: " + error);
                Toast.makeText(getContext(), "Failed to load categories: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }



}

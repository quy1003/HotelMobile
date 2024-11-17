package com.example.hotelmobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.hotelmobile.R;
import com.example.hotelmobile.databaseHelper.CategoryDBHelper;
import com.example.hotelmobile.model.Category;

import java.util.ArrayList;
import java.util.List;

public class EditCategoryFragment extends Fragment {

    private EditText edtCategoryName;
    private Button btnEditCategory;
    private CategoryDBHelper categoryHelper;
    private List<Category> myList;
    private ArrayAdapter<Category> myAdapter;
    private ListView lv;
    private Category selectedCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_category, container, false);

        // Ánh xạ các thành phần giao diện
        edtCategoryName = view.findViewById(R.id.edtCategoryName);
        btnEditCategory = view.findViewById(R.id.btnEditCategory);
        lv = view.findViewById(R.id.listViewCategoryId);

        // Khởi tạo trợ giúp cơ sở dữ liệu
        categoryHelper = new CategoryDBHelper();

        // Khởi tạo danh sách và adapter
        myList = new ArrayList<>();
        myAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, myList);
        lv.setAdapter(myAdapter);

        // Tải dữ liệu từ Firebase và hiển thị trên ListView
        loadCategories();

        // Xử lý sự kiện chọn item từ ListView
        lv.setOnItemClickListener((parent, view1, position, id) -> {
            selectedCategory = myList.get(position);
            edtCategoryName.setText(selectedCategory.getName());
        });

        // Xử lý sự kiện cập nhật danh mục
        btnEditCategory.setOnClickListener(v -> {
            if (selectedCategory == null) {
                Toast.makeText(getContext(), "Please select a category!", Toast.LENGTH_LONG).show();
                return;
            }

            String categoryName = edtCategoryName.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                selectedCategory.setName(categoryName);
                categoryHelper.updateCategory(selectedCategory);

                // Refresh danh sách
                loadCategories();
                Toast.makeText(getContext(), "Category Updated!", Toast.LENGTH_SHORT).show();
                edtCategoryName.setText("");
            } else {
                Toast.makeText(getContext(), "Category Update Failed! Name cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    /**
     * Tải danh sách danh mục từ Firebase và cập nhật giao diện.
     */
    private void loadCategories() {
        categoryHelper.getAllCategories(new CategoryDBHelper.DataStatus() {
            @Override
            public void onDataLoaded(List<Category> categories) {
                myList.clear();
                myList.addAll(categories);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Failed to load categories: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
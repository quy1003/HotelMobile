package com.example.hotelmobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.hotelmobile.databaseHelper.CategoryDBHelper;
import com.example.hotelmobile.model.Category;

public class AddCategoryFragment extends Fragment {

    private EditText edtCategoryName;
    private Button btnAddCategory;
    private CategoryDBHelper categoryHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);

        // Ánh xạ các view
        edtCategoryName = view.findViewById(R.id.edtCategoryName);
        btnAddCategory = view.findViewById(R.id.btnAddCategory);

        // Khởi tạo CategoryDBHelper
        categoryHelper = new CategoryDBHelper();

        // Xử lý sự kiện khi nhấn nút Add Category
        btnAddCategory.setOnClickListener(v -> {
            String categoryName = edtCategoryName.getText().toString().trim();

            if (!categoryName.isEmpty()) {
                // Tạo đối tượng Category và thêm vào database
                Category category = new Category(categoryName);
                categoryHelper.addCategory(category);

                // Thông báo và reset input
                Toast.makeText(getContext(), "Category added successfully!", Toast.LENGTH_SHORT).show();
                edtCategoryName.setText("");
            } else {
                Toast.makeText(getContext(), "Please enter a category name!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}

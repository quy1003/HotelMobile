package com.example.hotelmobile.databaseHelper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hotelmobile.model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryDBHelper {

    private final DatabaseReference categoryDatabase;

    public CategoryDBHelper() {
        // Khởi tạo tham chiếu Firebase Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://hotelmobile-d180a-default-rtdb.asia-southeast1.firebasedatabase.app/");
        categoryDatabase = firebaseDatabase.getReference("categories");

    }
    public interface DataStatus {
        void onDataLoaded(List<Category> categories); // Thành công
        void onError(String error); // Thất bại
    }
    /**
     * Thêm một danh mục mới vào Firebase Realtime Database.
     *
     * @param category Đối tượng Category cần thêm.
     */
    public void addCategory(Category category) {
        // Truy vấn danh sách hiện có để tìm ID lớn nhất
        categoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int maxId = 0;

                // Tìm ID lớn nhất trong danh sách hiện tại
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Category existingCategory = categorySnapshot.getValue(Category.class);
                    if (existingCategory != null && existingCategory.getId() > maxId) {
                        maxId = existingCategory.getId();
                    }
                }

                // Tạo ID mới lớn hơn ID lớn nhất hiện có
                int newId = maxId + 1;
                category.setId(newId);

                // Lưu danh mục mới vào Firebase
                categoryDatabase.child(String.valueOf(newId)).setValue(category)
                        .addOnSuccessListener(aVoid -> {
                            System.out.println("Category added successfully to Firebase with ID: " + newId);
                        })
                        .addOnFailureListener(e -> {
                            System.err.println("Failed to add category to Firebase: " + e.getMessage());
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Failed to fetch categories: " + error.getMessage());
            }
        });
    }



    public void getAllCategories(DataStatus dataStatus) {
        categoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> categories = new ArrayList<>();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    if (category != null) {
                        categories.add(category);
                    }
                }
                dataStatus.onDataLoaded(categories); // Trả kết quả qua callback
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dataStatus.onError(error.getMessage()); // Thông báo lỗi qua callback
            }
        });
    }

    /**
     * Cập nhật danh mục.
     *
     * @param category Đối tượng Category với thông tin mới.
     */
    public void updateCategory(Category category) {
        String categoryId = String.valueOf(category.getId());
        categoryDatabase.child(categoryId).setValue(category)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Category updated successfully.");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Failed to update category: " + e.getMessage());
                });
    }

    public void deleteCategories(List<Category> categoriesToDelete) {
        for (Category category : categoriesToDelete) {
            String categoryId = String.valueOf(category.getId());
            categoryDatabase.child(categoryId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("Category deleted successfully with ID: " + categoryId);
                    })
                    .addOnFailureListener(e -> {
                        System.err.println("Failed to delete category with ID " + categoryId + ": " + e.getMessage());
                    });
        }
    }









    /**
     * Interface lắng nghe dữ liệu danh mục.
     */
    public interface OnCategoryDataListener {
        void onSuccess(List<Category> categories);

        void onFailure(Exception e);
    }
}

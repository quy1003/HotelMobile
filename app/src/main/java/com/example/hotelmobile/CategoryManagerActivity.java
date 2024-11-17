package com.example.hotelmobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.hotelmobile.AddCategoryFragment;
//import com.example.hotelmobile.EditCategoryFragment;
//import com.example.hotelmobile.DeleteCategoryFragment;

public class CategoryManagerActivity extends AppCompatActivity {

    private Button btnAddCategory, btnEditCategory, btnDeleteCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_manager);

        btnAddCategory = findViewById(R.id.btnAddCategory);
        btnEditCategory = findViewById(R.id.btnEditCategory);
        btnDeleteCategory = findViewById(R.id.btnDeleteCategory);

        // Set event listeners for buttons
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AddCategoryFragment());
            }
        });

        btnEditCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new EditCategoryFragment());
            }
        });

        btnDeleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new DeleteCategoryFragment());
            }
        });
    }

    // Method to load a fragment dynamically
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment); // Replace the container view with the fragment
        transaction.addToBackStack(null); // Add the transaction to the back stack
        transaction.commit();
    }
}

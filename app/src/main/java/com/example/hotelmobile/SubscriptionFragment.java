package com.example.hotelmobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hotelmobile.R;
import com.example.hotelmobile.adapter.RoomAdapter;
import com.example.hotelmobile.model.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubscriptionFragment extends Fragment {
    private Spinner spinnerCategory, spinnerLocation;
    private EditText edtMinPrice, edtMaxPrice;
    private Button btnSearch;
    private ListView listViewSearchResults;

    private DatabaseReference databaseReference;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;
    private int selectedCategoryId = -1;
    private String selectedCategoryName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy dữ liệu từ arguments
        if (getArguments() != null) {
            selectedCategoryId = getArguments().getInt("categoryId", -1);
            selectedCategoryName = getArguments().getString("categoryName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://hotelmobile-d180a-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseReference = database.getReference();

        // Initialize views
        initializeViews(view);

        // Initialize room list and adapter
        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(getContext(), roomList);
        listViewSearchResults.setAdapter(roomAdapter);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);


        // Kiểm tra và set giá trị category được chọn SAU KHI đã load xong categories
        if (selectedCategoryId != -1 && selectedCategoryName != null) {
            // Đợi một chút để đảm bảo adapter đã được set
            spinnerCategory.post(() -> {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerCategory.getAdapter();
                if (adapter != null) {
                    for (int i = 0; i < adapter.getCount(); i++) {
                        if (adapter.getItem(i).equals(selectedCategoryName)) {
                            spinnerCategory.setSelection(i);
                            break;
                        }
                    }
                    // Thực hiện tìm kiếm sau khi đã chọn category
                    performSearch();
                }
            });
        }

        // Load spinners data
        loadCategories();
        loadLocations();


        // Set up search button click listener
        btnSearch.setOnClickListener(v -> performSearch());
    }

    private void initializeViews(View view) {
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerLocation = view.findViewById(R.id.spinnerLocation);
        edtMinPrice = view.findViewById(R.id.edtMinPrice);
        edtMaxPrice = view.findViewById(R.id.edtMaxPrice);
        btnSearch = view.findViewById(R.id.btnSearch);
        listViewSearchResults = view.findViewById(R.id.listViewSearchResults);
    }

    private void loadCategories() {
        databaseReference.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> categories = new ArrayList<>();
                categories.add("All Categories"); // Default option

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    if (categorySnapshot.exists() &&
                            categorySnapshot.child("name").exists() &&
                            categorySnapshot.getValue() != null) {

                        String categoryName = categorySnapshot.child("name").getValue(String.class);
                        if (categoryName != null) {
                            categories.add(categoryName);
                        }
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_spinner_item,
                        categories
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);

                // Sau khi load xong categories và set adapter, mới set selection
                if (selectedCategoryName != null) {
                    for (int i = 0; i < categories.size(); i++) {
                        if (categories.get(i).equals(selectedCategoryName)) {
                            spinnerCategory.setSelection(i);
                            // Thực hiện tìm kiếm sau khi đã chọn category
                            performSearch();
                            break;
                        }
                    }
                }
                spinnerCategory.setAdapter(adapter);

                // Thêm kiểm tra null
                if (selectedCategoryName != null && spinnerCategory.getAdapter() != null) {
                    for (int i = 0; i < categories.size(); i++) {
                        if (categories.get(i).equals(selectedCategoryName)) {
                            spinnerCategory.setSelection(i);
                            // Chỉ thực hiện search khi cả 2 spinner đã được khởi tạo
                            if (spinnerLocation != null && spinnerLocation.getAdapter() != null) {
                                performSearch();
                            }
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading categories: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLocations() {
        databaseReference.child("hotels").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Set<String> uniqueLocations = new HashSet<>();
//                uniqueLocations.add("All Locations"); // Default option
//
//                // Xử lý theo cấu trúc JSON hiện có
//                for (DataSnapshot hotelSnapshot : snapshot.getChildren()) {
//                    if (hotelSnapshot.child("location").exists()) {
//                        String location = hotelSnapshot.child("location").getValue(String.class);
//                        if (location != null && !location.isEmpty()) {
//                            // Thêm location từ khách sạn The Riversee
//                            uniqueLocations.add(location);
//                        }
//                    }
//                }
//
//                List<String> locations = new ArrayList<>(uniqueLocations);
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                        getContext(),
//                        android.R.layout.simple_spinner_item,
//                        locations
//                );
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spinnerLocation.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Error loading locations: " + error.getMessage(),
//                        Toast.LENGTH_SHORT).show();
//            }
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> locations = new ArrayList<>();
                locations.add("All Locations"); // Thêm option mặc định vào đầu list

                // Xử lý dữ liệu từ Firebase
                for (DataSnapshot hotelSnapshot : snapshot.getChildren()) {
                    if (hotelSnapshot.child("location").exists()) {
                        String location = hotelSnapshot.child("location").getValue(String.class);
                        if (location != null && !location.isEmpty() && !locations.contains(location)) {
                            locations.add(location);
                        }
                    }
                }

                // Tạo và set adapter cho spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_spinner_item,
                        locations
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLocation.setAdapter(adapter);

                // Set selection mặc định là "All Locations"
                spinnerLocation.setSelection(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading locations: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch() {
        if (spinnerCategory == null || spinnerLocation == null ||
                spinnerCategory.getSelectedItem() == null ||
                spinnerLocation.getSelectedItem() == null) {
            return;
        }

        String selectedCategory = spinnerCategory.getSelectedItem().toString();
        String selectedLocation = spinnerLocation.getSelectedItem().toString();
        String minPriceStr = edtMinPrice.getText().toString();
        String maxPriceStr = edtMaxPrice.getText().toString();

        int minPrice = minPriceStr.isEmpty() ? 0 : Integer.parseInt(minPriceStr);
        int maxPrice = maxPriceStr.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(maxPriceStr);

        // Xử lý theo cấu trúc JSON hiện có
        databaseReference.child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomList.clear();

                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    // Kiểm tra điều kiện available
                    Boolean isAvailable = roomSnapshot.child("available").getValue(Boolean.class);
                    if (isAvailable != null && isAvailable) {
                        // Lấy thông tin category
                        String categoryName = roomSnapshot.child("category")
                                .child("name").getValue(String.class);

                        // Lấy thông tin location của hotel
                        String location = roomSnapshot.child("hotel")
                                .child("location").getValue(String.class);

                        // Lấy giá phòng
                        Integer price = roomSnapshot.child("pricePerNight").getValue(Integer.class);

                        boolean matchesCategory = selectedCategory.equals("All Categories") ||
                                (categoryName != null && categoryName.equals(selectedCategory));

                        boolean matchesLocation = selectedLocation.equals("All Locations") ||
                                (location != null && location.equals(selectedLocation));

                        boolean matchesPrice = price != null &&
                                price >= minPrice && price <= maxPrice;

                        if (matchesCategory && matchesLocation && matchesPrice) {
                            Room room = roomSnapshot.getValue(Room.class);
                            if (room != null) {
                                roomList.add(room);
                            }
                        }
                    }
                }

                roomAdapter.notifyDataSetChanged();

                if (roomList.isEmpty()) {
                    Toast.makeText(getContext(), "No rooms found matching your criteria",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error searching rooms: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
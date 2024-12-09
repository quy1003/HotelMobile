package com.example.hotelmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotelmobile.R;
import com.example.hotelmobile.adapter.CategoryAdapter;
import com.example.hotelmobile.adapter.HotelAdapter;
import com.example.hotelmobile.adapter.TopRatedHotelAdapter;
import com.example.hotelmobile.model.Category;
import com.example.hotelmobile.model.Hotel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    // Views
    private ViewFlipper adsViewFlipper;
    private EditText searchEditText;
    private ImageButton searchButton;
    private RecyclerView topRatedHotelsRecyclerView;
    private RecyclerView categoriesRecyclerView;
    private ListView hotelListView;
    private TextView seeAllHotels;

    // Adapters
    private TopRatedHotelAdapter topRatedHotelAdapter;
    private CategoryAdapter categoryAdapter;
    private HotelAdapter hotelAdapter;

    // Firebase
    private DatabaseReference databaseReference;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initializeFirebase();
        initializeViews(view);
        setupViewFlipper();
        setupRecyclerViews();
        setupClickListeners();
        loadData();
        return view;
    }

    private void initializeFirebase() {
        databaseReference = FirebaseDatabase.getInstance("https://hotelmobile-d180a-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference();
    }

    private void initializeViews(View view) {
        // Find all views
        adsViewFlipper = view.findViewById(R.id.adsViewFlipper);
        topRatedHotelsRecyclerView = view.findViewById(R.id.topRatedHotelsRecyclerView);
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView);
        hotelListView = view.findViewById(R.id.hotelListView);
        seeAllHotels = view.findViewById(R.id.seeAllHotels);
    }

    private void setupViewFlipper() {
        List<String> adsArr = new ArrayList<>();
        adsArr.add("https://3.bp.blogspot.com/--2G5-blY_NE/U8LBLJLDwDI/AAAAAAAAIrQ/wQtHbEhbzNI/s1600/luxury-hotel-HD-Wallpapers.jpg");
        adsArr.add("https://th.bing.com/th/id/R.80aea698218905fddad8d5dd851bac54?rik=kdsQoBtEBrZ1yw&riu=http%3a%2f%2fimages.huffingtonpost.com%2f2014-05-09-HuffPo_hotelrestaurants_1.jpeg&ehk=C8ep8WAnAscYD94ZzXG1nCNiTn5MkxR05VlZ29sYrfU%3d&risl=&pid=ImgRaw&r=0");
        adsArr.add("https://th.bing.com/th/id/OIP.VkSuys0o_e-rY6_40bsu6gHaEo?rs=1&pid=ImgDetMain");

        for (String imageUrl : adsArr) {
            ImageView imageView = new ImageView(getContext());
            Glide.with(getContext()).load(imageUrl).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            adsViewFlipper.addView(imageView);
        }

        adsViewFlipper.setFlipInterval(2000);
        adsViewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);
        adsViewFlipper.setInAnimation(slide_in);
        adsViewFlipper.setOutAnimation(slide_out);
    }

    private void setupRecyclerViews() {
        // Top Rated Hotels RecyclerView
        topRatedHotelAdapter = new TopRatedHotelAdapter();
        topRatedHotelsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        topRatedHotelsRecyclerView.setAdapter(topRatedHotelAdapter);

        // Categories RecyclerView
        categoryAdapter = new CategoryAdapter();
        LinearLayoutManager categoriesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoriesRecyclerView.setLayoutManager(categoriesLayoutManager);
        categoriesRecyclerView.setAdapter(categoryAdapter);
    }

    private void setupClickListeners() {
        seeAllHotels.setOnClickListener(v -> showAllHotels());
    }

    private void loadData() {
        loadTopRatedHotels();
        loadCategories();
        loadAllHotels();
    }

    private void loadTopRatedHotels() {
        DatabaseReference hotelsRef = databaseReference.child("hotels");
        DatabaseReference commentsRef = databaseReference.child("comments");

        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot commentsSnapshot) {
                Map<String, double[]> hotelRatings = new HashMap<>();

                // Calculate ratings from comments
                for (DataSnapshot hotelCommentsSnapshot : commentsSnapshot.getChildren()) {
                    String hotelId = hotelCommentsSnapshot.getKey();
                    double[] ratingData = {0, 0}; // [totalRating, count]

                    for (DataSnapshot commentSnapshot : hotelCommentsSnapshot.getChildren()) {
                        if (commentSnapshot.child("rating").exists()) {
                            double rating = commentSnapshot.child("rating").getValue(Double.class);
                            ratingData[0] += rating;
                            ratingData[1]++;
                        }
                    }

                    hotelRatings.put(hotelId, ratingData);
                }

                // Get hotel details and sort by rating
                hotelsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot hotelsSnapshot) {
                        List<Hotel> hotels = new ArrayList<>();

                        for (DataSnapshot hotelSnapshot : hotelsSnapshot.getChildren()) {
                            Hotel hotel = hotelSnapshot.getValue(Hotel.class);
                            if (hotel != null) {
                                String hotelId = hotelSnapshot.getKey();
                                double[] ratingData = hotelRatings.get(hotelId);

                                if (ratingData != null && ratingData[1] > 0) {
                                    double averageRating = ratingData[0] / ratingData[1];
                                    hotel.setAverageRating((float) averageRating);
                                    hotel.setTotalRatings((int) ratingData[1]);
                                    hotels.add(hotel);
                                }
                            }
                        }

                        // Sort by rating (highest to lowest)
                        Collections.sort(hotels, (h1, h2) ->
                                Float.compare(h2.getAverageRating(), h1.getAverageRating()));

                        topRatedHotelAdapter.setHotels(hotels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error loading hotels: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading comments: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategories() {
        databaseReference.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> categories = new ArrayList<>();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    if (categorySnapshot.exists() && categorySnapshot.getValue() != null) {
                        Category category = categorySnapshot.getValue(Category.class);
                        if (category != null) {
                            categories.add(category);
                        }
                    }
                }
                categoryAdapter.setCategories(categories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading categories: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllHotels() {
        databaseReference.child("hotels").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Hotel> hotels = new ArrayList<>();
                for (DataSnapshot hotelSnapshot : snapshot.getChildren()) {
                    Hotel hotel = hotelSnapshot.getValue(Hotel.class);
                    if (hotel != null) {
                        hotels.add(hotel);
                    }
                }
                hotelAdapter = new HotelAdapter(getContext(), hotels);
                hotelListView.setAdapter(hotelAdapter);
                hotelListView.setOnItemClickListener((parent, view, position, id) -> {
                    Hotel selectedHotel = hotels.get(position);
                    // Chuyển đến HotelDetail activity với ID của khách sạn được chọn
                    Intent intent = new Intent(getContext(), HotelDetail.class);
                    intent.putExtra("hotel_id", selectedHotel.getHotelId());
                    startActivity(intent);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading hotels: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        // Implement search functionality
        Toast.makeText(getContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();
    }

    private void showAllHotels() {
        // Implement show all hotels functionality
        Toast.makeText(getContext(), "Showing all hotels", Toast.LENGTH_SHORT).show();
    }
}
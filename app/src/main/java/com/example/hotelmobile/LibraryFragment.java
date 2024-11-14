//package com.example.hotelmobile;
//
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//
//import android.provider.MediaStore;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.hotelmobile.databaseHelper.UserDBHelper;
//import com.example.hotelmobile.model.User;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link LibraryFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class LibraryFragment extends Fragment {
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public LibraryFragment() {
//        // Required empty public constructor
//    }
//    //------------- Khai báo Components ---------------//
//    EditText edtFullName, edtUserName, edtPassword, edtEmail, edtRole;
//    Button btnRegister, btnChooseImage;
//    TextView textViewImage;
//    //-------------------------------//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment LibraryFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static LibraryFragment newInstance(String param1, String param2) {
//        LibraryFragment fragment = new LibraryFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_library, container, false);
//
//        // Khởi tạo các thành phần UI
//        edtFullName = view.findViewById(R.id.edtName);
//        edtUserName = view.findViewById(R.id.edtUsername);
//        edtPassword = view.findViewById(R.id.edtPassword);
//        edtEmail = view.findViewById(R.id.edtEmail);
////        edtRole = view.findViewById(R.id.edtRole);
//        btnRegister = view.findViewById(R.id.btnRegister);
//        btnChooseImage = view.findViewById(R.id.button_select_file);
//        textViewImage = view.findViewById(R.id.text_file_name);
//
//        // Xử lý sự kiện chọn ảnh
//        btnChooseImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Mở trình chọn ảnh
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent, 100);
//            }
//        });
//
//        // Xử lý sự kiện khi nhấn nút đăng ký
//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Lấy dữ liệu từ các trường nhập liệu
//                String fullName = edtFullName.getText().toString();
//                String userName = edtUserName.getText().toString();
//                String password = edtPassword.getText().toString();
//                String email = edtEmail.getText().toString();
////                String role = edtRole.getText().toString();
//                String avatar = textViewImage.getText().toString();  // Lấy đường dẫn ảnh từ TextView (đã lưu trong onActivityResult)
//
//                // Khởi tạo đối tượng User
//                User user = new User();
//                user.setName(fullName);
//                user.setUserName(userName);
//                user.setPassword(password);
//                user.setEmail(email);
////                user.setRole(role);
//                user.setAvatar(avatar);
//
//                // Thêm user vào database
//                UserDBHelper dbHelper = new UserDBHelper(getContext());
//                dbHelper.addUser(user);
//
//                // Thông báo đăng ký thành công
//                Toast.makeText(getContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
//                //Set empty cho Edit Text
//                edtFullName.setText("");
//                edtUserName.setText("");
//                edtPassword.setText("");
//                edtEmail.setText("");
////                edtRole.setText("");
//            }
//        });
//
//        return view;
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == getActivity().RESULT_OK && requestCode == 100) {
//            // Kiểm tra xem có dữ liệu trả về từ Intent hay không
//            if (data != null) {
//                // Lấy URI của ảnh đã chọn
//                Uri selectedImageUri = data.getData();
//                if (selectedImageUri != null) {
//                    // Lấy tên file từ URI
//                    String fileName = getFileName(selectedImageUri);
//                    // Hiển thị tên file vào TextView
//                    textViewImage.setText(fileName);
//                }
//            }
//        }
//    }
//    public String getFileName(Uri uri) {
//        String fileName = null;
//        String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
//        try (Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null)) {
//            if (cursor != null && cursor.moveToFirst()) {
//                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
//                fileName = cursor.getString(columnIndex);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return fileName != null ? fileName : "Unknown";
//    }
//}
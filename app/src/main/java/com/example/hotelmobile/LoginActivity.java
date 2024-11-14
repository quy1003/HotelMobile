//package com.example.hotelmobile;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.example.hotelmobile.databaseHelper.UserDBHelper;
//import com.example.hotelmobile.model.User;
//
//public class LoginActivity extends AppCompatActivity {
//    EditText edtUsername,edtPassword;
//    Button btnLogin;
//    UserDBHelper dbHelper;
//    SharedPreferences sharedPreferences;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_login);
//
//        //Ánh xạ ID
//        edtUsername = findViewById(R.id.edtUsername);
//        edtPassword = findViewById(R.id.edtPassword);
//        btnLogin = findViewById(R.id.btnLogin);
//        dbHelper = new UserDBHelper(this);
//        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        //
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String username = edtUsername.getText().toString();
//                String password = edtPassword.getText().toString();
//
//                //Kiểm tra thông tin user
//                User user = dbHelper.checkLogin(username, password);
//
//                if(user!=null){
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putInt("user_id", user.getId());
//                    editor.putString("user_name", user.getName());
//                    editor.putString("user_username", user.getUserName());
//                    editor.putString("user_role", user.getRole());
//                    editor.putString("user_avatar", user.getAvatar());
//                    editor.putString("user_email", user.getEmail());
//                    editor.putBoolean("is_logged_in", true); // Đánh dấu trạng thái đăng nhập
//                    editor.apply();
//                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_LONG).show();
//                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                    finish();
//                }
//                else{
//                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_LONG).show();
//                }
//
//
//            }
//        });
//
//        //
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//}
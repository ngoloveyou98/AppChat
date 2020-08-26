package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText mEdtUser, mEdtEmail, mEdtPass;
    private Button mBtnRegister;
    private RadioButton mRbtnNam, mRbtnNu;
    private RadioGroup RGGioiTinh;
    private String sex;
    //private String mUser = "", mEmail = "", mPassword = "";

    private DatabaseReference mReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Anhxa();
        mAuth = FirebaseAuth.getInstance();
//        mRbtnNam.setChecked(true);
        initAction();
    }

    private void register(final String username, String email, String password, final String sex) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            String userid = firebaseUser.getUid();
                            mReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("userid", userid);
                            hashMap.put("username", username);
                            hashMap.put("imageUrl", "default");
                            hashMap.put("search", username.toLowerCase());
                            hashMap.put("sex",sex);

                            mReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Emai hoặc password không đúng định dạng", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void initAction() {

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mUser = mEdtUser.getText().toString();
                String mEmail = mEdtEmail.getText().toString();
                String mPassword = mEdtPass.getText().toString();


                if (mUser.equals("") || mEmail.equals("") || mPassword.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng không được để thông tin trống", Toast.LENGTH_SHORT).show();
                } else if (mPassword.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu phải nhiều hơn 6 kí tự", Toast.LENGTH_SHORT).show();
                } else {

                    register(mUser, mEmail, mPassword, sex);
                }
            }
        });

        RGGioiTinh.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {



//                if(i == R.id.radioButtonNam){
//                    Toast.makeText(RegisterActivity.this, "Nam", Toast.LENGTH_SHORT).show();
//                }else if(i == R.id.radioButtonNu){
//                    Toast.makeText(RegisterActivity.this, "Nu", Toast.LENGTH_SHORT).show();
//                }
                doOnDifficultyLevelChanged(radioGroup, i);
            }
        });

    }

    private void doOnDifficultyLevelChanged(RadioGroup group, int i) {
         i = group.getCheckedRadioButtonId();

        if(i == R.id.radioButtonNam){
            sex = "Nam";
        }else if(i == R.id.radioButtonNu){
            sex = "Nữ";
        }
    }
    private void Anhxa() {
        RGGioiTinh = findViewById(R.id.radioGroup);
        mRbtnNam = findViewById(R.id.radioButtonNam);
        mRbtnNu = findViewById(R.id.radioButtonNu);
        mEdtUser = (EditText) findViewById(R.id.editTextUser);
        mEdtEmail = (EditText) findViewById(R.id.editTextEmail);
        mEdtPass = (EditText) findViewById(R.id.editTextPassword);
        mBtnRegister = (Button) findViewById(R.id.buttonRegister);

    }
}

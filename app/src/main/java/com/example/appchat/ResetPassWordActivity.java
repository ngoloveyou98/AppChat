package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ResetPassWordActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private MaterialEditText mEdtSend;
    private Button mBtnReset;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass_word);
        initView();
        setUpToolbar();
        initAction();
    }

    private void initAction() {
        firebaseAuth = FirebaseAuth.getInstance();
        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = mEdtSend.getText().toString().trim();
                if (mail.equals("")){
                    Toast.makeText(ResetPassWordActivity.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ResetPassWordActivity.this, "Reset thành công", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPassWordActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    private void initView() {
        mToolbar    = findViewById(R.id.toolbarReset);
        mEdtSend    = findViewById(R.id.editTextSendEmail);
        mBtnReset   = findViewById(R.id.buttonReset);

    }

    private void setUpToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResetPassWordActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }

}

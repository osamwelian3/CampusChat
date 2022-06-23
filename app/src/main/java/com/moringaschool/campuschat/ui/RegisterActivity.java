package com.moringaschool.campuschat.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moringaschool.campuschat.R;
import com.moringaschool.campuschat.Utilities.MemoryData;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://campuschat-6075f-default-rtdb.firebaseio.com/");

    @BindView(R.id.name)
    EditText nameInput;
    @BindView(R.id.phone)
    EditText phoneInput;
    @BindView(R.id.email)
    EditText emailInput;
    @BindView(R.id.registerButton)
    AppCompatButton registerButton;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        if (!MemoryData.getPhone(this).isEmpty()){
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.putExtra("email", MemoryData.getEmail(this));
            intent.putExtra("name", MemoryData.getName(this));
            intent.putExtra("phone", MemoryData.getPhone(this));
            startActivity(intent);
            finish();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == registerButton){
            progressDialog.show();
            String name = nameInput.getText().toString();
            String phone = phoneInput.getText().toString();
            String email = emailInput.getText().toString();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty()){
                Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("users").hasChild(phone)){
                            Toast.makeText(RegisterActivity.this, "User with that phone already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            databaseReference.child("users").child(phone).child("name").setValue(name);
                            databaseReference.child("users").child(phone).child("email").setValue(email);
                            databaseReference.child("users").child(phone).child("profile_pic").setValue("");

                            // save phone number to memory
                            MemoryData.saveName(name, RegisterActivity.this);
                            MemoryData.savePhone(phone, RegisterActivity.this);
                            MemoryData.saveEmail(email, RegisterActivity.this);

                            Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.putExtra("phone", phone);
                            intent.putExtra("name", name);
                            intent.putExtra("phone", phone);
                            startActivity(intent);
                            finish();
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegisterActivity.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        }
    }
}
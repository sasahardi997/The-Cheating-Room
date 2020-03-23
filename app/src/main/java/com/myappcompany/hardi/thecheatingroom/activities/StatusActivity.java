package com.myappcompany.hardi.thecheatingroom.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myappcompany.hardi.thecheatingroom.R;

import java.util.HashMap;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private Button mSaveBtn;
    private ProgressDialog mProgress;

    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mToolbar = findViewById(R.id.status_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = mCurrentUser.getUid();

        mStatus = findViewById(R.id.status_input);
        mSaveBtn = findViewById(R.id.status_save_btn);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we save the changes");
                mProgress.show();

               String status = mStatus.getEditText().getText().toString();

               mStatusDatabase = FirebaseDatabase.getInstance().getReference("Users").child(uid);
               mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           mProgress.dismiss();
                           Toast.makeText(StatusActivity.this, "Status updated Successfully!", Toast.LENGTH_SHORT).show();
                           startActivity(new Intent(StatusActivity.this, SettingsActivity.class));
                       } else {
                           mProgress.dismiss();
                           Toast.makeText(StatusActivity.this, task.toString(), Toast.LENGTH_SHORT).show();
                       }
                   }
               });
            }
        });

        mStatusDatabase = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("status");
        mStatusDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mStatus.getEditText().setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

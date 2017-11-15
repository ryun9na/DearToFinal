package com.ryungna.afinal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;



public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Button join;
    Button login;
    EditText email;
    EditText passwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        join = (Button)findViewById(R.id.join);
        login = (Button)findViewById(R.id.login);
        email = (EditText) findViewById(R.id.email);
        passwd = (EditText) findViewById(R.id.passwd);
        mAuth = FirebaseAuth.getInstance(); //firebaseAuh 개체의 공유 인스턴스 가져오기


        //로그인되어있는지 아닌지 확인하는거임
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Toast.makeText(Login.this,user.getEmail(), Toast.LENGTH_SHORT);
                    Intent intent = new Intent(Login.this, MainFrag.class);
                    finish(); //로그인이 되면 로그인액티비티가 사라지는거임
                    startActivity(intent);
                    Toast.makeText(Login.this,"hello", Toast.LENGTH_SHORT);

                } else {
                    // User is signed out
                    Toast.makeText(Login.this,"failed login", Toast.LENGTH_SHORT);

                }
                // ...
            }
        };//endof mAuthListener


        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.createUserWithEmailAndPassword(email.getText().toString(), passwd.getText().toString())
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                UserModel userModel = new UserModel();

                                UserModel.userName = email.getText().toString();

                                UserModel.userId= task.getResult().getUser().getUid();
                                Log.d("asdf", UserModel.userId);
                                FirebaseDatabase.getInstance().getReference().child("user").child(userModel.userId).child("email").setValue(userModel.userName);

                                if (!task.isSuccessful()) {
                                    Toast.makeText(Login.this, "회원가입 실패",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else{//성공적으로 가입이 되면
                                    Toast.makeText(Login.this, "회원가입 성공",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });







            }
        });//end of join.setOnClickListener




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginUser(email.getText().toString(),passwd.getText().toString());


            }
        });//end of login.setOnClickListener




    }//end of onCreate



    public void loginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) { //로그인실패
                            Toast.makeText(Login.this, "로그인 실패",
                                    Toast.LENGTH_SHORT).show();

                        }
                        else {

                            //FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Login.this, "로그인 성공",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



}//end of class
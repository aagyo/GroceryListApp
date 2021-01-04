package com.example.grocerylistapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.grocerylistapp.Model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton navBarListsBtn;
    private ImageButton navBarInspirationBtn;
    private FloatingActionButton addBtn;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        navBarInspirationBtn = (ImageButton) findViewById(R.id.navbar_inspbtn);
        navBarListsBtn = (ImageButton) findViewById(R.id.navbar_listsbtn);
        addBtn = findViewById(R.id.navbar_add);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        uId = mUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Grocery List").child(uId);

        navBarInspirationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You inspiration", Toast.LENGTH_SHORT).show();
            }
        });

        navBarListsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You lists", Toast.LENGTH_SHORT).show();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You add", Toast.LENGTH_SHORT).show();
                customDialog();
            }
        });
        //getSupportActionBar().setTitle("Grocery Shopping List");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.toolbar_add){
            Toast.makeText(getApplicationContext(), "You add", Toast.LENGTH_SHORT).show();}
        else if(id == R.id.toolbar_settings){
            Toast.makeText(getApplicationContext(), "You settings", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void customDialog(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);

        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);

        View myView = inflater.inflate(R.layout.input_data, null);
        AlertDialog dialog = mydialog.create();
        dialog.setView(myView);

        EditText name = myView.findViewById(R.id.edt_name);
        EditText amount = myView.findViewById(R.id.edt_amount);
        EditText note = myView.findViewById(R.id.edt_note);
        Button addInputBtn = myView.findViewById(R.id.inputbtn_add);

        addInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mName = name.getText().toString();
                String mAmount = amount.getText().toString();
                String mNote = note.getText().toString();

                int theAmount = 1;
                if(TextUtils.isEmpty(mName)){
                    name.setError("RequiredField");
                    return;
                }
                if(TextUtils.isEmpty(mAmount)){
                    theAmount= Integer.parseInt(mAmount);
                }
                else{
                    theAmount= Integer.parseInt(mAmount);
                }

                String id = mDatabase.push().getKey();
                Product productToSave = new Product(mName, theAmount, mNote, id);
                assert id != null;
                mDatabase.child(id).setValue(productToSave);
                Toast.makeText(getApplicationContext(), "Added a new product", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        dialog.show();
    }
}

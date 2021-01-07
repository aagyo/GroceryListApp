package com.example.grocerylistapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerylistapp.Model.Product;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

    private RecyclerView recyclerView;
    private  FirebaseRecyclerAdapter<Product, MyViewHolder> adapter;
    private  FirebaseRecyclerOptions<Product> options;

    private String name;
    private int amount;
    private String note;
    private String postKey;

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

        mDatabase.keepSynced(true);

        recyclerView = findViewById(R.id.home_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);

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

    @Override
    protected void onStart() {
        super.onStart();
        options = new FirebaseRecyclerOptions.Builder<Product>().setQuery(mDatabase, Product.class).build();

        adapter = new FirebaseRecyclerAdapter<Product, MyViewHolder>(options) {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
                return new MyViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Product model) {
                holder.setProductName(model.getName());
                holder.setProductNote(model.getNote());
                holder.setAmount(String.valueOf(model.getAmount()));

                holder.itemView.findViewById(R.id.more_options).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(holder.itemView.getContext(), holder.itemView.findViewById(R.id.more_options));
                        popup.inflate(R.menu.options_mnu);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.options_update:
                                        postKey = getRef(position).getKey();
                                        name = model.getName();
                                        amount = model.getAmount();
                                        note = model.getNote();
                                        updateProduct();
                                        break;
                                    case R.id.options_delete:
                                        mDatabase.child(getRef(position).getKey()).removeValue();
                                        break;
                                }
                                return false;
                            }
                        });
                        popup.show();
                    }
                });
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public void updateProduct(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);

        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);

        View mView = inflater.inflate(R.layout.update_product, null);

        AlertDialog dialog = myDialog.create();
        dialog.setView(mView);

        EditText edt_Name = mView.findViewById(R.id.edt_name_upd);
        EditText edt_Amount = mView.findViewById(R.id.edt_amount_upd);
        EditText edt_Note = mView.findViewById(R.id.edt_note_upd);

        edt_Name.setText(name);
        edt_Name.setSelection(name.length());

        edt_Amount.setText(String.valueOf(amount));
        edt_Amount.setSelection(String.valueOf(amount).length());

        edt_Note.setText(note);
        edt_Note.setSelection(note.length());

        Button btnUpdate =  mView.findViewById(R.id.update_btn);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mName = edt_Name.getText().toString();
                String mAmount = edt_Amount.getText().toString();
                String mNote = edt_Note.getText().toString();

                int intAmount = Integer.parseInt(mAmount);

                mDatabase.child(postKey).child("name").setValue(mName);
                mDatabase.child(postKey).child("amount").setValue(intAmount);
                mDatabase.child(postKey).child("note").setValue(mNote);

                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Item updated successfuly", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}

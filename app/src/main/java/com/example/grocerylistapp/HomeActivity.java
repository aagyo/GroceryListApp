package com.example.grocerylistapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerylistapp.Model.Product;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private RecyclerView rusedRecyclerView;
    private FirebaseRecyclerOptions<Product> options;

    private String name;
    private int amount;
    private String note;
    private String postKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Shopping List");

        ImageButton navBarInspirationBtn = (ImageButton) findViewById(R.id.navbar_inspbtn);
        ImageButton navBarListsBtn = (ImageButton) findViewById(R.id.navbar_listsbtn);
        FloatingActionButton addBtn = findViewById(R.id.navbar_add);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uId = mUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Grocery List").child(uId);

        mDatabase.keepSynced(true);

        recyclerView = findViewById(R.id.home_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);

        rusedRecyclerView = findViewById(R.id.rused_recycler);
        LinearLayoutManager ruLayoutManager = new LinearLayoutManager(this);

        ruLayoutManager.setStackFromEnd(true);
        ruLayoutManager.setReverseLayout(true);
        rusedRecyclerView.setLayoutManager(ruLayoutManager);

        fillData();

        navBarInspirationBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), InspirationActivity.class));
                Intent intent = new Intent(getApplicationContext(), InspirationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

//        if(id == R.id.toolbar_add){
//            Toast.makeText(getApplicationContext(), "You add", Toast.LENGTH_SHORT).show();}
        if (id == R.id.toolbar_settings) {
            onBackPressed();
        }
        return true;
    }

    private void customDialog() {
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
                if (TextUtils.isEmpty(mName)) {
                    name.setError("RequiredField");
                    return;
                }
                if (!TextUtils.isEmpty(mAmount)) {
                    try {
                        theAmount = Integer.parseInt(mAmount.trim());
                    } catch (Exception e) {
                        amount.setError("This should be a number");
                        return;
                    }
                }

                String id = mDatabase.push().getKey();
                Product productToSave = new Product(mName, theAmount, mNote, id);
                assert id != null;
                mDatabase.child("ToBuyProducts").child(id).setValue(productToSave);
                Toast.makeText(getApplicationContext(), "Added a new product", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void fillData() {
        this.overridePendingTransition(0, 0);
        options = new FirebaseRecyclerOptions.Builder<Product>().setQuery(mDatabase.child("ToBuyProducts"), Product.class).build();

        FirebaseRecyclerAdapter<Product, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Product, MyViewHolder>(options) {
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

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        name = model.getName();
                        amount = model.getAmount();
                        note = model.getNote();

                        String id = mDatabase.push().getKey();
                        Product productToSave = new Product(name, amount, note, id);
                        assert id != null;
                        mDatabase.child("RecentlyUsedProducts").child(id).setValue(productToSave);
                        mDatabase.child("ToBuyProducts").child(getRef(holder.getAdapterPosition()).getKey()).removeValue();
                    }
                });
                holder.itemView.findViewById(R.id.more_options).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(holder.itemView.getContext(), holder.itemView.findViewById(R.id.more_options));
                        popup.inflate(R.menu.options_mnu);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @SuppressLint("NonConstantResourceId")
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
                                        mDatabase.child("ToBuyProducts").child(getRef(holder.getAdapterPosition()).getKey()).removeValue();
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
        FirebaseRecyclerOptions<Product> ruOptions = new FirebaseRecyclerOptions.Builder<Product>().setQuery(mDatabase.child("RecentlyUsedProducts"), Product.class).build();

        FirebaseRecyclerAdapter<Product, MyViewHolder> ruAdapter = new FirebaseRecyclerAdapter<Product, MyViewHolder>(ruOptions) {

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recentlyused_product, parent, false);
                return new MyViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Product model) {
                holder.setProductName(model.getName());
                holder.setProductNote(model.getNote());
                holder.setAmount(String.valueOf(model.getAmount()));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        name = model.getName();
                        amount = model.getAmount();
                        note = model.getNote();

                        String id = mDatabase.push().getKey();
                        Product productToSave = new Product(name, amount, note, id);
                        assert id != null;
                        mDatabase.child("ToBuyProducts").child(id).setValue(productToSave);
                        mDatabase.child("RecentlyUsedProducts").child(getRef(holder.getAdapterPosition()).getKey()).removeValue();
                    }
                });
                holder.itemView.findViewById(R.id.delete_option).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabase.child("RecentlyUsedProducts").child(getRef(holder.getAdapterPosition()).getKey()).removeValue();
                    }
                });
            }
        };
        adapter.startListening();
        ruAdapter.startListening();
        recyclerView.setAdapter(adapter);
        rusedRecyclerView.setAdapter(ruAdapter);
    }

    public void updateProduct() {
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

        Button btnUpdate = mView.findViewById(R.id.update_btn);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mName = edt_Name.getText().toString();
                String mAmount = edt_Amount.getText().toString();
                String mNote = edt_Note.getText().toString();

                int intAmount;
                try {
                    intAmount = Integer.parseInt(mAmount.trim());
                } catch (Exception e) {
                    edt_Amount.setError("This should be a number");
                    return;
                }

                mDatabase.child("ToBuyProducts").child(postKey).child("name").setValue(mName);
                mDatabase.child("ToBuyProducts").child(postKey).child("amount").setValue(intAmount);
                mDatabase.child("ToBuyProducts").child(postKey).child("note").setValue(mNote);

                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Item updated successfuly", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}

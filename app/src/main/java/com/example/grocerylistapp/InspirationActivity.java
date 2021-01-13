package com.example.grocerylistapp;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.grocerylistapp.Model.ParseItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class InspirationActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton navBarListsBtn;
    private ImageButton navBarInspirationBtn;

    private RecyclerView recyclerView;
    private ParseAdapter parseAdapter;
    private ArrayList<ParseItem> parseItems = new ArrayList<>();
    private static Timer timer = new Timer();
    private static AsyncTask<Void, Void, Void> timerTask;
    private ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspiration);

        progressBar = findViewById(R.id.progressBar);

        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Inspiration");

        navBarInspirationBtn = (ImageButton) findViewById(R.id.navbar_inspbtn);
        navBarListsBtn = (ImageButton) findViewById(R.id.navbar_listsbtn);

        navBarInspirationBtn.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        navBarListsBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#89000000")));

        recyclerView = findViewById(R.id.insp_recycler);
        //maybe
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Context ct = this;
        //Content content = new Content();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                parseAdapter = new ParseAdapter(parseItems, ct);
                recyclerView.setAdapter(parseAdapter);
                new Content().execute();
            }
        }, 0, 60000 * 30);

        navBarListsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
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
    protected void onStart() {
        super.onStart();
        this.overridePendingTransition(0, 0);
    }

    private class Content extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(InspirationActivity.this, android.R.anim.fade_out));
            parseAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(InspirationActivity.this, android.R.anim.fade_in));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String url = "https://foodnetwork.co.uk/recipes/";
                Document doc = Jsoup.connect(url).get();
                Elements data = doc.select("div.card.picture.recipepage.collectionItemBlock");
                int size = data.size();
                if(data.size() >= 5){
                    size = 5;
                }
                for (int i = 0; i < size; i++) {
                    String imgUrl = data.select("div.card.picture.recipepage.collectionItemBlock")
                            .eq(i)
                            .attr("style");
                    imgUrl = imgUrl.replace("background-image: url('", "");
                    imgUrl = imgUrl.replace("')", "");

                    String title = data.select("a.card-link")
                            .eq(i)
                            .attr("title");

                    String ingredientsUrl = data.select("a.card-link")
                            .eq(i)
                            .attr("href");
                    ingredientsUrl = "https://foodnetwork.co.uk" + ingredientsUrl;

                    StringBuilder ingredients = new StringBuilder();

                    Document docc = Jsoup.connect(ingredientsUrl).get();
                    Elements dataa = docc.select("div.recipe-tab-container").eq(0).select("div.ingredient");

                    for(int x = 0; x < dataa.size(); ++x){
                        ingredients.append(dataa
                                .eq(x)
                                .text());
                        ingredients.append("\n");
                    }

                    Elements tableData = data.select("div.card.picture.recipepage.collectionItemBlock")
                            .eq(i)
                            .select("div.recipe-card-content");

                    String prepTime = tableData.select("div.recipe-card-item")
                            .eq(0)
                            .select("div.recipe-card-text")
                            .text();
                    String cookTime = tableData.select("div.recipe-card-item")
                            .eq(1)
                            .select("div.recipe-card-text")
                            .text();
                    String serves = tableData.select("div.recipe-card-item")
                            .eq(2)
                            .select("div.recipe-card-text")
                            .text();
                    String difficulty = tableData.select("div.recipe-card-item")
                            .eq(3)
                            .select("div.recipe-card-text")
                            .text();
                    parseItems.add(new ParseItem(imgUrl, title, prepTime, cookTime, serves, difficulty, ingredients.toString()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}
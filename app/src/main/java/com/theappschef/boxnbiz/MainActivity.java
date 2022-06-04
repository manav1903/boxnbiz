package com.theappschef.boxnbiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private MainAdapter MainAdapter;

    private ProgressBar progressBar;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 35;
    List<User> displayedList;
    SharedPref sharedPref;
    private int currentPage = PAGE_START;

    JSONArray jsonArrayGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = new SharedPref(this);
        if (isOnline()) {
            start();
        } else {

            RecyclerView recyclerView = findViewById(R.id.recyclerview);
            progressBar = findViewById(R.id.progressbar);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            MainAdapter = new MainAdapter(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(MainAdapter);



            EditText editText = findViewById(R.id.edit);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().equals("")) {
                        Constants.Companion.setSearchRunning(false);
                    } else {
                        Constants.Companion.setSearchRunning(true);
                    }
                    filter(editable.toString());
                }
            });
            try {
                displayedList = new ArrayList<>();
                List<User> result = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(sharedPref.getOff());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    User user = new User();
                    user.setImageUrl(jsonObject.getString("avatar_url"));
                    user.setTitle(jsonObject.getString("login"));

                    user.setDesc(jsonObject.getString("id"));
                    result.add(user);
                    displayedList.add(user);
                    Log.d("as", result.toString());
                }
                MainAdapter.addAll(result);
                progressBar.setVisibility(View.GONE);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    void start() {

        jsonArrayGlobal = new JSONArray();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        progressBar = findViewById(R.id.progressbar);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        MainAdapter = new MainAdapter(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(MainAdapter);

        sharedPref.setOff("");

        EditText editText = findViewById(R.id.edit);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {
                    Constants.Companion.setSearchRunning(false);
                } else {
                    Constants.Companion.setSearchRunning(true);
                }
                progressBar.setVisibility(View.GONE);
                filter(editable.toString());
            }
        });
        displayedList = new ArrayList<>();
        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                loadNextPage();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        loadFirstPage();
    }

    private void loadNextPage() {

        Handler handler = new Handler(Looper.getMainLooper());
        getGuy("https://api.github.com/users?since=" + String.valueOf(currentPage * 20), "", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                String results = response.body().string();
                try {
                    handler.post(() -> {
                        try {
                            MainAdapter.removeLoadingFooter();
                            isLoading = false;
                            List<User> result = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(results);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                User user = new User();
                                user.setImageUrl(jsonObject.getString("avatar_url"));
                                user.setTitle(jsonObject.getString("login"));

                                user.setDesc(jsonObject.getString("id"));
                                result.add(user);

                                jsonArrayGlobal.put(jsonObject);

                                displayedList.add(user);
                                Log.d("as", result.toString());
                            }

                            sharedPref.setOff(jsonArrayGlobal.toString());
//                            List<User> results = response.body();
                            MainAdapter.addAll(result);

                            if (currentPage != TOTAL_PAGES) MainAdapter.addLoadingFooter();
                            else isLastPage = true;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void filter(String text) {
        List<User> temp = new ArrayList<>();
        for (User d : displayedList) {
            if (d.getTitle().contains(text)) {
                temp.add(d);
            }
        }
        MainAdapter.updateList(temp);
    }

    private void loadFirstPage() {

        Handler handler = new Handler(Looper.getMainLooper());
        getGuy("https://api.github.com/users?since=" + String.valueOf(currentPage * 20), "", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                String results = response.body().string();
                try {
                    handler.post(() -> {
                        try {
                            List<User> result = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(results);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                User user = new User();
                                user.setImageUrl(jsonObject.getString("avatar_url"));
                                user.setTitle(jsonObject.getString("login"));
                                user.setDesc(jsonObject.getString("id"));
                                result.add(user);
                                displayedList.add(user);
                                jsonArrayGlobal.put(jsonObject);
                            }
                            sharedPref.setOff(jsonArrayGlobal.toString());
                            Log.d("as", result.toString());
                            progressBar.setVisibility(View.GONE);
                            MainAdapter.addAll(result);
                            if (currentPage <= TOTAL_PAGES) MainAdapter.addLoadingFooter();
                            else isLastPage = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    OkHttpClient client = new OkHttpClient();

    void getGuy(String url, String json, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
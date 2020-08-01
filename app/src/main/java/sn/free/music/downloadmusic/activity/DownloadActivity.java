package sn.free.music.downloadmusic.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import sn.free.music.downloadmusic.R;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sn.free.music.downloadmusic.adapter.AdapterMusicSearch;
import sn.free.music.downloadmusic.adapter.AdapterSearch;
import sn.free.music.downloadmusic.interfaces.JsonGetDataByRetrofit;
import sn.free.music.downloadmusic.model.ItemJMusicSearch;
import sn.free.music.downloadmusic.model.ItemIdMusic;
import sn.free.music.downloadmusic.model.Song;
import sn.free.music.downloadmusic.netWork.DownloadVideoAsyn;
import sn.free.music.downloadmusic.netWork.PostIdGetLinkMusic;
import sn.free.music.downloadmusic.netWork.PostLinkToGetId;

public class DownloadActivity extends AppCompatActivity implements DownloadVideoAsyn.EventDownloadVideoCallBack, TextWatcher, TextView.OnEditorActionListener, AdapterMusicSearch.ClickCallBack, AdapterSearch.CallBackClickEvent, View.OnClickListener, PostLinkToGetId.GetIdMusicCallBack, PostIdGetLinkMusic.GetLinkMusicSCallBack {
    private RecyclerView lvSuggestion, lvMusicSearch;
    private EditText edtSearch;
    private Dialog dialog;
    private Dialog dialogDownload;
    private TextView tvProgress;
    private ArrayList<Song> arrMusicSearch = new ArrayList<>();
    private AdapterMusicSearch adapterMusicSearchMusic;

    private InterstitialAd mInterstitialAd;
    private AdView adView;
    private ProgressBar prLoading;
    private String titleMusic = "";
    private ArrayList<String> arrSuggestion = new ArrayList<>();
    private AdapterSearch adapterSearch;
    private ImageView imgBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initViews();
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void initViews() {

        adView = findViewById(R.id.adView);
        MobileAds.initialize(this, getResources().getString(R.string.mobile_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        edtSearch = findViewById(R.id.edt_search);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT);
        edtSearch.addTextChangedListener(this);
        edtSearch.setOnEditorActionListener(this);
        edtSearch.setOnClickListener(this);
        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        prLoading = findViewById(R.id.loading);
        lvMusicSearch = findViewById(R.id.lv_search_music);
        lvSuggestion = findViewById(R.id.lv_suggestion);

        adapterMusicSearchMusic = new AdapterMusicSearch(arrMusicSearch, this);
        adapterMusicSearchMusic.setCallBack(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        lvMusicSearch.setLayoutManager(manager);
        lvMusicSearch.setAdapter(adapterMusicSearchMusic);

        LinearLayoutManager manager1 = new LinearLayoutManager(this);
        manager1.setOrientation(LinearLayoutManager.VERTICAL);
        lvSuggestion.setLayoutManager(manager1);
        adapterSearch = new AdapterSearch(arrSuggestion, this);
        adapterSearch.setCallBack(this);
        lvSuggestion.setAdapter(adapterSearch);

        showSoftKeyboard(edtSearch);
        if (getIntent().getStringExtra("KEY_TAG") != null) {
            edtSearch.setText(getIntent().getStringExtra("KEY_TAG"));
            lvSuggestion.setVisibility(View.GONE);
            lvMusicSearch.setVisibility(View.VISIBLE);
            if (!edtSearch.getText().toString().isEmpty()) {
                loadVideoSearchByApi(edtSearch.getText().toString().trim());
                hideKeyboard(this);

            } else {
                Toast.makeText(this, "" + getResources().getString(R.string.warning_search), Toast.LENGTH_SHORT).show();
            }
            hideKeyboard(this);
        }

        if (getIntent().getStringExtra("HOT_SONGS_TITLE") != null) {
            edtSearch.setText(getIntent().getStringExtra("HOT_SONGS_TITLE"));
            lvSuggestion.setVisibility(View.GONE);
            lvMusicSearch.setVisibility(View.VISIBLE);
            if (!edtSearch.getText().toString().isEmpty()) {
                loadVideoSearchByApi(edtSearch.getText().toString().trim());
                hideKeyboard(this);

            } else {
                Toast.makeText(this, "" + getResources().getString(R.string.warning_search), Toast.LENGTH_SHORT).show();
            }
            hideKeyboard(this);

            if (getIntent().getStringExtra("HOT_SONGS_ID") != null) {
                Log.e("idadfadfafadfa", getIntent().getStringExtra("HOT_SONGS_ID"));
                showDialogGetLink();
                titleMusic = getIntent().getStringExtra("HOT_SONGS_TITLE");
                PostLinkToGetId postLinkToGetId = new PostLinkToGetId();
                postLinkToGetId.setCallBack(this);
                postLinkToGetId.execute("https://www.youtube.com/watch?v=" + getIntent().getStringExtra("HOT_SONGS_ID"));
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!edtSearch.getText().toString().isEmpty()) {
            lvMusicSearch.setVisibility(View.GONE);
            if (lvSuggestion.getVisibility() == View.GONE) {
                lvSuggestion.setVisibility(View.VISIBLE);
            }
            AddResultSearchOkhttp addResultSearchOk = new AddResultSearchOkhttp();
            addResultSearchOk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://suggestqueries.google.com/complete/search?client=youtube&ds=yt&client=firefox&hl=vi&q=" + edtSearch.getText().toString());

        } else {
            lvMusicSearch.setVisibility(View.VISIBLE);
            lvSuggestion.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        lvSuggestion.setVisibility(View.GONE);
        lvMusicSearch.setVisibility(View.VISIBLE);
        if (!edtSearch.getText().toString().isEmpty()) {
            loadVideoSearchByApi(v.getText().toString().trim());
            hideKeyboard(this);
        } else {
            Toast.makeText(this, "" + getResources().getString(R.string.warning_search), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void loadVideoSearchByApi(String key) {
        arrMusicSearch.clear();

        prLoading.setVisibility(View.VISIBLE);
        Toast.makeText(this, "" + key, Toast.LENGTH_SHORT).show();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.googleapis.com/youtube/v3/").addConverterFactory(GsonConverterFactory.create()).build();
        JsonGetDataByRetrofit jsonGetData = retrofit.create(JsonGetDataByRetrofit.class);
        try {
            Call<ItemJMusicSearch> call = jsonGetData.getDataByVideoID("AIzaSyCRt9wIKqvmlQw0ACGrd2fCVMaYzkF3nWE", key);
            call.enqueue(new Callback<ItemJMusicSearch>() {
                @Override
                public void onResponse(Call<ItemJMusicSearch> call, Response<ItemJMusicSearch> response) {
                    if (response != null) {
                        for (int i = 0; i < response.body().getItems().size(); i++) {
                            String id = response.body().getItems().get(i).getId().getVideoId();
                            String title = response.body().getItems().get(i).getSnippet().getTitle();
                            String auth = response.body().getItems().get(i).getSnippet().getChannelTitle();
                            String linkImage = "";
                            try {
                                linkImage = response.body().getItems().get(i).getSnippet().getThumbnails().getMedium().getUrl();
                            } catch (Exception e) {
                                Log.e("errorLoadImage", "error");

                            }
                            arrMusicSearch.add(new Song(id, title, auth, linkImage));
                        }

                        for (int i = 0; i < arrMusicSearch.size(); i++) {
                            Log.e("tiltleMusic", arrMusicSearch.get(i).getTitle());
                        }
                        prLoading.setVisibility(View.GONE);
                        adapterMusicSearchMusic.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<ItemJMusicSearch> call, Throwable t) {
                    Toast.makeText(DownloadActivity.this
                            , "Error", Toast.LENGTH_SHORT).show();
                    prLoading.setVisibility(View.GONE);
                }
            });
            call.clone();
        } catch (Exception ex) {
            Toast.makeText(DownloadActivity.this, "please wait", Toast.LENGTH_SHORT).show();
            prLoading.setVisibility(View.GONE);
        }


    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void clickItemSearch(int position) {
        edtSearch.setText(arrSuggestion.get(position));
        lvSuggestion.setVisibility(View.GONE);
        lvMusicSearch.setVisibility(View.VISIBLE);
        if (!edtSearch.getText().toString().isEmpty()) {
            loadVideoSearchByApi(edtSearch.getText().toString().trim());
            hideKeyboard(this);
        } else {
            Toast.makeText(this, "" + getResources().getString(R.string.warning_search), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void clickImageSearch(int position) {
        edtSearch.setText(arrSuggestion.get(position));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void downloadVideoFinish(String s) {
        dialogDownload.dismiss();
        Toast.makeText(this, "" + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateProgress(final int pro) {
        tvProgress.setText(pro + "%");
    }

    @Override
    public void downloadVideoFail(String fail) {
        dialogDownload.dismiss();
        Toast.makeText(this, "Download fail", Toast.LENGTH_SHORT).show();
    }

    public void showDialogGetLink() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_get_link);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public void onBackPressed() {

        if (mInterstitialAd != null) {
            mInterstitialAd.show();
        } else {
            finish();
        }
    }

    @Override
    public void clickItem(int position) {
        showDialogGetLink();
        titleMusic = arrMusicSearch.get(position).getTitle();
        PostLinkToGetId postLinkToGetId = new PostLinkToGetId();
        postLinkToGetId.setCallBack(this);
        postLinkToGetId.execute("https://www.youtube.com/watch?v=" + arrMusicSearch.get(position).getIdMusic());
    }

    @Override
    public void getIdSuccess(ItemIdMusic itemIdMusic) {
        PostIdGetLinkMusic postIdGetLinkMusic = new PostIdGetLinkMusic();
        postIdGetLinkMusic.setCallBack(this);
        postIdGetLinkMusic.execute(itemIdMusic);

    }

    @Override
    public void getIdFail() {
        Toast.makeText(this, "get Id fail", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void getLinkMusicSuccess(String linkMusic) {
        dialog.dismiss();
        showDialogDownload();
        DownloadVideoAsyn downloadVideoAsyn = new DownloadVideoAsyn(titleMusic, 1, this);
        downloadVideoAsyn.setCallBack(this);
        downloadVideoAsyn.execute(linkMusic);

    }

    @Override
    public void getLinkMusicFail() {
        dialog.dismiss();
        Toast.makeText(this, "error get music!!", Toast.LENGTH_SHORT).show();
    }

    private class AddResultSearchOkhttp extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(strings[0]).build();
            try {
                okhttp3.Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                arrSuggestion.clear();
                JSONArray jsonArray = new JSONArray(s);
                JSONArray jsonArray1 = jsonArray.getJSONArray(1);
                final String[] suggestions = new String[10];
                suggestions.clone();
                for (int i = 0; i < 10; i++) {
                    if (jsonArray1 != null) {
                        suggestions[i] = jsonArray1.get(i).toString();
                    }
                }
                String[] columnNames = {"_id", "suggestion"};
                MatrixCursor cursor = new MatrixCursor(columnNames);
                String[] temp = new String[2];
                int id = 0;


                for (String item : suggestions) {
                    if (item != null) {
                        temp[0] = Integer.toString(id++);
                        temp[1] = item;
                        Log.d("item Search", item);
                        cursor.addRow(temp);
                        setDataSuggestion(temp);
                    }
                }

                for (int i = 0; i < arrSuggestion.size(); i++) {
                    Log.e("arrSuggestion", arrSuggestion.get(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
            adapterSearch.notifyDataSetChanged();
        }

    }

    public void setDataSuggestion(String[] temp) {
        if (temp.length != 0) {
            arrSuggestion.addAll(Arrays.asList(temp));
            for (int i = 0; i < arrSuggestion.size(); i++) {
                Log.d("size", arrSuggestion.get(i));
                try {
                    arrSuggestion.remove(i);
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
        }
    }

    private void showDialogDownload() {
        dialogDownload = new Dialog(this);
        dialogDownload.setContentView(R.layout.dialog_download);
        tvProgress = dialogDownload.findViewById(R.id.tv_progress);
        dialogDownload.show();
    }

}

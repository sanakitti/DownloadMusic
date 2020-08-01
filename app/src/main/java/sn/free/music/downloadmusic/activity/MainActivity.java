package sn.free.music.downloadmusic.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sn.free.music.downloadmusic.BuildConfig;
import sn.free.music.downloadmusic.R;
import sn.free.music.downloadmusic.adapter.AdapterMusicSearch;
import sn.free.music.downloadmusic.interfaces.JsonGetDataByRetrofit;
import sn.free.music.downloadmusic.model.Song;

import java.util.ArrayList;
import java.util.Random;

import static sn.free.music.downloadmusic.activity.DownloadedActivity.UPDATE_MUSIC;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterMusicSearch.ClickCallBack {
    private Random random;
    private final String[] PERMISSION_LIST = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private TextView tvDownloadIntent;
    private AdapterMusicSearch adapterMusicSearch;
    private ArrayList<Song> arrHotSongs = new ArrayList<>();
    private SharedPreferences checkFirst;
    private ImageView imgDownloaded, imgOption;
    private ImageView imgPlayAndPause;
    private RelativeLayout rlSearch;
    private RecyclerView lvHotSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkPermissionAvailable() == false) {
            return;
        }
        initViews();
    }

    private boolean checkPermissionAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String p : PERMISSION_LIST) {
                if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(PERMISSION_LIST, 0);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (checkPermissionAvailable() == true) {
            initViews();
        } else {
            finish();
        }
    }

    public void initViews() {

        checkFirst = getSharedPreferences("checkFirst", MODE_PRIVATE);
        if (checkFirst.getString("checkFirst", "").equals("") || checkFirst.getString("checkFirst", "") == null) {
            checkFirst.edit().putString("checkFirst", "1").commit();
        } else if (checkFirst.getString("checkFirst", "").equals("1")) {
            random = new Random();
            int x = random.nextInt(3);
            Log.e("asdfasdfa", x + "");
            if (x == 1) {
                showDialogRating();
            }
        } else {
            Log.e("asdfadfadf", checkFirst.getString("checkFirst", ""));
        }
        rlSearch = findViewById(R.id.rl_search_song);
        rlSearch.setOnClickListener(this);
        imgPlayAndPause = findViewById(R.id.img_play_and_pause);
        imgPlayAndPause.setOnClickListener(this);
        imgDownloaded = findViewById(R.id.img_downloaded);
        imgOption = findViewById(R.id.img_option);
        imgOption.setOnClickListener(this);
        imgDownloaded.setOnClickListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_MUSIC);
        tvDownloadIntent = findViewById(R.id.tv_download_intent);
        tvDownloadIntent.setOnClickListener(this);
        lvHotSongs = findViewById(R.id.lv_hot_songs);
        LinearLayoutManager layoutManagerHotSongs = new LinearLayoutManager(this);
        layoutManagerHotSongs.setOrientation(LinearLayoutManager.VERTICAL);
        lvHotSongs.setLayoutManager(layoutManagerHotSongs);
        adapterMusicSearch = new AdapterMusicSearch(arrHotSongs, this);
        adapterMusicSearch.setCallBack(this);
        lvHotSongs.setAdapter(adapterMusicSearch);
        loadHotSongs();

    }


    public void onClickTag(View view) {
        TextView textView = (TextView) view;
        Intent intent = new Intent(this, DownloadActivity.class);
        intent.putExtra("KEY_TAG", textView.getText());
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_search_song:
                startActivity(new Intent(this, DownloadActivity.class));

                break;

            case R.id.img_downloaded:
                startActivity(new Intent(this, DownloadedActivity.class));
                break;
            case R.id.img_option:
                PopupMenu popupMenu = new PopupMenu(this, imgOption);
                popupMenu.inflate(R.menu.menu_option);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.menu_share:
                                shareApp();
                                return true;
                            case R.id.menu_rate:
                                rateApp();
                                return true;
                            default:
                                return false;
                        }

                    }
                });
                popupMenu.show();
                break;

        }
    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            String shareMessage = "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            Toast.makeText(this, "share app error", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialogRating() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_rating_app);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        Button btnCancel = dialog.findViewById(R.id.btn_cancel_rating);
        Button btnOkRating = dialog.findViewById(R.id.btn_ok_rating);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOkRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFirst.edit().putString("checkFirst", "2").commit();
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                dialog.dismiss();
            }
        });
    }


    public void loadHotSongs() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.googleapis.com/youtube/v3/").addConverterFactory(GsonConverterFactory.create()).build();
        JsonGetDataByRetrofit jsonGetData = retrofit.create(JsonGetDataByRetrofit.class);
        try {
            Call<sn.free.music.downloadmusic.model.hotSongs.Example> call = jsonGetData.getHotSongs("AIzaSyCRt9wIKqvmlQw0ACGrd2fCVMaYzkF3nWE");
            call.enqueue(new Callback<sn.free.music.downloadmusic.model.hotSongs.Example>() {
                @Override
                public void onResponse(Call<sn.free.music.downloadmusic.model.hotSongs.Example> call, Response<sn.free.music.downloadmusic.model.hotSongs.Example> response) {
                    if (response != null) {
                        for (int i = 0; i < response.body().getItems().size(); i++) {
                            String category = response.body().getItems().get(i).getSnippet().getCategoryId();
                            if (category.equals("10")) {
                                String id = response.body().getItems().get(i).getId();
                                String title = response.body().getItems().get(i).getSnippet().getTitle();
                                String auth = response.body().getItems().get(i).getSnippet().getChannelTitle();
                                String linkImage = "";
                                try {
                                    linkImage = response.body().getItems().get(i).getSnippet().getThumbnails().getMedium().getUrl();

                                } catch (Exception e) {

                                }
                                if (!id.contains("-")) {
                                    arrHotSongs.add(new Song(id, title, auth, linkImage));
                                }
                                adapterMusicSearch.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onFailure
                        (Call<sn.free.music.downloadmusic.model.hotSongs.Example> call, Throwable
                                t) {
                    Toast.makeText(MainActivity.this
                            , "Error", Toast.LENGTH_SHORT).show();
                }
            });
            call.clone();
        } catch (
                Exception ex) {
            Toast.makeText(MainActivity.this, "please wait", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void clickItem(int position) {
        Intent intent = new Intent(this, DownloadActivity.class);
        intent.putExtra("HOT_SONGS_ID", arrHotSongs.get(position).getIdMusic());
        intent.putExtra("HOT_SONGS_TITLE", arrHotSongs.get(position).getTitle());
        startActivity(intent);
    }
}

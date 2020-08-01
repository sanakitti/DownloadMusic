package sn.free.music.downloadmusic.activity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import sn.free.music.downloadmusic.R;
import sn.free.music.downloadmusic.adapter.AdapterMusicDownloaded;
import sn.free.music.downloadmusic.model.ItemMusicDownloaded;

public class DownloadedActivity extends AppCompatActivity implements AdapterMusicDownloaded.ClickCallBack, View.OnClickListener {
    private RecyclerView lvManager;
    private AdapterMusicDownloaded adapterMusicSearch;
    private ArrayList<ItemMusicDownloaded> arrayList = new ArrayList<>();
    private Animation animZom;
    private LinearLayout llEmptyFile;
    private static final String AUTHORITY = "sn.free.music.downloadmusic" + ".fileprovider";
    public static final String PATH = Environment.getExternalStorageDirectory().toString() + "/musicdownloader/";
    public static final String UPDATE_MUSIC = "update_music";
    private ImageView imgBackSearch;
    private InterstitialAd mInterstitialAd;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_songs);
        initViews();
    }

    private void initViews() {
        adView = findViewById(R.id.adView);

        initAdsFull();
        MobileAds.initialize(this, getResources().getString(R.string.mobile_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        llEmptyFile = findViewById(R.id.ll_empty_file);
        animZom = AnimationUtils.loadAnimation(this, R.anim.anim_ic_download);
        imgBackSearch = findViewById(R.id.img_back_downloaded);
        imgBackSearch.setOnClickListener(this);
        lvManager = findViewById(R.id.lv_main);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        adapterMusicSearch = new AdapterMusicDownloaded(arrayList, this);
        adapterMusicSearch.setCallBack(this);
        lvManager.setLayoutManager(manager);
        lvManager.setAdapter(adapterMusicSearch);
        LoadAsync loadAsync = new LoadAsync();
        loadAsync.execute();

    }

    @Override
    public void clickItemMusic(int postion) {
        Intent playIntent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final File photoFile = new File(arrayList.get(postion).getLink());
            Uri photoURI = FileProvider.getUriForFile(this, AUTHORITY, photoFile);
            playIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            playIntent.setDataAndType(photoURI, "audio/*");
        } else {
            playIntent.setDataAndType(Uri.parse(arrayList.get(postion).getLink()), "audio/*");
        }
        startActivity(playIntent);
    }

    @Override
    public void clickOptionItemMusic(final int postion, View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_option_main);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        File file = new File(arrayList.get(postion).getLink());
                        boolean deleted = file.delete();
                        if (deleted == true) {
                            Toast.makeText(DownloadedActivity.this, getResources().getString(R.string.delete_finish), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DownloadedActivity.this, getResources().getString(R.string.delete_error), Toast.LENGTH_SHORT).show();
                        }
                        arrayList.remove(postion);
                        adapterMusicSearch.notifyDataSetChanged();
                        break;
                    case R.id.menu_share_main:
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        final File photoFile = new File(arrayList.get(postion).getLink());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Uri photoURI = FileProvider.getUriForFile(DownloadedActivity.this, AUTHORITY, photoFile);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
                        } else {
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
                        }

                        shareIntent.setType("audio/*");
                        startActivity(Intent.createChooser(shareIntent, "Share music"));
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back_downloaded:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {

        if (mInterstitialAd != null) {
            mInterstitialAd.show();
        } else {
            finish();
        }

    }

    private class LoadAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(Void... voids) {
            arrayList.clear();
            File f = new File(PATH);

            File[] files = f.listFiles();
            if (f.exists()) {
                if (files.length <= 0) {
                    Log.d("sizearrList", "0");
                    return "OK";
                } else {
                    Log.d("sizearrList", "2");
                    arrayList.clear();

                    Arrays.sort(files, new Comparator() {
                        public int compare(Object o1, Object o2) {
                            if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                                return -1;
                            } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                                return +1;
                            } else {
                                return 0;
                            }
                        }
                    });

                    for (File aFile : files) {
                        if (!aFile.isDirectory()) {
                            String name = aFile.getName();
                            String path = aFile.getPath();
                            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                            mediaMetadataRetriever.setDataSource(aFile.getAbsolutePath());
                            String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                            long dur = Long.parseLong(duration);

                            Date lastModified = getFileLastModified(path);
                            arrayList.add(new ItemMusicDownloaded(name, path, lastModified, dur));
                        } else {
                            Log.d("myLog", "Do not add");
                        }
                    }
                    Log.d("sizeArrsss", arrayList.size() + "");
                    for (int i = 0; i < arrayList.size(); i++) {
                        Log.d("sizeArr", arrayList.get(i).getTitle());
                    }

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (arrayList.size() == 0) {
                llEmptyFile.setVisibility(View.VISIBLE);
            } else {
                llEmptyFile.setVisibility(View.GONE);
            }
            adapterMusicSearch.notifyDataSetChanged();
        }
    }

    public static Date getFileLastModified(String pathFile) {
        File file = new File(pathFile);
        return new Date(file.lastModified());
    }

    private void initAdsFull() {
        mInterstitialAd = new InterstitialAd(this);
        MobileAds.initialize(this,
                getResources().getString(R.string.mobile_id));
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.ads_full));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                finish();
                // Code to be executed when the interstitial ad is closed.
            }
        });

    }

}

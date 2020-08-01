package sn.free.music.downloadmusic.interfaces;

import sn.free.music.downloadmusic.model.ItemJMusicSearch;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonGetDataByRetrofit {
    @GET("search?part=snippet&chart=mostPopular&maxResults=20&type=video")
    Call<ItemJMusicSearch> getDataByVideoID(
            @Query("key") String keyTube,
            @Query("q") String search);

    @GET("videos?part=snippet&categoryId=10&chart=mostPopular&maxResults=50")
    Call<sn.free.music.downloadmusic.model.hotSongs.Example> getHotSongs(
            @Query("key") String keyTube);
}

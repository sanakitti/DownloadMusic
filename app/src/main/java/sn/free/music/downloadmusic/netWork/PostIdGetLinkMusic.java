package sn.free.music.downloadmusic.netWork;
import android.os.AsyncTask;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import sn.free.music.downloadmusic.model.ItemIdMusic;

public class PostIdGetLinkMusic extends AsyncTask<ItemIdMusic, Void, String> {
    private GetLinkMusicSCallBack callBack;

    public void setCallBack(GetLinkMusicSCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected String doInBackground(ItemIdMusic... itemIdMusics) {
        try {
            Log.e("dataConvertadfa", itemIdMusics[0].getLink() + "      " + itemIdMusics[0].getId() + "           " + itemIdMusics[0].getvId());
            Document doc = Jsoup.connect(itemIdMusics[0].getLink().trim())
                    .ignoreContentType(true)
                    .data("type", "youtube")
                    .data("_id", itemIdMusics[0].getId().trim())
                    .data("v_id", itemIdMusics[0].getvId().trim())
                    .data("mp3_type", "128")
                    .post();
            if (doc != null) {
                return getLinkVideo(doc.toString());
            }
        } catch (Exception e) {

        }
        return null;
    }

    private String getLinkVideo(String document) {
        Log.e("DocumentGetLinkMusic", document);
        if (document.contains("http")) {
            String linkMusic = document.substring(document.indexOf("http"));
            linkMusic = linkMusic.substring(0, linkMusic.indexOf("\""));
            linkMusic = linkMusic.replace("\\", "");
            linkMusic = linkMusic.replace("&quot;", "");
            linkMusic = linkMusic.trim();
            Log.e("LInkMusicConvert", linkMusic + "");
            return linkMusic;
        } else {
            Log.e("LInkMusicConvert", "not find link convert");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if (callBack != null) {
            if (s.equals("") || s == null) {
                callBack.getLinkMusicFail();
            } else {
                callBack.getLinkMusicSuccess(s);
            }
        }
    }

    public interface GetLinkMusicSCallBack {
        void getLinkMusicSuccess(String linkMusic);

        void getLinkMusicFail();
    }
}

package sn.free.music.downloadmusic.netWork;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import sn.free.music.downloadmusic.model.ItemIdMusic;

public class PostLinkToGetId extends AsyncTask<String, Void, ItemIdMusic> {


    private GetIdMusicCallBack callBack;

    public void setCallBack(GetIdMusicCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected ItemIdMusic doInBackground(String... strings) {
        try {
            Document doc = Jsoup.connect("https://mate03.y2mate.com/vi/mp3/ajax")
                    .ignoreContentType(true)
                    .data("url", strings[0].trim())
                    .data("q_auto", "0")
                    .data("ajax", "1")
                    .post();
            if (doc != null) {

                Log.e("statusGetId", doc.toString() + "");
                return getIdMusic(doc.toString());

            } else {
                Log.e("statusGetId", "error");
            }
        } catch (Exception e) {

        }
        return null;
    }


    @Override
    protected void onPostExecute(ItemIdMusic itemIdMusic) {
        if (itemIdMusic != null) {
            if (callBack != null) {
                callBack.getIdSuccess(itemIdMusic);
            }
        } else {
            if (callBack != null) {
                callBack.getIdFail();
            }

        }
    }

    private ItemIdMusic getIdMusic(String document) {
        String link = "";
        String id = "";
        String vId = "";
        if (document.contains("url:")) {
            int indexUrl = document.indexOf("url:");
            document = document.substring(indexUrl + 7);
            Log.e("documentConvert", document);
            link = document.substring(0, document.indexOf("\""));
            link = link.replace("\\", "");
            Log.e("LinkConvert", link + "");


            if (document.contains("_id:")) {
                document = document.substring(document.indexOf("_id:") + 6);
                id = document.substring(0, document.indexOf(","));
                id = id.replace("\'", "");
                Log.e("documentConvert", document);
                Log.e("idConvert", id + "");
            }
            if (document.contains("v_id:")) {
                document = document.substring(document.indexOf("v_id:") + 6);
                vId = document.substring(0, document.indexOf(","));
                vId = vId.replace("\'", "");
                Log.e("vIdConvert", vId);
            }
        } else {
            Log.e("documentConvert", "not url");
        }
        if (!link.equals("") && !id.equals("") && !vId.equals("")) {
            return new ItemIdMusic(link, id, vId);
        }
        return null;
    }

    public interface GetIdMusicCallBack {
        void getIdSuccess(ItemIdMusic itemIdMusic);

        void getIdFail();
    }
}

package sn.free.music.downloadmusic.model;

public class Song {
    private String idMusic;
    private String title;
    private String auTh;
    private String image;


    public Song(String idMusic, String title, String auTh, String image) {
        this.idMusic = idMusic;
        this.title = title;
        this.auTh = auTh;
        this.image = image;
    }


    public String getIdMusic() {
        return idMusic;
    }

    public void setIdMusic(String idMusic) {
        this.idMusic = idMusic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuTh() {
        return auTh;
    }

    public void setAuTh(String auTh) {
        this.auTh = auTh;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

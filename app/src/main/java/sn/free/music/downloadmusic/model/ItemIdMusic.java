package sn.free.music.downloadmusic.model;

public class ItemIdMusic {
    private String link;
    private String id;
    private String vId;

    public ItemIdMusic(String link, String id, String vId) {
        this.link = link;
        this.id = id;
        this.vId = vId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getvId() {
        return vId;
    }

    public void setvId(String vId) {
        this.vId = vId;
    }
}

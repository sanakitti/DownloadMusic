package sn.free.music.downloadmusic.model;

import java.util.Date;

public class ItemMusicDownloaded {
    private String title;
    private String link;
    private Date pubDate;
    private float duration;

    public ItemMusicDownloaded(String title, String link, Date pubDate, float duration) {
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }
}

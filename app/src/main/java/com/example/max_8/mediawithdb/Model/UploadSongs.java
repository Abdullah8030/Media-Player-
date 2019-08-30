package com.example.max_8.mediawithdb.Model;

import java.io.Serializable;

public class UploadSongs implements Serializable {

    private String songTitle,songDuration,songLink,mkey,NameArtist,image;

    public UploadSongs() {
    }

    public UploadSongs(String songTitle, String songDuration, String songLink,String NameArtist,String image) {

        if(songTitle.trim().equals("")){ this.songTitle = "No title"; }
        this.songTitle = songTitle;
        this.songDuration = songDuration;
        this.songLink = songLink;
        this.NameArtist = NameArtist;
        this.image = image;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNameArtist() {
        return NameArtist;
    }

    public void setNameArtist(String nameArtist) {
        NameArtist = nameArtist;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public String getMkey() {
        return mkey;
    }

    public void setMkey(String mkey) {
        this.mkey = mkey;
    }
}

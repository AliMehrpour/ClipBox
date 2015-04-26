package com.volcano.clipbox.model;

import java.util.Date;

/**
 * Clip entity
 */
public class Clip {
    public int id;
    public String value;
    public Date createDate;
    public boolean favorite;

    private static Clip sLastClip = null;

    public Clip(int id, String value, Date createDate, boolean favorite) {
        this.id = id;
        this.value = value;
        this.createDate = createDate;
        this.favorite = favorite;
    }

    public static void setLastCLip(Clip lastCLip) {
        sLastClip = lastCLip;
    }

    public static Clip getLastClip() {
        return sLastClip;
    }

    public Clip toggleFavorite() {
        return new Clip(id, value, createDate, !favorite);
    }
}

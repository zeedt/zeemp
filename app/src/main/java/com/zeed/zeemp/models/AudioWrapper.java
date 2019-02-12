package com.zeed.zeemp.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zeed on 9/16/18.
 */

public class AudioWrapper implements Serializable {

    private List<Audio> audioList;


    public List<Audio> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<Audio> audioList) {
        this.audioList = audioList;
    }
}

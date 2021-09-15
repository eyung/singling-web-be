package com.ey.singlingweb.audio;

import java.util.Objects;
import java.util.UUID;

public class AudioProfile {
    private UUID audioID;
    private String audioLink;

    public AudioProfile(UUID audioID, String audioLink) {
        this.audioID = audioID;
        this.audioLink = audioLink;
    }

    public UUID getAudioID() {
        return audioID;
    }

    public void setAudioID(UUID audioID) {
        this.audioID = audioID;
    }

    public String getAudioLink() {
        return audioLink;
    }

    public void setAudioLink(String audioLink) {
        this.audioLink = audioLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AudioProfile that = (AudioProfile) o;
        return Objects.equals(audioID, that.audioID) &&
                Objects.equals(audioLink, that.audioLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(audioID, audioLink);
    }
}

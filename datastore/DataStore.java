package com.ey.singlingweb.datastore;

import com.ey.singlingweb.audio.AudioProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DataStore {
    private static final List<AudioProfile> AUDIO_PROFILES = new ArrayList<>();

    static {
        //USER_PROFILES.add(new UserProfile(UUID.randomUUID(), "test", null));
    }

    public List<AudioProfile> getAudioProfiles() {
        return AUDIO_PROFILES;
    }

    public void addAudioProfile(AudioProfile audioProfile) {
        AUDIO_PROFILES.add(new AudioProfile(audioProfile.getAudioID(), audioProfile.getAudioLink()));
    }
}

package com.ey.singlingweb.audio;

import com.ey.singlingweb.datastore.DataStore;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class AudioProfileDataAccessService {
    private final DataStore AudioProfileDataStore;

    public AudioProfileDataAccessService(DataStore AudioProfileDataStore) {
        this.AudioProfileDataStore = AudioProfileDataStore;
    }

    List<AudioProfile> getAudioProfile() {
        return AudioProfileDataStore.getAudioProfiles();
    }

    void addAudioProfile(UUID audioID, String audioLink) {
        AudioProfileDataStore.addAudioProfile(new AudioProfile(audioID, audioLink));
    }
}

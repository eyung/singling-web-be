package com.ey.singlingweb.audio;

import com.ey.singlingweb.bucket.BucketName;
import com.ey.singlingweb.filestore.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class AudioProfileService {

    private final AudioProfileDataAccessService audioProfileDataAccessService;
    private final FileStore fileStore;

    @Autowired
    public AudioProfileService(AudioProfileDataAccessService audioProfileDataAccessService, FileStore fileStore) {
        this.audioProfileDataAccessService = audioProfileDataAccessService;
        this.fileStore = fileStore;
    }

    List<AudioProfile> getAudioProfiles() {
        return audioProfileDataAccessService.getAudioProfile();
    }

    void uploadAudio(UUID audioID, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file [ " + file.getSize() + "]");
        }

        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));

        // Save
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), "audio");
        String filename = String.format("%s-%s.wav", "audio", audioID);

        audioProfileDataAccessService.addAudioProfile(audioID, filename);

        AudioProfile audio = audioProfileDataAccessService
                .getAudioProfile()
                .stream()
                .filter(audioProfile -> audioProfile.getAudioID().equals(audioID))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Not found ID"));

        try {
            fileStore.save(path, filename, Optional.of(metadata), file.getInputStream());
            audio.setAudioLink(filename);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }
}

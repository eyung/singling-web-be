package com.ey.singlingweb.profile;

import com.ey.singlingweb.datastore.FakeDataStore;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserProfileDataAccessService {

    private final FakeDataStore fakeUserProfileDataStore;

    public UserProfileDataAccessService(FakeDataStore fakeUserProfileDataStore) {
        this.fakeUserProfileDataStore = fakeUserProfileDataStore;
    }

    List<UserProfile> getUserProfiles() {
        return fakeUserProfileDataStore.getUserProfiles();
    }
}

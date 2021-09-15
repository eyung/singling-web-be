package com.ey.singlingweb.bucket;

public enum BucketName {

    PROFILE_IMAGE("effiam-bucket");

    private final String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}

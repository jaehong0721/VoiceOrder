package com.rena21c.voiceorder.services;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;

public class AwsS3FileUploader {

    private final String bucketName;
    private final TransferUtility transferUtility;

    public AwsS3FileUploader(String bucketName, TransferUtility transferUtility) {
        this.bucketName = bucketName;
        this.transferUtility = transferUtility;
    }

    public void upload(File file, TransferListener transferLisener) {
        TransferObserver transferObserver = transferUtility.upload(bucketName, file.getName(), file);
        transferObserver.setTransferListener(transferLisener);
    }

    public static class Builder {

        private String bucketName;
        private TransferUtility transferUtility;

        public Builder setBucketName(String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        public Builder setTransferUtility(TransferUtility transferUtility) {
            this.transferUtility = transferUtility;
            return this;
        }

        public AwsS3FileUploader build() {
            // TODO: @NonNull등 어노테이션 기반으로 null 체크 테스트
            if (bucketName == null || transferUtility == null)
                throw new NullPointerException("S3 업로더 초기화 오류");
            return new AwsS3FileUploader(bucketName, transferUtility);
        }
    }

}

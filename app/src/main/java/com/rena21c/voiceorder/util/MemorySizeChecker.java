package com.rena21c.voiceorder.util;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

public class MemorySizeChecker {

    private final long requiredSpace;

    public MemorySizeChecker(long requiredSpace){
        this.requiredSpace = requiredSpace;
    }

    public boolean isEnough() {
        return getAvailableInternalMemorySize() < requiredSpace;
    }

    private long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize;
        long availableBlocks;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlocks = stat.getAvailableBlocksLong();
            blockSize = stat.getBlockSizeLong();
        } else {
            availableBlocks = stat.getAvailableBlocks();
            blockSize = stat.getBlockSize();
        }
        return availableBlocks * blockSize;
    }
}

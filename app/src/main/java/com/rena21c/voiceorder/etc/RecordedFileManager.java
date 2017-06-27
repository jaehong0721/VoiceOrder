package com.rena21c.voiceorder.etc;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

public class RecordedFileManager {

    private final long SEC = 1000;
    private final long MIN = 60 * SEC;
    private final long HOUR = 60 * MIN;
    private final long DAY = 24 * HOUR;

    private final long standardTimeInMillis = 3 * DAY;

    private final File dir;

    public RecordedFileManager(File dir) {
        this.dir = dir;
    }

    public void deleteRecordedFile(long currentTimeInMillis) {
        ArrayList<File> recordedFiles = getRecordedFiles();

        if(recordedFiles.size() == 0) return;

        for (File file : recordedFiles ) {
            Long lastModified = file.lastModified();

            if (lastModified + standardTimeInMillis < currentTimeInMillis) {
                file.delete();
            }
        }
    }

    public boolean isStored(String fileName) {
        File file = new File(dir, fileName + ".mp4");
        return file.exists();
    }

    public ArrayList<File> getRecordedFiles() {
        return new ArrayList<>(Arrays.asList(
                dir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".mp4");
                    }
            })
        ));
    }

}

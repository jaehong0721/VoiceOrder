package com.rena21c.voiceorder.etc;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

public class RecordedFileManager {

    private final long SEC = 1000;
    private final long MIN = 60 * SEC;
    private final long HOUR = 60 * MIN;
    private final long DAY = 24 * HOUR;

    private final long standardTimeInMillis = 3 * DAY;

    private final File rootDir;
    private final File saveDir;

    public RecordedFileManager(File rootDir) {
        this.rootDir = rootDir;
        this.saveDir = new File(rootDir + "/recordedFiles");
    }

    public void createSaveDir() {
        saveDir.mkdirs();
    }

    public String getRootDir() {
        return rootDir.getPath();
    }

    public void saveRecordedFile(File source, String fileName) throws IOException {
        File saveDest = new File(rootDir.getPath() + "/recordedFiles/" + fileName + ".mp4");
        copyFileUsingChannel(source, saveDest);

        File renameDest = new File(rootDir.getPath() + "/" + fileName + ".mp4");
        source.renameTo(renameDest);
    }

    public void deleteRecordedFile(long currentTimeInMillis) {
        ArrayList<File> recordedFiles = getRecordedFiles();

        if (recordedFiles == null) return;

        for (File file : recordedFiles) {
            Long lastModified = file.lastModified();

            if (lastModified + standardTimeInMillis < currentTimeInMillis) {
                file.delete();
            }
        }
    }

    public boolean isStored(String fileName) {
        File file = new File(saveDir.getPath() + "/" + fileName + ".mp4");
        return file.exists();
    }

    public String getRecordedFilePath(String fileName) {
        return saveDir.getPath() + "/" + fileName + ".mp4";
    }

    private ArrayList<File> getRecordedFiles() {
        File[] recordedFiles = saveDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".mp4");
            }
        });

        return recordedFiles != null ? new ArrayList<>(Arrays.asList(recordedFiles)) : null;
    }

    private void copyFileUsingChannel(File source, File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } finally {
            sourceChannel.close();
            destChannel.close();
        }
    }
}

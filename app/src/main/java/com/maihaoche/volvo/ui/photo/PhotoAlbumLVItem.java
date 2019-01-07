package com.maihaoche.volvo.ui.photo;

/**
 */
public class PhotoAlbumLVItem {
    private String pathName;
    private int fileCount;
    private String firstImagePath;

    public PhotoAlbumLVItem(String pathName, int fileCount, String firstImagePath) {
        this.pathName = pathName;
        this.fileCount = fileCount;
        this.firstImagePath = firstImagePath;
    }

    public String getPathName() {
        return pathName;
    }


    public int getFileCount() {
        return fileCount;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }


}

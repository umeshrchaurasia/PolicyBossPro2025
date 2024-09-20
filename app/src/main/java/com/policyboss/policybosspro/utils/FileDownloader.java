package com.policyboss.policybosspro.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

public class FileDownloader {
    public static void downloadFile(Context context, String fileURL) {
        String fileName = extractFileName(fileURL);
        
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(fileURL);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        downloadManager.enqueue(request);
    }

    private static String extractFileName(String fileURL) {
        int lastIndex = fileURL.lastIndexOf("/");
        if (lastIndex != -1 && lastIndex < fileURL.length() - 1) {
            return fileURL.substring(lastIndex + 1);
        }
        return "downloaded_file"; // Default filename if extraction fails
    }
}

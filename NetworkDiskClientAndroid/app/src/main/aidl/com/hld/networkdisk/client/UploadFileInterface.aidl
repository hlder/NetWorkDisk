// UploadFileInterface.aidl
package com.hld.networkdisk.client;

// Declare any non-default types here with import statements

interface UploadFileInterface {
    void onProgress(String filePath,long fileLength,long nowLength);
}
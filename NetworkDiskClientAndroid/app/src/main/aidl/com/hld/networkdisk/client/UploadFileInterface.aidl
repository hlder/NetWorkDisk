// UploadFileInterface.aidl
package com.hld.networkdisk.client;

import com.hld.networkdisk.client.OnUploadListener;
// Declare any non-default types here with import statements

interface UploadFileInterface {
//    void onProgress(String filePath, long fileLength, long nowLength);
    void setListener(OnUploadListener onUploadListener);
}
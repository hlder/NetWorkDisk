package com.hld.networkdisk.client;

oneway interface OnUploadListener {
    void onProgress(String filePath, long fileLength, long nowLength);
}
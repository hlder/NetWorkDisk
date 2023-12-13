package com.hld.networkdisk.server.filemanager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class A {
    void a(){
        try {
            FileOutputStream fs = new FileOutputStream("");
            fs.write(1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e){
            e.printStackTrace();
        }

    }
}

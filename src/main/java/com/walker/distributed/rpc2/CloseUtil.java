package com.walker.distributed.rpc2;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author walkerwei
 * @version 2017/2/3
 */
public class CloseUtil {
    public static void close(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

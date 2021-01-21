package org.darcro.messageproc.util;

import com.google.common.io.BaseEncoding;

public class ByteTool {

    public String toHex(byte[] data) {
        return BaseEncoding.base16().encode(data);
    }

}

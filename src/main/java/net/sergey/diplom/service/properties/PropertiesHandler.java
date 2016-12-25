package net.sergey.diplom.service.properties;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Component
public class PropertiesHandler extends Properties {
    private String md5Hex;

    public PropertiesHandler() {
        super();
    }

    public synchronized void load(String inStream) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(inStream);
        String md5Hex = DigestUtils.md5Hex(fileInputStream);
        if (!md5Hex.equals(this.md5Hex)) {
            this.md5Hex = md5Hex;
            super.load(new FileInputStream(inStream));
        }
    }


}

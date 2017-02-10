package net.sergey.diplom.services.parser;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface ParseFileScv {
    String parseFileAirfoil(MultipartFile fileAirfoil) throws IOException;

    String csvToString(InputStream urlFile) throws IOException;
}

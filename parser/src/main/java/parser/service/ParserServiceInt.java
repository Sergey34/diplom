package parser.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ParserServiceInt {
    String parse();

    String parseFileAirfoil(MultipartFile fileAirfoil) throws IOException;

    void stop();

    String parsePrefix(String prefix);

    String parseAirfoil(String airfoilId);

}

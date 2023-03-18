package afsp.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Utils {

    public static String encodeString(String origin){
            return URLEncoder.encode(origin, StandardCharsets.UTF_8);
    }
    public static String decodeString(String encoded){
        return URLDecoder.decode(encoded,StandardCharsets.UTF_8);
    }
}

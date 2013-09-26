package gnutch;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

class UrlEscaper {
    public static String unescape(String str) throws UnsupportedEncodingException{
        StringWriter sw = new StringWriter();
        byte[] bytes = new byte[1024];
        int bytePtr = 0;
        char [] chars = str.toCharArray();
        for(int i=0;i<chars.length;i++){
            assert bytePtr < bytes.length;
            char ch = chars[i];
            if(ch == '%' && Character.isDigit(chars[i + 1]) && Character.isDigit(chars[i + 2])){
                bytes[bytePtr++] = (byte)Integer.parseInt(str.substring(i+1, i+3), 16);
                i+=2;
            } else {
                sw.append(new String(Arrays.copyOf(bytes, bytePtr), "UTF-8"));
                bytePtr = 0;

                sw.append(ch);
            }
        };
        sw.append(new String(Arrays.copyOf(bytes, bytePtr), "UTF-8"));

        return sw.toString();
    };
}

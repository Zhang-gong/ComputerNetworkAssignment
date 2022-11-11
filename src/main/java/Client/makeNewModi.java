package Client;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class makeNewModi {
    static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    static String WWW_ROOT="D:\\code\\JAVA\\assignment3\\";

    public static void main(String args[]) throws Exception
    {
        writelastmodified();
    }

    private static void writelastmodified() throws Exception {
        BufferedWriter bfwriter = new BufferedWriter(new FileWriter(WWW_ROOT + "/conf/lastModified.txt"));
        LocalDateTime time = LocalDateTime.now();
        String localTime = df.format(time);
        bfwriter.write(localTime);
        bfwriter.close();
    }
}
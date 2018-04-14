import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * MAIN CLASS
 */
public class MainClass {

    private static int connectionTimeout = 10 * 1000;
    private static int readTimeout = 10 * 1000;

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: \n<URL> [connectionTimeout_sec] [readTimeout_sec]");
            return;
        }

        // reading input arguments
        URL url = new URL(args[0]);
        if (args.length >= 2 && args[1] != null) {
            connectionTimeout = Integer.parseInt(args[1]) * 1000;
        }
        if (args.length >= 3 && args[2] != null) {
            readTimeout = Integer.parseInt(args[2]) * 1000;
        }

        Document doc;
        String title;

        doc = Jsoup.connect(url.toString()).get();
        title = doc.title();
        System.out.println("Jsoup Can read HTML page from URL, title : " + title);
    }

    public String downloadPage(URL url, String toFile) {
        try {
            FileUtils.copyURLToFile(url, new File(toFile), connectionTimeout, readTimeout);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return toFile;
    }

    public String downloadNio(URL url, String toFile) {
        try (FileOutputStream fos = new FileOutputStream(toFile);
             ReadableByteChannel rbc = Channels.newChannel(url.openStream())) {

            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return toFile;
    }

}

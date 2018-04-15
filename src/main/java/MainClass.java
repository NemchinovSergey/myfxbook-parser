import org.apache.commons.io.FileUtils;

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

        // https://www.myfxbook.com/statements/2401713/statement.csv
        // https://www.myfxbook.com/members/dmitriy127/si14-arbitrage-1/2401713

        // reading input arguments
        String address = args[0];
        URL url = new URL(address);
        if (args.length >= 2 && args[1] != null) {
            connectionTimeout = Integer.parseInt(args[1]) * 1000;
        }
        if (args.length >= 3 && args[2] != null) {
            readTimeout = Integer.parseInt(args[2]) * 1000;
        }

        String accountId = getAccountId(url);
        if (accountId == null) {
            System.out.println("Unknown Account ID");
            return;
        }

        String statementUrl = "https://www.myfxbook.com/statements/" + accountId + "/statement.csv";
        System.out.println("Downloading: " + statementUrl);

        URL downloadUrl = new URL(statementUrl);
        String savedFile = downloadFile(downloadUrl, accountId + ".csv");
        if (savedFile != null) {
            System.out.println("Success!");
        }
    }

    private static String getAccountId(URL url) {
        if (url.getPath().contains("/statements")) {
            String[] arr = url.getPath().split("/");
            if (arr.length >= 3) {
                return arr[2];
            }
        } else if (url.getPath().contains("/members")) {
            String[] arr = url.getPath().split("/");
            if (arr.length >= 5) {
                return arr[4];
            }
        }
        return null;
    }

    public static String downloadFile(URL url, String toFile) {
        try {
            FileUtils.copyURLToFile(url, new File(toFile), connectionTimeout, readTimeout);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return toFile;
    }

    public static String downloadFileNio(URL url, String toFile) {
        try (FileOutputStream fos = new FileOutputStream(toFile);
             ReadableByteChannel rbc = Channels.newChannel(url.openStream())) {

            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return toFile;
    }

}

package org.ganderson.webcrawl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;

/**
 * Testing utilities.
 */
public class HtmlTestUtils {

    /**
     * Suppress public constructor.
     */
    private HtmlTestUtils() {
        // No op
    }

    /**
     * Builds an HTML document with anchors using the provided href.
     *
     * @param baseUrl The base URL of the 'page'.
     * @param references The hrefs to use.
     */
    public static Document buildAnchorWithReferences(URL baseUrl, String... references) {

        StringBuilder builder = new StringBuilder();

        // Example header tag
        builder.append("<html lang=\"en-us\">\n" +
                           "   <head>\n" +
                           "      <script type=\"text/javascript\" async=\"\" src=\"//j.6sc.co/6si.min" +
                           ".js\"></script>\n" +
                           "      <meta property=\"og:description\" content=\"some content value\">\n" +
                           "      <link rel=\"shortcut icon\" href=\"/assets/images/global/favicon.ico\">\n" +
                           "      <link rel=\"stylesheet\" href=\"https://styles.css\">\n" +
                           "      <script type=\"text/javascript\" src=\"https://a-script\"></script>\n" +
                           "   </head>");

        builder.append("<body>");

        for (String reference : references) {
            builder.append("<div><a href=\"");
            builder.append(reference);
            builder.append("\"/><div>");
        }

        builder.append("</body>");
        builder.append("</html>");


        return Jsoup.parse(builder.toString(), baseUrl.toString());
    }
}

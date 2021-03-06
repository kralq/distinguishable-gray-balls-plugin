package hudson.plugins.grayballs;

import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.apache.commons.httpclient.util.DateUtil;
import org.jvnet.hudson.test.HudsonTestCase;

import com.gargoylesoftware.htmlunit.WebResponse;

public class StatusIconIntegrationTest extends HudsonTestCase {

    static String join(String first, String second) {
        if (first.endsWith("/"))
            first = first.substring(0, first.length() - 1);
        if (second.startsWith("/"))
            second = second.substring(1);
        return first + "/" + second;
    }

    static String hash(String algorithm, byte[] data) throws NoSuchAlgorithmException {
        byte[] hash = MessageDigest.getInstance(algorithm).digest(data);
        BigInteger bi = new BigInteger(1, hash);
        String result = bi.toString(16);
        if (result.length() % 2 != 0) {
            return "0" + result;
        }
        return result;
    }

    public void testGreenBall() throws Exception {
        final WebClient wc = new WebClient();
        final URL url = new URL(join(wc.getContextPath(), "images/48x48/aborted.png"));
        final WebResponse webResponse = wc.getPage(url).getWebResponse();
        String digest = hash("SHA-1", webResponse.getContentAsBytes());
        assertEquals("Content does not match expected digest", "4cd60d5b479ae668fa74285e920c742d2578be28", digest);
        assertTrue("Cache-Control header missing",
                webResponse.getResponseHeaderValue("Cache-Control").contains("s-maxage"));
        assertTrue("Response has expired",
                new Date().before(DateUtil.parseDate(webResponse.getResponseHeaderValue("Expires"))));
    }
}

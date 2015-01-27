package hudson.plugins.grayballs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StatusIconFilter implements Filter {

  final String patternStr = "/(\\d{2}x\\d{2})/%s(_anime|)\\.(gif|png)";

  final Pattern patternDisabled = Pattern.compile(String.format(patternStr, "disabled"));

  final Pattern patternAborted = Pattern.compile(String.format(patternStr, "aborted"));

  final Logger logger = Logger.getLogger("hudson.plugins.grayballs");

  public void init(FilterConfig config) throws ServletException {
  }

  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
    if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
      final HttpServletRequest httpServletRequest = (HttpServletRequest) req;
      final HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
      final String uri = httpServletRequest.getRequestURI();
      if (uri.endsWith(".gif") || uri.endsWith(".png")) {
        String newImageUrl = mapImage(uri);
        if (newImageUrl != null) {
          if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Redirecting {0} to {1}", new Object[] { uri, newImageUrl });
          }
          RequestDispatcher dispatcher = httpServletRequest.getRequestDispatcher(newImageUrl);
          dispatcher.forward(httpServletRequest, httpServletResponse);
          return;
        }
      }
    }
    chain.doFilter(req, resp);
  }

  private String mapImage(String uri) {
    if (uri.contains("plugin/grayballs/")) return null;
    Matcher m;

    if ((m = patternDisabled.matcher(uri)).find()) {
      return "/plugin/grayballs/" + m.group(1) + "/disabled" + m.group(2) + "." + m.group(3);
    }

    if ((m = patternAborted.matcher(uri)).find()) {
      return "/plugin/grayballs/" + m.group(1) + "/aborted" + m.group(2) + "." + m.group(3);
    }

    return null;
  }

  public void destroy() {
  }
}

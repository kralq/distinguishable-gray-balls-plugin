package hudson.plugins.grayballs;

import hudson.Plugin;
import hudson.PluginWrapper;
import hudson.util.ColorPalette;
import hudson.util.PluginServletFilter;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class PluginImpl extends Plugin {

  transient PluginWrapper wrapper;

  transient final Logger logger = Logger.getLogger("hudson.plugins.grayballs");

  @Override
  public void start() throws Exception {
    super.start();
    load();
    PluginServletFilter.addFilter(new StatusIconFilter());
    try {
      wrapper = null;
      Field wrapperField = Plugin.class.getDeclaredField("wrapper");
      wrapperField.setAccessible(true);
      wrapper = (PluginWrapper) wrapperField.get(this);
    } catch (Exception e) {
      logger.log(Level.WARNING, "Unable to access plugin wrapper", e);
    }
  }

  @Override
  public void doDynamic(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
    rsp.setHeader("Cache-Control", "public, s-maxage=86400");
    if (wrapper == null) {
      super.doDynamic(req, rsp);
      return;
    }
    String path = req.getRestOfPath();

    if (path.length() == 0)
      path = "/";

    if (path.indexOf("..") != -1 || path.length() < 1) {
      // don't serve anything other than files in the sub directory.
      rsp.sendError(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    // use serveLocalizedFile to support automatic locale selection
    logger.log(Level.FINE, "Serving cached resource {0}", path);
    rsp.serveLocalizedFile(req, new URL(wrapper.baseResourceURL, '.' + path), 86400000);
  }
}

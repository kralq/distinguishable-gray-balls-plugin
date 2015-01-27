package hudson.plugins.grayballs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.regex.Matcher;

import org.junit.Before;
import org.junit.Test;

public class StatusIconFilterTest {

  StatusIconFilter statusIconFilter;

  @Before
  public void setup() {
    statusIconFilter = new StatusIconFilter();
  }

  @Test
  public void patternShouldMatch() {
    final Matcher m = statusIconFilter.patternAborted.matcher("/nocacheImages/48x48/aborted.gif");
    assertThat(m.find(), is(true));
    assertThat(m.group(1), equalTo("48x48"));
    assertThat(m.group(2), equalTo(""));
    assertThat(m.group(3), equalTo("gif"));
  }

  @Test
  public void patternShouldMatchPNG() {
    final Matcher m = statusIconFilter.patternAborted.matcher("/nocacheImages/48x48/aborted.png");
    assertThat(m.find(), is(true));
    assertThat(m.group(1), equalTo("48x48"));
    assertThat(m.group(2), equalTo(""));
    assertThat(m.group(3), equalTo("png"));
  }

  @Test
  public void patternShouldAlsoMatch() {
    final Matcher m = statusIconFilter.patternAborted.matcher("/nocacheImages/48x48/aborted_anime.gif");
    assertThat(m.find(), is(true));
    assertThat(m.group(1), equalTo("48x48"));
    assertThat(m.group(2), equalTo("_anime"));
  }

  @Test
  public void patternShouldNotMatch() {
    final Matcher m = statusIconFilter.patternAborted.matcher("/nocacheImages/48x48/red_anime.gif");
    assertThat(m.find(), is(false));
  }
}

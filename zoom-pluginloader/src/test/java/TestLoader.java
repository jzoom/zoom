import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import com.jzoom.zoom.common.res.ClassResolvers;
import com.jzoom.zoom.plugin.PluginLoadException;
import com.jzoom.zoom.pluginloader.impl.SimplePluginHolder;
import com.jzoom.zoom.pluginloader.impl.SimplePluginHost;
import com.jzoom.zoom.web.ZoomTestContainer;
import com.jzoom.zoom.web.ZoomWeb;

public class TestLoader {

	@Test
	public void testLoader() throws Exception {
		ZoomWeb web = new ZoomWeb();
		web.init();
		URL url = new URL("file:///Users/jzoom/SourceCode/work/ecard-api/wx-plugin/target/wx-plugin-1.0.0.jar");
		
		
		SimplePluginHost host = new SimplePluginHost( web.getIoc().get(ClassResolvers.class) );
		
		SimplePluginHolder holder = new SimplePluginHolder(url);
		holder.load();
		
		holder.startup(host, web.getIoc().get(ClassResolvers.class) );
	}
}

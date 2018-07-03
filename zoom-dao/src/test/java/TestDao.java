import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jzoom.zoom.dao.impl.SimpleSqlBuilder;
import com.sun.source.tree.AssertTree;

public class TestDao {


	@Test
	public void testRegexp() {
		assertTrue(SimpleSqlBuilder.AS_PATTERN.matcher("sth as a").matches());
	}
}

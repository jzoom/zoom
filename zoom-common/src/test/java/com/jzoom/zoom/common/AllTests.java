package com.jzoom.zoom.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jzoom.zoom.common.codec.CodecTest;
import com.jzoom.zoom.common.config.PropertiesConfigReaderTest;
import com.jzoom.zoom.common.decrypt.TestDecrypt;
import com.jzoom.zoom.common.filtter.PatternFilterFactoryTest;
import com.jzoom.zoom.common.json.JSONTest;
import com.jzoom.zoom.common.utils.OrderedListTest;
import com.jzoom.zoom.common.utils.ValidateUtilTest;

@RunWith(Suite.class)
@SuiteClasses({ CodecTest.class,PropertiesConfigReaderTest.class,TestDecrypt.class,PatternFilterFactoryTest.class,
	JSONTest.class,OrderedListTest.class,ValidateUtilTest.class})
public class AllTests {

}

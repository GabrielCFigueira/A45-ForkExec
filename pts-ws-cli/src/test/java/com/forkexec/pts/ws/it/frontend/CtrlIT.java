package com.forkexec.pts.ws.it.frontend;

import com.forkexec.pts.ws.BadInitFault_Exception;

import org.junit.Before;
import org.junit.Test;

/**
 * Class that tests Control operation
 */
public class CtrlIT extends BaseIT {
	
	@Before
	public void setUp(){
		frontend.ctrlClear();
	}

	@Test (expected = BadInitFault_Exception.class)
	public void initPointsErrorTest() throws BadInitFault_Exception {
		frontend.ctrlInit(-100);
	}
	
	@Test
	public void initPointsTest() throws BadInitFault_Exception {
		frontend.ctrlInit(200);
	}
}
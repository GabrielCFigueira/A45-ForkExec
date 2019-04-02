package com.forkexec.pts.ws.it;

import com.forkexec.pts.ws.BadInitFault_Exception;

import org.junit.Before;
import org.junit.Test;

/**
 * Class that tests Users operation
 */
public class CtrlIT extends BaseIT {
	
	@Before
	public void clear(){
		client.ctrlClear();
	}

	@Test (expected = BadInitFault_Exception.class)
	public void initPointsErrorTest() throws BadInitFault_Exception {
		client.ctrlInit(-100);
	}
	
	@Test
	public void initPointsTest() throws BadInitFault_Exception {
		client.ctrlInit(200);
	}
}
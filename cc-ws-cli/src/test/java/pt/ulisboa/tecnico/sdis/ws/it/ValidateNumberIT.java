package pt.ulisboa.tecnico.sdis.ws.it;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Class that tests ValidateNumber operation
 */
public class ValidateNumberIT extends BaseIT {
	
	@Test
	public void validateNumberError1() {
		assertFalse(client.validateNumber(""));
	}
	
	@Test
	public void validateNumberError2() {
		assertFalse(client.validateNumber(" "));
	}
	
	@Test
	public void validateNumberError3() {
		assertFalse(client.validateNumber("1"));
	}
	
	@Test
	public void validateNumberError4() {
		assertFalse(client.validateNumber("a"));
	}
	
	@Test
	public void validateNumberError5() {
		assertFalse(client.validateNumber("$"));
	}
	
	@Test
	public void validateNumberError6() {
		assertFalse(client.validateNumber("0123456789101112"));
	}
	
	@Test
	public void validateNumber_Example() {
		assertTrue(client.validateNumber("4024007102923926"));
	}
	
	@Test
	public void validateNumber_Visa() {
		assertTrue(client.validateNumber("4012888888881881"));
	}
	
	@Test
	public void validateNumber_MasterCard() {
		assertTrue(client.validateNumber("5555555555554444"));
	}
	
	@Test
	public void validateNumber_Discover() {
		assertTrue(client.validateNumber("6011849224495626"));
	}
}
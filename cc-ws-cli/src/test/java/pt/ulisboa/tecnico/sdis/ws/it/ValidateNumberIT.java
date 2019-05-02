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
	public void validateNumber_Example_Error() {
		assertFalse(client.validateNumber("4024007122923926"));
	}
	
	@Test
	public void validateNumber_Visa() {
		assertTrue(client.validateNumber("4012888888881881"));
	}
	
	@Test
	public void validateNumber_VisaLong() {
		assertTrue(client.validateNumber("4539425109372955004"));
	}
	
	@Test
	public void validateNumber_MasterCard() {
		assertTrue(client.validateNumber("5555555555554444"));
	}
	
	@Test
	public void validateNumber_Discover() {
		assertTrue(client.validateNumber("6011849224495626"));
	}
	
	@Test
	public void validateNumber_DiscoverLong() {
		assertTrue(client.validateNumber("6011519892160773227"));
	}
	
	@Test
	public void validateNumber_DinersClub_Carte_Blanche() {
		assertTrue(client.validateNumber("30365099888928"));
	}
	
	@Test
	public void validateNumber_DinersClub_International() {
		assertTrue(client.validateNumber("36739336179727"));
	}
	
	@Test
	public void validateNumber_DinersClub_North_America() {
		assertTrue(client.validateNumber("5449341035016019"));
	}
	
	@Test
	public void validateNumber_Visa_Electron() {
		assertTrue(client.validateNumber("4917968197279393"));
	}
	
	@Test
	public void validateNumber_JCB() {
		assertTrue(client.validateNumber("3528046019155142"));
	}
	
	@Test
	public void validateNumber_JCBLong() {
		assertTrue(client.validateNumber("3534557107578355864"));
	}
	
	@Test
	public void validateNumber_InstaPayment() {
		assertTrue(client.validateNumber("6371718415428965"));
	}
	
	@Test
	public void validateNumber_Maestro() {
		assertTrue(client.validateNumber("6761997134354045"));
	}
	
	@Test
	public void validateNumber_AmericanExpression() {
		assertTrue(client.validateNumber("373123899700065"));
	}
}
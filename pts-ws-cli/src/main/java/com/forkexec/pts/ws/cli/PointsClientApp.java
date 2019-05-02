package com.forkexec.pts.ws.cli;

/** 
 * Client application. 
 * 
 * Looks for Points using UDDI and arguments provided
 */
public class PointsClientApp {

	public static void main(String[] args) throws Exception {
		// Check arguments.
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + PointsClientApp.class.getName() + " uddiURL N [Test]");
			return;
		}

		String uddiURL = args[0];
		int n = Integer.parseInt(args[1]);
		String testCase = args[2];

		// Create frontend
		PointsFrontEnd frontEnd = new PointsFrontEnd(n, uddiURL);


		switch(testCase){
			case "ping":
				System.out.println("Invoke ping()...");
				String result = frontEnd.ctrlPing("client");
				System.out.print("Result: ");
				System.out.println(result);
			break;
			case "F1":
				System.out.println("Starting test case F1");
				frontEnd.ctrlFail(1, "delay:5");
				frontEnd.pointsBalance("a@b");
				System.out.println("Ending test case F1");
			break;
			case "F2":
				System.out.println("Starting end case F2");
				System.out.println("Ending test case F2");
			break;
		}
	}

}

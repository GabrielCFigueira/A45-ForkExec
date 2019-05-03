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

		// To help measure time taken
		long start;

		frontEnd.ctrlClear();
		System.out.printf("Starting test case %s\n", testCase);
		switch(testCase){
			case "ping":
				System.out.println("Invoke ping()...");
				String result = frontEnd.ctrlPing("client");
				System.out.print("Result: ");
				System.out.println(result);
			break;
			case "F1": // normal case
				frontEnd.pointsBalance("a@b");
			break;
			case "F2": // fault demonstration (delay)
				frontEnd.ctrlFail(1, "delay:10");
				frontEnd.ctrlFail(2, "delay:5");
				frontEnd.pointsBalance("a@b");
			break;
			case "F3": // Cache demonstration (only 1 read, the rest is cache based)
				start = System.currentTimeMillis();
				for(int i = 0; i < 1000; ++i) { frontEnd.pointsBalance("a@b"); }
				System.out.printf("Total time taken: %d ms\n", System.currentTimeMillis() - start);
			break;
			case "F4": // Comparision for implementation without caching
				start = System.currentTimeMillis();
				for(int i = 0; i < 10; ++i) { (new PointsFrontEnd(n, uddiURL)).pointsBalance("a@b"); }
				System.out.printf("Total time taken: %d ms\n", System.currentTimeMillis() - start);
			break;
			case "F5": // Concurrent access: reads on same address
			break;
			case "F6": // Concurrent access: writes on same address
			break;
			case "F7": // Concurrent access: reads on diferent addresses
			break;
			case "F8": // Concurrent access: writes on diferent addresses
			break;
		}
		System.out.printf("Ending test case %s\n", testCase);
	}

}

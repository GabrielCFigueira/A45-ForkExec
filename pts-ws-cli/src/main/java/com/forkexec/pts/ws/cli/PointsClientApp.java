package com.forkexec.pts.ws.cli;

import com.forkexec.pts.ws.cli.exception.EmailIsNotRegisteredException;
import com.forkexec.pts.ws.cli.exception.InvalidEmailAddressException;

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
		switch (testCase) {
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
				for (int i = 0; i < 1000; ++i) {
					frontEnd.pointsBalance("a@b");
				}
				System.out.printf("Total time taken: %d ms\n", System.currentTimeMillis() - start);
			break;
			case "F4": // Comparision for implementation without caching
				start = System.currentTimeMillis();
				for (int i = 0; i < 10; ++i) {
					(new PointsFrontEnd(n, uddiURL)).pointsBalance("a@b");
				}
				System.out.printf("Total time taken: %d ms\n", System.currentTimeMillis() - start);
			break;
			case "F5": // Concurrent access: reads on same address
				frontEnd.ctrlFail(1, "delay:5");
				frontEnd.ctrlFail(2, "delay:5");
				Thread thread1 = new Thread() {
					public void run() {
						try {
							System.out.println(frontEnd.pointsBalance("bomdia@tecnico"));
						} catch (InvalidEmailAddressException | EmailIsNotRegisteredException e) {
							// TODO Auto-generated catch block
						}
					}
				};
				Thread thread2 = new Thread() {
					public void run() {
						try {
							System.out.println(frontEnd.pointsBalance("bomdia@tecnico"));
						} catch (InvalidEmailAddressException | EmailIsNotRegisteredException e) {
							// TODO Auto-generated catch block
						}
					}
				};

				thread1.start();
				thread2.start();

				thread1.join();
				thread2.join();
			break;
			case "F6": // Concurrent access: writes on same address
				frontEnd.ctrlFail(1, "delay:5");
				frontEnd.ctrlFail(2, "delay:5");
				Thread thread1 = new Thread() {
					public void run() {
						try {
							System.out.println(frontEnd.addPoints("bomdia@tecnico"));
						} catch (InvalidEmailAddressException | EmailIsNotRegisteredException e) {
							// TODO Auto-generated catch block
						}
					}
				};
				Thread thread2 = new Thread() {
					public void run() {
						try {
							System.out.println(frontEnd.spendPoints("bomdia@tecnico"));
						} catch (InvalidEmailAddressException | EmailIsNotRegisteredException e) {
							// TODO Auto-generated catch block
						}
					}
				};

				thread1.start();
				thread2.start();

				thread1.join();
				thread2.join();
			break;
			case "F7": // Concurrent access: reads on diferent addresses
				frontEnd.ctrlFail(1, "delay:5");
				frontEnd.ctrlFail(2, "delay:5");

				Thread thread1 = new Thread() {
					public void run() {
						try {
							System.out.println(frontEnd.pointsBalance("bomdia@tecnico"));
						} catch (InvalidEmailAddressException | EmailIsNotRegisteredException e) {
							// TODO Auto-generated catch block
						}
					}
				};
				Thread thread2 = new Thread() {
					public void run() {
						try {
							System.out.println(frontEnd.pointsBalance("boanoite@tecnico"));
						} catch (InvalidEmailAddressException | EmailIsNotRegisteredException e) {
							// TODO Auto-generated catch block
						}
					}
				};

				thread1.start();
				thread2.start();

				thread1.join();
				thread2.join();
			break;
			case "F8": // Concurrent access: writes on diferent addresses
				frontEnd.ctrlFail(1, "delay:5");
				frontEnd.ctrlFail(2, "delay:5");

				frontEnd.ctrlFail(1, "delay:5");
				frontEnd.ctrlFail(2, "delay:5");

				Thread thread1 = new Thread() {
					public void run() {
						try {
							System.out.println(frontEnd.addPoints("bomdia@tecnico", 100));
						} catch (InvalidEmailAddressException | EmailIsNotRegisteredException e) {
							// TODO Auto-generated catch block
						}
					}
				};
				Thread thread2 = new Thread() {
					public void run() {
						try {
							System.out.println(frontEnd.spendPoints("boanoite@tecnico", 100));
						} catch (InvalidEmailAddressException | EmailIsNotRegisteredException e) {
							// TODO Auto-generated catch block
						}
					}
				};

				thread1.start();
				thread2.start();

				thread1.join();
				thread2.join();
			break;
		}
		System.out.printf("Ending test case %s\n", testCase);
	}

}

package eishub;

import javax.swing.SwingUtilities;

enum RobotAction {
	UNKNOWN, PUSH, WAIT
};

/**
 * Carriage environment runs in its own thread. it is killed with
 * {@link Thread#interrupt()}.
 *
 */
public class CarriageEnvironment implements Runnable {

	private CarriageWindow window = new CarriageWindow();

	private RobotAction robotAction1 = RobotAction.UNKNOWN;
	private RobotAction robotAction2 = RobotAction.UNKNOWN;

	private Long stepNum = new Long(0);

	private int carriagePos = 0;

	/**
	 * 
	 */
	public CarriageEnvironment() {

	}

	/**
	 * 
	 */
	public void run() {
		try {
			while (true) {
				System.out
						.println("Step counter: "
								+ stepNum
								+ ". Blocking until both robots have performed an action.");
				while (robotAction1 == RobotAction.UNKNOWN
						|| robotAction2 == RobotAction.UNKNOWN) {
					System.out.println("Waiting (.5s): Robot 1 choose "
							+ robotAction1 + " and robot 2 choose "
							+ robotAction2 + ".");
					Thread.sleep(500);
				}
				System.out.println("Ready: Robot 1 choose " + robotAction1
						+ " and robot 2 choose " + robotAction2 + ".");

				if (robotAction1 == RobotAction.PUSH
						&& robotAction2 == RobotAction.WAIT) {
					carriagePos++;
					if (carriagePos >= 3) {
						carriagePos = 0;
					}
				} else if (robotAction1 == RobotAction.WAIT
						&& robotAction2 == RobotAction.PUSH) {
					carriagePos--;
					if (carriagePos < 0) {
						carriagePos = 2;
					}
				}

				// Update display.
				window.setState(carriagePos);

				// Reset actions and increase step counter.
				robotAction1 = RobotAction.UNKNOWN;
				robotAction2 = RobotAction.UNKNOWN;
				stepNum++;

				// block for 0,5 a second
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			System.out.println("environment was interrupted and killed");
		}
	}

	/**
	 * 
	 * @return
	 */
	public int getCarriagePos() {
		return carriagePos;
	}

	synchronized public void robotAction(String entity, RobotAction action) {
		if (entity.equals("robot1") && robotAction1 == RobotAction.UNKNOWN) {
			System.out.println("Robot 1 performs " + action);
			robotAction1 = action;
		}
		if (entity.equals("robot2") && robotAction2 == RobotAction.UNKNOWN) {
			System.out.println("Robot 2 performs " + action);
			robotAction2 = action;
		}
	}

	/**
	 * 
	 */
	synchronized public void robotPush1() {
		if (robotAction1 == RobotAction.UNKNOWN) {
			System.out.println("Robot 1 Pushes");
			robotAction1 = RobotAction.PUSH;
		}
		// System.out.println("Push 1");
		// Long currStep = stepNum;
		// System.out.println("Set action");
		// robotAction1 = RobotAction.PUSH;
		// System.out.println("Block");
		// while (currStep.equals(stepNum));
		// System.out.println("Done Push 1");
	}

	/**
	 * 
	 */
	synchronized public void robotPush2() {
		if (robotAction2 == RobotAction.UNKNOWN) {
			System.out.println("Robot 2 Pushes");
			robotAction2 = RobotAction.PUSH;
		}
		// System.out.println("Push 2");
		// Long currStep = stepNum;
		// System.out.println("Set action");
		// robotAction2 = RobotAction.PUSH;
		// System.out.println("Block");
		// while (currStep.equals(stepNum));
		// System.out.println("Done Push 2");
	}

	/**
	 * 
	 */
	public void robotWait1() {
		System.out.println("Wait 1");
		Long currStep = stepNum;
		System.out.println("Set action");
		robotAction1 = RobotAction.WAIT;
		System.out.println("Block");
		while (currStep.equals(stepNum))
			;
		System.out.println("Done wait 1");
	}

	/**
	 * 
	 */
	public void robotWait2() {
		System.out.println("Wait 2");
		Long currStep = stepNum;
		System.out.println("Set action");
		robotAction2 = RobotAction.WAIT;
		System.out.println("Block");
		while (currStep.equals(stepNum))
			;
		System.out.println("Done wait 2");
	}

	/**
	 * 
	 * @return
	 */
	public long getStepNumber() {
		return stepNum;
	}

	/**
	 * 
	 * @return
	 */
	public int getRobotPercepts1() {
		return carriagePos;
	}

	/**
	 * 
	 * @return
	 */
	public int getRobotPercepts2() {
		return carriagePos;
	}

	/**
	 * 
	 */
	public void release() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				window.dispose();
			}
		});
	}

}
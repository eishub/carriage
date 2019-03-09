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

	public CarriageEnvironment() {
	}

	@Override
	public void run() {
		try {
			while (true) {
				System.out.println(
						"Step counter: " + this.stepNum + ". Blocking until both robots have performed an action.");
				while (this.robotAction1 == RobotAction.UNKNOWN || this.robotAction2 == RobotAction.UNKNOWN) {
					System.out.println("Waiting (.5s): Robot 1 choose " + this.robotAction1 + " and robot 2 choose "
							+ this.robotAction2 + ".");
					Thread.sleep(500);
				}
				System.out.println("Ready: Robot 1 choose " + this.robotAction1 + " and robot 2 choose "
						+ this.robotAction2 + ".");

				if (this.robotAction1 == RobotAction.PUSH && this.robotAction2 == RobotAction.WAIT) {
					this.carriagePos++;
					if (this.carriagePos >= 3) {
						this.carriagePos = 0;
					}
				} else if (this.robotAction1 == RobotAction.WAIT && this.robotAction2 == RobotAction.PUSH) {
					this.carriagePos--;
					if (this.carriagePos < 0) {
						this.carriagePos = 2;
					}
				}

				// Update display.
				this.window.setState(this.carriagePos);

				// Reset actions and increase step counter.
				this.robotAction1 = RobotAction.UNKNOWN;
				this.robotAction2 = RobotAction.UNKNOWN;
				this.stepNum++;

				// block for 0,5 a second
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			System.out.println("environment was interrupted and killed");
		}
	}

	public int getCarriagePos() {
		return this.carriagePos;
	}

	synchronized public void robotAction(String entity, RobotAction action) {
		if (entity.equals("robot1") && this.robotAction1 == RobotAction.UNKNOWN) {
			System.out.println("Robot 1 performs " + action);
			this.robotAction1 = action;
		}
		if (entity.equals("robot2") && this.robotAction2 == RobotAction.UNKNOWN) {
			System.out.println("Robot 2 performs " + action);
			this.robotAction2 = action;
		}
	}

	synchronized public void robotPush1() {
		if (this.robotAction1 == RobotAction.UNKNOWN) {
			System.out.println("Robot 1 Pushes");
			this.robotAction1 = RobotAction.PUSH;
		}
	}

	synchronized public void robotPush2() {
		if (this.robotAction2 == RobotAction.UNKNOWN) {
			System.out.println("Robot 2 Pushes");
			this.robotAction2 = RobotAction.PUSH;
		}
	}

	public void robotWait1() {
		System.out.println("Wait 1");
		Long currStep = this.stepNum;
		System.out.println("Set action");
		this.robotAction1 = RobotAction.WAIT;
		System.out.println("Block");
		while (currStep.equals(this.stepNum)) {
		}
		System.out.println("Done wait 1");
	}

	public void robotWait2() {
		System.out.println("Wait 2");
		Long currStep = this.stepNum;
		System.out.println("Set action");
		this.robotAction2 = RobotAction.WAIT;
		System.out.println("Block");
		while (currStep.equals(this.stepNum)) {
		}
		System.out.println("Done wait 2");
	}

	public long getStepNumber() {
		return this.stepNum;
	}

	public int getRobotPercepts1() {
		return this.carriagePos;
	}

	public int getRobotPercepts2() {
		return this.carriagePos;
	}

	public void release() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				CarriageEnvironment.this.window.dispose();
			}
		});
	}
}
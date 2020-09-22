package eishub;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eis.EIDefaultImpl;
import eis.PerceptUpdate;
import eis.exceptions.ActException;
import eis.exceptions.EntityException;
import eis.exceptions.ManagementException;
import eis.iilang.Action;
import eis.iilang.EnvironmentState;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.Percept;

@SuppressWarnings("serial")
public class EnvironmentInterface extends EIDefaultImpl {
	private Numeral robot1Pos, robot2Pos, robot1Step, robot2Step;
	private CarriageEnvironment env;
	private Thread t;

	/**
	 * Empty constructor.
	 */
	public EnvironmentInterface() throws ManagementException {
	}

	@Override
	public void init(final Map<String, Parameter> parameters) throws ManagementException {
		System.out.println("Initializing carriage environment...");
		reset(parameters);

		try { // Create two entities.
			addEntity("robot1", "robot");
			addEntity("robot2", "robot");
		} catch (final EntityException e) {
			new ManagementException("Could not initialize robots", e);
		}
	}

	@Override
	public void kill() throws ManagementException {
		terminate();
		setState(EnvironmentState.KILLED);
	}

	/**
	 * Clean up.
	 */
	private void terminate() {
		if (this.t != null) {
			this.t.interrupt();
		}
		if (this.env != null) {
			this.env.release();
			this.env = null;
		}
	}

	@Override
	public void reset(final Map<String, Parameter> parameters) throws ManagementException {
		terminate();

		// Create environment.
		this.env = new CarriageEnvironment();

		// Set state to paused.
		setState(EnvironmentState.PAUSED);

		this.t = new Thread(this.env);
		this.t.start();
	}

	/**
	 * Returns all current percepts.
	 */
	@Override
	public PerceptUpdate getPerceptsForEntity(final String entity) {
		final List<Percept> toAdd = new LinkedList<>();
		final List<Percept> toDelete = new LinkedList<>();
		if (entity.equals("robot1")) {
			final Numeral newPos = new Numeral(this.env.getRobotPercepts1());
			if (!newPos.equals(this.robot1Pos)) {
				toAdd.add(new Percept("carriagePos", newPos));
				if (this.robot1Pos != null) {
					toDelete.add(new Percept("carriagePos", this.robot1Pos));
				}
				this.robot1Pos = newPos;
			}
			final Numeral newStep = new Numeral(this.env.getStepNumber());
			if (!newStep.equals(this.robot1Step)) {
				toAdd.add(new Percept("step", newStep));
				if (this.robot1Step != null) {
					toDelete.add(new Percept("step", this.robot1Step));
				}
				this.robot1Step = newStep;
			}
		} else if (entity.equals("robot2")) {
			final Numeral newPos = new Numeral(this.env.getRobotPercepts2());
			if (!newPos.equals(this.robot2Pos)) {
				toAdd.add(new Percept("carriagePos", newPos));
				if (this.robot2Pos != null) {
					toDelete.add(new Percept("carriagePos", this.robot2Pos));
				}
				this.robot2Pos = newPos;
			}
			final Numeral newStep = new Numeral(this.env.getStepNumber());
			if (!newStep.equals(this.robot2Step)) {
				toAdd.add(new Percept("step", newStep));
				if (this.robot2Step != null) {
					toDelete.add(new Percept("step", this.robot2Step));
				}
				this.robot2Step = newStep;
			}
		}
		return new PerceptUpdate(toAdd, toDelete);

	}

	/**
	 * Perform push action.
	 *
	 * @param entity Only one of the two entities 'robot1' or 'robot2' can perform a
	 *               push action.
	 */
	public void actionpush(final String entity) {
		this.env.robotAction(entity, RobotAction.PUSH);
	}

	/**
	 * Perform wait action.
	 *
	 * @param entity Only one of the two entities 'robot1' or 'robot2' can perform a
	 *               wait action.
	 */
	public void actionwait(final String entity) {
		this.env.robotAction(entity, RobotAction.WAIT);
	}

	@Override
	public boolean isSupportedByEntity(final Action action, final String entity) {
		if (getEntities().contains(entity)) {
			return isSupportedByEnvironment(action);
		} else {
			return false;
		}
	}

	@Override
	public boolean isSupportedByEnvironment(final Action action) {
		return isSupportedByType(action, "robot");
	}

	@Override
	public boolean isSupportedByType(final Action action, final String type) {
		if (type.equals("robot")) {
			final String name = action.getName();
			return name.equals("wait") || name.equals("push");
		} else {
			return false;
		}
	}

	/**
	 * Performs requested action, if entity can perform it.
	 */
	@Override
	public void performEntityAction(final Action action, final String entity) throws ActException {
		final String name = action.getName();
		if (name.equals("wait")) {
			actionwait(entity);
		} else if (name.equals("push")) {
			actionpush(entity);
		} else {
			throw new ActException(ActException.NOTSUPPORTEDBYENVIRONMENT);
		}
	}
}

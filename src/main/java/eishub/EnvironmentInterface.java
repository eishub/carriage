package eishub;

import java.util.LinkedList;
import java.util.Map;

import eis.*;
import eis.exceptions.ActException;
import eis.exceptions.EntityException;
import eis.exceptions.EnvironmentInterfaceException;
import eis.exceptions.ManagementException;
import eis.iilang.Action;
import eis.iilang.EnvironmentState;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import eis.iilang.Numeral;

@SuppressWarnings("serial")
public class EnvironmentInterface extends EIDefaultImpl {
	
	private Environment env;
	private Thread t;
	
	/**
	 * Empty constructor.
	 */
	public EnvironmentInterface() throws ManagementException {

	}
	
	@Override
	public void init(Map<String, Parameter> parameters) throws ManagementException {
		
		System.out.println("Initializing carriage environment.");
		reset(parameters);

		// Create two entities.
		try {
			this.addEntity("robot1","robot");
			this.addEntity("robot2","robot");
		} catch (EntityException e) {
			new ManagementException("Could not initialize robots", e);
		}
	}
	
	@Override
	public void kill() throws ManagementException {
		this.terminate();
		setState(EnvironmentState.KILLED);
	}
	
	/**
	 * Clean up.
	 */
	private void terminate() {
		if (t != null) {
			t.interrupt();
		}
		if (env != null) {
			env.release();
			env = null;
		}
	}
	
	@Override
	public void reset(Map<String, Parameter> parameters)
			throws ManagementException {
		this.terminate();
		
		// Create environment.
		env = new Environment();
		
		// Set state to paused.
		setState(EnvironmentState.PAUSED);
		
		t = new Thread(env);
		t.start();
	}
	
	/**
	 * 
	 */
	public void run() {

		while(true) {
			// provide current step counter as percept to all connected agents
			long step = env.getStepNumber();
			Percept percept = new Percept("step", new Numeral(step) );
			
			for(String entity : this.getEntities()) {
				try {
					this.notifyAgentsViaEntity(percept, entity);
				} catch (EnvironmentInterfaceException e) {
					// Simply discard exception...?
					System.out.println("Could not send percept " + percept.toProlog());
				}
			}

			// block for 1 second
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// Simply discard exception...?
				System.out.println(e);
			}
		}
	}

	/**
	 * Returns all current percepts.
	 */
	@Override
	public LinkedList<Percept> getAllPerceptsFromEntity(String entity) {

		LinkedList<Percept> ret = new LinkedList<Percept>();
		
		if (entity.equals("robot1")) {
			ret.add(new Percept("carriagePos", new Numeral(env.getRobotPercepts1())));
		} else if (entity.equals("robot2")) {
			ret.add(new Percept("carriagePos", new Numeral(env.getRobotPercepts2())));
		}

		return ret;
	}
	
	/**
	 * Perform push action.
	 * 
	 * @param entity
	 * 		Only one of the two entities 'robot1' or 'robot2' can perform a push action.
	 * @return Always returns percept "success"!?
	 */
	public Percept actionpush(String entity) {
		env.robotAction(entity, RobotAction.PUSH);
		
//		if (entity.equals("robot1")) {
//			env.robotPush1();
//		}
//		if (entity.equals("robot2")) {
//			env.robotPush2();
//		}

		return new Percept("success");
	}

	/**
	 * Perform wait action.
	 * 
	 * @param entity
	 * 		Only one of the two entities 'robot1' or 'robot2' can perform a wait action.
	 * @return Always returns percept "success"!?
	 */
	public Percept actionwait(String entity) {
		env.robotAction(entity, RobotAction.WAIT);
		
//		if (entity.equals("robot1")) {
//			env.robotWait1();
//		}
//		if (entity.equals("robot2")) {
//			env.robotWait2();
//		}

		return new Percept("success");
	}

	@Override
	public boolean isSupportedByEntity(Action action, String entity) {
		
		if (action.getName().equals("push") && getEntities().contains(entity)) {
			return true;
		}
		if (action.getName().equals("wait") && getEntities().contains(entity)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isSupportedByEnvironment(Action action) {

		if (action.getName().equals("push") || action.getName().equals("wait")) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isSupportedByType(Action action, String type) {

		if (type.equals("robot") && action.getName().equals("push") ) {
			return true;
		}
		if (type.equals("robot") && action.getName().equals("wait")) {
			return true;		
		}
		
		return false;
	}

	/**
	 * Performs requested action, if entity can perform it.
	 */
	@Override
	public Percept performEntityAction(String entity, Action action) throws ActException {

		if (action.getName().equals("wait")) {
			return actionwait(entity);
		}
		else if ( action.getName().equals("push") ) {
			return actionpush(entity);
		}
		else {
			throw new AssertionError("action " + action.getName() + "not recognized");			
		}
	}

	@Override
	public String queryEntityProperty(String entity, String property) {
		throw new UnsupportedOperationException("queryEntityProperty has not been implemented.");
	}

	@Override
	public String queryProperty(String property) {
		throw new UnsupportedOperationException("queryProperty has not been implemented.");
	}

	/**
	 * Returns expected version of Environment Interface Standard that is used.
	 */
	public String requiredVersion() {
		return "0.3";
	}

}

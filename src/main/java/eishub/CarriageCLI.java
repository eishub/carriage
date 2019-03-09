package eishub;

import java.util.Collection;
import java.util.Map;

import eis.exceptions.ActException;
import eis.exceptions.AgentException;
import eis.exceptions.ManagementException;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
import eis.exceptions.RelationException;
import eis.iilang.Action;
import eis.iilang.Percept;

abstract class Agent extends Thread {
	protected EnvironmentInterface ei = null;
	protected String id = null;

	public Agent(EnvironmentInterface ei, String id) {
		this.ei = ei;
		this.id = id;
		setPriority(MIN_PRIORITY);
	}

	protected void say(String msg) {
		System.out.println(this.id + " says: " + msg);
	}

}

class PushingAgent extends Agent {
	public PushingAgent(EnvironmentInterface ei, String id) {
		super(ei, id);
	}

	@Override
	public void run() {
		try {
			this.ei.associateEntity(this.id, this.ei.getFreeEntities().get(0));

			while (true) {
				// perceive
				Map<String, Collection<Percept>> percepts = null;
				percepts = this.ei.getAllPercepts(this.id);
				say("I believe the carriage is at " + percepts.values());

				// act
				this.ei.performAction(this.id, new Action("push"));

				try {
					Thread.sleep(950);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (PerceiveException | RelationException | ActException | NoEnvironmentException e) {
			e.printStackTrace();
		}
	}
}

class AlternatingAgent extends Agent {
	public AlternatingAgent(EnvironmentInterface ei, String id) {
		super(ei, id);
	}

	@Override
	public void run() {
		try {
			this.ei.associateEntity(this.id, this.ei.getFreeEntities().get(0));

			while (true) {
				// perceive
				Map<String, Collection<Percept>> percepts = null;
				percepts = this.ei.getAllPercepts(this.id);
				say("I believe the carriage is at " + percepts.values());

				// act
				this.ei.performAction(this.id, new Action("push"));

				// perceive
				percepts = this.ei.getAllPercepts(this.id);
				say("I believe the carriage is at " + percepts.values());

				// act
				this.ei.performAction(this.id, new Action("wait"));

				try {
					Thread.sleep(950);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (PerceiveException | RelationException | ActException | NoEnvironmentException e) {
			e.printStackTrace();
		}
	}
}

public class CarriageCLI {
	public static void main(String[] args) {
		try {
			// loading the environment
			EnvironmentInterface ei = new EnvironmentInterface();
			if (ei.isStartSupported()) {
				ei.start();
			}

			Thread.sleep(1000);

			// creating two agents
			Agent ag1 = new PushingAgent(ei, "ag1");
			Agent ag2 = new AlternatingAgent(ei, "ag2");

			// registering agents
			ei.registerAgent("ag1");
			ei.registerAgent("ag2");

			// starting the agents
			ag1.start();
			ag2.start();
		} catch (InterruptedException | AgentException | ManagementException e) {
			e.printStackTrace();
		}
	}
}

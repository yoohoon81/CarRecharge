package ScheduleSystem;

import jade.core.Agent;

public class CarAgent extends Agent implements CarAgentInterface {
	
	public int Id;
	private int RequiredLoad;	
	private int LoadToRecharge;	
	private int Deadline;	
	private int ElapsedSeconds;
	public CarAgent() {
		registerO2AInterface(CarAgentInterface.class, this);	
		ElapsedSeconds = 0;
	}
	
	protected void setup() {
		
		Object[] args = getArguments();
		System.out.println("Starting up a CarAgent" + args[0]);
		Id = Integer.parseInt(args[0].toString());
		RequiredLoad = Integer.parseInt(args[2].toString());
		LoadToRecharge = Integer.parseInt(args[2].toString());
		Deadline = Integer.parseInt(args[1].toString());
	}

	@Override
	public int getDeadline() {
		return Deadline;
	}

	@Override
	public int getLoadToRecharge() {
		return LoadToRecharge;
	}
	@Override
	public int getRequiredLoad() {
		return RequiredLoad;
	}

	@Override
	public int getId() {
		return Id;
	}

	@Override
	public void recharge() {
		LoadToRecharge -= 1;
		
	}

	@Override
	public int ElapseSecond() {
		return ElapsedSeconds++;
	}

	@Override
	public int getChargedPercent() {
		if(RequiredLoad <= 0)
			return 100;
		
		return (RequiredLoad - LoadToRecharge) * 100 / RequiredLoad;
	}	

	@Override
	public void reset()
	{
		LoadToRecharge = RequiredLoad;
		ElapsedSeconds = 0;
	}
}

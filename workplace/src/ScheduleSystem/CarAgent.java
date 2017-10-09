package ScheduleSystem;

import jade.core.Agent;

public class CarAgent extends Agent implements CarAgentInterface {
	
	public int Id;
	private int InitialRequiredLoad;	
	private int CurrentRequiredLoad;	
	private int StartTime;		
	private int Deadline;	
	private int ElapsedSeconds;
	public boolean BeingRecharged;
	public CarAgent() {
		registerO2AInterface(CarAgentInterface.class, this);	
		ElapsedSeconds = 0;
	}
	
	// (BY) Generate car and set values of car property
	protected void setup() {
		
		Object[] args = getArguments();
		System.out.println("Starting up a CarAgent" + args[0]);
		Id = Integer.parseInt(args[0].toString());
		InitialRequiredLoad = Integer.parseInt(args[1].toString());
		CurrentRequiredLoad = Integer.parseInt(args[1].toString());
		StartTime = Integer.parseInt(args[2].toString());
		Deadline = Integer.parseInt(args[3].toString());
	}

	@Override
	public int GetDeadline() {
		return Deadline;
	}

	@Override
	public int GetCurrentRequiredLoad() {
		return CurrentRequiredLoad;
	}

	@Override
	public int GetInitialRequiredLoad() {
		return InitialRequiredLoad;
	}

	@Override
	public int GetId() {
		return Id;
	}

	@Override
	public void Recharge() {
		CurrentRequiredLoad -= 1;
		
	}

	@Override
	public int ElapseSecond() {
		return ElapsedSeconds++;
	}

	@Override
	public int GetChargedPercent() {
		if(InitialRequiredLoad <= 0)
			return 100;
		
		return (InitialRequiredLoad - CurrentRequiredLoad) * 100 / InitialRequiredLoad;
	}	

	@Override
	public void Reset()
	{
		CurrentRequiredLoad = InitialRequiredLoad;
		ElapsedSeconds = 0;
		EndRecharging();
	}

	@Override
	public int GetStartTime() {
		return StartTime;
	}
	@Override
	public boolean IsBeingRecharged() {
		return BeingRecharged;
	}
	@Override
	public void StartRecharging() {
		 BeingRecharged = true;
	}
	@Override
	public void EndRecharging() {
		 BeingRecharged = false;
	}
	
	
}

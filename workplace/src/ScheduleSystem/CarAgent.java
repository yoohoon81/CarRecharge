package ScheduleSystem;

import jade.core.Agent;

public class CarAgent extends Agent implements CarAgentInterface {
	
	public int Id;
	private int InitialRequiredLoad;	
	private int CurrentRequiredLoad;	
	private int StartTime;		
	private int Deadline;	
	private int ElapsedSeconds;
	private int WaitedSeconds;
	public boolean BeingRecharged;
	public CarStatus CarStatus;
	
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
		CarStatus = ScheduleSystem.CarStatus.Pending;
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
	public int IncreaseWaitedSeconds() {
		return WaitedSeconds++;
	}
	
	@Override
	public int GetWaitedSeconds() {
		return WaitedSeconds++;
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
		BeingRecharged = false;
		CarStatus = ScheduleSystem.CarStatus.Pending;
		WaitedSeconds = 0;
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
		 CarStatus = ScheduleSystem.CarStatus.Recharging;
	}
	@Override
	public void EndRecharging() {
		 BeingRecharged = false;
		 CarStatus = GetChargedPercent() < 100 ? ScheduleSystem.CarStatus.IncompleteCharged : ScheduleSystem.CarStatus.FullyCharged;			 
	}

	@Override
	public CarStatus GetStatus() {
		return CarStatus;
	}

	@Override
	public void SetStatus(ScheduleSystem.CarStatus status) {
		CarStatus = status;		
	}

	@Override
	public void Set(int startTime, int deadline, int requiredLoad) {
		StartTime = startTime;
		Deadline = deadline;
		InitialRequiredLoad = requiredLoad;
	}
	
	
}

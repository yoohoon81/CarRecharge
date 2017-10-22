package ScheduleSystem;

import java.util.concurrent.ThreadLocalRandom;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

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

	private int count;
	
	public CarAgent() {
		registerO2AInterface(CarAgentInterface.class, this);	
		ElapsedSeconds = 0;
	}
	
	// (BY) Generate car and set values of car property
	protected void setup() {
		

		Object[] args = getArguments();
		System.out.println("Starting up a CarAgent" + args[0]);
		Id = Integer.parseInt(args[0].toString());
		InitialRequiredLoad = GetRandomInt(25, 500);
		CurrentRequiredLoad = InitialRequiredLoad;
		StartTime = GetRandomInt(0, 500);
		Deadline = StartTime +	GetRandomInt(100, 500);		
	}

	private int GetRandomInt(int start, int end)
	{
		return ThreadLocalRandom.current().nextInt(start, end);
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
	
	@Override
	public void sendrequest()
	{
		if(count == 0)
		{
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);	
			msg.addReceiver(new AID("SchedulingAgent", AID.ISLOCALNAME));
			msg.setContent(getLocalName() + " :  need to charge " + InitialRequiredLoad + "	need to start from " + StartTime + " to " + Deadline );	
			msg.setSender(getAID());

			send(msg);
			count++;
		}
		
//		if(count == 0)
//		{
//			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);	
//			msg.addReceiver(new AID("SchedulingAgent", AID.ISLOCALNAME));
//			msg.setContent(getLocalName() + " :  Request charging...");	
//			System.out.println(msg.getContent());
//			
//			send(msg);
//			count++;
//		}
		
	}
	
	@Override
	public void resetcount()
	{
		count = 0;
	}
	
}

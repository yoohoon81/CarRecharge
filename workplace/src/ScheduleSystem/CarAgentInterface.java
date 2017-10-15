package ScheduleSystem;

public interface CarAgentInterface {
	public int GetDeadline();
	public int GetStartTime();
	public int GetCurrentRequiredLoad();
	public int GetInitialRequiredLoad();
	public int GetChargedPercent();	
	public int GetId();
	public void Recharge();
	public int IncreaseWaitedSeconds();
	public int GetWaitedSeconds();
	public void Reset();
	public boolean IsBeingRecharged();
	public void StartRecharging();
	public void EndRecharging();
	public CarStatus GetStatus(); 
	public void SetStatus(CarStatus status);
	public void Set(int startTime, int deadline, int requiredLoad); 
}

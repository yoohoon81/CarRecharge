package ScheduleSystem;

public interface CarAgentInterface {
	public int GetDeadline();
	public int GetStartTime();
	public int GetCurrentRequiredLoad();
	public int GetInitialRequiredLoad();
	public int GetChargedPercent();	
	public int GetId();
	public void Recharge();
	public int ElapseSecond();
	public void Reset();
	public boolean IsBeingRecharged();
	public void StartRecharging();
	public void EndRecharging();
}

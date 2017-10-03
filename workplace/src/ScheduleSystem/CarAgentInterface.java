package ScheduleSystem;

public interface CarAgentInterface {
	public int getDeadline();
	public int getLoadToRecharge();
	public int getRequiredLoad();
	public int getChargedPercent();	
	public int getId();
	public void recharge();
	public int ElapseSecond();
	public void reset();
}

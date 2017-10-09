package ScheduleSystem;

public class Outlet
{
	public int Id;
	public boolean BeingUsed;
	public int UsedLoad;
	public Integer CarIdUsedBy;
	public int NextTurnNumber;
	
	public Outlet(int id)
	{
		Id = id;
		UsedLoad = 0;
		CarIdUsedBy = (Integer) null;
		NextTurnNumber = 0;
	}
	
	public boolean IsOccupied()
	{
		return CarIdUsedBy != (Integer) null;
	}
	
	public void Release()
	{
		CarIdUsedBy = (Integer) null;	
	}
	
	public int Used()
	{
		return UsedLoad++;
	}
}
package ScheduleSystem;

public class Car
{
	Car(int id, int initialRequiredLoad, int currentRequiredLoad, int deadline)
	{
		Id = id;
		InitialRequiredLoad = initialRequiredLoad;
		CurrentRequiredLoad = currentRequiredLoad;
		Deadline = deadline;
	}
	public int Id;
	public int InitialRequiredLoad;
	public int CurrentRequiredLoad;
	public int Deadline;	
}

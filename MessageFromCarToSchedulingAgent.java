package ScheduleSystem;

import java.util.Date;

public class MessageFromCarToSchedulingAgent {
	public int CarNumber; // id of this car
	public int MaxLoad; // of this car (KW)
	public int CurrentLoad; // of this car
	public Date EarliestStartTime; // the time this car wants to start from
	public Date LatestChargedTime; // the time this car wants to finish charging at latest.	
}

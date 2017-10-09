package ScheduleSystem;

import java.util.Date;

public class MessageFromSchedulingAgentToCar {
	public int BayNumber; // Bay the car should come to recharge
	public Date StartTime; // booked check-in time
	public Date FinishTime; // booked check-out time
}

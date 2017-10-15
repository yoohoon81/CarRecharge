package ScheduleSystem;

import java.util.ArrayList;

public class Genome {
	public ArrayList<Integer> CarIds;

	public int TotalTimeElapsed;
	public int TotalRechargedLoad;
	public int TotalWaitedSeconds;
	public double StandardDeviationRechargedPercent;
	public double StandardDeviationWaitedSeconds;

	public int RequiredLoadWeight;
	public int TimeToDeadlineWeight;
	public int WaitedSecondsWeight;
	
	public Genome()
	{
		CarIds = new ArrayList<Integer>();
	}
	
	public void AddCarId(int id)
	{
		CarIds.add(id);
	}
	
	//(BY) The higher, the better
	public double GetScore(Genome min, Genome max)
	{
		return - Sigmoid(GetSigmoidParam(TotalTimeElapsed, min.TotalTimeElapsed, max.TotalTimeElapsed))
				+ Sigmoid(GetSigmoidParam(TotalRechargedLoad, min.TotalRechargedLoad, max.TotalRechargedLoad))
				- Sigmoid(GetSigmoidParam(TotalWaitedSeconds, min.TotalWaitedSeconds, max.TotalWaitedSeconds))
				- Sigmoid(GetSigmoidParam(StandardDeviationRechargedPercent, min.StandardDeviationRechargedPercent, max.StandardDeviationRechargedPercent))
				- Sigmoid(GetSigmoidParam(StandardDeviationWaitedSeconds, min.StandardDeviationWaitedSeconds, max.StandardDeviationWaitedSeconds));
	}
	
	private static double GetSigmoidParam(double v, double min, double max)
	{
		if (max == min)
			return 0;
		return (v - (max + min) / 2) / (max - min);
	}
	
	private static double Sigmoid(double x)
	{
	    return 1 / (1 + Math.exp(-x));
	}
}

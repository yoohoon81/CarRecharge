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
//	public double GetScore(Genome worst, Genome best)
//	{
//		double score = - Sigmoid(GetSigmoidParam(TotalTimeElapsed, best.TotalTimeElapsed, worst.TotalTimeElapsed))
//				+ Sigmoid(GetSigmoidParam(TotalRechargedLoad, worst.TotalRechargedLoad, best.TotalRechargedLoad))
//				- Sigmoid(GetSigmoidParam(TotalWaitedSeconds, best.TotalWaitedSeconds, worst.TotalWaitedSeconds))
//				- Sigmoid(GetSigmoidParam(StandardDeviationRechargedPercent, best.StandardDeviationRechargedPercent, worst.StandardDeviationRechargedPercent))
//				- Sigmoid(GetSigmoidParam(StandardDeviationWaitedSeconds, best.StandardDeviationWaitedSeconds, worst.StandardDeviationWaitedSeconds));
//		return score;
//	}
	
	public double GetScore(double[] weights)
	{
		return GetScore(weights[0], weights[1], weights[2], weights[3], weights[4]);
	}
	
	public double GetScore(double TotalTimeElapsedWeight, double TotalRechargedLoadWeight, double TotalWaitedSecondsWeight, double StandardDeviationRechargedPercentWeight, double StandardDeviationWaitedSecondsWeight)
	{
		double score = - TotalTimeElapsed * TotalTimeElapsedWeight
				+ TotalRechargedLoad * TotalRechargedLoadWeight
				- TotalWaitedSeconds * TotalWaitedSecondsWeight
				- StandardDeviationRechargedPercent * StandardDeviationRechargedPercentWeight
				- StandardDeviationWaitedSeconds * StandardDeviationWaitedSecondsWeight;
		return score;
	}
	
	private static double GetSigmoidParam(double v, double min, double max)
	{
		if (max == min)
			return 0;
		return (v - (max + min) / 2) / (max - min);
	}
	
	private static double Sigmoid(double x)
	{
	    double v = 1 / (1 + Math.exp(-x));
	    return v;
	}
}

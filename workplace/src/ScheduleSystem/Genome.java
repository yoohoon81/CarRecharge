package ScheduleSystem;

import java.util.ArrayList;

public class Genome {
	public ArrayList<Integer> CarIds;

	public int TotalTimeElapsed;
	public double StandardDeviation;
	public Genome()
	{
		CarIds = new ArrayList<Integer>();
	}
	
	public void AddCarId(int id)
	{
		CarIds.add(id);
	}
	
	public double GetScore()
	{
		return StandardDeviation * 10 + TotalTimeElapsed;
		//return Sigmoid(StandardDeviation) + Sigmoid(TotalTimeElapsed);
	}
	
	private static double Sigmoid(double x)
	{
	    return 1 / (1 + Math.exp(-x));
	}
}

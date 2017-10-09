package ScheduleSystem;

import java.util.ArrayList;

import ScheduleSystem.Outlet;

public class Bay
{
	public Bay(int id, int capacity, int numberOfOutlets)
	{
		Id = id;
		Capacity = capacity;
		Outlets = new ArrayList<Outlet>();
		UsedLoad = 0;
		for(int i = 0; i < numberOfOutlets; i++) { Outlets.add(new Outlet(i)); }
		System.out.println("Bay " + id + " created");
	}
	public int Id;
	public int Capacity;
	public ArrayList<Outlet> Outlets;
	public int UsedLoad;
	
	public int Used()
	{		
		return UsedLoad++;
	}
}
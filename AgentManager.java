// see the waiting list
// decision factors: required rate, charged rate, waited time, ...
// pick a waiting car with the best weighted value

package ScheduleSystem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JTextArea;

import ScheduleSystem.Dashboard.UiActionClass;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.wrapper.State;


/*********************************************************
 * 
 * Creator: Brad Yoo 
 * Date: 15/09/2017 
 * Description: In charge of managing Agents
 * 
*********************************************************/
public class AgentManager {

	private static AgentManager Instance = null;

	private AgentController SchedulingAgentCtrl = null;
	private ContainerController MainCtrl = null;
	
	private int NumberOfBays;
	private int BayCapacity;
	private int OutletPerBay;
	private int RunningGenerations;
	private int SiblingsInAGeneration;
	private int MutationRate;
	
	private JTextArea MessageTextArea;	

	private UiActionClass UiActionClass;
	private int TotalElapsedSeconds;
	private int CarNumber;
	
	protected AgentManager()
	{

		
	}
	
	public static AgentManager getInstance()
	{
		if(Instance == null)
			Instance = new AgentManager();
		return Instance;
	}
	
	public AgentManager SetMasterAgent(int numberOfBays, int bayCapacity, int outletPerBay, int runningGenerations, int siblingsInAGeneration, int mutationRate)
	{
		NumberOfBays = numberOfBays;
		BayCapacity = bayCapacity;
		OutletPerBay = outletPerBay;
		RunningGenerations = runningGenerations;
		SiblingsInAGeneration = siblingsInAGeneration;
		MutationRate = mutationRate;
		return this;
	}
	
	public AgentManager SetLogField(JTextArea messageTextArea)
	{
		MessageTextArea = messageTextArea;
		return this;
	}
	
	
	public AgentManager SetUpdateUi(UiActionClass uiActionClass)
	{
		UiActionClass = uiActionClass;
		return this;
	}
	
	public AgentController StartMasterAgent()
	{		
		//Agent things
		Runtime rt = Runtime.instance();
		System.out.println("Launching the platform Main Container...");
		
		Profile pMain = new ProfileImpl(null, 8888, null);
		pMain.setParameter(Profile.GUI, "true");
		if(MainCtrl == null)
			MainCtrl = rt.createMainContainer(pMain);
		
		// 1. Launch Scheduling agent
		System.out.println("Starting up a SchedulingAgent...");
		
		try {
			Object[] agentArgs = { NumberOfBays, BayCapacity, OutletPerBay };
			SchedulingAgentCtrl = MainCtrl.createNewAgent("SchedulingAgent", SchedulingAgent.class.getName(), agentArgs);
			SchedulingAgentCtrl.start();
			
		} catch (StaleProxyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return SchedulingAgentCtrl;	
	}
	
	private ArrayList<AgentController> carList = null;
	
	public ArrayList<AgentController> StartCarAgents(int carNumber)
	{		
		CarNumber = carNumber;
		carList = new ArrayList<AgentController>();
		
		// 2. Launch Car agents
		try
		{
//			int[] requiredLoads = {100,100,100,100,100,100,
//					400,400,400,400,400,
//					500,500,500,500,500,
//					250,250,250,
//					50,50,
//					450,450,
//					25
//			};
//			
//			int[] deadlines = {50,50,150,150,200,200,
//					100,100,200,250,250,
//					100,100,200,300,300,
//					150,150,350,
//					50,100,
//					200,500,
//					250
//			};
			
			if(carList.size() != carNumber)
			{			
				for(int i = 0; i < carNumber; i++)
				{
					int requiredLoad = GetRandomInt(25, 500);
					int startTime = GetRandomInt(0, 200);
					int deadline = startTime + GetRandomInt(50, 300);
					Object[] agentArgs = {i, requiredLoad, startTime, deadline}; 
					
					AgentController carAgentCtrl = null;
					try
					{
						carAgentCtrl = MainCtrl.getAgent("CarAgent"+i);
					}
					catch (ControllerException ex)
					{				
						carAgentCtrl = MainCtrl.createNewAgent("CarAgent"+i, CarAgent.class.getName(), agentArgs);
						carAgentCtrl.start();
					}
					
					carList.add(carAgentCtrl);					
				}
			}				
								
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return carList;		
	}
	
	private void ResetCars() 
	{
		for(AgentController ca: carList)
		{
			CarAgentInterface o2a;
			try {
				o2a = ca.getO2AInterface(CarAgentInterface.class);
				o2a.Reset();	
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<AgentController> GetCarList()
	{
		return carList;
	}
	
	private void Log(String log)
	{
		MessageTextArea.append(log);
	}
	
	private void Log(ArrayList<String> logs)
	{
		for(String log: logs)
		{
			MessageTextArea.append(log);
		}		
	}
	
	public void ResetBays()
	{
		for(Bay bay: GetBays())
		{
			bay.UsedLoad = 0;
			for(Outlet outlet: bay.Outlets)
			{
				outlet.NextTurnNumber = 0;
				outlet.UsedLoad = 0;
			}
		}	
	}
	
	private int GetRandomInt(int start, int end)
	{
		return ThreadLocalRandom.current().nextInt(start, end);
	}
	
	private ArrayList<CarAgentInterface> GetCarsReadyForCharging()
	{
		ArrayList<CarAgentInterface> result = new ArrayList<CarAgentInterface>();
		for(AgentController ac: carList)
		{
			CarAgentInterface car = GetCarInterface(ac);
			if(car.GetStartTime() <= TotalElapsedSeconds && car.GetDeadline() > TotalElapsedSeconds && !car.IsBeingRecharged() && car.GetChargedPercent() < 100)
				result.add(car);
		}
		return result;
	}
	
	public void Run(int speed)
	{
		Genome bestGenome = null;
		int bayCapacity = BayCapacity;	
		
		Float bestRequiredLoadWeight = null;
		Float bestTimeToDeadlineWeight = null;
		Float bestCapacityLeftWeight = null;
		
		
		// generations
		for(int k=0; k < RunningGenerations; k++)
		{
			Log("Gen[" + k + "] starts\n");
			
			ArrayList<Genome> genomes = new ArrayList<Genome>();
			
			// sibilings
			for(int i=0; i < SiblingsInAGeneration; i++)
			{	
				Genome genome = new Genome();
				
				Genome modifiedBestGenome = bestGenome;
				
				// keep original genome in the first genome
//				if(i > 0 && modifiedBestGenome != null)
//				{
//					int numberOfSwap = GetRandomInt(1, 4);
//					for(int ii=0; ii < carList.size() / numberOfSwap; ii++)
//					{
//						// mutation
//						Integer mutationCarId = null;
//						if(ThreadLocalRandom.current().nextInt(0, 99) < MutationRate)
//						{
//							while(true)							
//							{
//								mutationCarId = GetCar(carList.get(ThreadLocalRandom.current().nextInt(0, carList.size()))).Id;
//								if(!modifiedBestGenome.CarIds.contains(mutationCarId))
//									break;
//							}
//						}
//						if(mutationCarId != null)
//							modifiedBestGenome.CarIds.set(ThreadLocalRandom.current().nextInt(0, modifiedBestGenome.CarIds.size()), mutationCarId);							
//						
//						int switchRange = ThreadLocalRandom.current().nextInt(0, 6);
//						int maxSize = modifiedBestGenome.CarIds.size();
//						int randomIndex = ThreadLocalRandom.current().nextInt(0, maxSize);
//						if(randomIndex == 0)
//						{
//							int c1 = modifiedBestGenome.CarIds.get(randomIndex);
//							modifiedBestGenome.CarIds.set(randomIndex, modifiedBestGenome.CarIds.get((randomIndex + switchRange) % maxSize));
//							modifiedBestGenome.CarIds.set((randomIndex + switchRange) % maxSize, c1);
//						}
//						else
//						{
//							int c1 = modifiedBestGenome.CarIds.get(randomIndex);
//							int index = randomIndex - switchRange;
//							if(index < 0)
//								index += maxSize;
//							modifiedBestGenome.CarIds.set(randomIndex, modifiedBestGenome.CarIds.get(index));
//							modifiedBestGenome.CarIds.set(index, c1);
//						}
//					}
//				}
				
//				if(modifiedBestGenome != null)
//				{
//					Log("Switched genome[" + i + "] ");
//					for(int cid : modifiedBestGenome.CarIds)
//					{
//						Log(cid + ", ");
//					}
//					Log("\n");
//				}
//				int genomeCarIndex = 0;
				
				/*** SETUP ***/

				ResetCars();		
				ResetBays();
				UiActionClass.actionPerformed(null);
				
//				ArrayList<AgentController> carListToCharge = (ArrayList<AgentController>) carList.clone();
				ArrayList<String> messages = new ArrayList<String>();
//				for(Bay bay : GetBays())
//				{
//					for(Outlet outlet : bay.Outlets)
//					{							
//						AgentController ca = GetCarAgent(genomeToBeUsed, carListToCharge, genomeCarIndex++);
//						outlet.CarIdUsedBy = GetCar(ca).Id;
//						
//						//genome
//						genome.AddCarId(GetCar(ca).Id);
//					}
//				}				

				float randomRequiredLoadWeight = GetRandomInt(50, 500);
				float randomTimeToDeadlineWeight = GetRandomInt(50, 500);
				
				float requiredLoadWeight = bestGenome != null ? bestGenome.RequiredLoadWeight + (GetRandomInt(0, 10) - 5) : randomRequiredLoadWeight ;
				float timeToDeadlineWeight = bestGenome != null ? bestGenome.TimeToDeadlineWeight + (GetRandomInt(0, 10) - 5) : randomTimeToDeadlineWeight;
				//float capacityLeftWeight = GetRandomInt(1, 99) / 100;
				
				Log("genome[" + i + "]=> RequiredLoadWeight (RLW): " + requiredLoadWeight + ", TimeToDeadlineWeight (TTDW): " + timeToDeadlineWeight + "\n" );
				
				TotalElapsedSeconds = 0;
				//updating
				while(true)
				{
					messages = new ArrayList<String>();
					
					boolean allOutletAvailable = true;
					boolean allBayRunOut = true;
					
					// Get cars currently ready for charging
					ArrayList<CarAgentInterface> carsReadyForCharging = GetCarsReadyForCharging();
					
					carsReadyForCharging.sort(new Comparator<CarAgentInterface>() {
						public int compare(CarAgentInterface ca1, CarAgentInterface ca2)
						{
							return ((ca1.GetCurrentRequiredLoad() - ca2.GetCurrentRequiredLoad()) * requiredLoadWeight +
									(ca1.GetDeadline() - ca2.GetDeadline()) * timeToDeadlineWeight) > 0 ? 1 : -1;
									
						}
					});
					int carsReadyForChargingIndex = 0;
					
					for(Bay bay : GetBays())
					{
						if(bay.UsedLoad < bayCapacity)
						{
							allBayRunOut = false;
							
							for(Outlet outlet : bay.Outlets)
							{
								//release outlet when recharging a car is done
								if(outlet.IsOccupied())
								{
									int carId = outlet.CarIdUsedBy;
									CarAgentInterface ca = GetCarInterface(carId);
									if(ca.GetCurrentRequiredLoad() <= 0 || ca.GetDeadline() <= TotalElapsedSeconds)
									{	
										outlet.Release();
										ca.EndRecharging();
									}
								}
								
								//find another car needing to recharge
								if(!outlet.IsOccupied())
								{
									int carLeft = carsReadyForCharging.size();
									if (carLeft > carsReadyForChargingIndex)
									{										
										CarAgentInterface ca = carsReadyForCharging.get(carsReadyForChargingIndex++);
										
										outlet.CarIdUsedBy = ca.GetId();
										ca.StartRecharging();
										
										//genome
										genome.AddCarId(outlet.CarIdUsedBy);
									}
								}					
								
								//Recharge
								if(outlet.IsOccupied())
								{									
									int carId = outlet.CarIdUsedBy;					
									AgentController carAgent = GetCarAgent(carId);
									CarAgentInterface ca = GetCarInterface(carAgent);
									ca.Recharge();
									outlet.Used();
									bay.Used();
									allOutletAvailable = false;
								}
							}
						}
					}
					
					Log(messages);
					UiActionClass.actionPerformed(null);
					
					//exit when all outlets not used or no car to recharge
					if(TotalElapsedSeconds > 1000 || allBayRunOut)
					{
						//genome
						genome.TotalTimeElapsed = TotalElapsedSeconds;
						Log("Charged percents of gen[" + i + "]: ");
						int sum = 0;
						for(AgentController carAgent : carList)
						{
							CarAgentInterface o2a;
							try {
								o2a = carAgent.getO2AInterface(CarAgentInterface.class);
								Log(o2a.GetChargedPercent() + ", ");
								sum += o2a.GetChargedPercent();
							} catch (StaleProxyException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}								
						}
						int mean = sum / carList.size();
						Log("\nMean: " + mean);
						
						sum = 0;
						for(AgentController carAgent : carList)
						{
							CarAgentInterface o2a;
							try {
								o2a = carAgent.getO2AInterface(CarAgentInterface.class);
								sum += Math.pow(o2a.GetChargedPercent() - mean, 2);
							} catch (StaleProxyException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}								
						}				
						double sqrt = Math.sqrt(sum / carList.size());
						Log(", Standard Deviation(SD): " + sqrt + "\n");
						genome.StandardDeviation = sqrt;
						
						genome.RequiredLoadWeight = requiredLoadWeight;
						genome.TimeToDeadlineWeight = timeToDeadlineWeight;
						
						genomes.add(genome);
						break;
					}			
					
					//Elapse time
					for(AgentController carAgent : carList)
					{
						try {
							CarAgentInterface o2a = carAgent.getO2AInterface(CarAgentInterface.class);
							if(o2a.GetCurrentRequiredLoad() > 0)
								o2a.ElapseSecond();
	
						} catch (StaleProxyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
						
					try {
						Thread.sleep(speed);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					TotalElapsedSeconds++;					
				}				
				//end of siblings	
			}
			
			Log("(TotalTimeElapsed, SD):" );
			for(Genome gn : genomes)
			{
				Log("(" + gn.TotalTimeElapsed + "," + gn.StandardDeviation + "), ");
			}
			Log("\n");
			
			/*** DRAW ***/
			
			genomes.sort(new Comparator<Genome>() {
				public int compare(Genome gs1, Genome gs2)
				{
					return gs1.GetScore() > gs2.GetScore() ? 1 : gs1.GetScore() < gs2.GetScore() ? -1 : 0;
				}
			});
			bestGenome = genomes.get(0);
			Log("Gen["+k+"] best genome (RLW,TTDW)=>("+bestGenome.RequiredLoadWeight + "," + bestGenome.TimeToDeadlineWeight+"): "+ 
					bestGenome.GetScore() +", elased: " + bestGenome.TotalTimeElapsed + ", SD: "+ bestGenome.StandardDeviation +" \n");
			
			genomes.clear();
			
			// end of generation
			Log("\n");
		}
	}
	
	Car GetCar(AgentController carAgent)
	{
		CarAgentInterface o2a;
		try {
			o2a = carAgent.getO2AInterface(CarAgentInterface.class);
			return new Car(o2a.GetId(), o2a.GetInitialRequiredLoad(), o2a.GetCurrentRequiredLoad(), o2a.GetDeadline());
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	CarAgentInterface GetCarInterface(AgentController carAgent)
	{
		try {
			return carAgent.getO2AInterface(CarAgentInterface.class);
			
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	CarAgentInterface GetCarInterface(int carId)
	{
		return GetCarInterface(GetCarAgent(carId));
	}
	
	private void Recharge(AgentController carAgent)
	{
		CarAgentInterface o2a;
		try {
			o2a = carAgent.getO2AInterface(CarAgentInterface.class);
			o2a.Recharge();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Car GetCar(int id)
	{
		for(AgentController carAgent: carList) 
		{
			Car c = GetCar(carAgent);
			if(c.Id == id)
				return c;			
		}
		return null;
	}
	
	private AgentController GetCarAgent(int id)
	{
		for(AgentController carAgent: carList) 
		{
			Car c = GetCar(carAgent);
			if(c.Id == id)
				return carAgent;			
		}
		return null;
	}	
	
	private AgentController GetCarAgent(Genome g, ArrayList<AgentController> carListToCharge, int genomeCarIndex)
	{
		int randomCarIndex = ThreadLocalRandom.current().nextInt(0, carListToCharge.size());		
		int carIndex = g == null ? randomCarIndex : FindIndexByCarId(g.CarIds.size() > genomeCarIndex ? g.CarIds.get(genomeCarIndex) : GetRandomCarIdExceptFor(g.CarIds), carListToCharge);
		AgentController carAgent = carListToCharge.get(carIndex != -1 ? carIndex : randomCarIndex);
		carListToCharge.remove(carIndex != -1 ? carIndex : randomCarIndex);
		return carAgent;
	}
	
	private int FindIndexByCarId(int id, ArrayList<AgentController> agents)
	{
		//System.out.println("agents == null: " + agents == null);
		if(agents == null)
			agents = carList;
		
		for(AgentController ac : carList)
		{
			Car car = GetCar(ac);
			if (car.Id == id)
			{
				return agents.indexOf(ac);
			}
		}
		
		return -1;
	}
	
	private Integer GetRandomCarIdExceptFor(ArrayList<Integer> excludeIds)
	{
		while(true)
		{
			int randomCarId = GetCar(carList.get(ThreadLocalRandom.current().nextInt(0, carList.size()))).Id;
			if(!excludeIds.contains(randomCarId))
				return randomCarId;
		}
	}
	
	public ArrayList<Bay> GetBays()
	{
		ISchedulingAgent schedulingAgent;
		try {
			schedulingAgent = SchedulingAgentCtrl.getO2AInterface(ISchedulingAgent.class);
			return schedulingAgent.GetBays();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public int GetTotalElapsedSeconds()
	{
		return TotalElapsedSeconds;
	}
}

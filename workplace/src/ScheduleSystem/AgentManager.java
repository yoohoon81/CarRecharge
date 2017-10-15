// see the waiting list
// decision factors: required rate, charged rate, waited time, ...
// pick a waiting car with the best weighted value

package ScheduleSystem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JSlider;
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

	private Genome MinGenome;
	private Genome MaxGenome;
	
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
					int startTime = GetRandomInt(0, 500);
					int deadline = startTime + 
							GetRandomInt(100, 500);
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
				outlet.BeingUsed = false;
				outlet.CarIdUsedBy = null;
			}
		}	
	}
	
	public Genome GetMinGenome()
	{
		return MinGenome;
	}
	
	public Genome GetMaxGenome()
	{
		return MaxGenome;
	}
	
	public void SetWeights()
	{
		ArrayList<Genome> genomes = GetGenomes(null, null, 100);
		
		MinGenome = new Genome();
		MaxGenome = new Genome();
		boolean isFirst = true;
		
		for(Genome g: genomes)
		{
			if(isFirst || g.TotalRechargedLoad < MinGenome.TotalRechargedLoad)
				MinGenome.TotalRechargedLoad = g.TotalRechargedLoad;
			if(isFirst || g.TotalRechargedLoad > MaxGenome.TotalRechargedLoad)
				MaxGenome.TotalRechargedLoad = g.TotalRechargedLoad;
			
			if(isFirst || g.TotalTimeElapsed < MinGenome.TotalTimeElapsed)
				MinGenome.TotalTimeElapsed = g.TotalTimeElapsed;
			if(isFirst || g.TotalTimeElapsed > MaxGenome.TotalTimeElapsed)
				MaxGenome.TotalTimeElapsed = g.TotalTimeElapsed;
			
			if(isFirst || g.TotalWaitedSeconds < MinGenome.TotalWaitedSeconds)
				MinGenome.TotalWaitedSeconds = g.TotalWaitedSeconds;
			if(isFirst || g.TotalWaitedSeconds > MaxGenome.TotalWaitedSeconds)
				MaxGenome.TotalWaitedSeconds = g.TotalWaitedSeconds;
			
			if(isFirst || g.StandardDeviationRechargedPercent < MinGenome.StandardDeviationRechargedPercent)
				MinGenome.StandardDeviationRechargedPercent = g.StandardDeviationRechargedPercent;
			if(isFirst || g.StandardDeviationRechargedPercent > MaxGenome.StandardDeviationRechargedPercent)
				MaxGenome.StandardDeviationRechargedPercent = g.StandardDeviationRechargedPercent;
			
			if(isFirst || g.StandardDeviationWaitedSeconds < MinGenome.StandardDeviationWaitedSeconds)
				MinGenome.StandardDeviationWaitedSeconds = g.StandardDeviationWaitedSeconds;
			if(isFirst || g.StandardDeviationWaitedSeconds > MaxGenome.StandardDeviationWaitedSeconds)
				MaxGenome.StandardDeviationWaitedSeconds = g.StandardDeviationWaitedSeconds;
			
			isFirst = false;			
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

	private ArrayList<CarAgentInterface> GetCarsNeedingToRecharge()
	{
		ArrayList<CarAgentInterface> result = new ArrayList<CarAgentInterface>();
		for(AgentController ac: carList)
		{
			CarAgentInterface car = GetCarInterface(ac);
			if(car.GetStatus() != CarStatus.FullyCharged && car.GetStatus() != CarStatus.IncompleteCharged)
				result.add(car);
		}
		return result;
	}
	
	private double Round(double d)
	{
		double a = 100;
		return Math.round(d * 100) / a;
	}
	
	private ArrayList<Genome> GetGenomes(Genome bestGenome, JSlider speedSlider, int siblingsInAGeneration)
	{
		ArrayList<Genome> genomes = new ArrayList<Genome>();
		
		if(speedSlider != null)
			Log("RequiredLoadWeight (RLW), TimeToDeadlineWeight (TTDW), WaitedSecondsWeight (WSW)\n" );
		
		// sibilings
		for(int i=0; i < siblingsInAGeneration; i++)
		{	
			Genome genome = new Genome();
			
			/*** SETUP ***/

			ResetCars();		
			ResetBays();
			UiActionClass.actionPerformed(null);
			
			ArrayList<String> messages = new ArrayList<String>();

			int randomRequiredLoadWeight = GetRandomInt(50, 500);
			int randomTimeToDeadlineWeight = GetRandomInt(50, 500);
			int randomWaitedSecondsWeight = GetRandomInt(50, 500);
			
			int requiredLoadWeight = bestGenome != null ? bestGenome.RequiredLoadWeight + (i == 0 ? 0 : GetRandomInt(0, 500) - 250) : randomRequiredLoadWeight ;
			int timeToDeadlineWeight = bestGenome != null ? bestGenome.TimeToDeadlineWeight + (i == 0 ? 0 : GetRandomInt(0, 500) - 250) : randomTimeToDeadlineWeight;
			int waitedSecondsWeight = bestGenome != null ? bestGenome.WaitedSecondsWeight + (i == 0 ? 0 : GetRandomInt(0, 500) - 250) : randomWaitedSecondsWeight;
						
			if(speedSlider != null)
				Log("genome[" + i + "]=> (RLW,TTDW,WSW): (" + requiredLoadWeight + "," + timeToDeadlineWeight + "," + waitedSecondsWeight + ")\n" );
			
			TotalElapsedSeconds = 0;
			//updating
			while(true)
			{
				messages = new ArrayList<String>();
				
				boolean allBayRunOut = true;
				
				// Get cars currently ready for charging
				ArrayList<CarAgentInterface> carsReadyForCharging = GetCarsReadyForCharging();
				
				carsReadyForCharging.sort(new Comparator<CarAgentInterface>() {
					public int compare(CarAgentInterface ca1, CarAgentInterface ca2)
					{
						return ((ca1.GetCurrentRequiredLoad() - ca2.GetCurrentRequiredLoad()) * requiredLoadWeight +
								(ca1.GetDeadline() - ca2.GetDeadline()) * timeToDeadlineWeight +
								(ca1.GetWaitedSeconds() - ca2.GetWaitedSeconds()) * waitedSecondsWeight
								) > 0 ? 1 : -1;
					}
				});
				int carsReadyForChargingIndex = 0;
				
				for(Bay bay : GetBays())
				{
					if(bay.UsedLoad < BayCapacity)
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
							}
						}
					}
				}
				
				Log(messages);
				UiActionClass.actionPerformed(null);
				
				
				
				//exit when all outlets not used or no car to recharge
				if(TotalElapsedSeconds >= 1000 || allBayRunOut || GetCarsNeedingToRecharge().size() == 0)
				{
					//genome
					genome.TotalTimeElapsed = TotalElapsedSeconds;
					if(speedSlider != null)
						Log("Charged percents of gen[" + i + "]: ");
					int sumChargedPercent = 0;
					int sumRechargedLoad = 0;
					int sumWaitedSeconds = 0;
					for(AgentController carAgent : carList)
					{
						CarAgentInterface o2a;
						try {
							o2a = carAgent.getO2AInterface(CarAgentInterface.class);
							if(speedSlider != null)
								Log(o2a.GetChargedPercent() + ", ");
							sumChargedPercent += o2a.GetChargedPercent();
							sumWaitedSeconds += o2a.GetWaitedSeconds();
							sumRechargedLoad += o2a.GetInitialRequiredLoad() - o2a.GetCurrentRequiredLoad();
						} catch (StaleProxyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}								
					}
					double meanChargedPercent = sumChargedPercent / carList.size();
					double meanWaitedSeconds = sumWaitedSeconds / carList.size();
					
					genome.TotalWaitedSeconds = sumWaitedSeconds;
					
					if(speedSlider != null)
						Log("\n");
					//Log("\nAverage (charged percent, waited Seconds): (" + meanChargedPercent + ", " + meanWaitedSeconds + ")");
					
					sumChargedPercent = 0;
					sumWaitedSeconds = 0;
					for(AgentController carAgent : carList)
					{
						CarAgentInterface o2a;
						try {
							o2a = carAgent.getO2AInterface(CarAgentInterface.class);
							sumChargedPercent += Math.pow(o2a.GetChargedPercent() - meanChargedPercent, 2);
							sumWaitedSeconds += Math.pow(o2a.GetWaitedSeconds() - meanWaitedSeconds, 2);
						} catch (StaleProxyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}								
					}				
					double sqrtRechargedPercent = Math.sqrt(sumChargedPercent / carList.size());
					double sqrtWaitedSeconds = Math.sqrt(sumWaitedSeconds / carList.size());
					//Log(", Standard Deviation(SD): " + sqrtRechargedPercent + "\n");
					genome.StandardDeviationRechargedPercent = Round(sqrtRechargedPercent);
					genome.StandardDeviationWaitedSeconds = Round(sqrtWaitedSeconds);
					genome.TotalRechargedLoad = sumRechargedLoad; 
					genome.RequiredLoadWeight = requiredLoadWeight;				
					genome.TimeToDeadlineWeight = timeToDeadlineWeight;
					genome.WaitedSecondsWeight = waitedSecondsWeight;
					genomes.add(genome);
					break;
				}			
				
				//update car status 
				for(AgentController carAgent : carList)
				{
					try {
						CarAgentInterface o2a = carAgent.getO2AInterface(CarAgentInterface.class);
						CarStatus carStatus = o2a.GetStatus();
						if(o2a.GetStartTime() < TotalElapsedSeconds)
						{
							if(carStatus == CarStatus.Waiting)
								o2a.IncreaseWaitedSeconds();
							
							if(carStatus == CarStatus.Pending)
								o2a.SetStatus(CarStatus.Waiting);
						}	

					} catch (StaleProxyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
				try {
					int v = speedSlider != null ? speedSlider.getValue() : 0;
					Thread.sleep(v * v);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				TotalElapsedSeconds++;					
			}				
			//end of siblings	
		}
		return genomes;
	}
	
	public void Run(JSlider speedSlider)
	{
		Genome bestGenome = null;
				
		// generations
		for(int k=0; k < RunningGenerations; k++)
		{
			Log("\nGen[" + k + "] starts\n");
			
			ArrayList<Genome> genomes = GetGenomes(bestGenome, speedSlider, SiblingsInAGeneration);
			
			Log("(TotalTimeElapsed (TTE), Total recharged load (TRL), Total waited seconds (TWS), Recharged percent SD (RPSD), Waited seconds SD (WSSD), Score):\n" );
			for(Genome gn : genomes)
			{
				Log("(" + gn.TotalTimeElapsed + "," + 
						gn.TotalRechargedLoad + "," + 
						gn.TotalWaitedSeconds + "," + 
						gn.StandardDeviationRechargedPercent + "," + 
						gn.StandardDeviationWaitedSeconds + "," +
						Round(gn.GetScore(MinGenome, MaxGenome)) +
						"), ");
			}
			Log("\n");
			
			/*** DRAW ***/
			
			genomes.sort(new Comparator<Genome>() {
				public int compare(Genome gs1, Genome gs2)
				{
					return gs1.GetScore(MinGenome, MaxGenome) > gs2.GetScore(MinGenome, MaxGenome) ? -1 : gs1.GetScore(MinGenome, MaxGenome) < gs2.GetScore(MinGenome, MaxGenome) ? 1 : 0;
				}
			});
			
			bestGenome = genomes.get(0);
			Log("Gen["+k+"] best genome (RLW,TTDW,WSW)=>("+
					bestGenome.RequiredLoadWeight + "," + 
					bestGenome.TimeToDeadlineWeight + "," +
					bestGenome.WaitedSecondsWeight +					
					"): "+ 
					bestGenome.GetScore(MinGenome, MaxGenome) +"\n");
			Log("(TTE, TRL, RPSD, TWS, WSSD) => (" + bestGenome.TotalTimeElapsed + "," + bestGenome.TotalRechargedLoad + "," + bestGenome.StandardDeviationRechargedPercent +
					"," + bestGenome.TotalWaitedSeconds + "," + bestGenome.StandardDeviationWaitedSeconds + ")");
			
			genomes.clear();
			
			// end of generation
			Log("\n");
		}
	}
//	
//	private void PrintGenomeInfo(ArrayList<Genome> g, int genIndex)
//	{
//		
//	}
	
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

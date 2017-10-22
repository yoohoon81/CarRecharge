package ScheduleSystem;

import java.util.ArrayList;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.*;


@SuppressWarnings("serial")
public class SchedulingAgent extends Agent implements ISchedulingAgent {
	
	ArrayList<Bay> bays;

	public SchedulingAgent()
	{
		registerO2AInterface(ISchedulingAgent.class, this);		
	}
	protected void setup() {
		System.out.println(getLocalName() + ": I have been created");
		
		Object[] args = getArguments();
		int numberOfBays = Integer.parseInt(args[0].toString());
		int bayCapacity = Integer.parseInt(args[1].toString());
		int outletPerBay = Integer.parseInt(args[2].toString());
		
		bays = new ArrayList<Bay>();
		for(int i = 0; i < numberOfBays; i++) { bays.add(new Bay(i, bayCapacity, outletPerBay)); }
		
		CyclicBehaviour cb = new CyclicBehaviour(this) {
			public void action() {
				try {
					ACLMessage msg = receive();
					
					if (msg != null) {																							
						if(msg.getPerformative() == 16)																	
						{																																											
							ACLMessage fwd = msg.createReply();														
							fwd.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
							System.out.println(msg.getContent());	
							
							send(fwd);																																																																		

						}
					}
					block();

					
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		addBehaviour(cb);
	}

	@Override
	public ArrayList<Bay> GetBays() {
		return bays;
	}
}
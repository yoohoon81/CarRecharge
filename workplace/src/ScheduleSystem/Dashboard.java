package ScheduleSystem;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import sun.rmi.runtime.Log;

import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.Dimension;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.JSlider;
import javax.swing.BoxLayout;
import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

/*********************************************************
 * 
 * Creator: Brad Yoo 
 * Date: 15/09/2017 
 * Description: Managing UI
 * 
*********************************************************/
public class Dashboard extends JFrame {
	private JTable carTable;
	private JTextField carNumberTextField;
	private JTextField numberOfBaysTextField;
	private JTable stationTable;
	private JTextField bayCapacityTextField;
	private JTextField outletPerBayTextField;
	private JButton runButton;
	private JTextArea messageTextArea;
	private JTextField speedTextField;
	private JPanel statusPanel;
	private JButton generateButton;
	private JPanel panelNorth;
	private AgentManager agentManager;
	private JTextField runningGenerationsTextField;
	private JTextField siblingsInAGenerationTextField;
	private JTextField mutationRateTextField;
	private JLabel elapsedLabel;
	private JSlider speedSlider;
	private JTextField StandardDeviationRechargedPercentRange;
	private JTextField TotalWaitedSecondsRange;
	private JTextField TotalRechargedLoadRange;
	private JTextField TotalTimeElapsedRange;
	private JTextField StandardDeviationWaitedSecondsRange;
	private JTextField TotalRechargedLoad;
	private JTextField TotalTimeElapsed;
	private JTextField TotalWaitedSeconds;
	private JTextField StandardDeviationRechargedPercent;
	private JTextField StandardDeviationWaitedSeconds;
	private JButton resetButton;
	/**
	 * Create the frame.
	 */
	public static void main(String[] args) throws StaleProxyException, InterruptedException {
		
		try {
			Dashboard frame = new Dashboard();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void InitUi()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1168, 779);
		getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 1152, 961);
		getContentPane().add(tabbedPane);
		
		JPanel settingsPanel = new JPanel();
		tabbedPane.addTab("Settings", null, settingsPanel, null);
		tabbedPane.setEnabledAt(0, true);
		
		JPanel panel_1 = new JPanel();
		
		JLabel lblNewLabel_1 = new JLabel("Number of Bays");
		lblNewLabel_1.setBounds(7, 7, 92, 20);
		
		JLabel lblNewLabel_3 = new JLabel("Outlet per Bay");
		lblNewLabel_3.setBounds(7, 37, 80, 15);
		
		outletPerBayTextField = new JTextField();
		outletPerBayTextField.setBounds(116, 34, 64, 21);
		outletPerBayTextField.setText("4");
		outletPerBayTextField.setHorizontalAlignment(SwingConstants.LEFT);
		outletPerBayTextField.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Bay Capacity KW");
		lblNewLabel_2.setBounds(7, 62, 98, 15);
		
		bayCapacityTextField = new JTextField();
		bayCapacityTextField.setBounds(116, 59, 65, 21);
		bayCapacityTextField.setText("10000");
		bayCapacityTextField.setColumns(10);
		
		numberOfBaysTextField = new JTextField();
		numberOfBaysTextField.setBounds(116, 7, 64, 21);
		numberOfBaysTextField.setText("2");
		numberOfBaysTextField.setColumns(10);
		settingsPanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblNewLabel = new JLabel("Number of Cars");
		panel.add(lblNewLabel);
		
		carNumberTextField = new JTextField();
		panel.add(carNumberTextField);
		carNumberTextField.setText("24");
		carNumberTextField.setColumns(10);
		settingsPanel.add(panel);
		settingsPanel.add(panel_1);
		panel_1.setLayout(null);
		panel_1.add(lblNewLabel_1);
		panel_1.add(lblNewLabel_3);
		panel_1.add(outletPerBayTextField);
		panel_1.add(lblNewLabel_2);
		panel_1.add(bayCapacityTextField);
		panel_1.add(numberOfBaysTextField);
		
		JPanel panel_5 = new JPanel();
		settingsPanel.add(panel_5);
		panel_5.setLayout(null);
		
		JLabel lblNewLabel_4 = new JLabel("One simulating second is equal to                  ms");
		lblNewLabel_4.setBounds(12, 8, 307, 15);
		panel_5.add(lblNewLabel_4);
		
		speedTextField = new JTextField();
		speedTextField.setText("1");
		speedTextField.setBounds(215, 5, 44, 21);
		panel_5.add(speedTextField);
		speedTextField.setColumns(10);
		
		JLabel lblNewLabel_5 = new JLabel("Running generations");
		lblNewLabel_5.setBounds(12, 33, 124, 15);
		panel_5.add(lblNewLabel_5);
		
		runningGenerationsTextField = new JTextField();
		runningGenerationsTextField.setText("50");
		runningGenerationsTextField.setBounds(169, 30, 61, 21);
		panel_5.add(runningGenerationsTextField);
		runningGenerationsTextField.setColumns(10);
		
		JLabel lblNewLabel_6 = new JLabel("Siblings in a generation");
		lblNewLabel_6.setBounds(12, 58, 138, 15);
		panel_5.add(lblNewLabel_6);
		
		siblingsInAGenerationTextField = new JTextField();
		siblingsInAGenerationTextField.setText("10");
		siblingsInAGenerationTextField.setBounds(169, 55, 61, 21);
		panel_5.add(siblingsInAGenerationTextField);
		siblingsInAGenerationTextField.setColumns(10);
		
		JLabel lblNewLabel_7 = new JLabel("Mutation rate (%)");
		lblNewLabel_7.setBounds(12, 83, 124, 15);
		panel_5.add(lblNewLabel_7);
		
		mutationRateTextField = new JTextField();
		mutationRateTextField.setText("10");
		mutationRateTextField.setBounds(169, 80, 61, 21);
		panel_5.add(mutationRateTextField);
		mutationRateTextField.setColumns(10);
		
		statusPanel = new JPanel(new BorderLayout());
		tabbedPane.addTab("Status", null, statusPanel, null);
		tabbedPane.setEnabledAt(1, true);
		panelNorth = new JPanel();
		statusPanel.add(panelNorth, BorderLayout.NORTH);
		
		generateButton = new JButton("Generate");
		panelNorth.add(generateButton);
		
		runButton = new JButton("Run");
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Run());
				t.start();
			}
		});
		runButton.setEnabled(false);
		panelNorth.add(runButton);
		
		speedSlider = new JSlider();
		speedSlider.setValue(20);
		speedSlider.setMaximum(40);
		speedSlider.setMinimum(0);
		panelNorth.add(speedSlider);
		
		JLabel lblElapsed = new JLabel("Elapsed:");
		panelNorth.add(lblElapsed);
		
		elapsedLabel = new JLabel("0");
		panelNorth.add(elapsedLabel);
		
		generateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)  {

				Generate();				
			}
		});
		
		JPanel panelCentre = new JPanel();
		panelCentre.setSize(new Dimension(0, 900));
		panelCentre.setPreferredSize(new Dimension(10, 800));
		statusPanel.add(panelCentre, BorderLayout.CENTER);
		panelCentre.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		panelCentre.add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		carTable = new JTable();
		JScrollPane scrollPane = new JScrollPane(carTable);
		scrollPane.setMinimumSize(new Dimension(23, 15));
		panel_2.add(scrollPane);
		
		carTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Id", "Start time", "Deadline", "Required KW", "Load to recharge", "Charged %", "Status"
			}
		) {
			Class[] columnTypes = new Class[] {
				Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, CarStatus.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			};
			
			boolean[] canEdit = new boolean[] {false, true, true, true, false, false, false, false, false, false};
			public boolean isCellEditable(int rowIndex, int columnIndex) 
			{				
				return canEdit[columnIndex];
			}
		});
		
		carTable.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				//System.out.println("lost");
			}
			
			@Override
			public void focusGained(FocusEvent e) {

				DefaultTableModel model = (DefaultTableModel) carTable.getModel();

				ArrayList<AgentController> carList = agentManager.GetCarList();
				for(int i = 0; i < model.getRowCount(); i++)
				{
					CarAgentInterface ca = agentManager.GetCarInterface(carList.get(i));
					
					ca.Set(Integer.parseInt(model.getValueAt(i, 1).toString()), 
							Integer.parseInt(model.getValueAt(i, 2).toString()),
							Integer.parseInt(model.getValueAt(i, 3).toString())
						);
				}
			}
		});
		//.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

//			@Override
//			public void valueChanged(ListSelectionEvent e) {
//				System.out.println("fff");
//				
//			}
//			
//		});
		
		JPanel panel_3 = new JPanel();
		panelCentre.add(panel_3, BorderLayout.CENTER);
		
		stationTable = new JTable();
		stationTable.setPreferredScrollableViewportSize(new Dimension(450, 200));
		JScrollPane scrollPane_1 = new JScrollPane(stationTable);
		panel_3.add(scrollPane_1);
		
		stationTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Bay number", "Outlet number", "Charged KW", "Occupied by"
			}
		) {
			Class[] columnTypes = new Class[] {
				Integer.class, Integer.class, Integer.class, Integer.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		
		/* South begin */
		
		JPanel panelEast = new JPanel();
		statusPanel.add(panelEast, BorderLayout.EAST);
		panelEast.setLayout(new BorderLayout(1, 1));
		
		JPanel panel_6 = new JPanel();
		panelEast.add(panel_6, BorderLayout.NORTH);
		panel_6.setLayout(new GridLayout(6, 3, 3, 3));
		
		JLabel lblNewLabel_9 = new JLabel("");
		panel_6.add(lblNewLabel_9);
		
		JLabel label = new JLabel("");
		panel_6.add(label);
		
		resetButton = new JButton("Find values");
		resetButton.setEnabled(false);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				agentManager.SetWeights();
				UpdateUi(null);	
				runButton.setEnabled(true);
			}
		});
		panel_6.add(resetButton);
		
		JLabel lblTotaltimeelapsed = new JLabel("TotalTimeElapsed");
		lblTotaltimeelapsed.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_6.add(lblTotaltimeelapsed);
		
		TotalTimeElapsedRange = new JTextField();
		TotalTimeElapsedRange.setEditable(false);
		panel_6.add(TotalTimeElapsedRange);
		TotalTimeElapsedRange.setColumns(10);
		
		TotalTimeElapsed = new JTextField();
		TotalTimeElapsed.setColumns(10);
		panel_6.add(TotalTimeElapsed);
		
		JLabel TotalRechargedLoadLabel = new JLabel("TotalRechargedLoad");
		TotalRechargedLoadLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_6.add(TotalRechargedLoadLabel);
		
		TotalRechargedLoadRange = new JTextField();
		TotalRechargedLoadRange.setEditable(false);
		TotalRechargedLoadLabel.setLabelFor(TotalRechargedLoadRange);
		panel_6.add(TotalRechargedLoadRange);
		TotalRechargedLoadRange.setColumns(10);
		
		TotalRechargedLoad = new JTextField();
		TotalRechargedLoad.setColumns(10);
		panel_6.add(TotalRechargedLoad);
		
		
		JLabel lblNewLabel_10 = new JLabel("TotalWaitedSeconds");
		lblNewLabel_10.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_6.add(lblNewLabel_10);
		
		TotalWaitedSecondsRange = new JTextField();
		TotalWaitedSecondsRange.setEditable(false);
		panel_6.add(TotalWaitedSecondsRange);
		TotalWaitedSecondsRange.setColumns(10);
		
		TotalWaitedSeconds = new JTextField();
		TotalWaitedSeconds.setColumns(10);
		panel_6.add(TotalWaitedSeconds);
		
		JLabel lblNewLabel_8 = new JLabel("RechargedPercent SD");
		lblNewLabel_8.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_6.add(lblNewLabel_8);
		
		StandardDeviationRechargedPercentRange = new JTextField();
		StandardDeviationRechargedPercentRange.setEditable(false);
		panel_6.add(StandardDeviationRechargedPercentRange);
		StandardDeviationRechargedPercentRange.setColumns(1);
		
		StandardDeviationRechargedPercent = new JTextField();
		StandardDeviationRechargedPercent.setColumns(1);
		panel_6.add(StandardDeviationRechargedPercent);
		
		JLabel lblWaitedsecondsSd = new JLabel("WaitedSeconds SD");
		lblWaitedsecondsSd.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_6.add(lblWaitedsecondsSd);
		
		StandardDeviationWaitedSecondsRange = new JTextField();
		StandardDeviationWaitedSecondsRange.setEditable(false);
		StandardDeviationWaitedSecondsRange.setColumns(1);
		panel_6.add(StandardDeviationWaitedSecondsRange);
		
		StandardDeviationWaitedSeconds = new JTextField();
		StandardDeviationWaitedSeconds.setColumns(1);
		panel_6.add(StandardDeviationWaitedSeconds);
		
		JPanel panel_4 = new JPanel();
		panelEast.add(panel_4);
		panel_4.setPreferredSize(new Dimension(650, 480));
		panel_4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		
		messageTextArea = new JTextArea(30, 75);
		messageTextArea.setEditable(false);
		JScrollPane scrollPane_2 = new JScrollPane(messageTextArea);
		scrollPane_2.setPreferredSize(new Dimension(650, 500));
		scrollPane_2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel_4.add(scrollPane_2);
		
		statusPanel.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{panelNorth, panelCentre, panelEast}));
	}
	
	public Dashboard() {
		
		InitUi();		
		
		agentManager = AgentManager.getInstance();
		
		agentManager.SetMasterAgent(Integer.parseInt(numberOfBaysTextField.getText()), 
				Integer.parseInt(bayCapacityTextField.getText()), 
				Integer.parseInt(outletPerBayTextField.getText()),
				Integer.parseInt(runningGenerationsTextField.getText()),
				Integer.parseInt(siblingsInAGenerationTextField.getText()),
				Integer.parseInt(mutationRateTextField.getText())
				)
			.SetLogField(messageTextArea)
			.SetUpdateUi(new UiActionClass())
			.StartMasterAgent();
	}
	
	public void Generate()
	{
		agentManager.StartCarAgents(Integer.parseInt(carNumberTextField.getText()));		
		agentManager.ResetBays();
		//agentManager.SetWeights();
		UpdateUi(null);		
		
		resetButton.setEnabled(true);
		
	}
		
	private class Run implements Runnable
	{
		public void run() {
			agentManager.Run(speedSlider);
		}
	}
	
	public class UiActionClass implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			UpdateUi(null);			
		}
	} 
	
	public void UpdateUi(ArrayList<String> messages)
	{		
		if(messages != null)
		{
			for(String message : messages)
				messageTextArea.append(message);							
		}
		
		DefaultTableModel model = (DefaultTableModel) carTable.getModel();

		ArrayList<AgentController> carList = agentManager.GetCarList();
		for(int i = 0; i < carList.size(); i++)
		{
			AgentController car = carList.get(i);
			try 
			{
				CarAgentInterface o2a = car.getO2AInterface(CarAgentInterface.class);
				if(model.getRowCount() < carList.size())
					model.addRow(new Object[] { 
							o2a.GetId(), 
							o2a.GetStartTime(), 
							o2a.GetDeadline(), 
							o2a.GetInitialRequiredLoad(), 
							o2a.GetCurrentRequiredLoad(), 
							o2a.GetChargedPercent(),
							o2a.GetStatus()
					});
				else
				{
					model.setValueAt(o2a.GetId(), i, 0);
					model.setValueAt(o2a.GetStartTime(), i, 1);
					model.setValueAt(o2a.GetDeadline(), i, 2);
					model.setValueAt(o2a.GetInitialRequiredLoad(), i, 3);
					model.setValueAt(o2a.GetCurrentRequiredLoad(), i, 4);
					model.setValueAt(o2a.GetChargedPercent(), i, 5);
					model.setValueAt(o2a.GetStatus(), i, 6);
				}
			} 
			catch (Exception ex)
			{
				ex.printStackTrace();				
			}
		}
		
		DefaultTableModel stationModel = (DefaultTableModel) stationTable.getModel();
		ArrayList<Bay> bays = agentManager.GetBays();
	
		for(int i = 0; i < bays.size(); i++)
		{
			Bay bay = bays.get(i);
			try 
			{
				ArrayList<Outlet> outlets = bay.Outlets;
				for(int k = 0; k < outlets.size(); k++)
				{
					if(stationModel.getRowCount() < outlets.size() * bays.size())
						stationModel.addRow(new Object[] { bay.Id, outlets.get(k).Id, outlets.get(k).UsedLoad, outlets.get(k).CarIdUsedBy });
					else
					{
						stationModel.setValueAt(bay.Id, 					outlets.size() * i + k, 0);
						stationModel.setValueAt(outlets.get(k).Id, 			outlets.size() * i + k, 1);
						stationModel.setValueAt(outlets.get(k).UsedLoad, 	outlets.size() * i + k, 2);
						stationModel.setValueAt(outlets.get(k).CarIdUsedBy, outlets.size() * i + k, 3);
					}
				}				
			} 
			catch (Exception ex)
			{
				ex.printStackTrace();				
			}
		}
		
		elapsedLabel.setText(Integer.toString(agentManager.GetTotalElapsedSeconds()));
		
		//runButton.setEnabled(carList.size() > 0 && bays.size() > 0);
		
		//show ranges from random run & recommend average values
		Genome min = agentManager.GetMinGenome();
		Genome max = agentManager.GetMaxGenome();

		if(min != null && max != null)
		{
			TotalRechargedLoadRange.setText(min.TotalRechargedLoad + "~" + max.TotalRechargedLoad);
			TotalRechargedLoad.setText(Integer.toString((min.TotalRechargedLoad + max.TotalRechargedLoad) / 2));
			TotalTimeElapsedRange.setText(min.TotalTimeElapsed + "~" + max.TotalTimeElapsed);
			TotalTimeElapsed.setText(Integer.toString((min.TotalTimeElapsed + max.TotalTimeElapsed) / 2));
			TotalWaitedSecondsRange.setText(min.TotalWaitedSeconds + "~" + max.TotalWaitedSeconds);
			TotalWaitedSeconds.setText(Integer.toString((min.TotalWaitedSeconds + max.TotalWaitedSeconds) / 2));
			StandardDeviationRechargedPercentRange.setText(min.StandardDeviationRechargedPercent + "~" + max.StandardDeviationRechargedPercent);
			StandardDeviationRechargedPercent.setText(Double.toString((min.StandardDeviationRechargedPercent + max.StandardDeviationRechargedPercent) / 2));
			StandardDeviationWaitedSecondsRange.setText(min.StandardDeviationWaitedSeconds + "~" + max.StandardDeviationWaitedSeconds);
			StandardDeviationWaitedSeconds.setText(Double.toString((min.StandardDeviationWaitedSeconds + max.StandardDeviationWaitedSeconds) / 2));
		}
	}
}
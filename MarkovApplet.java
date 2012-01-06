/* Import necessary JDK packages. */
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

/* Import JRefinery packages used to display plots. */
import com.jrefinery.data.*;
import com.jrefinery.chart.*;

/* Include the basic HTML code needed by appletviewer in order to
   facilitate testing of the applet with the appletviewer utility. */

/*
<applet code="MarkovApplet" archive="jcommon-0.5.2.jar, jfreechart-0.7.0.jar" 
"width=750 height=550></applet>
*/

/* This applet provides a simple Markov queue simulation whose parameters
   can be easily adjusted. */
public class MarkovApplet extends JApplet implements Runnable, ActionListener {
    
    MarkovQueue queue;               // the simulation queue
    QueueStateDataset stateEvolData; // dataset used to plot state evolution
    HistDataset stateFreqData;
    HistDataset arrivalProbData;     // used to plot arrival probabilites
    RealHistDataset serviceProbData; // used to plot service probabilites
    
    Thread thread = null;            // the thread that runs the simulation
    protected boolean done = true;   // a flag used to by the thread

    ParamDialog paramDialog; // the simulation parameter setting dialog box

    /* Simulation parameters. */
    protected int serverValue = 1;
    protected double tickValue = 0.1;
    protected double lambdaValue = 1;
    protected double muValue = 1;

    /* Simulation control buttons. */
    protected JButton startButton, stopButton, resetButton, settingsButton;

    /* Simulation status fields. */
    protected JLabel currentState, currentTime, 
	currentArrivals, currentDepartures;

    /* Applet initialization method. */
    public void init() {

	/* Set up a panel containing the simulation parameters and current
	   status. */
	JPanel statusPanel = new JPanel();
	statusPanel.setLayout(new GridLayout(2, 4, 1, 1));

	currentState = new JLabel("0");
	currentTime = new JLabel("0");
	currentArrivals = new JLabel("0");
	currentDepartures = new JLabel("0");

	statusPanel.add(new JLabel("Queue state: "));
	statusPanel.add(currentState);
	statusPanel.add(new JLabel("Last arrivals: "));
	statusPanel.add(currentArrivals);
	statusPanel.add(new JLabel("Time: "));
	statusPanel.add(currentTime);
	statusPanel.add(new JLabel("Last departures: "));
	statusPanel.add(currentDepartures);

	/* Set up a panel containing the control buttons and the simulation
	   status panel created above. */
	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

	startButton = new JButton("Start");
	stopButton = new JButton("Stop"); 
	stopButton.setEnabled(false);
	resetButton = new JButton("Reset");
	settingsButton = new JButton("Settings");

	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
	buttonPanel.add(startButton);
	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
	buttonPanel.add(stopButton);
	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
	buttonPanel.add(resetButton);
	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
	buttonPanel.add(settingsButton);
	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
	buttonPanel.add(statusPanel);
	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

	/* Set up two panels containing the chart components. Note that the
	   state dataset object is persistent even though the queue 
	   simulation object is not. */
	JPanel chartPanel1 = new JPanel();
	chartPanel1.setLayout(new BoxLayout(chartPanel1, BoxLayout.X_AXIS));

	stateFreqData = new HistDataset(15);
	JFreeChart stateFreqChart =
	    ChartFactory.createVerticalBarChart("Number of Visits per State",
						"State", "Number of visits",
						stateFreqData, false);
	JFreeChartPanel stateFreqChartPanel =
	    new JFreeChartPanel(stateFreqChart, 300, 300, false, 300, 300);


	stateEvolData = new QueueStateDataset();
	JFreeChart stateEvolChart = 
	    ChartFactory.createXYChart("Queue State Evolution", "Time", 
				       "State", stateEvolData, false);
	JFreeChartPanel stateEvolChartPanel = 
	    new JFreeChartPanel(stateEvolChart, 300, 300, false, 300, 300);

	chartPanel1.add(stateFreqChartPanel);
	chartPanel1.add(stateEvolChartPanel);
	chartPanel1.add(Box.createRigidArea(new Dimension(15, 0)));

	JPanel chartPanel2 = new JPanel();
	chartPanel2.setLayout(new BoxLayout(chartPanel2, BoxLayout.X_AXIS));

	arrivalProbData = new HistDataset(15);
	JFreeChart arrivalProbChart =
	    ChartFactory.createVerticalBarChart("Arrival Distribution",
						"Arrivals per interval",
						"Probability", 
						arrivalProbData, false);
	JFreeChartPanel arrivalProbChartPanel = 
	    new JFreeChartPanel(arrivalProbChart, 300, 150, false, 300, 150);

	serviceProbData = new RealHistDataset(15);
	JFreeChart serviceProbChart = 
	    ChartFactory.createVerticalBarChart("Service Length Distribution",
						"Service length",
						"Probability",
						serviceProbData, false);
	JFreeChartPanel serviceProbChartPanel = 
	    new JFreeChartPanel(serviceProbChart, 300, 150, false, 300, 150);

	chartPanel2.add(arrivalProbChartPanel);
	chartPanel2.add(serviceProbChartPanel);
	chartPanel2.add(Box.createRigidArea(new Dimension(15, 0)));

	/* Use the applet's action listener to process events. */
	startButton.addActionListener(this);
	stopButton.addActionListener(this);
	resetButton.addActionListener(this);	
	settingsButton.addActionListener(this);

	/* Add the various panels created above to the applet. */
	Container cp = getContentPane();
	cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

	cp.add(chartPanel1);
	cp.add(chartPanel2);
	cp.add(buttonPanel);    
    }

    /* Start the applet. */
    public void start() {
	if (thread != null) {
	    thread.resume();

	    startButton.setEnabled(false);
	    stopButton.setEnabled(true);

	    System.out.println("Simulation resumed");
	} 
    }

    /* Stop (i.e., pause) the applet. */
    public void stop() {
	if (thread != null) {
	    thread.suspend();

	    startButton.setEnabled(true);
	    stopButton.setEnabled(false);

	    System.out.println("Simulation suspended");
	} 
    }

    /* Reset the simulation. */
    public void reset() {
	done = true;

	stateFreqData.clear();
	arrivalProbData.clear();
	serviceProbData.clear();
	stateEvolData.clear();

	updateStatusFields(0, 0, 0, 0);

	startButton.setEnabled(true);
	stopButton.setEnabled(false);
	settingsButton.setEnabled(true);

	System.out.println("Simulation reset");
    }

    /* Update the status fields. */
    public void updateStatusFields(double t, int s, int a, int d) {
	String str = String.valueOf(t);
	
	if (str != null) {
	    if (str.length() > 4) {
		str = str.substring(0, 4);
	    }
	} else {
	    str = new String("0");
	}	 
	currentTime.setText(str);
	currentState.setText(String.valueOf(s));
	currentArrivals.setText(String.valueOf(a));
	currentDepartures.setText(String.valueOf(d));
    }

    /* Change the simulation parameter settings by opening a parameter
       change dialog box. */
    public void changeParameters() {
	ParamDialog dialog = new ParamDialog(null,
           serverValue, tickValue, lambdaValue, muValue);
	dialog.pack(); 
	dialog.show();	    
	
	/* When the 'ok' button in the dialog box is pressed,
	   the box hides itself and the collected values are 
	   subsequently extracted. */
	serverValue = dialog.getServerValue();
	tickValue = dialog.getTickValue();
	lambdaValue = dialog.getLambdaValue();
	muValue = dialog.getMuValue();

	/* Display parameter changes on the console for debugging purposes. */
	System.out.println("Parameters updated: servers = " + serverValue + 
			   ", tick = " + tickValue + 
			   ", lambda = " + lambdaValue + 
			   ", mu = " + muValue);
    }

    /* This is the body of the thread that runs the queue simulation. */
    public void run() {

	/* Run the simulation until the done flag is set. */
	while(!done) {

	    /* Advance forward by one time interval. */
	    queue.simulateStep();	    

	    /* Get queue status data. */
	    double t = queue.getTime();
	    int s = queue.getState();
	    int a = queue.getLastArrivals();
	    int d = queue.getLastDepartures();

	    /* Update chart datasets. */
	    stateEvolData.add(t, s);
	    updateStatusFields(t, s, a, d);
 
	    Double currVal = (Double) (stateFreqData.getValue(0, 
							      new Integer(s)));
	    stateFreqData.add(s, currVal.doubleValue() + 1);
	    arrivalProbData.add(a, probPoisson(lambdaValue, tickValue, a));

	    Vector v = queue.getLastServiceLengths();
	    int i;

	    for (i = 0; i < v.size(); i++) {
		Double dd = (Double) v.get(i);
		serviceProbData.add(dd.doubleValue(), probExponential(muValue, 
						       dd.doubleValue()));
	    }

	    /* Pause the thread a moment before running the loop again. */
	    try {	
		Thread.sleep(200);
	    } catch (InterruptedException e) {
		System.out.println("Queue thread interrupted");
	    }
	}
    }

    /* Handle action events generated by applet controls. */
    public void actionPerformed(ActionEvent e) {

	/* Retrieve the command associated with the action. */
	String str = e.getActionCommand();

	/* Start the simulation. */
	if (str.equals("Start")) {	  

	    /* If no queue is running, create a new queue with the
	       default parameters, create a new thread to run the
	       simulation, and start chugging away! */	
	    if (done) {

		/* Disable the 'start' button, enable the 'stop' button, 
		   and disable the 'settings' button
		   when the queue simulation is started. */		
		startButton.setEnabled(false);
		stopButton.setEnabled(true);
		settingsButton.setEnabled(false);

		done = false;
		queue = new MarkovQueue(serverValue, tickValue, 
					lambdaValue, muValue);
		thread = new Thread(this);
		thread.start();

		System.out.println("Simulation started");
	    } 

	    /* If there already is a simulation thread in memory, just 
	       continue running it. */
	    start();
	} 

	/* Pause the simulation. */
	else if (str.equals("Stop")) {
	    stop();
	} 

	/* Reset the simulation. */
	else if (str.equals("Reset")) {
	    reset();
	} 

	/* Change the simulation parameters. */
	else if (str.equals("Settings")) {       
	    changeParameters();
	}
    }
    
    /* Clean up before exiting. */
    public void destroy() {

	/* Stop and discard the thread. */
	if (thread != null) {
	    thread.stop();
	    thread = null;
	}
    }

    /* Calculate the probability that a Poisson random value
       with the specified parameters assumes the specified value. */
    public double probPoisson(double l, double t, int k) {
	return Math.exp(-l*t)*Math.pow(l*t, k)/factorial(k);
    }

    /* Calculate the probability that an exponential random value
       with the specified parameters assumes a value that 
       is less than the specified value. */
    public double probExponential(double l, double x) {
	return 1 - Math.exp(-l*x);
    }

    /* Calculate the factorial of the specified number. */
    private int factorial(int k) {
	int f = k;
	int temp = f;

	if (f == 0) { f = 1; }

	while (temp > 2) {
	    temp--;
	    f *= temp;
	} 

	return f;
    }
}

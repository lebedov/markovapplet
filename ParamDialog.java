import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

/* This dialog box class is used to obtain the parameters used by the
   queue simulation. */
public class ParamDialog extends JDialog {

    /* Set up the default field values. */
    protected int serverValue, finiteServerValue;
    protected double tickValue;
    protected double lambdaValue;
    protected double muValue;

    /* Various parameter entry fields. */
    protected JRadioButton yesInfServerButton, noInfServerButton;
    protected IntOnlyTextField serverField;
    protected RealOnlyTextField tickField;
    protected RealOnlyTextField lambdaField;
    protected RealOnlyTextField muField;

    /* These methods just retrieve the various field values. */
    public int getServerValue() { return serverValue; }
    public double getTickValue() { return tickValue; }
    public double getLambdaValue() { return lambdaValue; }
    public double getMuValue() { return muValue; }
    
    public ParamDialog(Frame owner, int s, double t, double l, double m) {
	
	/* Call parent constructor. */
	super(owner, "Enter Simulation Parameters", true);

	/* Get the parent dialog box pane. */
	Container cp = getContentPane();

	/* Store the existing parameters specified. */
	serverValue = s;
	tickValue = t;
	lambdaValue = l;
	muValue = m;

	/* Set up the field entry panel. */
	JPanel entryPanel = new JPanel(new GridLayout(5, 2));
       
	serverField = new IntOnlyTextField(String.valueOf(serverValue), 4);
	tickField = new RealOnlyTextField(String.valueOf(tickValue), 4);
	lambdaField = new RealOnlyTextField(String.valueOf(lambdaValue), 4);
	muField = new RealOnlyTextField(String.valueOf(muValue), 4);

	yesInfServerButton = new JRadioButton("M/M/Infinity");
	noInfServerButton = new JRadioButton("M/M/s");

	/* Bind the radio buttons together. */
	ButtonGroup group = new ButtonGroup();
	group.add(yesInfServerButton);
	group.add(noInfServerButton);

	entryPanel.add(yesInfServerButton);
	entryPanel.add(noInfServerButton);
	entryPanel.add(new JLabel("Number of servers:")); 
	entryPanel.add(serverField);
	entryPanel.add(new JLabel("Time increment")); 
	entryPanel.add(tickField);
	entryPanel.add(new JLabel("Arrival rate:")); 
	entryPanel.add(lambdaField);
	entryPanel.add(new JLabel("Service rate:")); 
	entryPanel.add(muField);

	/* Toggle the radio buttons and server number specification field
	   depending on the server value. */
	if (s >= 0) {
	    noInfServerButton.setSelected(true);
	} else {
	    yesInfServerButton.setSelected(true);
	    serverField.setEnabled(false);
	}

	/* Construct the button panel. */
	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

	JButton okButton = new JButton("OK");
	JButton resetButton = new JButton("Reset");
	JButton cancelButton = new JButton("Cancel");

	buttonPanel.add(Box.createHorizontalGlue());
	buttonPanel.add(okButton);
	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
	buttonPanel.add(resetButton);
	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
	buttonPanel.add(cancelButton);
	
	getRootPane().setDefaultButton(okButton);

	/* Add both of the panes to the dialog box. */
	cp.add(entryPanel, BorderLayout.CENTER);
	//	cp.add(Box.createRigidArea(new Dimension(0, 5)));
	cp.add(buttonPanel, BorderLayout.SOUTH);

	/* Set the server value to 0 (which the queue simulation interprets
	   as infinity) and disable the server number specification field
	   if the infinite server radio button is activated. */
	yesInfServerButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    finiteServerValue = serverField.getValue();
		    serverField.setText("-1");		    
		    serverField.setEnabled(false);
		}
	    }
					     );

	/* Activate the server number specification field if the
	   finite server radio button is activated. */
	noInfServerButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    serverField.setText(String.valueOf(finiteServerValue));
		    serverField.setEnabled(true);
		}
	    }
					    );

	/* Validate and retrieve all of the values in the data entry fields
	   when the 'ok' button is pressed. */
	okButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {

		    /* Validate the retrieved settings. */
		    if (validateParams()) {

			/* The settings are read after validation so
			   that they do not get accidentally stored if
			   the user presses the 'cancel' button. */
			serverValue = serverField.getValue();		    
			tickValue = tickField.getValue();
			lambdaValue = lambdaField.getValue();
			muValue = muField.getValue();
		    
			/* Hide the dialog box object after valid 
			   parameter values have been obtained so that the 
			   values can be read from the dialog box object by 
			   the applet. */
			hide();
		    }
		}
	    }
				   );
	/* Reset the fields to their default values. */
	resetButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    serverField.setText("1");
		    tickField.setText("0.1");
		    lambdaField.setText("1.0");
		    muField.setText("1.0");		   
		}
	    }
				      );
		    
	/* Close the dialog box if the 'cancel' button is pressed. */
	cancelButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
	System.out.println("Parameters updated: servers = " + serverValue + 
			   ", tick = " + tickValue + 
			   ", lambda = " + lambdaValue + 
			   ", mu =" + muValue);

		    hide();
		}
	    }
				       );

	/* Don't do anything when the dialog box is closed by
	   some means other than by pressing the buttons. */
	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    /* Check whether the various parameter variables contain valid values.
       If not, display an error message in a pop-up box and return false. */   
    protected boolean validateParams() {
	String errorMsg = new String();

	if (serverField.getValue() == 0) {
	    errorMsg += "The number of servers must be nonzero!\n";
	}
	if (tickField.getValue() == 0) {
	    errorMsg += "The time increment must be nonzero!\n";
	}
	if (lambdaField.getValue() == 0) {
	    errorMsg += "The arrival rate must be nonzero!\n";
	}
	if (muField.getValue() == 0) {
	    errorMsg += "The service rate must be nonzero!\n";
	}
	
	if (errorMsg.length() > 0) {
	    JOptionPane.showMessageDialog(null, errorMsg,
					  "Invalid Parameters Detected",
					  JOptionPane.ERROR_MESSAGE);
	    return false;
	} else {
	    return true;
	}
    }
}

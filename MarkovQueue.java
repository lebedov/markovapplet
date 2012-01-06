import java.util.*;

/* This class implements a simulated memoryless Markovian queue with an 
   arbitrary number of identical servers. */
class MarkovQueue {
    protected int state = 0;    // current state of the queue
    protected double time = 0;  // queue clock

    protected double tick;      // time interval between simulation iterations 
    protected int servers;      // number of servers
    protected Vector customers; // list of customers currently in the queue
 
    protected double lambda;    // customer arrival parameter
    protected double mu;        // service time parameter

    protected int lastArrivals = 0;   // number of arrivals in last interval
    protected int lastDepartures = 0; // number of departures in last interval
    protected Vector lastServiceLengths; // last generated service lengths

    /* This internal class represents a customer who passes through the 
       queue to receive service from a server. */
    class Customer extends Object {
	protected double serviceTime, untilDone, arrivalTime;
	
	Customer(double s, double a) {
	    serviceTime = untilDone = s;
	    arrivalTime = a;
	}
    }

    /* Construct a queue and initialize its state and time counters
       with default values. */
    MarkovQueue() {
	tick = 0.1;
	servers = 1;
	lambda = mu = 1;
	customers = new Vector();
    }

    /* Construct an M/M/s queue using the specified parameters. */
    MarkovQueue(int s, double t, double l, double m) {
	servers = s;
	tick = t;
	lambda = l;
	mu = m;
	customers = new Vector();
    }

    /* Simulate the passage of one interval of time (as defined by the tick
       variable). */
    
    public void simulateStep() {
	int i;
	int a, d = 0;
	Customer c;

	/* Advance the queue clock. */
	time += tick;

	/* Process the customers currently being serviced by decrementing the
	   lifetimes of s customers at exit end of the queue, where s is the
	   number of servers. If the servers variable is not a positive 
	   integer, then an infinite number of servers is assumed to exist
	   and all of the customers in the queue get processed. */
	if (servers < 1) {
	    for (i = 0; i < customers.size(); i++) {
		c = (Customer) customers.elementAt(i);
		c.untilDone -= tick;
		customers.setElementAt(c, i);
	    }
	} else {
	    for (i = 0; i < servers & i < customers.size(); i++) {
		c = (Customer) customers.elementAt(i);
		c.untilDone -= tick;
		customers.setElementAt(c, i);
	    }
	}

	/* Remove those customers who have been completely serviced 
	   from the queue. */
	for (i = 0; i < customers.size(); i++) {
	    c = (Customer) customers.elementAt(i);
	    if (c.untilDone <= 0) {
		customers.remove(i);
		d++;
	    }
	}

	/* Determine how many new Poisson distributed arrivals occur during
	   the interval. This is done after simulating the processing of
	   customers currently in the queue so that the new customers do
	   not immediately get processed. */
	a = genPoisson(lambda, tick);
	
	/* Assign an exponentially distributed service time to each 
	   arrival. */
	lastServiceLengths = new Vector(a);
	for (i = 0; i < a; i++) {

	    /* New arrivals are appended to the end of the customers
	       vector; departures are removed from the front end. */
	    double s = genExponential(mu);
	    c = new Customer(s, time);	
	    customers.add(c);
	    lastServiceLengths.add(new Double(s));
	}

	/* Update the queue state. */
	state = customers.size();

	/* Store the number of arrivals and departures that took place. */
	lastArrivals = a;
	lastDepartures = d;
    }

    /* Return the current queue time. */
    public double getTime() { return time; }

    /* Return the current queue state. */
    public int getState() { return state; }

    /* Return the number of arrivals who came during the last interval. */
    public int getLastArrivals() { return lastArrivals; }

    /* Return the number of departures who left during the last interval. */
    public int getLastDepartures() { return lastDepartures; }

    /* Return the service lengths of the last group of arrivals. */
    public Vector getLastServiceLengths() { return lastServiceLengths; }

    /* Generate a value for a Poisson random variable with the
       specified parameters using the method described in 
       chapter 10 of Kenneth Ross' "A First Course in Probability." */
    public int genPoisson(double l, double t) {
	double prod = 1;
	int n = 0;

	while (prod >= Math.exp(-l*t)) {
	    n++;
	    prod *= Math.random();
	}

	return n - 1;
    }
    
    /* Generate a value for an exponential random variable with the
       specified parameters. As above, the method used is due to
       Mr. Ross' aformentioned text. */
    public double genExponential(double l) {
	return Math.log(Math.random())/-l;
    }
}
	

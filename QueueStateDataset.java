import java.util.*;
import com.jrefinery.data.*;

/* This class is used to store a series of queue states at successive times
   such that the JFreeChart package can access the data via the methods in
   the XYDataset interface. */
public class QueueStateDataset extends AbstractSeriesDataset 
    implements XYDataset {

    protected Vector data;       // time/state entries are stored here
    protected int maxSize = 100; // maximum number of entries to store 

    /* This inner class is used to store the time/state pairs. */
    class DataUnit extends Object {
	double time;
	int state;

	DataUnit(double t, int s) {
	    time = t;
	    state = s;
	}

	double getTime() { return time; }
	int getState() { return state; }
    } 

    /* Construct a dataset with the default maximum number of entries. */
    public QueueStateDataset() {
	data = new Vector(maxSize);
    }

    /* Construct a dataset with the specified maximum number of entries. */
    public QueueStateDataset(int m) {
	if (m > 0) {
	    maxSize = m;
	}
	data = new Vector(maxSize);
    }

    /* Get the X value (i.e., the time) of the specified item in the 
       series. */
    public Number getXValue(int series, int item) {
	DataUnit u;
	
	/* Return null if a nonexistent data unit is requested. */
	try {
	    u = (DataUnit) data.get(item);
	} catch (ArrayIndexOutOfBoundsException e) {
	    return null;
	}
	return new Double(u.getTime());
    }

    /* Get the Y value (i.e., the state) of the specified item in the
       series. */
    public Number getYValue(int series, int item) {
	DataUnit u;

	/* Return null if a nonexistent data unit is requested. */
	try {
	    u = (DataUnit) data.get(item);
	} catch (ArrayIndexOutOfBoundsException e) {
	    return null;
	}
	return new Integer(u.getState());
    }

    /* As the dataset only contains one series of data, the series
       argument is ignored. */
    public int getItemCount(int series) { return data.size(); }
    public int getSeriesCount() { return 1; }
    public String getSeriesName(int series) { return "State"; }

    /* Append a time/state pair to the dataset. */
    public boolean add(double t, int s) {	
	boolean addStatus, removeStatus = true;

	addStatus = data.add(new DataUnit(t, s));	
	if (data.size() > maxSize) {
	    removeStatus = removeFirst();
	}

	/* Inform all relevant chart objects that the dataset has changed
	   and should therefore be redrawn. */
	notifyListeners(new DatasetChangeEvent(this));
	return addStatus && removeStatus;
    }

    /* Remove the indicated item from the dataset. */
    public boolean remove(int i) {
	try {
	    data.remove(i);
	} catch (ArrayIndexOutOfBoundsException e) {
	    return false;
	}
	notifyListeners(new DatasetChangeEvent(this));
	return true;
    }
    
    /* Remove the first item in the dataset. */
    public boolean removeFirst() {
	try {
	    data.remove(0);
	} catch (ArrayIndexOutOfBoundsException e) {
	    return false;
	}
	notifyListeners(new DatasetChangeEvent(this));
	return true;
    }
    
    /* Empty the entire dataset. */
    public void clear() {
	data.clear();
	notifyListeners(new DatasetChangeEvent(this));
    }
}

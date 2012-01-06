import java.util.*;
import com.jrefinery.data.*;

/* This class is used to store a series of entries that must be plotted
   as a histogram. In contrast to the parent class, the categories
   in this class are intervals of floating point values. */
public class RealHistDataset extends HistDataset {

    double interval = 0.5;  // interval between categories

    /* Construct a dataset. */
    public RealHistDataset(int w) {
	super(w);
    }

    /* Get the value associated with the specified  
       categories in the series. */
    public Number getValue(int series, Object category) {
	Double c = (Double) category;
	int i = (new Double(c.doubleValue()/interval)).intValue();
	Double val;

	try {
	    val = (Double) data.get(i);
	} catch (ArrayIndexOutOfBoundsException e) {
	    return new Double(0);
	}

	return val;
    }

    /* Add a value pair to the dataset. */
    public boolean add(double x, double y) {	
	int i, index, start = 0, max, min;

	/* Quantize the x value such that the product of the interval and the
	   index into the data vector gives the quantized session time. */    
	index = (new Double(x / interval)).intValue();

	/* If the given value is larger than the largest index currently in
	   the vector, extend the vector up to that index by inserting
	   zero entries. */
	if (index >= data.size()) {
	    for (i = data.size(); i <= index; i++) {
		data.add(i, new Double(0));
	    } 
	} 

	/* Insert the average of the new entry with whatever is already
	   there if the existing entry is not zero. */
	try {
	    Double currVal = (Double) data.get(index);
	    if (currVal.doubleValue() == 0) {
		currVal = new Double(y);
	    } else {
		currVal = new Double((y + currVal.doubleValue()) / 2);
	    }
	    data.set(index, currVal);	
	} catch (ArrayIndexOutOfBoundsException e) {
	    return false;
	}

	/* In order to avoid cluttering the chart, the category vector
	   is constructed to act as a maximum width window on the data 
	   vector. */
	if (categories.size() > 0) {
	    Double tempMax = (Double) categories.get(categories.size() - 1);
	    Double tempMin = (Double) categories.get(0);
	    max = new Double(tempMax.doubleValue() / interval).intValue();
	    min = new Double(tempMin.doubleValue() / interval).intValue();

	    /* Perform a bit of heuristic window positioning so that
	       changing data is displayed without causing the chart to
	       jump around too much. */
	    if (index < data.size() - window) {
		if (index > max + window) {
		    start = index;
		} else if (index > max) {
		    start = max;
		} else if (index > min + window) {
		    start = index;
		} else if (index > min) {
		    start = min;
		} else {
		    start = index;
		}	    
	    } else if (index - window >= 0) {
		start = index - window;
	    } else {
		start = 0;
	    }
	} 

	categories.clear();
	for (i = start; (i < data.size()) && (i < start + window); i++) {
	    categories.add(i - start, new Double(interval * i));
	}

	/* Inform all relevant chart objects that the dataset has changed
	   and should therefore be redrawn. */
	notifyListeners(new DatasetChangeEvent(this));
	return true;
    }

}

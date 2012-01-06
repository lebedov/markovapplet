import java.util.*;
import com.jrefinery.data.*;

/* This class is used to store a series of entries that must be plotted
   as a histogram. */
public class HistDataset extends AbstractSeriesDataset 
    implements CategoryDataset {

    protected int window; // number of entries to plot

    /* Bar heights are stored in a vector that is resized as needed. */
    protected Vector data, categories;
    protected Hashtable hash;

    /* Construct a dataset. */
    public HistDataset(int w) {
	window = w;
	data = new Vector();
	categories = new Vector(window);
    }

    /* Get the value associated with the specified  
       categories in the series. */
    public Number getValue(int series, Object category) {
	Integer i = (Integer) category;
	Double d;

	try {
	    d = (Double) data.get(i.intValue());
	} catch (ArrayIndexOutOfBoundsException e) {
	    return new Double(0);
	}

	return d;
    }

    /* As the dataset only contains one series of data, the series
       argument is ignored. */
    public int getSeriesCount() { return 1; }
    public String getSeriesName(int series) { return ""; }
    public int getCategoryCount() { return categories.size(); }
    public List getCategories() { return new Vector(categories); }
	
    /* Add a value pair to the dataset. */
    public boolean add(int x, double y) {	
	int i, start = 0, max, min;

	/* If the given value is larger than the largest index currently in
	   the vector, extend the data vector up to that index by inserting
	   zero entries. */
	if (x  >= data.size()) {
	    for (i = data.size(); i <= x; i++) {
		data.add(i, new Double(0));
	    } 
	} 

	try {
	    data.set(x, new Double(y));	
	} catch (ArrayIndexOutOfBoundsException e) {
	    return false;
	}

	/* In order to avoid cluttering the chart, the category vector
	   is constructed to act as a maximum width window on the data 
	   vector. */
	if (categories.size() > 0) {
	    max = ((Integer) categories.get(categories.size() - 1)).intValue();
	    min = ((Integer) categories.get(0)).intValue();

	    /* Perform a bit of heuristic window positioning so that
	       changing data is displayed without causing the chart to
	       jump around too much. */
	    if (x < data.size() - window) {
		if (x > max + window) {
		    start = x;
		} else if (x > max) {
		    start = max;
		} else if (x > min + window) {
		    start = x;
		} else if (x > min) {
		    start = min;
		} else {
		    start = x;
		}	    
	    } else if (x - window >= 0) {
		start = x - window;
	    } else {
		start = 0;
	    }
	} 

	categories.clear();
	for (i = start; (i < data.size()) && (i < start + window); i++) {
	    categories.add(i - start, new Integer(i));
	}
		
	/* Inform all relevant chart objects that the dataset has changed
	   and should therefore be redrawn. */
	notifyListeners(new DatasetChangeEvent(this));
	return true;
    }

    /* Empty the entire dataset. */
    public void clear() {
	data.clear();
	categories.clear();

	notifyListeners(new DatasetChangeEvent(this));
    }
}

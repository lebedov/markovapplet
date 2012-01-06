import javax.swing.*;
import javax.swing.text.*;

/* This extension of JTextField only allows the entry of positive floating
   point numbers. */
public class RealOnlyTextField extends JTextField {

    public RealOnlyTextField(String str, int cols) {
	super(str, cols);
    }

    protected Document createDefaultModel() {
	return new RealOnlyDocument();
    }

    static class RealOnlyDocument extends PlainDocument {
	protected boolean firstDecimal = true;
       
	public void insertString(int offset, String str, AttributeSet a) 
	    throws BadLocationException {

	    /* Don't do anything if there is nothing to insert! */
	    if (str == null) {
		return;
	    }

 	    /* Insert the string if it is formatted properly. */
	    try {
		double d = Double.parseDouble(str);
	    } catch (NumberFormatException e) {

		/* Handle decimal points the first time they appear. */
		if (!str.equals(".")) {
		    return;
		} else if (str.equals(".") && !firstDecimal) {
		    return;
		} else {
		    firstDecimal = false;
		}		
	    }
	    //System.out.println("[" + str + "]");

	    /* If the string was successfully parsed but had a decimal point
	       in it (as could happen when this class is instantiated with
	       a given value), the firstDecimal flag must be set. */
	    if (str.indexOf(".") != -1) {
		firstDecimal = false;
	    }
	    
	    /* Insert the string into the field. */
	    super.insertString(offset, str, a);	    
	}

	/*  Reset the decimal point flag so that a decimal point
	    can be inserted later. */
	public void remove(int offset, int len) throws BadLocationException {
	    String str = getText(offset, len);
	    if (str.indexOf(".") != -1) {
		firstDecimal = true;
	    }
	    super.remove(offset, len);
	}
    }

    /* Return whatever is in the field as a floating point value. */
    public double getValue() {
	double d;

	try {
	    d = Double.parseDouble(getText());
	} catch (NumberFormatException e) {
	    return 0;
	} 
	return d;	    
    }
}

import javax.swing.*;
import javax.swing.text.*;

/* This extension of JTextField only allows the entry of integers. */
class IntOnlyTextField extends JTextField {

    public IntOnlyTextField(String str, int cols) {
	super(str, cols);
    }

    protected Document createDefaultModel() {
	return new IntOnlyDocument();
    }

    static class IntOnlyDocument extends PlainDocument {
       
	public void insertString(int offset, String str, AttributeSet a) 
	    throws BadLocationException {

	    /* Don't do anything if there is nothing to insert! */
	    if (str == null) {
		return;
	    }

 	    /* Insert the string if it is formatted properly. */
	    try {
		int i = Integer.parseInt(str);
	    } catch (NumberFormatException e) {

		/* Don't do anything if the string is misformatted. */
		return;
	    }
	    super.insertString(offset, str, a);
	}
    }

    /* Return whatever is in the field as an integer value. */
    public int getValue() {
	int i;

	try {
	    i = Integer.parseInt(getText());
	} catch (NumberFormatException e) {
	    return 0;
	} 
	return i;		
    }
}

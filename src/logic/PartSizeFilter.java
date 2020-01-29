/**
 * 
 */
package logic;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * @author nicola.ferrari
 *
 */
public class PartSizeFilter extends DocumentFilter {

	private long maxSize;
	private boolean doEmptyOnWrongReplace = false;
 
    public PartSizeFilter(long max) {
    	maxSize = max;
    }
 
    public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
    	try {
    		long number = Long.parseLong(fb.getDocument().getText(0, fb.getDocument().getLength()) + str);
    	
	        if (number <= maxSize)
	            super.insertString(fb, offs, str, a);
	        else
	            Toolkit.getDefaultToolkit().beep();
    	}
        catch(NumberFormatException e) {
        	Toolkit.getDefaultToolkit().beep();
    	}
    }
     
    public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
    	try {
	    	String oldStr = fb.getDocument().getText(0, fb.getDocument().getLength());
	    	String newStr = oldStr.substring(0, offs) + str + oldStr.substring(offs+length, oldStr.length());
	    	
	    	long number = Long.parseLong(newStr);
	    	
	        if (number <= maxSize) {
	            super.replace(fb, offs, length, str, a);
	    	}
	        else if(doEmptyOnWrongReplace) {
	        	super.replace(fb, 0, fb.getDocument().getLength(), "", a);
	        	doEmptyOnWrongReplace = true;
	        }
	        else {
	            Toolkit.getDefaultToolkit().beep();
	        }
    	}
    	catch(NumberFormatException e) {
    		Toolkit.getDefaultToolkit().beep();
    	}
    }

	/**
	 * @return the maxSize
	 */
	public long getMaxSize() {
		return maxSize;
	}

	/**
	 * @param maxSize the maxSize to set
	 */
	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
		//Quando viene cambiato il massimo, viene effettuato un replace con la stessa stringa. Se questa non passa il filtro, svuoto il campo.
		doEmptyOnWrongReplace = true;
	}

}

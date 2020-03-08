/**
 * 
 */
package gui;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * @author nicola.ferrari
 *
 */
@Deprecated
public class PartSizeFilter extends DocumentFilter {

	private double maxSize;
	private boolean doEmptyOnWrongReplace = false;
 
    public PartSizeFilter(double d) {
    	maxSize = d;
    }
 
    public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
    	
    	//Match anche di numeri in via di scrittura (e.g: '54.'). In tal caso, il parse del numero fallisce, ma non lo
    	//reputo un errore: faccio il catch dell'eccezione e non faccio niente. 
    	//Se il parse ha successo, allora procedo con l'assicurarmi che il numero sia minore del massimo
    	//Voglio anche l'unità di misura
    	if( fb.getDocument().getText(0, fb.getDocument().getLength()).matches("^[0-9]+[.]?[0-9]\\s[KMG]?B$") ) {
    		try {
    			double number = Double.parseDouble(fb.getDocument().getText(0, fb.getDocument().getLength()) + str);
    	
		        if (number <= maxSize)
		            super.insertString(fb, offs, str, a);
		        else
		            Toolkit.getDefaultToolkit().beep();
    		}
    		catch(NumberFormatException e) {}
		}
        else
            Toolkit.getDefaultToolkit().beep();
        
    }
     
    public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
    	String oldStr = fb.getDocument().getText(0, fb.getDocument().getLength());
    	String newStr = oldStr.substring(0, offs) + str + oldStr.substring(offs+length, oldStr.length());
    	
    	//Match anche di numeri in via di scrittura (e.g: '54.'). In tal caso, il parse del numero fallisce, ma non lo
    	//reputo un errore: faccio il catch dell'eccezione e non faccio niente. 
    	//Se il parse ha successo, allora procedo con l'assicurarmi che il numero sia minore del massimo
    	//Voglio anche l'unità di misura
    	if( newStr.matches("^[0-9]+[.]?[0-9]\\s[KMG]?B$") ) {
    		try {
    			double number = Double.parseDouble(newStr);
	    	
		        if (number <= maxSize) {
		            super.replace(fb, offs, length, str, a);
		    	}
		        else if(doEmptyOnWrongReplace) {
		        	super.replace(fb, 0, fb.getDocument().getLength(), "", a);
		        	doEmptyOnWrongReplace = false;
		        }
		        else {
		            Toolkit.getDefaultToolkit().beep();
		        }
    		}
        	catch(NumberFormatException e) {}
    	}
        else
            Toolkit.getDefaultToolkit().beep();
    }

	/**
	 * @return the maxSize
	 */
	public double getMaxSize() {
		return maxSize;
	}

	/**
	 * @param maxSize the maxSize to set
	 */
	public void setMaxSize(double maxSize) {
		this.maxSize = maxSize;
		//Quando viene cambiato il massimo, viene effettuato un replace con la stessa stringa. Se questa non passa il filtro, svuoto il campo.
		doEmptyOnWrongReplace = true;
	}

}

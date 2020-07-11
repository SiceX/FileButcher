/**
 * 
 */
package logic.tasks;

/**
 * Enumerazione delle modalit� di svolgimento del Task.
 * <br>Le modalit� possibili sono:
 * <br>-BUTCHER_SAME_SIZE:	Divisione in parti di dimensioni uguali
 * <br>-BUTCHER_CRYPT_SAME_SIZE:	Divisione in parti di dimensioni uguali con cifratura
 * <br>-BUTCHER_CUSTOM_NUMBER:	Divisione in un numero di parti definito dall'utente
 * <br>-REBUILD_SAME_SIZE:	Ricomposizione di un file a partire dalla prima di N parti di dimensione uguale
 * <br>-REBUILD_CRYPT_SAME_SIZE:	Ricomposizione e decifratura di un file a partire dalla prima di N parti di dimensione uguale
 * <br>-REBUILD_CUSTOM_NUMBER:	Ricomposizione di un file a partire dalla prima di N parti dove N era un numero definito dall'utente
 * @author nicola.ferrari
 */
public enum TaskMode {
	/**
	 * Divisione in parti di dimensioni uguali.
	 * <br>Estensione:	.par
	 * <br>Descrizione:	Parti uguali
	 */
	BUTCHER_SAME_SIZE(".par", "Parti uguali"),
	/**
	 * Divisione in parti di dimensioni uguali con cifratura
	 * <br>Estensione:	.crypar
	 * <br>Descrizione:	Parti uguali sicuro
	 */
	BUTCHER_CRYPT_SAME_SIZE(".crypar", "Parti uguali sicuro"),
	/**
	 * Divisione in un numero di parti definito dall'utente
	 * <br>Estensione:	.crypar
	 * <br>Descrizione:	N parti
	 */
	BUTCHER_CUSTOM_NUMBER(".parn", "N parti"),
	/**
	 * Ricomposizione di un file a partire dalla prima di N parti di dimensione uguale
	 * <br>Estensione:	.par
	 * <br>Descrizione:	Parti uguali
	 */
	REBUILD_SAME_SIZE(".par", "Parti uguali"),
	/**
	 * Ricomposizione e decifratura di un file a partire dalla prima di N parti di dimensione uguale
	 * <br>Estensione:	.crypar
	 * <br>Descrizione:	Parti uguali sicuro
	 */
	REBUILD_CRYPT_SAME_SIZE(".crypar", "Parti uguali sicuro"),
	/**
	 * Ricomposizione di un file a partire dalla prima di N parti dove N era un numero definito dall'utente
	 * <br>Estensione:	.parn
	 * <br>Descrizione:	N parti
	 */
	REBUILD_CUSTOM_NUMBER(".parn", "N parti");
	
	/**
	 * Estensione del file corrispondente
	 */
	private String fileExt;
	/**
	 * Descrizione della modalit�
	 */
	private String displayName;

	TaskMode(String fileExt, String displayName) {
		this.fileExt = fileExt;
		this.displayName = displayName;
	}
	
	/**
	 * @return Array dei valori selezionabili all'atto della modifica nella ComboBox della tabella
	 */
	public static TaskMode[] selectableValues() {
		TaskMode values[] = {
				TaskMode.BUTCHER_SAME_SIZE,
				TaskMode.BUTCHER_CRYPT_SAME_SIZE,
				TaskMode.BUTCHER_CUSTOM_NUMBER
		};
		return values;
	}
	
	/**
	 * @return L'estensione del file corrispondente
	 */
	public String getFileExtension() {
       return fileExt;
	}

	/**
	 * @return La descrizione della modalit�
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 *	@return La descrizione della modalit�
	 */
	@Override
	public String toString() {
		return displayName;
	}

}

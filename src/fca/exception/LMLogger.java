package fca.exception;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import fca.messages.ExceptionMessages;

/**
 * G�re les logs de Lattice Miner et propose des m�thodes pour stocker dans le fichier log plus
 * facilement et sans avoir a utiliser {@link Logger} et {@link Level}
 * @author Ludovic Thomas
 * @version 1.0
 */
public class LMLogger {
	
	/** Titre du log si l'exception n'est pas affich�e en bo�te de dialogue */
	private static final String NOT_SHOW = ExceptionMessages.getString("LMLogger.NotShow"); //$NON-NLS-1$
	
	/** Titre du log si l'exception a �t� affich�e en bo�te de dialogue */
	private static final String SHOW = ExceptionMessages.getString("LMLogger.ShowInBox"); //$NON-NLS-1$
	
	/** A priori pas n�cessire de r�cup�rer les Warning car ils sont d�j� trait�s */
	private static final boolean LOG_WARNING = false;
	
	/** Repr�sente le logger qui sauvegarde les erreurs du logiciel */
	private static Logger LOGGER;
	
	/** Le singleton repr�sentant {@link LMLogger} */
	private static LMLogger SINGLETON = null;
	
	/**
	 * Cr�er le logger de Lattice Miner
	 */
	private LMLogger() {
		// Cr�er le fichier de log et ouvre le log
		try {
			// Cr�er le fichier de log en mode "concat�nation"
			boolean append = true;
			int sizeLimit = 1000000; // 1 Mb
			FileHandler handler = new FileHandler("%t/lm.log", sizeLimit, 1, append); //$NON-NLS-1$
			
			// Ajoute le fichier au logger
			LOGGER = Logger.getLogger("lm.fca"); //$NON-NLS-1$
			LOGGER.addHandler(handler);
		} catch (IOException e) {
			System.out.println(ExceptionMessages.getString("LMLogger.LogError")); //$NON-NLS-1$
		}
	}
	
	/**
	 * @return l'unique instance de {@link LMLogger}
	 */
	public static final LMLogger getLMLogger() {
		if (SINGLETON == null) {
			SINGLETON = new LMLogger();
		}
		return SINGLETON;
	}
	
	/**
	 * Log dans le fichier de log un message de type Severe
	 * @param message a loguer
	 * @param notShow vrai si l'exception n'est pas affich�e en bo�te de dialogue, faux sinon
	 */
	public static final void logSevere(String message, boolean notShow) {
		LOGGER.log(Level.SEVERE, getTitle(notShow), message);
	}
	
	/**
	 * Log dans le fichier de log une exception de type Severe
	 * @param exception a loguer
	 * @param notShow vrai si l'exception n'est pas affich�e en bo�te de dialogue, faux sinon
	 */
	public static final void logSevere(Exception exception, boolean notShow) {
		LOGGER.log(Level.SEVERE, getTitle(notShow), exception);
	}
	
	/**
	 * Log dans le fichier de log un message de type Warning
	 * @param message a loguer
	 * @param notShow vrai si l'exception n'est pas affich�e en bo�te de dialogue, faux sinon
	 */
	public static final void logWarning(String message, boolean notShow) {
		if (LOG_WARNING) {
			LOGGER.log(Level.WARNING, getTitle(notShow), message);
		}
	}
	
	/**
	 * Log dans le fichier de log une exception de type Warning
	 * @param exception a loguer
	 * @param notShow vrai si l'exception n'est pas affich�e en bo�te de dialogue, faux sinon
	 */
	public static final void logWarning(Exception exception, boolean notShow) {
		if (LOG_WARNING) {
			LOGGER.log(Level.WARNING, getTitle(notShow), exception);
		}
	}
	
	/**
	 * Retourne le titre du log selon que l'exception soit affich�e ou non en bo�te de dialogue
	 * @param notShow vrai si l'exception n'est pas affich�e en bo�te de dialogue, faux sinon
	 * @return le titre du log a mettre
	 */
	private static final String getTitle(boolean notShow) {
		if (notShow) {
			return NOT_SHOW;
		} else {
			return SHOW;
		}
	}
	
}

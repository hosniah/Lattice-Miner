package fca.exception;

import fca.messages.ExceptionMessages;

/**
 * Exception lorsqu'un op�rateur �choue dans sa requete
 * @author Ludovic Thomas
 * @version 1.0
 */
public class OperatorPerformException extends LatticeMinerException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6162608612804353381L;

	/**
	 * Constructeur
	 * @param moreInformation le message d�taill�
	 */
	public OperatorPerformException(String moreInformation) {
		super(moreInformation);
	}
	
	/*
	 * (non-Javadoc)
	 * @see fca.exception.LatticeMinerException#getMessageGeneral()
	 */
	@Override
	public String getMessageGeneral() {
		return ExceptionMessages.getString("OperatorPerformException"); //$NON-NLS-1$
	}
}

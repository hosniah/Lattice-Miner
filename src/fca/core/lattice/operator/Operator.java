package fca.core.lattice.operator;

/**
 * Cette classe abstraite {@link Operator} permet d'appeler et de cr�er rapidement une op�ration
 * gr�ce aux possibilit�s des templates et eux typages des m�thodes qui s'en suivent. Un simple
 * appel � la m�thode <code>perform(E)<code> permet de r�aliser l'op�ration.
 * @param <D> type des donn�es en entr�es (� la cr�ation de l'op�rateur)
 * @param <E> type du param�tre de la requ�te
 * @param <R> type du r�sultat de la requ�te
 * @author Ludovic Thomas
 * @version 1.0
 */
public abstract class Operator<D, E, R> {
	
	/**
	 * Donn�es utiles pour l'op�ration
	 */
	protected D data;
	
	/**
	 * R�sultat de l'op�ration
	 */
	protected R result;
	
	/**
	 * Nom de l'op�ration
	 */
	private String operatorName;
	
	/**
	 * Version de l'op�ration
	 */
	private double operatorVersion;
	
	/** Version par d�faut d'une op�ration */
	private final static double INIT_VERSION = 1.0;
	
	/**
	 * Constructeur d'une op�ration
	 * @param data les donn�es de bases de l'op�ration
	 * @param operatorName le nom de l'op�ration
	 */
	public Operator(D data, String operatorName) {
		this.operatorName = operatorName;
		this.operatorVersion = INIT_VERSION;
		this.data = data;
		this.result = null;
	}
	
	/**
	 * Ex�cute l'algorithme pour une requ�te sp�cifiaue via le param�tre
	 * @param entry la donn�e de la requ�te
	 * @return la r�sultat de la requ�te
	 */
	public abstract R perform(E entry);
	
	/**
	 * @return le nom de l'op�ration
	 */
	public String getOperatorName() {
		return operatorName;
	}
	
	/**
	 * @return la version de l'op�ration
	 */
	public double getOperatorVersion() {
		return operatorVersion;
	}
	
}

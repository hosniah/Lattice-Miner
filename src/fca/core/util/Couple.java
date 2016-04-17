package fca.core.util;

/**
 * Represente un doublet de donn�es
 * @param <T1> type du premier �l�ment du doublet
 * @param <T2> type du deuxi�me �l�ment du doublet
 * @author Ludovic Thomas
 * @version 1.0
 */
public class Couple<T1, T2> {
	
	/**
	 * Le premier �l�ment du doublet
	 */
	protected T1 first;
	
	/**
	 * Le deuxi�me �l�ment du doublet
	 */
	protected T2 second;
	
	/**
	 * Construit un doublet
	 * @param first le premier �l�ment du doublet
	 * @param second le deuxi�me �l�ment du doublet
	 */
	public Couple(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}
	
	/**
	 * @return le premier �l�ment du doublet
	 */
	public T1 getFirst() {
		return first;
	}
	
	/**
	 * Change le premier �l�ment du doublet
	 * @param first le nouveau premier �l�ment du doublet
	 */
	public void setFirst(T1 first) {
		this.first = first;
	}
	
	/**
	 * @return le deuxi�me �l�ment du doublet
	 */
	public T2 getSecond() {
		return second;
	}
	
	/**
	 * Change le deuxi�me �l�ment du doublet
	 * @param second le nouveau deuxi�me �l�ment du doublet
	 */
	public void setSecond(T2 second) {
		this.second = second;
	}
	
}

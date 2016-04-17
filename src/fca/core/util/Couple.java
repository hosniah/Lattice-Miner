package fca.core.util;

/**
 * Represente un doublet de données
 * @param <T1> type du premier élément du doublet
 * @param <T2> type du deuxième élément du doublet
 * @author Ludovic Thomas
 * @version 1.0
 */
public class Couple<T1, T2> {
	
	/**
	 * Le premier élément du doublet
	 */
	protected T1 first;
	
	/**
	 * Le deuxième élément du doublet
	 */
	protected T2 second;
	
	/**
	 * Construit un doublet
	 * @param first le premier élément du doublet
	 * @param second le deuxième élément du doublet
	 */
	public Couple(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}
	
	/**
	 * @return le premier élément du doublet
	 */
	public T1 getFirst() {
		return first;
	}
	
	/**
	 * Change le premier élément du doublet
	 * @param first le nouveau premier élément du doublet
	 */
	public void setFirst(T1 first) {
		this.first = first;
	}
	
	/**
	 * @return le deuxième élément du doublet
	 */
	public T2 getSecond() {
		return second;
	}
	
	/**
	 * Change le deuxième élément du doublet
	 * @param second le nouveau deuxième élément du doublet
	 */
	public void setSecond(T2 second) {
		this.second = second;
	}
	
}

package fca.core.util;

/**
 * Represente un triplet de données
 * @param <T1> type du premier élément du triplet
 * @param <T2> type du deuxième élément du triplet
 * @param <T3> type du troisième élément du triplet
 * @author Ludovic Thomas
 * @version 1.0
 */
public class Triple<T1, T2, T3> extends Couple<T1, T2> {
	
	/**
	 * Le troisième élément du triplet elementType="fca.lattice.graphical.GraphicalConcept"
	 */
	protected T3 third;
	
	/**
	 * Construit un triplet
	 * @param first le premier élément du triplet
	 * @param second le deuxième élément du triplet
	 * @param third le troisième élément du triplet
	 */
	public Triple(T1 first, T2 second, T3 third) {
		super(first, second);
		this.third = third;
	}
	
	/**
	 * @return le troisième élément du triplet
	 */
	public T3 getThird() {
		return third;
	}
	
	/**
	 * Change le troisième élément du triplet
	 * @param third le nouveau troisième élément du triplet
	 */
	public void setThird(T3 third) {
		this.third = third;
	}
	
}

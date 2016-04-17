package fca.core.util;

/**
 * Represente un triplet de donn�es
 * @param <T1> type du premier �l�ment du triplet
 * @param <T2> type du deuxi�me �l�ment du triplet
 * @param <T3> type du troisi�me �l�ment du triplet
 * @author Ludovic Thomas
 * @version 1.0
 */
public class Triple<T1, T2, T3> extends Couple<T1, T2> {
	
	/**
	 * Le troisi�me �l�ment du triplet elementType="fca.lattice.graphical.GraphicalConcept"
	 */
	protected T3 third;
	
	/**
	 * Construit un triplet
	 * @param first le premier �l�ment du triplet
	 * @param second le deuxi�me �l�ment du triplet
	 * @param third le troisi�me �l�ment du triplet
	 */
	public Triple(T1 first, T2 second, T3 third) {
		super(first, second);
		this.third = third;
	}
	
	/**
	 * @return le troisi�me �l�ment du triplet
	 */
	public T3 getThird() {
		return third;
	}
	
	/**
	 * Change le troisi�me �l�ment du triplet
	 * @param third le nouveau troisi�me �l�ment du triplet
	 */
	public void setThird(T3 third) {
		this.third = third;
	}
	
}

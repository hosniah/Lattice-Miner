package fca;

import fca.core.util.BasicSet;
import fca.core.util.LogicalFormula;

/**
 * Classe secondaire pour lancer Lattice Miner en test
 * 
 * @author Geneviève Roberge
 * @version 1.0
 */
public class TestMain {

	/**
	 * Methode pour tester Lattice Miner
	 * 
	 * @param args
	 *            arguments de tests de Lattice Miner
	 */
	public static void main(String[] args) {

		String formulaString = "([allo] AND [salut]) OR NOT ([patate] AND [banane] AND [pomme verte]) OR [bleu]"; //$NON-NLS-1$
		BasicSet validElements = new BasicSet();
		validElements.add("bleu"); //$NON-NLS-1$
		validElements.add("rouge"); //$NON-NLS-1$
		validElements.add("vert"); //$NON-NLS-1$
		validElements.add("pomme verte"); //$NON-NLS-1$
		validElements.add("pomme"); //$NON-NLS-1$
		validElements.add("poire"); //$NON-NLS-1$
		validElements.add("banane"); //$NON-NLS-1$
		validElements.add("carotte"); //$NON-NLS-1$
		validElements.add("patate"); //$NON-NLS-1$
		validElements.add("celeri vert"); //$NON-NLS-1$
		validElements.add("salut"); //$NON-NLS-1$
		validElements.add("bonjour"); //$NON-NLS-1$
		validElements.add("allo"); //$NON-NLS-1$

		LogicalFormula formula = new LogicalFormula(formulaString,
				validElements);
		System.out.println("Valid = " + formula.isValid()); //$NON-NLS-1$

		if (!formula.isValid()) {
			int pos = formula.getErrorPosition();
			int length = formula.getErrorLength();

			System.out.println("Error : " + formula.getErrorMessage()); //$NON-NLS-1$
			System.out.println("Error position : " + pos); //$NON-NLS-1$
			System.out
					.println("Error string : " + formulaString.substring(pos, pos + length)); //$NON-NLS-1$
		}

		else {
			BasicSet attSet = new BasicSet();
			attSet.add("rouge"); //$NON-NLS-1$
			attSet.add("vert"); //$NON-NLS-1$
			attSet.add("patate"); //$NON-NLS-1$
			attSet.add("pomme verte"); //$NON-NLS-1$
			attSet.add("banane"); //$NON-NLS-1$
			attSet.add("salut"); //$NON-NLS-1$
			attSet.add("bonjour"); //$NON-NLS-1$
			attSet.add("carotte"); //$NON-NLS-1$

			System.out
					.println("Accept " + attSet.toString() + " = " + formula.accept(attSet)); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
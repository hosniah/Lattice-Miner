package fca;

import fca.exception.LMLogger;
import fca.gui.context.ContextViewer;
import fca.gui.util.constant.LMIcons;
import fca.gui.util.constant.LMImages;
import fca.messages.MainMessages;

/**
 * Classe principale pour lancer Lattice Miner</br> Ce prototype a �t� �labor�
 * dans le cadre du m�moire de ma�trise de Genevi�ve Roberge (UQO) sous la
 * direction des professeurs Rokia Missaoui et Jurek Czyzowicz.
 * 
 * @author Genevi�ve Roberge
 * @author Ludovic Thomas
 * @author Yoann Montouchet
 * @version 1.3
 */
public class LatticeMiner {

	/** Num�ro de la version actuelle de Lattice Miner */
	public static final double LATTICE_MINER_VERSION = 1.3;

	/**
	 * Methode principale permettant de lancer Lattice Miner
	 * 
	 * @param args
	 *            aucun argument n'est n�cessaire pour lancer Lattice Miner
	 */
	public static void main(String[] args) {

		// Aucun argument n'est n�cessaire pour lancer Lattice Miner
		if (args.length != 0) {
			System.out.println(MainMessages
					.getString("LatticeMiner.NoParameter")); //$NON-NLS-1$
			System.out.println(MainMessages
					.getString("LatticeMiner.PleaseRelaunch")); //$NON-NLS-1$
			System.exit(-1);
		}

		// Initalise la classe du logger
		LMLogger.getLMLogger();

		// Initialise la classe des images et des ic�nes
		LMImages.getLMImages();
		LMIcons.getLMIcons();

		//try {
			// Initialise et cr�er la fenetre des contextes
			ContextViewer.getContextViewer();
		//} catch (Exception e) {
		//	LMLogger.logSevere(e, true);
	//	}
	}
}
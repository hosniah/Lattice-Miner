package fca.io.context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import fca.core.context.Context;
import fca.exception.ReaderException;
import fca.messages.IOMessages;

/**
 * Le standard ContextReader pour la lecture de contexte
 * @author Ludovic Thomas
 * @version 1.0
 */
public abstract class ContextReader extends FileReader {
	
	/**
	 * Le context en cours de creation
	 */
	protected Context context;
	
	/**
	 * Le fichier qui contient le contexte a lire
	 */
	protected File contextFile;
	
	/**
	 * Constructeur du lecteur de contexte abstrait
	 * @param contextFile le fichier a lire
	 * @throws FileNotFoundException si le fichier ne peut être trouvé
	 * @throws ReaderException si une erreur de lecture arrive
	 */
	public ContextReader(File contextFile) throws FileNotFoundException, ReaderException {
		super(contextFile);
		this.contextFile = contextFile;
	}
	
	/**
	 * Lit le context via le fichier, recupere le nom du contexte via le fichier et mets le context
	 * comme étant modifié pour pouvoir le sauvegarder
	 * @throws ContextReaderException si une erreur de lecture arrive
	 */
	protected void readContext() throws ReaderException {
		
		// Creer le context
		launchContextCreation();
		
		// Recupere le nom du fichier pour le nom du contexte
		String fileName = contextFile.getName();
		int endIdx = fileName.indexOf('.');
		String contextName = fileName.substring(0, endIdx);
		context.setName(contextName);
		
		// Associe le fichier au context cree
		context.setContextFile(contextFile);
		context.setModified(false);
	}
	
	/**
	 * Entamme la création du contexte, notemment en appelant createContext() au moment souhaité
	 * @throws ContextReaderException si une erreur de lecture arrive
	 */
	protected abstract void launchContextCreation() throws ReaderException;
	
	/**
	 * Créer le contexte en lisant le fichier
	 * @throws ContextReaderException si une erreur de lecture arrive
	 */
	protected abstract void createContext() throws ReaderException;
	
	/**
	 * Initialise le contexte et le fichier avant de commencer la lecture
	 */
	protected abstract void initContext();
	
	/**
	 * @return le context associé au ContextReader
	 * @throws ReaderException si le context est vide
	 */
	public Context getContext() throws ReaderException {
		if (context == null)
			throw new ReaderException(IOMessages.getString("IO.EmptyContext")); //$NON-NLS-1$
		return context;
	}
	
}
package freenet.client.async;

import freenet.client.FetchException;

/**
 * Thrown when creating a KeyListener fails.
 * @author Matthew Toseland <toad@amphibian.dyndns.org> (0xE43DA450)
 *
 */
class KeyListenerConstructionException extends Exception {

	KeyListenerConstructionException(FetchException e) {
		super(e);
	}
	
	public FetchException getFetchException() {
		return (FetchException) getCause();
	}
	
}

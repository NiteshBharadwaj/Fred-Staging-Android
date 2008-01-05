/* This code is part of Freenet. It is distributed under the GNU General
 * Public License, version 2 (or at your option any later version). See
 * http://www.gnu.org/ for further details of the GPL. */
package freenet.store;

import freenet.keys.KeyVerifyException;

/**
 * @author toad
 */
public abstract class StoreCallback {
	
	/** Length of a data block. Fixed for lifetime of the store. */
	public abstract int dataLength();
	
	/** Length of a header block. Fixed for the lifetime of the store. */
	public abstract int headerLength();
	
	/** Length of a routing key. Routing key is what we use to search for a block. Also fixed. */
	public abstract int routingKeyLength();
	
	/** Whether we should create a .keys file to keep full keys in in order to reconstruct. */
	public abstract boolean storeFullKeys();
	
	/** Length of a full key. Full keys are stored in the .keys file. Also fixed. */
	public abstract int fullKeyLength();
	
	/** Can the same key be valid for two different StorableBlocks? */
	public abstract boolean collisionPossible();
	
	protected FreenetStore store;
	
	/** Called once when first connecting to a FreenetStore */
	void setStore(FreenetStore store) {
		if(this.store != null)
			throw new IllegalArgumentException("Calling setStore twice!");
		this.store = store;
	}
	
	// Reconstruction
	
	/** Construct a StorableBlock from the data, headers, and optionally routing key or full key 
	 * @throws KeyVerifyException */
	abstract StorableBlock construct(byte[] data, byte[] headers, byte[] routingKey, byte[] fullKey) throws KeyVerifyException;
}

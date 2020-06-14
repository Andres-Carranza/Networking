/**
 * 
 */
package andres.networking.client;

/**
 * @author andre
 *
 */
public interface NetworkListener {
	public abstract void messageReceived(String sender, String ID, String message);
}

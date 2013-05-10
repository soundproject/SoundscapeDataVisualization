package infrastructure.interfaces;

import infrastructure.SelfRegisteringComponent;

/**
 * Interface used by {@link Component} to allow objects
 * to handle die() event when raised 
 * @author Gadi
 *
 */
public interface IDeathListener {
	
	void handleDeath(Object object);

}

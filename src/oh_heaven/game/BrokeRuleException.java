package oh_heaven.game;

/**
 * An exception thrown when a player breaks a rule
 */
@SuppressWarnings("serial")
public class BrokeRuleException extends Exception {
	public BrokeRuleException(String violation) {
		super(violation);
	}
}

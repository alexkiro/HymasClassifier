package hymas.motion.m6.classifier;

class BearingException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * Exceptie proprie (utilizata la protejarea parametrilor metodelor).
	 * 
	 * @param _message
	 */
	public BearingException(String _message)
	{
		super(_message);
	}
}

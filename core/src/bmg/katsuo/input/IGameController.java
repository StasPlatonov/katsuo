package bmg.katsuo.input;

import com.badlogic.gdx.controllers.Controller;

/**
 * class used to abstract the controller
 * 
 * @author john
 *
 */
public interface IGameController
{
	boolean hasTOriggerAxis();
	
	void update();
	
	void remove();
	
	Controller getController();

	/**
	 * check if the controller is connected
	 * 
	 * @return
	 */
	boolean isConnected();

	/**
	 * check the value of the right trigger
	 * 
	 * @return
	 */
	float getRightTrigger();

	/**
	 * check the value of the left trigger
	 * 
	 * @return
	 */
	float getLeftTrigger();

	/**
	 * get the x-axis value of the right analog stick
	 * 
	 * @return -1 to 1
	 */
	float getRightX();

	/**
	 * get the y-axis value of the right analog stick
	 * 
	 * @return -1 to 1
	 */
	float getRightY();

	/**
	 * get the x-axis value of the right analog stick
	 * 
	 * @return -1 to 1
	 */
	float getLeftX();

	/**
	 * get the y-axis value of the right analog stick
	 * 
	 * @return -1 to 1
	 */
	float getLeftY();

	/**
	 * check if the `A` button is pressed
	 * 
	 * @return
	 */
	boolean buttonAPressed();

	/**
	 * check if the `B` button is pressed
	 * 
	 * @return
	 */
	boolean buttonBPressed();

	/**
	 * check if the `X` button is pressed - note this is based on xbox configuration
	 * 
	 * @return
	 */
	boolean buttonXPressed();

	/**
	 * check if the `Y` button is pressed - note this is based on an xbox
	 * configuration
	 * 
	 * @return
	 */
	boolean buttonYPressed();
	
	boolean leftPressed();
	
	boolean rightPressed();
	
	boolean upPressed();
	
	boolean downPressed();

	boolean L1Pressed();

	boolean R1Pressed();

	boolean L3Pressed();

	boolean R3Pressed();
	
	boolean selectPressed();
	
	boolean startPressed();

	/**
	 * check if the `A` button was just pressed
	 * 
	 * @return
	 */
	boolean buttonAJustPressed();

	/**
	 * check if the `B` button was just pressed
	 * 
	 * @return
	 */
	boolean buttonBJustPressed();

	/**
	 * check if the `X` button was just pressed - note this is based on an xbox
	 * configuration
	 * 
	 * @return
	 */
	boolean buttonXJustPressed();

	/**
	 * check if the `Y` button was just pressed = note this is based on an xbox
	 * configuration
	 * 
	 * @return
	 */
	boolean buttonYJustPressed();
	
	boolean leftJustPressed();
	
	boolean rightJustPressed();
	
	boolean upJustPressed();
	
	boolean downJustPressed();

	boolean L1JustPressed();

	boolean R1JustPressed();

	boolean L3JustPressed();

	boolean R3JustPressed();
	
	boolean selectJustPressed();
	
	boolean startJustPressed();
}

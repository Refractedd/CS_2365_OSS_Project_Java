public class application {

	/* TODO
		Finish banking system
	 */
	public static void main(String[] args) {
		//Start Banking System
		Buffer buff = new Buffer();
		banking bank = new banking(buff);

		bank.start();

		//Start Login GUI
		logOn login = new logOn(buff);
		login.loginFrame();
	}

}

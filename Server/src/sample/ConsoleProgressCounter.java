package sample;

public class ConsoleProgressCounter {

	private static final int MAX = 2500;
	private static final int REST = 1;
	//DOES NOT WORK IN THE ECLIPSE IDE CONSOLE!
	public static void main(String[] args){
		
		String max_String = Integer.toString(MAX);
		String done = "Done!";
		
		for(int i = 0; i <= MAX; i++){
			//carriage return will return to the start of the line
			System.out.print(Integer.toString(i) + " / " + max_String + "\r");
			
			try {
				Thread.sleep(REST);
			} catch (InterruptedException e) {
				
			}
		}
		System.out.print(done);
		for(int i = 0; i < ((max_String.length() * 2) + 3 - done.length());i++){
			System.out.print(" ");
		}
		System.out.println();
		
	}

}

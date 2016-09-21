import java.io.*;

public class CircleTheDot {
	
  public static void main(String[] args) throws IOException, FileNotFoundException{
    
    
    
    int size = 15;
    if (args.length == 1) {
      try{
        size = Integer.parseInt(args[0]);
        if(size<4){
          System.out.println("Invalide argument, using default...");
          size = 9;
        }
      } catch(NumberFormatException e){
        System.out.println("Invalide argument, using default...");
      }
    }
    StudentInfo a = new StudentInfo();
    a.display();
    GameController game = new GameController(size);
  }
}

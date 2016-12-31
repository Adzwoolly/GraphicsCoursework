package coursework.woollena;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LevelReader {

	private BufferedReader br;
	
	public LevelReader(int levelNumber){
		File file = new File("coursework/woollena/levels/Level" + levelNumber + ".lvl");
		System.out.println("File exists: " + file.exists());
		try{
			br = new BufferedReader(new FileReader(file));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the next line
	 * @return nextLine
	 */
	public String getNextLine(){
		String nextLine = null;
		try {
			nextLine = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nextLine;
	}
}
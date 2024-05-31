package comparer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Comparer {
	private static String line1; // Line read on reader1
	private static String line2; // Line read on reader2
	private static BufferedReader reader1; // Reader to read file1
	private static BufferedReader reader2; // Reader to read file2 (The modified file)
	private static StringBuilder output = new StringBuilder(); // Final output that displays all the modifications and their file numbers
	private static StringBuilder newLines = new StringBuilder(); // Added files, used to produce output
	private static StringBuilder deletedLines = new StringBuilder(); // Deleted files, used to produce output
	private static StringBuilder modifiedLines = new StringBuilder(); // Modified files, used to produce output
	private static int lineNum = 0; // Line number

	
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: java Comparer <file1> <file2>");
		}
        String filePath1 = args[0];
        String filePath2 = args[1];
		File file1 = new File(filePath1);
		File file2 = new File(filePath2);
		// Check if files exist and compare them if they do
		if(file1.exists() && file2.exists()) {
			compare(file1, file2);
		} else System.out.println("Files not found");
		// Produce output from assisting stringbuilders
		output.append(newLines.toString() + deletedLines.toString() + modifiedLines.toString());
		writeFile();
		System.out.println(output);
	}
	
	
	private static void writeFile() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("results.txt"));
			writer.write(output.toString());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}


	/*
	 * Function to compare the original file and the modified file. Made to compare JSON,
	 * not tested for other purposes.
	 * file1 = original file
	 * file2 = modified file
	 */
	public static void compare(File file1, File file2) {
		try {
			// Prepare BufferedReaders
			reader1 = new BufferedReader(new FileReader(file1));
			reader2 = new BufferedReader(new FileReader(file2));
			
			// Read first lines of the files
			line1 = reader1.readLine();
			line2 = reader2.readLine();
			
			// Loop through the lines while not null
			while (line1 != null && line2 != null) {
				lineNum++;
				// If read lines are not the same, call handleDifference() and find out if
				// the line is a new line on file2, line has been deleted or line has been modified
				if (!line1.equals(line2)) {
					handleDifference();
				}
				// Read next lines
				line1 = reader1.readLine();
				line2 = reader2.readLine();
				/*
				if (line1 != null && line2.substring(0, line1.length()).equals(line1)) {
					line2 = line2.substring(0, line1.length());
				}
				*/
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/*
	 * Checks if lines have been added, deleted or modified. Prepares assisting StringBuilders
	 * that contain info on the line number with the changes and the text on the changed lines.
	 */
	public static void handleDifference() {
		String tempLine1 = line1;	// Store line1 where difference was detected
		String tempLine2 = line2;	// Store line2 where difference was detected
		Boolean firstLineCompare = true;
		StringBuilder tempNewLines = new StringBuilder("Lines added on line " + lineNum + ":\r\n");
		StringBuilder tempDeletedLines = new StringBuilder("Lines deleted on line " + lineNum + ":\r\n");
		StringBuilder tempModifiedLines = new StringBuilder("Lines modified on line " + lineNum + ":\r\n" + line2);
		try {
			// Mark a spot where the return on the readers after the comparison
			reader1.mark(100000);
			reader2.mark(100000);
			
			// Finds the next matching line after a difference in the files
			while (!line1.equals(tempLine2) && !line2.equals(tempLine1)) {
				lineNum++;
				tempDeletedLines.append(line1 + "\r\n");
				tempNewLines.append(line2 + "\r\n");
				line1 = reader1.readLine();
				line2 = reader2.readLine();
				
				// True when both reader1 and reader2 have the same next line after the difference.
				// That means the line has been modified. Then returns.
				if (firstLineCompare && line1.equals(line2)) {
					modifiedLines.append(tempModifiedLines + "\r\n");
					return;
				}
				
				firstLineCompare = false;
			}
			// True if lines have been deleted on file2
			if (line1.equals(tempLine2)) {
				deletedLines.append(tempDeletedLines);
				reader2.reset();
				return;
			}
			// True if lines have been added to file2
			if (line2.equals(tempLine1)) {
				newLines.append(tempNewLines);
				reader1.reset();
				return;
			}
			System.out.println("Virhe");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

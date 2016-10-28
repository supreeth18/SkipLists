import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeSet;

public class TreeMap {
	public static void main(String[] args) {

		Scanner sc = null;
		String operation = "";
		long operand = 0;
		int modValue = 9907;
		long result = 0;

		if (args.length > 0) {
			File file = new File(args[0]);
			try {
				sc = new Scanner(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			sc = new Scanner(System.in);
		}

		
		TreeSet<Long> tree = new TreeSet<Long>();

		Timer timer = new Timer();
		
		
		while (!((operation = sc.next()).equalsIgnoreCase("End"))) {
			switch (operation) {
			case "Insert":
			case "Add":
			case "add":
				operand = sc.nextLong();
				tree.add(operand);
				result = (result + 1) % modValue;
				break;
			case "Find":
			case "Contains":
				operand = sc.nextLong();
				if (tree.contains(operand)) {
					result = (result + 1) % modValue;
				}
				break;
			case "Delete":
			case "Remove":
				operand = sc.nextLong();
				if (tree.remove(operand)) {
					result = (result + 1) % modValue;
				}
				break;
			}
		}

		
		timer.end();
		
		System.out.println(result);
		System.out.println(timer);
	}

}

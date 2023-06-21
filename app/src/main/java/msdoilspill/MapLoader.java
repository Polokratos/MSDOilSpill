package msdoilspill;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;

public class MapLoader {

    public static void initCells (
            int length, int height, String filepath, Cell[][] destination
        ) throws FileNotFoundException
    {
        int[] types = new int[length * height];
        Scanner scanner = new Scanner(new File(filepath));
        int i = 0;
        while (scanner.hasNextInt() && i < types.length) {
            types[i++] = scanner.nextInt();
        }

        for (int x = 0; x < length; ++x)
            for (int y = 0; y < height; ++y)
				destination[x][y] = new Cell(types[length * y + x]);
    }
}

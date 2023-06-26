package msdoilspill;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class CEVLoader {
    // Loads ONE file. Therefore will be required to be run multiple times as time changes.
    // As I got only one set of (x,y) coords, I put it as current coords.
    // I will check, maybe the Speed and Dir are for wind, and u,v for current?
    public static void LoadToCells(String sourceFile, Cell[][] dest) throws FileNotFoundException,IOException
    {
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));

        for (int y = 0; y < dest.length; y++) {
            String line = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(line,",");
            for(int x = 0; x < 227; x++)
            {
                String token = tokenizer.nextToken();
                String[] values = token.split(";");
                
                dest[y][x].cev.currentX_ms = Double.parseDouble(values[0]);
                dest[y][x].cev.currentY_ms = Double.parseDouble(values[1]);
                dest[y][x].cev.windX_ms    = Double.parseDouble(values[2]);
                dest[y][x].cev.windY_ms    = Double.parseDouble(values[3]);
                
                if(Double.parseDouble(values[0])==0.0) System.out.println(values[0]);
                if(Double.parseDouble(values[1])==0.0) System.out.println(values[1]);
                if(Double.parseDouble(values[2])==0.0) System.out.println(values[2]);
                if(Double.parseDouble(values[3])==0.0) System.out.println(values[3]);

            }
        }
        reader.close();

    }
}

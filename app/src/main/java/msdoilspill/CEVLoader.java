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
        int y = 0;
        while(true)
        {
            String line = reader.readLine();
            if(line == null) break;
            StringTokenizer tokenizer = new StringTokenizer(line,",");

            for(int x = 0; x < tokenizer.countTokens(); x++)
            {
                String token = tokenizer.nextToken();
                String[] values = token.split(";");
                
                dest[x][y].cev.currentX_ms = Double.parseDouble(values[0]);
                dest[x][y].cev.currentY_ms = Double.parseDouble(values[1]);
                dest[x][y].cev.windX_ms = Double.parseDouble(values[2]);
                dest[x][y].cev.windY_ms = Double.parseDouble(values[3]);
            }
            y++;
        }
        reader.close();
    }
}

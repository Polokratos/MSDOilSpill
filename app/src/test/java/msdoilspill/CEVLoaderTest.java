package msdoilspill;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.junit.jupiter.api.Test;

public class CEVLoaderTest {
    @Test public void SimpleLoadTest() throws FileNotFoundException, IOException
    {
        URL url = Thread.currentThread().getContextClassLoader().getResource("CEVLoaderTestCSV.csv");
        String path = url.getPath();
        Cell[][] dst = new Cell[227][400];

        for(int x = 0; x < 227; x++)
            for(int y = 0; y < 400; y++)
            {
                dst[x][y] = new Cell();
            }
        

        CEVLoader.LoadToCells(path, dst);
        //Quick check of the first value
        assert Math.abs(dst[0][0].cev.currentX_ms + 0.03127952059190637) < 0.01;
        assert Math.abs(dst[0][0].cev.currentY_ms + 0.034956164248675016) < 0.01;
        assert Math.abs(dst[0][0].cev.windX_ms + 0.034956245659800475) < 0.01;
        assert Math.abs(dst[0][0].cev.windY_ms + 0.03127942059236447) < 0.01;
    }
}

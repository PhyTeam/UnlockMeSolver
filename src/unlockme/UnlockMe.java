/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unlockme;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bbphuc
 */
public class UnlockMe {

    private static final long MEGABYTE = 1024L * 1024L;
    
    private static final String DFS = "dfs";
    private static final String BFS = "bfs";
    private static final String HCS = "hcs";
    
    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }
    
    public static AbstractSearcher createSearcher(String type){
        //System.out.println(type);
        if(type.equalsIgnoreCase(DFS)){
            return new DepthFirstSearcher();
        }
        if(type.equalsIgnoreCase(BFS))
            return new BreathFirsrSearcher();
        if(type.equalsIgnoreCase(HCS))
            return new ClimbHillSearcher();
        
        // Other algorithm 
        System.err.println("Unsupport algorthm.");
        throw new IllegalArgumentException(type);
    }
    /**
     * @param args the command line arguments
     * @throws java.lang.CloneNotSupportedException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws CloneNotSupportedException, IOException {
        // Start time estimate
        long startTime = System.currentTimeMillis();
        // Parse args
        int count;
        String type = null, testfile = null;
        for(count = 0; count < args.length; count++){
            if(args[count].equalsIgnoreCase("-s")){
                type = args[count+1];
                count++; continue;
            }
            
            if(args[count].equalsIgnoreCase("-f")){
                testfile = args[count + 1];
                count++;
            }
        }
        
        InputStream is;
        if  (testfile == null)
            // pass data by redirect system input 
            is = System.in;
        else {
            // Pass data by file
            File fin = new File(testfile);
            is = new FileInputStream(fin);
        }
        State s = State.loadFromFile(is);
        Searcher searcher = new Searcher();
        searcher.path(s);
        //long memory = Runtime.getRuntime().
        searcher.print();
        // Denote this is end of steps
        System.out.println("END.");
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        
        // Pass elapsed time by System.err
        System.err.print(elapsedTime);
        
        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.err.print(memory);
    }
    
}

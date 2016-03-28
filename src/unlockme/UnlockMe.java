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
import java.nio.file.Paths;

/**
 *
 * @author bbphuc
 */
public class UnlockMe {

    private static final long MEGABYTE = 1024L * 1024L;
    
    private static final String DFS = "dfs";
    private static final String BFS = "bfs";
    private static final String HCS = "hcs";
    private static final String AS = "as";
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
        if(type.equalsIgnoreCase(AS))
            return new AStar();
        if(type.equalsIgnoreCase(HCS))
            return new ClimbHillSearcher();
        
        // Other algorithm 
        System.err.println("Unsupport algorthm.");
        throw new IllegalArgumentException(type);
    }
    
    public static long solve(State state, AbstractSearcher searcher){
        long startTime = System.currentTimeMillis();
        State.count = 0;
        boolean ret = searcher.search(state);
        //if(ret) System.out.printf("LOG: found %d\n", State.count);
        //else System.out.println("Not");
        //long memory = Runtime.getRuntime().
        //searcher.print();
        // Denote this is end of steps
        //System.out.println("END.");
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        
        // Pass elapsed time by System.err
        //System.err.println(elapsedTime);
        
        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        long memory = runtime.totalMemory() - runtime.freeMemory();
        //System.err.print(memory);
        
        // Print result
        System.out.printf("|%d\t|%d\t|%d\t|\n", elapsedTime, State.count, bytesToMegabytes(memory));
        return elapsedTime;
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
        String input_dir = null;
        for(count = 0; count < args.length; count++){
            if(args[count].equalsIgnoreCase("-s")){
                type = args[count+1];
                count++; continue;
            }
            
            if(args[count].equalsIgnoreCase("-f")){
                testfile = args[count + 1];
                count++;
            }
            if(args[count].equalsIgnoreCase("-a")){
                input_dir = args[count + 1];
                count++;
            }
        }
        String okExtension = ".txt";
        if(input_dir != null){
            File folder = Paths.get(input_dir).toFile();
            File[] ts = folder.listFiles((File pathname) -> pathname.isFile() && pathname.getAbsoluteFile()
                .toString().toLowerCase().endsWith(okExtension));
            for(File f : ts){
                System.out.printf("Test: %s\n", f.getName());
                InputStream is = new FileInputStream(f);
                State s = State.loadFromFile(is);
                
                AbstractSearcher bfs = createSearcher(BFS);
                AbstractSearcher dfs = createSearcher(DFS);
                AbstractSearcher as = createSearcher(AS);
                long t1 = solve(s, bfs);
                long t2 = solve(s, dfs);
                long t3 = solve(s, as);
                //System.out.printf("%d | %d | %d\n", t1,t2,t3);
                
            }
            System.exit(0);
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
        AbstractSearcher searcher = createSearcher(type);
        boolean ret = searcher.search(s);
        //long memory = Runtime.getRuntime().
        searcher.print();
        // Denote this is end of steps
        System.out.println("END.");
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        
        // Pass elapsed time by System.err
        System.out.println("t = " + elapsedTime);
        
        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println(memory);
        System.out.println("State createed : " + State.count);
        if(!ret) System.exit(-1);
    }
    
}

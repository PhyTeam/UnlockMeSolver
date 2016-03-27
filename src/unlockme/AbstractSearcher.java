package unlockme;

import java.util.Objects;

/**
 *
 * @author bbphuc
 */
public class AbstractSearcher{

    /**
     * Declare a structure to build search tree for backtrack
     */
    public final class Node implements Comparable<Node>{
        public int level;
        public Node preNode = null;
        public State state;
        
        private int hash;
        /**
         * Constructor
         * @param level the level of this state.
         * @param state 
         * @param prev parent node
         */
        public Node(int level, State state, Node prev) {
            this.level = level;
            this.state = state;
            this.preNode = prev;

            this.hash = getHashCode();
        }
        
        @Override
        public int compareTo(Node o) {
            double t_f = this.level + this.state.getEvaluationValueFromCache();
            double o_f = o.level + o.state.getEvaluationValueFromCache();
            if (t_f <= o_f )
                return -1;
            else if (t_f == o_f) 
                return 0;
            else return 1;
        }
        @Override
        public boolean equals(Object o){
            if(o.getClass() != Node.class)
                return false;
            return this.state.equals(((Node)o).state);
        }
        
        private int getHashCode(){
            return Objects.hashCode(this.state);
        }
        @Override
        public int hashCode() {
            return hash;
        }
    }
    
    // Contain last node which searcher has found
    protected Node last_node;
    
    /***
     * Declare a overriable method to find solution
     * @param state the first state of the game which loaded in a file
     * @return true if there is a solution for this game, false otherwise
     */
    public boolean search(State state){
        return false;
    }
    
    
    /**
     * Print solution to output 
     */
    public void print(){
        print_r(last_node);
    }
    
    protected void print_r(Node node){
        if(node != null){
            print_r(node.preNode);
            String str = String.format("%d %d %d \n", 
                    node.state.getPreIndex(),
                    node.state.getPreX(), 
                    node.state.getPreY());
            if (node.state.getPreIndex() != 0)
                System.out.print(str);
        }
    }
}

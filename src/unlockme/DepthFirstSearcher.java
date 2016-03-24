package unlockme;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author bbphuc
 */
public class DepthFirstSearcher extends AbstractSearcher {
    Stack<Node> stack = new Stack<>();
    HashSet<State> visitedNode = new HashSet<>();
    
    private final int max_depth = 80;
    
    @Override
    public boolean search(State state) {
        Node head = new Node(0, state, null);
        // Add initial state
        stack.add(head);
        while(!stack.isEmpty()) {
            final Node node = stack.pop();
            if (node.state.checkGoal()){
                this.last_node = node;
                // Success
                return true;
            }
            visitedNode.add(node.state);
            // Check current level
            if(node.level > max_depth)
                continue;
            
            // Find out which node is on this path
            Node temp = node;
            HashSet<State> lstVisited = new HashSet<>();
            stack.forEach(n -> lstVisited.add(n.state));
            while(temp != null){
                lstVisited.add(temp.state);
                temp = temp.preNode;
            }
            
            // Generate next states
            List<State> newstates = node.state.getNewState();
            newstates.stream().map((nstate) -> new Node(node.level + 1, nstate, node)).forEach((childNode) -> {
                if(!visitedNode.contains(childNode.state)){
                    visitedNode.add(childNode.state);
                    stack.push(childNode);
                }
                
            });
        }
        return false;
    }
    
}

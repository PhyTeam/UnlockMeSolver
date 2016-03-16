/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unlockme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Unlock me
 * @author bbphuc
 */
public class State implements IState, Cloneable {
    
    public static List<State> visitedState = new ArrayList<>();
    

    private int preX;
    private int preY;
    private int preIndex;
    
    public int getPreIndex(){
        return preIndex;
    };
    
    public int getPreX() {
        return preX;
    }

    public int getPreY() {
        return preY;
    }
    
    public void setPreMove(int idx, int x, int y){
        this.preX = x;
        this.preY = y;
        this.preIndex = idx;
    }
    
    public static class Block implements Cloneable{
        
        int x,y; // Toan do tren cung, ben trai
        int uvx, uvy; // Neu (uvx,uvy) = (1,0) thanh doc, (0,1) la thanh ngang
        int index; // Index trong ma tran
        int l; // Do dai cua thanh
        
        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getUvx() {
            return uvx;
        }

        public int getUvy() {
            return uvy;
        }

        public int getIndex() {
            return index;
        }

        public int getL() {
            return l;
        }

        public Block(int x, int y, int uvx, int uvy, int index, int l) {
            this.x = x;
            this.y = y;
            this.uvx = uvx;
            this.uvy = uvy;
            this.index = index;
            this.l = l;
        }
        
        @Override
        public String toString(){
            PrintStream ps = null;
            try {
                String str = new String();
                ps = new PrintStream(str);
                ps.printf( "(%d,%d) L%d I%d (%d,%d)", x,y,index,l, uvx,uvy);
                return ps.toString();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(State.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                ps.close();
            }
            return "";
        }
    }
    
    protected int mState[][];
    
    protected List<Block> lblock;
    
    public List<Block> test;
    
    public State(){
        mState = new int[6][6];
        lblock = new ArrayList<>();
        test = new ArrayList<>();
    }
    
    protected State move(Block block, int dx, int dy)  {
        // Kiem tra buoc di hop le
        int uvx = block.getUvx(),
            uvy = block.getUvy(),
            delta = dx * uvx + dy * uvy;
        int sig = (delta < 0) ? -1 : 1;
        int x0, y0;
        
        if(delta < 0) { // Di chuyen nguoc ve phia trai hoac di len
            x0 = block.getX();
            y0 = block.getY();        
        } else {
            x0 = block.getX() + (block.getL()-1) * uvx;
            y0 = block.getY() + (block.getL()-1) * uvy;
        }
        
        for (int i = 1; i <= Math.abs(delta); i++) {
            boolean ok;
            boolean conditions = inbound(x0 + i * sig * uvx, 0,5) && inbound(y0 + i * sig * uvy, 0, 5);
            //System.out.println(" IN = " + (y0 + i * sig * uvy));
            if (conditions) 
                ok = this.mState[x0 + i * sig * uvx][ y0 + i * sig * uvy] == 0;
            else ok = false;
            if(!ok) return null;
        }
        
        
        // Generate new state and copy infomation
        State newstate = new State();
        // Save premove
        newstate.setPreMove(block.index, dx * uvx, dy * uvy);
        
        for (int i = 0; i < 6; i++) {
            System.arraycopy(this.mState[i], 0, newstate.mState[i], 0, 6);
        }
        newstate.lblock = new ArrayList<>();
        
        this.lblock.stream().forEach(newstate.lblock::add);
        
        // Sinh trang thai moi
        //System.out.println("Long = " + block.getL());
        for (int i = 0; i < block.getL(); i++) {
            newstate.mState[block.getX() + i * uvx]
                  [block.getY() + i * uvy] = 0;
        }
        for (int i = 0; i < block.getL(); i++) {
            newstate.mState[block.getX() + (dx + i) * uvx]
                  [block.getY() + (dy + i) * uvy] = block.getIndex();
        }
        // Create new block
        Block nblock = new Block(
            block.x, block.y, block.uvx, block.uvy, block.index, block.l
        );
        
        nblock.x = nblock.x + dx * uvx;
        nblock.y = nblock.y + dy * uvy;
        
        Collections.replaceAll(newstate.lblock, block, nblock);
        //System.out.println(newstate);
        return newstate;
    }
    
    
    @Override
    public boolean checkGoal(){
        return mState[2][5] == -1;
    }
    
    @Override
    public List<State> getNewState(){
        List<State> ret = new ArrayList<>();
        
        for (Block block : lblock) {
                //System.out.println(lblock.size());
                State r = null;
                for (int i = 1; ; i++) {
                    r = move(block, i, i);
                    if(r == null) break;
                    else {
                        //System.out.println(r);
                        // Check some thing like that before
                        if (!visitedState.contains(r))
                            // If ok add to result list
                            ret.add(r);
                    }
                }
                for (int i = 1; ; i++) {
                    r = move(block, -i, -i);
                    if(r == null) break;
                    else {
                        if (!visitedState.contains(r))
                            // If ok add to result list
                            ret.add(r);
                    }
                }
        }
        // Add to visited list
        visitedState.addAll(ret);
        // Return result
        return ret;
    }
    
    @Override
    public double evaluationFunction(){
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if(o.getClass() != State.class)
            return false;
        State other = (State)o;
        boolean isEquals;
        
        // Check state array 
        isEquals = Arrays.deepEquals(this.mState, other.mState);
        
        return isEquals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.lblock);
        return hash;
    }
    
    
    
    @Override
    public String toString(){
        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                buff.append(this.mState[i][j]);
                buff.append("\t");
            }
            buff.append("\n");
        }
        return buff.toString();
    }
    
    public static State loadFromFile(InputStream is){
        State state = new State();
        List<Block> lb = state.lblock;
        Scanner scanner = new Scanner(is);
        int i = 0;
        while(scanner.hasNextInt()){
            int value = scanner.nextInt();
            int col = i % 6;
            int row = i / 6;
            state.mState[row][col] = value;
            i++;
        }
        // Xu li du lieu
        boolean checked[][] = new boolean[6][6];
        for (int j = 0; j < 6; j++) {
            for (int k = 0; k < 6; k++) {
                if (checked[j][k])
                    continue;
                checked[j][k] = true;
                if(state.mState[j][k] != 0){
                    int index = state.mState[j][k],
                        count = 1;

                    for (int l = 1; l < 6; l++) {
                        if (inbound(j + l, 0, 5) && (
                                state.mState[j + l][k] == index)){
                            checked[j+l][k] = true;
                            count++;
                        }
                        else break;
                    }

                    if(count > 1){
                        Block b = new Block(j, k, 1, 0, index, count);
                        lb.add(b);
                        continue;
                    }
                    count = 1;
                    for (int l = 1; l < 6; l++) {
                        if (
                                inbound(k + l, 0, 5) && 
                                (state.mState[j][k + l] == index)){
                            checked[j][k + l] = true;
                            count++;
                        }
                        else break;
                    }
                    if(count > 1){
                        Block b = new Block(j, k, 0, 1, index, count);
                        lb.add(b);
                    }
                }
            }
        }
        
        // Return result
        return state;
    }
    
    static boolean inbound(int value, int min, int max){
        return (value >= min && value <= max);
    }
   
    
    
}

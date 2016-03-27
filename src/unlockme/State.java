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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Unlock me
 * @author bbphuc
 */
public class State implements IState, Cloneable {
    
    public static long count;
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
        int uvx, uvy; // Neu (uvx,uvy) = (1,0) thanh doc, (0,1) la thanh ngang
        int index; // Index trong ma tran
        int l; // Do dai cua thanh

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

        public Block(int uvx, int uvy, int index, int l) {
            this.uvx = uvx;
            this.uvy = uvy;
            this.index = index;
            this.l = l;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj.getClass() != Block.class)
                return false;
            Block other = (Block)obj;
            return other.index == this.index;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 79 * hash + this.index;
            return hash;
        }
        
        
        
        @Override
        public String toString(){
            PrintStream ps = null;
            try {
                String str = new String();
                ps = new PrintStream(str);
                ps.printf( "L%d I%d (%d,%d)",index,l, uvx,uvy);
                return ps.toString();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(State.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                ps.close();
            }
            return "";
        }
    }
    
    public static class Point implements Cloneable{
        public int x;
        public int y;

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Point(int x, int y) {
            this.x = x; this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj.getClass() != Point.class)
                return false;
            return ((Point)obj).x == this.x && ((Point)obj).y == this.y;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 79 * hash + this.x;
            hash = 79 * hash + this.y;
            return hash;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return new Point(this.x, this.y);
        }
        
        
        
    }
    //protected int mState[][];
    
    protected int mboard[];
    
    private final int mask = 0x1F;
   
    private void set(int value, int x, int y){
        int offset = 5 * y;
        int v = (value << offset) & (mask << offset) ;
        mboard[x] = (v | (mboard[x] & ~(mask << offset)));
    }
    
    private int get(int x, int y){
        int val =  (mboard[x] >> ( y * 5 )) & mask;
        if(val == mask)
            return -1;
        else return val;
    }
    
    public static void main(String[] args){
        State state = new State();
        state.set(-1, 1, 2);
        System.out.println(state.get(1, 2));
        state.set(2,1,5);
        System.out.println(state.get(1, 5));
        state.set(4,1,4);
        System.out.println(state.get(1,2));
    }
    
    protected List<Block> lblock;
    
    protected Map<Block, Point> mblocks;
    
    public State(){
        count++;
        //mState = new int[6][6];
        mblocks = new HashMap<>();
        mboard = new int[6];
    }
    
    protected State move(Block block, int dx, int dy)  {
        // Kiem tra buoc di hop le
        int uvx = block.getUvx(),
            uvy = block.getUvy(),
            delta = dx * uvx + dy * uvy;
        int sig = (delta < 0) ? -1 : 1;
        int x0, y0;
        
        Point pos = mblocks.get(block);
        if(delta < 0) { // Di chuyen nguoc ve phia trai hoac di len
            x0 = pos.getX();
            y0 = pos.getY();        
        } else {
            x0 = pos.getX() + (block.getL()-1) * uvx;
            y0 = pos.getY() + (block.getL()-1) * uvy;
        }
        
        for (int i = 1; i <= Math.abs(delta); i++) {
            boolean ok;
            boolean conditions = inbound(x0 + i * sig * uvx, 0,5) && inbound(y0 + i * sig * uvy, 0, 5);
            //System.out.println(" IN = " + (y0 + i * sig * uvy));
            if (conditions) 
                //ok = this.mState[x0 + i * sig * uvx][ y0 + i * sig * uvy] == 0;
                ok = this.get(x0 + i * sig * uvx,y0 + i * sig * uvy) == 0;
            else ok = false;
            if(!ok) return null;
        }
        
        
        // Generate new state and copy infomation
        State newstate = new State();
        // Save premove
        newstate.setPreMove(block.index, dx * uvx, dy * uvy);
        
        System.arraycopy(this.mboard, 0, newstate.mboard, 0, 6);
        //for (int i = 0; i < 6; i++) {
        //    System.arraycopy(this.mState[i], 0, newstate.mState[i], 0, 6);
        //}
        newstate.lblock = this.lblock;
        
        // Sinh trang thai moi
        for (int i = 0; i < block.getL(); i++) {
            //newstate.mState[pos.getX() + i * uvx]
             //     [pos.getY() + i * uvy] = 0;
            newstate.set(0, pos.getX() + i * uvx, pos.getY() + i * uvy);
        }
        for (int i = 0; i < block.getL(); i++) {
            //newstate.mState[pos.getX() + (dx + i) * uvx]
            //    [pos.getY() + (dy + i) * uvy] = block.getIndex();
            newstate.set(block.getIndex(), pos.getX() + (dx + i) * uvx, pos.getY() + (dy + i) * uvy);
        }
        // Create new block
        int nx = pos.x + dx * uvx;
        int ny = pos.y + dy * uvy;
        Point newPosition = new Point(nx,ny);
        
        this.mblocks.forEach((Block t, Point u) -> {
            try {
                newstate.mblocks.put(t, (Point)u.clone());
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(State.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        // Replace old position
        newstate.mblocks.put(block, newPosition);
        
        return newstate;
    }
    
    
    @Override
    public boolean checkGoal(){
        //return mState[2][5] == -1;
        return this.get(2, 5) == -1;
        
    }
    
    @Override
    public List<State> getNewState(){
        List<State> ret = new ArrayList<>();
        
        for (Block block : lblock) {
                State r = null;
                for (int i = 1; ; i++) {
                    r = move(block, i, i);
                    if(r == null) break;
                    else {
                        //System.out.println(r);
                        ret.add(r);
                    }
                }
                for (int i = 1; ; i++) {
                    r = move(block, -i, -i);
                    if(r == null) break;
                    else {
                        ret.add(r);
                    }
                }
        }
        // Return result
        return ret;
    }
    
    private double eval(int index, int row, int col, int level){
        if (level > 5) return 0;
        Block cur = null;
        double res = 0, resl = 0;
        for (Block block : lblock){
            if (block.index == index) cur = block;
        }
        // test
        
        // truong hop tranh' cot
        if ( row == -1){
            
            int a_left = col, a_right = 5 - col;
            if (a_left < cur.l && a_right < cur.l) return -1;    // khong the di chuyen
            else {
                // co the dich trai
                //double resl = 0;
                if (a_left >= cur.l){
                    for (int i = mblocks.get(cur).y- 1; i >= col - cur.l; i--){
                        if (this.get(mblocks.get(cur).x,i) != 0){
                            resl++;
                            double n = eval(this.get(mblocks.get(cur).x,i),mblocks.get(cur).x, -1, level + 1);
                            if (n == -1) {
                                resl = -1;
                                break;
                            }
                            resl += n;
                        }
                    }
                    // neu ben trai khong he dung do -> return
                    if ( resl == 0) return 0;
                }
                else resl = -1;  // khong the dich trai

                // co the dich phai
                if (a_right >= cur.l){
                    for (int i = mblocks.get(cur).y + cur.l; i <= col + cur.l ; i++){
                        if (this.get(mblocks.get(cur).x,i) != 0){
                            res++;
                            double n = eval(this.get(mblocks.get(cur).x,i),mblocks.get(cur).x, -1, level + 1);
                            if (n == -1) {
                                res = -1;
                                break;
                            }
                            res += n;
                        }
                    }
                    // neu ben phai khong he dung do -> return
                    if ( res == 0) return 0;
                }
                else res = -1;   // khong the dich phai
            }
        }
        // truong hop tranh hang
        else if (col == -1){
            int a_up = row, a_down = 5 - row;
            if (a_up < cur.l && a_down < cur.l) return -1;    // khong the di chuyen
            else {
                // co the dich len
                //double resl = 0;
                if (a_up >= cur.l){
                    for (int i = mblocks.get(cur).x - 1; i >= row - cur.l; i--){
                        if (this.get(i,mblocks.get(cur).y) != 0){
                            resl++;
                            double n = eval(this.get(i,mblocks.get(cur).y),-1, mblocks.get(cur).y, level + 1);
                            if (n == -1) {
                                resl = -1;
                                break;
                            }
                            resl += n;
                        }
                    }
                    // neu ben tren khong he dung do -> return
                    if ( resl == 0) return 0;
                }
                else resl = -1;  // khong the dich len
                
                // co the dich phai
                if (a_down >= cur.l){
                    for (int i = mblocks.get(cur).x + cur.l; i <= row + cur.l ; i++){
                        if (this.get(i,mblocks.get(cur).y) != 0){
                            //System.out.print(this);
                            //System.out.println(i + " " + this.mState[i][cur.y]);
                            res++;
                            double n = eval(this.get(i,mblocks.get(cur).y),-1, mblocks.get(cur).y, level + 1);
                            if (n == -1) {
                                res = -1;
                                break;
                            }
                            res += n;
                        }
                    }
                    if ( res == 0) return 0;
                }
                else res = -1;   // khong the dich xuong
            }
        }
        if ( res == -1) return resl;
        else if (resl == -1) return res;
        else if (res > resl) return resl;
        else return resl;
    }
    
    @Override
    public double evaluationFunction(){
        // TODO
        double res = 0;
        int i;
        Block head = null;
        // find the head
        for (Block block : lblock){
            if (block.index == -1) {
                head = block;
                break;
            }
        }
        //System.out.println(head.index);
        //System.out.println("head.x = " + head.x + "  head.l = " + head.l);
        for (i = mblocks.get(head).y + head.l; i <= 5; i++){
            if (this.get(2,i) != 0){
                double n = eval(this.get(2,i),2,-1,0);
                res = res + 1 + n;
            }
        }
        return res;
    }
    
  /* 
    @Override
    public double evaluationFunction(){
        double ret = 0;
        Block head = null;
       
        // find the head
        for (Block block : lblock){
            if (block.index == -1) {
                head = block;
                break;
            }
        }
        for (int i = mblocks.get(head).y + head.l; i <= 5; i++){
            //if (this.mState[2][i] != 0){
            if (this.get(2, i) != 0){
                ret++;
            }
        }
        return ret + 2;
    }
*/
    @Override
    public boolean equals(Object o) {
        if(o.getClass() != State.class)
            return false;
        State other = (State)o;
        boolean isEquals;
        //isEquals = this.mblocks.equals(other.mblocks);
        isEquals = Arrays.equals(this.mboard, other.mboard);
        
        return isEquals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.mblocks);
        return hash;
    }
    
    
    
    @Override
    public String toString(){
        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                //buff.append(this.mState[i][j]);
                buff.append(this.get(i, j));
                buff.append("\t");
            }
            buff.append("\n");
        }
        return buff.toString();
    }
    
    public static State loadFromFile(InputStream is){
        State state = new State();
        state.lblock = new ArrayList<>();
        List<Block> lb = state.lblock;
        Scanner scanner = new Scanner(is);
        int i = 0;
        while(scanner.hasNextInt()){
            int value = scanner.nextInt();
            int col = i % 6;
            int row = i / 6;
            //state.mState[row][col] = value;
            state.set(value, row, col);
            i++;
        }
        
        // Xu li du lieu
        boolean checked[][] = new boolean[6][6];
        for (int j = 0; j < 6; j++) {
            for (int k = 0; k < 6; k++) {
                int count = 0;
                if (checked[j][k])
                    continue;
                checked[j][k] = true;
                if(state.get(j,k) != 0){
                    int index = state.get(j,k);
                        count = 1;

                    for (int l = 1; l < 6; l++) {
                        if (inbound(j + l, 0, 5) && (
                                state.get(j + l,k) == index)){
                            checked[j+l][k] = true;
                            count++;
                        }
                        else break;
                    }

                    if(count > 1){
                        Block b = new Block(1, 0, index, count);
                        Point pos = new Point(j,k);
                        lb.add(b);
                        // Add to map
                        state.mblocks.put(b, pos);
                        continue;
                    }
                    count = 1;
                    for (int l = 1; l < 6; l++) {
                        if (
                                inbound(k + l, 0, 5) && 
                                (state.get(j,k + l) == index)){
                            checked[j][k + l] = true;
                            count++;
                        }
                        else break;
                    }
                    if(count > 1){
                        Block b = new Block(0, 1, index, count);
                        lb.add(b);
                        Point pos = new Point(j,k);
                        state.mblocks.put(b, pos);
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

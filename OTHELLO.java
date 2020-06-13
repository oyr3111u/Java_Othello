import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class OTHELLO implements MouseListener{
    OTHELLOmodel m;
    OTHELLOview  v;
    OTHELLO(){
        m = new OTHELLOmodel();
        v = new OTHELLOview(m);
        v.addMouseListener(this);
        Frame f = new Frame();
        f.add(v); f.pack(); f.setVisible(true);
    }
    public void mouseExited(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseClicked(MouseEvent e){
        int bx = v.getBrdX(e);
        int by = v.getBrdY(e);
        m.play(bx,by);
        if(m.game != -1){ gameOver();}
        v.repaint();
    }
    private void gameOver(){
        if(m.game == 0){ v.setMessage("DRAW"); }
        if(m.game == 1){ v.setMessage("Black WIN"); }
        if(m.game == 2){ v.setMessage("White WIN"); }
    }
    public static void main(String args[]){
        OTHELLO othello = new OTHELLO();
    }
}

class OTHELLOmodel{
    int board[][];
    int turn;
    int game;

    OTHELLOmodel(){
        board = new int[8][8];
        for(int i=0 ; i<8 ; i++){
            for(int j=0 ; j<8 ; j++){
                board[i][j] = 0;
            }
        }
	board[3][4] = board[4][3] = 1;
        board[3][3] = board[4][4] = 2;
        turn = 1;
        game = -1;
    }
    public boolean isValidMove(int x, int y){
        if((game != -1)||(x<0)||(8<=x)||(y<0)||(8<=y)){
            return false;
        }
        return ( board[x][y] == 0);
    }
    public void play(int x, int y){
        if(!isValidMove(x,y)) return;
        if(canPut(x,y,turn)){
            put(x,y,turn);
            turn = opponent(turn);
            if(countMove(turn) == 0){
                turn = opponent(turn);
            }
        }
	judgeGame();
    }
    public int opponent(int turn){
        if(turn == 1){
            return 2;
        } else {
            return 1;
        }
    }
    public boolean canPut(int x, int y, int turn){
        if(board[x][y] != 0) return false;
        int vx[] = {-1,-1,-1,0,0,1,1,1};
        int vy[] = {-1,0,1,-1,1,-1,0,1};
        for(int i=0 ; i<8 ; i++){
            if(countFlip(x,y,vx[i],vy[i],turn) > 0) return true;
        }
        return false;
    }
    public int countMove(int turn){
        int count = 0;
        for(int i=0; i<8 ;i++){
            for(int j=0 ; j<8 ; j++){
                if(canPut(i,j,turn)){
                    count++;
                }
            }
        }
        return count;
    }
    public int countStone(int sc){
        int count = 0;
        for(int i=0 ; i<8 ; i++){
            for(int j=0 ; j<8 ; j++){
                if(board[i][j] == sc){
                    count++;
                }
            }
        }
        return count;
    }
    public int countFlip(int x, int y, int vx, int vy,int turn){
        int count = 0;
        int xi = x + vx;
        int yi = y + vy;
        while((0<=xi)&&(xi<8)&&(0<=yi)&&(yi<8)){
            if(board[xi][yi] == opponent(turn)){
                count++;
            } else if(board[xi][yi] == turn){
                return count;
            } else {
                return 0;
            }
            xi += vx;
	    yi += vy;
        }
        return 0;
    }
    public void put(int x, int y,int turn){
        int vx[] = {-1,-1,-1,0,0,1,1,1};
        int vy[] = {-1,0,1,-1,1,-1,0,1};
        board[x][y] = turn;
        for(int i=0 ; i<8 ; i++){
            int count = countFlip(x,y,vx[i],vy[i],turn);
            if(count>0){
                flip(x,y,vx[i],vy[i],count);
            }
        }
    }
    public void flip(int x, int y, int vx, int vy, int count){
        int xi = x + vx;
        int yi = y + vy;
        for(int i=0 ; i<count ; i++){
            board[xi][yi] = opponent(board[xi][yi]);
            xi += vx;
            yi += vy;
        }
    }
    public int judgeGame(){
        if((countMove(1) == 0)&&(countMove(2) == 0)){
            int bn = countStone(1);
            int wn = countStone(2);
            if(bn>wn){
                game = 1;
                return game;
            } else if(bn<wn){
                game = 2;
                return game;
            } else{
                game = 0;
                return game;
            }
        }
        return game;
    }
}

class OTHELLOview extends Canvas{
    OTHELLOmodel model;
    int pointedX, pointedY;
    String message = null;
    BufferedImage cell[];

    private Color backGround = new Color(51,153,102);
    private Color lineColor  = new Color(0,0,0);

    public OTHELLOview(OTHELLOmodel model){
        this.model = model;
        setSize(1000,1000);
        pointedX = pointedY = -1;
        cell = new BufferedImage[3];
        try{
            for(int i=0; i<3 ; i++){
                cell[i] = ImageIO.read(new File("othello"+i+".png"));
            }
        } catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
    public int getBrdX(MouseEvent e){
        int gx = e.getX();
        if((100<=gx)&&(gx<900)){
            return (gx - 100) / 100;
        }
        return -1;
    }
    public int getBrdY(MouseEvent e){
        int gy = e.getY();
        if((100<=gy)&&(gy<900)){
            return (gy - 100) / 100;
        }
        return -1;
    }
    public void paint(Graphics gc){
        gc.setColor(backGround);
        gc.fillRect(0,0,1000,1000);
        gc.setColor(lineColor);
        for(int i=0; i<9 ; i++){
            gc.drawLine(100,100+i*100,900,100+i*100);
            gc.drawLine(100+i*100,100,100+i*100,900);
        }
        for(int i=0 ; i<8 ; i++){
            for(int j=0 ; j<8 ; j++){
                gc.drawImage(cell[model.board[i][j]],i*100+100,j*100+100,this);
            }
        }
        if(message != null){
            gc.setColor(Color.red);
            gc.setFont(new Font("Default",Font.PLAIN,200));
            gc.drawString(message,10,500);
        }
    }
    public void setMessage(String mes){
        message = mes;
    }
}
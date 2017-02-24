package com.mytetris;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalSliderUI;

import org.omg.CORBA.PUBLIC_MEMBER;

import com.mytetris.Shape;
import com.mytetris.Shape.Tetrominos;

public class Board extends JPanel implements ActionListener {

	public  int BOARD_WIDTH = 20;
	public  int BOARD_HEIGHT = 43;
	private static final Color [] COLORS ={new Color(88, 45, 163), new Color(163, 167, 45), Color.blue, Color.yellow, Color.ORANGE, Color.red, new Color(45, 163, 65), new Color(163, 45, 149)};
	private Timer timer;
	private boolean isFallingFinished = false;
	private boolean isStarted = false;
	private boolean isPaused = false;
	//private int numLinesMoved = 0;
	private int curX = 0;
	private int curY = 0;
	//private JLabel statusBar;
	private Shape prevPiece = newPrevPiece();
	private Shape curPiece;
	private Tetrominos[] board;
	private float canvasSide = 200,
			mainX = 0, mainY = 0, mainWidth = 100, mainHeight = 200,
			nextX = 140, nextY = 0, nextSide = 60,
			quitX = 140, quitY = 180, quitWidth = 60, quitHeight = 20,
			pauseX = 20, pauseY = 90, pauseWidth = 60, pauseHeight = 20,
			linesX = 140, linesY = 100,
			fontSize = 16;
	private int side,width,height,remainX,remainY;
	private int piece;
	private int lines = 0;
	private int score = 0;
	private int level = 1;
	//score factor 1-10
	 int M = 1;
	//number of rows required to level up
	 int N = 20;
	//speed factor from 0.1 - 1.0
	private float S = 0.1F;
	private int delay = 500;
	private JSlider mSlider;
	private JSlider nSlider;
	private JSlider sJSlider;

	
	public Board(Tetris parent) {
		// TODO Auto-generated constructor stub
		setFocusable(true);
		curPiece = new Shape();
		timer = new Timer(delay, this);
		//statusBar = parent.getStatusBar();
		mSlider = parent.getMSlider();
		mSlider.setMaximum(10);
		mSlider.setMinimum(1);
		mSlider.setValue(1);
		mSlider.setMajorTickSpacing(1);
		mSlider.setPaintTicks(true);
		mSlider.setPaintLabels(true);
		mSlider.addChangeListener(new MChageListener());
		mSlider.setName("M");
		
		nSlider = parent.getNSlider();
		nSlider.setMaximum(50);
		nSlider.setMinimum(20);
		nSlider.setValue(20);
		nSlider.setMajorTickSpacing(5);
		nSlider.setMinorTickSpacing(1);
		nSlider.setPaintTicks(true);
		nSlider.setPaintLabels(true);
		nSlider.addChangeListener(new NChangeListener());
		
		sJSlider = parent.getSSlider();
		sJSlider.setMinimum(1);
		sJSlider.setMaximum(10);
		sJSlider.setValue(1);
		sJSlider.setMajorTickSpacing(1);
		sJSlider.setPaintTicks(true);
		sJSlider.setPaintLabels(true);
		sJSlider.addChangeListener(new SChangeListener());
	
		JComboBox<String> box = parent.getBox();
		box.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String hardness = (String)box.getSelectedItem();
				
					if (hardness.equals("Easy:10*20")) {
						
						BOARD_WIDTH = 10;
						BOARD_HEIGHT = 23;
						lines=0;
						level=1;
						score=0;
						start();
					} else if (hardness.equals("Normal:15*30")) {
						
						BOARD_WIDTH= 15;
						BOARD_HEIGHT = 33;
						lines=0;
						level=1;
						score=0;
						start();
					} 
					else if(hardness.equals("Hard:20*40" )) {
						
						BOARD_WIDTH = 20;
						BOARD_HEIGHT = 43;
						lines=0;
						level=1;
						score=0;
						start();
					}
			}
		});
		
		board = new Tetrominos[BOARD_WIDTH*BOARD_HEIGHT];
		clearBoard();
		addMouseWheelListener(new MyTetrisAdapter());
		addMouseListener(new LeftRightAdapter());
		addMouseMotionListener(new PauseAdapter());
		addMouseMotionListener(new ReshapeAdapter());
	}
	

	public int squareWidth(){
		return piece;
	}
	
	public int squareHeight(){
		return piece;
	}
	
	public Tetrominos shapeAt(int x, int y){
		return board[y * BOARD_WIDTH + x];
	}
	
	public void clearBoard(){
		for(int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++)
		{
			board[i] = Tetrominos.NoShape;
		}
	}
	
	private void pieceDropped() {
		for(int i = 0; i < 4; i++){
			int x = curX + curPiece.x(i);
			int y = curY - curPiece.y(i);
			board[y * BOARD_WIDTH + x] = curPiece.getShape();
		}
		
		removeFullLines();
		
		if(!isFallingFinished){
			newPiece();
		}
	}
	
	public Shape newPrevPiece(){
		Shape prev = new Shape();
		prev.setRandomShape();
		return prev;
	}
	
	public void newPiece() {
		curPiece = prevPiece;

		if(isStarted)
		{
		prevPiece = newPrevPiece();
		}
		
		curX = BOARD_WIDTH / 2 ;
		curY = BOARD_HEIGHT - 1 + curPiece.minY();
		
		if(!tryMove(curPiece, curX, curY - 1) ){
			
			curPiece.setShape(Tetrominos.NoShape);
			timer.stop();
			isStarted = false;
			//statusBar.setText("Game Over");
		}
	}
	
	private void oneLineDown(){
		if(!tryMove(curPiece, curX, curY - 1))
		pieceDropped();
	}
	
	@Override
	public void actionPerformed(ActionEvent e){
		if(isFallingFinished){
			isFallingFinished = false;
			newPiece();
		}else{
			oneLineDown();
		}
	}
		
	//draw tetrominos
	private void drawSquare(Graphics g, int x, int y, Tetrominos shape ){
		Color color = COLORS[shape.ordinal()];
		 g.setColor(color);
		 g.fillRect(x + 1, y + 1, squareWidth() - 1, squareHeight() - 1);
		 g.setColor(Color.black);
		 g.drawRect(x, y, squareWidth(), squareHeight());	 
	}
	private void drawSquare2(Graphics g, int x, int y, Tetrominos shape ){
		Color color = COLORS[shape.ordinal()];
		 g.setColor(color);
		 g.fillRect(x + 1, y + 1, side/20 - 1, side/20 - 1);
		 g.setColor(Color.black);
		 g.drawRect(x, y, side/20, side/20);	 
	}
	
	public int sideToDevice(float length){
		return Math.round(side*length/canvasSide);
	}
	
	public int xToDevice(float x){
		//return (int)(remainX + side/canvasSide*x);
		return remainX + Math.round(side*x/canvasSide);
	}
	
	public int yToDevice(float y){
		//return (int)(remainY + side/canvasSide*y);
		return remainY + Math.round(side*y/canvasSide);
	}
	
	public void init(){
		Dimension dimension = getSize();
		width = dimension.width - 1;
		height = dimension.height - 1;
		//side = (int)Math.round(0.9*Math.min(width, height));
		side = Math.round(0.9F*Math.min(width, height));
		remainX = (width-side)/2;
		remainY = (height - side)/2;
		
		piece = Math.round(side/(BOARD_HEIGHT -3));

	}
	
	//paint on board
	@Override
	public void paint(Graphics g){
		
		super.paint(g);
		
		init();
		//g.drawRect(xToDevice(mainX), yToDevice(mainY), 10*piece, 20*piece);
		g.drawRect(xToDevice(mainX), yToDevice(mainY), BOARD_WIDTH*piece, (BOARD_HEIGHT-3)*piece);
		g.drawRect(xToDevice(nextX), yToDevice(nextY), sideToDevice(nextSide), sideToDevice(nextSide));
		//g.drawRect(xToDevice(quitX), remainY + 18*piece, sideToDevice(quitWidth), 2*piece);
		g.drawRect(xToDevice(quitX), remainY + 18*piece*(BOARD_HEIGHT-3)/20, sideToDevice(quitWidth), 2*piece*(BOARD_HEIGHT-3)/20);
		g.setFont(new Font("Arial", 1, sideToDevice(fontSize)));
		//g.drawString("QUIT", xToDevice(quitX + 9), remainY + 20*piece -6);
		g.drawString("QUIT", xToDevice(quitX + 9), remainY + 20*piece*(BOARD_HEIGHT-3)/20 -6);
		g.setFont(new Font("Arial", 1, sideToDevice(10)));
		g.drawString("Lines: "+String.valueOf(lines), xToDevice(linesX), yToDevice(linesY));
		g.drawString("Level: "+String.valueOf(level), xToDevice(linesX), yToDevice(linesY - 10));
		g.drawString("Score: "+String.valueOf(score), xToDevice(linesX), yToDevice(linesY + 10));
		
		
		if(isPaused){
			g.setColor(Color.blue);
			g.drawRect(xToDevice(pauseX), yToDevice(pauseY), sideToDevice(pauseWidth), sideToDevice(pauseHeight));
			g.drawString("PAUSE", xToDevice(pauseX + 12), yToDevice(pauseY + pauseHeight-6));
		/*	//test
			curPiece = newPrevPiece();
			prevPiece = newPrevPiece();*/
			repaint();
		}
		
		int boardTop = remainY - (int)2*sideToDevice(100/BOARD_WIDTH);
		
		for(int i = 0; i< BOARD_HEIGHT; i++){
			for(int j = 0; j < BOARD_WIDTH; ++j){
				Tetrominos shape = shapeAt(j, BOARD_HEIGHT - i - 1);
				
				if(shape != Tetrominos.NoShape){
					//drawSquare(g, remainX+j*squareWidth(), boardTop + i*squareHeight(), shape);
					//drawSquare(g, remainX+j*squareWidth(), boardTop + (i - 1)*(sideToDevice(mainHeight)/20), shape);	
					drawSquare(g, remainX+j*squareWidth(), boardTop + (i - 1)*(sideToDevice(mainHeight)/(BOARD_HEIGHT-3)), shape);	
				}
				
			/*	if(prevPiece.getShape() != Tetrominos.NoShape)
				{temp = prevPiece;
				drawSquare(g, xToDevice(nextX  + 25)+j*squareWidth(), yToDevice(nextY + 10) + i*(sideToDevice(mainHeight)/20), temp.getShape());}*/
				
			}
		}
		
/*		if(!isStarted)
		{
			for(int i = 0; i<4; ++i)
			{
				int x = curX + prevPiece.x(i);
				int y = curY - prevPiece.y(i);
				//drawSquare(g, remainX+x*squareWidth(), boardTop + (BOARD_HEIGHT - y - 1)*squareHeight(), curPiece.getShape());
				drawSquare(g, remainX+x*squareWidth(), boardTop + (BOARD_HEIGHT - y - 1)*(sideToDevice(mainHeight)/20), prevPiece.getShape());	
			}	
		}*/
		
		if(curPiece.getShape() != Tetrominos.NoShape){
			for(int i = 0; i<4; ++i)
			{
				int x = curX + curPiece.x(i);
				int y = curY - curPiece.y(i);
				//drawSquare(g, remainX+x*squareWidth(), boardTop + (BOARD_HEIGHT - y - 1)*squareHeight(), curPiece.getShape());
				//drawSquare(g, remainX+x*squareWidth(), boardTop + (BOARD_HEIGHT - y - 2 )*(sideToDevice(mainHeight)/20), curPiece.getShape());
				drawSquare(g, remainX+x*squareWidth(), boardTop + (BOARD_HEIGHT - y - 2 )*(sideToDevice(mainHeight)/(BOARD_HEIGHT - 3)), curPiece.getShape());
			}	
		}	
		
		if(prevPiece.getShape() != Tetrominos.NoShape){
			for(int i = 0; i<4; ++i)
			{
				int x1 = prevPiece.x(i);
				int y1 = - prevPiece.y(i);
				//drawSquare(g, remainX+x*squareWidth(), boardTop + (BOARD_HEIGHT - y - 1)*squareHeight(), curPiece.getShape());
				//drawSquare(g, xToDevice(nextX  + 25)+x1*squareWidth(), yToDevice(nextY + 10) + ( - y1 + 1)*(sideToDevice(mainWidth)/10), prevPiece.getShape());
				drawSquare2(g, xToDevice(nextX  + 25)+x1*(side/20), yToDevice(nextY + 10) + ( - y1 + 1)*(sideToDevice(mainWidth)/10), prevPiece.getShape());
			}
		}
		
		g.setColor(Color.white);
		g.fillRect(0, 0, width, remainY);
	}
	
	public void start(){
		if(isPaused)
			return;
		
		isStarted = true;
		isFallingFinished = false;
		//numLinesMoved = 0;
		clearBoard();
		newPiece();
		timer.start();
	} 
	
	public void stop(){
		if(isStarted){
			curPiece.setShape(Shape.Tetrominos.NoShape);
			prevPiece.setShape(Shape.Tetrominos.NoShape);
			level=0;
			score=0;
			lines = 0;
			repaint();
			timer.stop();
		}
	}
	
	public void pause(){
		if(!isStarted)
			return;
		
		isPaused = !isPaused;
		
		if(isPaused){
			timer.stop();
			
		}else{
			timer.start();
			
			//lines = numLinesMoved;
			repaint();
		}
	}
	
	private boolean tryMove(Shape newPiece, int newX, int newY){
		for(int i = 0 ; i < 4; ++i){
			int x = newX + newPiece.x(i);
			int y = newY - newPiece.y(i);
			
			if(x < 0 || x >= BOARD_WIDTH || y < 0 || y > BOARD_HEIGHT)
				{
					return false;
				}

			 if(shapeAt(x, y) != Tetrominos.NoShape)
				return false;
		}
		
		curPiece = newPiece;
		curX = newX;
		curY = newY;
		
		repaint();
		
		return true;
	}
	
	private boolean tryChange(Shape temp, int newX, int newY){
		for(int i = 0 ; i < 4; ++i){
			int x = newX + temp.x(i);
			int y = newY - temp.y(i);
			
			if(x < 0 || x >= BOARD_WIDTH || y < 0 )
				{
					return false;
				}

			 if(shapeAt(x, y) != Tetrominos.NoShape)
				return false;
		}
	
		return true;
	}
	
	private void removeFullLines(){
		int numFullLines =0;
		
		for(int i = BOARD_HEIGHT - 1; i >= 0; --i){
			boolean lineIsFull = true;
			
			for(int j = 0; j < BOARD_WIDTH; ++j){
				if(shapeAt(j, i) == Tetrominos.NoShape){
					lineIsFull = false;
					break;
				}
			}
			
			if(lineIsFull){
				++numFullLines;
				++lines;
				score = score + level * M;
				//each time N lines dropped, level up, speed up
				if(lines % N == 0 && lines != 0)
				{
					level ++;
					//@parameter speed, is actually a time gap between each falling
					//speed = time-gap-base (500ms) / FS
					float FS=(level - 1)*S +1;
					delay = Math.round(500F / FS);
					timer.setDelay(delay);
				}
				
				for(int k = i; k < BOARD_HEIGHT - 1; ++k){
					for(int j = 0; j < BOARD_WIDTH; ++j){
						board[k * BOARD_WIDTH + j] = shapeAt(j, k+1);
					}
				}
			}
			
		}
		

		if(numFullLines > 0){
			 /*for(int k= 0; k < numFullLines; k++){
				 lines ++;
			 }
			score = score + level*M;*/
			//statusBar.setText(String.valueOf(numLinesMoved));
			isFallingFinished = true;
			curPiece.setShape(Tetrominos.NoShape);
			repaint();
		}
			
	}
	
/*	private void dropDown(){
		int newY = curY;
		
		while(newY > 0){
			if(!tryMove(curPiece, curX, newY - 1))
				{break;}
			
			--newY;
		}
		
		pieceDropped();
	}*/
	
/*	class MyTetrisAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			if(!isStarted || curPiece.getShape() == Tetrominos.NoShape){
				return;
			}
			
			int keyCode = e.getKeyCode();
			
			if(keyCode == 'p' || keyCode == 'P'){
				pause();
			}
			
			if(isPaused)
				return;
			
			switch (keyCode) {
			case KeyEvent.VK_LEFT:
				tryMove(curPiece, curX - 1, curY);
				break;
			case KeyEvent.VK_RIGHT:
				tryMove(curPiece, curX + 1, curY);
				break;
			case KeyEvent.VK_DOWN:
				tryMove(curPiece.rotateLeft(), curX, curY);
				break;
			case KeyEvent.VK_UP:
				tryMove(curPiece.rotateRight(), curX, curY);
				break;
			case KeyEvent.VK_SPACE:
				dropDown();
				break;
			case 'd':
				oneLineDown();
				break;
			case 'D':
				oneLineDown();
				break;
				

			default:
				break;
			}
		}
	}*/

	
	class MyTetrisAdapter extends MouseAdapter{
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			// TODO Auto-generated method stub
			super.mouseWheelMoved(e);
			int roll = e.getWheelRotation();
			if(roll < 0 && !isPaused)
			{
				tryMove(curPiece.rotateRight(), curX, curY);
			}
			if(roll > 0 && !isPaused)
			{
				tryMove(curPiece.rotateLeft(), curX, curY);
			}
		}
	}

	class PauseAdapter extends MouseAdapter{
		
		@Override
		public void mouseMoved(MouseEvent e) {
			int mainXmin = xToDevice(mainX);
			int mainXmax = xToDevice(mainX + mainWidth);
			int mainYmin = yToDevice(mainY);
			int mainYmax = yToDevice(mainY + mainHeight);
			
			int x = e.getX();
			int y = e.getY();
			
			if(x > mainXmin && x < mainXmax && y > mainYmin && y < mainYmax){
				if(!isPaused)
				pause();
				if(isStarted)
				repaint();
			}
			else{
				if(isPaused)
					pause();
				if(isStarted)
				repaint();
			}
		}
	}
	
	//change shape when pausing and cursor moved into shape
	class ReshapeAdapter extends MouseAdapter{
		boolean changed = false;
		boolean inside = false;
		@Override
		public void mouseMoved(MouseEvent e){
			int x = e.getX();
			int y = e.getY();
			
			int boardTop = remainY - (int)2*sideToDevice(100/BOARD_WIDTH);
			
			if(isPaused ){
				if(curPiece.getShape() != Tetrominos.NoShape && !changed){

					for(int i = 0; i<4 ; ++i)
					{
						int xT = curX + curPiece.x(i);
						int yT = curY - curPiece.y(i);
						int squareWidth = squareWidth();
						//int startX=remainX+xT*squareWidth, startY=boardTop + (BOARD_HEIGHT - yT - 2 )*(sideToDevice(mainHeight)/20);
						int startX=remainX+xT*squareWidth, startY=boardTop + (BOARD_HEIGHT - yT - 2 )*(sideToDevice(mainHeight)/(BOARD_HEIGHT-3));
						
						//use ¡°Point-Inside-Polygon¡± test algorithm	to detect the cursor is in this polygon or not
						int [] xAxis = new int []{startX,startX+squareWidth+1,startX+squareWidth+1,startX}; 
						int [] yAxis = new int []{startY,startY,startY+squareWidth+1,startY+squareWidth+1}; 
						Polygon thisShape = new Polygon(xAxis,yAxis,4);
						boolean in = thisShape.contains(x,y);
						if(in ){
							
							inside = true;
							changed = true;
						
							Shape temp= newPrevPiece();
							boolean fit = tryChange(temp, curX, curY);
							while(!fit || temp.isSame(curPiece)|| temp.isSame(prevPiece))
							{
								temp = newPrevPiece();
								fit =tryChange(temp, curX, curY);
							}
						
							curPiece = temp;
							
							score = score - level*M;
							repaint();
							
							break;
						}
					}
				}
				
				boolean [] judge = new boolean[4];
				//check again£¬
				for(int i = 0; i<4; ++i)
				{
					int xT = curX + curPiece.x(i);
					int yT = curY - curPiece.y(i);
					int squareWidth = squareWidth();
					//int startX=remainX+xT*squareWidth, startY=boardTop + (BOARD_HEIGHT - yT - 2 )*(sideToDevice(mainHeight)/20);
					int startX=remainX+xT*squareWidth, startY=boardTop + (BOARD_HEIGHT - yT - 2 )*(sideToDevice(mainHeight)/(BOARD_HEIGHT-3));
					int [] xAxis = new int []{startX,startX+squareWidth+1,startX+squareWidth+1,startX}; 
					int [] yAxis = new int []{startY,startY,startY+squareWidth+1,startY+squareWidth+1}; 
					Polygon thisShape = new Polygon(xAxis,yAxis,4);
					boolean in = thisShape.contains(x,y);
					if(in){
						judge[i]=true;
					}
				}
				
				inside = judge[0] || judge[1] || judge[2] || judge[3];
					
				if(!inside && changed){
					changed = false;
				}
				
			}
			
/*				for(int i = 0; i<4; ++i)
				{
					int xT = curX + curPiece.x(i);
					int yT = curY - curPiece.y(i);
					int squareWidth = squareWidth();
					//drawSquare(g, remainX+x*squareWidth(), boardTop + (BOARD_HEIGHT - y - 1)*squareHeight(), curPiece.getShape());
					int startX=remainX+xT*squareWidth, startY=boardTop + (BOARD_HEIGHT - yT - 2 )*(sideToDevice(mainHeight)/20);
					if(x > startX+squareWidth || x<startX || y > startY+squareWidth || y<startY && changed){
						
						changed = false;
						break;
					}
				}*/
			
		}
	}

	class LeftRightAdapter extends MouseAdapter{

		@Override
		public void mousePressed(MouseEvent e){
			int borderXMin = xToDevice(quitX);
			int borderXMax = xToDevice(quitX + quitWidth);
			int borderYMin = yToDevice(quitY);
			int borderYMax = yToDevice(quitY + quitHeight);

			
			if(e.getButton() == MouseEvent.BUTTON1 ){
				int x = e.getX();
				int y = e.getY();

				if(x > borderXMin && x < borderXMax && y >borderYMin && y <borderYMax){
					System.exit(0);
				}
				if(isStarted && !isPaused)
				{tryMove(curPiece, curX - 1, curY);}
			}
			
			if(e.getButton() == MouseEvent.BUTTON3 && isStarted && !isPaused){
				tryMove(curPiece, curX + 1, curY);
			}
		}
	}
	
	class MChageListener implements ChangeListener{
		public void stateChanged(ChangeEvent e){
			JSlider temp = (JSlider)e.getSource();
			if(! temp.getValueIsAdjusting())
			{
				M = temp.getValue();
			}
		}
	}
	
	class NChangeListener implements ChangeListener{
		public void stateChanged(ChangeEvent e){
			JSlider temp = (JSlider)e.getSource();
			if(! temp.getValueIsAdjusting()){
				N = temp.getValue();
			}
		}
	}
	
	class SChangeListener implements ChangeListener{
		public void stateChanged(ChangeEvent e){
			JSlider temp = (JSlider)e.getSource();
			if(!temp.getValueIsAdjusting()){
				S = temp.getValue()*0.1F;
			}
		}
	}
	
}

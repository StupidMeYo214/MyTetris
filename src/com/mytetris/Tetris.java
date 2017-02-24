package com.mytetris;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Tetris extends JFrame{
	
	private JLabel statusBar;
	private JSlider mSlider = new JSlider();
	private JSlider nSlider = new JSlider();
	private JSlider windowSizeSlider = new JSlider();
	private  JSlider sJSlider = new JSlider();
	private JPanel sizePanel = new JPanel();
	private JButton startButton = new JButton("START");
	private JButton stopButton = new JButton("STOP");
	String[] hardArray = { "Hard:20*40" , "Normal:15*30","Easy:10*20" };
	private JComboBox<String> box = new JComboBox<String>(hardArray);
	public JComboBox<String> getBox() {
		return box;
	}
	private int sizeFactor=1;

	private JPanel southPanel = new JPanel();
	
	public int width = Math.round(600*(1F+sizeFactor*0.1F)); public int height=Math.round(600*(1F+sizeFactor*0.1F));
	public Tetris(){
		
		Board board = new Board(this);
		add(board);
		
		sizePanel.setLayout(new BoxLayout(sizePanel, BoxLayout.Y_AXIS));
	
		sizePanel.add(box);
		add(sizePanel,BorderLayout.EAST);
		//southPanel.add(startButton);
		southPanel.add(stopButton);
		//add(southPanel,BorderLayout.SOUTH);
		
		
		windowSizeSlider.setMaximum(10);
		windowSizeSlider.setMinimum(1);
		windowSizeSlider.setValue(1);
		windowSizeSlider.setMajorTickSpacing(1);
		windowSizeSlider.setPaintLabels(true);
		windowSizeSlider.setPaintTicks(true);
		windowSizeSlider.addChangeListener(new changeWindowSize());
		windowSizeSlider.setName("Scale");
		addSlider(windowSizeSlider, "Zoom", sizePanel);
		
		//add(controlPanel,BorderLayout.SOUTH);
		addSlider(mSlider, "M: ",sizePanel);
		addSlider(nSlider, "N: ",sizePanel);
		addSlider(sJSlider, "S: (10^-1)", sizePanel);
		
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				board.start();
				
			}
		});
		
	stopButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				board.stop();
				
			}
		});
		
		/*board.newPiece();
		board.repaint();*/
		
		setSize(width,height);
		setTitle("Tetris");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		board.setBackground(Color.white);
	}
	
	public void changeSize(){
		this.setSize(width,height);
	}
	
	class changeWindowSize implements ChangeListener{
		public void stateChanged(ChangeEvent e){
			JSlider temp = (JSlider)e.getSource();
			if(!temp.getValueIsAdjusting()){
				sizeFactor = temp.getValue();
				if (sizeFactor!=1){
					width=Math.round(600*(1F+sizeFactor*0.1F));
					height=width;
					//System.out.println(width+","+height);
					changeSize();
				}
			}
		}
	}
	
	
   public void addSlider(JSlider s, String description,JPanel newpanel)
   {
   
      JPanel panel = new JPanel();
      panel.add(new JLabel(description));
      panel.add(s);
      newpanel.add(panel);
   }
	
	public JLabel getStatusBar(){
		return statusBar;
	}
	
	public JSlider getMSlider(){
		return mSlider;
	}

	public JSlider getNSlider(){
		return nSlider;
	}
	
	public int getSizeFactor(){
		return sizeFactor;
	}
	
	public JSlider getWindowSizeSlider(){
		return windowSizeSlider;
	}
	
	public JSlider getSSlider(){
		return sJSlider;
	}
	public static void main(String [] args){
		
		Tetris tetris = new Tetris();
		
		tetris.setLocationRelativeTo(null);
		tetris.setVisible(true);
		
		
	}
	
	
}

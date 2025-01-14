package IA;

import java.util.Arrays;
import java.util.Random;

import javax.swing.JLabel;

import mazeCreate.locationPrint;

public class reforcement extends Thread{
	//
	private double[][] qTable;
	private int[][] map;
	private int mode; //0 - usufruir, 1 - explorar
	
	//numeros para percorrer a matriz
	private int startC;
	private int startL;
	
	private int previousLine;
	private int currentLine;
	
	private int goal;
	private int steps = 0;
	private int exits = 0;
	private int speed = 0;
	private boolean hide = false;
	
	private JLabel xL;
	private JLabel yL;
	private JLabel sL;
	private JLabel eL;
	
	private double y = 0.9;
	
	//maximos da matriz
	private int max_width;
	private int max_height;
	
	private Random random;
	
	private archieve arq;
	
	//onde sera desenhado o local do agente
	locationPrint agente;
	
	public reforcement(int[][] matriz, int mode, int goal, locationPrint agente, JLabel x, JLabel y, JLabel steps, JLabel exit) {
		random = new Random();
		this.map = matriz;
		max_width = matriz.length;
		max_height = matriz[0].length;
		this.mode = mode;
		this.goal = goal;
		qTable = new double[max_height*max_width][4];
		this.arq = new archieve();
		this.agente = agente;
		
		this.xL = x;
		this.yL = y;
		this.sL = steps;
		this.eL = exit;

		arq.loadFileInMe(qTable, "qTableReforcement.txt");
	}
	
	public void run(){
		startSomewhere();
		agente.setPosition(startC, startL);
		while(steps < 1000000) {
			try {
				sleep(speed);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			action();
			
			if(!hide)
				agente.setPosition(startC, startL);
			
			steps++;
			sL.setText("" + steps);
		}
		agente.stopMe();
		arq.saveFile(qTable, "qTableReforcement.txt", qTable.length);
		
	}
	
	public void changeSpeed(int new_speed) {
		this.speed = new_speed;
	}
	
	public void hideAgent() {
		if(hide)
			hide = false;
		else
			hide = true;
	}
	
	/**
	 * action: 0 - esquerda, 1 - cima, 2 - direita, 3 - baixo
	 */
	private void action() {
		int act;
		
		if(mode == 1) {
			fillLinaQTable(); //vai atribuir -1 para a ação que o levar a uma parede
			act = random.nextInt(4);
			
			while(qTable[currentLine][act] == -1) {
				if(qTable[currentLine][act] != -1)
					break;
				act = random.nextInt(4);
			}
			
			switch(act) {
				//esquerda
				case 0:
					startC--;
					yL.setText(""+(startC+1));
					break;
				//cima
				case 1:
					startL--;
					xL.setText(""+(startL+1));
					break;
				//direita
				case 2:
					startC++;
					yL.setText(""+(startC+1));
					break;
				//baixo
				case 3:
					startL++;
					xL.setText(""+(startL+1));
					break;	
			}
			
			calcCurrentLine();
			qTable[previousLine][act] = calcActionQ(currentLine);
		}
	}
	
	private void fillLinaQTable() {
		//esquerda
		if((startC-1) >= 0) {
			if(map[startL][startC-1] == 1 && qTable[currentLine][0] == 0)
				qTable[currentLine][0] = -1;
		}else {
			qTable[currentLine][0] = -1;
		}
		
		//cima
		if((startL-1) >= 0) {
			if(map[startL-1][startC] == 1 && qTable[currentLine][1] == 0)
				qTable[currentLine][1] = -1;
		}else {
			qTable[currentLine][1] = -1;
		}
		
		//direita
		if((startC+1) <= (max_width-1)) {
			if(map[startL][startC+1] == 1 && qTable[currentLine][2] == 0)
				qTable[currentLine][2] = -1;
		}else {
			qTable[currentLine][2] = -1;
		}
		
		//baixo
		if((startL+1) <= (max_height-1)) {
			if(map[startL+1][startC] == 1 && qTable[currentLine][3] == 0)
				qTable[currentLine][3] = -1;
		}else {
			qTable[currentLine][3] = -1;
		}
	}
	/**
	 * @param state: o estado o qual foi tomado uma ação
	 * @return
	 */
	private synchronized double calcActionQ(int state) {
		double q = 0;
		int ref = 0;
		
		if(state == goal) {
			ref = 10;
			exits++;
			eL.setText("" + exits);
		}

		q = ref + (y * max(state));
		
		return q;
	}
	
	private void startSomewhere() {
		startC = random.nextInt(max_width);
		startL = random.nextInt(max_height);
		
		while(map[startL][startC] != 0) {
			startL = random.nextInt(61);
			startC = random.nextInt(61);
		}
		
		calcCurrentLine();
	}
	
	private void calcCurrentLine() {
		previousLine = currentLine;
		currentLine = startC + startL + ((max_width-1) * startL);
	}
	
	private double max(int state){
        double maior = 0;
        for(int i = 0; i < 4; i++)
            if(qTable[state][i] > maior)
                maior = qTable[state][i];
            
        return maior;
    }
	
	
	
}

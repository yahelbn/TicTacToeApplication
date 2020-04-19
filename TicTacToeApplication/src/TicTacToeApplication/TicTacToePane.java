package TicTacToeApplication;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Ellipse;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
public class TicTacToePane extends BorderPane implements TicTacToeInterface {
	private static int number_of_objects = 0;
	private char whoseTurn = 'X';
	private TokenHandler adapter = new TokenAdapter();
	private SaveButton jbtSave;
	private ClearButton jbtClear;
	private ReloadButton jbtReload;
	private UndoButton jbtUndo;
	private RedoButton jbtRedo;
	private Label lblStatus = new Label("X's turn to play");
	private Cell[][] cell = new Cell[3][3];
	private HBox buttonHBox = new HBox(10);
	private RandomAccessFile raf;
	private CareTaker ct = new CareTaker();
	private EventHandler<ActionEvent> ae = 	e -> ((Command) e.getSource()).Execute();
	
	private TicTacToePane() {
		try {
			raf = new RandomAccessFile("TicTacToe.dat", "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		GridPane pane = new GridPane();
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				pane.add(cell[i][j] = new Cell(), i, j);
		this.setCenter(pane);
		
		VBox bottomPane = new VBox(5);
		
		jbtSave = new SaveButton(cell, raf);
		jbtClear = new ClearButton(cell, raf);
		jbtReload = new ReloadButton(cell, raf);
		jbtUndo = new UndoButton (cell, raf);
		jbtRedo = new RedoButton(cell, raf);
		jbtSave.setOnAction(ae);
		jbtClear.setOnAction(ae);
		jbtReload.setOnAction(ae);
		jbtUndo.setOnAction(ae);
		jbtRedo.setOnAction(ae);
		buttonHBox.getChildren().addAll(jbtSave, jbtClear, jbtReload, jbtUndo, jbtRedo);
		bottomPane.getChildren().addAll(lblStatus, buttonHBox);
		this.setBottom(bottomPane);
	}

	public void addButtons(CommandButton...btArray) {
		for(CommandButton bt : btArray) {
			buttonHBox.getChildren().add(bt);
			bt.setOnAction(ae);
		}
	}
	public static TicTacToePane getInstance() {
		if (number_of_objects > NUMBER_OF_OBJECTS)
			return null;
		else {
			number_of_objects++;
			return new TicTacToePane();
		}
	}

	public static void reduceNumberOfObjects() {
		number_of_objects--;
	}

	public static int getNumberOfObjects() {
		return number_of_objects;
	}

	public static void resetNumberOfObjects() {
		number_of_objects = 0;
	}
	
		public boolean isFull() {
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++)
					if (cell[i][j].getToken() == ' ')
						return false;
			return true;
		}

		/** Determine if the player with the specified token wins */
		public boolean isWon(char token) {
			for (int i = 0; i < 3; i++)
				if (cell[i][0].getToken() == token && cell[i][1].getToken() == token && cell[i][2].getToken() == token)
					return true;
			for (int j = 0; j < 3; j++)
				if (cell[0][j].getToken() == token && cell[1][j].getToken() == token && cell[2][j].getToken() == token)
					return true;
			if (cell[0][0].getToken() == token && cell[1][1].getToken() == token && cell[2][2].getToken() == token)
				return true;
			if (cell[0][2].getToken() == token && cell[1][1].getToken() == token && cell[2][0].getToken() == token)
				return true;
			return false;
		}


		// An inner class for a cell
		class Cell extends Pane implements TicTacToeInterface{ // Token used for this cell
			private char token = ' ';
			
			public Cell() {
				setStyle(STYLE_COMMAND);
				this.setPrefSize(2000, 2000);
				this.setOnMouseClicked(e -> handleMouseClick());
			}

			/** Return token */
			public char getToken() {
				return token;
			}

			/** Set a new token */
			public void setToken(char c) {
				
				token = c;
				adapter.draw(this, token);
			}

			/* Handle a mouse click event */
			private void handleMouseClick() { // If cell is empty and game is not over
				char tokens[][] = new char[cell.length][cell[0].length];
				for(int i=0; i<tokens.length; i++) 
					for(int j=0; j<tokens[i].length; j++) 
						tokens[j][i] = cell[j][i].getToken();
						
				
				ct.add(new Memento(tokens));
				
				if (token == ' ' && whoseTurn != ' ') {
					
					setToken(whoseTurn); // Set token in the cell
					
					// Check game status
					if (isWon(whoseTurn)) {
						lblStatus.setText(whoseTurn + " won! The game is over");
						whoseTurn = ' '; // Game is over
					} else if (isFull()) {
						lblStatus.setText("Draw! The game is over");
						whoseTurn = ' '; // Game is over
					} else { // Change the turn
						whoseTurn = (whoseTurn == 'X') ? 'O' : 'X';
						// Display whose turn
						lblStatus.setText(whoseTurn + "'s turn");
					}
				}
			}
			
			public void clear() {
				this.getChildren().clear();
				whoseTurn = 'X';
				token = ' ';
				lblStatus.setText(whoseTurn + "'s turn");
			}
		}
		
		class Memento {
			private char[][] tokenMatrix;
			Memento(char[][] tokenMatrix) {
				this.tokenMatrix = tokenMatrix;
			}
			public char[][] getBoard() {
				return tokenMatrix;
			}
		}
		
		class CareTaker {
			private List <Memento> mementoList = new ArrayList <Memento>();
			private int index;
			public CareTaker() {
				index = mementoList.size();
			}
			
			public void add(Memento currentState) {
				if(currentState != null) {
					mementoList.add(currentState);
					index = mementoList.size();
				}
			}
			
			public Memento getPrev() {
				if(mementoList.isEmpty() || index <= 0 )  
					return null;
				index--;
				return mementoList.get(index);
			}
			
			public Memento getNext() {
				if(mementoList.isEmpty() || index >= mementoList.size() - 1)
					return null;
				index++;
				return mementoList.get(index);
			}
			
			public int getLength() {
				return index;
			}
		}

		
	interface TokenHandler {
		void draw(Cell cell, char tokenToDraw);
	}
	  
	  public class TokenAdapter extends Pane implements TokenHandler{
		  private DrawX xPainter = new DrawX();
		  private DrawO oPainter = new DrawO();
		  
		  @Override
		  public void draw(Cell cell, char tokenToDraw) {
			  if(tokenToDraw == 'X') 
				  xPainter.draw(cell);
			  else if(tokenToDraw == 'O') 
				  oPainter.draw(cell);  
		  }
	  }
	  
	  public class DrawX {
		  public void draw(Cell cell) {
			  Line line1 = new Line(10, 10, cell.getWidth() - 10, cell.getHeight() - 10);
			  line1.endXProperty().bind(cell.widthProperty().subtract(10));
			  line1.endYProperty().bind(cell.heightProperty().subtract(10));
			  Line line2 = new Line(10, cell.getHeight() - 10, cell.getWidth() - 10, 10);
			  line2.startYProperty().bind(cell.heightProperty().subtract(10));
			  line2.endXProperty().bind(cell.widthProperty().subtract(10));
			  cell.getChildren().addAll(line1, line2); 
		  }
	  }
	  
	  public class DrawO {
		  public void draw(Cell cell) {
			  Ellipse ellipse = new Ellipse(cell.getWidth() / 2, cell.getHeight() / 2, cell.getWidth() / 2 - 10, cell.getHeight() / 2 - 10);
		      ellipse.centerXProperty().bind(cell.widthProperty().divide(2));
		      ellipse.centerYProperty().bind(cell.heightProperty().divide(2));
		      ellipse.radiusXProperty().bind(cell.widthProperty().divide(2).subtract(10));        
		      ellipse.radiusYProperty().bind(cell.heightProperty().divide(2).subtract(10));   
		      ellipse.setStroke(Color.BLACK);
		      ellipse.setFill(Color.WHITE);
		      cell.getChildren().add(ellipse);
		  }
	  }
	  
	  
	  
	  interface Command {
			public void Execute();
		}

		class CommandButton extends Button implements Command {
			private RandomAccessFile raf;
			private Cell[][] cell;
			CommandButton(Cell[][] cell, RandomAccessFile raf) {
				this.cell = cell;
				this.raf = raf;
				
			}
			@Override
			public void Execute() {}
			
		}
		
		public void clearTheBoard() {
			for (int i = 0; i < cell.length; i++) {
				for (int j = 0; j < cell[i].length; j++) {
					cell[i][j].clear();
				}
			}
		}
		
		class SaveButton extends CommandButton {
			SaveButton(Cell[][] cell, RandomAccessFile raf) {
				super(cell, raf);
				this.setText("Save");
			}
			
			@Override
			public void Execute() {
				try {
					for (int i = 0; i < cell.length; i++) {
						for (int j = 0; j < cell[i].length; j++) {
							raf.writeChar(cell[i][j].getToken());
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
			
		}
		
	
		class ClearButton extends CommandButton {
			ClearButton(Cell[][] cell, RandomAccessFile raf) {
				super(cell, raf);
				this.setText("Clear");
			}
			
			@Override
			public void Execute() {
					clearTheBoard();
				try {
					raf.seek(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		
		}
		
		class ReloadButton extends CommandButton {
			ReloadButton(Cell[][] cell, RandomAccessFile raf) {
				super(cell, raf);
				this.setText("Reload");
			}
			
			@Override
			public void Execute() {
				try {
					clearTheBoard();
					
					raf.seek(0);
					if (raf.length() != 0) {

						for (int i = 0; i < cell.length; i++) {
							for (int j = 0; j < cell[i].length; j++) {
								adapter.draw(cell[i][j], raf.readChar());
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		class UndoButton extends CommandButton {
			
			UndoButton(Cell[][] cell, RandomAccessFile raf) {
				super(cell, raf);
				this.setText("Undo");
			}
			
			@Override
			public void Execute() {
				Memento current = ct.getPrev();
				if(current != null) {
					clearTheBoard();
					char[][] tokenMatrix = current.getBoard();
					for(int i=0; i<tokenMatrix.length; i++) { 
						for(int j=0; j<tokenMatrix[i].length; j++) { 
							cell[j][i].setToken(tokenMatrix[j][i]);
						}
				 	}
					whoseTurn = (whoseTurn == 'X') ? 'O' : 'X';
				
					lblStatus.setText(whoseTurn + "'s turn");
				}
			}
		}
		
		class RedoButton extends CommandButton {
			RedoButton(Cell[][] cell, RandomAccessFile raf) {
				super(cell, raf);
				this.setText("Redo");
			}
			
			@Override
			public void Execute() {
				Memento next = ct.getNext();

				if(next != null) {
					clearTheBoard();
					char[][] tokenMatrix = next.getBoard();
					for(int i=0; i<tokenMatrix.length; i++) { 
						for(int j=0; j<tokenMatrix[i].length; j++) { 
							cell[j][i].setToken(tokenMatrix[j][i]);
						}
					}
					whoseTurn = (whoseTurn == 'X') ? 'O' : 'X';
					lblStatus.setText(whoseTurn + "'s turn");
				}
			}
		}
}


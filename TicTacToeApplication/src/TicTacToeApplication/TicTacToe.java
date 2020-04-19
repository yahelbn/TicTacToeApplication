package TicTacToeApplication;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Ellipse;
public class TicTacToe extends Application implements TicTacToeInterface
{ 

  @Override 
  public void start(Stage primaryStage) throws Exception {

		Stage[] stages = new Stage[NUMBER_OF_OBJECTS];
		Scene[] scenes = new Scene[NUMBER_OF_OBJECTS];

		TicTacToePane[] panes = new TicTacToePane[NUMBER_OF_OBJECTS];
		try {
			for (int i = 0; i < 1 + NUMBER_OF_OBJECTS; i++) {
				if (i >= NUMBER_OF_OBJECTS)
					System.out.println(SINGELTON_MESSAGE);
				else {
					panes[i] = TicTacToePane.getInstance();
					scenes[i] = new Scene(panes[i], 450, 170);
					stages[i] = new Stage();
					stages[i].setTitle(TITLE + (" " + (i+1)));
					stages[i].setScene(scenes[i]);
					stages[i].setResizable(true);
					stages[i].show();
					stages[i].setAlwaysOnTop(true);
					stages[i].setOnCloseRequest(event -> {TicTacToePane.reduceNumberOfObjects();
					});

				}
			}
		} catch (Exception e) {
			TicTacToePane.resetNumberOfObjects();
		}

	}

  
  public static void main(String[] args)
  { launch(args);
  }
}

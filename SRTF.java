import java.util.function.UnaryOperator;
import java.util.Optional;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * SRTF
 */
public class SRTF extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Shortest Remaining Time First ");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("Welcome to SRTF");
        sceneTitle.setFont(Font.font("Tahoma",FontWeight.BOLD,20));
        grid.add(sceneTitle, 0, 0, 2, 1);
        
        Label noOfJobsLabel = new Label("Enter Number of Jobs:");
        grid.add(noOfJobsLabel, 0, 1);

        final Text actionTarget = new Text();
        grid.add(actionTarget, 1, 5);
        
        //restrict input to decimal digits only
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();

            if (text.matches("[0-9]*")) {
                return change;
            }

            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);

        TextField jobTextField = new TextField();
        jobTextField.setTextFormatter(textFormatter);
        jobTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Integer noOfJobs = Integer.valueOf(jobTextField.getText());
                if(noOfJobs > 0){
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText("Look, a Confirmation Dialog");
                    alert.setContentText("Are you ok with this?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        // ... user chose OK
                        actionTarget.setFill(Color.FORESTGREEN);
                        actionTarget.setText("Creating " + noOfJobs + " processes");
                    } else {
                        // ... user chose CANCEL or closed the dialog
                    }
                    
                }
                else {
                    Alert ealert = new Alert(AlertType.ERROR);
                    ealert.setTitle("Error!");
                    ealert.setHeaderText("Oops! There was an error");
                    ealert.setContentText("Number of processes must be greater than zero");

                    ealert.showAndWait();
                    actionTarget.setFill(Color.FIREBRICK);
                    actionTarget.setText("Error! Invalid input");
                }

            }
        });

        grid.add(jobTextField, 1, 1);

        Button btn = new Button("Enter");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(btn, 1, 3);

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                actionTarget.setFill(Color.FORESTGREEN);
                Integer noOfJobs = Integer.valueOf(jobTextField.getText());
                actionTarget.setText("Creating " + noOfJobs + " processes");

            }
        });

        Scene scene = new Scene(grid, 400, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }//close start function
    
    EventHandler<ActionEvent> handler1 = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

        }
    };
}//close public SRJF class

/**
 * Job class
 * Collects details about the jobs
 */
class Job {
    private int jobNo, cpuTime, arrivalTime, finishTime, startTime;

    /** Job class constructor */
    public Job(int job, int burst, int atime) {
        jobNo = job;
        cpuTime = burst;
        arrivalTime = atime;
    }

    /** Getter & Setter methods below */

    public int turnAroundTime() {
        return finishTime - arrivalTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getCPUTime() {
        return cpuTime;
    }

    public int getJobNumber() {
        return jobNo;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int fTime) {
        finishTime = fTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int sTime) {
        startTime = sTime;
    }
    
}//close Job class
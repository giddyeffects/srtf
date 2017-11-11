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
import javafx.stage.Modality;

/**
 * SRTF
 */
public class SRTF extends Application {
    //declare the application scenes/screens
    Scene scene1, scene2;
    //declare the  application grids
    GridPane grid1 = new GridPane(), grid2 = new GridPane();
    Stage thestage, newStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        thestage = primaryStage;
        
        primaryStage.setTitle("Shortest Remaining Time First ");
        
        grid1.setAlignment(Pos.CENTER);
        grid1.setHgap(10);
        grid1.setVgap(10);
        grid1.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("Welcome to SRTF");
        sceneTitle.setFont(Font.font("Tahoma",FontWeight.BOLD,20));
        grid1.add(sceneTitle, 0, 0, 2, 1);
        
        Label noOfJobsLabel = new Label("Enter Number of Jobs:");
        grid1.add(noOfJobsLabel, 0, 1);

        final Text actionTarget = new Text();
        grid1.add(actionTarget, 1, 5);
        
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
                Alert ealert = new Alert(AlertType.ERROR);
                String t = jobTextField.getText();
                if(t == null || t.isEmpty()){
                    ealert.setTitle("Error!");
                    ealert.setHeaderText("Invalid Input!");
                    ealert.setContentText("You must enter number of processes");

                    ealert.showAndWait();
                }
                else {
                    Integer noOfJobs = Integer.valueOf(t);
                    if (noOfJobs > 0) {//restricting number of jobs to 1-9 range etc etc
                        Alert alert = new Alert(AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation");
                        alert.setHeaderText("Confirm Process Creation");
                        alert.setContentText("We are about to create " + noOfJobs + " CPU Processes. Are you ok with this?");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            // ... user chose OK
                            grid2 = gridGen(noOfJobs);
                            scene2 = new Scene(grid2, 400,275);
                            //make another stage for scene2
                            newStage = new Stage();
                            newStage.setScene(scene2);
                            //tell stage it is meannt to pop-up (Modal)
                            newStage.initModality(Modality.APPLICATION_MODAL);
                            newStage.setTitle("Create Processes");
                            newStage.showAndWait();

                            actionTarget.setFill(Color.FORESTGREEN);
                            actionTarget.setText("Creating " + noOfJobs + " processes");
                        } else {
                            // ... user chose CANCEL or closed the dialog
                            actionTarget.setText("");
                        }

                    } else {
                        ealert.setTitle("Error!");
                        ealert.setHeaderText("Invalid Input!");
                        ealert.setContentText("Number of processes must be greater than zero");

                        ealert.showAndWait();
                        actionTarget.setFill(Color.FIREBRICK);
                        actionTarget.setText("Error! Invalid input");
                    }
                }
            }
        });

        grid1.add(jobTextField, 1, 1);

        Button btnScene1 = new Button("Enter");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btnScene1);
        grid1.add(btnScene1, 1, 3);

        btnScene1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                actionTarget.setFill(Color.FORESTGREEN);
                Integer noOfJobs = Integer.valueOf(jobTextField.getText());
                actionTarget.setText("Creating " + noOfJobs + " processes");

            }
        });

        scene1 = new Scene(grid1, 400, 275);
        primaryStage.setScene(scene1);
        primaryStage.show();
    }//close start function
    
    EventHandler<ActionEvent> handler1 = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

        }
    };//close scene1 handler function

    GridPane gridGen(int num) {
        Job[] JobsArray = new Job[num]; //an array of Job objects
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5));
        int counter=1;

        //create the jobs
        for (int i = 0; i < num; i++) {
            int jobNo = i+1;
            Label label1 = new Label("Enter Process "+jobNo+" CPU Burst Time:");
            grid.add(label1, 0, counter);
            TextField textField1 = new TextField();
            grid.add(textField1, 1, counter);
            counter++;
            Label label2 = new Label("Enter Process "+jobNo+" Arrival Time");
            grid.add(label2, 0, counter);
            TextField textField2 = new TextField();
            grid.add(textField2, 1, counter);
            //Integer cpuTime = Integer.valueOf(textField1.getText());
            //Integer arrTime = Integer.valueOf(textField2.getText());
            //JobsArray[0] = new Job(jobNo, cpuTime, arrTime);
            
            Button btnScene1 = new Button("Submit "+jobNo);
            HBox hbBtn = new HBox(10);
            hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
            hbBtn.getChildren().add(btnScene1);
            btnScene1.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Integer cpuTime = Integer.valueOf(textField1.getText());
                    Integer arrTime = Integer.valueOf(textField2.getText());
                    JobsArray[i] = new Job(jobNo, cpuTime, arrTime);
                }
            });
            counter++;
            grid.add(btnScene1, 1, counter);
            //counter++;//to cumulatively add the elements
        }
        return grid;
    }

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
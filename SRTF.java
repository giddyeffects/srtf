//import SRTF.Job;
import java.util.function.UnaryOperator;
import java.util.Optional;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
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
    TableView<Job> processTable = new TableView<Job>();
    ObservableList<Job> data;
    Stage thestage, newStage;
    int counter2=0, totalTime=0;
    double aveTAT=0.0, aveWT=0.0;
    Job[] JobsArray;
    Label chartLabel = new Label("Gantt Chart:");
    Label aveTATLabel = new Label("Average Turnaround Time: ");
    Label aveWTLabel = new Label("Average Waiting Time: ");

    Text att = new Text();
    Text awt = new Text();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        thestage = primaryStage;
        
        primaryStage.setTitle("Shortest Remaining Time First ");
        //primaryStage.setWidth(600);
        //primaryStage.setHeight(500);
        
        grid1.setAlignment(Pos.CENTER);
        grid1.setMinWidth(500);
        grid1.setHgap(10);
        grid1.setVgap(10);
        grid1.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("Welcome to SRTF Simulator");
        sceneTitle.setFont(Font.font("Tahoma",FontWeight.BOLD,20));
        grid1.add(sceneTitle, 0, 0, 2, 1);
        
        Label noOfJobsLabel = new Label("Enter Number of Jobs:");
        grid1.add(noOfJobsLabel, 0, 1);

        final Text actionTarget = new Text();
        grid1.add(actionTarget, 1, 5);
        processTable.setEditable(false);
        processTable.setMinHeight(50);

        TableColumn<Job, Integer> jobNoCol = new TableColumn<Job, Integer>("Process No.");
        jobNoCol.setCellValueFactory(new PropertyValueFactory<Job, Integer>("jobNo"));
        TableColumn<Job, Integer> burstTimeCol = new TableColumn<Job, Integer>("CPU Burst Time");
        burstTimeCol.setMinWidth(100);
        burstTimeCol.setCellValueFactory(new PropertyValueFactory<Job, Integer>("burstTime"));
        TableColumn<Job, Integer> arrivalTimeCol = new TableColumn<Job, Integer>("Arrival Time");
        arrivalTimeCol.setCellValueFactory(new PropertyValueFactory<Job, Integer>("arrivalTime"));
        TableColumn<Job, Integer> tatCol = new TableColumn<Job, Integer>("TAT");
        tatCol.setCellValueFactory(new PropertyValueFactory<Job, Integer>("turnAroundTime"));
        TableColumn<Job, Integer> wtCol = new TableColumn<Job, Integer>("WT");
        wtCol.setCellValueFactory(new PropertyValueFactory<Job, Integer>("waitingTime"));

        processTable.setItems(data);
        processTable.getColumns().addAll(jobNoCol, burstTimeCol, arrivalTimeCol, tatCol, wtCol);

        grid1.add(processTable, 0, 6, 2, 1);
        
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
        Tooltip tooltip = new Tooltip();
        tooltip.setText("Enter an integer\n Between 1 to 5\n");
        jobTextField.setTooltip(tooltip);
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
                    if (noOfJobs > 0 && noOfJobs < 6 ) {//restricting number of jobs to 1-9 range etc etc
                        Alert alert = new Alert(AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation");
                        alert.setHeaderText("Confirm Process Creation");
                        alert.setContentText("We are about to create " + noOfJobs + " CPU Processes. Are you ok with this?");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            // ... user chose OK
                            grid2 = createProcessPane(noOfJobs);
                            scene2 = new Scene(grid2, 400,400);
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
                        ealert.setContentText("Number of processes must be in the range 1-5");

                        ealert.showAndWait();
                        actionTarget.setFill(Color.FIREBRICK);
                        actionTarget.setText("Error! Invalid input");
                    }
                }
            }
        });

        grid1.add(jobTextField, 1, 1);

        Button btnScene1 = new Button("Enter");
        //btnScene1.setStyle("-fx-background-color: darkslateblue; -fx-textfill: white;");
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
        grid1.add(chartLabel, 0, 7);

        grid1.add(aveTATLabel, 0, 9);
        grid1.add(aveWTLabel, 0, 10);
        grid1.add(att, 1, 9);
        grid1.add(awt, 1, 10);

        scene1 = new Scene(grid1, 550, 400);
        jobTextField.requestFocus();
        primaryStage.setScene(scene1);
        primaryStage.show();
    }//close start function
    
    EventHandler<ActionEvent> handler1 = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

        }
    };//close scene1 handler function

    GridPane createProcessPane(int num) {
        JobsArray = new Job[num]; //an array of Job objects
        TextField[] inputTextField = new TextField[num*2];
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5));
        int counter=1,cnt2=0;

        //create the jobs
        for (int i = 0; i < num; i++) {
            int jobNo = i+1;
            Label label1 = new Label("Enter Process "+jobNo+" CPU Burst Time:");
            grid.add(label1, 0, counter);
            TextField textField1 = new TextField();
            //restrict input to decimal digits only
            UnaryOperator<TextFormatter.Change> filter = change -> {
                String text = change.getText();

                if (text.matches("[0-9]*")) {
                    return change;
                }

                return null;
            };
            TextFormatter<String> textFormatter = new TextFormatter<>(filter);
            textField1.setTextFormatter(textFormatter);
            inputTextField[cnt2] = textField1;
            grid.add(inputTextField[cnt2], 1, counter);
            counter++;
            cnt2++;
            Label label2 = new Label("Enter Process "+jobNo+" Arrival Time");
            grid.add(label2, 0, counter);
            TextField textField2 = new TextField();
            TextFormatter<String> textFormatter2 = new TextFormatter<>(filter);
            textField2.setTextFormatter(textFormatter2);
            inputTextField[cnt2] = textField2;
            grid.add(inputTextField[cnt2], 1, counter);
            //Integer cpuTime = Integer.valueOf(textField1.getText());
            //Integer arrTime = Integer.valueOf(textField2.getText());
            //JobsArray[0] = new Job(jobNo, cpuTime, arrTime);
            

            counter++;
            cnt2++;
            
            //counter++;//to cumulatively add the elements
        }
        Button btnScene1 = new Button("Submit");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btnScene1);
        btnScene1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                counter2 = 0; //reset counter2
                totalTime = 0; //reset totalTime
                for (int i = 0; i < num; i++) {
                    int jn = i + 1;
                    Integer cpuTime = Integer.valueOf(inputTextField[counter2].getText());
                    counter2++;
                    Integer arrTime = Integer.valueOf(inputTextField[counter2].getText());
                    JobsArray[i] = new Job(jn, cpuTime, arrTime);
                    totalTime += cpuTime;//get total execution time
                    counter2++;
                }
                processJobs(JobsArray);

                newStage.close();//close the create processes dialog box
            }
        });
        grid.add(btnScene1, 1, counter);

        return grid;
    }//close createProcessPane function

    public void processJobs(Job[] JobsArray) {

        int[] timeChart = new int[totalTime];
        
        for (int i = 0; i < totalTime; i++) {
            //select shortest process which has arrived
            int activeProcess = 0;//currently active process Index
            int minCpuTime = 99999; //current least cpu time... put initial large number as the shortest cpu time
            for (int j = 0; j < JobsArray.length; j++) {
                //check if the process has arrived in the timeline 'i'
                if(JobsArray[j].getArrivalTime() <= i ) {
                    //if the job's cpu time is less than the current least cpu time i.e. is the shortest time, and not equal to zero
                    if(JobsArray[j].getCpuTime() < minCpuTime && JobsArray[j].getCpuTime() != 0) {
                        activeProcess = j; //the index of active job
                        minCpuTime = JobsArray[activeProcess].getCpuTime();
                    }
                }
            }
            //Assign current process to currrent time on the time chart
            timeChart[i] = activeProcess+1;//which is activeprocess index plus one
            
            //decrement remaining time of selected process by 1 since it has been assigned 1 unit of cpu time
            JobsArray[activeProcess].decrementCT(1);

            //@TODO see how to combine these two repeating loops
            //calculate Waiting Time(WT) and Turn Around time(TT)
            for (int j = 0; j < JobsArray.length; j++) {
                //check if the process has arrived in the timeline 'i'
                if (JobsArray[j].getArrivalTime() <= i) {
                    if (JobsArray[j].getCpuTime() != 0) {
                        //if process has arrived but it's not completed TT is incremented by 1
                        JobsArray[j].incrementTT(1);
                        //if process has not been currently assigned the CPU but has arrived increment WT
                        if(j != activeProcess) JobsArray[j].incrementWT(1);
                    }
                    else if (j == activeProcess)//current process has been assigned CPU but cpu time 
                        JobsArray[j].incrementTT(1);
                }
            }
        }
        aveTAT = 0.0; aveWT = 0.0; //reset average TaT and WT variables
        //calculate aveTAT and aveWT
        for (Job job : JobsArray) {
            aveTAT += job.getTurnAroundTime();
            aveWT += job.getWaitingTime();
        }
        aveTAT /= JobsArray.length;
        aveWT /= JobsArray.length;

        //draw gantt chart


        att.setText(Double.toString(this.aveTAT));
        awt.setText(Double.toString(this.aveWT));

        
        

        //add data to table view
        data = FXCollections.observableArrayList(JobsArray);
        processTable.setItems(data);
    }//close processJob function

    public void drawChart(int[] timeChart) {
        //testing
        for (int i = 0; i < totalTime; i++) {
            System.out.println(" process at time " + i + " is " + timeChart[i]);
        }
    }

    /**
    * Job class
    * Collects details about the jobs
    */
    public static class Job {
        private SimpleIntegerProperty jobNo, cpuTime, burstTime, arrivalTime, finishTime, startTime, turnAroundTime = new SimpleIntegerProperty(0), waitingTime = new SimpleIntegerProperty(0);

        /** Job class constructor */
        public Job(int job, int burst, int atime) {
            this.jobNo = new SimpleIntegerProperty(job);
            this.cpuTime = new SimpleIntegerProperty(burst);
            this.burstTime = new SimpleIntegerProperty(burst); //store originally entered CPU Time
            this.arrivalTime = new SimpleIntegerProperty(atime);
        }

        /** Getter & Setter methods below */

        /*public int turnAroundTime() {
            return finishTime.get() - arrivalTime.get();
        }*/

        public int getTurnAroundTime() {
            return this.turnAroundTime.get();
        }

        public void incrementTT(int no) {
            this.turnAroundTime.set(this.turnAroundTime.get() + no);
        }

        public int getWaitingTime() {
            return this.waitingTime.get();
        }

        public void incrementWT(int no) {
            this.waitingTime.set(this.waitingTime.get() + no);
        }

        public int getArrivalTime() {
            return this.arrivalTime.get();
        }

        public int getCpuTime() {
            return this.cpuTime.get();
        }

        public void decrementCT(int no) {
            this.cpuTime.set(this.cpuTime.get() - no);
        }

        public int getBurstTime() {
            return this.burstTime.get();
        }

        public int getJobNo() {
            return this.jobNo.get();
        }

        public int getFinishTime() {
            return this.finishTime.get();
        }

        public void setFinishTime(int fTime) {
            this.finishTime.set(fTime);
        }

        public int getStartTime() {
            return this.startTime.get();
        }

        public void setStartTime(int sTime) {
            this.startTime.set(sTime);
        }

    }//close Job class
}//close public SRJF class
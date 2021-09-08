package duke;

import java.io.IOException;
import java.util.Scanner;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Duke class.
 * Uses storage class to load existing data.
 * If there is not existing file, it will create one.
 * It takes in user input and create the task object.
 * It will save the final list into the file when programmes end.
 */
public class Duke extends Application {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;
    private Image user = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image duke = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

//    public Duke(String filePath) {
//        this.storage = new Storage(filePath);
//        this.tasks = new TaskList(storage.load());
//        this.ui = new Ui(this.storage, this.tasks);
//    }
//
//    public void run() {
//        boolean isExit = false;
//        System.out.println(ui.greet());
//        while (!isExit) {
//            String input = ui.readLine();
//        }
//    }
//
//    public static void main(String[] args) {
//        new Duke("data/tasks.txt").run();
//    }

    @Override
    public void start(Stage stage) {
        //Step 1. Setting up required components

        //The container for the content of the chat to scroll.
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);

        userInput = new TextField();
        sendButton = new Button("Send");

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);

        scene = new Scene(mainLayout);

        stage.setScene(scene);
        stage.show();

        //Step 2. Formatting the window to look as expected
        stage.setTitle("Duke");
        stage.setResizable(false);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);

        mainLayout.setPrefSize(400.0, 600.0);

        scrollPane.setPrefSize(385, 535);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);

        // You will need to import `javafx.scene.layout.Region` for this.
        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);

        userInput.setPrefWidth(325.0);

        sendButton.setPrefWidth(55.0);

        AnchorPane.setTopAnchor(scrollPane, 1.0);

        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);

        AnchorPane.setLeftAnchor(userInput , 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);

        //Step 3. Add functionality to handle user input.
        sendButton.setOnMouseClicked((event) -> {
            handleUserInput();
        });

        userInput.setOnAction((event) -> {
            handleUserInput();
        });

        //Scroll down to the end every time dialogContainer's height changes.
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));
    }

    /**
     * Iteration 1:
     * Creates a label with the specified text and adds it to the dialog container.
     * @param text String containing text to add
     * @return a label with the specified text that has word wrap enabled.
     */
    private Label getDialogLabel(String text) {
        // You will need to import `javafx.scene.control.Label`.
        Label textToAdd = new Label(text);
        textToAdd.setWrapText(true);

        return textToAdd;
    }

    /**
     * Iteration 2:
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    private void handleUserInput() {
        Label userText = new Label(userInput.getText());
        Label dukeText = new Label(getResponse(userInput.getText()));
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, new ImageView(user)),
                DialogBox.getDukeDialog(dukeText, new ImageView(duke))
        );
        userInput.clear();
    }

    /**
     * You should have your own function to generate a response to user input.
     * Replace this stub with your completed method.
     */
    private String getResponse(String input) {
        String s;
        int taskNumber;

        Storage storage = new Storage("data/duke.txt");
        TaskList list = new TaskList(storage.load());
        Ui ui = new Ui(storage, list);
        try {
            if (input.startsWith("bye")) {
                return ui.goodbye();
            } else if (input.equals("list")) {
                return ui.listTasks();
            } else {
                if (input.startsWith("done")) {
                    taskNumber = Integer.parseInt(input.substring(input.indexOf(" ") + 1)) - 1;
                    assert taskNumber >= 0 : "Task number should be positive";
                    return ui.markDone(taskNumber);
                } else if (input.startsWith("todo")) {
                    Todo todo = new Todo(input.substring(input.indexOf(" ") + 1));
                    return ui.add(todo);
                } else if (input.startsWith("deadline")) {
                    Deadline deadline = new Deadline(input.substring(input.indexOf(" ") + 1, input.indexOf(" /by")),
                            input.substring(input.indexOf("/by") + 4));
                    return ui.add(deadline);
                } else if (input.startsWith("event")) {
                    Event event = new Event(input.substring(input.indexOf(" ") + 1, input.indexOf(" /at")),
                            input.substring(input.indexOf("/at") + 4));
                    return ui.add(event);
                } else if (input.startsWith("delete")) {
                    taskNumber = Integer.parseInt(input.substring(input.indexOf(" ") + 1)) - 1;
                    assert taskNumber >= 0 : "Task number should be positive";
                    return ui.delete(taskNumber);

                } else if (input.startsWith("find")) {
                    int thingToFind = input.indexOf(" ") + 1;
                    return ui.findTasks(input.substring(thingToFind));
                } else if (input.startsWith("help")) {
                    String helpMessage = "Welcome to the quick help page!\n" +
                            "Here are some examples of what you can type: \n" +
                            "\t1) help\n" +
                            "\t2) list\n" +
                            "\t3) todo do homework\n" +
                            "\t4) deadline assignment \\by 2021-09-24\n" +
                            "\t5) event career fair \\at 2021-09-08\n" +
                            "\t6) find homework\n" +
                            "\t7) done 3\n" +
                            "\t8) delete 1\n" +
                            "\t9) bye\n";
                    return helpMessage;
                }
            }
            return "I don't understand. Please try again!";
        } catch (StringIndexOutOfBoundsException e) {
            return "You keyed in the command wrongly";
        } catch (IndexOutOfBoundsException e) {
            return "Sorry but the number you keyed in is out of range";

        }
    }
}

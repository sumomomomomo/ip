package joeduck.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import joeduck.JoeDuck;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private JoeDuck joeDuck;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/Player.png"));
    private Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/GoblinLeader.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects JoeDuck */
    public void setJoeDuck(JoeDuck joeDuck) {
        this.joeDuck = joeDuck;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing JoeDuck's reply
     * and then appends them to the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = joeDuck.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDukeDialog(response, dukeImage)
        );
        userInput.clear();
    }
}

/*
 * MIT License
 *
 * Copyright (c) 2024 Mark J. Koch ( @maehem on GitHub )
 *
 * Portions of this software are Copyright (c) 2018 Henadzi Matuts and are
 * derived from their project: https://github.com/HenadziMatuts/Reuromancer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.maehem.podunk;

import com.maehem.podunk.debug.DebugTab;
import com.maehem.podunk.gui.MainPane;
import com.maehem.podunk.logging.Logging;
import com.maehem.podunk.logging.LoggingHandler;
import com.maehem.podunk.logging.LoggingMessageList;
import com.maehem.podunk.old.engine.GameState;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Podunk extends Application {

    public static final Logger LOGGER = Logger.getLogger("com.maehem.podunk");

    public static final String VERSION = "0.0.0";
    private final LoggingMessageList messageLog = new LoggingMessageList();
    private final LoggingHandler loggingHandler = new LoggingHandler(messageLog);

    private final GameState gameState;

    public static Stage primaryStage;
    private MainPane mainPane;

    private final Stage debugWindow = new Stage();

    public Podunk() {
        Logging.configureLogging();

        // TRUE for debug.
        LOGGER.setUseParentHandlers(false);  // Prevent INFO and HIGHER from going to stderr.
        LOGGER.info("Podunk Engine version:  " + VERSION);

        this.gameState = new GameState();
        gameState.setGameName("Podunk", "Podunk City Simulator");
        gameState.setGameVersion("0.0.0");

        LOGGER.log(Level.INFO, "JavaFX Version: {0}",
                System.getProperties().get("javafx.runtime.version")
        );
        LOGGER.log(Level.INFO, "Game: {0} {1}", new Object[]{gameState.getLongGameName(), gameState.getGameVersion()});

        LOGGER.fine("Fine log message.");
        LOGGER.finer("Finer log message.");
        LOGGER.finest("Finest log message.");
    }

    @Override
    public void start(Stage stage) throws Exception {
        initDebugWindow();
        primaryStage = stage;

        stage.setTitle("Podunk");

        //Pane rootPane = new Pane();
        mainPane = new MainPane();

        //rootPane.setPrefSize(200, 200);
        Scene scene = new Scene(mainPane);  // Create the Scene
        stage.setScene(scene); // Add the scene to the Stage

        stage.show(); // Display the Stage

        stage.setOnCloseRequest((t) -> {
            LOGGER.log(Level.CONFIG, "Window close request ...");

            mainPane.onWindowClosed(t);
            // Call t.consume() to cancel window closing.

//        if (storageModel.dataSetChanged()) {  // if the dataset has changed, alert the user with a popup
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.getButtonTypes().remove(ButtonType.OK);
//            alert.getButtonTypes().add(ButtonType.CANCEL);
//            alert.getButtonTypes().add(ButtonType.YES);
//            alert.setTitle("Quit application");
//            alert.setContentText(String.format("Close without saving?"));
//            alert.initOwner(primaryStage.getOwner());
//            Optional<ButtonType> res = alert.showAndWait();
//
//            if (res.isPresent()) {
//                if (res.get().equals(ButtonType.CANCEL)) {
//                    event.consume();
//                }
//            }
//        }
            Platform.exit();
        });
        primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
    }

    private void initDebugWindow() {
        DebugTab debugTab = new DebugTab(messageLog, gameState);
        debugTab.setFormatter(loggingHandler.getFormatter());
        Scene debugScene = new Scene(debugTab);

        debugWindow.setScene(debugScene);
        debugWindow.setTitle("Debug Window");
        debugWindow.setAlwaysOnTop(true);
        debugWindow.setOnHidden((t) -> {
            gameState.setShowDebug(false);
        });
        debugWindow.setOnShowing((t) -> {
            gameState.setShowDebug(true);
        });

        debugWindow.show();
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        debugWindow.setX(bounds.getWidth() - debugWindow.getWidth());
        debugWindow.setY(bounds.getHeight() - debugWindow.getHeight());
    }

    /**
     * If you need to fire the close event... Window window =
     * Main.getPrimaryStage() // Get the primary stage from your Application
     * class .getScene() .getWindow();
     *
     * window.fireEvent(new WindowEvent(window,
     * WindowEvent.WINDOW_CLOSE_REQUEST));
     *
     * @param event
     */
    private void closeWindowEvent(WindowEvent event) {
        LOGGER.log(Level.CONFIG, "Window close request ...");

        // Call event.consume() to cancel window closing.
//        if (storageModel.dataSetChanged()) {  // if the dataset has changed, alert the user with a popup
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.getButtonTypes().remove(ButtonType.OK);
//            alert.getButtonTypes().add(ButtonType.CANCEL);
//            alert.getButtonTypes().add(ButtonType.YES);
//            alert.setTitle("Quit application");
//            alert.setContentText(String.format("Close without saving?"));
//            alert.initOwner(primaryStage.getOwner());
//            Optional<ButtonType> res = alert.showAndWait();
//
//            if (res.isPresent()) {
//                if (res.get().equals(ButtonType.CANCEL)) {
//                    event.consume();
//                }
//            }
//        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

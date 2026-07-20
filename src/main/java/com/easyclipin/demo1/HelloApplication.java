package com.easyclipin.demo1;

import com.easyclipin.demo1.model.Link;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;

public class HelloApplication extends Application
{
    private boolean memoryFile = false;
    private String fileName = "";
    private String filePath = "";

    @Override
    public void start(Stage stage) throws IOException
    {
        //Where links are going to be stored
        ObservableList<Link> linkRecords = FXCollections.observableArrayList();
        stage.setAlwaysOnTop(true);

        AnchorPane anchorPane = new AnchorPane();

        //Anchor layout config
        anchorPane.setMaxWidth(300);
        anchorPane.setPrefWidth(300);
        anchorPane.setMaxHeight(300);
        anchorPane.setPrefHeight(300);

        //Vbox instantiation and spacing
        VBox vBoxStage = new VBox();
        vBoxStage.setSpacing(5);
        anchorPane.getChildren().add(vBoxStage);

        //Setting anchor spacing for the Vbox staging area
        AnchorPane.setTopAnchor(vBoxStage, 5.0);
        AnchorPane.setRightAnchor(vBoxStage, 5.0);
        AnchorPane.setLeftAnchor(vBoxStage, 5.0);
        AnchorPane.setBottomAnchor(vBoxStage, 5.0);

        //Instantiating button bar and setting orientation on child nodes
        ButtonBar buttonB = new ButtonBar();
        buttonB.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        buttonB.setPrefWidth(300);
        buttonB.setPrefHeight(10);

        //Instantiating buttons
        Button addB = new Button("Add");
        Button delB = new Button("Delete");
        Button importB = new Button("Import");
        Button saveB = new Button("Save");

        //Adding buttons to bar
        ButtonBar.setButtonData(addB, ButtonBar.ButtonData.OTHER);
        ButtonBar.setButtonData(delB, ButtonBar.ButtonData.OTHER);
        ButtonBar.setButtonData(importB, ButtonBar.ButtonData.OTHER);
        ButtonBar.setButtonData(saveB, ButtonBar.ButtonData.OTHER);

        //Button Bar layout config
        buttonB.setButtonMinWidth(50);
        buttonB.setButtonOrder("L_E+U+FBXI_YNOCAH_R");
        buttonB.getButtons().addAll(saveB, importB, delB, addB);

        //Add list of link records to view
        ListView<Link> linkView = new ListView<>(linkRecords);

        linkView.setPrefWidth(295);
        linkView.setItems(linkRecords);
        linkView.getSelectionModel();

        //PressHold to get the link on Clipboard
        addPressAndHoldHandler(linkView, Duration.seconds(0.5), event-> {System.out.println("Pressed and held");});

        //Adding window elements
        vBoxStage.getChildren().add(buttonB);
        vBoxStage.getChildren().add(linkView);

        //Button events
        addB.setOnAction(event ->
        {
            //Temporarily let window pop on top of the app to select an import list
            stage.setAlwaysOnTop(false);
            TextInputDialog dialog = new TextInputDialog();
            Link linkA = new Link();

            dialog.setTitle("Add new link");
            dialog.setHeaderText("Create New Link");
            dialog.setContentText("Enter link name:");
            dialog.setGraphic(null);

            Optional<String> nameD = dialog.showAndWait();

            dialog.setContentText("Enter link's URL:");
            Optional<String> urlD = dialog.showAndWait();

            nameD.ifPresent(name ->
            {
                if(!name.trim().isEmpty())
                {
                    linkA.setName(name);
                }
            });

            urlD.ifPresent(urlS ->
            {
                if(!urlS.trim().isEmpty())
                {
                    linkA.setLink(urlS);
                }
            });

            linkRecords.add(linkA);
            linkView.getSelectionModel().select(linkA);
            stage.setAlwaysOnTop(true);
            System.out.println("Record: "+linkA+" added.");
        });

        delB.setOnAction(event ->
        {
            linkRecords.remove(linkView.getSelectionModel().getSelectedItem());
        });

        importB.setOnAction(event ->
        {
            try
            {
                //Temporarily let window pop on top of the app to select an import list
                stage.setAlwaysOnTop(false);
                Path currentPath = Paths.get("");
                selectFileInFileExplorer(currentPath, linkRecords);
                stage.setAlwaysOnTop(true);
            }
            catch (IOException e)
            {
                System.out.println(e.fillInStackTrace());
            }
        });

        saveB.setOnAction(event ->
        {
            try
            {
                stage.setAlwaysOnTop(false);
                saveCurrentLinks(linkRecords);
                stage.setAlwaysOnTop(true);
            } catch (IOException e)
            {
                System.out.println("Problem with the save location or writing to file");
            }
        });

        //Scene configuration, make sure it stays over other applications. Minimizes click on and off between windows to get wathever links you need
        Scene scene = new Scene(anchorPane, 300, 300, Color.RED);
        stage.setTitle("EasyClipIn");
        stage.setScene(scene);

        //Image by payungkead from Flaticon.com
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/clipboard_bypayungkead_flaticon.com.png")));
        stage.show();


    }
    //Thank you @james_d from stack overflow for the idea
    private void addPressAndHoldHandler(ListView<Link> linkView, Duration holdTime, EventHandler<MouseEvent> handler) {
        class Wrapper<T> {
            T content;
        }
        Wrapper<MouseEvent> eventWrapper = new Wrapper<>();

        //Start a timer once event starts
        PauseTransition holdTimer = new PauseTransition(holdTime);
        holdTimer.setOnFinished(event -> handler.handle(eventWrapper.content));

        //Instantiate event on mouse press
        linkView.addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
        {
            eventWrapper.content = event;
            holdTimer.playFromStart();
        });

        //Instantiate event on mouse release
        linkView.addEventHandler(MouseEvent.MOUSE_RELEASED, event ->
        {
            try {
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection data = new StringSelection(linkView.getSelectionModel().getSelectedItem().getLink());
                cb.setContents(data, null);
                holdTimer.stop();
            } catch (NullPointerException e) {
                System.out.println("Empty ListView");
            }
        });

        //Instantiate event on mouse drag
        linkView.addEventHandler(MouseEvent.MOUSE_DRAGGED, event ->
        {
            try {
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection data = new StringSelection(linkView.getSelectionModel().getSelectedItem().getLink());
                cb.setContents(data, null);
                holdTimer.stop();
            } catch (NullPointerException e) {
                System.out.println("Empty ListView");
            }
        });
    }

    private void selectFileInFileExplorer(final Path filePath, ObservableList<Link>  linkRecords) throws IOException
    {
        try
        {
            JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            chooser.setDialogTitle("Select a list of links you would like to import");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Select a .txt file", "txt");
            chooser.setFileFilter(filter);

            int res = chooser.showOpenDialog(null);

            if(res == JFileChooser.APPROVE_OPTION)
            {
                File selectedFile = new File(chooser.getSelectedFile().getAbsolutePath());
                Scanner input = new Scanner(selectedFile);
                while (input.hasNext()) {
                    String nextToken = input.nextLine();
                    int delimInd = nextToken.indexOf(" ");
                    Link tempLink = new Link();
                    tempLink.setName(nextToken.substring(0 , delimInd));
                    tempLink.setLink(nextToken.substring(delimInd+1));

                    linkRecords.add(tempLink);
                }
                input.close();
                setMemoryFile(true);
                System.out.println(chooser.getSelectedFile().getAbsolutePath());
                setFilePath(chooser.getSelectedFile().getAbsolutePath());
            }
        }
        catch (Exception e)
        {
            System.out.println(e.fillInStackTrace());
        }
    }

    private void saveCurrentLinks(ObservableList<Link>  linkRecords) throws IOException
    {
        if(!isMemoryFile())
        {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add new file");
            dialog.setHeaderText("Creating new link file");
            dialog.setContentText("Enter filename.\nFile saves under the directory where the .jar is currently at.");
            dialog.setGraphic(null);
            Optional<String> fileD = dialog.showAndWait();

            fileD.ifPresent(fileName ->
            {
                if(fileName.contains(".txt"))
                {
                    fileName = fileName.substring(0, fileName.indexOf(".")-1);
                }

                try (FileWriter newWriter = new FileWriter(fileName+".txt");)
                {
                    BufferedWriter bw = new BufferedWriter(newWriter);

                    for(int i = 0 ; i < linkRecords.size(); i++)
                    {
                        bw.write(linkRecords.get(i).getName()+" "+linkRecords.get(i).getLink());
                        bw.newLine();
                    }

                    bw.close();
                }
                catch (IOException e)
                {
                    System.out.println("Something went wrong writing to file");
                }

            });
        }
        else
        {
            try (FileWriter newWriter = new FileWriter(getFilePath()))
            {
                BufferedWriter bw = new BufferedWriter(newWriter);

                for(int i = 0 ; i < linkRecords.size(); i++)
                {
                    bw.write(linkRecords.get(i).getName()+" "+linkRecords.get(i).getLink());
                    bw.newLine();
                }

                bw.close();
            }
            catch (IOException e)
            {
                System.out.println("Something went wrong writing to file");
            }
        }

    }

    public boolean isMemoryFile() {
        return memoryFile;
    }

    public void setMemoryFile(boolean memoryFile) {
        this.memoryFile = memoryFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}

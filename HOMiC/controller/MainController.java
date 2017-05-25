package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Main;

public class MainController {
	private Main main;
	private Stage primaryStage;

	private String textToSearch = "";
	private String textChangeOn = "";
	private File mainPath = null;

	@FXML
	private TextField tfInput, tfOutput, tfChangeOn, tfSearch;

	public void setMain(Main main, Stage primaryStage) {
		this.main = main;
		this.primaryStage = primaryStage;
	}

	@FXML
	public void setInputPath() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedDirectory = directoryChooser.showDialog(primaryStage);

		if (selectedDirectory == null) {
			tfInput.setText("");
		} else {
			tfInput.setText(selectedDirectory.getAbsolutePath());
		}
	}

	@FXML
	public void setOutputPath() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedDirectory = directoryChooser.showDialog(primaryStage);

		if (selectedDirectory == null) {
			tfOutput.setText("");
		} else {
			tfOutput.setText(selectedDirectory.getAbsolutePath());
		}
	}

	@FXML
	public void convert() throws InterruptedException {

		if (tfInput.getText().equals("")) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Podaj œcie¿kê wejœcia");
			alert.setHeaderText("Podaj œcie¿kê wejœcia");
			alert.showAndWait();
			return;
		}
		if (tfOutput.getText().equals("")) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Podaj œcie¿kê wyjœcia");
			alert.setHeaderText("Podaj œcie¿kê wyjœcia");
			alert.showAndWait();
			return;
		}
		if (tfSearch.getText().equals("")) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Wpisz frazê do wyszukania");
			alert.setHeaderText("Wpisz frazê do wyszukania");
			alert.showAndWait();
			return;
		}
		if (tfChangeOn.getText().equals("")) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Wpisz frazê, na któr¹ zamieniæ");
			alert.setHeaderText("Wpisz frazê, na któr¹ zamieniæ");
			alert.showAndWait();
			return;
		}
		if (!tfOutput.getText().matches("([a-zA-Z]:)?(\\\\[a-zA-Z¹êó³œæÊŒ£Ó¿¯0-9_.-]+)+\\\\?")) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Podaj poprawn¹ œcie¿kê wyjœcia");
			alert.setHeaderText("Podaj poprawn¹ œcie¿kê wyjœcia");
			alert.showAndWait();
			return;
		}

		textToSearch = tfSearch.getText();
		textChangeOn = tfChangeOn.getText();
		mainPath = new File(tfInput.getText());

		Stage dialog = new Stage();
		dialog.initStyle(StageStyle.UTILITY);
		Image image = new Image("/view/hamster patient.jpg");
		ImageView imageView = new ImageView(image);
		Scene scene = new Scene(new Group(imageView));
		dialog.setTitle("Trwa zamiana");
		dialog.setScene(scene);
		dialog.show();

		try {
			Files.walk(Paths.get(tfInput.getText())).filter(
					p -> (p.toString().toUpperCase().endsWith(".LDT") || p.toString().toUpperCase().endsWith(".IES")))
					.forEach(p -> {
						try {
							String content = readContentOfFile(p);
							content = content.replaceAll(textToSearch, textChangeOn);
							saveContentInNewDirectory(p, content);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
		} catch (IOException e) {
			dialog.close();
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Niepoprawna œcie¿ka pliku");
			alert.setHeaderText("Niepoprawna œcie¿ka pliku.");
			alert.showAndWait();
			return;
		}

		dialog.close();
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Zamiana zakoñczona");
		alert.setHeaderText("Zamiana zakoñczona.");
		alert.showAndWait();

	}

	private String readContentOfFile(Path p) throws IOException {
		BufferedReader br = null;
		String contentOfFile = null;
		try {
			br = new BufferedReader(new FileReader(p.toString()));

			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			contentOfFile = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			br.close();
		}
		return contentOfFile;
	}

	private void saveContentInNewDirectory(Path p, String content) {
		String child = p.toString().substring(tfInput.getText().length());
		child = child.replaceAll(textToSearch, textChangeOn);
		Path outputPath = Paths.get(tfOutput.getText() + child);
		try {
			Files.createDirectories(outputPath.getParent());

			try (PrintStream out = new PrintStream(new FileOutputStream(outputPath.toString()))) {
				out.print(content);
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

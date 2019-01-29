// BV Ue1 WS2018/19 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-11

package bv_ws1819;

import java.io.File;

import bv_ws1819.Predictor.PredictorType;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class FilterAppController {
	
	private static final String initialFileName = "test1.jpg";
	private static File fileOpenPath = new File(".");
	
	private static final Predictor predictor = new Predictor();

    @FXML
    private Label entropyOrgLabel;
    
    @FXML
    private Label entropyFehlerLabel;
    
    @FXML
    private Label entropyRecLabel;
    
    @FXML
    private Label mseLabel;
    
    @FXML
    private Label quantizationLabel;

    @FXML
    private Slider quantizationSlider;

    @FXML
    private ComboBox<PredictorType> predictorSelection;

    @FXML
    private ImageView originalImageView;

    @FXML
    private ImageView noisyImageView;

    @FXML
    private ImageView filteredImageView;

    @FXML
    private Label messageLabel;

    @FXML
    void openImage() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(fileOpenPath); 
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images (*.jpg, *.png, *.gif)", "*.jpeg", "*.jpg", "*.png", "*.gif"));
		File selectedFile = fileChooser.showOpenDialog(null);
		if(selectedFile != null) {
			fileOpenPath = selectedFile.getParentFile();
			RasterImage img = new RasterImage(selectedFile);
			img.convertToGray();
			img.setToView(originalImageView);
	    	processImages();
	    	messageLabel.getScene().getWindow().sizeToScene();;
		}
    }
	
    @FXML
    void borderProcessingChanged() {
    	processImages();
    }

    @FXML
    void predictorChanged() {
    	processImages();
    }
    
	@FXML
	public void initialize() {
		// set combo boxes items
		predictorSelection.getItems().addAll(PredictorType.values());
		predictorSelection.setValue(PredictorType.A);
		
		// initialize parameters
		predictorChanged();
		
		// load and process default image
		RasterImage img = new RasterImage(new File(initialFileName));
		img.convertToGray();
		img.setToView(originalImageView);
		processImages();
	}
	
	private void processImages() {
		if(originalImageView.getImage() == null)
			return; // no image: nothing to do
		
		long startTime = System.currentTimeMillis();
		
		RasterImage origImg = new RasterImage(originalImageView); 
		RasterImage errorImg = new RasterImage(origImg.width, origImg.height); 
		RasterImage recImg = new RasterImage(origImg.width, origImg.height); 
		
		predictor.prediction(origImg, errorImg, recImg, predictorSelection.getValue());
		float entroOrg = (float) predictor.getEntropy(origImg.argb.length, origImg);
		float entroError = (float) predictor.getEntropy(errorImg.argb.length, errorImg);
		float entroRec = (float) predictor.getEntropy(recImg.argb.length, recImg);
		float mse = predictor.mse(origImg, recImg);
		
		errorImg.setToView(noisyImageView);
		recImg.setToView(filteredImageView);
		entropyOrgLabel.setText(""+entroOrg);
		entropyFehlerLabel.setText(""+entroError);
		entropyRecLabel.setText(""+entroRec);
		mseLabel.setText(""+mse);
		
	   	messageLabel.setText("Processing time: " + (System.currentTimeMillis() - startTime) + " ms");
	}
	

	



}

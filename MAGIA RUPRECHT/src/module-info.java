module MAGIA_RUPRECHT{
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
    requires javafx.media;
	requires javafx.base;
	requires java.sql;
	requires java.desktop;
	
	
    requires com.fasterxml.jackson.databind;

    // --- Jackson用リフレクション ---
    opens novelengine.model to com.fasterxml.jackson.databind;
	
    opens application to javafx.graphics, javafx.fxml, javafx.media;

	exports application;

}

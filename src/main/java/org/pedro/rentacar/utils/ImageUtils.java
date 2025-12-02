package org.pedro.rentacar.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class ImageUtils {
    private static final String UPLOAD_DIR = "src/main/resources/images/vehicles/";
    

    public static String saveImage(File sourceFile) {
        try {
            // Create upload directory if it doesn't exist
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // Generate a unique filename
            String originalFilename = sourceFile.getName();
            String fileExtension = "";
            int lastDot = originalFilename.lastIndexOf('.');
            if (lastDot > 0) {
                fileExtension = originalFilename.substring(lastDot);
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Save the file
            Path targetPath = Paths.get(UPLOAD_DIR + uniqueFilename);
            Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Return the relative path
            return "images/vehicles/" + uniqueFilename;
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    

    public static boolean deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return false;
        }
        
        try {
            // Convert relative path to absolute path
            String absolutePath = "src/main/resources/" + imagePath;
            File file = new File(absolutePath);
            return file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

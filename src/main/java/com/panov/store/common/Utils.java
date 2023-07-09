package com.panov.store.common;

import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

import static com.panov.store.common.Constants.STATIC_IMAGES_FOLDER;

/**
 * Methods of this class are used to process lists.
 *
 * @author Maksym Panov
 * @version 1.0
 */
public final class Utils {
    private Utils() {}

    public static String saveImageToFilesystem(String newImage, String currentImageName) {
        if (currentImageName != null) {
            File currentImage = new File(STATIC_IMAGES_FOLDER + "/" + currentImageName);
            if (currentImage.exists())
                currentImage.delete();
        }

        if (newImage.isBlank()) {
            return null;
        }

        System.out.println(newImage);

        String imageType = switch (newImage.substring(0, 5).toUpperCase()) {
            case ("/9J/4") -> ".jpg";
            case ("PHN2Z") -> ".svg";
            case ("UKLGR") -> ".webp";
            default -> ".png";
        };

        System.out.println(imageType);

        String newImageName = UUID.randomUUID()
                .toString()
                .replaceAll("-", "") + imageType;

        try (
                OutputStream out =
                        new FileOutputStream(
                                STATIC_IMAGES_FOLDER + "/" + newImageName
                        )
        ) {
            byte[] imageBytes = Base64.decode(newImage);
            out.write(imageBytes);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return newImageName;
    }
}

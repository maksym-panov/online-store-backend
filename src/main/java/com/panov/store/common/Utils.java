package com.panov.store.common;

import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import static com.panov.store.common.Constants.STATIC_IMAGES_FOLDER;

/**
 * Methods of this class are used to process lists.
 *
 * @author Maksym Panov
 * @version 1.0
 */
public class Utils {
    private Utils() {}

    /**
     * Use to skip some items and limit the size of the list.
     *
     * @param quantity if specified, the method will return only the first
     *                 {@code quantity} list elements.
     * @param offset if specified, the method will skip first {@code offset}
     *               list elements.
     * @return a shortened list.
     */
    public static <T> List<T> makeCut(List<T> list, Integer quantity, Integer offset) {
        if (quantity == null || quantity < 0)
            return list;
        if (offset == null || offset < 0)
            offset = 0;
        if (offset > list.size())
            offset = list.size();
        if (offset + quantity > list.size())
            quantity = list.size() - offset;
        return list.subList(offset, offset + quantity);
    }

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

package com.panov.store.services;

import com.panov.store.common.Utils;
import com.panov.store.dao.DAO;
import com.panov.store.dao.ProductRepository;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.exceptions.ResourceNotFoundException;
import com.panov.store.model.Product;
import com.panov.store.model.User;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.List;
import static com.panov.store.common.Constants.STATIC_IMAGES_FOLDER;

/**
 * Service-layer class that processes {@link Product} entities.
 *
 * @author Maksym Panov
 * @version 1.0
 * @see DAO
 * @see ProductRepository
 */
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;

    /**
     * Uses {@link DAO} implementation to retrieve list of all existing {@link Product} entities. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception.
     *
     * @return a {@link List} of {@link Product} objects
     */
    public List<Product> getRangeOfProducts(Integer offset, Integer quantity, Integer typeId) {
        try {
            List<Product> productRange;
            if (typeId != null) {
                productRange = repository.getPackageByProductType(offset, quantity, typeId);
            } else {
                productRange = repository.getPackage(offset, quantity);
            }

            if (productRange == null)
                productRange =  Collections.emptyList();
            productRange.forEach(this::fetchImage);
            return productRange;
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find products");
        }
    }

    /**
     * Uses {@link DAO} implementation to retrieve a {@link Product} entity by specified identity. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception or there is
     * no such {@link Product} object.
     *
     * @param id an identity of the sought {@link Product}
     * @return a {@link Product} object with specified identity
     */
    public Product getById(Integer id) {
        Product product = repository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find this product"));
        fetchImage(product);
        return product;
    }

    /**
     * Searches for {@link Product} objects using {@link DAO} implementation by specified
     * name pattern. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception.
     *
     * @param namePattern a name pattern you want to search by
     * @param offset sets the first entity from which method will fetch
     *               products that match the value
     * @param quantity the maximal number of entities that will be fetched
     * @param strict if {@code true} returns exact matches and if {@code false}
     *               returns a list of {@link Product} objects whose names contain
     *               {@code namePattern} as their part (case-insensitive)
     * @return a list of {@link Product} objects that match specified pattern
     */
    public List<Product> getByNamePattern(String namePattern, Integer offset, Integer quantity, boolean strict) {
        try {
            var products = repository.getByColumn(namePattern, offset, quantity, strict);
            if (products == null)
                products = Collections.emptyList();
            products.forEach(this::fetchImage);
            return products;
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find products");
        }
    }

    /**
     * Uses {@link DAO} implementation to save new {@link Product} in the data storage. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority
     * can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotCreatedException} if {@link DAO} object throws an exception.
     *
     * @param product an object to save
     * @return an identity of saved {@link Product}
     */
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public Integer createProduct(Product product) {
        Map<String, String> matches = thisNaturalIdExists(product);
        if (matches.size() != 0)
            throw new ResourceNotCreatedException(matches);

        if (product.getImage() != null) {
            product.setImage(
                    Utils.saveImageToFilesystem(
                            product.getImage(),
                            null
                    )
            );
        }

        Integer id = repository.insert(product);

        if (id == null)
            throw new ResourceNotCreatedException("Could not create this product");

        return id;
    }

    /**
     * Uses {@link DAO} implementation to change information of {@link Product}. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority
     * can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotUpdatedException} if {@link DAO} object throws an exception.
     *
     * @param product an object that contains new data and identity of {@link Product}
     *                     that should be updated
     * @return an identity of updated {@link Product}
     */
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public Integer changeProduct(Product product) {
        Map<String, String> matches = thisNaturalIdExists(product);
        if (matches.size() != 0)
            throw new ResourceNotUpdatedException(matches);

        Integer id = null;

        try {
            Product inDB = repository.get(product.getProductId())
                    .orElseThrow(() -> new ResourceNotUpdatedException("There is no such product"));
            if (product.getImage() == null) {
                product.setImage(inDB.getImage());
            } else {
                product.setImage(
                        Utils.saveImageToFilesystem(
                                product.getImage(),
                                inDB.getImage()
                        )
                );
            }

            id = repository.update(product);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotUpdatedException("Could not update this product");

        return id;
    }

    /**
     * Checks if the name of provided object is already used in existing {@link Product} object.
     *
     * @param product an object to check
     * @return a {@link Map} that contains decision about uniqueness of the name of specified {@link Product}
     */
    private Map<String, String> thisNaturalIdExists(Product product) {
        Map<String, String> matches = new HashMap<>();

        try {
            var nameMatch = getByNamePattern(product.getName(), null, null, true);
            if (nameMatch.size() != 0 && !product.getProductId().equals(nameMatch.get(0).getProductId()))
                matches.put("name", "Product with this name already exists");
        } catch(Exception ignored) {}

        return matches;
    }

    private void fetchImage(Product product) {
        if (product.getImage() == null) {
            return;
        }

        try {
            File imageFile = new File(STATIC_IMAGES_FOLDER + "/" + product.getImage());
            byte[] imageArr = FileUtils.readFileToByteArray(imageFile);
            String imageEncoded = Base64.toBase64String(imageArr);
            product.setImage(imageEncoded);
        } catch (Exception ignored) {}
    }
}

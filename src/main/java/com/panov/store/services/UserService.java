package com.panov.store.services;

import com.panov.store.common.Utils;
import com.panov.store.dao.UserRepository;
import com.panov.store.dto.AuthEntity;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotFoundException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.jwt.JwtService;
import com.panov.store.model.User;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.panov.store.dao.DAO;
import static com.panov.store.common.Constants.STATIC_IMAGES_FOLDER;

import java.io.File;
import java.util.*;

/**
 * Service-layer class that processes {@link User} entities.
 *
 * @author Maksym Panov
 * @version 1.0
 * @see DAO
 * @see UserRepository
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final DAO<User> repository;
    private final JwtService jwtService;

    /**
     * Uses {@link DAO} implementation to retrieve list of all existing {@link User} entities. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority
     * can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception.
     *
     * @return a {@link List} of {@link User} objects
     */
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public List<User> getUserList(Integer offset, Integer quantity) {
        try {
            var list = repository.getPackage(offset, quantity);
            if (list == null)
                list = Collections.emptyList();
            list.forEach(this::fetchProfileImage);
            return list;
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find users");
        }
    }

    /**
     * Uses {@link DAO} implementation to retrieve a {@link User} entity by specified identity. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority or the
     * {@link User} that is the owner of provided identity, can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception or there is
     * no such {@link User} object.
     *
     * @param id an identity of the sought {@link User}
     * @return a {@link User} object with specified identity
     */
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER') or hasAuthority(#id.toString)")
    public User getById(Integer id) {
        User user = repository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find this user"));
        fetchProfileImage(user);
        return user;
    }

    /**
     * Searches for {@link User} objects using {@link DAO} implementation by specified
     * phone number or email. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception.
     *
     * @param naturalId a phone number or email of the sought user
     * @param offset sets the first entity from which method will fetch
     *               products that match the value
     * @param quantity the maximal number of entities that will be fetched
     * @return a list of {@link User} objects whose have specified {@code naturalId}
     */
    public List<User> getByNaturalId(String naturalId, Integer offset, Integer quantity) {
        try {
            boolean strict = true;
            var users = repository.getByColumn(naturalId, offset, quantity, strict);
            if (users == null || users.size() == 0)
                throw new ResourceNotFoundException("There is no such user");
            users.forEach(this::fetchProfileImage);
            return users;
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find user");
        }
    }

    /**
     * Uses {@link DAO} implementation to register and save new {@link User} in the data storage. <br><br>
     * Re-throws a {@link ResourceNotCreatedException} if {@link DAO} object throws an exception.
     *
     * @param user {@link User} that should be registered
     */
    public void registerUser(User user) {
        var matches = thisNaturalIdExists(user);
        if (matches.size() != 0)
            throw new ResourceNotCreatedException(matches);

        Integer id = null;

        try {
            id = repository.insert(user);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotCreatedException("Could not create this user");
    }

    /**
     * Uses {@link DAO} implementation to change information of {@link User}. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority or the
     * {@link User} that is the owner of provided identity, can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotUpdatedException} if {@link DAO} object throws an exception.
     *
     * @param user an object that contains new data and identity of {@link User}
     *             that should be updated
     * @return an identity of updated {@link User}
     */
    @PreAuthorize(
            "hasAuthority('ADMINISTRATOR') or " +
            "hasAuthority(#user.getUserId().toString())"
    )
    public Integer changeUser(User user) {
        var matches = thisNaturalIdExists(user);
        if (matches.size() != 0)
            throw new ResourceNotCreatedException(matches);

        Integer id = null;

        try {
            User inDB = repository.get(user.getUserId())
                    .orElseThrow(() -> new ResourceNotUpdatedException("Could not update this user."));
            if (user.getImage() == null) {
                user.setImage(inDB.getImage());
            } else {
                user.setImage(
                        Utils.saveImageToFilesystem(
                                user.getImage(),
                                inDB.getImage()
                        )
                );
            }
            id = repository.update(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotCreatedException("Could not change this user");

        return id;
    }

    @PreAuthorize(
            "hasAuthority('ADMINISTRATOR') or " +
            "hasAuthority(#user.getUserId().toString())"
    )
    public AuthEntity changeUserWithPhoneNumber(User user) {
        var inDB = repository.get(user.getUserId())
                .orElseThrow(() -> new ResourceNotUpdatedException("Could not update this user."));
        String current = inDB.getPersonalInfo().getPhoneNumber();
        String requested = user.getPersonalInfo().getPhoneNumber();

        Integer id = changeUser(user);

        if (current.equals(requested)) {
            return new AuthEntity("", id);
        }
        return new AuthEntity(jwtService.createToken(user), id);
    }

    /**
     * Checks if the phone number or the email of object is already used in existing {@link User} object.
     *
     * @param user an object to check
     * @return a {@link Map} that contains decision about uniqueness of the phone number
     * and the email of specified {@link User}
     */
    private Map<String, String> thisNaturalIdExists(User user) {
        Map<String, String> matches = new HashMap<>();

        try {
            var phoneNumberMatch = getByNaturalId(user.getPersonalInfo().getPhoneNumber(), null, null);
            phoneNumberMatch = phoneNumberMatch
                    .stream()
                    .filter(u -> u.getPersonalInfo().getPhoneNumber() != null)
                    .toList();
            if (phoneNumberMatch.size() != 0 && !user.getUserId().equals(phoneNumberMatch.get(0).getUserId()))
                matches.put("phoneNumber", "User with this phone number already exists");
        } catch(Exception ignored) {}

        try {
            var emailMatch = getByNaturalId(user.getPersonalInfo().getEmail(), null, null);
            emailMatch = emailMatch
                    .stream()
                    .filter(
                            u -> u.getPersonalInfo().getEmail() != null &&
                                !u.getPersonalInfo().getEmail().isBlank()
                    )
                    .toList();
            if (emailMatch.size() != 0 && !user.getUserId().equals(emailMatch.get(0).getUserId()))
                matches.put("email", "User with this email already exists");
        } catch(Exception ignored) {}

        return matches;
    }

    private void fetchProfileImage(User user) {
        if (user.getImage() == null) {
            return;
        }
        String imageName = user.getImage();

        try {
            File imageFile = new File(STATIC_IMAGES_FOLDER + "/" + imageName);
            byte[] imageBytes = FileUtils.readFileToByteArray(imageFile);
            String imageEncoded = Base64.toBase64String(imageBytes);
            user.setImage(imageEncoded);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

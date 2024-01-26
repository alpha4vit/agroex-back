package com.vention.agroex.repository.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vention.agroex.entity.User;
import com.vention.agroex.exception.JsonIOException;
import com.vention.agroex.exception.UserListInitializationException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

@Component
@Slf4j
public class JsonUserRepository {

    private ObjectMapper objectMapper;
    private Map<Long, User> users = new HashMap<>();
    private final File dbUsersFile = new File("src/main/resources/db/json/users.json");
    private Long currentId = 0L;

    @PostConstruct
    public void init(){
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        try {
            log.info("Reading users array from json");
            List<User> usersList = new ArrayList<>(Arrays.asList(objectMapper.readValue(dbUsersFile, User[].class)));
            usersList.forEach(user -> this.users.put(user.getId(), user));
            currentId = this.users.keySet().stream().max(Long::compareTo).orElseGet(() -> currentId);
            log.info("Users array successfully read from json");
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new UserListInitializationException("Error during user array initialization");
        }
    }

    public List<User> findAll(){
        return users.values().stream().toList();
    }

    public Optional<User> findById(Long id){
        return Optional.ofNullable(this.users.get(id));
    }

    public User save(User user){
        if (this.users.containsKey(user.getId())){
            User before = this.users.get(user.getId());
            user.setEmailVerified(before.getEmailVerified());
            user.setRegistrationDate(before.getRegistrationDate());
            this.users.put(user.getId(), user);
            log.info(String.format("User with id=%d successfully updated", user.getId()));
        }
        else {
            currentId++;
            user.setId(currentId);
            this.users.put(user.getId(), user);
            log.info(String.format("User with id=%d successfully created", user.getId()));
        }
        rewriteFile();
        return user;
    }

    public void delete(User user){
        this.users.remove(user.getId());
        log.info(String.format("User with id=%d successfully deleted", user.getId()));
        rewriteFile();
    }

    private void rewriteFile(){
        try (FileOutputStream outputStream = new FileOutputStream(dbUsersFile, false)){
            String content = objectMapper.writeValueAsString(this.users.values());
            outputStream.write(content.getBytes());
            log.info("Json file successfully updated");
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new JsonIOException("Error during rewriting JSON file");
        }
    }
}

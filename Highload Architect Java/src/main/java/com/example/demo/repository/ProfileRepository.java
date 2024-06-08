package com.example.demo.repository;

import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Gender;
import com.example.demo.model.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileRepository {

    private final DataSource dataSource;

    public ProfileRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Profile getProfileById(UUID id) {
        Profile profile = null;
        try (Connection connection = dataSource.getConnection()) {
            String selectProfileSql = "SELECT id, name, surname, date_of_birth, interests, gender, city FROM user_profile WHERE id = ?";
            try (PreparedStatement profileStatement = connection.prepareStatement(selectProfileSql)) {
                profileStatement.setObject(1, id);
                try (ResultSet resultSet = profileStatement.executeQuery()) {
                    if (resultSet.next()) {
                        UUID profileId = (UUID) resultSet.getObject("id");
                        String name = resultSet.getString("name");
                        String surname = resultSet.getString("surname");
                        LocalDate dateOfBirth = resultSet.getDate("date_of_birth").toLocalDate();
                        String interests = resultSet.getString("interests");
                        String genderStr = resultSet.getString("gender");
                        Gender gender = (genderStr != null) ? Gender.valueOf(genderStr) : null;
                        String city = resultSet.getString("city");

                        profile = new Profile(profileId, name, surname, dateOfBirth, gender, interests, city);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profile;
    }

    public List<Profile> searchProfiles(String name, String surname) {
        List<Profile> profiles = new ArrayList<>();
        StringBuilder searchProfilesSql = new StringBuilder("SELECT id, name, surname, date_of_birth, interests, gender, city FROM user_profile WHERE 1=1");

        List<Object> parameters = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            searchProfilesSql.append(" AND name LIKE ?");
            parameters.add("%" + name + "%");
        }
        if (surname != null && !surname.isEmpty()) {
            searchProfilesSql.append(" AND surname LIKE ?");
            parameters.add("%" + surname + "%");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement searchStatement = connection.prepareStatement(searchProfilesSql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                searchStatement.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet resultSet = searchStatement.executeQuery()) {
                while (resultSet.next()) {
                    UUID profileId = (UUID) resultSet.getObject("id");
                    String resultName = resultSet.getString("name");
                    String resultSurname = resultSet.getString("surname");
                    LocalDate dateOfBirth = resultSet.getDate("date_of_birth").toLocalDate();
                    String interests = resultSet.getString("interests");
                    String genderStr = resultSet.getString("gender");
                    Gender gender = (genderStr != null) ? Gender.valueOf(genderStr) : null;
                    String city = resultSet.getString("city");

                    Profile profile = new Profile(profileId, resultName, resultSurname, dateOfBirth, gender, interests, city);
                    profiles.add(profile);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profiles;
    }
}



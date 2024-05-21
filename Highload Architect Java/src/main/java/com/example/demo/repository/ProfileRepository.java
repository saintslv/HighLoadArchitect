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
import java.util.UUID;

@Repository
public class ProfileRepository{

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
                        Gender gender = Gender.valueOf(resultSet.getString("gender"));
                        String city = resultSet.getString("city");

                        profile = new Profile(profileId, name, surname, dateOfBirth, gender, interests, city);
                    }
                    else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
        }
        return profile;
    }
}

package com.sharkozp.testproject.system.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by oleksandr on 1/25/16.
 */
@DatabaseTable(tableName = "user_choice")
public class UserChoice {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true)
    private Person person;
    @DatabaseField
    private String userChoice;

    public UserChoice() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getUserChoice() {
        return userChoice;
    }

    public void setUserChoice(String userChoice) {
        this.userChoice = userChoice;
    }
}

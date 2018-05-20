package de.haw_landshut.studienprojekt;

public enum sharedPreferencesEnum {
     USER_INFO_FILE("user_info"),
     FIRST_NAME("first_name"),
     LAST_NAME("last_name"),
     GENDER("gender"),
     BIRTHDAY("birthday"),
     HEIGHT("height"),
     WEIGHT("weight"),
     EMAIL("email");

    private final String string;

    sharedPreferencesEnum(String name){this.string = name;}

    @Override
    public String toString() {
        return this.string;
    }
}

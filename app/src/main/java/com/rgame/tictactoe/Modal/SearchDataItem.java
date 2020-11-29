package com.rgame.tictactoe.Modal;

public class SearchDataItem {
    public String owner_name,room_name,room_type,room_password;

    public SearchDataItem() {}

    public SearchDataItem(String owner_name, String room_name,String room_type,String room_password) {
        this.owner_name = owner_name;
        this.room_name = room_name;
        this.room_type = room_type;
        this.room_password = room_password;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public String getRoom_password() {
        return room_password;
    }

    public void setRoom_password(String room_password) {
        this.room_password = room_password;
    }
}

package com.example.myapplication;

import java.util.List;

public class UsersList {
    private int page;
    private List<User> data;

    public int getPage() {
        return page;
    }

    public List<User> getData() {
        return data;
    }

    public class User {
        private int id;
        private String email;
        private String first_name;
        private String last_name;
        private String avatar;

        public String getEmail(){
            return email;
        }
        public String getFirst_name(){
            return first_name;
        }

        public  String getLast_name(){
            return last_name;
        }
        public  String getAvatar(){
            return avatar;
        }

    }
}

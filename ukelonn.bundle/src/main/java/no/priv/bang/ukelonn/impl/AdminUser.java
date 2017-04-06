package no.priv.bang.ukelonn.impl;

public class AdminUser {
    private String userName;
    private int userId = 0;
    private int administratorId = 0;
    private String firstname = "Ikke innlogget";
    private String surname = "";

    public AdminUser(String userName, int userId, int administratorId, String firstname, String surname) {
        super();
        this.userName = userName;
        this.userId = userId;
        this.administratorId = administratorId;
        this.firstname = firstname;
        this.surname = surname;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAdministratorId() {
        return administratorId;
    }

    public void setAdministratorId(int administratorId) {
        this.administratorId = administratorId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + administratorId;
        result = prime * result + ((firstname == null) ? 0 : firstname.hashCode());
        result = prime * result + ((surname == null) ? 0 : surname.hashCode());
        result = prime * result + userId;
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AdminUser other = (AdminUser) obj;
        if (administratorId != other.administratorId)
            return false;
        if (firstname == null) {
            if (other.firstname != null)
                return false;
        } else if (!firstname.equals(other.firstname))
            return false;
        if (surname == null) {
            if (other.surname != null)
                return false;
        } else if (!surname.equals(other.surname))
            return false;
        if (userId != other.userId)
            return false;
        if (userName == null) {
            if (other.userName != null)
                return false;
        } else if (!userName.equals(other.userName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AdminUser [userName=" + userName + ", userId=" + userId + ", administratorId=" + administratorId
            + ", firstname=" + firstname + ", surname=" + surname + "]";
    }

}

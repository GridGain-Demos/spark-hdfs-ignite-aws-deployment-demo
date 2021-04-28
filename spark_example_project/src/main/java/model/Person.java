package model;

import java.util.Objects;

public class Person {
    private long id;
    private String city;
    private String firstname;
    private long age;
    private long companyId;
    private String secondname;
    private String hiredDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public String getSecondname() {
        return secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public String getHiredDate() {
        return hiredDate;
    }

    public void setHiredDate(String hiredDate) {
        this.hiredDate = hiredDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id &&
                age == person.age &&
                companyId == person.companyId &&
                Objects.equals(city, person.city) &&
                Objects.equals(firstname, person.firstname) &&
                Objects.equals(secondname, person.secondname) &&
                Objects.equals(hiredDate, person.hiredDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, city, firstname, age, companyId, secondname, hiredDate);
    }
}

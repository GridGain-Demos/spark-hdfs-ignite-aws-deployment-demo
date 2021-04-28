package model;

import java.util.Objects;

public class Company {
    private long id;
    private String companyName;
    private String socialNumber;

    public Company(long id, String companyName, String socialNumber) {
        this.id = id;
        this.companyName = companyName;
        this.socialNumber = socialNumber;
    }

    public String getSocialNumber() {
        return socialNumber;
    }

    public void setSocialNumber(String socialNumber) {
        this.socialNumber = socialNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return id == company.id &&
                Objects.equals(companyName, company.companyName) &&
                Objects.equals(socialNumber, company.socialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, companyName, socialNumber);
    }
}

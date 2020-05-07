package model.entities;

import java.io.Serializable;

public class Case implements Entity<Integer>, Serializable {
    private Integer id;
    private String name;
    private Double totalSumDonated;

    public Case(Integer id, String name, Double totalSumDonated) {
        this.id = id;
        this.name = name;
        this.totalSumDonated = totalSumDonated;
    }

    @Override
    public String toString() {
        return "Case{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", totalSumDonated=" + totalSumDonated +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTotalSumDonated() {
        return totalSumDonated;
    }

    public void setTotalSumDonated(Double totalSumDonated) {
        this.totalSumDonated = totalSumDonated;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }
}

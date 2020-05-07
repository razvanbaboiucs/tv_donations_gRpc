package model.entities;

import javafx.util.Pair;

import java.io.Serializable;

public class Donation implements Entity<Pair<Integer, Integer>>, Serializable {
    private Pair<Integer, Integer> id;
    private Integer caseId;
    private Integer donorId;
    private Double sumDonated;

    public Donation(Integer caseId, Integer donorId, Double sumDonated) {
        this.id = new Pair<Integer, Integer>(donorId, caseId);
        this.caseId = caseId;
        this.donorId = donorId;
        this.sumDonated = sumDonated;
    }

    @Override
    public String toString() {
        return "Donation{" +
                "id=" + id +
                ", caseId=" + caseId +
                ", donorId=" + donorId +
                ", sumDonated=" + sumDonated +
                '}';
    }

    public Integer getCaseId() {
        return caseId;
    }

    public void setCaseId(Integer caseId) {
        this.caseId = caseId;
    }

    public Integer getDonorId() {
        return donorId;
    }

    public void setDonorId(Integer donorId) {
        this.donorId = donorId;
    }

    public Double getSumDonated() {
        return sumDonated;
    }

    public void setSumDonated(Double sumDonated) {
        this.sumDonated = sumDonated;
    }

    @Override
    public Pair<Integer, Integer> getId() {
        return id;
    }

    @Override
    public void setId(Pair<Integer, Integer> id) {
        this.id = id;
    }
}

package model.entities;

public interface Entity<ID> {
    ID getId();
    void setId(ID id);
}

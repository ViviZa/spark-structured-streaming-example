import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Trade implements Serializable {

    @JsonProperty("ID")
    private long ID;


    @JsonProperty("CONTRACT")
    private String CONTRACT;

    @JsonProperty("PRICE")
    private double PRICE;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getCONTRACT() {
        return CONTRACT;
    }

    public void setCONTRACT(String CONTRACT) {
        this.CONTRACT = CONTRACT;
    }

    public double getPRICE() {
        return PRICE;
    }

    public void setPRICE(double PRICE) {
        this.PRICE = PRICE;
    }

}

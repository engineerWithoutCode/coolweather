package db;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

public class Customer extends DataSupport{
    //name是唯一的，且默认值为unknown
    @Column(unique = true, defaultValue = "unknown")
    private String weatherId;

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}

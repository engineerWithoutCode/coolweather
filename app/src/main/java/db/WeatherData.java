package db;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

public class WeatherData extends DataSupport{
    //name是唯一的，且默认值为unknown
    @Column(unique = true, defaultValue = "unknown")
    private String weatherId;
    private String name;
    private String tmpNow;
    private String typeNow;
    private String aqi;
    private String pm25;
    private String comfort;
    private String carWash;
    private String sport;
    private String updateTime;

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTmpNow() {
        return tmpNow;
    }

    public void setTmpNow(String tmpNow) {
        this.tmpNow = tmpNow;
    }

    public String getTypeNow() {
        return typeNow;
    }

    public void setTypeNow(String typeNow) {
        this.typeNow = typeNow;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getComfort() {
        return comfort;
    }

    public void setComfort(String comfort) {
        this.comfort = comfort;
    }

    public String getCarWash() {
        return carWash;
    }

    public void setCarWash(String carWash) {
        this.carWash = carWash;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}

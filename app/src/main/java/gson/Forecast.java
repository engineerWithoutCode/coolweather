package gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    public String date;
    @SerializedName("tmp")
    public Tmp temperature;
    @SerializedName("cond")
    public Cond cond;

    public class Tmp{
        public String max;
        public String min;
    }

    public class Cond {
        @SerializedName("txt_d")
        public String txt_d;
    }
}

package main.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;



public class SettingsResponse {
    @JsonProperty("MULTIUSER_MODE")
    private boolean multiuserMode;
    @JsonProperty("POST_PREMODERATION")
    private boolean postPremoderation;
    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean statisticIsPublic;


    public boolean isMultiuserMode() {
        return multiuserMode;
    }

    public void setMultiuserMode(boolean multiuserMode) {
        this.multiuserMode = multiuserMode;
    }

    public boolean isPostPremoderation() {
        return postPremoderation;
    }

    public void setPostPremoderation(boolean postPremoderation) {
        this.postPremoderation = postPremoderation;
    }

    public boolean isStatisticIsPublic() {
        return statisticIsPublic;
    }

    public void setStatisticIsPublic(boolean statisticIsPublic) {
        this.statisticIsPublic = statisticIsPublic;
    }
}

package main.service;

import main.model.response.SettingsResponse;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    public SettingsResponse getGlobalSettings(){
        SettingsResponse settingsResponse = new SettingsResponse();
        settingsResponse.setMultiuserMode(true);
        settingsResponse.setStatisticIsPublic(true);
        return settingsResponse;
    }


}

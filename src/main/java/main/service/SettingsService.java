package main.service;

import main.model.GlobalSettings;
import main.model.GlobalSettingsRepository;
import main.model.response.SettingsResponse;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    private final GlobalSettingsRepository globalSettingsRepository;

    public SettingsService(GlobalSettingsRepository globalSettingsRepository) {
        this.globalSettingsRepository = globalSettingsRepository;
    }

    public SettingsResponse getGlobalSettings(){
        SettingsResponse settingsResponse = new SettingsResponse();
        Iterable<GlobalSettings> iterable = globalSettingsRepository.findAll();
        for (GlobalSettings globalSettings : iterable) {
            boolean value = globalSettings.getValue().equals("YES");
            if(globalSettings.getCode().equals("MULTIUSER_MODE"))
                settingsResponse.setMultiuserMode(value);
            else if(globalSettings.getCode().equals("POST_PREMODERATION"))
                settingsResponse.setPostPremoderation(value);
            else if(globalSettings.getCode().equals("STATISTICS_IS_PUBLIC"))
                settingsResponse.setStatisticIsPublic(value);
        }
        return settingsResponse;
    }


}

package main.service;

import main.model.GlobalSetting;
import main.model.repositories.GlobalSettingsRepository;
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
        Iterable<GlobalSetting> iterable = globalSettingsRepository.findAll();
        for (GlobalSetting globalSetting : iterable) {
            boolean value = globalSetting.getValue().equals("YES");
            if(globalSetting.getCode().equals("MULTIUSER_MODE"))
                settingsResponse.setMultiuserMode(value);
            else if(globalSetting.getCode().equals("POST_PREMODERATION"))
                settingsResponse.setPostPremoderation(value);
            else if(globalSetting.getCode().equals("STATISTICS_IS_PUBLIC"))
                settingsResponse.setStatisticIsPublic(value);
        }
        return settingsResponse;
    }


}

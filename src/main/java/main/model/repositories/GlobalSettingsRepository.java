package main.model.repositories;

import main.model.GlobalSetting;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalSettingsRepository extends PagingAndSortingRepository<GlobalSetting, Integer> {
    GlobalSetting findByCode(String code);
}

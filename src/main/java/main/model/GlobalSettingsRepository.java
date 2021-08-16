package main.model;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GlobalSettingsRepository extends PagingAndSortingRepository<GlobalSettings, Integer> {
}

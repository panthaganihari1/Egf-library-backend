package com.egf.library.repository;

import com.egf.library.model.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {

    List<LoginLog> findAllByOrderByLoginTimeDesc();

    @Query("SELECT COUNT(DISTINCT l.username) FROM LoginLog l WHERE l.status = 'success'")
    Long countUniqueMembers();

    @Query("SELECT l.deviceType, COUNT(l) FROM LoginLog l GROUP BY l.deviceType")
    List<Object[]> countByDeviceType();
}
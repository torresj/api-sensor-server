package com.torresj.apisensorserver.jpa;

import com.torresj.apisensorserver.models.UserHouseRelation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHouseRelationRepository extends JpaRepository<UserHouseRelation, Long> {

  List<UserHouseRelation> findByUserId(long userId);

  List<UserHouseRelation> findByHouseId(long houseId);
}

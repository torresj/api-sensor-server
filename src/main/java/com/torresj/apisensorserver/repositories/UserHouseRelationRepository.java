package com.torresj.apisensorserver.repositories;

import com.torresj.apisensorserver.models.UserHouseRelation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHouseRelationRepository extends JpaRepository<UserHouseRelation, Long> {

  List<UserHouseRelation> findByUserId(long userId);

  List<UserHouseRelation> findByHouseId(long houseId);

  Optional<UserHouseRelation> findByUserIdAndHouseId(long userId, long houseId);
}

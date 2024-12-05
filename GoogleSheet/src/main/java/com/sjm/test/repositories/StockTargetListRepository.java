package com.sjm.test.repositories;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
import com.sjm.test.yahdata.analy.model.InstantPerformanceResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StockTargetListRepository extends MongoRepository<InstantPerformanceResult, String> {
//	List<Player> findByPositionAndIsAvailable(PlayerPosition playerPosition, boolean isAvailable);
//
//	  List<Player> findDistinctNameByPositionIn(List<PlayerPosition> playerPositions);
//
//	  List<Player> findByBirthDateIsBetweenOrderByBirthDate(Date fromDate, Date toDate);
//
//	  Player findFirstByOrderByBirthDateDesc();
//
//	  List<Player> findFirst10ByOrderByBirthDate();
//
//	  Page<Player> findByIdIsNotNull(Pageable pageable);
}

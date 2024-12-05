package com.sjm.test.repositories;

import com.sjm.test.yahdata.analy.bean.raw.StockBean;
//import com.soccer.mongo.models.Player;
//import com.soccer.mongo.models.PlayerPosition;
import java.util.Date;
import java.util.List;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StockRepository extends MongoRepository<StockBean, String> {
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

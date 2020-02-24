package me.hockeystats.nhl.season;

import com.jmethods.catatumbo.EntityManager;
import com.jmethods.catatumbo.EntityQueryRequest;
import com.jmethods.catatumbo.QueryResponse;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class Seasons {
  private final EntityManager entityManager;

  public Seasons(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public Mono<Season> findById(long id) {
    return Mono.fromCallable(
        () -> {
          EntityQueryRequest request =
              entityManager.createEntityQueryRequest(
                  "SELECT * FROM Season WHERE seasonId = @seasonId");
          request.setNamedBinding("seasonId", id);
          QueryResponse<Season> response =
              entityManager.executeEntityQueryRequest(Season.class, request);
          List<Season> seasons = response.getResults();
          if (seasons.size() > 1) {
            throw new IllegalStateException("Multiple entries for the same season");
          } else if (seasons.size() == 0) {
            return null;
          }
          return seasons.get(0);
        });
  }

  public Flux<Season> saveAll(Flux<Season> seasons) {
    return Mono.fromCallable(
            () -> {
              List<Season> list =
                  seasons
                      .toStream()
                      .peek(
                          s -> {
                            if (s.getCreatedAt() == null) {
                              s.setCreatedAt(ZonedDateTime.now());
                            }
                          })
                      .collect(Collectors.toList());
              return entityManager.upsert(list);
            })
        .subscribeOn(Schedulers.elastic())
        .flatMapIterable(l -> l);
  }
}

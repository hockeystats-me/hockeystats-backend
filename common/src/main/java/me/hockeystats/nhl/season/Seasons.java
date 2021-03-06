package me.hockeystats.nhl.season;

import com.jmethods.catatumbo.EntityManager;
import com.jmethods.catatumbo.EntityQueryRequest;
import me.hockeystats.nhl.BaseRepository;

public class Seasons extends BaseRepository<Season, Long> {
  public Seasons(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  protected Class<Season> getEntityClass() {
    return Season.class;
  }

  @Override
  protected EntityQueryRequest findByIdQuery(Long id) {
    EntityQueryRequest request =
        entityManager.createEntityQueryRequest("SELECT * FROM Season WHERE nhlId = @nhlId");
    request.setNamedBinding("nhlId", id);
    return request;
  }
}

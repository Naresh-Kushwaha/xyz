package com.navratri.app.event.repository;

import com.navratri.app.activity.ActivityType;
import com.navratri.app.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByCancelledFalseAndStartTimeAfterOrderByStartTimeAsc(Instant after, Pageable pageable);

    // NOTE: `query` must never be null here -- see EventService.discover(), which passes ""
    // when the caller didn't type a search term. Postgres/Hibernate can't infer a JDBC type
    // for a parameter that's sometimes bound null and sometimes a String in the same query
    // (it falls back to bytea and lower(bytea) blows up -- see the fix history for the old
    // area-only search), so we sidestep that by always passing a non-null String and relying
    // on "" being a substring of everything. `activity` is a strongly-typed enum compared via
    // "member of" against a collection, which doesn't hit that same ambiguity, so it's fine
    // to actually pass null there when no activity filter is chosen.
    @Query("""
            select e from Event e
            where e.cancelled = false
              and e.startTime > :after
              and (:activity is null or :activity member of e.activities)
              and (
                    lower(e.name) like lower(concat('%', :query, '%'))
                 or lower(e.description) like lower(concat('%', :query, '%'))
                 or lower(e.approximateLocation) like lower(concat('%', :query, '%'))
              )
            order by e.startTime asc
            """)
    Page<Event> search(@Param("query") String query,
                        @Param("activity") ActivityType activity,
                        @Param("after") Instant after,
                        Pageable pageable);
}

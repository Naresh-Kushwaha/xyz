package com.navratri.app.group.repository;

import com.navratri.app.activity.ActivityType;
import com.navratri.app.group.entity.NavratriGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupRepository extends JpaRepository<NavratriGroup, Long> {

    @Query("""
            select g from NavratriGroup g
            where (:activity is null or :activity member of g.activities)
            order by g.createdAt desc
            """)
    Page<NavratriGroup> search(@Param("activity") ActivityType activity, Pageable pageable);
}

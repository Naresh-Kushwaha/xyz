package com.navratri.app.safety.repository;

import com.navratri.app.safety.entity.Report;
import com.navratri.app.safety.entity.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);
    Page<Report> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

package com.codereviewer.repository;

import com.codereviewer.entity.ReviewRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewRecord, String> {
    List<ReviewRecord> findTop10ByOrderByReviewedAtDesc();
}

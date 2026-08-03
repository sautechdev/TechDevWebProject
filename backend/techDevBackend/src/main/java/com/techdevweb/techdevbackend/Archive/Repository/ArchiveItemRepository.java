package com.techdevweb.techdevbackend.Archive.Repository;

import com.techdevweb.techdevbackend.Archive.Entity.ArchiveItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArchiveItemRepository extends JpaRepository<ArchiveItem, Long> {
    List<ArchiveItem> findByArchiveEventId(Long archiveEventId);
    void deleteByArchiveEventId(Long archiveEventId);
}

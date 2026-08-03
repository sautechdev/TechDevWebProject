package com.techdevweb.techdevbackend.Project.Service;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.Entity.Announcement;

import java.util.List;

public interface AnnouncementService {

    List<Announcement> getAnnouncements(Long projectId);

    // Sadece proje sahibi duyuru yayinlayabilir
    Announcement createAnnouncement(Long projectId, User currentUser, String title, String content);

    void deleteAnnouncement(Long announcementId, User currentUser);
}

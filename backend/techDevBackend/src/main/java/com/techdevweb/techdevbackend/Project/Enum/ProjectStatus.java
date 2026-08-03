package com.techdevweb.techdevbackend.Project.Enum;

public enum ProjectStatus {
    PENDING,    // yeni olusturuldu, admin onayi bekliyor - public listede GORUNMEZ
    ACTIVE,     // admin onayladi, herkese acik
    REJECTED,   // admin reddetti - public listede GORUNMEZ
    COMPLETED,
    ARCHIVED
}

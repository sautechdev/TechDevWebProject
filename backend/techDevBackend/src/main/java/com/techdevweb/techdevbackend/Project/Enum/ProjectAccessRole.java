package com.techdevweb.techdevbackend.Project.Enum;

// Bir kullanicinin bir projeyle iliskisini tanimlar.
// ProjectMemberRole'den farkli: bu, henuz uye olmayan (sadece basvuran
// ya da hic iliskisi olmayan) kullanicilari da kapsar.
public enum ProjectAccessRole {
    OWNER,
    MEMBER,
    APPLICANT,
    NONE
}

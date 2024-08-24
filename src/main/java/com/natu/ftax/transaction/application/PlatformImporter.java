package com.natu.ftax.transaction.application;

import org.springframework.web.multipart.MultipartFile;

public interface PlatformImporter {

    void importTransaction(MultipartFile file);
}

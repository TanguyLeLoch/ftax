package com.natu.ftax.transaction.importer;

import org.springframework.web.multipart.MultipartFile;

public interface PlatformImporter {

    void importTransaction(MultipartFile file);
}

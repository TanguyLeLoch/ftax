package com.natu.ftax.transaction.importer;

import com.natu.ftax.client.Client;
import org.springframework.web.multipart.MultipartFile;

public interface PlatformImporter {

    void importTransaction(MultipartFile file, Client client);
}

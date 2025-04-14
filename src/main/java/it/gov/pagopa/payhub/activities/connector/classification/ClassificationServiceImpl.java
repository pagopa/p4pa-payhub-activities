package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.ClassificationClient;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
@Slf4j
public class ClassificationServiceImpl implements ClassificationService {

    private final ClassificationClient classificationClient;
    private final AuthnService authnService;

    public ClassificationServiceImpl(ClassificationClient classificationClient, AuthnService authnService) {
        this.classificationClient = classificationClient;
        this.authnService = authnService;
    }

    @Override
    public Integer saveAll(List<Classification> classificationDTOList) {
        return classificationClient.saveAll(classificationDTOList, authnService.getAccessToken());
    }

    @Override
    public Classification save(Classification classificationDTO) {
        return classificationClient.save(classificationDTO, authnService.getAccessToken());
    }

    @Override
    public Long deleteByOrganizationIdAndIufAndLabel(Long organizationId, String iuf, ClassificationsEnum classification) {
        return classificationClient.deleteByOrganizationIdAndIufAndLabel(organizationId, iuf, classification, authnService.getAccessToken());
    }

    @Override
    public Long deleteBySemanticKey(TransferSemanticKeyDTO transferSemanticKeyDTO) {
        Long organizationId = transferSemanticKeyDTO.getOrgId();
        String iuv = transferSemanticKeyDTO.getIuv();
        String iur = transferSemanticKeyDTO.getIur();
        int transferIndex = transferSemanticKeyDTO.getTransferIndex();
        return classificationClient.deleteByOrganizationIdAndIuvAndIurAndTransferIndex(organizationId, iuv, iur, transferIndex, authnService.getAccessToken());
    }

    @Override
    public Long deleteByOrganizationIdAndIudAndLabel(Long organizationId, String iud, ClassificationsEnum classification) {
        return classificationClient.deleteByOrganizationIdAndIudAndLabel(organizationId, iud, classification, authnService.getAccessToken());
    }
}

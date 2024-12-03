package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentDTO;

public interface InstallmentDao {

    long upsert(InstallmentDTO installment);
}

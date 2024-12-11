package it.gov.pagopa.payhub.activities.dao;

public interface IuvSequenceNumberDao {

  /**
   *
   * @param ipaCode
   * @return
   */
  long getNextIuvSequenceNumber(String ipaCode);

}

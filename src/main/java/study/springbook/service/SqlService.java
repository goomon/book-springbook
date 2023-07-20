package study.springbook.service;

import study.springbook.exception.SqlRetrievalFailureException;

public interface SqlService {

    String getSql(String key) throws SqlRetrievalFailureException;
}

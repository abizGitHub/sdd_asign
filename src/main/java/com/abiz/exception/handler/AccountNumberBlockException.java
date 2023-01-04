package com.abiz.exception.handler;

import java.text.MessageFormat;

public class AccountNumberBlockException extends RuntimeException {

    public AccountNumberBlockException(TYPE type, String accountNumber) {
        super(type.getMsg(accountNumber));
    }

    public enum TYPE {
        HAS_ALREADY_BLOCKED("account number:[{0}] has already blocked!"),
        HAS_ALREADY_UN_BLOCKED("account number:[{0}] has already unblocked!");
        private String msgTemplate;

        TYPE(String msgTemplate) {
            this.msgTemplate = msgTemplate;
        }

        public String getMsg(String accountNumber) {
            return MessageFormat.format(msgTemplate, accountNumber);
        }
    }
}


package com.torresj.apisensorserver.exceptions;

import lombok.Getter;

@Getter
public class EntityAlreadyExistsException extends Exception {

  private static final long serialVersionUID = -7306413635068340585L;
  private Object object;

  public EntityAlreadyExistsException() {
  }

  public EntityAlreadyExistsException(Object object) {
    this.object = object;
  }

}
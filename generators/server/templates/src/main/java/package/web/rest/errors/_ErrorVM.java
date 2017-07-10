<%#
 Copyright 2013-2017 the original author or authors from the JHipster project.

 This file is part of the JHipster project, see https://jhipster.github.io/
 for more information.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-%>
package <%=packageName%>.web.rest.errors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * View Model for transferring error message with a list of field errors.
 */
public class ErrorVM implements Serializable {

    private final String message;
    private final String description;
    private int code = 0;
    private HttpStatus status = HttpStatus.BAD_REQUEST;


    private List<FieldErrorVM> fieldErrors;

    public ErrorVM(String message) {
        this(message, null);
    }

    public ErrorVM(String message, String description) {
        this.message = message;
        this.description = description;
    }

    public ErrorVM(String message, String description, HttpStatus status) {
        this.message = message;
        this.description = description;
        this.status = status;
    }

    public ErrorVM(String message, String description, List<FieldErrorVM> fieldErrors) {
        this.message = message;
        this.description = description;
        this.fieldErrors = fieldErrors;
    }

    public ErrorVM(String message, String description, int code, List<FieldErrorVM> fieldErrors) {
        this.message = message;
        this.description = description;
        this.fieldErrors = fieldErrors;
        this.code = code;
    }

    public ErrorVM(String message, String description, int code, HttpStatus status, List<FieldErrorVM> fieldErrors) {
        this.message = message;
        this.description = description;
        this.fieldErrors = fieldErrors;
        this.code = code;
        this.status = status;
    }

    public ErrorVM(String message, String description, int code) {
        this.message = message;
        this.description = description;
        this.code = code;
    }

    public ErrorVM(String message, String description, int code, HttpStatus status) {
        this.message = message;
        this.description = description;
        this.code = code;
        this.status = status;
    }

    public void add(String objectName, String field, String message) {
        if (fieldErrors == null) {
            fieldErrors = new ArrayList<>();
        }
        fieldErrors.add(new FieldErrorVM(objectName, field, message));
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

    public List<FieldErrorVM> getFieldErrors() {
        return fieldErrors;
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

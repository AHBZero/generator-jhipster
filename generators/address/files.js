/**
 * Copyright 2013-2017 the original author or authors from the JHipster project.
 *
 * This file is part of the JHipster project, see https://jhipster.github.io/
 * for more information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
const cleanup = require('../cleanup');
const constants = require('../generator-constants');

/* Constants use throughout */
const SERVER_MAIN_RES_DIR = constants.SERVER_MAIN_RES_DIR;

module.exports = {
    writeFiles
};

function writeFiles() {
    return {
        cleanupOldServerFiles() {
            cleanup.cleanupOldServerFiles(this, this.javaDir, this.testDir);
        },

        writeJavaUserManagementFiles() {
            if (this.skipUserManagement) return;
            // user management related files

            /* User management resources files */
            if (this.databaseType === 'sql') {
                this.template(`${SERVER_MAIN_RES_DIR}config/liquibase/cities.csv`, `${SERVER_MAIN_RES_DIR}config/liquibase/cities.csv`);
                this.template(`${SERVER_MAIN_RES_DIR}config/liquibase/countries.csv`, `${SERVER_MAIN_RES_DIR}config/liquibase/countries.csv`);
                this.template(`${SERVER_MAIN_RES_DIR}config/liquibase/states.csv`, `${SERVER_MAIN_RES_DIR}config/liquibase/states.csv`);
                this.template(`${SERVER_MAIN_RES_DIR}config/liquibase/changelog/00000000000000_address_schema.xml`, `${SERVER_MAIN_RES_DIR}config/liquibase/changelog/00000000000000_address_schema.xml`);
            }
        }
    };
}

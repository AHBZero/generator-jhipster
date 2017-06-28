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
package <%=packageName%>.service;

import <%=packageName%>.domain.Installation;
import <%=packageName%>.repository.InstallationRepository;
import <%=packageName%>.repository.UserRepository;
import <%=packageName%>.security.SecurityUtils;
import <%=packageName%>.service.push.InstallationPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class InstallationService {

    private final Logger log = LoggerFactory.getLogger(InstallationService.class);

    @Autowired
    private InstallationRepository installationRepository;

    @Autowired
    private InstallationPushService installationPushService;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Installation save(Installation installation) {
        log.debug("Request to save Installation : {}", installation);
        String currentUser = SecurityUtils.getCurrentUserLogin();

        if (currentUser == null) {
            throw new IllegalArgumentException("Not logged to register the token");
        }

        installation.setUser(userRepository.findOneByLogin(currentUser).orElseThrow(() -> new IllegalArgumentException("Installation for user must not be null")));

        List<Installation> byDeviceTokenAndDestination = installationRepository.findByDeviceToken(installation.getDeviceToken());
        if (!byDeviceTokenAndDestination.isEmpty()) {
            installationPushService.confirm(byDeviceTokenAndDestination.get(0));
            return byDeviceTokenAndDestination.get(0);
        } else {
            Installation result = installationRepository.save(installation);
            installationPushService.confirm(installation);
            return result;
        }
    }


    @Transactional(readOnly = true)
    public Page<Installation> findAll(Pageable pageable) {
        log.debug("Request to get all Installations");
        Page<Installation> result = installationRepository.findAll(pageable);
        return result;
    }

    @Transactional(readOnly = true)
    public Installation findOne(String id) {
        log.debug("Request to get Installation : {}", id);
        Installation installation = installationRepository.findOne(id);
        return installation;
    }

    public void delete(String id) {
        log.debug("Request to inactivate Installation : {}", id);
        installationRepository.delete(id);
    }

    @Transactional(readOnly = true)
    public Page<Installation> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Installations for queryCollectorAndOptionalEmployee {}", query);
        return installationRepository.findByAliasContaining(query, pageable);
    }

    public void removeInstallationsForDeviceTokens(Set<String> inactiveTokens) {
        log.info("Removing from database the following Tokens: {}", inactiveTokens);
        List<Installation> expired = installationRepository.findByDeviceTokenIn(inactiveTokens);
        if (expired != null && !expired.isEmpty()) {
            installationRepository.deleteInBatch(expired);
        }
    }

    public void updateInstallationsForCanonicalIDs(Map<String, String> changedTokens) {

        log.info("Update tokens for new canonical tokens: {}", changedTokens);
        for (Map.Entry<String, String> entry : changedTokens.entrySet()) {
            installationRepository.updateTokenForCanonicalID(entry.getKey(), entry.getValue());
        }
    }

    public void removeInstallationsForDeviceToken(String invalidToken) {
        List<Installation> expired = installationRepository.findByDeviceToken(invalidToken);
        if (expired != null && !expired.isEmpty()) {
            log.info("Removing from database the following Token: {}", invalidToken);
            installationRepository.delete(expired);
        }
    }

    public List<Installation> findByUserId(Long id) {
        return installationRepository.findByUserId(id);
    }

}

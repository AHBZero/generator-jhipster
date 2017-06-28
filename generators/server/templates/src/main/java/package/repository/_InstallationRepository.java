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
package <%=packageName%>.repository;

import <%=packageName%>.domain.Installation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public interface InstallationRepository extends JpaRepository<Installation, String> {

    List<Installation> findByDeviceTokenIn(Set<String> tokens);

    List<Installation> findByDeviceToken(String inactiveToken);

    @Modifying(clearAutomatically = true)
    @Query("update Installation set deviceToken = :newToken where deviceToken = :oldToken")
    void updateTokenForCanonicalID(@Param("oldToken") String oldToken, @Param("newToken") String newToken);

    List<Installation> findByUserId(Long id);

    List<Installation> findByUserLogin(String login);

    Page<Installation> findByAliasContaining(String query, Pageable pageable);

}

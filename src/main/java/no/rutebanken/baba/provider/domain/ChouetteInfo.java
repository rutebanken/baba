/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 */

package no.rutebanken.baba.provider.domain;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.util.Objects;
import java.util.Set;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
public class ChouetteInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public String xmlns;
    public String xmlnsurl;
    public String referential;
    public String organisation;
    @Column(name = "cuser")
    public String user;
    public String dataFormat;
    public boolean enableValidation;
    public boolean allowCreateMissingStopPlace;
    public boolean enableStopPlaceIdMapping;
    public boolean enableCleanImport;
    public boolean enableAutoImport;
    public boolean enableAutoValidation;
    public boolean enableBlocksExport;
    public boolean generateDatedServiceJourneyIds;
    public boolean googleUpload;
    @Column(name = "google_qa_upload")
    public boolean googleQAUpload;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CHOUETTE_INFO_SERVICE_LINK_MODES", joinColumns = @JoinColumn(name = "CHOUETTE_INFO_ID"))
    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSPORT_MODE")
    public Set<TransportMode> generateMissingServiceLinksForModes;

    public Long migrateDataToProvider = null;

    public ChouetteInfo() {
    }

    public ChouetteInfo(String xmlns, String xmlnsurl, String referential, String organisation, String user) {
        this.xmlns = xmlns;
        this.referential = referential;
        this.organisation = organisation;
        this.user = user;
    }

    public ChouetteInfo(Long id, String prefix, String xmlnsurl, String referential, String organisation, String user) {
        this(prefix, xmlnsurl, referential, organisation, user);
        this.id = id;
    }

    @Override
    public String toString() {
        return "ChouetteInfo{" +
                       "id=" + id +
                       ", xmlns='" + xmlns + '\'' +
                       ", xmlnsurl='" + xmlnsurl + '\'' +
                       ", referential='" + referential + '\'' +
                       ", organisationDTO='" + organisation + '\'' +
                       ", user='" + user + '\'' +
                       ", enableValidation='" + enableValidation + '\'' +
                       ", allowCreateMissingStopPlace='" + allowCreateMissingStopPlace + '\'' +
                       ", enableStopPlaceIdMapping='" + enableStopPlaceIdMapping + '\'' +
                       ", enableCleanImport='" + enableCleanImport + '\'' +
                       ", enableAutoImport='" + enableAutoImport + '\'' +
                       ", enableAutoValidation='" + enableAutoImport + '\'' +
                       ", enableBlocksExport='" + enableBlocksExport + '\'' +
                       ", generateMissingServiceLinksForModes='" + generateMissingServiceLinksForModes + '\'' +
                       ", generateDatedServiceJourneyIds='" + generateDatedServiceJourneyIds + '\'' +
                       ", googleUpload='" + googleUpload + '\'' +
                       ", googleQAUpload='" + googleQAUpload + '\'' +
                       '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChouetteInfo that = (ChouetteInfo) o;

        if (!Objects.equals(id, that.id)) {
            return false;
        }
        if (!Objects.equals(xmlns, that.xmlns)) {
            return false;
        }
        if (!Objects.equals(referential, that.referential)) {
            return false;
        }
        if (!Objects.equals(organisation, that.organisation)) {
            return false;
        }
        return Objects.equals(user, that.user);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (xmlns != null ? xmlns.hashCode() : 0);
        result = 31 * result + (referential != null ? referential.hashCode() : 0);
        result = 31 * result + (organisation != null ? organisation.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

}

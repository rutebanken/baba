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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
public class ChouetteInfo {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long id;
    public String xmlns;
    public String xmlnsurl;
    public String referential;
    public String organisation;
    @Column(name="cuser")
    public String user;
    public String regtoppVersion;
    public String regtoppCoordinateProjection;
    public String regtoppCalendarStrategy;
    public String dataFormat;
    public boolean enableValidation;
    public boolean allowCreateMissingStopPlace;
    public boolean enableStopPlaceIdMapping;
    public boolean enableCleanImport;
    public boolean enableAutoImport;

    public Long migrateDataToProvider = null;

    public ChouetteInfo(){}

    public ChouetteInfo(String xmlns,String xmlnsurl, String referential, String organisation, String user) {
        this.xmlns = xmlns;
        this.referential = referential;
        this.organisation = organisation;
        this.user = user;
    }

    public ChouetteInfo(Long id, String prefix, String xmlnsurl, String referential, String organisation, String user) {
        this(prefix, xmlnsurl,referential, organisation, user);
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
                (regtoppVersion != null ? ", regtoppVersion='" + regtoppVersion + '\'' : "") +
                (regtoppCoordinateProjection != null? ", regtoppCoordinateProjection='" + regtoppCoordinateProjection + '\'' : "")+
                (regtoppCalendarStrategy != null? ", regtoppCalendarStrategy='" + regtoppCalendarStrategy + '\'' : "")+
                ", enableValidation='" + enableValidation + '\'' +
                ", allowCreateMissingStopPlace='" + allowCreateMissingStopPlace + '\'' +
                ", enableStopPlaceIdMapping='" + enableStopPlaceIdMapping + '\'' +
                ", enableCleanImport='" + enableCleanImport + '\'' +
                ", enableAutoImport='" + enableAutoImport + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChouetteInfo that = (ChouetteInfo) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (xmlns != null ? !xmlns.equals(that.xmlns) : that.xmlns != null) return false;
        if (referential != null ? !referential.equals(that.referential) : that.referential != null) return false;
        if (organisation != null ? !organisation.equals(that.organisation) : that.organisation != null) return false;
        return user != null ? user.equals(that.user) : that.user == null;

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

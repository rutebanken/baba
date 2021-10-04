package no.rutebanken.baba.chouette;

import no.rutebanken.baba.provider.domain.Provider;

/**
 * Data transfer object for invoking the Chouette REST Referential services.
 */
public class ChouetteReferentialInfo {


    private String schemaName;
    private String dataspaceName;
    private String dataspaceFormat;
    private String dataspaceProjection;
    private String userName;
    private String organisationName;
    private String xmlns;
    private String xmlnsUrl;

    public ChouetteReferentialInfo() {

    }

    /**
     * Create a Chouette referential DTO from the corresponding provider in the Baba database.
     *
     * @param provider the provider in the Baba database corresponding to the referential in the Chouette database.
     */
    public ChouetteReferentialInfo(Provider provider) {

        this.schemaName = provider.getChouetteInfo().referential;
        this.dataspaceFormat = provider.getChouetteInfo().dataFormat;

        String regtoppCoordinateProjection = provider.getChouetteInfo().regtoppCoordinateProjection;
        if (regtoppCoordinateProjection != null) {
            this.dataspaceProjection = regtoppCoordinateProjection.replace("EPSG:", "");
        } else {
            this.dataspaceProjection = "";
        }
        this.dataspaceName = provider.getName();
        this.organisationName = provider.getChouetteInfo().organisation;
        this.userName = provider.getChouetteInfo().user;
        this.xmlns = provider.getChouetteInfo().xmlns;
        this.xmlnsUrl = provider.getChouetteInfo().xmlnsurl;
    }


    public String getSchemaName() {
        return schemaName;
    }

    public String getDataspaceName() {
        return dataspaceName;
    }

    public String getDataspaceFormat() {
        return dataspaceFormat;
    }

    public String getDataspaceProjection() {
        return dataspaceProjection;
    }

    public String getUserName() {
        return userName;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public String getXmlns() {
        return xmlns;
    }

    public String getXmlnsUrl() {
        return xmlnsUrl;
    }

}

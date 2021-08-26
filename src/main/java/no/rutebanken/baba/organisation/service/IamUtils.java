package no.rutebanken.baba.organisation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.rutebanken.baba.exceptions.BabaException;
import no.rutebanken.baba.organisation.model.responsibility.EntityClassificationAssignment;
import no.rutebanken.baba.organisation.model.responsibility.ResponsibilityRoleAssignment;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class IamUtils {

    private IamUtils() {
    }

    static String generatePassword() {
        List<CharacterRule> rules = Arrays.asList(
                // at least one upper-case character
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                // at least one lower-case character
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                // at least one digit character
                new CharacterRule(EnglishCharacterData.Digit, 1));
        return new PasswordGenerator().generatePassword(12, rules);
    }

    static RoleAssignment toRoleAssignment(ResponsibilityRoleAssignment roleAssignment) {
        RoleAssignment atr = new RoleAssignment();
        atr.r = roleAssignment.getTypeOfResponsibilityRole().getPrivateCode();
        atr.o = roleAssignment.getResponsibleOrganisation().getPrivateCode();

        if (roleAssignment.getResponsibleArea() != null) {
            atr.z = roleAssignment.getResponsibleArea().getRoleAssignmentId();
        }

        if (!CollectionUtils.isEmpty(roleAssignment.getResponsibleEntityClassifications())) {
            roleAssignment.getResponsibleEntityClassifications().forEach(ec -> addEntityClassification(atr, ec));
        }
        return atr;
    }


    private static void addEntityClassification(RoleAssignment atr, EntityClassificationAssignment entityClassificationAssignment) {
        if (atr.e == null) {
            atr.e = new HashMap<>();
        }


        String entityTypeRef = entityClassificationAssignment.getEntityClassification().getEntityType().getPrivateCode();
        List<String> entityClassificationsForEntityType = atr.e.computeIfAbsent(entityTypeRef, k -> new ArrayList<>());

        // Represented negated entity classifications with '!' prefix for now. consider more structured representation.
        String classifierCode = entityClassificationAssignment.getEntityClassification().getPrivateCode();
        if (!entityClassificationAssignment.isAllow()) {
            classifierCode = "!" + classifierCode;
        }

        entityClassificationsForEntityType.add(classifierCode);
    }

    static String toAtr(ResponsibilityRoleAssignment roleAssignment) {
        RoleAssignment atr = toRoleAssignment(roleAssignment);

        try {
            ObjectMapper mapper = new ObjectMapper();
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, atr);
            return writer.toString();
        } catch (IOException e) {
            throw new BabaException(e);
        }
    }
}
